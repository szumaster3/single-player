package core.cache

import core.cache.crypto.XTEACryption
import core.cache.misc.ContainersInformation
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer

class CacheFile(
    val indexFileId: Int,
    private val indexFile: RandomAccessFile,
    private val dataFile: RandomAccessFile,
    private val maxContainerSize: Int,
    private val cacheFileBuffer: ByteArray = ByteArray(520)
) {

    fun getContainerUnpackedData(containerId: Int, xteaKeys: IntArray? = null): ByteArray? {
        var packedData = getContainerData(containerId) ?: return null
        if (xteaKeys != null && xteaKeys.any { it != 0 }) {
            packedData = XTEACryption.decrypt(xteaKeys, ByteBuffer.wrap(packedData), 5, packedData.size).array()
        }
        return ContainersInformation.unpackCacheContainer(packedData)
    }

    fun getContainerData(containerId: Int): ByteArray? = synchronized(dataFile) {
        try {
            if (indexFile.length() < 6L * (containerId + 1)) return null

            indexFile.seek(6L * containerId)
            indexFile.readFully(cacheFileBuffer, 0, 6)

            val containerSize = ((cacheFileBuffer[0].toInt() and 0xFF) shl 16) or
                    ((cacheFileBuffer[1].toInt() and 0xFF) shl 8) or
                    (cacheFileBuffer[2].toInt() and 0xFF)

            var sector = ((cacheFileBuffer[3].toInt() and 0xFF) shl 16) or
                    ((cacheFileBuffer[4].toInt() and 0xFF) shl 8) or
                    (cacheFileBuffer[5].toInt() and 0xFF)

            if (containerSize <= 0 || containerSize > maxContainerSize) return null
            if (sector <= 0 || dataFile.length() / 520L < sector) return null

            val data = ByteArray(containerSize)
            var offset = 0
            var part = 0

            while (offset < containerSize) {
                if (sector == 0) return null

                dataFile.seek(520L * sector)
                val toRead = minOf(512, containerSize - offset)
                dataFile.readFully(cacheFileBuffer, 0, toRead + 8)

                val readContainerId = ((cacheFileBuffer[0].toInt() and 0xFF) shl 8) or (cacheFileBuffer[1].toInt() and 0xFF)
                val readPart = ((cacheFileBuffer[2].toInt() and 0xFF) shl 8) or (cacheFileBuffer[3].toInt() and 0xFF)
                val nextSector = ((cacheFileBuffer[4].toInt() and 0xFF) shl 16) or
                        ((cacheFileBuffer[5].toInt() and 0xFF) shl 8) or
                        (cacheFileBuffer[6].toInt() and 0xFF)
                val readIndexFileId = cacheFileBuffer[7].toInt() and 0xFF

                if (readContainerId != containerId || readPart != part || readIndexFileId != indexFileId) return null

                System.arraycopy(cacheFileBuffer, 8, data, offset, toRead)
                offset += toRead
                part++
                sector = nextSector
            }

            data
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun write(containerId: Int, data: ByteArray, xteaKeys: IntArray? = null): Boolean = synchronized(dataFile) {
        try {
            val encryptedData = data.copyOf()
            if (xteaKeys != null && xteaKeys.any { it != 0 }) {
                XTEACryption.encrypt(xteaKeys, ByteBuffer.wrap(encryptedData), 0, encryptedData.size)
            }

            val containerSize = encryptedData.size
            val numSectors = (containerSize + 511) / 512

            // Read first sector if exists
            var firstSector = 0
            if (indexFile.length() >= 6L * (containerId + 1)) {
                indexFile.seek(6L * containerId)
                indexFile.readFully(cacheFileBuffer, 0, 6)
                firstSector = ((cacheFileBuffer[3].toInt() and 0xFF) shl 16) or
                        ((cacheFileBuffer[4].toInt() and 0xFF) shl 8) or
                        (cacheFileBuffer[5].toInt() and 0xFF)
            }

            val sectors = IntArray(numSectors)
            if (firstSector > 0 && dataFile.length() / 520L >= firstSector) {
                var sector = firstSector
                for (i in 0 until numSectors) {
                    sectors[i] = sector
                    dataFile.seek(520L * sector)
                    if (dataFile.read(cacheFileBuffer, 0, 8) != 8) break
                    val next = ((cacheFileBuffer[4].toInt() and 0xFF) shl 16) or
                            ((cacheFileBuffer[5].toInt() and 0xFF) shl 8) or
                            (cacheFileBuffer[6].toInt() and 0xFF)
                    sector = if (next != 0) next else (dataFile.length() / 520L + 1).toInt()
                }
            } else {
                val totalSectors = (dataFile.length() / 520L).toInt()
                for (i in 0 until numSectors) sectors[i] = totalSectors + i + 1
            }

            indexFile.seek(6L * containerId)
            indexFile.write((containerSize shr 16) and 0xFF)
            indexFile.write((containerSize shr 8) and 0xFF)
            indexFile.write(containerSize and 0xFF)

            val first = sectors[0]
            indexFile.write((first shr 16) and 0xFF)
            indexFile.write((first shr 8) and 0xFF)
            indexFile.write(first and 0xFF)

            var offset = 0
            for (i in 0 until numSectors) {
                val sector = sectors[i]
                val chunkSize = minOf(512, containerSize - offset)

                dataFile.seek(520L * sector)

                cacheFileBuffer[0] = (containerId shr 8).toByte()
                cacheFileBuffer[1] = containerId.toByte()
                cacheFileBuffer[2] = (i shr 8).toByte()
                cacheFileBuffer[3] = i.toByte()

                val nextSector = if (i < numSectors - 1) sectors[i + 1] else 0
                cacheFileBuffer[4] = (nextSector shr 16).toByte()
                cacheFileBuffer[5] = (nextSector shr 8).toByte()
                cacheFileBuffer[6] = nextSector.toByte()

                cacheFileBuffer[7] = indexFileId.toByte()

                System.arraycopy(encryptedData, offset, cacheFileBuffer, 8, chunkSize)
                dataFile.write(cacheFileBuffer, 0, chunkSize + 8)
                offset += chunkSize
            }

            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}
