package content.global.activity.ttrail.puzzle

import core.game.node.item.Item
import org.rs.consts.Items

/**
 * Represents a sliding puzzle boxes.
 *
 * @property key A puzzle type.
 * @property item The puzzle box item id.
 * @property solutionTiles The puzzle pieces.
 */
enum class PuzzleBox(
    val key: String,
    val item: Item,
    val solutionTiles: List<Int>
) {
    TREE("tree", Item(Items.PUZZLE_BOX_3571), (3643..3666).toList()),
    TROLL("troll", Item(Items.PUZZLE_BOX_2795), (2749..2772).toList()),
    CASTLE("castle", Item(Items.PUZZLE_BOX_3565), (3619..3642).toList());

    companion object {
        /**
         * Retrieves a [PuzzleBox] by its string key.
         *
         * @param key The puzzle type (e.g., "castle").
         * @return The corresponding [PuzzleBox], or `null` if not found.
         */
        fun fromKey(key: String): PuzzleBox? = values().find { it.key == key }

        /**
         * Retrieves a [PuzzleBox] by the associated item id.
         *
         * @param itemId the puzzle box item id.
         * @return The corresponding [PuzzleBox], or `null` if not found.
         */
        fun fromItemId(itemId: Int): PuzzleBox? = values().find { it.item.id == itemId }
    }

    /**
     * The full solution of the puzzle, consisting of all solution tiles plus a blank tile (`-1`) at the end.
     */
    val fullSolution: List<Int> = solutionTiles + -1
}