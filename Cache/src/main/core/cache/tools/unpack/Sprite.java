package core.cache.tools.unpack;

import core.cache.Cache;
import cache.Constants;
import core.cache.Container;
import core.cache.FileStore;
import core.cache.ReferenceTable;
import core.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Sprite {

    private static final Logger logger = Logger.getLogger(Sprite.class.getName());

    public static void main(String[] args) {
        File outputDir = new File(Constants.SPRITE_PATH);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            logger.severe("Failed to create output directory: " + outputDir.getAbsolutePath());
            return;
        }

        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            ReferenceTable spriteTable = cache.getReferenceTable(8);
            if (spriteTable == null) {
                logger.severe("Sprite reference table not found (index 8)");
                return;
            }

            for (int spriteId = 0; spriteId < spriteTable.capacity(); spriteId++) {
                if (spriteTable.getEntry(spriteId) == null) continue;

                try {
                    Container container = cache.read(8, spriteId);
                    core.cache.sprite.Sprite sprite = core.cache.sprite.Sprite.decode(container.getData());

                    // For each sprite, dump only the first frame
                    if (sprite.size() > 0) {
                        BufferedImage image = ImageUtils.createColoredBackground(
                                ImageUtils.makeColorTransparent(sprite.getFrame(0), Color.WHITE),
                                new Color(0xFF00FF, false)
                        );

                        File outFile = new File(outputDir, spriteId + ".png");
                        ImageIO.write(image, "png", outFile);
                    }

                    if ((spriteId + 1) % 50 == 0 || spriteId == spriteTable.capacity() - 1) {
                        double progress = (double) (spriteId + 1) / spriteTable.capacity() * 100;
                        logger.info(String.format("Processed %d/%d sprites (%.2f%%)", spriteId + 1, spriteTable.capacity(), progress));
                    }

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to dump sprite ID " + spriteId, e);
                }
            }

            logger.info("Sprite dumping completed.");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open cache", e);
        }
    }
}
