package core.cache.misc.buffer

import java.io.ByteArrayOutputStream

class OutputStream {
    private val out = ByteArrayOutputStream()
    var offset = 0
        private set

    fun writeByte(v: Int) {
        out.write(v and 0xFF)
        offset++
    }

    fun writeInt(v: Int) {
        out.write((v shr 24) and 0xFF)
        out.write((v shr 16) and 0xFF)
        out.write((v shr 8) and 0xFF)
        out.write(v and 0xFF)
        offset += 4
    }

    fun writeShort(v: Int) {
        out.write((v shr 8) and 0xFF)
        out.write(v and 0xFF)
        offset += 2
    }

    fun writeBytes(b: ByteArray?) {
        if (b == null) return
        out.write(b)
        offset += b.size
    }

    fun writeBigSmart(value: Int) {
        if (value < 32767) writeShort(value)
        else {
            val high = (value shr 16) + 32767
            writeShort(high)
            writeShort(value and 0xFFFF)
        }
    }

    fun getBytes(dest: ByteArray, off: Int, len: Int) {
        val src = out.toByteArray()
        System.arraycopy(src, 0, dest, off, kotlin.math.min(len, src.size))
    }

    fun setOffset(off: Int) {}

    fun toByteArray(): ByteArray {
        return out.toByteArray()
    }
}
