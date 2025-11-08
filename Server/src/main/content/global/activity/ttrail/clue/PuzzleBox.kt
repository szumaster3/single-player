package content.global.activity.ttrail.clue

import core.api.getAttribute
import core.api.inInventory
import core.game.node.entity.player.Player
import shared.consts.Items

enum class PuzzleBox(val type: String, val id: Int, val tiles: List<Int>) {
    TROLL(
        "troll",
        Items.PUZZLE_BOX_3571,
        (Items.SLIDING_PIECE_3643..Items.SLIDING_PIECE_3666).toList()
    ),
    CASTLE(
        "castle",
        Items.PUZZLE_BOX_2795,
        (Items.SLIDING_PIECE_2749..Items.SLIDING_PIECE_2772).toList()
    ),
    TREE(
        "tree",
        Items.PUZZLE_BOX_3565,
        (Items.SLIDING_PIECE_3619..Items.SLIDING_PIECE_3642).toList()
    ),
    GLIDER(
        "glider",
        Items.SPARE_CONTROLS_4002,
        listOf(
            3904, 3906, 3908, 3910, 3912, 3914, 3916, 3918, 3920, 3922,
            3924, 3926, 3928, 3930, 3932, 3934, 3936, 3938, 3940, 3942,
            3944, 3946, 3948, 3950
        )
    );

    val fullSolution: List<Int> = tiles + -1

    companion object {
        fun forType(type: String) = values().firstOrNull { it.name.lowercase() == type.lowercase() }
        fun forId(id: Int) = values().firstOrNull { it.id == id }
        fun random() = values().random()

        /**
         * Check the player has already completed the puzzle.
         */
        fun isComplete(player: Player, type: String): Boolean {
            val box = forType(type) ?: return false
            return getAttribute(player, "$type:puzzle:done", false) && inInventory(player, box.id, 1)
        }
    }
}