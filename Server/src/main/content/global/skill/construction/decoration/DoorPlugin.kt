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
import core.game.world.map.Direction
import core.game.world.map.RegionManager
import core.plugin.Initializable
import core.plugin.Plugin
import java.awt.Point

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
            val secondDoor = getSecondDoor(door)
            if (secondDoor != null) {
                DoorActionHandler.open(
                    door,
                    secondDoor,
                    door.id + 2,
                    secondDoor.id + 2,
                    true,
                    -1,
                    false
                )
            }
            handleDoorState(door, secondDoor)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    private fun handleDoorState(door: Scenery, second: Scenery?) {
        val replaceId = getReplaceId(door)
        val secondReplaceId = second?.let { getReplaceId(it) } ?: -1

        if (door.id in CLOSED_IDS) {
            openDoor(door, second, replaceId, secondReplaceId)
        } else {
            closeDoor(door, second, replaceId, secondReplaceId)
        }
    }

    private fun openDoor(door: Scenery, second: Scenery?, replaceId: Int, secondReplaceId: Int) {
        if (second == null) {
            val (offsetX, offsetY, newRot) = getOpenOffset(door)
            val newDoor =
                Scenery(replaceId, door.location.transform(offsetX, offsetY, 0), door.type, newRot)
            SceneryBuilder.replace(door, newDoor)
            return
        }

        var obj = door
        var sec = second
        val mod = if (obj.type == 9) -1 else 1

        var firstDir = (obj.rotation + ((mod + 4) % 4)) % 4
        val p = DoorActionHandler.getRotationPoint(obj.rotation) ?: Point(0, 0)
        var firstLoc = obj.location.transform(p.x * mod, p.y * mod, 0)

        val offsetDir =
            Direction.getDirection(sec.location.x - obj.location.x, sec.location.y - obj.location.y)
        var secondDir = (sec.rotation + mod) % 4

        if (firstDir == 1 && offsetDir == Direction.NORTH) {
            firstDir = 3
        } else if (firstDir == 2 && offsetDir == Direction.EAST) {
            firstDir = 0
        } else if (firstDir == 3 && offsetDir == Direction.SOUTH) {
            firstDir = 1
        } else if (firstDir == 0 && offsetDir == Direction.WEST) {
            firstDir = 2
        }

        if (firstDir == secondDir) {
            secondDir = (secondDir + 2) % 4
        }

        val secondLoc = sec.location.transform(p.x, p.y, 0)

        SceneryBuilder.replace(obj, obj.transform(replaceId, firstDir, firstLoc))
        SceneryBuilder.replace(sec, sec.transform(secondReplaceId, secondDir, secondLoc))
    }

    private fun closeDoor(door: Scenery, second: Scenery?, replaceId: Int, secondReplaceId: Int) {
        if (second == null) {
            val (offsetX, offsetY, newRot) = getCloseOffset(door)
            val newDoor =
                Scenery(replaceId - 2, door.location.transform(offsetX, offsetY, 0), door.type, newRot)
            SceneryBuilder.replace(door, newDoor)
            return
        }

        var obj = door
        var sec = second
        val mod = if (obj.type == 9) -1 else 1

        var firstDir = (obj.rotation + ((mod + 4) % 4)) % 4
        val p = DoorActionHandler.getRotationPoint(obj.rotation) ?: Point(0, 0)
        var firstLoc = obj.location.transform(p.x * mod, p.y * mod, 0)

        val offsetDir =
            Direction.getDirection(sec.location.x - obj.location.x, sec.location.y - obj.location.y)
        var secondDir = (sec.rotation + mod) % 4

        if (firstDir == 1 && offsetDir == Direction.NORTH) {
            firstDir = 3
        } else if (firstDir == 2 && offsetDir == Direction.EAST) {
            firstDir = 0
        } else if (firstDir == 3 && offsetDir == Direction.SOUTH) {
            firstDir = 1
        } else if (firstDir == 0 && offsetDir == Direction.WEST) {
            firstDir = 2
        }

        if (firstDir == secondDir) {
            secondDir = (secondDir + 2) % 4
        }

        val secondLoc = sec.location.transform(p.x, p.y, 0)

        SceneryBuilder.replace(obj, obj.transform(replaceId - 2, firstDir, firstLoc))
        SceneryBuilder.replace(sec, sec.transform(secondReplaceId - 2, secondDir, secondLoc))
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

    private fun getSecondDoor(door: Scenery): Scenery? {
        val possibleIds = setOf(door.id, door.id + 1, door.id - 1)

        val directions =
            listOf(0 to 0, 1 to 0, -1 to 0, 0 to 1, 0 to -1, 1 to 1, -1 to 1, 1 to -1, -1 to -1)

        for ((dx, dy) in directions) {
            val loc = door.location.transform(dx, dy, 0)

            for (slot in 0 until 4) {
                val obj =
                    try {
                        RegionManager.getObject(loc.z, loc.x, loc.y, slot)
                    } catch (e: Exception) {
                        null
                    }

                if (obj != null && obj.id in possibleIds && obj != door) {
                    return obj
                }
            }
        }

        return null
    }

    companion object {
        private val REPLACEMENT_MAP =
            mapOf(
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
