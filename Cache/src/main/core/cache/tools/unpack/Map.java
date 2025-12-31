package core.cache.tools.unpack;

import cache.Constants;
import core.cache.*;
import core.cache.FileStore;
import core.cache.ReferenceTable.Entry;
import core.cache.type.CacheIndex;
import core.util.CompressionUtils;
import core.util.XTEAManager;
import core.util.crypto.Djb2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class Map {

    private static final Logger logger = Logger.getLogger(Map.class.getName());

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord record) {
                return String.format(FORMAT, new Date(record.getMillis()), record.getLevel().getLocalizedName(),
                        record.getMessage());
            }
        });
        handler.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        Path mapDir = Paths.get(Constants.MAP_PATH);
        try {
            Files.createDirectories(mapDir);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to create map directory: " + mapDir, e);
            return;
        }

        if (!XTEAManager.load(Path.of(Constants.XTEAS_PATH))) {
            logger.severe("Failed to load XTEA keys");
            return;
        }

        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            ReferenceTable index = cache.getReferenceTable(5);
            if (index == null) {
                logger.severe("Index 5 not found");
                return;
            }

            java.util.Map<Integer, Integer> mapFiles = new HashMap<>();
            java.util.Map<Integer, Integer> landFiles = new HashMap<>();

            for (int fileId = 0; fileId < index.capacity(); fileId++) {
                Entry entry = index.getEntry(fileId);
                if (entry == null || entry.getIdentifier() == -1) continue;
                int ident = entry.getIdentifier();

                for (int x = 0; x <= 255; x++) {
                    for (int y = 0; y <= 255; y++) {
                        if (ident == Djb2.hash("m" + x + "_" + y)) mapFiles.put((x << 8) | y, fileId);
                        if (ident == Djb2.hash("l" + x + "_" + y)) landFiles.put((x << 8) | y, fileId);
                    }
                }
            }

            Set<Integer> regionIds = new HashSet<>(mapFiles.keySet());
            regionIds.retainAll(landFiles.keySet());

            logger.info("Regions with both map and landscape: " + regionIds.size());
            saveMapIndex(Paths.get(Constants.DUMP_PATH), regionIds, landFiles, mapFiles, Format.BINARY);

            int mapCount = 0, landCount = 0;

            for (int regionId : regionIds) {
                int x = (regionId >> 8) & 0xFF;
                int y = regionId & 0xFF;
                int[] keys = XTEAManager.lookup(regionId);

                // Dump map
                mapCount += dumpRegion(cache, mapDir, CacheIndex.MAPS.getID(), mapFiles.get(regionId),
                        "m" + x + "_" + y + ".gz", null);

                // Dump landscape
                landCount += dumpRegion(cache, mapDir, CacheIndex.MAPS.getID(), landFiles.get(regionId),
                        "l" + x + "_" + y + ".gz", keys);
            }

            logger.info(String.format("Dumped %d map files and %d landscape files.", mapCount, landCount));

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error accessing cache", e);
        }
    }

    private static int dumpRegion(Cache cache, Path outputDir, int index, int fileId, String fileName, int[] keys) {
        try {
            Container container = keys == null ? cache.read(index, fileId) : cache.read(index, fileId, keys);
            ByteBuffer buffer = container.getData();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            Path outFile = outputDir.resolve(fileName);
            Files.write(outFile, CompressionUtils.gzip(data));
            return 1;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to dump region file: " + fileName, e);
            return 0;
        }
    }

    private static void saveMapIndex(Path dumpDir, Set<Integer> regionIds, java.util.Map<Integer, Integer> landFiles,
                                     java.util.Map<Integer, Integer> mapFiles, Format format) {
        switch (format) {
            case BINARY -> {
                Path file = dumpDir.resolve("map_index");
                try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw")) {
                    raf.setLength(0);
                    raf.writeShort(regionIds.size());
                    for (int regionId : regionIds) {
                        raf.writeShort(regionId);
                        raf.writeShort(landFiles.get(regionId));
                        raf.writeShort(mapFiles.get(regionId));
                    }
                    logger.info("Binary map_index generated with " + regionIds.size() + " entries.");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to save binary map_index", e);
                }
            }
            case CSV -> {
                Path file = dumpDir.resolve("map_index.csv");
                try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                    writer.write("regionId,x,y,landscapeFileId,mapFileId\n");
                    for (int regionId : regionIds) {
                        int x = (regionId >> 8) & 0xFF;
                        int y = regionId & 0xFF;
                        writer.write(String.format("%d,%d,%d,%d,%d%n",
                                regionId, x, y, landFiles.get(regionId), mapFiles.get(regionId)));
                    }
                    logger.info("CSV map_index.csv generated with " + regionIds.size() + " entries.");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to save CSV map_index.csv", e);
                }
            }
            default -> logger.warning("Unsupported format: " + format);
        }
    }

    public enum Format {
        BINARY,
        CSV
    }
}
