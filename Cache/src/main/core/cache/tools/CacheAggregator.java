package core.cache.tools;

import cache.Constants;
import core.cache.Container;
import core.cache.FileStore;
import core.cache.ReferenceTable;
import core.cache.ReferenceTable.Entry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 * Aggregates files from another cache into the current cache if they match in CRC and version.
 */
public final class CacheAggregator {

    private static final Logger logger = Logger.getLogger(CacheAggregator.class.getName());

    public static void main(String[] args) {
        try {
            aggregate(Constants.CACHE_PATH, Constants.MERGED_CACHE_PATH);
            logger.info("Cache aggregation completed.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to aggregate caches", e);
        }
    }

    public static void aggregate(String sourcePath, String targetPath) throws IOException {
        try (FileStore sourceStore = FileStore.open(sourcePath);
             FileStore targetStore = FileStore.open(targetPath)) {

            int typeCount = targetStore.getFileCount(255);

            for (int type = 0; type < typeCount; type++) {
                if (type == 7) {
                    logger.warning("Skipping type 7 (unsupported newer ref table format)");
                    continue;
                }

                ReferenceTable sourceTable = ReferenceTable.decode(Container.decode(sourceStore.read(255, type)).getData());
                ReferenceTable targetTable = ReferenceTable.decode(Container.decode(targetStore.read(255, type)).getData());

                for (int fileId = 0; fileId < targetTable.capacity(); fileId++) {
                    Entry targetEntry = targetTable.getEntry(fileId);
                    if (targetEntry == null) continue;

                    if (needsRepacking(targetStore, targetEntry, type, fileId)) {
                        Entry sourceEntry = sourceTable.getEntry(fileId);

                        if (sourceEntry != null
                                && sourceEntry.getVersion() == targetEntry.getVersion()
                                && sourceEntry.getCrc() == targetEntry.getCrc()) {
                            ByteBuffer sourceData = sourceStore.read(type, fileId);
                            targetStore.write(type, fileId, sourceData);
                            int finalFileId = fileId;
                            int finalType = type;
                            logger.info(() -> "Repacked file " + finalType + ":" + finalFileId);
                        }
                    }
                }
            }
        }
    }

    private static boolean needsRepacking(FileStore store, Entry entry, int type, int fileId) {
        try {
            ByteBuffer buffer = store.read(type, fileId);
            if (buffer == null || buffer.remaining() <= 2) return true;

            byte[] data = new byte[buffer.remaining() - 2];
            buffer.position(0);
            buffer.get(data);

            CRC32 crc = new CRC32();
            crc.update(data);

            if ((int) crc.getValue() != entry.getCrc()) return true;

            buffer.position(buffer.limit() - 2);
            return (buffer.getShort() & 0xFFFF) != entry.getVersion();
        } catch (IOException e) {
            return true;
        }
    }
}
