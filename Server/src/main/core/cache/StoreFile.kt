package core.cache

import java.nio.ByteBuffer

class StoreFile {
    private var dynamic: Boolean = false
    private var data: ByteArray = ByteArray(0)
    fun put(buffer: ByteBuffer) { val arr = ByteArray(buffer.remaining()); buffer.get(arr); data = arr }
    fun data(): ByteBuffer = ByteBuffer.wrap(data)
    fun setData(d: ByteArray) { data = d }
    fun isDynamic(): Boolean = dynamic
    fun setDynamic(d: Boolean) { dynamic = d }
}