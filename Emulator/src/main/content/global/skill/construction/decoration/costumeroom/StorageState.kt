package content.global.skill.construction.decoration.costumeroom

import core.game.node.entity.player.Player
import org.json.simple.JSONObject

class StorageState(val player: Player) {
    val containers: MutableMap<Storable.Type, StorageContainer> = mutableMapOf(
        Storable.Type.BOOK to StorageContainer(),
        Storable.Type.CAPE to StorageContainer(),
        Storable.Type.FANCY to StorageContainer(),
        Storable.Type.TOY to StorageContainer(),
        Storable.Type.LOW_LEVEL_TRAILS to StorageContainer(),
        Storable.Type.MED_LEVEL_TRAILS to StorageContainer(),
        Storable.Type.HIGH_LEVEL_TRAILS to StorageContainer(),
        Storable.Type.ONE_SET_OF_ARMOUR to StorageContainer(),
        Storable.Type.TWO_SETS_OF_ARMOUR to StorageContainer(),
        Storable.Type.THREE_SETS_OF_ARMOUR to StorageContainer(),
        Storable.Type.FOUR_SETS_OF_ARMOUR to StorageContainer(),
        Storable.Type.FIVE_SETS_OF_ARMOUR to StorageContainer(),
        Storable.Type.SIX_SETS_OF_ARMOUR to StorageContainer(),
        Storable.Type.ALL_SETS_OF_ARMOUR to StorageContainer(),
        Storable.Type.TWO_SETS_ARMOUR_CASE to StorageContainer(),
        Storable.Type.FOUR_SETS_ARMOUR_CASE to StorageContainer(),
        Storable.Type.ALL_SETS_ARMOUR_CASE to StorageContainer()
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
            Storable.Type.values().forEach { type ->
                if (containersJson.containsKey(type.name.lowercase())) {
                    containers[type] = StorageContainer.fromJson(containersJson[type.name.lowercase()] as JSONObject)
                }
            }
        }
    }
}
