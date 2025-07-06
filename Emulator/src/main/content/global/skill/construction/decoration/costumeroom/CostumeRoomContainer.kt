package content.global.skill.construction.decoration.costumeroom

import org.json.simple.JSONArray
import org.json.simple.JSONObject

class CostumeRoomContainer {
    private val maxSize = 30
    val storedItems = mutableListOf<CostumeRoomStorage>()
    var currentPage = 0
    val pageSize = 30

    fun addItem(item: CostumeRoomStorage): Boolean {
        if (storedItems.size >= maxSize) return false
        storedItems.add(item)
        return true
    }

    fun removeItem(item: CostumeRoomStorage): Boolean {
        return storedItems.remove(item)
    }

    fun hasItem(item: CostumeRoomStorage): Boolean {
        return storedItems.contains(item)
    }

    fun store(item: CostumeRoomStorage): Boolean = addItem(item)

    fun withdraw(item: CostumeRoomStorage): Boolean = removeItem(item)

    fun nextPage() {
        if ((currentPage + 1) * pageSize < storedItems.size) currentPage++
    }

    fun prevPage() {
        if (currentPage > 0) currentPage--
    }

    fun getPageItems(): List<CostumeRoomStorage> {
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
        root["page"] = currentPage.toString()
        return root
    }

    companion object {
        fun fromJson(root: JSONObject): CostumeRoomContainer {
            val container = CostumeRoomContainer()
            val itemsArray = root["items"] as? JSONArray ?: JSONArray()
            for (i in 0 until itemsArray.size) {
                val itemObj = itemsArray[i] as JSONObject
                val name = itemObj["name"].toString()
                val itemEnum = try {
                    CostumeRoomStorage.valueOf(name)
                } catch (e: Exception) {
                    null
                }
                if (itemEnum != null) {
                    container.storedItems.add(itemEnum)
                }
            }
            container.currentPage = root["page"]?.toString()?.toIntOrNull() ?: 0
            return container
        }
    }
}