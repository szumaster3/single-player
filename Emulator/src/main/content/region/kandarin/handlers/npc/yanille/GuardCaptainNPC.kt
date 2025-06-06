package content.region.kandarin.handlers.npc.yanille

import core.api.sendChat
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.NPCs

class GuardCaptainNPC : NPCBehavior(NPCs.GUARD_CAPTAIN_3109) {
    private var nextHiccup = 0L

    override fun tick(self: NPC): Boolean {
        val now = System.currentTimeMillis()
        if (now < nextHiccup || !self.isPlayerNearby(10)) return true

        if (RandomFunction.roll(15)) {
            sendChat(self, "*HIC*")
            nextHiccup = now + 10000L
        }
        return true
    }
}
