package content.global.skill.crafting.items.armour.leather.studded

import org.rs.consts.Items

/**
 * Represents the different types of studded leather armour that can be crafted.
 */
enum class StuddedArmour(val leather: Int, val product: Int, val level: Int, val experience: Double, ) {
    CHAPS(Items.LEATHER_CHAPS_1095, Items.STUDDED_CHAPS_1097, 44, 42.0),
    BODY(Items.LEATHER_BODY_1129, Items.STUDDED_BODY_1133, 41, 40.0),
    ;

    companion object {
        /**
         * Map leather item ids to [StuddedArmour] entries for fast lookup.
         */
        private val map = values().associateBy { it.leather }

        /**
         * Returns [StuddedArmour] by leather item id, or null if none.
         */
        @JvmStatic
        fun forId(itemId: Int): StuddedArmour? = map[itemId]
    }
}
