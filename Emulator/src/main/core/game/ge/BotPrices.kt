package core.game.ge

import core.cache.def.impl.ItemDefinition
import org.rs.consts.Items

/**
 * This class handles getting item prices, including both standard prices and custom price overrides for specific items.
 * It provides a method to get the price of an item by its ID, either from custom overrides or from the item definition.
 */
class BotPrices {
    companion object {

        /**
         * Gets the price of an item by its ID. If a price override exists, it returns the overridden price.
         * Otherwise, it returns the price defined in the item definition.
         *
         * @param id The item ID.
         * @return The price of the item.
         */
        @JvmStatic
        fun getPrice(id: Int): Int = getPriceOverrides(id) ?: ItemDefinition.forId(id).value

        /**
         * Gets the custom price override for an item if it exists. If no override exists, returns null.
         *
         * @param id The item ID.
         * @return The overridden price if it exists, otherwise null.
         */
        @JvmStatic
        fun getPriceOverrides(id: Int): Int? =
            when (id) {
                // List of item ID overrides with their corresponding prices.
                Items.PURE_ESSENCE_7936 -> 50
                Items.BOW_STRING_1777 -> 250
                Items.MAGIC_LOGS_1513 -> 750
                Items.COWHIDE_1739 -> 250
                Items.DRAGON_BONES_536 -> 1250
                Items.GREEN_DRAGONHIDE_1753 -> 550
                Items.GRIMY_RANARR_207 -> 1214
                Items.GRIMY_AVANTOE_211 -> 453
                Items.GRIMY_CADANTINE_215 -> 232
                Items.GRIMY_DWARF_WEED_217 -> 86
                Items.GRIMY_GUAM_199 -> 50
                Items.GRIMY_HARRALANDER_205 -> 115
                Items.GRIMY_IRIT_209 -> 860
                Items.GRIMY_KWUARM_213 -> 334
                Items.GRIMY_LANTADYME_2485 -> 115
                Items.GRIMY_MARRENTILL_201 -> 250
                Items.LOBSTER_379 -> 268
                Items.RAW_LOBSTER_377 -> 265
                Items.LOOP_HALF_OF_A_KEY_987 -> 5250
                Items.TOOTH_HALF_OF_A_KEY_985 -> 4263
                Items.SWORDFISH_373 -> 400
                Items.RAW_SWORDFISH_371 -> 390
                Items.SHARK_385 -> 720
                Items.RAW_SHARK_383 -> 710
                // If no custom override is available, return null.
                else -> null
            }
    }
}
