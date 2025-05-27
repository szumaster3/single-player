package content.global.skill.crafting.items.armour.capes

import core.game.node.item.Item
import org.rs.consts.Items

/**
 * Represents capes.
 */
enum class Capes(val dye: Dyes, val cape: Item) {
    BLACK(Dyes.BLACK, Item(Items.BLACK_CAPE_1019)),
    RED(Dyes.RED, Item(Items.RED_CAPE_1007)),
    BLUE(Dyes.BLUE, Item(Items.BLUE_CAPE_1021)),
    YELLOW(Dyes.YELLOW, Item(Items.YELLOW_CAPE_1023)),
    GREEN(Dyes.GREEN, Item(Items.GREEN_CAPE_1027)),
    PURPLE(Dyes.PURPLE, Item(Items.PURPLE_CAPE_1029)),
    ORANGE(Dyes.ORANGE, Item(Items.ORANGE_CAPE_1031)),
    PINK(Dyes.PINK, Item(Items.PINK_CAPE_6959));

    companion object {
        private val dyeToCapeMap: MutableMap<Int?, Capes?> = HashMap()

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
