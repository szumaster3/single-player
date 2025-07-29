package core

import com.google.gson.*
import core.api.PersistWorld
import core.api.getItemName
import core.api.log
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.tools.Log
import core.tools.SystemLogger.logShutdown
import core.tools.SystemLogger.logStartup
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Handles server-wide data persistence using JSON files.
 * Implements [PersistWorld] for parsing and saving stored data.
 */
class ServerStore : PersistWorld {

    override fun parse() {
        logStartup("Parsing server store...")
        val dir = File(ServerConstants.STORE_PATH ?: return)
        if (!dir.exists()) {
            dir.mkdirs()
            return
        }

        val gson = Gson()
        var count = 0
        dir.listFiles()?.forEach { file ->
            val key = file.nameWithoutExtension
            try {
                FileReader(file).use { reader ->
                    val jsonElement = JsonParser.parseReader(reader)
                    if (jsonElement.isJsonObject) {
                        fileMap[key] = jsonElement.asJsonObject
                        count++
                    }
                }
            } catch (e: Exception) {
                log(this::class.java, Log.ERR, "Failed parsing ${file.name} - stack trace below.")
                e.printStackTrace()
            }
        }
        counter = count
        logStartup("Initialized $counter store files.")
    }

    override fun save() {
        logShutdown("Saving server store...")
        val dir = File(ServerConstants.DATA_PATH + File.separator + "serverstore")
        if (!dir.exists()) {
            dir.mkdirs()
            return
        }

        val gson = Gson().newBuilder().setPrettyPrinting().create()

        fileMap.forEach { (name, data) ->
            val path = "${dir.absolutePath}${File.separator}$name.json"
            FileWriter(path).use { writer ->
                gson.toJson(data, writer)
            }
        }
    }

    companion object {
        val fileMap = HashMap<String, JsonObject>()
        var counter = 0

        @JvmStatic
        fun getArchive(name: String): JsonObject = fileMap.getOrPut(name) { JsonObject() }

        fun setArchive(name: String, data: JsonObject) {
            fileMap[name] = data
        }

        fun clearDailyEntries() {
            fileMap.keys.filter { it.lowercase().contains("daily") }.forEach { fileMap[it]?.entrySet()?.clear() }
        }

        fun clearWeeklyEntries() {
            fileMap.keys.filter { it.lowercase().contains("weekly") }.forEach { fileMap[it]?.entrySet()?.clear() }
        }

        fun JsonObject.getInt(key: String, default: Int = 0): Int =
            this[key]?.asInt ?: default

        fun JsonObject.getString(key: String, default: String = "nothing"): String =
            this[key]?.asString ?: default

        fun JsonObject.getLong(key: String, default: Long = 0L): Long =
            this[key]?.asLong ?: default

        fun JsonObject.getBoolean(key: String, default: Boolean = false): Boolean =
            this[key]?.asBoolean ?: default

        fun List<Int>.toJsonArray(): com.google.gson.JsonArray =
            com.google.gson.JsonArray().apply { this@toJsonArray.forEach { add(it) } }

        fun JsonObject.addToList(key: String, value: String) {
            val array: JsonArray = if (this.has(key) && this.get(key).isJsonArray) {
                this.getAsJsonArray(key)
            } else {
                val newArray = JsonArray()
                this.add(key, newArray)
                newArray
            }
            array.add(JsonPrimitive(value))
        }

        inline fun <reified T> JsonObject.getList(key: String): List<T> {
            val arr = this[key]?.asJsonArray ?: return emptyList()
            return arr.mapNotNull { element ->
                when {
                    T::class.java == String::class.java && element.isJsonPrimitive -> element.asString as? T
                    T::class.java == Int::class.java && element.isJsonPrimitive -> element.asInt as? T
                    else -> null
                }
            }
        }

        fun JsonObject.addToList(key: String, value: com.google.gson.JsonElement) {
            val arr = this.getAsJsonArray(key) ?: com.google.gson.JsonArray().also { this.add(key, it) }
            arr.add(value)
        }

        fun NPCItemFilename(npc: Int, item: Int, period: String = "daily"): String {
            val itemName = getItemName(item).lowercase().replace(" ", "-")
            val npcName = NPC(npc).name.lowercase()
            return "$period-$npcName-$itemName"
        }

        fun NPCItemMemory(npc: Int, item: Int, period: String = "daily"): JsonObject =
            getArchive(NPCItemFilename(npc, item, period))

        fun getNPCItemStock(npc: Int, item: Int, limit: Int, player: Player, period: String = "daily"): Int {
            val itemMemory = NPCItemMemory(npc, item, period)
            val key = player.name
            val stock = limit - itemMemory.getInt(key)
            return stock.coerceAtLeast(0)
        }

        fun getNPCItemAmount(npc: Int, item: Int, limit: Int, player: Player, amount: Int, period: String = "daily"): Int {
            val stock = getNPCItemStock(npc, item, limit, player, period)
            return amount.coerceAtMost(stock).coerceAtLeast(0)
        }

        fun addNPCItemAmount(npc: Int, item: Int, limit: Int, player: Player, amount: Int, period: String = "daily") {
            val itemMemory = NPCItemMemory(npc, item, period)
            val key = player.name
            val newAmount = (itemMemory.getInt(key) + amount).coerceIn(0, limit)
            itemMemory.addProperty(key, newAmount)
        }
    }
}
