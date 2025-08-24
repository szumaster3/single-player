package content.region.kandarin.gnome.plugin

import core.api.*
import core.api.isQuestComplete
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.npc.NPC
import shared.consts.NPCs
import shared.consts.Quests
import shared.consts.Scenery

/**
 * Handles Gnome Spirit Tree transportation system.
 */
class SpiritTreePlugin : InteractionListener {

    private val spiritTrees = intArrayOf(Scenery.SPIRIT_TREE_1317, Scenery.SPIRIT_TREE_1293, Scenery.SPIRIT_TREE_1294)

    override fun defineListeners() {

        /*
         * Handles spirit tree options.
         */

        on(spiritTrees, IntType.SCENERY, "talk-to", "teleport") { player, _ ->
            if (!isQuestComplete(player, Quests.TREE_GNOME_VILLAGE)) {
                player.sendMessage("The tree doesn't feel like talking.")
                return@on true
            }
            openDialogue(player, SpiritTreeDialogue(true), NPC(NPCs.SPIRIT_TREE_3636))
            return@on true
        }
    }
}