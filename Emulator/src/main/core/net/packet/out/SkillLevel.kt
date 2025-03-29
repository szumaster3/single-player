package core.net.packet.out

import core.game.node.entity.skill.Skills
import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.SkillContext
import kotlin.math.ceil

/**
 * The type Skill level.
 */
class SkillLevel : OutgoingPacket<SkillContext> {
    override fun send(context: SkillContext) {
        val buffer = IoBuffer(38)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        val skills = context.player.getSkills()
        if (context.skillId == Skills.PRAYER) {
            buffer.putA(ceil(skills.prayerPoints).toInt())
        } else if (context.skillId == Skills.HITPOINTS) {
            buffer.putA(skills.lifepoints)
        } else {
            buffer.putA(skills.getLevel(context.skillId, true))
        }
        buffer.putIntA(skills.getExperience(context.skillId).toInt()).put(context.skillId)
        context.player.details.session.write(buffer)
    }
}