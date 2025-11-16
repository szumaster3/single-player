package core.cache

import core.cache.Cache.getReferenceFile
import core.cache.misc.ContainersInformation
import core.tools.StringUtils.getNameHash
import java.nio.ByteBuffer

/**
 * Represents a cache file manager.
 *
 * @author Dragonkk
 */
class CacheFileManager(
    val cacheFile: CacheFile,
    private val shouldDiscardFilesData: Boolean
) {

    var data: ContainersInformation
    private var filesData: Array<Array<ByteArray?>?> = arrayOf()

    init {
        val informContainerData = getReferenceFile()?.getContainerData(cacheFile.indexFileId)
            ?: throw IllegalStateException("Reference file data for index ${cacheFile.indexFileId} is null")
        data = ContainersInformation(informContainerData)
        resetFilesData()
    }

    val containersSize: Int
        get() = data.containers.size

    fun getFilesSize(containerId: Int): Int {
        if (!validContainer(containerId)) return -1
        return data.containers[containerId].files.size
    }

    fun resetFilesData() {
        filesData = arrayOfNulls(data.containers.size)
    }

    fun validContainer(containerId: Int): Boolean =
        containerId in 0 until data.containers.size

    fun validFile(containerId: Int, fileId: Int): Boolean =
        validContainer(containerId) &&
                fileId in 0 until data.containers[containerId].files.size

    fun getFileIds(containerId: Int): IntArray? =
        if (!validContainer(containerId)) null else data.containers[containerId].filesIndexes

    fun getArchiveId(name: String?): Int {
        if (name == null) return -1
        val hash = getNameHash(name)
        return data.containersIndexes.firstOrNull {
            data.containers[it].nameHash == hash
        } ?: -1
    }

    fun getFileData(containerId: Int, fileId: Int, xteaKeys: IntArray? = null): ByteArray? {
        if (!validFile(containerId, fileId)) return null

        if (filesData[containerId] == null || filesData[containerId]!![fileId] == null) {
            if (!loadFilesData(containerId, xteaKeys)) return null
        }

        val data = filesData[containerId]!![fileId]

        if (shouldDiscardFilesData) {
            if (filesData[containerId]!!.size == 1) {
                filesData[containerId] = null
            } else {
                filesData[containerId]!![fileId] = null
            }
        }

        return data
    }

    fun loadFilesData(containerId: Int, xteaKeys: IntArray?): Boolean {
        val data = cacheFile.getContainerUnpackedData(containerId, xteaKeys) ?: return false

        if (filesData[containerId] == null) {
            if (this.data.containers[containerId] == null) return false
            filesData[containerId] = arrayOfNulls(this.data.containers[containerId].files.size)
        }

        val containerFiles = this.data.containers[containerId].filesIndexes

        if (containerFiles.size == 1) {
            filesData[containerId]!![containerFiles[0]] = data
        } else {
            var readPosition = data.size - 1
            val loopCount = data[readPosition].toInt() and 0xff
            readPosition -= loopCount * containerFiles.size * 4

            val buffer = ByteBuffer.wrap(data)
            val filesSize = IntArray(containerFiles.size)
            buffer.position(readPosition)

            repeat(loopCount) {
                var offset = 0
                for (i in containerFiles.indices) {
                    offset += buffer.getInt()
                    filesSize[i] += offset
                }
            }

            val filesBuffer = Array(containerFiles.size) { ByteArray(filesSize[it]) }
            filesSize.fill(0)

            buffer.position(readPosition)
            var sourceOffset = 0
            repeat(loopCount) {
                var dataRead = 0
                for (i in containerFiles.indices) {
                    dataRead += buffer.getInt()
                    System.arraycopy(data, sourceOffset, filesBuffer[i], filesSize[i], dataRead)
                    sourceOffset += dataRead
                    filesSize[i] += dataRead
                }
            }

            containerFiles.forEachIndexed { idx, fileIndex ->
                filesData[containerId]!![fileIndex] = filesBuffer[idx]
            }
        }

        return true
    }

    fun setInformation(info: ContainersInformation) {
        data = info
        resetFilesData()
    }

}
