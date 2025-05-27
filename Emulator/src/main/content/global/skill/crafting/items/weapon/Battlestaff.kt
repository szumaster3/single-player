package content.global.skill.crafting.items.weapon

import org.rs.consts.Items

/**
 * Represents the different types of battlestaffs that can be crafted.
 */
enum class Battlestaff(
    val required: Int,
    val productId: Int,
    val amount: Int = 1,
    val requiredLevel: Int,
    val experience: Double,
) {
    /**
     * Crafting Water Battlestaff using a Water Orb.
     */
    WATER_BATTLESTAFF(
        required = Items.WATER_ORB_571,
        productId = Items.WATER_BATTLESTAFF_1395,
        requiredLevel = 54,
        experience = 100.0,
    ),

    /**
     * Crafting Earth Battlestaff using an Earth Orb.
     */
    EARTH_BATTLESTAFF(
        required = Items.EARTH_ORB_575,
        productId = Items.EARTH_BATTLESTAFF_1399,
        requiredLevel = 58,
        experience = 112.5,
    ),

    /**
     * Crafting Fire Battlestaff using a Fire Orb.
     */
    FIRE_BATTLESTAFF(
        required = Items.FIRE_ORB_569,
        productId = Items.FIRE_BATTLESTAFF_1393,
        requiredLevel = 62,
        experience = 125.0,
    ),

    /**
     * Crafting Air Battlestaff using an Air Orb.
     */
    AIR_BATTLESTAFF(
        required = Items.AIR_ORB_573,
        productId = Items.AIR_BATTLESTAFF_1397,
        requiredLevel = 66,
        experience = 137.5,
    ),
    ;

    companion object {
        private val requiredMap: Map<Int, Battlestaff> = values().associateBy { it.required }
        private val productMap: Map<Int, Battlestaff> = values().associateBy { it.productId }

        /**
         * Finds [Battlestaff] by orb item id or `null` if none.
         */
        @JvmStatic
        fun forId(itemId: Int): Battlestaff? = requiredMap[itemId]

        /**
         * Finds [Battlestaff] by product id or `null` if none.
         */
        @JvmStatic
        fun forProductId(productId: Int): Battlestaff? = productMap[productId]
    }
}
