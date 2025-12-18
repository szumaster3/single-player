package content.global.skill.hunter

import core.api.getStatLevel
import core.game.node.entity.npc.NPC
import core.game.node.entity.skill.Skills
import core.game.node.item.Item

open class BoxTrapNode(npcIds: IntArray, level: Int, experience: Double, rewards: Array<Item>, private val summoningLevel: Int) :
    TrapNode(
        npcIds     = npcIds,
        level      = level,
        experience = experience,
        objectIds  = intArrayOf(19188, 19189),
        rewards    = rewards
    )
{

    override fun canCatch(wrapper: TrapWrapper, npc: NPC): Boolean {
        if (getStatLevel(wrapper.player, Skills.SUMMONING) < summoningLevel) {
            return false
        }
        return super.canCatch(wrapper, npc)
    }
}
