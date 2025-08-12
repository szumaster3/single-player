package core.game.system.config

import com.google.gson.Gson
import com.google.gson.JsonArray
import core.ServerConstants
import core.api.log
import core.cache.def.impl.SceneryDefinition
import core.tools.Log
import java.io.FileReader

class SceneryConfigParser {
    private val gson = Gson()

    fun load() {
        var count = 0
        FileReader(ServerConstants.CONFIG_PATH + "object_configs.json").use { reader ->
            val configList = gson.fromJson(reader, JsonArray::class.java)
            for (element in configList) {
                val e = element.asJsonObject
                val ids = e.get("ids")?.asString
                    ?.split(",")
                    ?.mapNotNull { it.trim().toIntOrNull() }
                    ?: continue

                for (id in ids) {
                    val def = SceneryDefinition.forId(id)
                    val configs = def.handlers

                    for ((key, value) in e.entrySet()) {
                        val keyStr = key
                        val valueStr = value.asString
                        if (valueStr.isEmpty() || valueStr == "null") continue

                        when (keyStr) {
                            "examine" -> configs[keyStr] = valueStr
                            "render_anim" -> def.animations = valueStr.toIntOrNull() ?: def.animations
                        }
                    }
                    count++
                }
            }
        }
        log(this::class.java, Log.DEBUG, "Parsed $count object configs.")
    }
}
