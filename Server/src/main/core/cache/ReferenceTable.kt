package core.cache

import core.cache.misc.Container
import core.cache.misc.ContainersInformation
import core.cache.misc.FilesContainer
import core.cache.misc.buffer.OutputStream
import java.util.*

class ReferenceTable(private val info: ContainersInformation) {
    private var revision: Int = info.revision
    private var named: Boolean = info.filesNamed
    private var usesWhirpool: Boolean = info.whirpool
    private var archives: Array<FilesContainer?> = info.containers.copyOf().map { it as FilesContainer? }.toTypedArray()
    private var validArchiveIds: IntArray = info.containersIndexes

    private var updatedRevision = false
    private var needsArchivesSort = false
    private var needsFilesSort: BooleanArray = BooleanArray(archives.size) { false }

    init {
        decodeHeader()
    }

    fun sortArchives() {
        Arrays.sort(validArchiveIds)
        needsArchivesSort = false
    }

    fun addEmptyArchive(archiveId: Int) {
        needsArchivesSort = true
        val newValidIds = IntArray(validArchiveIds.size + 1)
        System.arraycopy(validArchiveIds, 0, newValidIds, 0, validArchiveIds.size)
        newValidIds[newValidIds.size - 1] = archiveId
        validArchiveIds = newValidIds

        val reference: FilesContainer
        if (archives.size <= archiveId) {
            val newArchives = arrayOfNulls<FilesContainer>(archiveId + 1)
            System.arraycopy(archives, 0, newArchives, 0, archives.size)
            reference = FilesContainer()
            newArchives[archiveId] = reference
            archives = newArchives
        } else {
            reference = FilesContainer()
            archives[archiveId] = reference
        }
        needsFilesSort = BooleanArray(archives.size) { false }
    }

    fun addEmptyFile(archiveId: Int, fileId: Int) {
        val archive = archives.getOrNull(archiveId) ?: return
        if (archive.files == null) archive.files = emptyArray()
        val filesList = archive.files!!.toMutableList()
        while (filesList.size <= fileId) filesList.add(Container())
        archive.files = filesList.toTypedArray()
        needsFilesSort[archiveId] = true
    }

    fun sortTable() {
        if (needsArchivesSort) sortArchives()
        for (id in validArchiveIds) {
            if (needsFilesSort.getOrElse(id) { false }) sortFiles(id)
        }
    }

    private fun sortFiles(archiveId: Int) {
        val archive = archives[archiveId] ?: return
        val files = archive.files ?: return
        archive.files = files.sortedBy { it.nameHash }.toTypedArray()
        needsFilesSort[archiveId] = false
    }

    fun setRevision(rev: Int) {
        revision = rev
        updatedRevision = true
    }

    fun updateRevision() {
        if (!updatedRevision) {
            revision++
            updatedRevision = true
        }
    }

    fun getRevision() = revision

    fun getArchives(): Array<FilesContainer?> = archives

    fun getValidArchiveIds(): IntArray = validArchiveIds

    fun isNamed() = named

    fun usesWhirpool() = usesWhirpool

    fun getProtocol(): Int {
        if (archives.size > 65535) return 7
        for (id in validArchiveIds) {
            val archive = archives[id] ?: continue
            val filesCount = archive.files?.size ?: 0
            if (filesCount > 65535) return 7
        }
        return if (revision == 0) 5 else 6
    }

    fun encodeHeader(): ByteArray {
        val stream = OutputStream()
        val protocol = getProtocol()

        stream.writeByte(protocol)
        if (protocol >= 6) stream.writeInt(revision)
        stream.writeByte((if (named) 0x1 else 0) or (if (usesWhirpool) 0x2 else 0))

        if (protocol >= 7) stream.writeBigSmart(validArchiveIds.size)
        else stream.writeShort(validArchiveIds.size)

        var lastArchive = 0
        for (id in validArchiveIds) {
            val offset = id - lastArchive
            if (protocol >= 7) stream.writeBigSmart(offset) else stream.writeShort(offset)
            lastArchive = id
        }

        if (named) {
            for (id in validArchiveIds) {
                val archive = archives[id] ?: continue
                stream.writeInt(archive.nameHash)
            }
        }

        for (id in validArchiveIds) {
            val archive = archives[id] ?: continue
            stream.writeInt(archive.crc)
        }
        for (id in validArchiveIds) {
            val archive = archives[id] ?: continue
            stream.writeInt(archive.version)
        }

        for (id in validArchiveIds) {
            val archive = archives[id] ?: continue
            val filesCount = archive.files?.size ?: 0
            if (protocol >= 7) stream.writeBigSmart(filesCount) else stream.writeShort(filesCount)
        }

        for (id in validArchiveIds) {
            val archive = archives[id] ?: continue
            val files = archive.files ?: continue
            var lastFile = 0
            for ((fileId, _) in files.withIndex()) {
                val offset = fileId - lastFile
                if (protocol >= 7) stream.writeBigSmart(offset) else stream.writeShort(offset)
                lastFile = fileId
            }
        }

        if (named) {
            for (id in validArchiveIds) {
                val archive = archives[id] ?: continue
                val files = archive.files ?: continue
                for (file in files) {
                    stream.writeInt(file.nameHash)
                }
            }
        }

        val data = ByteArray(stream.offset)
        stream.setOffset(0)
        stream.getBytes(data, 0, data.size)
        return data
    }

    private fun decodeHeader() {
        named = info.filesNamed
        usesWhirpool = info.whirpool
        archives = info.containers.copyOf().map { it as FilesContainer? }.toTypedArray()
        validArchiveIds = info.containersIndexes
        needsFilesSort = BooleanArray(archives.size) { false }
    }

    companion object ReferenceTableAdapter {

        fun loadReferenceTableForIndex(indexId: Int): ReferenceTable? {
            val refFile = Cache.getReferenceFile() ?: return null
            val packed = refFile.getContainerData(indexId) ?: return null
            val unpacked = ContainersInformation.unpackCacheContainer(packed) ?: return null
            val info = ContainersInformation(unpacked)
            return ReferenceTable(info)
        }

        fun writeReferenceTableForIndex(indexId: Int, refTable: ReferenceTable): Boolean {
            val refFile = Cache.getReferenceFile() ?: return false

            val body = refTable.encodeHeader()
            val container = buildUncompressedContainer(body)
            val wrote = refFile.write(indexId, container, null)
            if (!wrote) return false

            val managers = Cache.getIndexes()
            if (indexId in managers.indices) {
                val manager = managers[indexId]
                if (manager != null) {
                    val newPacked = refFile.getContainerData(indexId)
                    if (newPacked != null) {
                        try {
                            val info = ContainersInformation(newPacked)
                            manager.setInformation(info)
                        } catch (_: Throwable) {}
                    }
                }
            }

            return true
        }

        private fun buildUncompressedContainer(body: ByteArray): ByteArray {
            val out = ByteArray(1 + 4 + body.size)
            out[0] = 0
            val len = body.size
            out[1] = ((len shr 24) and 0xFF).toByte()
            out[2] = ((len shr 16) and 0xFF).toByte()
            out[3] = ((len shr 8) and 0xFF).toByte()
            out[4] = (len and 0xFF).toByte()
            System.arraycopy(body, 0, out, 5, body.size)
            return out
        }
    }
}
