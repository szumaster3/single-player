package content.global.plugins.item

import content.data.consumables.effects.DwarvenRockCakeEffect
import core.api.sendMessage
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.Items

class DwarvenRockCakeOptionPlugin : InteractionListener {

    override fun defineListeners() {

        on(intArrayOf(Items.DWARVEN_ROCK_CAKE_7509, Items.DWARVEN_ROCK_CAKE_7510), IntType.ITEM, "eat") { player, _ ->
            val cakeEffect = DwarvenRockCakeEffect()
            cakeEffect.activate(player)
            sendMessage(player, "Ow! You nearly broke a tooth!")
            sendMessage(player, "The rock cake resists all attempts to eat it.")
            return@on true
        }
    }

}