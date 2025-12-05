package content.region.other.entrana.quest.zep.plugin

import content.region.other.entrana.quest.zep.cutscene.AirBalloonCutscene
import core.api.*
import core.game.component.Component
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.GameWorld
import core.game.world.map.Location
import shared.consts.*

// First:2x sandbag, 1x log, 5x relax, 1x red rope, 1x log, 2xrelax, 1x red rope, 2x relax, 2x logs, 3x relax.
// Second:1x log, 2x relax, 1x red rope, 1x rope, 7x relax, 1x sandbag, 2x relax, 1x red rope, 1x log, 1x rope, 1x relax.
// Third:1x log, 2x relax, 1x rope, 3x relax, 1x log, 1x red rope, 4x relax , 1x sandbag, 1x red rope, 2x relax , 1x rope.

class EnlightenedJourneyPlugin : InteractionListener, InterfaceListener {

    override fun defineListeners() {
        onUseWith(IntType.SCENERY, Items.WILLOW_BRANCH_5933, Scenery.BASKET_19132) { player, _, _ ->
            if (getQuestStage(player, Quests.ENLIGHTENED_JOURNEY) >= 7) {
                if (!removeItem(player, Item(Items.WILLOW_BRANCH_5933, 12))) {
                    sendMessage(player, "You do not have enough willow branches.")
                } else {
                    lock(player, 300)
                    sendNPCDialogue(
                        player,
                        NPCs.AUGUSTE_5049,
                        "Great! Let me just put it together and we'll be ready to lift off! Speak to me again in a moment."
                    )
                    setQuestStage(player, Quests.ENLIGHTENED_JOURNEY, 8)
                    runTask(player, 3) {
                        AirBalloonCutscene(player)
                            .start(true)
                    }
                }
            }
            return@onUseWith true
        }
    }

    override fun defineInterfaceListeners() {
        on(Components.ZEP_INTERFACE_SIDE_471) { player: Player, component: Component, opcode: Int, buttonID: Int, slot: Int, itemID: Int ->
            val progress = "zep_sequence_progress"
            val index = getAttribute(player, progress, 0)
            setVarbit(player, 2880, 8) // s
            setVarbit(player, 2881, 10) // l
            if (buttonID == requiredSequence[index]) {
                setAttribute(player, progress, index + 1)
                // player.debug("[$buttonID]")
                if (index + 1 >= requiredSequence.size) {
                    removeAttribute(player, progress)
                    teleport(player, Location(2940, 3420, 0))
                    openDialogue(player, object : DialogueFile() {
                        override fun handle(componentID: Int, buttonID: Int) {
                            npc = core.game.node.entity.npc.NPC(NPCs.AUGUSTE_5049)
                            when (stage) {
                                0 -> playerl(FaceAnim.FRIENDLY, "So what are you going to do now?").also { stage++ }
                                1 -> npcl(FaceAnim.FRIENDLY, "I am considering starting a balloon enterprise. People all over ${GameWorld.settings?.name} will be able to travel in a new, exciting way.").also { stage++ }
                                2 -> npcl(FaceAnim.FRIENDLY, "As my first assistant, you will always be welcome to use a balloon. You'll have to bring your own fuel, though.").also { stage++ }
                                3 -> playerl(FaceAnim.FRIENDLY, "Thanks!").also { stage++ }
                                4 -> npcl(FaceAnim.FRIENDLY, "I will base my operations in Entrana. If you'd like to travel to new places, come see me there.").also { stage++ }
                                5 -> {
                                    end()
                                    finishQuest(player, Quests.ENLIGHTENED_JOURNEY)
                                }
                            }
                        }
                    })
                }
            } else {
                unlock(player)
                closeOverlay(player)
                closeSingleTab(player)
                removeAttribute(player, progress)
            }

            return@on true
        }
    }

    companion object {
        private val requiredSequence = listOf(4, 4, 9, 5, 5, 5, 5, 5, 10, 9, 5, 5, 10, 5, 5, 9, 9, 5, 5, 5)
    }
}
