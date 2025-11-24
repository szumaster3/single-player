package content.global.skill.herblore.potions

import content.global.skill.herblore.HerblorePulse
import content.global.skill.herblore.herbs.HerbItem
import shared.consts.Items

/**
 * Enum representing unfinished potions used in Herblore.
 */
enum class UnfinishedPotion(val base: Int, val ingredient: Int, val product: Int, val level: Int) {
    GUAM(HerblorePulse.VIAL_OF_WATER, HerbItem.GUAM.product.id, Items.GUAM_POTIONUNF_91, 3),
    ROGUE_PURSE(HerblorePulse.VIAL_OF_WATER, HerbItem.ROGUES_PUSE.product.id, Items.ROGUES_PURSE_POTIONUNF_4840, 3),
    MARRENTILL(HerblorePulse.VIAL_OF_WATER, HerbItem.MARRENTILL.product.id, Items.MARRENTILL_POTIONUNF_93, 5),
    TARROMIN(HerblorePulse.VIAL_OF_WATER, HerbItem.TARROMIN.product.id, Items.TARROMIN_POTIONUNF_95, 12),
    OGRE(HerblorePulse.VIAL_OF_WATER, Items.JANGERBERRIES_247, Items.VIAL_2389, 14),
    HARRALANDER(HerblorePulse.VIAL_OF_WATER, HerbItem.HARRALANDER.product.id, Items.HARRALANDER_POTIONUNF_97, 22),
    RANARR(HerblorePulse.VIAL_OF_WATER, HerbItem.RANARR.product.id, Items.RANARR_POTIONUNF_99, 30),
    TOADFLAX(HerblorePulse.VIAL_OF_WATER, HerbItem.TOADFLAX.product.id, Items.TOADFLAX_POTIONUNF_3002, 34),
    SPIRIT_WEED(HerblorePulse.VIAL_OF_WATER, HerbItem.SPIRIT_WEED.product.id, Items.SPIRIT_WEED_POTIONUNF_12181, 40),
    IRIT(HerblorePulse.VIAL_OF_WATER, HerbItem.IRIT.product.id, Items.IRIT_POTIONUNF_101, 45),
    AVANTOE(HerblorePulse.VIAL_OF_WATER, HerbItem.AVANTOE.product.id, Items.AVANTOE_POTIONUNF_103, 50),
    KWUARM(HerblorePulse.VIAL_OF_WATER, HerbItem.KWUARM.product.id, Items.KWUARM_POTIONUNF_105, 55),
    SNAPDRAGON(HerblorePulse.VIAL_OF_WATER, HerbItem.SNAPDRAGON.product.id, Items.SNAPDRAGON_POTIONUNF_3004, 63),
    CADANTINE(HerblorePulse.VIAL_OF_WATER, HerbItem.CADANTINE.product.id, Items.CADANTINE_POTIONUNF_107, 66),
    LANTADYME(HerblorePulse.VIAL_OF_WATER, HerbItem.LANTADYME.product.id, Items.LANTADYME_POTIONUNF_2483, 69),
    DWARF_WEED(HerblorePulse.VIAL_OF_WATER, HerbItem.DWARF_WEED.product.id, Items.DWARF_WEED_POTIONUNF_109, 72),
    TORSTOL(HerblorePulse.VIAL_OF_WATER, HerbItem.TORSTOL.product.id, Items.TORSTOL_POTIONUNF_111, 75),
    STRONG_WEAPON_POISON(HerblorePulse.COCONUT_MILK, Items.CACTUS_SPINE_6016, Items.WEAPON_POISON_PLUSUNF_5936, 73),
    SUPER_STRONG_WEAPON_POISON(HerblorePulse.COCONUT_MILK, Items.CAVE_NIGHTSHADE_2398, Items.WEAPON_POISON_PLUS_PLUSUNF_5939, 82),
    STRONG_ANTIPOISON(HerblorePulse.COCONUT_MILK, HerbItem.TOADFLAX.product.id, Items.ANTIPOISON_PLUSUNF_5942, 68),
    SUPER_STRONG_ANTIPOISON(HerblorePulse.COCONUT_MILK, HerbItem.IRIT.product.id, Items.ANTIPOISON_PLUS_PLUSUNF_5951, 79);

    companion object {
        /**
         * Finds the unfinished potion matching the given item and base.
         */
        fun forID(item: Int, base: Int): UnfinishedPotion? = values().firstOrNull { potion ->
            (potion.ingredient == item || potion.ingredient == base) && (item == potion.base || base == potion.base)
        }
    }
}
