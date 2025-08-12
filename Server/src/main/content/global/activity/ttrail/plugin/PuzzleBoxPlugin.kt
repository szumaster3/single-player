package content.global.activity.ttrail.plugin

import core.api.*
import core.game.component.Component
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.net.packet.OutgoingContext
import core.net.packet.PacketRepository
import core.net.packet.out.ContainerPacket
import shared.consts.Components
import shared.consts.Items
import shared.consts.Scenery
import kotlin.math.abs

/**
 * Plugin handling interactions and interface logic for Treasure Trails puzzle boxes.
 *
 * Supports puzzle management including loading, saving,
 * verifying completion, and generating solvable puzzles.
 *
 * Related to Monkey Madness quest puzzles.
 *
 * @author gabriel_aguido, szu
 */
class PuzzleBoxPlugin : InteractionListener, InterfaceListener {

    /**
     * Temporary session storage for players' puzzle states.
     * Maps a Player to their puzzles' keys and their current tile layouts.
     */
    private val puzzleSessionState = mutableMapOf<Player, MutableMap<String, List<Int>>>()

    /**
     * Defines item and scenery interaction listeners related to puzzle boxes and quest puzzles.
     * Registers handlers for opening puzzles, viewing quest puzzle pieces,
     * and operating puzzle panels.
     */
    override fun defineListeners() {

        /*
         * Handles interaction with puzzle boxes.
         */

        PuzzleBox.values().forEach { box ->
            on(box.item.id, IntType.ITEM, "Open") { player, _ ->
                handlePuzzleOpen(player, box.key, box.solutionTiles + listOf(-1))
                return@on true
            }
        }

        /*
         * Handles quest puzzle (Monkey madness).
         */

        on(Items.SPARE_CONTROLS_4002, IntType.ITEM, "View") { player, _ ->
            val puzzlePieces = ((Items.SLIDING_BUTTON_3904..Items.SLIDING_BUTTON_3950 step 2).map { Item(it) } + Item(-1)).toTypedArray()
            val settings = IfaceSettingsBuilder().build()
            player.packetDispatch.sendIfaceSettings(settings, 6, Components.TRAIL_PUZZLE_363, 0, 25)
            player.interfaceManager.open(Component(Components.TRAIL_PUZZLE_363))
            PacketRepository.send(
                ContainerPacket::class.java,
                OutgoingContext.Container(player, -1, -1, 140, puzzlePieces, 25, false)
            )
            return@on true
        }

        /*
         * Handles interaction with panel (shows solution) puzzle (Monkey madness).
         */

        on(Scenery.REINITIALISATION_PANEL_4871, IntType.SCENERY, "Operate") { player, _ ->
            if (!getAttribute(player, "mm:puzzle:done", false)) {
                val settings = IfaceSettingsBuilder().enableAllOptions().build()
                player.packetDispatch.sendIfaceSettings(settings, 6, Components.TRAIL_PUZZLE_363, 0, 25)
                player.interfaceManager.open(Component(Components.TRAIL_PUZZLE_363))

                val key = "mm"
                val items = (Items.SLIDING_BUTTON_3904..Items.SLIDING_BUTTON_3950 step 2).toList() + listOf(-1)
                if (loadPuzzleState(player, key) == null) {
                    val shuffled = generateSolvablePuzzle(items)
                    savePuzzleStateInSession(player, key, shuffled)
                }

                sendPuzzle(player, key)
            } else {
                sendMessage(player, "You have already solved the puzzle.")
            }
            return@on true
        }
    }

    /**
     * Defines interface listeners for puzzle UI components.
     * Handles puzzle tile moves and showing the solution.
     */
    override fun defineInterfaceListeners() {
        on(Components.TRAIL_PUZZLE_363) { player, _, _, buttonID, slot, _ ->

            val key = (PuzzleBox.values().map { it.key } + "mm").find {
                loadPuzzleState(player, it) != null
            } ?: return@on true

            val puzzle = loadPuzzleState(player, key)?.toMutableList() ?: return@on true
            val solution = PuzzleBox.fromKey(key)?.fullSolution
                ?: if (key == "mm") (Items.SLIDING_BUTTON_3904..Items.SLIDING_BUTTON_3950 step 2).toList() + listOf(-1)
                else return@on true

            when {
                buttonID == 6 && slot in 0..24 -> {
                    val offsets = listOf(-1, 1, -5, 5)
                    val targetSlot = offsets.map { findTargetSlot(puzzle, slot, it) }.firstOrNull { it != -1 }

                    if (targetSlot != null) {
                        puzzle[targetSlot] = puzzle[slot]
                        puzzle[slot] = -1

                        savePuzzleStateInSession(player, key, puzzle)
                        sendPuzzle(player, key)

                        if (puzzle == solution) {
                            setAttribute(player, "$key:puzzle:done", true)
                            savePuzzleStateInAttributes(player, key, puzzle)
                            player.debug("[$key] Puzzle completed.")

                            PuzzleBox.fromKey(key)?.let { box ->
                                setCharge(box.item, 1100)
                            }
                        }
                    }
                }
                buttonID == 0 -> {
                    val solutionItems = solution.map { Item(it) }.toTypedArray()

                    if (!getAttribute(player, "$key:puzzle:done", false)) {
                        PacketRepository.send(
                            ContainerPacket::class.java,
                            OutgoingContext.Container(player, -1, -1, 140, solutionItems, 25, false),
                        )
                    }
                }
            }

            return@on true
        }
    }

