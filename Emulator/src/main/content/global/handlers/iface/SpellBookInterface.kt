package content.global.handlers.iface

import core.api.setVarbit
import core.game.interaction.InterfaceListener
import org.rs.consts.Components

class SpellBookInterface : InterfaceListener {

    override fun defineInterfaceListeners() {
        on(Components.MAGIC_192) { player, _, _, buttonID, _, _ ->
            val varbitValue = when (buttonID) {
                64 -> 0
                65 -> 1
                66 -> 2
                else -> return@on false
            }
            setVarbit(player, 1376, varbitValue)
            return@on true
        }
    }
}