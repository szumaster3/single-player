package content.region.asgarnia.handlers.burthope

import core.api.sendChat
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import org.rs.consts.NPCs

class LidioNPC  : NPCBehavior(NPCs.LIDIO_4293) {
    private val forceChat =
        arrayOf(
            "Potatoes are filling and healthy too!",
            "Come try my lovely pizza or maybe some fish!",
            "Stew to fill the belly, on sale here!"
        )

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