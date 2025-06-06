package content.global.handlers.npc

import core.api.animate
import core.api.sendChat
import core.api.stopWalk
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.Animations
import org.rs.consts.NPCs

class TownCrierNPC : NPCBehavior(*ID) {

    private val forceChatAnimation = listOf(
        "The Duke of Lumbridge needs a hand." to Animations.TOWN_CRIER_RING_BELL_6865,
        "The squirrels! The squirrels are coming! Noooo, get them out of my head!" to Animations.TOWN_CRIER_SCRATCHES_HEAD_6863
    )

    private var nextChat = 0L

    override fun tick(self: NPC): Boolean {
        val now = System.currentTimeMillis()
        if (now < nextChat) return super.tick(self)

        if (RandomFunction.random(300) == 5) {
            val (chat, animation) = forceChatAnimation.random()

            stopWalk(self)
            animate(self, animation)
            sendChat(self, chat)

            nextChat = now + 7000L
        }

        return super.tick(self)
    }

    companion object {
        private val ID = intArrayOf(
            NPCs.TOWN_CRIER_6135,
            NPCs.TOWN_CRIER_6136,
            NPCs.TOWN_CRIER_6137,
            NPCs.TOWN_CRIER_6138,
            NPCs.TOWN_CRIER_6139,
        )
    }
}
