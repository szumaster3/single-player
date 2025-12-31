package core.cache.tools.unpack;

import core.cache.Cache;
import cache.Constants;
import core.cache.Container;
import core.cache.FileStore;
import core.cache.ReferenceTable;
import core.util.CompressionUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Model {

    private static final Logger logger = Logger.getLogger(Model.class.getName());

    public static void main(String[] args) {
        File outputDir = new File(Constants.MODEL_PATH);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            logger.severe("Failed to create output directory: " + outputDir.getAbsolutePath());
            return;
        }

        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            ReferenceTable table = cache.getReferenceTable(7);
            if (table == null) {
                logger.severe("Model reference table not found (index 7)");
                return;
            }

            int total = table.capacity();

            for (int fileId = 0; fileId < total; fileId++) {
                if (table.getEntry(fileId) == null) continue;

                try {
                    Container container = cache.read(7, fileId);
                    byte[] uncompressedData = new byte[container.getData().limit()];
                    container.getData().get(uncompressedData);

                    File outputFile = new File(outputDir, fileId + ".gz");
                    try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(outputFile.toPath()))) {
                        dos.write(CompressionUtils.gzip(uncompressedData));
                    }

                    if ((fileId + 1) % 50 == 0 || fileId == total - 1) {
                        double progress = (double) (fileId + 1) / total * 100;
                        logger.info(String.format("Processed %d/%d models (%.2f%%)", fileId + 1, total, progress));
                    }

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to dump model ID " + fileId, e);
                }
            }

            logger.info("Model dumping completed.");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open cache", e);
        }
    }
}
