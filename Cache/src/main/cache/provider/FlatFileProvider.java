package cache.provider;

import cache.Constants;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import core.rs3.util.Packet;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

public class FlatFileProvider {

    private static final Logger logger = Logger.getLogger(FlatFileProvider.class.getName());

    private static final int BLOCK_SIZE = 520;
    private static final int INDEX_ENTRY_SIZE = 6;
    private static final int ARCHIVE_COUNT = 256;

    public static void main(String[] args) throws Exception {
        Path src = Paths.get(Constants.CACHE_PATH);
        Path out = Paths.get(Constants.FLAT_FILES);

        if (args.length >= 1) src = Paths.get(args[0]);
        if (args.length >= 2) out = Paths.get(args[1]);

        if (!Files.exists(src)) {
            logger.severe("Missing source cache directory: " + src);
            System.exit(1);
        }

        Files.createDirectories(out);

        long start = System.currentTimeMillis();
        buildFlatIndex(src, out);
        long end = System.currentTimeMillis();
        logger.info(String.format("Process completed in %.2fs (output: %s)", (end - start) / 1000.0, out));
    }

    public static void buildFlatIndex(Path srcCacheDir, Path outDir) throws IOException {
        Path dat2 = srcCacheDir.resolve("main_file_cache.dat2");
        if (!Files.exists(dat2)) throw new FileNotFoundException("missing main_file_cache.dat2");

        int[] archiveCRC = new int[ARCHIVE_COUNT];
        int[] archiveRevision = new int[ARCHIVE_COUNT];

        Path idx255Path = srcCacheDir.resolve("main_file_cache.idx255");
        byte[] idx255Bytes = Files.exists(idx255Path) ? Files.readAllBytes(idx255Path) : null;

        try (RandomAccessFile dat = new RandomAccessFile(dat2.toFile(), "r")) {

            if (idx255Bytes != null) {
                int groups255 = idx255Bytes.length / INDEX_ENTRY_SIZE;
                ByteBuffer buf255 = ByteBuffer.wrap(idx255Bytes);
                Path idx255Dir = outDir.resolve("255");
                Files.createDirectories(idx255Dir);

                for (int i = 0; i < groups255; i++) {
                    int length = readMedium(buf255);
                    int sector = readMedium(buf255);
                    if (length <= 0 || sector <= 0) continue;

                    byte[] raw = readArchiveSector(dat, sector, length);
                    if (raw == null) continue;

                    Path outFile = idx255Dir.resolve(i + ".dat");
                    Files.write(outFile, raw);


                    if (i < ARCHIVE_COUNT) {
                        archiveCRC[i] = Packet.computeCRC(raw, 0, raw.length);
                        archiveRevision[i] = 1;
                    }
                }
            }

            for (int archive = 0; archive <= 28; archive++) {
                Path idxPath = srcCacheDir.resolve("main_file_cache.idx" + archive);
                if (!Files.exists(idxPath)) continue;

                byte[] idxBytes = Files.readAllBytes(idxPath);
                int groups = idxBytes.length / INDEX_ENTRY_SIZE;
                if (groups == 0) continue;

                ByteBuffer idxBuffer = ByteBuffer.wrap(idxBytes);
                Path archiveDir = outDir.resolve(String.valueOf(archive));
                Files.createDirectories(archiveDir);

                for (int group = 0; group < groups; group++) {
                    int length = readMedium(idxBuffer);
                    int sector = readMedium(idxBuffer);
                    if (length <= 0 || sector <= 0) continue;

                    byte[] raw = readArchiveSector(dat, sector, length);
                    if (raw == null) continue;

                    Path outFile = archiveDir.resolve(group + ".dat");
                    Files.write(outFile, raw);

                }
            }

            byte[] masterBytes = generateReferenceData(archiveCRC, archiveRevision);
            byte[] masterContainer = wrap(masterBytes, 2);

            Path masterOut = outDir.resolve("255").resolve("255.dat");
            Files.createDirectories(masterOut.getParent());
            Files.write(masterOut, masterContainer);
        }
    }

    private static byte[] generateReferenceData(int[] crc, int[] revision) {
        ByteBuffer buffer = ByteBuffer.allocate(crc.length * 8);
        for (int i = 0; i < crc.length; i++) {
            int c = crc[i];
            int r = revision[i];
            if (i == 24 && c == 0) c = 609698396;
            buffer.putInt(c);
            buffer.putInt(r);
        }
        return buffer.array();
    }

    private static byte[] readArchiveSector(RandomAccessFile dat, int sector, int len) throws IOException {
        byte[] result = new byte[len];
        int written = 0;

        while (written < len && sector != 0) {
            dat.seek((long) sector * BLOCK_SIZE);
            dat.readUnsignedShort();
            dat.readUnsignedShort();
            int nextSector = ((dat.readUnsignedByte() << 16) | (dat.readUnsignedByte() << 8) | dat.readUnsignedByte());
            dat.readUnsignedByte();
            int toRead = Math.min(len - written, BLOCK_SIZE - 8);
            dat.readFully(result, written, toRead);
            written += toRead;
            sector = nextSector;
        }

        return Arrays.copyOf(result, written);
    }

    private static int readMedium(ByteBuffer buf) {
        return ((buf.get() & 0xFF) << 16) | ((buf.get() & 0xFF) << 8) | (buf.get() & 0xFF);
    }

    private static byte[] wrap(byte[] data, int compression) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(compression);
        out.write(ByteBuffer.allocate(4).putInt(data.length).array());

        byte[] compressed;
        if (compression == 0) {
            compressed = data;
        } else if (compression == 1) {
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            try (BZip2CompressorOutputStream bzip = new BZip2CompressorOutputStream(tmp)) {
                bzip.write(data);
            }
            compressed = tmp.toByteArray();
        } else {
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            try (java.util.zip.GZIPOutputStream gzip = new java.util.zip.GZIPOutputStream(tmp)) {
                gzip.write(data);
                gzip.finish();
            }
            compressed = tmp.toByteArray();
        }

        if (compression != 0) out.write(ByteBuffer.allocate(4).putInt(compressed.length).array());

        out.write(compressed);
        out.write(0);
        out.write(0);
        return out.toByteArray();
    }
}
