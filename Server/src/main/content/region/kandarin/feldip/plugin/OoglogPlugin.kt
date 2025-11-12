package content.region.kandarin.feldip.plugin

import content.region.kandarin.feldip.dialogue.GrimechinDialogue
import core.api.openDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.Scenery

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
