package content.global.skill.crafting.spinning

import content.global.skill.crafting.CraftingObjects
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.item.Item
import shared.consts.Animations
import shared.consts.Components
import shared.consts.Items

class SpinningPlugin : InteractionListener, InterfaceListener {

    override fun defineListeners() {

        /*
         * Handles interaction with spinning wheel.
         */

        on(CraftingObjects.SPINNING_WHEEL, IntType.SCENERY, "spin") { player, _ ->
            openInterface(player, Components.CRAFTING_SPINNING_459)
            return@on true
        }

        /*
         * Handles creating golden wool.
         */

        onUseWith(IntType.SCENERY, Items.GOLDEN_FLEECE_3693, *CraftingObjects.SPINNING_WHEEL) { player, _, _ ->
            if (removeItem(player, Items.GOLDEN_FLEECE_3693)) {
                addItem(player, Items.GOLDEN_WOOL_3694)
                animate(player, Animations.OLD_COOK_RANGE_896)
                sendDialogue(player, "You spin the Golden Fleece into a ball of Golden Wool.")
            }
            return@onUseWith true
        }
    }

    override fun defineInterfaceListeners() {
        on(Components.CRAFTING_SPINNING_459) { player, _, opcode, buttonID, _, _ ->
            val spin = Spinning.forId(buttonID) ?: return@on true
            if (!inInventory(player, spin.need, 1)) {
                sendMessage(player, "You need " + getItemName(spin.need).lowercase() + " to make this.")
                return@on true
            }
            var amt = -1
            when (opcode) {
                155 -> amt = 1
                196 -> amt = 5
                124 -> amt = player.inventory.getAmount(Item(spin.need))
                199 ->
                    sendInputDialogue(player, true, "Enter the amount:") { value: Any ->
                        if (value is String) {
                            submitIndividualPulse(
                                entity = player,
                                pulse = SpinningPulse(player, Item(spin.need, 1), value.toInt(), spin),
                            )
                        } else {
                            submitIndividualPulse(
                                entity = player,
                                pulse = SpinningPulse(player, Item(spin.need, 1), value as Int, spin),
                            )
                        }
                    }
            }
            if (opcode == 199) {
                return@on true
            }
            submitIndividualPulse(
                entity = player,
                pulse = SpinningPulse(player, Item(spin.need, 1), amt, spin)
            )
            return@on true
        }
    }
}
