package content.global.skill.construction.item

import core.game.node.entity.player.Player
import core.tools.RandomFunction
import org.rs.consts.Items

enum class Nail(
    val itemId: Int,
    val bendRate: Int,
) {
    BRONZE(
        itemId = Items.BRONZE_NAILS_4819,
        bendRate = 5,
    ),
    IRON(
        itemId = Items.IRON_NAILS_4820,
        bendRate = 7,
    ),
    BLACK(
        itemId = Items.BLACK_NAILS_4821,
        bendRate = 8,
    ),
    STEEL(
        itemId = Items.STEEL_NAILS_1539,
        bendRate = 10,
    ),
    MITHRIL(
        itemId = Items.MITHRIL_NAILS_4822,
        bendRate = 13,
    ),
    ADAMANT(
        itemId = Items.ADAMANTITE_NAILS_4823,
        bendRate = 15,
    ),
    RUNE(
        itemId = Items.RUNE_NAILS_4824,
        bendRate = 20,
    ),
    ;

    /**
     * Checks if a random function produces a value that corresponds to a bend.
     * The result is based on a predefined `bendRate`. If the random function returns 0, it indicates a bend.
     *
     * @return `true` if a bend occurs, otherwise `false`.
     */
    fun isBend(): Boolean = RandomFunction.getRandom(bendRate) == 0

    companion object {
        /**
         * A collection of all possible values of the `Nails` enum.
         */
        val values = enumValues<Nail>()

        /**
         * A map associating each `Nails` enum value to its corresponding `itemId`.
         */
        val product = values.associateBy { it.itemId }

        /**
         * Retrieves the first `Nails` type from the player's inventory that contains the required amount of nails.
         *
         * This function checks the player's inventory for each nail type and returns the first one
         * that satisfies the condition of having at least the required amount.
         *
         * @param player The player whose inventory is being checked.
         * @param requiredAmount The amount of nails required to satisfy the condition.
         * @return The `Nails` enum value that corresponds to the type of nails found in the inventory, or `null` if none are found.
         */
        @JvmStatic
        fun get(
            player: Player,
            requiredAmount: Int,
        ): Nail? {
            for (nailType in values()) {
                if (player.inventory.contains(nailType.itemId, requiredAmount)) {
                    return nailType
                }
            }
            return null
        }
    }
}
