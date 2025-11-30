package content.region.asgarnia.falador.quest.rd.plugin

import content.region.asgarnia.falador.quest.rd.RecruitmentDrive
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.Topic
import core.game.interaction.QueueStrength
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.tools.END_DIALOGUE
import shared.consts.Animations
import shared.consts.Items
import shared.consts.Scenery
import shared.consts.Sounds

object RDUtils {
    const val VARBIT_FOX_EAST = 680
    const val VARBIT_FOX_WEST = 681
    const val VARBIT_CHICKEN_EAST = 682
    const val VARBIT_CHICKEN_WEST = 683
    const val VARBIT_GRAIN_EAST = 684
    const val VARBIT_GRAIN_WEST = 685

    fun getLocationForScenery(node: Node): Location =
        when (node.asScenery().id) {
            Scenery.CRATE_7347 -> Location(2476, 4943)
            Scenery.CRATE_7348 -> Location(2476, 4937)
            Scenery.CRATE_7349 -> Location(2475, 4943)
            else -> Location(0, 0)
        }

    fun resetPlayerState(player: Player) {
        setMinimapState(player, 0)
        listOf(VARBIT_FOX_EAST, VARBIT_FOX_WEST, VARBIT_CHICKEN_EAST, VARBIT_CHICKEN_WEST, VARBIT_GRAIN_EAST, VARBIT_GRAIN_WEST).forEach { setVarbit(player, it, 0) }
        listOf(Items.GRAIN_5607, Items.FOX_5608, Items.CHICKEN_5609).forEach { removeItem(player, it, Container.EQUIPMENT) }
        player.inventory.clear()
        player.equipment.clear()
        player.interfaceManager.openDefaultTabs()
        removeAttributes(player, RecruitmentDrive.stagePass, RecruitmentDrive.stageFail, RecruitmentDrive.stage, RecruitmentDrive.stage0, RecruitmentDrive.stage1, RecruitmentDrive.stage2, RecruitmentDrive.stage3, RecruitmentDrive.stage4)
        runTask(player, 3) { teleport(player, Location(2996, 3375)) }
    }

    fun processItemUsage(player: Player, used: Item, with: Item, newItem: Item, ) {
        replaceSlot(player, slot = used.index, Item(newItem.id))
        replaceSlot(player, slot = with.index, Item(Items.VIAL_229))
        animate(player, Animation(Animations.HUMAN_USE_PESTLE_AND_MORTAR_364))
        playAudio(player, Sounds.POUR_STICKY_LIQUID_2216)
        sendMessage(player, "You empty the vial into the tin.")
    }

    fun handleVialUsage(player: Player, used: Item) {
        lock(player, 5)
        lockInteractions(player, 5)

        if (removeItem(player, used.id)) {
            animate(player, Animation(Animations.POUR_VIAL_2259))
            playAudio(player, Sounds.POUR_STICKY_LIQUID_2216)

            val doorVial = RecruitmentDrivePlugin.Companion.DoorVials.doorVialsMap[used.id]
            if (doorVial != null) {
                setAttribute(player, doorVial.attribute, true)
                sendMessage(player, "You pour the vial onto the flat part of the spade.")
                addItem(player, Items.VIAL_229)
            } else {
                sendMessage(player, "The vial has no effect.")
            }
        } else {
            sendMessage(player, "You do not have the vial to use.")
        }

        if (RecruitmentDrivePlugin.Companion.DoorVials.doorVialsRequiredMap.all {
                getAttribute(player, it.value.attribute, false)
            }
        ) {
            animate(player, Animation(2259))
            playAudio(player, Sounds.POUR_STICKY_LIQUID_2216)
            sendMessage(player, "Something caused a reaction when mixed!")
            sendMessage(player, "The spade gets hotter, and expands slightly.")
            setVarbit(player, RecruitmentDrivePlugin.doorVarbit, 2)
        }
    }

