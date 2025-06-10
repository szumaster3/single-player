package content.region.karamja.handlers.shilo

import org.rs.consts.Items

/**
 * Represents the Antique items.
 */
enum class YanniAntiqueItem(val antique: Int, val price: Int) {
    BONE_KEY(Items.BONE_KEY_605, 100),
    STONE_PLAQUE(Items.STONE_PLAQUE_606, 100),
    TATTERED_SCROLL(Items.TATTERED_SCROLL_607, 100),
    CRUMPLED_SCROLL(Items.CRUMPLED_SCROLL_608, 100),
    LOCATING_CRYSTAL(Items.LOCATING_CRYSTAL_611, 500),
    BEADS_OF_THE_DEAD(Items.BEADS_OF_THE_DEAD_616, 1000),
    BERVIRIUS_NOTES(Items.BERVIRIUS_NOTES_624, 100),
    BLACK_PRISM(Items.BLACK_PRISM_4808, 5000);

    companion object {
        /**
         * Finds an [YanniAntiqueItem] by item id.
         *
         * @param id the item id
         * @return The matching [YanniAntiqueItem] if found, or `null` if no match exists.
         */
        fun getAntiqueItem(id: Int): YanniAntiqueItem? = values().find { it.antique == id }
    }
}
