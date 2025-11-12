package content.region.desert.quest.deserttreasure.plugin

import content.region.desert.quest.deserttreasure.DTUtils
import content.region.desert.quest.deserttreasure.DesertTreasure
import core.api.*
import core.game.activity.Cutscene
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.world.map.Location
import shared.consts.*

private data class Obelisk(val id: Int, val diamond: Int, val varbit: Int, val attr: String)
private data class MirrorParams(val regionId: Int, val teleportX: Int, val teleportY: Int, val moveCamX: Int, val moveCamY: Int, val moveCamDelay: Int, val rotateCamX: Int, val rotateCamY: Int, val rotateCamDelay: Int)

class DesertTreasurePlugin : InteractionListener {
    var temp = 6517

    override fun defineListeners() {

        listOf(
            Scenery.LADDER_6497 to (Location(2913, 4954, 3) to Location(3233, 2898, 0)),
            Scenery.LADDER_6498 to (Location(2846, 4964, 2) to Location(2909, 4963, 3)),
            Scenery.LADDER_6499 to (Location(2782, 4972, 1) to Location(2845, 4973, 2)),
            Scenery.LADDER_6500 to (Location(3233, 9293, 0) to Location(2783, 4941, 1))
        ).forEach { (id, locs) ->
            val (down, up) = locs
            on(id, SCENERY, "climb-down") { p, _ -> teleport(p, down); true }
            on(id, SCENERY, "climb-up") { p, _ -> teleport(p, up); true }
        }

        mapOf(
            Scenery.MYSTICAL_MIRROR_6423 to MirrorParams(12845, 33, 41, 33, 41, 1000, 33, 39, 990),   // South
            Scenery.MYSTICAL_MIRROR_6425 to MirrorParams(12590, 40, 39, 40, 39, 1200, 10, 39, 1200), // South-West
            Scenery.MYSTICAL_MIRROR_6427 to MirrorParams(10037, 56, 40, 56, 40, 1400, 56, 35, 1000), // North-West
            Scenery.MYSTICAL_MIRROR_6429 to MirrorParams(11322, 5, 27, 5, 27, 1000, 6, 27, 1000),    // North
            Scenery.MYSTICAL_MIRROR_6431 to MirrorParams(13878, 47, 31, 47, 31, 1000, 43, 31, 1000), // North-East
            Scenery.MYSTICAL_MIRROR_6433 to MirrorParams(13102, 36, 20, 36, 20, 1000, 44, 20, 1000)  // South-East
        ).forEach { (id, params) ->
            on(id, SCENERY, "look-into") { p, _ ->
                MirrorLookCutscene(
                    p,
                    params.regionId,
                    params.teleportX, params.teleportY,
                    params.moveCamX, params.moveCamY, params.moveCamDelay,
                    params.rotateCamX, params.rotateCamY, params.rotateCamDelay
                ).start()

                sendMessage(p, "You gaze into the mirror...")
                return@on true
            }
        }

        listOf(
            Obelisk(Scenery.OBELISK_6483, Items.BLOOD_DIAMOND_4670, DesertTreasure.varbitBloodObelisk, DesertTreasure.bloodDiamond),
            Obelisk(Scenery.OBELISK_6486, Items.SMOKE_DIAMOND_4672, DesertTreasure.varbitSmokeObelisk, DesertTreasure.smokeDiamond),
            Obelisk(Scenery.OBELISK_6489, Items.ICE_DIAMOND_4671, DesertTreasure.varbitIceObelisk, DesertTreasure.iceDiamond),
            Obelisk(Scenery.OBELISK_6492, Items.SHADOW_DIAMOND_4673, DesertTreasure.varbitShadowObelisk, DesertTreasure.shadowDiamond)
        ).forEach { obelisk ->
            onUseWith(IntType.SCENERY, obelisk.diamond, obelisk.id) { p, used, _ ->
                if (getDynLevel(p, Skills.MAGIC) < 50) {
                    sendMessages(p,
                        "You are not a powerful enough mage to breach the protective aura.",
                        "You need a Magic level of at least 50 to enter the Pyramid."
                    )
                    return@onUseWith true
                }

                if (removeItem(p, used)) {
                    sendMessage(p, "The diamond is absorbed into the pillar.")
                    setVarbit(p, obelisk.varbit, 1)
                    setAttribute(p, obelisk.attr, 1)

                    if (DTUtils.allDiamondsInserted(p)) {
                        sendMessage(p, "The force preventing access to the Pyramid has now vanished.")
                        if (getQuestStage(p, Quests.DESERT_TREASURE) == 9)
                            setQuestStage(p, Quests.DESERT_TREASURE, 10)
                    }
                }
                return@onUseWith true
            }
        }

        on(intArrayOf(Scenery.PYRAMID_ENTRANCE_6545, Scenery.PYRAMID_ENTRANCE_6547), SCENERY, "open") { player, node ->
            if (DTUtils.allDiamondsInserted(player)) {
                DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
            } else {
                sendMessage(player, "A mystical power has sealed this door...")
            }
            return@on true
        }

        on((6512..6517).toIntArray(), SCENERY, "search") { player, _ ->
            sendMessage(player, "You don't find anything interesting.")
            return@on true
        }

        on(Scenery.TUNNEL_6481, SCENERY, "enter") { player, _ ->
            if (isQuestComplete(player, Quests.DESERT_TREASURE)) {
                animate(player, Animations.CRAWL_844)
                player.teleport(Location(3233, 9313, 0), 1)
            } else {
                sendMessage(player, "This passage does not seem to lead anywhere...")
            }
            return@on true
        }

        on(Scenery.PORTAL_6551, SCENERY, "use") { player, _ ->
            teleport(player, Location(3233, 2887, 0))
            return@on true
        }

        val itemData =
            listOf(
                // Magic Logs
                Triple(
                    intArrayOf(Items.MAGIC_LOGS_1513, Items.MAGIC_LOGS_1514),
                    DesertTreasure.magicLogsAmount,
                    12 to "You hand over a magic log.",
                ),
                // Steel Bars
                Triple(
                    intArrayOf(Items.STEEL_BAR_2353, Items.STEEL_BAR_2354),
                    DesertTreasure.steelBarsAmount,
                    6 to "You hand over a steel bar.",
                ),
                // Molten Glass
                Triple(
                    intArrayOf(Items.MOLTEN_GLASS_1775, Items.MOLTEN_GLASS_1776),
                    DesertTreasure.moltenGlassAmount,
                    6 to "You hand over some molten glass.",
                ),
                // Bones
                Triple(
                    intArrayOf(Items.BONES_526, Items.BONES_527),
                    DesertTreasure.bonesAmount,
                    1 to "Thank you, those are enough bones for the spell.",
                ),
                // Ashes
                Triple(
                    intArrayOf(Items.ASHES_592, Items.ASHES_593),
                    DesertTreasure.ashesAmount,
                    1 to "Thank you, that is enough ash for the spell.",
                ),
                // Charcoal
                Triple(
                    intArrayOf(Items.CHARCOAL_973, Items.CHARCOAL_974),
                    DesertTreasure.charcoalAmount,
                    1 to "Thank you, that is enough charcoal for the spell.",
                ),
                // Blood Runes
                Triple(
                    intArrayOf(Items.BLOOD_RUNE_565),
                    DesertTreasure.bloodRunesAmount,
                    1 to "Thank you, that blood rune should be sufficient for the spell.",
                ),
            )

        itemData.forEach { (items, attribute, config) ->
            val (limit, message) = config
            onUseWith(IntType.NPC, items, NPCs.EBLIS_1923) { player, used, _ ->
                if (inInventory(player, used.id)) {
                    val currentAmount = getAttribute(player, attribute, 0)
                    if (currentAmount < limit) {
                        if (removeItem(player, used.id)) {
                            setAttribute(player, attribute, currentAmount + 1)
                            if (message.contains("Thank you")) {
                                sendNPCDialogue(player, NPCs.EBLIS_1923, message)
                            } else {
                                sendMessage(player, message)
                            }
                        }
                    }
                }
                return@onUseWith true
            }
        }
    }
}

private class MirrorLookCutscene(player: Player, private val regionId: Int, private val teleportX: Int, private val teleportY: Int, private val moveCamX: Int, private val moveCamY: Int, private val moveCamDelay: Int, private val rotateCamX: Int, private val rotateCamY: Int, private val rotateCamDelay: Int) : Cutscene(player) {

    override fun setup() {
        setExit(player.location.transform(0, 0, 0))
        loadRegion(regionId)
    }

    override fun runStage(stage: Int) {
        when (stage) {
            0 -> {
                fadeToBlack()
                timedUpdate(4)
            }
            1 -> {
                teleport(player, teleportX, teleportY)
                moveCamera(moveCamX, moveCamY, moveCamDelay)
                rotateCamera(rotateCamX, rotateCamY, rotateCamDelay)
                timedUpdate(1)
            }
            2 -> {
                openInterface(player, Components.LEGENDS_MIRROR_155)
                closeOverlay(player)
                timedUpdate(6)
            }
            3 -> {
                closeInterface(player)
                fadeToBlack()
                timedUpdate(4)
            }
            4 -> {
                end(false) { fadeFromBlack() }
            }
        }
    }
}