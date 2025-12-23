package content.global.skill.thieving.blackjack

import core.game.interaction.IntType
import core.game.interaction.InteractionListener

class BlackjackListener : InteractionListener {
    override fun defineListeners() {
        on(IntType.NPC, "lure") { player, node ->
            BlackjackService.lure(player, node.asNpc())
            return@on true
        }

        on(IntType.NPC, "knock-out") { player, node ->
            BlackjackService.knockOut(player, node.asNpc())
            return@on true
        }
    }
}