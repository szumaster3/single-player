package content.global.activity.ttrail.anagram

import content.global.activity.ttrail.puzzle.PuzzleBox
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.npc.NPC

class AnagramListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles iterate over all Anagram clues to set listeners for NPCs.
         */

        AnagramClue.values().forEach { clue ->
            on(clue.npcId, IntType.NPC, "talk-to") { player, node ->
                val puzzle = clue.challenge?.let { PuzzleBox.fromKey(it.toString()) }
                val hasClue = player.inventory.contains(clue.clueId, 1)
                val puzzleComplete = puzzle?.let { PuzzleBox.hasCompletePuzzleBox(player, it.key) } ?: false

                if (hasClue || puzzleComplete) {
                    AnagramClueScroll(clue).handleInteraction(player, node as NPC)
                }

                return@on true
            }
        }
    }
}
