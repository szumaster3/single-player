package content.global.skill.construction.decoration.costumeroom

import core.game.node.entity.player.Player
import org.json.simple.JSONObject

class CostumeRoomPlayerState(val player: Player) {
    val containers: MutableMap<CostumeRoomStorage.Type, CostumeRoomContainer> = mutableMapOf(
        CostumeRoomStorage.Type.BOOK to CostumeRoomContainer(),
        CostumeRoomStorage.Type.CAPE to CostumeRoomContainer(),
        CostumeRoomStorage.Type.FANCY to CostumeRoomContainer(),
        CostumeRoomStorage.Type.TOY to CostumeRoomContainer(),
        CostumeRoomStorage.Type.LOW_LEVEL_TRAILS to CostumeRoomContainer(),
        CostumeRoomStorage.Type.MED_LEVEL_TRAILS to CostumeRoomContainer(),
        CostumeRoomStorage.Type.HIGH_LEVEL_TRAILS to CostumeRoomContainer(),
        CostumeRoomStorage.Type.ONE_SET_OF_ARMOUR to CostumeRoomContainer(),
        CostumeRoomStorage.Type.TWO_SETS_OF_ARMOUR to CostumeRoomContainer(),
        CostumeRoomStorage.Type.THREE_SETS_OF_ARMOUR to CostumeRoomContainer(),
        CostumeRoomStorage.Type.FOUR_SETS_OF_ARMOUR to CostumeRoomContainer(),
        CostumeRoomStorage.Type.FIVE_SETS_OF_ARMOUR to CostumeRoomContainer(),
        CostumeRoomStorage.Type.SIX_SETS_OF_ARMOUR to CostumeRoomContainer(),
        CostumeRoomStorage.Type.ALL_SETS_OF_ARMOUR to CostumeRoomContainer(),
        CostumeRoomStorage.Type.TWO_SETS_ARMOUR_CASE to CostumeRoomContainer(),
        CostumeRoomStorage.Type.FOUR_SETS_ARMOUR_CASE to CostumeRoomContainer(),
        CostumeRoomStorage.Type.ALL_SETS_ARMOUR_CASE to CostumeRoomContainer()
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
