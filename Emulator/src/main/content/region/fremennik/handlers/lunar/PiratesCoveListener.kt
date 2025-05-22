package content.region.fremennik.handlers.lunar

import core.api.addItem
import core.api.freeSlots
import core.api.replaceScenery
import core.api.sendMessage
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.tools.RandomFunction
import org.rs.consts.Items
import org.rs.consts.Scenery

class PiratesCoveListener : InteractionListener {
    override fun defineListeners() {

        /*
         * Handles taking apples form barrel.
         */

        on(FULL_BARREL, IntType.SCENERY, "take-from") { player, node ->
            val incrementAmount = RandomFunction.random(83, 1000)

            if (freeSlots(player) < 1) {
                sendMessage(player, "Not enough space in your inventory.")
                return@on true
            }

            val scenery = node.asScenery()
            if (scenery.charge >= 0) {
                scenery.charge -= incrementAmount

                when (node.id) {
                    Scenery.BARREL_16884 -> addItem(player, Items.ROTTEN_APPLE_1984)
                    Scenery.BARREL_16885 -> addItem(player, Items.COOKING_APPLE_1955)
                    Scenery.TAR_BARREL_16860 -> addItem(player, Items.SWAMP_TAR_1939)
                }

                if (scenery.charge <= 0) {
                    sendMessage(player, "The barrel became empty!")
                    when (node.id) {
                        Scenery.TAR_BARREL_16860 -> replaceScenery(scenery, Scenery.TAR_BARREL_16688, 38)
                        Scenery.BARREL_16884, Scenery.BARREL_16885 -> replaceScenery(scenery, EMPTY_BARREL, 38)
                    }
                    scenery.charge = 1000
                }
            }
            return@on true
        }
    }

    companion object {
        private val FULL_BARREL = intArrayOf(Scenery.TAR_BARREL_16860, Scenery.BARREL_16884, Scenery.BARREL_16885)
        private const val EMPTY_BARREL = Scenery.BARREL_16886
    }
}
