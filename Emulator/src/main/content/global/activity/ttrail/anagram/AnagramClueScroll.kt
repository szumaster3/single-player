package content.global.activity.ttrail.anagram

import content.global.activity.ttrail.ClueScrollPlugin
import content.global.activity.ttrail.TreasureTrailManager
import content.global.activity.ttrail.puzzle.PuzzleBox
import core.api.addDialogueAction
import core.api.sendItemDialogue
import core.api.sendNPCDialogue
import core.api.sendString
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Plugin
import org.rs.consts.Items

class AnagramClueScroll(
    private val clue: AnagramClue, clueId: Int = clue.ordinal
) : ClueScrollPlugin(
    clue.name, clueId, clue.level, 345, null
) {

    override fun newInstance(arg: Any?): Plugin<Any> = AnagramClueScroll(clue, clueId)

    override fun read(player: Player) {
        for (i in 1..8) {
            sendString(player, "", interfaceId, i)
        }
        sendString(
            player, "<br><br>This anagram reveals<br>who to speak to next:<br><br><br>${
                clue.anagram.replace(
                    "<br>", "<br><br>"
                )
            }", interfaceId, 1
        )
    }

    fun handleInteraction(player: Player, npc: NPC) {
        val clue = AnagramClue.values().find { it.npcId == npc.id } ?: return

        val hasClue = player.inventory.contains(clue.clueId, 1)
        val puzzle = clue.challenge?.let { PuzzleBox.fromKey(it.toString()) }

        if (puzzle != null && hasClue) {
            val hasPuzzle = player.inventory.contains(puzzle.item.id, 1)
            val puzzleComplete = PuzzleBox.hasCompletePuzzleBox(player, puzzle.key)

            if (!puzzleComplete) {
                if (!hasPuzzle) {
                    val messages = listOf(
                        "Oh, I have a puzzle for you to solve.",
                        "Oh, I've been expecting you.",
                        "The solving of this puzzle could be the key to your treasure."
                    )
                    sendNPCDialogue(player, clue.npcId, messages.random())
                    player.inventory.add(puzzle.item)
                    addDialogueAction(player) { _, button ->
                        if (button > 0) {
                            sendItemDialogue(player, puzzle.item, "${npc.name} has given you a puzzle box!")
                        }
                    }
                }
                return
            }

            if (player.inventory.remove(Item(puzzle.item.id))) {
                sendNPCDialogue(player, clue.npcId, "Well done, traveller.")
                val manager = TreasureTrailManager.getInstance(player)

                val clueScrollPlugin = ClueScrollPlugin.getClueScrolls()[clue.clueId]

                if (clueScrollPlugin != null) {
                    clueScrollPlugin.reward(player)

                    if (manager.isCompleted) {
                        sendItemDialogue(player, Items.CASKET_405, "You've found a casket!")
                        manager.clearTrail()

                        val newClue = ClueScrollPlugin.getClue(clueScrollPlugin.level)
                        if (newClue != null) {
                            player.inventory.add(newClue)
                            sendItemDialogue(player, newClue, "You receive another clue scroll.")
                        }
                    } else {
                        val newClue = ClueScrollPlugin.getClue(clueScrollPlugin.level)
                        if (newClue != null) {
                            player.inventory.add(newClue)
                            sendItemDialogue(player, newClue, "You receive another clue scroll.")
                        }
                    }
                }
            }
        }
    }

    fun getClue(): AnagramClue = clue
}
