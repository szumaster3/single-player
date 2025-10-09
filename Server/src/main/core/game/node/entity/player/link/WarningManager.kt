package core.game.node.entity.player.link

import content.global.plugin.iface.FairyRing
import content.region.kandarin.yanille.quest.itwatchtower.cutscene.EnclaveCutscene
import core.api.*
import core.game.component.Component
import core.game.global.action.DoorActionHandler
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.tools.DARK_PURPLE
import shared.consts.*
import core.game.dialogue.FaceAnim
import core.game.event.FairyRingDialEvent
import core.game.global.action.ClimbActionHandler.climb
import core.game.interaction.IntType
import core.game.interaction.QueueStrength
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.item.Item
import core.game.world.GameWorld

class WarningManager : InteractionListener, InterfaceListener {

    override fun defineInterfaceListeners() {
        onOpen(Components.CWS_WARNING_24_581) { player, component ->
            increment(player, component.id)
            return@onOpen true
        }

        Warnings.values.forEach { warning ->
            if (warning.component != -1) {
                on(warning.component) { player, _, _, buttonId, _, _ ->
                    handleInterfaceAction(player, warning, buttonId)
                    return@on true
                }
            }
        }
    }

    private fun handleInterfaceAction(player: Player, warning: Warnings, buttonId: Int) {
        closeOverlay(player)
        closeInterface(player)
        when (buttonId) {
            17 -> handleConfirm(player, warning)
            18 -> handle(player, warning.component)
            20, 28 -> toggle(player, warning.component)
            else -> return
        }
    }

    private fun handleConfirm(player: Player, warning: Warnings) {
        warning.action(player)
        increment(player, warning.component)
    }

    private val cwsWarnings = mapOf(
        Components.CWS_WARNING_24_581 to Vars.VARBIT_CWS_WARNING_24_3872,
        Components.CWS_WARNING_26_627 to Vars.VARBIT_CWS_WARNING_26_4132
    )

    fun handle(player: Player, component: Int) {
        when (component) {
            Components.WILDERNESS_WARNING_382 -> handleWilderness(player)
            else -> handleCws(player, component)
        }
    }

    private fun handleWilderness(player: Player) {
        player.interfaceManager.close()
        player.getAttribute<core.game.node.scenery.Scenery>("wildy_ditch")
            ?.let { handleWildernessWarnings(player) }
        increment(player, Components.WILDERNESS_WARNING_382)
    }

    private fun handleCws(player: Player, component: Int) {
        val varbit = cwsWarnings[component]
        if (varbit != null && getVarbit(player, varbit) >= 6) {
            toggle(player, component)
        } else {
            increment(player, component)
        }
    }

    private fun handleScenery(player: Player, warning: Warnings, node: core.game.node.scenery.Scenery) {
        when (warning) {
            Warnings.MORT_MYRE -> player.setAttribute("myre_gate", node)
            Warnings.WILDERNESS_DITCH -> player.setAttribute("wildy_gate", node)
            else -> {}
        }
        if (!isDisabled(player, warning)) openWarning(player, warning)
        else warning.action(player)
    }

    override fun defineListeners() {
        sceneryWarnings.forEach { (sceneryID, warning) ->
            on(sceneryID, IntType.SCENERY, "go-through","climb-down","climb","open","climb-up","cross") { player, node ->
                if ((sceneryID == 3506 || sceneryID == 3507) && player.location.y < 3458) {
                    DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
                    return@on true
                }

                handleScenery(player, warning, node.asScenery())
                return@on true
            }
        }
    }

