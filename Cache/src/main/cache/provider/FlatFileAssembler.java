package cache.provider;

import cache.Constants;
import core.cache.FileStore;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlatFileAssembler {

    private static final Logger LOGGER = Logger.getLogger(FlatFileAssembler.class.getName());

    private final Path cacheRoot;
    private final Path sourceRoot;

    public FlatFileAssembler(Path cacheRoot, Path sourceRoot) {
        this.cacheRoot = cacheRoot;
        this.sourceRoot = sourceRoot;
    }

    public static void main(String[] args) {
        Path sourceRoot = Path.of(Constants.FLAT_FILES);
        Path cacheRoot = Path.of(Constants.NEW_CACHE);

        int[] indexes = new int[Constants.INDICES];
        for (int i = 0; i < indexes.length; i++) indexes[i] = i;

        try {
            new FlatFileAssembler(cacheRoot, sourceRoot).packIndexes(indexes);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cache packing failed.", e);
        }
    }

    public void packIndexes(int[] indexesToPack) throws IOException {
        try (FileStore store = FileStore.create(cacheRoot.toString(), 29)) {
            LOGGER.info(() -> "Packing indexes: " + Arrays.toString(indexesToPack));

            for (int idx : indexesToPack) {
                packIndex(store, idx);
            }

            LOGGER.info("Packing completed successfully.");
        }
    }

    private void packIndex(FileStore store, int indexId) {
        try {
            Path indexFile = sourceRoot.resolve(Path.of("255", indexId + ".dat"));
            if (Files.exists(indexFile)) {
                store.write(255, indexId, ByteBuffer.wrap(Files.readAllBytes(indexFile)));
                LOGGER.fine(() -> "Wrote 255/" + indexId + ".dat");
            }

            Path folder = sourceRoot.resolve(String.valueOf(indexId));
            if (!Files.isDirectory(folder)) {
                LOGGER.fine(() -> "No data folder for index " + indexId);
                return;
            }

            Files.list(folder)
                    .filter(path -> path.getFileName().toString().endsWith(".dat"))
                    .forEach(path -> writeFile(store, indexId, path));

            LOGGER.info(() -> "Packed index " + indexId);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to pack index " + indexId, e);
        }
    }

    private void writeFile(FileStore store, int indexId, Path filePath) {
        String name = filePath.getFileName().toString().replace(".dat", "");
        try {
            int fileId = Integer.parseInt(name);
            byte[] data = Files.readAllBytes(filePath);
            store.write(indexId, fileId, ByteBuffer.wrap(data));
        } catch (NumberFormatException e) {
            LOGGER.warning(() -> "Skipping non-numeric file: " + name);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error writing file " + filePath, e);
        }
    }
}
