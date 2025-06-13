package content.global.skill.construction.item

import org.rs.consts.Items

/**
 * Represents different types of planks.
 */
enum class Plank(val log: Int, val plank: Int, val price: Int, ) {
    WOOD(Items.LOGS_1511, Items.PLANK_960, 100),
    OAK(Items.OAK_LOGS_1521, Items.OAK_PLANK_8778, 250),
    TEAK(Items.TEAK_LOGS_6333, Items.TEAK_PLANK_8780, 500),
    MAHOGANY(Items.MAHOGANY_LOGS_6332, Items.MAHOGANY_PLANK_8782, 1500),
    ;

    companion object {
        private val product = values().associateBy { it.log }

        /**
         * Gets the [Plank] type for the given id.
         *
         * @param item The item id of the log.
         */
        fun getForLog(item: Int): Plank? = product[item]

        /**
         * Calculates the price of the plank with a 30% discount.
         *
         * @return The discounted price.
         */
        fun Plank.spellPrice(): Int = (this.price * 0.7).toInt()
    }
}
