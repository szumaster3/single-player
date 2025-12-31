package core.cache.tools;

import cache.Constants;
import core.cache.Container;
import core.cache.FileStore;
import core.cache.ReferenceTable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 * Verifies cache file integrity using CRC and version data from reference tables.
 * Reports missing, outdated, or corrupt files.
 */
public final class CacheVerifier {

    private static final Logger LOGGER = Logger.getLogger(CacheVerifier.class.getName());

    public static void main(String[] args) {
        String cachePath = Constants.CACHE_PATH;
        try {
            new CacheVerifier().verifyCache(cachePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cache verification failed.", e);
        }
    }

    /**
     * Verifies the entire cache by comparing CRC and version values
     * of all files against their reference table entries.
     */
    public void verifyCache(String cachePath) throws IOException {
        int totalChecked = 0;
        int totalCorrupt = 0;
        int totalMissing = 0;
        int totalOutdated = 0;

        try (FileStore store = FileStore.open(cachePath)) {
            int tableCount = store.getFileCount(255);
            LOGGER.info(() -> "Starting cache verification. Found " + tableCount + " reference tables.");

            for (int type = 0; type < tableCount; type++) {
                ByteBuffer tableBuf = store.read(255, type);
                if (tableBuf == null) continue;

                ReferenceTable table = ReferenceTable.decode(Container.decode(tableBuf).getData());
                if (table == null) continue;

                for (int file = 0; file < table.capacity(); file++) {
                    ReferenceTable.Entry entry = table.getEntry(file);
                    if (entry == null) continue;

                    totalChecked++;
                    ByteBuffer data = null;

                    try {
                        data = store.read(type, file);
                    } catch (IOException ex) {
                        int finalType2 = type;
                        int finalFile2 = file;
                        LOGGER.warning(() -> String.format("Index %d, File %d: read error (%s)", finalType2, finalFile2, ex.getMessage()));
                        totalMissing++;
                        continue;
                    }

                    if (data == null || data.capacity() <= 2) {
                        int finalType3 = type;
                        int finalFile3 = file;
                        LOGGER.warning(() -> String.format("Index %d, File %d: missing or empty.", finalType3, finalFile3));
                        totalMissing++;
                        continue;
                    }

                    // Exclude version.
                    byte[] bytes = new byte[data.limit() - 2];
                    data.position(0);
                    data.get(bytes, 0, bytes.length);

                    CRC32 crc = new CRC32();
                    crc.update(bytes);
                    int computedCrc = (int) crc.getValue();

                    if (computedCrc != entry.getCrc()) {
                        int finalType = type;
                        int finalFile = file;
                        LOGGER.warning(() -> String.format("Index %d, File %d: CRC mismatch (expected %d, got %d)",
                                finalType, finalFile, entry.getCrc(), computedCrc));
                        totalCorrupt++;
                    }

                    data.position(data.limit() - 2);
                    int version = data.getShort() & 0xFFFF;
                    if (version != entry.getVersion()) {
                        int finalType1 = type;
                        int finalFile1 = file;
                        LOGGER.info(() -> String.format("Index %d, File %d: out of date (expected v%d, found v%d)",
                                finalType1, finalFile1, entry.getVersion(), version));
                        totalOutdated++;
                    }
                }
            }
        }

        int finalTotalChecked = totalChecked;
        int finalTotalCorrupt = totalCorrupt;
        int finalTotalMissing = totalMissing;
        int finalTotalOutdated = totalOutdated;

        LOGGER.info(() -> String.format(
                "Verification complete. Checked: %d, Corrupt: %d, Missing: %d, Outdated: %d",
                finalTotalChecked, finalTotalCorrupt, finalTotalMissing, finalTotalOutdated
        ));
    }
}
