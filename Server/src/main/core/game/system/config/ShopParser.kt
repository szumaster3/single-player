package core.game.system.config

import com.google.gson.Gson
import com.google.gson.JsonArray
import core.ServerConstants
import core.api.log
import core.game.node.entity.player.Player
import core.tools.Log
import java.io.FileReader

class ShopParser {
    private val gson = Gson()

    companion object {
        fun openUid(
            player: Player,
            uid: Int,
        ): Boolean = true
    }

    fun load() {
        var count = 0
        FileReader(ServerConstants.CONFIG_PATH + "shops.json").use { reader ->
            val configList = gson.fromJson(reader, JsonArray::class.java)
            for (config in configList) {
                count++
            }
        }

        log(this::class.java, Log.INFO, "Parsed $count shops.")
    }
}
