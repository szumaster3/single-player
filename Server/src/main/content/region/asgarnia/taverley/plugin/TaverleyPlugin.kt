package content.region.asgarnia.taverley.plugin

import content.region.asgarnia.taverley.dialogue.GaiusDialogue
import content.region.asgarnia.taverley.dialogue.JatixDialogue
import content.region.asgarnia.taverley.dialogue.LordDaquariusDialogue
import content.region.asgarnia.taverley.dialogue.TegidDialogue
import core.api.openDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.NPCs

class TaverleyPlugin : InteractionListener {

    override fun defineListeners() {
        /*
         * Handles talking to NPCs around city.
         */

        on(NPCs.TEGID_1213, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, TegidDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.GAIUS_586, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, GaiusDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.JATIX_587, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, JatixDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.LORD_DAQUARIUS_200, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, LordDaquariusDialogue(), node.asNpc())
            return@on true
        }
    }
}