    fun handleSpadePull(player: Player) {
        lock(player, 3)
        lockInteractions(player, 3)

        if (RecruitmentDrivePlugin.Companion.DoorVials.doorVialsRequiredMap.all
                {
                    getAttribute(player, it.value.attribute, false)
                }
        ) {
            sendMessage(player, "You pull on the spade...")
            sendMessage(player, "It works as a handle, and you swing the stone door open.")
            setVarbit(player, RecruitmentDrivePlugin.doorVarbit, 3)
        } else {
            sendMessage(player, "You pull on the spade...")
            sendMessage(player, "It comes loose, and slides out of the hole in the stone.")
            addItemOrDrop(player, Items.METAL_SPADE_5587)
            setVarbit(player, RecruitmentDrivePlugin.doorVarbit, 0)
        }
    }

    fun handleDoorWalkThrough(player: Player) {
        when {
            inBorders(player, 2476, 4941, 2477, 4939) ->
                forceMove(player, player.location, Location(2478, 4940, 0), 20, 80)

            inBorders(player, 2477, 4941, 2478, 4939) ->
                forceMove(player, player.location, Location(2476, 4940, 0), 20, 80)
        }
    }

    fun searchingHelper(player: Player, attributeCheck: String, item: Int, searchingDescription: String, objectDescription: String) {
        sendMessage(player, searchingDescription)
        queueScript(player, 1, QueueStrength.WEAK) {
            if (attributeCheck.isNotEmpty() && !getAttribute(player, attributeCheck, false)) {
                setAttribute(player, attributeCheck, true)
                addItem(player, item)
                sendMessage(player, objectDescription)
            } else {
                sendMessage(player, "You don't find anything interesting.")
            }
            return@queueScript stopExecuting(player)
        }
    }

    fun processItemUsageAndReturn(player: Player, used: Item, with: Item, resultItem: Item) {
        processItemUsage(player, used, with, resultItem)
    }

    fun shuffleTask(player: Player) {
        val stageArray = intArrayOf(0, 1, 2, 3, 4, 5, 6)
        stageArray.shuffle()
        setAttribute(player, RecruitmentDrive.stage0, stageArray[0])
        setAttribute(player, RecruitmentDrive.stage1, stageArray[1])
        setAttribute(player, RecruitmentDrive.stage2, stageArray[2])
        setAttribute(player, RecruitmentDrive.stage3, stageArray[3])
        setAttribute(player, RecruitmentDrive.stage4, stageArray[4])
        setAttribute(player, RecruitmentDrive.stagePass, false)
        setAttribute(player, RecruitmentDrive.stageFail, false)
        setAttribute(player, RecruitmentDrive.stage, 0)
    }

    class ShelfHelper(private val flaskIdsArray: IntArray, private val specialAttribute: String? = null) : DialogueFile() {
        override fun handle(componentID: Int, buttonID: Int) {
            val player = player ?: return
            when (stage) {
                0 -> {
                    val count = flaskIdsArray.size
                    val message = when (count) {
                        3 -> "There are three vials on this shelf."
                        2 -> "There are two vials on this shelf."
                        1 -> "There is a vial on this shelf."
                        else -> {
                            npc("There is nothing of interest on these shelves.")
                            stage = END_DIALOGUE
                            return
                        }
                    }
                    npc(message)
                    stage = count * 10
                }

                in 10..39 -> {
                    val options = flaskIdsArray.mapIndexed { index, id ->
                        Topic("Take vial ${index + 1}", 100 + index)
                    } + Topic("Take all", 200) + Topic("Don't take any", END_DIALOGUE)

                    showTopics(*options.toTypedArray())
                    when (buttonID) {
                        in 100 until 100 + flaskIdsArray.size -> giveVials(player, listOf(flaskIdsArray[buttonID - 100]))
                        200 -> giveVials(player, flaskIdsArray.toList())
                        END_DIALOGUE -> stage = END_DIALOGUE
                    }
                }
            }
        }

        private fun giveVials(player: Player, vials: List<Int>) {
            vials.forEach { vialId ->
                addItemOrDrop(player, vialId)
                specialAttribute?.let {
                    val remaining = getAttribute(player, it, vials.size)
                    setAttribute(player, it, remaining - 1)
                } ?: run {
                    val attribute = RecruitmentDrivePlugin.Companion.Vials.vialMap[vialId]?.attribute ?: return@run
                    setAttribute(player, attribute, true)
                }
            }
            stage = END_DIALOGUE
        }
    }
}
