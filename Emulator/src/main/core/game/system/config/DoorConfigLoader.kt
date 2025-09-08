package core.game.system.config

import com.google.gson.Gson
import com.google.gson.JsonArray
import core.ServerConstants
import core.api.log
import core.tools.Log
import java.io.FileReader

class DoorConfigLoader {
    companion object {
        @JvmStatic val DOORS = hashMapOf<Int, Door>()

        @JvmStatic fun forId(id: Int): Door? = DOORS[id]
    }

    private val gson = Gson()

    fun load() {
        var count = 0
        FileReader(ServerConstants.CONFIG_PATH + "door_configs.json").use { reader ->
            val configs = gson.fromJson(reader, JsonArray::class.java)
            for (configElement in configs) {
                val e = configElement.asJsonObject
                val door = Door(e.get("id").asInt)
                door.replaceId = e.get("replaceId").asInt
                door.isFence = e.get("fence").asBoolean
                door.isMetal = e.get("metal").asBoolean
                door.isAutoWalk = e.get("autowalk")?.asBoolean ?: false
                door.questRequirement = e.get("questRequirement")?.asString ?: ""

                DOORS[door.id] = door

                val replacedDoor = Door(door.replaceId)
                replacedDoor.replaceId = door.id
                replacedDoor.isFence = door.isFence
                replacedDoor.isMetal = door.isMetal
                replacedDoor.isAutoWalk = door.isAutoWalk
                replacedDoor.questRequirement = door.questRequirement
                DOORS[door.replaceId] = replacedDoor
                count++
            }
        }

        log(this::class.java, Log.DEBUG, "Parsed $count door configs.")
    }

    class Door(
        val id: Int,
    ) {
        var replaceId = 0
        var isFence = false
        var isAutoWalk = false
        var isMetal = false
        var questRequirement = ""
    }
}
