package content.global.plugin.iface

import core.api.repositionChild
import core.game.interaction.InterfaceListener
import shared.consts.Components

/**
 * Handles the server-side repositioning of the interface for selecting options.
 */
class OptionInterface : InterfaceListener {

    override fun defineInterfaceListeners() {
        onOpen(Components.SELECT_AN_OPTION_140) { player, _ ->
            val indices = intArrayOf(0, 2, 3, 4, 5, 6)
            val xs = intArrayOf(23, 31, 234, 24, 123, 334)
            val ys = intArrayOf(5, 32, 32, 3, 36, 36)

            for (i in indices.indices) {
                repositionChild(player, Components.SELECT_AN_OPTION_140, indices[i], xs[i], ys[i])
            }
            return@onOpen true
        }

        onOpen(Components.DOUBLEOBJBOX_131) { player, _ ->
            repositionChild(player, Components.DOUBLEOBJBOX_131, 1, 96, 25)
            repositionChild(player, Components.DOUBLEOBJBOX_131, 3, 96, 98)
            return@onOpen true
        }
    }
}
