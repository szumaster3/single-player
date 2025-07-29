package content.global.skill.construction.decoration.costumeroom

import com.google.gson.JsonObject
import core.game.node.entity.player.Player

class StorageState(val player: Player) {

    enum class ContainerGroup {
        BOOK, CAPE, FANCY, TOY, TRAILS, ARMOUR, ARMOUR_CASE;

        companion object {
            fun fromType(type: Storable.Type): ContainerGroup = when (type) {
                Storable.Type.BOOK -> BOOK
                Storable.Type.CAPE -> CAPE
                Storable.Type.FANCY -> FANCY
                Storable.Type.TOY -> TOY

                Storable.Type.LOW_LEVEL_TRAILS,
                Storable.Type.MED_LEVEL_TRAILS,
                Storable.Type.HIGH_LEVEL_TRAILS -> TRAILS

                Storable.Type.ONE_SET_OF_ARMOUR,
                Storable.Type.TWO_SETS_OF_ARMOUR,
                Storable.Type.THREE_SETS_OF_ARMOUR,
                Storable.Type.FOUR_SETS_OF_ARMOUR,
                Storable.Type.FIVE_SETS_OF_ARMOUR,
                Storable.Type.SIX_SETS_OF_ARMOUR,
                Storable.Type.ALL_SETS_OF_ARMOUR -> ARMOUR

                Storable.Type.TWO_SETS_ARMOUR_CASE,
                Storable.Type.FOUR_SETS_ARMOUR_CASE,
                Storable.Type.ALL_SETS_ARMOUR_CASE -> ARMOUR_CASE
            }
        }
    }

    val containers: MutableMap<ContainerGroup, StorageContainer> = mutableMapOf()

    init {
        for (group in ContainerGroup.values()) {
            containers[group] = StorageContainer()
        }
    }

    fun getContainer(type: Storable.Type): StorageContainer {
        return containers[ContainerGroup.fromType(type)]!!
    }

    fun toJson(): JsonObject {
        val save = JsonObject()
        val containersJson = JsonObject()
        containers.forEach { (group, container) ->
            containersJson.add(group.name.lowercase(), container.toJson())
        }
        save.add("containers", containersJson)
        return save
    }

    fun readJson(data: JsonObject) {
        val containersJson = data.getAsJsonObject("containers") ?: return
        for (group in ContainerGroup.values()) {
            if (containersJson.has(group.name.lowercase())) {
                containers[group] = StorageContainer.fromJson(
                    containersJson.getAsJsonObject(group.name.lowercase())
                )
            }
        }
    }

}