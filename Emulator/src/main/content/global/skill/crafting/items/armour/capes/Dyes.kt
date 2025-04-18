package content.global.skill.crafting.items.armour.capes

import core.game.node.item.Item
import org.rs.consts.Items

/**
 * The enum Dyes.
 */
enum class Dyes(@JvmField val item: Item) {
    /**
     * The Black.
     */
    BLACK(Item(Items.BLACK_MUSHROOM_INK_4622)),

    /**
     * The Red.
     */
    RED(Item(Items.RED_DYE_1763)),

    /**
     * The Yellow.
     */
    YELLOW(Item(Items.YELLOW_DYE_1765)),

    /**
     * The Blue.
     */
    BLUE(Item(Items.BLUE_DYE_1767)),

    /**
     * The Orange.
     */
    ORANGE(Item(Items.ORANGE_DYE_1769)),

    /**
     * The Green.
     */
    GREEN(Item(Items.GREEN_DYE_1771)),

    /**
     * The Purple.
     */
    PURPLE(Item(Items.PURPLE_DYE_1773)),

    /**
     * The Pink.
     */
    PINK(Item(Items.PINK_DYE_6955));

    companion object {
        private val itemToDyeMap: MutableMap<Int?, Dyes?> = HashMap<Int?, Dyes?>()

        init {
            for (dye in values()) {
                itemToDyeMap.put(dye.item.id, dye)
            }
        }

        /**
         * For item dyes.
         *
         * @param item the item
         * @return the dyes
         */
        fun forItem(item: Item): Dyes? {
            return itemToDyeMap[item.id]
        }
    }
}
