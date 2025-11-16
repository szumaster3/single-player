package core.cache.misc

class FilesContainer {
    var nameHash: Int = 0
    var crc: Int = 0
    var version: Int = 0
    lateinit var filesIndexes: IntArray
    lateinit var files: Array<Container>
}