    /**
     * Opens the puzzle interface and initializes puzzle state for the player.
     * If the puzzle is already fully charged, it shows the solution immediately.
     * Otherwise loads existing puzzle state or generates a new solvable puzzle.
     *
     * @param player The player opening the puzzle.
     * @param key Unique key identifying the puzzle.
     * @param items The list of puzzle tile IDs, including the empty slot (-1).
     */
    private fun handlePuzzleOpen(player: Player, key: String, items: List<Int>) {
        val box = PuzzleBox.fromKey(key) ?: return
        val charge = getCharge(box.item) ?: 1000

        val puzzleData = if (charge >= 1100) {
            box.fullSolution
        } else {
            loadPuzzleState(player, key) ?: run {
                val shuffled = generateSolvablePuzzle(items)
                savePuzzleStateInSession(player, key, shuffled)
                shuffled
            }
        }

        val settings = IfaceSettingsBuilder().enableAllOptions().build()
        player.packetDispatch.sendIfaceSettings(settings, 6, Components.TRAIL_PUZZLE_363, 0, 25)
        player.interfaceManager.open(Component(Components.TRAIL_PUZZLE_363))
        sendPuzzle(player, key, puzzleData)
    }

    /**
     * Checks if moving the tile at [slot] by the [offset] results in a valid move.
     * Valid moves are only adjacent empty slots (up, down, left, right) within a 5x5 grid.
     *
     * @param puzzle The current puzzle tile list.
     * @param slot The index of the tile to move.
     * @param offset The offset to check (e.g. -1 for left, 1 for right, -5 up, 5 down).
     * @return The target slot index if valid, or -1 if the move is invalid.
     */
    private fun findTargetSlot(puzzle: List<Int>, slot: Int, offset: Int): Int {
        val target = slot + offset
        if (target !in 0..24) return -1

        val rowDiff = abs(slot / 5 - target / 5)
        return when (offset) {
            1, -1 -> if (rowDiff == 0 && puzzle[target] == -1) target else -1
            5, -5 -> if (rowDiff == 1 && puzzle[target] == -1) target else -1
            else -> -1
        }
    }

    /**
     * Saves the player's current puzzle state in session memory.
     * This is a temporary storage for unsaved puzzle progress.
     *
     * @param player The player whose puzzle state is saved.
     * @param key The unique puzzle key.
     * @param puzzle The current puzzle tile arrangement.
     */
    fun savePuzzleStateInSession(player: Player, key: String, puzzle: List<Int>) {
        puzzleSessionState.getOrPut(player) { mutableMapOf() }[key] = puzzle
    }

    /**
     * Loads the player's current puzzle state from session memory.
     *
     * @param player The player to load puzzle state for.
     * @param key The unique puzzle key.
     * @return The current puzzle tiles list or null if none saved.
     */
    fun loadPuzzleState(player: Player, key: String): List<Int>? = puzzleSessionState[player]?.get(key)

    /**
     * Saves the completed puzzle state persistently in player attributes.
     * Stores both a hashed and raw representation of the puzzle tile layout.
     *
     * @param player The player to save the state for.
     * @param key The unique puzzle key.
     * @param puzzle The completed puzzle tile list.
     */
    private fun savePuzzleStateInAttributes(player: Player, key: String, puzzle: List<Int>) {
        val encoded = puzzle.joinToString(",")
        setAttribute(player, "$key:puzzle", encoded.hashCode())
        setAttribute(player, "$key:puzzle:data", encoded)
    }

    /**
     * Sends the puzzle tile layout to the client's interface container.
     *
     * @param player The player to send the puzzle to.
     * @param key The puzzle key.
     * @param data Optional puzzle tile list to send; if null loads from session.
     */
    private fun sendPuzzle(player: Player, key: String, data: List<Int>? = null) {
        val puzzle = data ?: loadPuzzleState(player, key) ?: return
        val items = puzzle.map { Item(it) }.toTypedArray()
        PacketRepository.send(
            ContainerPacket::class.java,
            OutgoingContext.Container(player, -1, -1, 140, items, 25, false),
        )
    }

    /**
     * Checks if a puzzle tile configuration is solvable.
     * Uses inversion counting method on the flattened puzzle list ignoring the empty slot.
     *
     * @param puzzle The puzzle tile list including -1 for empty slot.
     * @return True if the puzzle is solvable, false otherwise.
     */
    fun isSolvable(puzzle: List<Int>): Boolean {
        val flat = puzzle.filter { it != -1 }
        val inversions = flat.indices.sumOf { i ->
            (i + 1 until flat.size).count { j -> flat[i] > flat[j] }
        }
        return inversions % 2 == 0
    }

    /**
     * Generates a shuffled puzzle tile list guaranteed to be solvable.
     * Continuously shuffles until a solvable permutation is found.
     *
     * @param items The list of puzzle tiles including the empty slot (-1).
     * @return A solvable shuffled list of puzzle tile IDs.
     */
    fun generateSolvablePuzzle(items: List<Int>): List<Int> {
        while (true) {
            val shuffled = items.shuffled()
            if (isSolvable(shuffled)) return shuffled
        }
    }
}
