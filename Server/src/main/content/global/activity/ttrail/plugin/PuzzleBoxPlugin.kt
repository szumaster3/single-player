package content.global.activity.ttrail.plugin

import content.global.activity.ttrail.clue.PuzzleBox
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.net.packet.PacketRepository
import core.net.packet.context.ContainerContext
import core.net.packet.out.ContainerPacket
import shared.consts.Components
import shared.consts.Scenery
import kotlin.math.absoluteValue

/**
 * Handles puzzle box interactions and interfaces.
 */
class PuzzleBoxPlugin : InteractionListener, InterfaceListener {
    /**
     * Tracks active puzzle sessions.
     */
    private val sessionState = mutableMapOf<Player, Pair<PuzzleBox, MutableList<Int>>>()

    override fun defineListeners() {

        /*
         * Handles interaction with puzzle boxes.
         */

        PuzzleBox.values().forEach { puzzle ->
            on(puzzle.id, IntType.ITEM, "open", "view") { player, _ ->
                openPuzzle(player, puzzle)
                return@on true
            }
        }

        /*
         * Handles hint for monkey madness puzzles.
         */

        on(Scenery.REINITIALISATION_PANEL_4871, IntType.SCENERY, "operate") { player, _ ->
            val puzzle = PuzzleBox.GLIDER
            if (getAttribute(player, "glider:puzzle:done", false)) {
                sendMessage(player, "You have already solved the puzzle.")
                return@on true
            }
            openPuzzle(player, puzzle)
            return@on true
        }
    }

    override fun defineInterfaceListeners() {
        on(Components.TRAIL_PUZZLE_363) { player, _, _, buttonID, slot, _ ->
            val (puzzleEnum, puzzle) = sessionState[player] ?: return@on true
            val solution = puzzleEnum.fullSolution

            when (buttonID) {
                6 -> if (clickTile(puzzle, slot)) {
                    sessionState[player] = puzzleEnum to puzzle
                    sendPuzzle(player, puzzleEnum)
                    if (puzzle == solution) sendMessage(player, "Congratulations! You've solved the puzzle!")
                }
                0 -> sendPuzzle(player, puzzleEnum, solution.toMutableList())
            }
            true
        }
    }

    /**
     * Opens a new puzzle session.
     */
    private fun openPuzzle(player: Player, puzzleEnum: PuzzleBox) {
        val puzzle = generatePuzzle(puzzleEnum.fullSolution)
        sessionState[player] = puzzleEnum to puzzle
        openPuzzleInterface(player)
        sendPuzzle(player, puzzleEnum)
    }

    /**
     * Opens the puzzle interface.
     */
    private fun openPuzzleInterface(player: Player) {
        val settings = IfaceSettingsBuilder().enableAllOptions().build()
        sendIfaceSettings(player, settings, 6, Components.TRAIL_PUZZLE_363, 0, 25)
        openInterface(player, Components.TRAIL_PUZZLE_363)
    }

    /**
     * Handles tile clicks.
     */
    private fun clickTile(puzzle: MutableList<Int>, slot: Int): Boolean {
        val emptyIndex = puzzle.indexOf(-1)
        val rowDiff = slot / 5 - emptyIndex / 5
        val colDiff = slot % 5 - emptyIndex % 5
        if ((rowDiff.absoluteValue + colDiff.absoluteValue) == 1) {
            puzzle[emptyIndex] = puzzle[slot]
            puzzle[slot] = -1
            return true
        }
        return false
    }

    /**
     * Sends the current puzzle state to the player interface.
     */
    private fun sendPuzzle(player: Player, puzzleEnum: PuzzleBox, data: List<Int>? = null) {
        val puzzle = data ?: sessionState[player]?.second ?: return
        PacketRepository.send(
            ContainerPacket::class.java,
            ContainerContext(player, -1, -1, 140, puzzle.map { if (it != -1) Item(it) else null }.toTypedArray(), 25, false)
        )
    }

    /**
     * Generate a puzzle based on the solution.
     */
    fun generatePuzzle(solution: List<Int>): MutableList<Int> {
        val puzzle = solution.filter { it != -1 }.toMutableList().apply { add(-1) }
        var emptyIndex = puzzle.lastIndex

        repeat(200) {
            val emptyRow = emptyIndex / 5
            val emptyCol = emptyIndex % 5
            var moved = false
            while (!moved) {
                val move = (0..3).random()
                val target = when (move) {
                    0 -> emptyIndex - 5
                    1 -> emptyIndex + 5
                    2 -> emptyIndex - 1
                    else -> emptyIndex + 1
                }
                val targetRow = target / 5
                val targetCol = target % 5
                if (target in 0..24 && (targetRow == emptyRow || targetCol == emptyCol)) {
                    puzzle[emptyIndex] = puzzle[target]
                    puzzle[target] = -1
                    emptyIndex = target
                    moved = true
                }
            }
        }

        val idx4 = puzzle.indexOf(solution[3])
        val idx5 = puzzle.indexOf(solution[4])
        if (idx4 / 5 == idx5 / 5 + 1 && idx4 % 5 == idx5 % 5) {
            puzzle[idx4] = puzzle[idx5]
            puzzle[idx5] = solution[4]
        }

        if (puzzle.last() != -1) {
            val emptyIdx = puzzle.indexOf(-1)
            puzzle[emptyIdx] = puzzle.last().also { puzzle[puzzle.lastIndex] = -1 }
        }

        return puzzle
    }

    /**
     * Saves the current puzzle session.
     */
    fun saveSession(player: Player, puzzleEnum: PuzzleBox, puzzle: MutableList<Int>) {
        sessionState[player] = puzzleEnum to puzzle
    }

    /**
     * Loads the saved puzzle session.
     */
    fun loadSession(player: Player, puzzleEnum: PuzzleBox): MutableList<Int>? {
        val (type, puzzle) = sessionState[player] ?: return null
        return if (type == puzzleEnum) puzzle else null
    }
}
