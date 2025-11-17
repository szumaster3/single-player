package core.cache

import core.cache.misc.buffer.InputStream
import core.cache.misc.buffer.OutputStream

class ReferenceTable(data: ByteArray, expectedCrc: Int) {
    var revision: Int = 0
        private set

    var validArchiveIds: IntArray = intArrayOf()
        private set

    var archiveCRCs: IntArray = intArrayOf()
    var archiveRevisions: IntArray = intArrayOf()
    var archiveFileCounts: IntArray = intArrayOf()
    var validFileIds: Array<IntArray?> = arrayOf()
    var archiveNameHashes: IntArray? = null
    var fileNameHashes: Array<IntArray?>? = null

    var archiveLookup: LookupTable? = null
    var fileLookup: Array<LookupTable?>? = null

    init {
        decode(data)
        archiveNameHashes?.let { archiveLookup = LookupTable(it) }

        if (fileNameHashes != null) {
            val tmp = Array(validArchiveIds.maxOrNull()!! + 1) { null as LookupTable? }
            for (archiveId in validArchiveIds) {
                val hashes = fileNameHashes!![archiveId]
                if (hashes != null) tmp[archiveId] = LookupTable(hashes)
            }
            fileLookup = tmp
        }
    }

    private fun decode(data: ByteArray) {
        val buffer = InputStream(data)
        val format = buffer.readUnsignedByte()
        require(format == 5 || format == 6) { "Unexpected ReferenceTable format $format" }

        revision = if (format >= 6) buffer.readInt() else 0
        val flags = buffer.readUnsignedByte()
        val named = flags and 0x1 != 0

        val archiveCount = buffer.readUnsignedShort()
        validArchiveIds = IntArray(archiveCount)
        var last = 0
        for (i in 0 until archiveCount) {
            val offset = buffer.readUnsignedShort()
            last += offset
            validArchiveIds[i] = last
        }

        archiveCRCs = IntArray(validArchiveIds.maxOrNull()!! + 1)
        archiveRevisions = IntArray(validArchiveIds.maxOrNull()!! + 1)
        archiveFileCounts = IntArray(validArchiveIds.maxOrNull()!! + 1)
        validFileIds = Array(archiveCRCs.size) { null }

        if (named) {
            archiveNameHashes = IntArray(archiveCRCs.size) { -1 }
            for (id in validArchiveIds) {
                archiveNameHashes!![id] = buffer.readInt()
            }
        }

        for (id in validArchiveIds) {
            archiveCRCs[id] = buffer.readInt()
        }

        for (id in validArchiveIds) {
            archiveRevisions[id] = buffer.readInt()
        }

        for (id in validArchiveIds) {
            archiveFileCounts[id] = buffer.readUnsignedShort()
        }

        for (archiveId in validArchiveIds) {
            val fileCount = archiveFileCounts[archiveId]
            if (fileCount == 0) continue
            val fileIds = IntArray(fileCount)
            var lastFile = 0
            for (i in 0 until fileCount) {
                val offset = buffer.readUnsignedShort()
                lastFile += offset
                fileIds[i] = lastFile
            }
            validFileIds[archiveId] = fileIds
        }

        if (named) {
            fileNameHashes = Array(archiveCRCs.size) { null }
            for (archiveId in validArchiveIds) {
                val files = validFileIds[archiveId]
                if (files == null || files.isEmpty()) continue
                val fileHashes = IntArray(files.size) { -1 }
                for (i in files.indices) {
                    fileHashes[i] = buffer.readInt()
                }
                fileNameHashes!![archiveId] = fileHashes
            }
        }
    }

    fun encode(): ByteArray {
        val stream = OutputStream()
        val protocol = if (revision == 0) 5 else 6
        stream.writeByte(protocol)
        if (protocol >= 6) stream.writeInt(revision)
        val namedFlag = if (archiveNameHashes != null) 0x1 else 0
        stream.writeByte(namedFlag)

        stream.writeShort(validArchiveIds.size)
        var lastArchive = 0
        for (id in validArchiveIds) {
            val offset = id - lastArchive
            stream.writeShort(offset)
            lastArchive = id
        }

        if (archiveNameHashes != null) {
            for (id in validArchiveIds) {
                stream.writeInt(archiveNameHashes!![id])
            }
        }

        for (id in validArchiveIds) stream.writeInt(archiveCRCs[id])
        for (id in validArchiveIds) stream.writeInt(archiveRevisions[id])
        for (id in validArchiveIds) stream.writeShort(archiveFileCounts[id])

        for (archiveId in validArchiveIds) {
            val files = validFileIds[archiveId] ?: continue
            var lastFile = 0
            for (fileId in files) {
                val offset = fileId - lastFile
                stream.writeShort(offset)
                lastFile = fileId
            }
        }

        if (fileNameHashes != null) {
            for (archiveId in validArchiveIds) {
                val files = validFileIds[archiveId] ?: continue
                val hashes = fileNameHashes!![archiveId] ?: continue
                for (i in files.indices) {
                    stream.writeInt(hashes[i])
                }
            }
        }

        return stream.toByteArray()
    }

    fun getArchiveIndexByNameHash(hash: Int): Int? = archiveLookup?.getIndex(hash)

    fun getFileIndexByNameHash(archiveId: Int, hash: Int): Int? =
        fileLookup?.getOrNull(archiveId)?.getIndex(hash)
}

class LookupTable(private val hashes: IntArray) {
    private val hashMap: Map<Int, Int> = hashes.mapIndexed { i, h -> h to i }.toMap()

    fun getIndex(hash: Int): Int? = hashMap[hash]
}
