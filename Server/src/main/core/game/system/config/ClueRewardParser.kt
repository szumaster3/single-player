package core.game.system.config

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import core.ServerConstants
import core.api.utils.WeightBasedTable
import core.api.utils.WeightedItem
import java.io.FileReader

class ClueRewardParser {
    companion object {
        @JvmStatic
        var easyTable = WeightBasedTable()

        @JvmStatic
        var medTable = WeightBasedTable()

        @JvmStatic
        var hardTable = WeightBasedTable()

        @JvmStatic
        var rareTable = WeightBasedTable()
    }

    private val gson = Gson()

    fun load() {
        FileReader(ServerConstants.CONFIG_PATH + "clue_rewards.json").use { reader ->
            val rawData = gson.fromJson(reader, JsonObject::class.java)

            easyTable = parseClueTable(rawData.getAsJsonArray("easy"))
            medTable  = parseClueTable(rawData.getAsJsonArray("medium"))
            hardTable = parseClueTable(rawData.getAsJsonArray("hard"))
            rareTable = parseClueTable(rawData.getAsJsonArray("rare"))
        }
    }

    private fun parseClueTable(data: JsonArray): WeightBasedTable {
        val table = WeightBasedTable()

        for (element in data) {
            val itemData = element.asJsonObject

            table.add(
                WeightedItem(
                    itemData.get("id").asInt,
                    itemData.get("minAmount").asInt,
                    itemData.get("maxAmount").asInt,
                    itemData.get("weight").asDouble,
                    false,
                )
            )
        }

        return table
    }
}
