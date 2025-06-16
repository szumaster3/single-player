package content.global.plugin.npc

import core.api.animate
import core.api.sendChat
import core.api.stopWalk
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.Animations
import org.rs.consts.NPCs

class TownCrierNPC : NPCBehavior(
    NPCs.TOWN_CRIER_6135,
    NPCs.TOWN_CRIER_6136,
    NPCs.TOWN_CRIER_6137,
    NPCs.TOWN_CRIER_6138,
    NPCs.TOWN_CRIER_6139
) {
    private companion object {
        private const val FORCE_CHAT_CHANCE = 150

        private val forceChatAnimation = mapOf(
            "The Duke of Lumbridge needs a hand." to Animations.TOWN_CRIER_RING_BELL_6865,
            "The squirrels! The squirrels are coming! Noooo, get them out of my head!" to Animations.TOWN_CRIER_SCRATCHES_HEAD_6863
        )

        private val forceChatList = forceChatAnimation.entries.toList()
    }

    override fun tick(self: NPC): Boolean {
        if (RandomFunction.random(FORCE_CHAT_CHANCE) == 0) {
            doForceChat(self)
        }
        return super.tick(self)
    }

    private fun doForceChat(self: NPC) {
        stopWalk(self)
        val (chat, animation) = forceChatList.random()
        animate(self, animation)
        sendChat(self, chat)
    }
}