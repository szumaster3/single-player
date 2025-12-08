package ms.store

import ms.system.util.ByteBufferUtils
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel.MapMode

/**
 * The server data storage.
 * @author Emperor
 */
object ServerStore {
    /**
     * The storage.
     */
    private var storage: MutableMap<String, StoreFile> = HashMap()

    /**
     * If the store has initialized.
     */
    private var initialized = false

    /**
     * Initializes the store.
     * @param path The file path.
     */
    fun init(path: String) {
        storage = HashMap()
        var file = File("$path/static_cache.arios")
        if (file.exists()) {
            try {
                RandomAccessFile(file, "rw").use { raf ->
                    val channel = raf.channel
                    val buffer: ByteBuffer = channel.map(MapMode.READ_WRITE, 0, channel.size())
                    val size = buffer.getShort().toInt() and 0xFFFF
                    for (i in 0 until size) {
                        val store = StoreFile()
                        val archive = ByteBufferUtils.getString(buffer)
                        check(!storage.containsKey(archive)) { "Duplicate archive found - archive=$archive!" }
                        val data = ByteArray(buffer.getInt())
                        buffer[data]
                        store.setData(data)
                        storage[archive] = store
                    }
                    check(!buffer.hasRemaining()) { "Unable to read full static store! " + buffer.remaining() }
                    channel.close()
                    raf.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        file = File("$path/dynamic_cache.arios")
        if (file.exists()) {
            try {
                RandomAccessFile(file, "rw").use { raf ->
                    val channel = raf.channel
                    val buffer: ByteBuffer = channel.map(MapMode.READ_WRITE, 0, channel.size())
                    val size = buffer.getShort().toInt() and 0xFFFF
                    for (i in 0 until size) {
                        val store = StoreFile()
                        store.isDynamic = true
                        val archive = ByteBufferUtils.getString(buffer)
                        val data = ByteArray(buffer.getInt())
                        buffer[data]
                        store.setData(data)
                        storage[archive] = store
                    }
                    check(!buffer.hasRemaining()) { "Unable to read all dynamic data (size=$size)!" }
                    channel.close()
                    raf.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        initialized = true
    }

    /**
     * Used for writing the static store.
     * @param path The path.
     */
    fun createStaticStore(path: String) {
        write("$path/static_cache.arios", false)
    }

    /**
     * Writes all the dynamic storage files (on server termination).
     * @param path The path.
     */
    fun dump(path: String) {
        write("$path/dynamic_cache.arios", true)
    }

    /**
     * Writes the store file to the given file path.
     * @param filePath The file path.
     * @param dynamic If the dynamic store is being written.
     */
    fun write(filePath: String?, dynamic: Boolean) {
        check(initialized) { "Server store has not been initialized!" }
        val f = File(filePath)
        if (f.exists()) {
            f.delete()
        }
        val buffer = ByteBuffer.allocate(1 shl 28)
        buffer.putShort(0.toShort())
        var size = 0
        for (archive in storage.keys) {
            val file = storage[archive]
            if (file!!.isDynamic != dynamic) {
                continue
            }
            size++
            val buf = file.data()
            ByteBufferUtils.putString(archive, buffer)
            buffer.putInt(buf.remaining())
            buffer.put(buf)
        }
        buffer.putShort(0, size.toShort())
        buffer.flip()
        try {
            RandomAccessFile(f, "rw").use { raf ->
                val channel = raf.channel
                channel.write(buffer)
                channel.close()
                raf.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Sets the archive data.
     * @param archive The archive id.
     * @param buffer The readable buffer.
     */
    fun setArchive(archive: String, buffer: ByteBuffer) {
        setArchive(archive, buffer, true)
    }

    /**
     * Sets the archive data.
     * @param archive The archive id.
     * @param buffer The readable buffer.
     * @param dynamic If the data changes during server runtime.
     */
    fun setArchive(archive: String, buffer: ByteBuffer, dynamic: Boolean) {
        val data = ByteArray(buffer.remaining())
        buffer[data]
        setArchive(archive, data, dynamic, true)
    }

    /**
     * Sets the archive data.
     * @param archive The archive index.
     * @param data The archive data.
     * @param dynamic If the data changes during server runtime.
     */
    fun setArchive(archive: String, data: ByteArray?, dynamic: Boolean) {
        setArchive(archive, data, dynamic, true)
    }


    /**
     * Sets the archive data.
     * @param archive The archive index.
     * @param data The archive data.
     * @param dynamic If the data changes during server runtime.
     * @param overwrite If the archive should be overwritten.
     */
    fun setArchive(archive: String, data: ByteArray?, dynamic: Boolean, overwrite: Boolean) {
        var file = storage[archive]
        if (file == null) {
            storage[archive] = StoreFile().also { file = it }
        } else check(overwrite) { "Already contained archive $archive!" }
        file!!.isDynamic = dynamic
        if (data != null) {
            file!!.setData(data)
        }
    }

    /**
     * Gets the archive data for the given archive id.
     * @param archive The archive index.
     * @return The archive data.
     */
    fun getArchive(archive: String): ByteBuffer {
        return get(archive)!!.data()
    }

    /**
     * Sets the archive file.
     * @param archive The archive.
     * @param file The file.
     */
    fun set(archive: String, file: StoreFile) {
        storage[archive] = file
    }

    /**
     * Gets the store file for the given archive.
     * @param archive The archive id.
     * @return The store file.
     */
    fun get(archive: String): StoreFile? {
        return storage[archive]
    }
}