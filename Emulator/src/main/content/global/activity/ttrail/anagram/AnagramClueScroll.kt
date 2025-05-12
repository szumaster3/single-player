package content.global.activity.ttrail.anagram

import content.global.activity.ttrail.ClueScrollPlugin
import content.global.activity.ttrail.TreasureTrailManager
import content.global.activity.ttrail.puzzle.PuzzleBox
import core.api.sendItemDialogue
import core.api.sendString
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Plugin

class AnagramClueScroll(
    private val clue: AnagramClue,
    clueId: Int = clue.ordinal
) : ClueScrollPlugin(
    clue.name,
    clueId,
    clue.level,
    345,
    null
) {

    override fun newInstance(arg: Any?): Plugin<Any> = AnagramClueScroll(clue, clueId)

    override fun read(player: Player) {
        for (i in 1..8) {
            sendString(player, "", interfaceId, i)
        }
        sendString(player,
            "<br><br>This anagram reveals<br>who to speak to next:<br><br><br>${clue.anagram.replace("<br>", "<br><br>")}",
            interfaceId,
            1
        )
    }

    fun handleInteraction(player: Player, npc: NPC) {
        val clue = AnagramClue.values().find { it.npcId == npc.id }
        if (clue == null) return

        val hasClue = player.inventory.contains(clue.clueId, 1)
        val puzzle = clue.challenge?.let { PuzzleBox.fromKey(it.toString()) }

        if (puzzle != null && hasClue) {
            if (!PuzzleBox.hasCompletePuzzleBox(player, puzzle.key)) {
                if (!player.inventory.contains(puzzle.item.id, 1)) {
                    player.inventory.add(puzzle.item)
                    sendItemDialogue(player, puzzle.item, "${clue.npcId}, has given you a puzzle box!")
                }
                return
            }

            if (player.inventory.remove(Item(puzzle.item.id))) {
                npc.sendChat("Well done, traveller.")
                val manager = TreasureTrailManager.getInstance(player)
                if (manager.clueId != clue.clueId) {
                    manager.startTrail(this)
                }
                manager.incrementStage()
            }
        }
    }

    fun getClue(): AnagramClue = clue
}
