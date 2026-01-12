package content.region.karamja.quest.roots.npc

import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior

class WildJadeVineBehavior : NPCBehavior(*WildJadeVineNPC.WILD_JADE_NPC) {
    override fun getXpMultiplier(self: NPC, attacker: Entity): Double = 0.1
}