package content.global.skill.construction.item

import org.rs.consts.Items

/**
 * Enum representing different types of planks in the game.
 *
 * Each plank type is associated with the following:
 * - A log type (to create the plank).
 * - The plank item ID.
 * - The price for converting logs into planks.
 *
 * @property log The item ID of the log used to create the plank.
 * @property plank The item ID of the plank created from the log.
 * @property price The price of the plank conversion.
 */
enum class Planks(
    val log: Int,
    val plank: Int,
    val price: Int,
) {
    WOOD(
        log = Items.LOGS_1511,
        plank = Items.PLANK_960,
        price = 100,
    ),
    OAK(
        log = Items.OAK_LOGS_1521,
        plank = Items.OAK_PLANK_8778,
        price = 250,
    ),
    TEAK(
        log = Items.TEAK_LOGS_6333,
        plank = Items.TEAK_PLANK_8780,
        price = 500,
    ),
    MAHOGANY(
        log = Items.MAHOGANY_LOGS_6332,
        plank = Items.MAHOGANY_PLANK_8782,
        price = 1500,
    ), ;

    companion object {
        /**
         * A map associating the log item ID to its corresponding `Planks` enum value.
         */
        private val product = values().associateBy { it.log }

        /**
         * Retrieves the `Planks` type associated with the given log item ID.
         *
         * @param item The item ID of the log.
         * @return The `Planks` enum value associated with the log item ID, or `null` if no matching log type is found.
         */
        fun getForLog(item: Int): Planks? = product[item]

        /**
         * Calculates the price of the plank with a 30% discount.
         *
         * @return The discounted price for the plank, calculated as 70% of the original price.
         */
        fun Planks.spellPrice(): Int = (this.price * 0.7).toInt()
    }
}
