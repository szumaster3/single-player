package content.global.skill.construction.decoration.costumeroom

import core.game.node.entity.player.Player
import org.json.simple.JSONObject

class CostumeRoomPlayerState(val player: Player) {
    val containers: MutableMap<CostumeRoomStorage.Type, CostumeRoomContainer> = mutableMapOf(
        CostumeRoomStorage.Type.BOOK to CostumeRoomContainer(),
        CostumeRoomStorage.Type.CAPE to CostumeRoomContainer(),
        CostumeRoomStorage.Type.FANCY to CostumeRoomContainer(),
        CostumeRoomStorage.Type.TOY to CostumeRoomContainer()
    )

    fun toJson(): JSONObject {
        val save = JSONObject()
        val containersJson = JSONObject()
        containers.forEach { (type, container) ->
            containersJson[type.name.lowercase()] = container.toJson()
        }
        save["containers"] = containersJson
        return save
    }

    fun readJson(data: JSONObject) {
        if (data.containsKey("containers")) {
            val containersJson = data["containers"] as JSONObject
            CostumeRoomStorage.Type.values().forEach { type ->
                if (containersJson.containsKey(type.name.lowercase())) {
                    containers[type] = CostumeRoomContainer.fromJson(containersJson[type.name.lowercase()] as JSONObject)
                }
            }
        }
    }
}