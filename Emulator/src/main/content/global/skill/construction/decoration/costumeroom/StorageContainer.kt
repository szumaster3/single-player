package content.global.skill.construction.decoration.costumeroom

import org.json.simple.JSONArray
import org.json.simple.JSONObject

class StorageContainer {
    private val maxSize = 30
    val storedItems = mutableListOf<Storable>()
    var currentPage = 0
    val pageSize = 30

    fun addItem(item: Storable): Boolean {
        if (storedItems.size >= maxSize) return false
        if (!storedItems.contains(item)) storedItems.add(item)
        return true
    }

    fun removeItem(item: Storable): Boolean {
        return storedItems.remove(item)
    }

    fun hasItem(item: Storable): Boolean {
        return storedItems.contains(item)
    }

    fun store(item: Storable): Boolean = addItem(item)

    fun withdraw(item: Storable): Boolean = removeItem(item)

    fun nextPage() {
        if ((currentPage + 1) * pageSize < Storable.values().size) currentPage++
    }

    fun prevPage() {
        if (currentPage > 0) currentPage--
    }

    fun getPageItems(): List<Storable> {
        val start = currentPage * pageSize
        val end = (start + pageSize).coerceAtMost(storedItems.size)
        if (start >= storedItems.size) return emptyList()
        return storedItems.subList(start, end)
    }

    fun toJson(): JSONObject {
        val root = JSONObject()
        val itemsArray = JSONArray()
        for (item in storedItems) {
            val itemObj = JSONObject()
            itemObj["name"] = item.name
            itemObj["displayId"] = item.displayId
            itemObj["takeIds"] = JSONArray().apply { item.takeIds.forEach { add(it.toString()) } }
            itemObj["type"] = item.type.name
            itemsArray.add(itemObj)
        }
        root["items"] = itemsArray
        return root
    }

    companion object {
        fun fromJson(root: JSONObject): StorageContainer {
            val container = StorageContainer()
            val itemsArray = root["items"] as? JSONArray ?: JSONArray()
            for (i in 0 until itemsArray.size) {
                val itemObj = itemsArray[i] as JSONObject
                val name = itemObj["name"].toString()
                val itemEnum = try {
                    Storable.valueOf(name)
                } catch (e: Exception) {
                    null
                }
                if (itemEnum != null) {
                    container.storedItems.add(itemEnum)
                }
            }
            container.currentPage = 0
            return container
        }
    }
}