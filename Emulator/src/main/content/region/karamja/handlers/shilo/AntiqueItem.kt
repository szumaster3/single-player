package content.region.karamja.handlers.shilo

import core.game.node.item.Item
import org.rs.consts.Items

/**
 * Represents the Antique items.
 */
enum class AntiqueItem(val item: Item, val price: Int, val message: String, val dialogue: String) {
    BONE_KEY(Item(Items.BONE_KEY_605, 1), 100, "I'll give you 100 coins for the Bone Key...", "That's a great bone key."),
    STONE_PLAQUE(Item(Items.STONE_PLAQUE_606, 1), 100, "I'll give you 100 coins for the Stone Plaque...", "That's a great stone plaque."),
    TATTERED_SCROLL(Item(Items.TATTERED_SCROLL_607, 1), 100, "I'll give you 100 coins for the tattered scroll...", "That's a great tattered scroll."),
    CRUMPLED_SCROLL(Item(Items.CRUMPLED_SCROLL_608, 1), 100, "I'll give you 100 coins for the crumpled scroll...", "That's a great crumpled scroll."),
    LOCATING_CRYSTAL(Item(Items.LOCATING_CRYSTAL_611, 1), 500, "I'll give you 500 coins for your locating crystal...", "That's a great Locating Crystal."),
    BEADS_OF_THE_DEAD(Item(Items.BEADS_OF_THE_DEAD_616, 1), 1000, "I'll give you 1000 coins for your 'Beads of the Dead'...", "Impressive necklace there."),
    BERVIRIUS_NOTES(Item(Items.BERVIRIUS_NOTES_624, 1), 100, "I'll give you 100 coins for your bervirius scroll...", "That's a great copy of Bervirius notes."),
    BLACK_PRISM(Item(Items.BLACK_PRISM_4808, 1), 5000, "I'll give you 5000 coins for your Black prism...", "Ah you'd like to sell this to me would you? I can offer you 5000 coins!");

    companion object {
        private val antiqueMap: MutableMap<Int, AntiqueItem> = HashMap()

        init {
            for (antique in values()) {
                antiqueMap[antique.item.id] = antique
            }
        }

        /**
         * Gets antique item.
         *
         * @param id the id
         * @return the antique item
         */
        fun getAntiqueItem(id: Int): AntiqueItem? {
            return antiqueMap[id]
        }
    }
}
