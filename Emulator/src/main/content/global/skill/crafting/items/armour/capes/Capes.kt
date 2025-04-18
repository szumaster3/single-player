package content.global.skill.crafting.items.armour.capes

import core.game.node.item.Item
import org.rs.consts.Items

/**
 * The enum Capes.
 */
enum class Capes(
    /**
     * Gets dye.
     *
     * @return the dye
     */
    val dye: Dyes,
    /**
     * Gets cape.
     *
     * @return the cape
     */
    val cape: Item
) {
    /**
     * The Black.
     */
    BLACK(Dyes.BLACK, Item(Items.BLACK_CAPE_1019)),

    /**
     * The Red.
     */
    RED(Dyes.RED, Item(Items.RED_CAPE_1007)),

    /**
     * The Blue.
     */
    BLUE(Dyes.BLUE, Item(Items.BLUE_CAPE_1021)),

    /**
     * The Yellow.
     */
    YELLOW(Dyes.YELLOW, Item(Items.YELLOW_CAPE_1023)),

    /**
     * The Green.
     */
    GREEN(Dyes.GREEN, Item(Items.GREEN_CAPE_1027)),

    /**
     * The Purple.
     */
    PURPLE(Dyes.PURPLE, Item(Items.PURPLE_CAPE_1029)),

    /**
     * The Orange.
     */
    ORANGE(Dyes.ORANGE, Item(Items.ORANGE_CAPE_1031)),

    /**
     * The Pink.
     */
    PINK(Dyes.PINK, Item(Items.PINK_CAPE_6959));

    companion object {
        private val dyeToCapeMap: MutableMap<Int?, Capes?> = HashMap<Int?, Capes?>()

        init {
            for (cape in values()) {
                dyeToCapeMap.put(cape.dye.item.id, cape)
            }
        }

        /**
         * For dye capes.
         *
         * @param dyeId the dye id
         * @return the capes
         */
        fun forDye(dyeId: Int): Capes? {
            return dyeToCapeMap[dyeId]
        }
    }
}
