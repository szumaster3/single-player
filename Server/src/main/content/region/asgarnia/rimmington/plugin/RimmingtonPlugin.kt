package content.region.asgarnia.rimmington.plugin

import content.region.asgarnia.rimmington.dialogue.CustomsSergeantDialogue
import core.api.openDialogue
import core.api.replaceScenery
import core.api.sendMessage
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.Scenery

class RimmingtonPlugin : InteractionListener {


    override fun defineListeners() {
        on(Scenery.CUSTOMS_SERGEANT_31459, IntType.SCENERY, "talk-to") { player, _ ->
            if (player.location.x >= 2963) {
                openDialogue(player, CustomsSergeantDialogue())
            }
            return@on true
        }

        /*
         * Handles Rimmik store doors.
         */

        on(Scenery.DOOR_1534, IntType.SCENERY, "close", "open") { player, node ->
            if (node.location.x == 2950 && node.location.y == 3207) {
                sendMessage(player, "The doors appear to be stuck.")
                return@on false
            }
            DoorActionHandler.handleDoor(player, node.asScenery().wrapper)
            return@on true
        }

        /*
         * Handles opening wardrobe at Melzar's Maze.
         */

        on(Scenery.WARDROBE_33963, IntType.SCENERY, "open") { _, node ->
            replaceScenery(node.asScenery(), Scenery.WARDROBE_35133, -1)
            return@on true
        }

        on(Scenery.WARDROBE_35133, IntType.SCENERY, "close") { _, node ->
            replaceScenery(node.asScenery(), Scenery.WARDROBE_33963, -1)
            return@on true
        }

    }
}
