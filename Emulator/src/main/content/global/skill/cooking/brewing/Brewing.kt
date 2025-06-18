package content.global.skill.cooking.brewing

import core.game.node.item.Item
import org.rs.consts.Items

enum class Brewing(
    val level: Int,
    val experience: Double,
    val product: Item,
    inputs: Array<Item>,
) {
    ASGARNIAN_ALE(24, 284.0, Item(Items.ASGARNIAN_ALE4_5785, 1), arrayOf(Item(Items.ASGARNIAN_HOPS_5996, 4))),
    AXEMAN_FOLLY(49, 413.0, Item(Items.AXEMANS_FOLLY4_5825, 1), arrayOf(Item(Items.OAK_ROOTS_6043, 1))),
    CHEFS_DELIGHT(54, 446.0, Item(Items.CHEFS_DELIGHT4_5834, 1), arrayOf(Item(Items.CHOCOLATE_DUST_1975, 4))),
    COMPOST(1, 1.0, Item(Items.COMPOST_6032, 1), arrayOf(Item(Items.BARLEY_MALT_6008))),
    DRAGON_BITTER(39, 347.0, Item(Items.DRAGON_BITTER4_5809, 1), arrayOf(Item(Items.KRANDORIAN_HOPS_6000, 4))),
    DWARVEN_STOUT(19, 215.0, Item(Items.DWARVEN_STOUT4_5777, 1), arrayOf(Item(Items.HAMMERSTONE_HOPS_5994, 4))),
    GREENMAN_ALE(29, 281.0, Item(Items.GREENMANS_ALE4_5793, 1), arrayOf(Item(Items.CLEAN_HARRALANDER_255, 4))),
    KELDA_STOUT(22, 0.0, Item(Items.KELDA_STOUT_6118, 1), arrayOf(Item(Items.KELDA_HOPS_6113, 4))),
    MATURE_CIDER(14, 192.0, Item(Items.MATURE_CIDER_5765, 1), arrayOf(Item(Items.APPLE_MUSH_5992, 4), Item(Items.THE_STUFF_8988, 1))),
    MIND_BOMB(34, 314.0, Item(Items.MIND_BOMB4_5801, 1), arrayOf(Item(Items.YANILLIAN_HOPS_5998, 4))),
    MOONLIGHT_MEAD(44, 380.0, Item(Items.MOONLIGHT_MEAD4_5817, 1), arrayOf(Item(Items.MUSHROOM_6004, 4))),
    SLAYER_RESPITE(59, 479.0, Item(Items.SLAYERS_RESPITE4_5841, 1), arrayOf(Item(Items.WILDBLOOD_HOPS_6002, 4))),
    WIZARDS_MIND_BOMB(34, 314.0, Item(Items.WIZARDS_MIND_BOMB_1907, 1), arrayOf(Item(Items.YANILLIAN_HOPS_5999, 4), Item(Items.THE_STUFF_8988))),
    ;

    val inputs: Array<Item> = inputs
}
