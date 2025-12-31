package core.cache.tools;

import core.cache.Cache;
import cache.Constants;
import core.cache.FileStore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates the update version table and saves it as a Java-style hex array.
 */
public final class VersionTable {

    private static final Logger logger = Logger.getLogger(VersionTable.class.getName());
    private static final String OUTPUT_PATH = "dumps/version_table.txt";

    public static void main(String[] args) {
        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            ByteBuffer table = cache.createChecksumTable().encode();

            // Prepare buffer with header
            ByteBuffer buf = ByteBuffer.allocate(table.limit() + 8);
            buf.put((byte) 0xFF);       // Type
            buf.putShort((short) 0xFF); // File ID
            buf.put((byte) 0);          // Compression type
            buf.putInt(table.limit());  // Payload size
            buf.put(table);
            buf.flip();

            saveToTextFile(buf, OUTPUT_PATH);
            logger.info("Version table saved to " + OUTPUT_PATH);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to generate or save version table", e);
        }
    }

    /**
     * Saves the contents of a buffer as a formatted Java-style hex array.
     *
     * @param buf      the buffer to write
     * @param filePath output file path
     * @throws IOException if writing fails
     */
    private static void saveToTextFile(ByteBuffer buf, String filePath) throws IOException {
        File outputFile = new File(filePath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("public static final int[] VERSION_TABLE = new int[] {\n    ");
            for (int i = 0; i < buf.limit(); i++) {
                writer.write(String.format("0x%02X", buf.get(i)));
                writer.write(i < buf.limit() - 1 ? ", " : "");
                if ((i + 1) % 8 == 0 && i != buf.limit() - 1) {
                    writer.write("\n    ");
                }
            }
            writer.write("\n};\n");
        }
    }
}
