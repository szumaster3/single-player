package content.global.skill.construction.decoration.costumeroom

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class StorageContainer {
    private val stored = mutableMapOf<StorableType, MutableList<Int>>()
    private val currentPage = mutableMapOf<StorableType, Int>()

    fun addItem(type: StorableType, id: Int) = stored.getOrPut(type) { mutableListOf() }.add(id)

    fun withdraw(type: StorableType, item: Storable) {
        val id = item.takeIds.firstOrNull() ?: item.displayId
        stored[type]?.remove(id)
    }

    fun contains(type: StorableType, id: Int) = id in (stored[type] ?: emptyList())

    fun getItems(type: StorableType) = stored[type]?.toList() ?: emptyList()

    fun getPageIndex(type: StorableType) = currentPage.getOrDefault(type, 0)

    fun nextPage(type: StorableType, totalItems: Int, pageSize: Int) {
        currentPage[type] =
            (getPageIndex(type) + 1).takeIf { it * pageSize < totalItems } ?: getPageIndex(type)
    }

    fun prevPage(type: StorableType) {
        currentPage[type] = (getPageIndex(type) - 1).coerceAtLeast(0)
    }

    fun toJson(): JsonObject =
        JsonObject().apply {
            stored.forEach { (type, list) ->
                add(type.name.lowercase(), JsonArray().apply { list.forEach(::add) })
            }
        }

    companion object {
        fun fromJson(json: JsonObject) =
            StorageContainer().apply {
                json.entrySet().forEach { (key, value) ->
                    val type = StorableType.valueOf(key.uppercase())
                    stored[type] = value.asJsonArray.map { it.asInt }.toMutableList()
                }
            }
    }
}
