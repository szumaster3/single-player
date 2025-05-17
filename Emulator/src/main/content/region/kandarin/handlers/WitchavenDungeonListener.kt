package content.region.kandarin.handlers

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.Scenery

class WitchavenDungeonListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles clue scroll chest.
         */

        on(Scenery.CHEST_18304, IntType.SCENERY, "open") { _, node ->
            replaceScenery(node.asScenery(), Scenery.CHEST_18321, -1, node.location)
            return@on true
        }

        on(Scenery.CHEST_18321, IntType.SCENERY, "shut") { _, node ->
            replaceScenery(node.asScenery(), Scenery.CHEST_18304, -1, node.location)
            return@on true
        }
    }
}