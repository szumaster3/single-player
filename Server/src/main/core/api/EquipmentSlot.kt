package core.api

/**
 * Represents the different equipment slots a player can use.
 */
enum class EquipmentSlot {
    /**
     * Head slot for helmets and headgear.
     */
    HEAD,

    /**
     * Cape or back slot.
     */
    CAPE,

    /**
     * Neck slot for amulets and necklaces.
     */
    NECK,

    /**
     * Weapon slot for main-hand weapons.
     */
    WEAPON,

    /**
     * Chest slot for body armor or clothing.
     */
    CHEST,

    /**
     * Shield or off-hand slot.
     */
    SHIELD,

    /**
     * Hidden/internal slot 1.
     */
    HIDDEN_1,

    /**
     * Legs slot for pants or leg armor.
     */
    LEGS,

    /**
     * Hidden/internal slot 2.
     */
    HIDDEN_2,

    /**
     * Hands slot for gloves or bracelets.
     */
    HANDS,

    /**
     * Feet slot for boots or shoes.
     */
    FEET,

    /**
     * Hidden/internal slot 3.
     */
    HIDDEN_3,

    /**
     * Ring slot.
     */
    RING,

    /**
     * Ammo slot for arrows, bolts, etc.
     */
    AMMO;

    companion object {
        private val slotMap = HashMap<String, EquipmentSlot>()

        init {
            slotMap["head"] = HEAD
            slotMap["helm"] = HEAD
            slotMap["helmet"] = HEAD
            slotMap["cape"] = CAPE
            slotMap["back"] = CAPE
            slotMap["neck"] = NECK
            slotMap["amulet"] = NECK
            slotMap["weapon"] = WEAPON
            slotMap["main"] = WEAPON
            slotMap["chest"] = CHEST
            slotMap["body"] = CHEST
            slotMap["torso"] = CHEST
            slotMap["shield"] = SHIELD
            slotMap["off"] = SHIELD
            slotMap["legs"] = LEGS
            slotMap["leg"] = LEGS
            slotMap["hands"] = HANDS
            slotMap["hand"] = HANDS
            slotMap["brace"] = HANDS
            slotMap["bracelet"] = HANDS
            slotMap["feet"] = FEET
            slotMap["ring"] = RING
            slotMap["ammo"] = AMMO
            slotMap["ammunition"] = AMMO
        }

        /**
         * Gets the [EquipmentSlot] by name.
         *
         * @param input the name of the equipment slot (case-insensitive).
         * @return the matching [EquipmentSlot], or `null` if no match is found.
         */
        fun slotByName(input: String): EquipmentSlot? = slotMap[input.lowercase()]
    }
}
