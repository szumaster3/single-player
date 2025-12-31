package core.util.gzip;

import core.io.Stream;

import java.util.zip.Inflater;

public final class GZipDecompressor {

    private static final Inflater inflaterInstance = new Inflater(true);

    private GZipDecompressor() {
    }

    public static boolean decompress(Stream stream, byte[] data) {
        synchronized (inflaterInstance) {
            if (stream.payload[stream.offset] == 31 && stream.payload[stream.offset + 1] == -117) {
                try {
                    inflaterInstance.setInput(stream.payload, stream.offset + 10, stream.payload.length - stream.offset - 18);
                    inflaterInstance.inflate(data);
                } catch (Exception e) {
                    inflaterInstance.reset();
                    return false;
                }
                inflaterInstance.reset();
                return true;
            } else {
                return false;
            }
        }
    }

}
