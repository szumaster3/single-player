package core.cache.misc

class Container {
    var version: Int = -1
    var crc: Int = -1
    var nameHash: Int = -1
    var updated: Boolean = false

    fun updateVersion() {
        version++
        updated = true
    }

    fun getNextVersion(): Int = if (updated) version else version + 1
}