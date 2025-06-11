package content.region.asgarnia.taverley.plugin

import core.api.sendChat
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.NPCs

class SylasNPC : NPCBehavior(NPCs.SYLAS_5987) {
    private val forceChat = arrayOf(
        "Selling Magic Beans!",
        "You there, you look like you could use some beans!",
        "All beans must go!",
        "Buy 1 bean get a bean free!",
        "Selling bags of Magic Beans!",
    )

    override fun onCreation(self: NPC) {
        self.walkRadius = 5
    }

    private var nextChat = 0L

    override fun tick(self: NPC): Boolean {
        val now = System.currentTimeMillis()
        if (now < nextChat || !self.isPlayerNearby(15)) {
            return true
        }

        if (RandomFunction.roll(8)) {
            sendChat(self, forceChat.random())
            nextChat = now + 15000L
        }

        return true
    }
}
