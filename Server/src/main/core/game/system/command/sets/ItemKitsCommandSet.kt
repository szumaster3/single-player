package core.game.system.command.sets

import core.api.addItem
import core.game.node.entity.combat.spell.Runes
import core.game.node.item.Item
import core.game.system.command.Privilege
import core.plugin.Initializable
import shared.consts.Items

@Initializable
class ItemKitsCommandSet : CommandSet(Privilege.ADMIN) {
    private val farmKit = arrayListOf(Items.RAKE_5341, Items.SPADE_952, Items.SEED_DIBBER_5343, Items.WATERING_CAN8_5340, Items.SECATEURS_5329, Items.GARDENING_TROWEL_5325)
    private val runeKit = Runes.values()
        .filter { "STAFF" !in it.name }
        .map { it.id }
        .toIntArray()

    override fun defineCommands() {

        /*
         * Command for giving the player a rope.
         */

        define(
            name = "rope",
            privilege = Privilege.ADMIN,
            usage = "::rope",
            description = "Giving the player a rope"
        ) { p, _ ->
            addItem(p, Items.ROPE_954)
        }

        /*
         * Command for giving the player a spade.
         */

        define(
            name = "spade",
            privilege = Privilege.ADMIN,
            usage = "::spade",
            description = "Giving the player a spade"
        ) { p, _ ->
            addItem(p, Items.SPADE_952)
        }

        /*
         * Command for giving the player a knife.
         */

        define(
            name = "knife",
            privilege = Privilege.ADMIN,
            usage = "::knife",
            description = "Giving the player a knife"
        ) { p, _ ->
            addItem(p, Items.KNIFE_946)
        }

        /*
         * Command for giving the player a chisel.
         */

        define(
            name = "chisel",
            privilege = Privilege.ADMIN,
            usage = "::chisel",
            description = "Giving the player a chisel"
        ) { p, _ ->
            addItem(p, Items.CHISEL_1755)
        }

        /*
         * Command for providing a kit of various farming tools.
         */

        define(
            name = "farmkit",
            privilege = Privilege.ADMIN,
            usage = "::farmkit",
            description = "Provides a kit of various farming equipment.",
        ) { player, _ ->
            for (item in farmKit) {
                player.inventory.add(Item(item))
            }
            return@define
        }

        /*
         * Command for giving 1000 of each rune type.
         */

        define(
            name = "runekit",
            privilege = Privilege.ADMIN,
            usage = "::runekit",
            description = "Gives 1k of each Rune type.",
        ) { player, _ ->
            for (item in runeKit) {
                addItem(player, item, 1000)
            }
            return@define
        }
    }
}
