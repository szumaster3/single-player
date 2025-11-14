package content.global.skill.construction.decoration

import content.global.skill.construction.BuildHotspot
import content.global.skill.construction.HousingStyle
import core.cache.def.impl.SceneryDefinition
import core.game.global.action.DoorActionHandler
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.plugin.Initializable
import core.plugin.Plugin
import core.game.world.map.Location

@Initializable
class DoorPlugin : OptionHandler() {

    override fun newInstance(arg: Any?): Plugin<Any> {
        for (style in HousingStyle.values()) {
            SceneryDefinition.forId(style.doorId).handlers["option:open"] = this
            SceneryDefinition.forId(style.secondDoorId).handlers["option:open"] = this
        }
        for (deco in BuildHotspot.DUNGEON_DOOR_LEFT.decorations + BuildHotspot.DUNGEON_DOOR_RIGHT.decorations) {
            SceneryDefinition.forId(deco.objectId).handlers["option:open"] = this
            SceneryDefinition.forId(deco.objectId).handlers["option:pick-lock"] = this
            SceneryDefinition.forId(deco.objectId).handlers["option:force"] = this
        }
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        val door = node as? Scenery ?: return true

        try {
            val x = door.location.chunkOffsetX
            val y = door.location.chunkOffsetY

            val secondDoor = when {
                y == 0 || y == 7 -> when (x) {
                    3 -> Scenery(door.id, Location.create(door.location.x + 1, door.location.y, door.location.z), door.type, door.rotation)
                    4 -> Scenery(door.id, Location.create(door.location.x - 1, door.location.y, door.location.z), door.type, door.rotation)
                    else -> null
                }
                x == 0 || x == 7 -> when (y) {
                    3 -> Scenery(door.id, Location.create(door.location.x, door.location.y + 1, door.location.z), door.type, door.rotation)
                    4 -> Scenery(door.id, Location.create(door.location.x, door.location.y - 1, door.location.z), door.type, door.rotation)
                    else -> null
                }
                else -> null
            }
            if (secondDoor != null) {
                DoorActionHandler.open(door, secondDoor, door.id + 2, secondDoor.id + 2,true,-1, false)
            }
            handleDoor(door, secondDoor)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    private fun handleDoor(door: Scenery, second: Scenery?) {
        val replaceId = getReplaceId(door)
        val secondReplaceId = second?.let { getReplaceId(it) } ?: -1

        if (door.id in CLOSED_IDS) {
            openDoor(door, second, replaceId, secondReplaceId)
        } else {
            closeDoor(door, second, replaceId, secondReplaceId)
        }
    }

    private fun openDoor(door: Scenery, second: Scenery?, replaceId: Int, secondReplaceId: Int) {
        val (offsetX, offsetY, newRot) = getOpenOffset(door)
        val newDoor = Scenery(replaceId, door.location.transform(offsetX, offsetY, 0), door.type, newRot)
        SceneryBuilder.replace(door, newDoor)

        second?.let {
            val (sOffsetX, sOffsetY, sRot) = getOpenOffset(it)
            val newSecond = Scenery(secondReplaceId, it.location.transform(sOffsetX, sOffsetY, 0), it.type, sRot)
            SceneryBuilder.replace(it, newSecond)
        }
    }

    private fun closeDoor(door: Scenery, second: Scenery?, replaceId: Int, secondReplaceId: Int) {
        val (offsetX, offsetY, newRot) = getCloseOffset(door)
        val newDoor = Scenery(replaceId - 2, door.location.transform(offsetX, offsetY, 0), door.type, newRot)
        SceneryBuilder.replace(door, newDoor)

        second?.let {
            val (sOffsetX, sOffsetY, sRot) = getCloseOffset(it)
            val newSecond = Scenery(secondReplaceId - 2, it.location.transform(sOffsetX, sOffsetY, 0), it.type, sRot)
            SceneryBuilder.replace(it, newSecond)
        }
    }

    private fun getOpenOffset(door: Scenery): Triple<Int, Int, Int> {
        return when {
            door.location.chunkOffsetY == 0 || door.location.chunkOffsetY == 7 -> {
                val offsetY = if (door.location.chunkOffsetY == 7) 1 else -1
                val rot = if (door.location.chunkOffsetX == 3) 0 else 2
                Triple(0, offsetY, rot)
            }
            door.location.chunkOffsetX == 0 || door.location.chunkOffsetX == 7 -> {
                val offsetX = if (door.location.chunkOffsetX == 7) 1 else -1
                val rot = if (door.location.chunkOffsetY == 3) 3 else 1
                Triple(offsetX, 0, rot)
            }
            else -> Triple(0, 0, door.rotation)
        }
    }

    private fun getCloseOffset(door: Scenery): Triple<Int, Int, Int> {
        return when {
            door.location.chunkOffsetY == 0 || door.location.chunkOffsetY == 7 -> {
                val offsetY = if (door.location.chunkOffsetY == 7) 1 else -1
                val rot = if (door.location.chunkOffsetY == 7) 3 else 1
                Triple(0, offsetY, rot)
            }
            door.location.chunkOffsetX == 0 || door.location.chunkOffsetX == 7 -> {
                val offsetX = if (door.location.chunkOffsetX == 7) 1 else -1
                val rot = if (door.location.chunkOffsetX == 7) 0 else 2
                Triple(offsetX, 0, rot)
            }
            else -> Triple(0, 0, door.rotation)
        }
    }

    private fun getReplaceId(door: Scenery): Int = REPLACEMENT_MAP[door.id] ?: (door.id + 2)
    private val CLOSED_IDS = REPLACEMENT_MAP.keys

    companion object {
        private val REPLACEMENT_MAP = mapOf(
            13100 to 13102,
            13101 to 13103,
            13006 to 13008,
            13007 to 13008,
            13015 to 13017,
            13016 to 13018,
            13094 to 13095,
            13096 to 13097,
            13109 to 13110,
            13107 to 13108,
            13118 to 13120,
            13119 to 13121
        )
    }
}
