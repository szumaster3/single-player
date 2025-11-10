package content.data

import core.api.inEquipment
import core.api.inInventory
import core.game.node.entity.player.Player
import shared.consts.Components
import shared.consts.Items

/**
 * Represents light sources.
 */
enum class LightSource(
    /**
     * The fm level required to light the source.
     */
    val level: Int,
    /**
     * The unlit item id.
     */
    val raw: Int,
    /**
     * The lit item id.
     */
    val product: Int,
    /**
     * The source can cause gas explosions.
     */
    val open: Boolean = false,
    /**
     * The darkness overlay strength.
     */
    val interfaceId: Int,
) {
    // Brightness level 1 (dim)

    /**
     * Candle - purchasable at Catherby candle shop.
     */
    CANDLE(1, Items.CANDLE_36, Items.LIT_CANDLE_33, true, Components.DARKNESS_MEDIUM_98),

    /**
     * Black candle - used during Merlin's Crystal quest.
     */
    BLACK_CANDLE(1, Items.BLACK_CANDLE_38, Items.LIT_BLACK_CANDLE_32, true, Components.DARKNESS_MEDIUM_98),

    /**
     * Torch - can be lit underwater in Haunted Mine.
     */
    TORCH(1, Items.UNLIT_TORCH_596, Items.LIT_TORCH_594, true, Components.DARKNESS_MEDIUM_98),

    /**
     * Candle lantern - crafted or bought.
     */
    WHITE_CANDLE_LANTERN(4, Items.CANDLE_LANTERN_4527, Items.CANDLE_LANTERN_4531, false, Components.DARKNESS_MEDIUM_98),

    /**
     * Black candle lantern - moderate light output.
     */
    BLACK_CANDLE_LANTERN(4, Items.CANDLE_LANTERN_4527, Items.CANDLE_LANTERN_4532, false, Components.DARKNESS_MEDIUM_98),

    // Brightness level 2 (medium)

    /**
     * Oil lamp - moderate light output.
     */
    OIL_LAMP(12, Items.OIL_LAMP_4522, Items.OIL_LAMP_4524, true, Components.DARKNESS_LIGHT_97),

    /**
     * Oil lantern - moderate light output.
     */
    OIL_LANTERN(26, Items.OIL_LANTERN_4535, Items.OIL_LANTERN_4539, false, Components.DARKNESS_LIGHT_97),

    /**
     * Sapphire lantern - used in Tears of Guthix.
     */
    SAPPHIRE_LANTERN(49, Items.SAPPHIRE_LANTERN_4701, Items.SAPPHIRE_LANTERN_4702, false, -1),

    /**
     * Mining helmet - can be disabled underwater.
     */
    MINING_HELMET(65, Items.MINING_HELMET_5014, Items.MINING_HELMET_5013, false, Components.DARKNESS_LIGHT_97),

    // Brightness level 3 (bright)

    /**
     * Bullseye lantern.
     */
    BULLSEYE_LANTERN(49, Items.BULLSEYE_LANTERN_4548, Items.BULLSEYE_LANTERN_4550, false, -1),

    /**
     * Emerald lantern - used during Lunar Diplomacy.
     */
    EMERALD_LANTERN(49, Items.EMERALD_LANTERN_9064, Items.EMERALD_LANTERN_9065, false, -1),

    /**
     * Seers' Headband 1 - permanent dim light source.
     */
    HEADBAND_1(1, -1, Items.SEERS_HEADBAND_1_14631, false, Components.DARKNESS_MEDIUM_98),

    /**
     * Seers' Headband 2 - permanent medium light source.
     */
    HEADBAND_2(1, -1, Items.SEERS_HEADBAND_2_14640, false, Components.DARKNESS_LIGHT_97),

    /**
     * Seers' Headband 3 - permanent bright light source.
     */
    HEADBAND_3(1, -1, Items.SEERS_HEADBAND_3_14641, false, -1),

    /**
     * Glowing fungus - works only in Abandoned Mine.
     */
    GLOWING_FUNGUS(1, -1, Items.GLOWING_FUNGUS_4075, false, -1),
    ;

    /**
     * Light intensity level used for darkness overlay logic.
     * - 1 dim
     * - 2 medium
     * - 3 bright
     */
    val strength: Int
        get() =
            when (interfaceId) {
                Components.DARKNESS_LIGHT_97 -> 1
                Components.DARKNESS_MEDIUM_98 -> 2
                -1 -> 3
                else -> Components.DARKNESS_DARK_96
            }

    companion object {
        private val BY_RAW_ID: Map<Int, LightSource> = values().associateBy { it.raw }
        private val BY_PRODUCT_ID: Map<Int, LightSource> = values().associateBy { it.product }

        /**
         * Gets the [LightSource] matching the unlit item id.
         *
         * @param id The unlit item id.
         */
        @JvmStatic fun forId(id: Int): LightSource? = BY_RAW_ID[id]

        /**
         * Gets the [LightSource] matching the lit item id.
         *
         * @param id The lit item id.
         */
        @JvmStatic fun forProductId(id: Int): LightSource? = BY_PRODUCT_ID[id]

        /** Checks if the player currently has any active (lit) light source. */
        @JvmStatic
        fun hasActiveLightSource(player: Player): Boolean = getActiveLightSource(player) != null

        /**
         * Searches the player inventory and equipment for an active light source.
         *
         * @return The active [LightSource].
         */
        @JvmStatic
        fun getActiveLightSource(player: Player): LightSource? =
            values().firstOrNull {
                inInventory(player, it.product) || inEquipment(player, it.product)
            }
    }
}
