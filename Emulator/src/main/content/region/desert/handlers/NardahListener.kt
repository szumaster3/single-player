package content.region.desert.handlers

import core.api.getUsedOption
import core.api.interaction.openNpcShop
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.NPCs

class NardahListener : InteractionListener {
    override fun defineListeners() {

        /*
         * Handles interaction with Kazemde and Rokuh NPCs.
         */

        on(intArrayOf(NPCs.KAZEMDE_3039, NPCs.ROKUH_3045), IntType.NPC, "talk-to", "trade") { player, node ->
            when (getUsedOption(player)) {
                "trade" -> openNpcShop(player, node.id)
                "talk-to" -> player.dialogueInterpreter.open(node.id)
            }
            return@on true
        }
    }
}