    companion object {
        private val sceneryWarnings = listOf(
            Scenery.PASSAGE_37929 to Warnings.CORPOREAL_BEAST_DANGEROUS,
            Scenery.PASSAGE_38811 to Warnings.CORPOREAL_BEAST_DANGEROUS,
            Scenery.STAIRS_25432 to Warnings.OBSERVATORY_STAIRS,
            Scenery.CLIMBING_ROPE_5946 to Warnings.LUMBRIDGE_SWAMP_CAVE_ROPE,
            Scenery.DARK_HOLE_5947 to Warnings.LUMBRIDGE_SWAMP_CAVE_ROPE,
            Scenery.GATE_3506 to Warnings.MORT_MYRE,
            Scenery.GATE_3507 to Warnings.MORT_MYRE,
            Scenery.TOWER_LADDER_2511 to Warnings.RANGING_GUILD,
            Scenery.WILDERNESS_DITCH_23271 to Warnings.WILDERNESS_DITCH
        )

        private val ladderZones = listOf(
            ZoneBorders(1836, 5174, 1930, 5257) to Location.create(2042, 5245, 0),
            ZoneBorders(1977, 5176, 2066, 5265) to Location.create(2123, 5252, 0),
            ZoneBorders(2090, 5246, 2197, 5336) to Location.create(2358, 5215, 0)
        )

        @JvmStatic
        fun openWarning(player: Player, warning: Warnings) {
            if (isDisabled(player, warning)) return
            player.interfaceManager.open(Component(warning.component))
            increment(player, warning.varbit)
        }

        @JvmStatic
        fun isDisabled(player: Player, warning: Warnings): Boolean {
            return getVarbit(player, warning.varbit) == 7
        }

        fun toggle(player: Player, componentId: Int) {
            val warning = Warnings.values().find { it.component == componentId } ?: return
            toggleWarning(player, warning)
        }

        fun toggleWarning(player: Player, warning: Warnings) {
            val current = getVarbit(player, warning.varbit)
            if (current == 6) {
                setVarbit(player, warning.varbit, 7, true)
                sendMessage(
                    player, "You have toggled this warning screen off. You will no longer see this warning screen."
                )
            } else {
                setVarbit(player, warning.varbit, 6, true)
                sendMessage(player, "You have toggled this warning screen on. You will see this interface again.")
            }
        }

        fun increment(player: Player, varbitId: Int) {
            Warnings.values().find { it.varbit == varbitId }?.let { warning ->
                val currentStatus = getVarbit(player, warning.varbit)
                if (currentStatus < 6) {
                    val newStatus = (currentStatus + 1).coerceAtMost(6)
                    setVarbit(player, warning.varbit, newStatus, true)
                    player.debug("Component varbit: [$DARK_PURPLE$warning</col>] increased to: [$DARK_PURPLE$newStatus</col>].")
                    if (newStatus == 6) {
                        enableToggleButton(player, warning)
                        sendMessage(player, "You can now disable this warning in the settings.")
                    }
                } else if (currentStatus == 6) {
                    enableToggleButton(player, warning)
                }
            }
        }

        private fun enableToggleButton(player: Player, warning: Warnings) {
            val toggleButton = when (warning.component) {
                Components.WILDERNESS_WARNING_382 -> 26
                Components.CWS_WARNING_24_581 -> 19
                else -> 21
            }
            sendInterfaceConfig(player, warning.component, toggleButton, false)
        }

        fun handleWildernessWarnings(player: Player) {
            val ditch = player.getAttribute<core.game.node.scenery.Scenery>("wildy_ditch")
            if (ditch != null) {
                player.removeAttribute("wildy_ditch")

                val (start, end) = getDitchLocations(player.location, ditch.location, ditch.rotation)
                if (player.location.getDistance(ditch.location) < 3) {
                    forceMove(player, start, end, 0, 60, null, Animations.JUMP_OVER_OBSTACLE_6132)
                    playAudio(player, Sounds.JUMP2_2462, 3)
                } else {
                    queueScript(player, 0, QueueStrength.NORMAL) {
                        forceMove(player, start, end, 0, 60, null, Animations.JUMP_OVER_OBSTACLE_6132)
                        playAudio(player, Sounds.JUMP2_2462, 3)
                        return@queueScript stopExecuting(player)
                    }
                }
                return
            }
        }

        private fun getDitchLocations(playerLocation: Location, ditchLocation: Location, rotation: Int): Pair<Location, Location> {
            val (x, y) = playerLocation.x to playerLocation.y
            return if (rotation % 2 == 0) {
                if (y <= ditchLocation.y) Location.create(x, ditchLocation.y - 1, 0) to Location.create(x, ditchLocation.y + 2, 0) else Location.create(x, ditchLocation.y + 2, 0) to Location.create(x, ditchLocation.y - 1, 0)
            } else {
                if (x > ditchLocation.x) Location.create(ditchLocation.x + 2, y, 0) to Location.create(ditchLocation.x - 1, y, 0) else Location.create(ditchLocation.x - 1, y, 0) to Location.create(ditchLocation.x + 2, y, 0)
            }
        }

        fun handleCorporalBeastWarning(player: Player) {
            if (hasRequirement(player, Quests.SUMMERS_END) && player.getAttribute("corp-beast-cave-delay", 0) <= GameWorld.ticks) {
                player.properties.teleportLocation = player.location.transform(4, 0, 0)
                player.setAttribute("corp-beast-cave-delay", GameWorld.ticks + 5)
            }
        }

        fun handleRaningGuildWarning(player: Player) {
            climb(player, core.game.world.update.flag.context.Animation(Animations.USE_LADDER_828), Location.create(2668, 3427, 1))
        }

        fun handleMortMyreGateWarning(player: Player) {
            val gate = player.getAttribute<core.game.node.scenery.Scenery>("myre_gate")
            if (gate != null) {
                player.removeAttribute("myre_gate")
            DoorActionHandler.handleAutowalkDoor(player, gate.asScenery())
            sendMessage(player, "You walk into the gloomy atmosphere of Mort Myre.", 3)
            }
        }

        fun handleFairyRingWarning(player: Player) {
            player.dispatch(FairyRingDialEvent(FairyRing.AJQ))
            teleport(player, FairyRing.AJQ.tile!!, TeleportManager.TeleportType.FAIRY_RING)
            if (!player.savedData.globalData.hasTravelLog(2)) player.savedData.globalData.setTravelLog(2)
        }

        fun handleSwampCaveWarning(player: Player) {
            if (!player.getSavedData().globalData.hasTiedLumbridgeRope()) {
                sendDialogue(player, "There is a sheer drop below the hole. You will need a rope.")
            } else {
                climb(
                    player,
                    core.game.world.update.flag.context.Animation(Animations.MULTI_BEND_OVER_827),
                    Location.create(3168, 9572, 0)
                )
            }
        }

        fun handleShantayPassWarning(player: Player) {
            if (!removeItem(player, Item(Items.SHANTAY_PASS_1854, 1))) {
                sendNPCDialogue(player, NPCs.SHANTAY_GUARD_838, "You need a Shantay pass to get through this gate. See Shantay, he will sell you one for a very reasonable price.", FaceAnim.NEUTRAL)
            } else {
                sendMessage(player, "You go through the gate.")
                forceMove(player, player.location, player.location.transform(0, if (player.location.y > 3116) -2 else 2, 0), 30, 120, null)
            }
        }

        @JvmStatic
        fun handleStrongholdLadderWarning(player: Player) {
            ladderZones.firstOrNull { (zone, _) -> inBorders(player, zone) }?.second?.let {
                climb(player, core.game.world.update.flag.context.Animation(Animations.MULTI_BEND_OVER_827), it)
            }
        }

        @JvmStatic
        fun handleMoleTunnelWarning(player: Player) {
            teleport(player, Location.create(1752, 5237, 0))
            playAudio(player, Sounds.ROOF_COLLAPSE_1384)
            sendMessage(player, "You seem to have dropped down into a network of mole tunnels.")
            if (!hasDiaryTaskComplete(player, DiaryType.FALADOR, 0, 5)) {
                finishDiaryTask(player, DiaryType.FALADOR, 0, 5)
            }
        }

        @JvmStatic
        fun handleWatchTowerWarning(player: Player) {
            if (isQuestComplete(player, Quests.WATCHTOWER) || getQuestStage(player, Quests.WATCHTOWER) >= 60) {
                teleport(player, Location.create(2588, 9410, 0), TeleportManager.TeleportType.INSTANT)
            } else {
                EnclaveCutscene(player).start(true)
            }
            sendMessage(player, "You run past the guard while he's busy.")
        }
    }
}

