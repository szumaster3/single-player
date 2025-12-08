package ms.store

import java.nio.ByteBuffer

/**
 * Represents a file used in the server store.
 * @author Emperor
 */
class StoreFile
/**
 * Constructs a new `StoreFile` `Object`.
 */
{
    /**
     * Gets the dynamic.
     * @return The dynamic.
     */
    /**
     * Sets the dynamic.
     * @param dynamic The dynamic to set.
     */
    /**
     * If the data can change during server runtime.
     */
    var isDynamic: Boolean = false

    /**
     * The file data.
     */
    private var data: ByteArray = byteArrayOf()

    /**
     * Puts the data on the buffer.
     * @param buffer The buffer.
     */
    fun put(buffer: ByteBuffer) {
        val data = ByteArray(buffer.remaining())
        buffer[data]
        this.data = data
    }

    /**
     * Creates a byte buffer containing the file data.
     * @return The buffer.
     */
    fun data(): ByteBuffer {
        return ByteBuffer.wrap(data)
    }

    /**
     * Sets the data.
     * @param data The data.
     */
    fun setData(data: ByteArray) {
        this.data = data
    }
}