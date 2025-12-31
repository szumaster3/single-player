package core.cache.tools;

import cache.Constants;
import core.cache.Container;
import core.cache.FileStore;
import core.cache.ReferenceTable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CacheDefragmenter {

    private static final Logger logger = Logger.getLogger(CacheDefragmenter.class.getName());

    public static void main(String[] args) {
        try {
            defragmentCache(Constants.CACHE_PATH);
            logger.info("Cache defragmentation completed successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to defragment cache", e);
        }
    }

    public static void defragmentCache(String path) throws IOException {
        try (FileStore in = FileStore.open(path);
             FileStore out = FileStore.create(path, in.getTypeCount())) {

            for (int type = 0; type < in.getTypeCount(); type++) {
                ByteBuffer buf = in.read(255, type);
                if (buf == null || buf.remaining() == 0) {
                    logger.warning("Skipping empty or missing type 255:" + type);
                    continue;
                }

                buf.mark();
                out.write(255, type, buf);
                buf.reset();

                ReferenceTable refTable = ReferenceTable.decode(Container.decode(buf).getData());
                for (int fileId = 0; fileId < refTable.capacity(); fileId++) {
                    if (refTable.getEntry(fileId) == null) continue;

                    ByteBuffer fileData = in.read(type, fileId);
                    if (fileData == null || fileData.remaining() == 0) {
                        logger.warning("Skipping empty or missing file " + type + ":" + fileId);
                        continue;
                    }
                    out.write(type, fileId, fileData);
                }

                logger.info("Defragmented type " + type);
            }
        }
    }
}
