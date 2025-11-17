package content.global.skill.construction

import core.api.amountInInventory
import core.api.log
import core.api.openDialogue
import core.api.sendString
import core.game.interaction.InterfaceListener
import core.tools.Log
import shared.consts.Components
import shared.consts.Items

class BuildInterfaceListener : InterfaceListener {
    override fun defineInterfaceListeners() {

        onOpen(Components.POH_BUILD_SCREEN_402) { player, _ ->
            val coins = amountInInventory(player, Items.COINS_995)
            for ((amount, childId) in COINS_VALUE_TO_CHILD) {
                if (coins >= amount) sendString(player, core.tools.YELLOW + "$amount coins", Components.POH_BUILD_SCREEN_402, childId)
            }
            return@onOpen true
        }

        on(Components.POH_BUILD_SCREEN_402) { player, _, _, button, _, _, ->
            val index = button - 160
            log(javaClass, Log.FINE, "BuildRoom Interface Index: $index")
            if (index in RoomProperties.values().indices) {
                openDialogue(player, "con:room", RoomProperties.values()[index])
            }
            return@on true
        }
    }

    companion object {
        private val COINS_VALUE_TO_CHILD = arrayOf(
            100 to 138, 5000 to 139, 7500 to 147, 7500 to 155, 7500 to 156, 7500 to 157,
            10000 to 140, 15000 to 141, 25000 to 142, 50000 to 143, 50000 to 149, 50000 to 150,
            75000 to 145, 75000 to 152, 100000 to 144, 100000 to 151, 150000 to 146, 150000 to 153,
            150000 to 154, 250000 to 148, 250000 to 159
        )
    }
}