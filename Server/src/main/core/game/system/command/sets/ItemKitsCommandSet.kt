package core.game.system.command.sets

import core.api.addItem
import core.game.node.item.Item
import core.game.system.command.Privilege
import core.plugin.Initializable
import org.rs.consts.Items

@Initializable
class ItemKitsCommandSet : CommandSet(Privilege.ADMIN) {
    private val farmKitItems = arrayListOf(Items.RAKE_5341, Items.SPADE_952, Items.SEED_DIBBER_5343, Items.WATERING_CAN8_5340, Items.SECATEURS_5329, Items.GARDENING_TROWEL_5325)
    private val runeKitItems = arrayListOf(Items.AIR_RUNE_556, Items.EARTH_RUNE_557, Items.FIRE_RUNE_554, Items.WATER_RUNE_555, Items.MIND_RUNE_558, Items.BODY_RUNE_559, Items.DEATH_RUNE_560, Items.NATURE_RUNE_561, Items.CHAOS_RUNE_562, Items.LAW_RUNE_563, Items.COSMIC_RUNE_564, Items.BLOOD_RUNE_565, Items.SOUL_RUNE_566, Items.ASTRAL_RUNE_9075)
    private val talismanKitItems = arrayListOf(Items.AIR_TALISMAN_1438, Items.EARTH_TALISMAN_1440, Items.FIRE_TALISMAN_1442, Items.WATER_TALISMAN_1444, Items.MIND_TALISMAN_1448, Items.BODY_TALISMAN_1446, Items.DEATH_TALISMAN_1456, Items.NATURE_TALISMAN_1462, Items.CHAOS_TALISMAN_1452, Items.LAW_TALISMAN_1458, Items.COSMIC_TALISMAN_1454, Items.BLOOD_TALISMAN_1450, Items.SOUL_TALISMAN_1460)
    private val dyeKitItems = arrayListOf(Items.RED_DYE_1763, Items.YELLOW_DYE_1765, Items.BLUE_DYE_1767, Items.ORANGE_DYE_1769, Items.GREEN_DYE_1771, Items.PURPLE_DYE_1773, Items.BLACK_MUSHROOM_INK_4622, Items.PINK_DYE_6955)

    override fun defineCommands() {

        /*
         * Provide a set of talismans.
         */

        define(
            name = "talismankit",
            privilege = Privilege.ADMIN,
            usage = "::talismankit",
            description = "Provides a set of talisman items.",
        ) { player, _ ->
            for (item in talismanKitItems) {
                player.inventory.add(Item(item))
            }
            return@define
        }

        /*
         * Provide a kit of various farming equipment.
         */

        define(
            name = "farmkit",
            privilege = Privilege.ADMIN,
            usage = "::farmkit",
            description = "Provides a kit of various farming equipment.",
        ) { player, _ ->
            for (item in farmKitItems) {
                player.inventory.add(Item(item))
            }
            return@define
        }

        /*
         * Provides 1000 of each rune type to the player.
         */

        define(
            name = "runekit",
            privilege = Privilege.ADMIN,
            usage = "::runekit",
            description = "Gives 1k of each Rune type.",
        ) { player, _ ->
            for (item in runeKitItems) {
                addItem(player, item, 1000)
            }
            return@define
        }
    }
}
