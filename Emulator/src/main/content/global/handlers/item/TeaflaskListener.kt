package content.global.handlers.item

import content.data.consumables.effects.NettleTeaEffect
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import org.rs.consts.Animations
import org.rs.consts.Items

class TeaflaskListener : InteractionListener {
    private val CUP_OF_TEA = intArrayOf(Items.CUP_OF_TEA_712, Items.CUP_OF_TEA_1978)
    private val MAX_CHARGE = 6000
    private val CHARGE_PER_CUP = 1000

    override fun defineListeners() {

        /*
         * Handles interaction with tea flask.
         */

        on(Items.TEA_FLASK_10859, IntType.ITEM, "drink", "look-in") { player, node ->
            val flask = node as Item
            val charges = getCharge(flask)

            when (getUsedOption(player)) {
                "drink" -> {
                    if (charges < CHARGE_PER_CUP) {
                        sendMessage(player, "The tea flask is empty.")
                        return@on true
                    }

                    lock(player, 1)
                    animate(player, Animations.USE_TEA_FLASK_5827)
                    adjustCharge(flask, -CHARGE_PER_CUP)
                    NettleTeaEffect().activate(player)
                    sendMessage(player, "You take a drink from the flask...")
                    sendChat(player, "Ahh, tea is so refreshing.", 1)
                }

                "look-in" -> {
                    val cupCount = (charges / CHARGE_PER_CUP).coerceIn(0, 5)
                    val cup = if (cupCount == 1) "cup" else "cups"
                    sendItemDialogue(player, flask.id, "The tea flask holds: $cupCount $cup of tea.")
                }
            }

            return@on true
        }

        /*
         * Handles filling empty cups with tea using tea flask.
         */

        onUseWith(IntType.ITEM, Items.TEA_FLASK_10859, Items.EMPTY_CUP_1980) { player, used, with ->
            val flask = used as Item
            val charges = getCharge(flask)

            if (charges < CHARGE_PER_CUP) {
                sendMessage(player, "The tea flask is empty.")
                return@onUseWith true
            }

            setCharge(flask, charges - CHARGE_PER_CUP)
            replaceSlot(player, with.asItem().slot, Item(Items.CUP_OF_TEA_712))
            sendMessage(player, "You fill the cup with tea.")
            return@onUseWith true
        }

        /*
         * Handles Add tea to flask.
         */

        onUseWith(IntType.ITEM, CUP_OF_TEA, Items.TEA_FLASK_10859) { player, used, with ->
            val flask = with as Item
            val charges = getCharge(flask)

            if (charges >= MAX_CHARGE) {
                sendMessage(player, "The tea flask is already full.")
                return@onUseWith true
            }

            replaceSlot(player, used.asItem().slot, Item(Items.EMPTY_CUP_1980))
            adjustCharge(flask, CHARGE_PER_CUP)
            sendMessage(player, "You add the tea to the flask.")
            return@onUseWith true
        }
    }
}
