package core.cache.misc.buffer

import java.io.ByteArrayInputStream

class InputStream(val inBuf: ByteArray) {
    private val inStream = ByteArrayInputStream(inBuf)

    fun readUnsignedByte(): Int {
        val v = inStream.read()
        return if (v < 0) 0 else v
    }

    fun readInt(): Int {
        val a = inStream.read()
        val b = inStream.read()
        val c = inStream.read()
        val d = inStream.read()
        if (d < 0) return 0
        return (a and 0xFF shl 24) or (b and 0xFF shl 16) or (c and 0xFF shl 8) or (d and 0xFF)
    }

    fun readUnsignedShort(): Int {
        val a = inStream.read()
        val b = inStream.read()
        if (b < 0) return 0
        return (a and 0xFF shl 8) or (b and 0xFF)
    }

    fun getBytes(dest: ByteArray, off: Int, len: Int) {
        val read = inStream.read(dest, off, len)
        if (read < len) {
            for (i in (off + kotlin.math.max(0, read)) until off + len) dest[i] = 0
        }
    }

    fun readBigSmart(): Int {
        val value = readUnsignedShort()
        return if (value < 32767) value else ((value - 32767) shl 16) + readUnsignedShort()
    }
}
