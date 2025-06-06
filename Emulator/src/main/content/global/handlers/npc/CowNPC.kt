package content.global.handlers.npc

import core.api.sendChat
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.NPCs

class CowNPC : NPCBehavior(NPCs.COW_81, NPCs.COW_397, NPCs.COW_955, NPCs.COW_1767, NPCs.COW_3309) {

    private var nextChat = 0L

    override fun tick(self: NPC): Boolean {
        val now = System.currentTimeMillis()
        if (now < nextChat) return true

        if (RandomFunction.roll(20)) {
            sendChat(self, "Moo")
            nextChat = now + 5000L
        }

        return true
    }
}