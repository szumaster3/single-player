package content.global.skill.fletching

import content.global.skill.fletching.items.arrows.ArrowHead
import content.global.skill.fletching.items.arrows.BrutalArrow
import content.global.skill.fletching.items.bolts.Bolt
import content.global.skill.fletching.items.bolts.GemBolt
import content.global.skill.fletching.items.bow.Strings
import content.global.skill.fletching.items.crossbow.Limb
import content.global.skill.fletching.items.darts.Dart
import core.game.node.item.Item
import shared.consts.Items
/**
 * Utility object for handling all logic related to the Fletching skill.
 */
object Fletching {

    /** The map of log item ids to their possible fletching outcomes (FletchingItems). */
    val logMap = Item.values().associateTo(HashMap()) { it.id to it.items }

    /** The id of an arrow shaft. */
    const val arrowShaftId = Items.ARROW_SHAFT_52

    /** The id of a headless arrow (shaft with feather). */
    const val fletchedShaftId = Items.HEADLESS_ARROW_53

    /** The id of a flighted ogre arrow. */
    const val fligtedOgreArrowId = Items.FLIGHTED_OGRE_ARROW_2865

    /** List of valid feather item ids usable in fletching. */
    val featherIds = intArrayOf(
        Items.FEATHER_314, Items.STRIPY_FEATHER_10087, Items.RED_FEATHER_10088,
        Items.BLUE_FEATHER_10089, Items.YELLOW_FEATHER_10090, Items.ORANGE_FEATHER_10091
    )

    /** List of valid string item ids for bows and crossbows. */
    val stringIds = intArrayOf(Items.BOW_STRING_1777, Items.CROSSBOW_STRING_9438)

    /** List of valid kebbit spike item ids used in certain types of fletching. */
    val kebbitSpikeIds = intArrayOf(Items.KEBBIT_SPIKE_10105, Items.LONG_KEBBIT_SPIKE_10107)

    /** List of gem item ids used for crafting gem-tipped bolts. */
    val gemIds = intArrayOf(
        Items.OYSTER_PEARL_411, Items.OYSTER_PEARLS_413,
        Items.OPAL_1609, Items.JADE_1611, Items.RED_TOPAZ_1613,
        Items.SAPPHIRE_1607, Items.EMERALD_1605, Items.RUBY_1603,
        Items.DIAMOND_1601, Items.DRAGONSTONE_1615, Items.ONYX_6573
    )

    /** All limb item ids for crossbow construction. */
    val limbIds = Limb.values().map(Limb::limb).toIntArray()

    /** All stock item ids for crossbow construction. */
    val stockIds = Limb.values().map(Limb::stock).toIntArray()

    /** All nail item ids used for brutal arrows. */
    val nailIds = BrutalArrow.values().map(BrutalArrow::base).toIntArray()

    /** All unfinished arrow ids (shafts with heads not yet combined). */
    val unfinishedArrows = ArrowHead.values().map { it.unfinished }.toIntArray()

    /** All unstrung bow item ids. */
    val unstrungBows = Strings.values().map(Strings::unfinished).toIntArray()

    /** All bolt base item ids for gem-tipped bolts. */
    val boltBaseIds = GemBolt.values().map { it.base }.toIntArray()

    /** All bolt tip item ids for gem-tipped bolts. */
    val boltTipIds = GemBolt.values().map { it.tip }.toIntArray()

    /**
     * Retrieves the fletching entries (possible outcomes) for a given item id.
     *
     * @param id The item id to look up.
     * @return An array of [FletchingItems] if found, or null otherwise.
     */
    @JvmStatic
    fun getEntries(id: Int): Array<FletchingItems>? = logMap[id]

    /**
     * Checks if the given item id represents a log that can be used in fletching.
     *
     * @param id The item id to check.
     * @return True if the item is a fletchable log, false otherwise.
     */
    @JvmStatic
    fun isLog(id: Int): Boolean = logMap.containsKey(id)

    /**
     * Checks if the given item id is a valid bolt.
     *
     * @param id The item id to check.
     * @return True if the item is a bolt, false otherwise.
     */
    @JvmStatic
    fun isBolt(id: Int): Boolean = Bolt.product[id] != null

    /**
     * Checks if the given item id is a dart.
     *
     * @param id The item id to check.
     * @return True if the item is a dart, false otherwise.
     */
    @JvmStatic
    fun isDart(id: Int): Boolean = Dart.product[id] != null

    /**
     * Gets the actual item instances for a given log or base item id.
     *
     * @param id The item id to get outcomes for.
     * @return An array of resulting [Item]s, or null if the item cannot be fletched.
     */
    @JvmStatic
    fun getItems(id: Int): Array<core.game.node.item.Item>? =
        getEntries(id)?.map { Item(it.id) }?.toTypedArray()

