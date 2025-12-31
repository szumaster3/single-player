package core.cache.tools;

import core.cache.Cache;
import cache.Constants;
import core.cache.FileStore;
import core.util.XTEAManager;

import java.util.Arrays;
import java.util.logging.*;

public class MapVerifier {

    private static final Logger logger = Logger.getLogger(MapVerifier.class.getName());

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord record) {
                return String.format(FORMAT, record.getMillis(), record.getLevel(), record.getMessage());
            }
        });
        handler.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        int incorrectCount = 0;

        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {

            for (int regionId = 0; regionId < 32_768; regionId++) {
                int[] keys = XTEAManager.lookup(regionId);
                String landscapeName = "l" + (regionId >> 8) + "_" + (regionId & 0xFF);
                int landFileId = cache.getFileId(5, landscapeName);

                if (landFileId == -1) continue;

                if (!isReadable(cache, 5, landFileId, keys)) {
                    int baseX = (regionId >> 8) << 6;
                    int baseY = (regionId & 0xFF) << 6;

                    logger.warning(String.format(
                            "Region ID: %d, Coords: (%d, %d), File: (5, %d), Keys: %s",
                            regionId, baseX, baseY, landFileId, Arrays.toString(keys)
                    ));
                    incorrectCount++;

                    // test reading without keys
                    if (isReadable(cache, 5, landFileId, null)) {
                        logger.info(String.format("Region ID: %d is no longer encrypted", regionId));
                    }
                }
            }

            logger.info("Total incorrect regions: " + incorrectCount);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to verify map regions", e);
        }
    }

    /**
     * Tries to read the file from cache with optional keys.
     *
     * @param cache   Cache instance
     * @param index   Cache index
     * @param fileId  File ID
     * @param keys    XTEA keys or null
     * @return true if readable, false otherwise
     */
    private static boolean isReadable(Cache cache, int index, int fileId, int[] keys) {
        try {
            if (keys == null) {
                cache.read(index, fileId).getData();
            } else {
                cache.read(index, fileId, keys).getData();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
