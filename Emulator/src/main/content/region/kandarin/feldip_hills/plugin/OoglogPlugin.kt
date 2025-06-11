package content.region.kandarin.feldip_hills.plugin

import content.region.kandarin.feldip_hills.dialogue.ooglog.GrimechinDialogue
import core.api.openDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.Scenery

class OoglogPlugin : InteractionListener {

    companion object {
        private const val GRIMECHIN = Scenery.GRIMECHIN_29106
    }

    override fun defineListeners() {
        on(GRIMECHIN, IntType.SCENERY, "Talk-to") { player, _ ->
            openDialogue(player, GrimechinDialogue())
            return@on true
        }
    }
}