    /**
     * Represents the fletching stocks.
     */
    private enum class Item(
        val id: Int,
        vararg fletchingItems: FletchingItems,
    ) {
        STANDARD(
            Items.LOGS_1511,
            FletchingItems.ARROW_SHAFT,
            FletchingItems.SHORT_BOW,
            FletchingItems.LONG_BOW,
            FletchingItems.WOODEN_STOCK,
        ),
        ACHEY(
            Items.ACHEY_TREE_LOGS_2862,
            FletchingItems.OGRE_ARROW_SHAFT,
            FletchingItems.OGRE_COMPOSITE_BOW,
        ),
        OAK(
            Items.OAK_LOGS_1521,
            FletchingItems.OAK_SHORTBOW,
            FletchingItems.OAK_LONGBOW,
            FletchingItems.OAK_STOCK,
        ),
        WILLOW(
            Items.WILLOW_LOGS_1519,
            FletchingItems.WILLOW_SHORTBOW,
            FletchingItems.WILLOW_LONGBOW,
            FletchingItems.WILLOW_STOCK,
        ),
        MAPLE(
            Items.MAPLE_LOGS_1517,
            FletchingItems.MAPLE_SHORTBOW,
            FletchingItems.MAPLE_LONGBOW,
            FletchingItems.MAPLE_STOCK,
        ),
        YEW(
            Items.YEW_LOGS_1515,
            FletchingItems.YEW_SHORTBOW,
            FletchingItems.YEW_LONGBOW,
            FletchingItems.YEW_STOCK,
        ),
        MAGIC(
            Items.MAGIC_LOGS_1513,
            FletchingItems.MAGIC_SHORTBOW,
            FletchingItems.MAGIC_LONGBOW,
        ),
        TEAK(
            Items.TEAK_LOGS_6333,
            FletchingItems.TEAK_STOCK,
        ),
        MAHOGANY(
            Items.MAHOGANY_LOGS_6332,
            FletchingItems.MAHOGANY_STOCK,
        ),
        ;

        val items: Array<FletchingItems> = fletchingItems as Array<FletchingItems>
    }

    /**
     * Represents the fletching items.
     */
    enum class FletchingItems(
        val id: Int,
        val experience: Double,
        val level: Int,
        val amount: Int,
    ) {
        ARROW_SHAFT(
            id = Items.ARROW_SHAFT_52,
            experience = 5.0,
            level = 1,
            amount = 15,
        ),
        SHORT_BOW(
            id = Items.SHORTBOW_U_50,
            experience = 5.0,
            level = 5,
            amount = 1,
        ),
        LONG_BOW(
            id = Items.LONGBOW_U_48,
            experience = 10.0,
            level = 10,
            amount = 1,
        ),
        WOODEN_STOCK(
            id = Items.WOODEN_STOCK_9440,
            experience = 6.0,
            level = 9,
            amount = 1,
        ),
        OGRE_ARROW_SHAFT(
            id = Items.OGRE_ARROW_SHAFT_2864,
            experience = 6.4,
            level = 5,
            amount = 4,
        ),

        // Authentic.
        OGRE_COMPOSITE_BOW(
            id = Items.COMP_OGRE_BOW_4827,
            experience = 40.0,
            level = 30,
            amount = 1,
        ),
        OAK_SHORTBOW(
            id = Items.OAK_SHORTBOW_U_54,
            experience = 16.5,
            level = 20,
            amount = 1,
        ),
        OAK_LONGBOW(
            id = Items.OAK_LONGBOW_U_56,
            experience = 25.0,
            level = 25,
            amount = 1,
        ),
        OAK_STOCK(
            id = Items.OAK_STOCK_9442,
            experience = 16.0,
            level = 24,
            amount = 1,
        ),
        WILLOW_SHORTBOW(
            id = Items.WILLOW_SHORTBOW_U_60,
            experience = 33.3,
            level = 35,
            amount = 1,
        ),
        WILLOW_LONGBOW(
            id = Items.WILLOW_LONGBOW_U_58,
            experience = 41.5,
            level = 40,
            amount = 1,
        ),
        WILLOW_STOCK(
            id = Items.WILLOW_STOCK_9444,
            experience = 22.0,
            level = 39,
            amount = 1,
        ),
        MAPLE_SHORTBOW(
            id = Items.MAPLE_SHORTBOW_U_64,
            experience = 50.0,
            level = 50,
            amount = 1,
        ),
        MAPLE_LONGBOW(
            id = Items.MAPLE_LONGBOW_U_62,
            experience = 58.3,
            level = 55,
            amount = 1,
        ),
        MAPLE_STOCK(
            id = Items.MAPLE_STOCK_9448,
            experience = 32.0,
            level = 54,
            amount = 1,
        ),
        YEW_SHORTBOW(
            id = Items.YEW_SHORTBOW_U_68,
            experience = 67.5,
            level = 65,
            amount = 1,
        ),
        YEW_LONGBOW(
            id = Items.YEW_LONGBOW_U_66,
            experience = 75.0,
            level = 70,
            amount = 1,
        ),
        YEW_STOCK(
            id = Items.YEW_STOCK_9452,
            experience = 50.0,
            level = 69,
            amount = 1,
        ),
        MAGIC_SHORTBOW(
            id = Items.MAGIC_SHORTBOW_U_72,
            experience = 83.3,
            level = 80,
            amount = 1,
        ),
        MAGIC_LONGBOW(
            id = Items.MAGIC_LONGBOW_U_70,
            experience = 91.5,
            level = 85,
            amount = 1,
        ),
        TEAK_STOCK(
            id = Items.TEAK_STOCK_9446,
            experience = 27.0,
            level = 46,
            amount = 1,
        ),
        MAHOGANY_STOCK(
            id = Items.MAHOGANY_STOCK_9450,
            experience = 41.0,
            level = 61,
            amount = 1,
        ),
    }
}
