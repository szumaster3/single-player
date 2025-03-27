package core.game.ge

import core.ServerConstants
import core.api.StartupListener
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileReader

/**
 * This class handles automatic stocking of items for the Grand Exchange.
 * It reads a JSON file containing the stock information and adds offers to the Grand Exchange.
 * This is triggered on server startup.
 */
class AutoStock : StartupListener {
    /**
     * Called when the server starts. It triggers the autostock process.
     */
    override fun startup() {
        autostock()
    }

    companion object {
        /** The path to the JSON file that contains the auto-stock data. */
        private val DB_PATH = "data" + File.separator + "eco" + File.separator + "autostock.json"

        /**
         * Reads the auto-stock JSON file and adds bot offers to the Grand Exchange.
         * This is executed only if auto-stock is enabled.
         */
        fun autostock() {
            if (ServerConstants.GE_AUTOSTOCK_ENABLED) {
                val parser = JSONParser()

                // Reading the JSON file containing the bot offers
                val botReader: FileReader = FileReader(DB_PATH)
                val botSave = parser.parse(botReader) as JSONObject

                // If the JSON contains offers, process them
                if (botSave.containsKey("offers")) {
                    val offers = botSave["offers"] as JSONArray

                    // Iterate over each offer and add it to the Grand Exchange
                    for (offer in offers) {
                        val o = offer as JSONObject
                        // Adding a bot offer using the item ID and quantity
                        GrandExchange.addBotOffer(o["item"].toString().toInt(), o["qty"].toString().toInt())
                    }
                }
            }
        }
    }
}
