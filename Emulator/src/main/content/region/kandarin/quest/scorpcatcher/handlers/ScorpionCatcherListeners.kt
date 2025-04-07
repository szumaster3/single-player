package content.region.kandarin.quest.scorpcatcher.handlers

import content.region.kandarin.quest.scorpcatcher.ScorpionCatcher
import content.region.kandarin.quest.scorpcatcher.dialogue.SeersDialogueFile
import content.region.kandarin.quest.scorpcatcher.dialogue.ThormacDialogueFile
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.system.config.NPCConfigParser
import core.game.system.task.Pulse
import core.game.world.GameWorld
import org.rs.consts.Items
import org.rs.consts.NPCs

class ScorpionCatcherListeners : InteractionListener {
    override fun defineListeners() {
        val haveAlready = "You already have this scorpion in this cage."
        val catchMessage = "You catch a scorpion!"

        val scorpionCages = mapOf(
            Items.SCORPION_CAGE_456 to Pair(Items.SCORPION_CAGE_457, NPCs.KHARID_SCORPION_386),
            Items.SCORPION_CAGE_459 to Pair(Items.SCORPION_CAGE_458, NPCs.KHARID_SCORPION_386),
            Items.SCORPION_CAGE_461 to Pair(Items.SCORPION_CAGE_462, NPCs.KHARID_SCORPION_386),
            Items.SCORPION_CAGE_460 to Pair(Items.SCORPION_CAGE_463, NPCs.KHARID_SCORPION_386),
            Items.SCORPION_CAGE_456 to Pair(Items.SCORPION_CAGE_459, NPCs.KHARID_SCORPION_385),
            Items.SCORPION_CAGE_457 to Pair(Items.SCORPION_CAGE_458, NPCs.KHARID_SCORPION_385),
            Items.SCORPION_CAGE_461 to Pair(Items.SCORPION_CAGE_460, NPCs.KHARID_SCORPION_385),
            Items.SCORPION_CAGE_462 to Pair(Items.SCORPION_CAGE_463, NPCs.KHARID_SCORPION_385),
            Items.SCORPION_CAGE_456 to Pair(Items.SCORPION_CAGE_461, NPCs.KHARID_SCORPION_387),
            Items.SCORPION_CAGE_457 to Pair(Items.SCORPION_CAGE_462, NPCs.KHARID_SCORPION_387),
            Items.SCORPION_CAGE_459 to Pair(Items.SCORPION_CAGE_460, NPCs.KHARID_SCORPION_387),
            Items.SCORPION_CAGE_458 to Pair(Items.SCORPION_CAGE_463, NPCs.KHARID_SCORPION_387)
        )

        val alreadyHaveMessage = setOf(
            Items.SCORPION_CAGE_457,
            Items.SCORPION_CAGE_458,
            Items.SCORPION_CAGE_462,
            Items.SCORPION_CAGE_463,
            Items.SCORPION_CAGE_459,
            Items.SCORPION_CAGE_460,
            Items.SCORPION_CAGE_461
        )

        for ((cage, newCageAndNpc) in scorpionCages) {
            val (newCage, npc) = newCageAndNpc
            onUseWith(IntType.NPC, cage, npc) { player, used, with ->
                if (alreadyHaveMessage.contains(cage)) {
                    player.sendMessage(haveAlready)
                } else {
                    handleScorpionCatch(player, used.asItem(), newCage, with, catchMessage, npc)
                }
                return@onUseWith true
            }
        }
    }

    private fun handleScorpionCatch(
        player: Player, used: Item, newCage: Int, with: Node, catchMessage: String, npc: Int
    ) {
        if (!removeItem(player, Item(used.id, 1))) return
        addItem(player, newCage)
        sendMessage(player, catchMessage)

        val attribute = when (npc) {
            NPCs.KHARID_SCORPION_386 -> ScorpionCatcher.ATTRIBUTE_TAVERLEY
            NPCs.KHARID_SCORPION_385 -> ScorpionCatcher.ATTRIBUTE_BARBARIAN
            NPCs.KHARID_SCORPION_387 -> ScorpionCatcher.ATTRIBUTE_MONK
            else -> ""
        }

        if (attribute.isNotEmpty() && !getAttribute(player, attribute, false)) {
            setAttribute(player, attribute, true)
        }

        runTask(player, 2) {
            with.asNpc().respawnTick =
                GameWorld.ticks + with.asNpc().definition.getConfiguration(NPCConfigParser.RESPAWN_DELAY, 34)
        }

    }

    companion object {
        fun getScorpionLocation(player: Player) {
            submitIndividualPulse(
                player,
                object : Pulse() {
                    var counter = 0

                    override fun pulse(): Boolean {
                        when (counter++) {
                            1 -> sendMessage(player, "The seer produces a small mirror.")
                            3 -> sendMessage(player, "The seer gazes into the mirror.")
                            6 -> sendMessage(player, "The seer smoothes his hair with his hand.")
                            9 -> {
                                setAttribute(player, ScorpionCatcher.ATTRIBUTE_MIRROR, true)
                                setAttribute(player, ScorpionCatcher.ATTRIBUTE_SECRET, true)
                                openDialogue(player, SeersDialogueFile())
                                return true
                            }
                        }
                        return false
                    }
                },
            )
        }

        fun getCage(player: Player) {
            submitIndividualPulse(
                player,
                object : Pulse() {
                    var counter = 0

                    override fun pulse(): Boolean {
                        when (counter++) {
                            0 -> {
                                sendMessage(player, "Thormac gives you a cage.")
                                addItemOrDrop(player, Items.SCORPION_CAGE_456)
                            }

                            2 -> {
                                setAttribute(player, ScorpionCatcher.ATTRIBUTE_CAGE, true)
                                openDialogue(player, ThormacDialogueFile())
                                return true
                            }
                        }
                        return false
                    }
                },
            )
        }
    }
}
