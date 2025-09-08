package core.game.ge

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import core.api.getItemName
import core.game.world.GameWorld
import core.tools.SystemLogger
import java.io.File
import java.io.FileReader

/**
 * Handles automatic stocking of Grand Exchange by
 * reading offers from a JSON file on server startup.
 */
class AutoStock {

    companion object {
        private val DB_PATH = "data${File.separator}eco${File.separator}ge_autostock.json"

        fun autostock() {
            if (GameWorld.settings?.autostock_ge != true) {
                SystemLogger.logGE("AutoStock is disabled in server settings.")
                return
            }

            SystemLogger.logGE("Loading AutoStock offers from $DB_PATH")

            val parser = JsonParser()
            val botReader = FileReader(DB_PATH)
            val botSave = parser.parse(botReader).asJsonObject

            val offers: JsonArray? = botSave.getAsJsonArray("offers")
            if (offers == null) {
                SystemLogger.logGE("No offers found in AutoStock JSON.")
                return
            }

            var count = 0
            for (offerElement in offers) {
                val o = offerElement.asJsonObject
                val item = o.get("item_id").asInt
                val amount = o.get("amount").asInt

                if (GrandExchange.addBotOffer(item, amount)) {
                    SystemLogger.logGE("AutoStocked $amount x ${getItemName(item)} [ID: $item]")
                    count++
                } else {
                    SystemLogger.logGE("Skipped item $item - not tradeable or failed to add.")
                }
            }

            SystemLogger.logGE("AutoStock completed. Total items stocked: $count")
        }
    }
}
