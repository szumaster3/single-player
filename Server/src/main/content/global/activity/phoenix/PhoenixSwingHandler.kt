package content.global.activity.phoenix

import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.CombatSwingHandler
import core.game.node.entity.combat.InteractionType
import core.game.node.entity.impl.Projectile
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.world.GameWorld
import core.game.world.map.RegionManager
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction

class PhoenixSwingHandler : CombatSwingHandler(CombatStyle.MAGIC) {

    companion object {
        private val MAGIC_ATTACK = Animation(11076)
        private val DUST_ATTACK  = Animation(11093)

        private const val MAX_HIT = 25
        private const val SPECIAL_CHANCE = 20
        private const val SPECIAL_COOLDOWN = 30
        private const val DUST_RADIUS = 5
        private const val MAGIC_RANGE = 10

        private const val DUST_FLAG = "phoenix:dust"
        private const val DUST_CD   = "phoenix:dust_cd"
    }

    override fun canSwing(entity: Entity, victim: Entity): InteractionType? {
        if (!isProjectileClipped(entity, victim, false)) {
            return InteractionType.NO_INTERACT
        }

        return if (
            victim.centerLocation.withinMaxnormDistance(
                entity.centerLocation,
                getCombatDistance(entity, victim, MAGIC_RANGE)
            )
        ) InteractionType.STILL_INTERACT else InteractionType.NO_INTERACT
    }

    override fun swing(entity: Entity?, victim: Entity?, state: BattleState?): Int {
        if (entity !is NPC || victim == null || state == null) return 0

        state.style = CombatStyle.MAGIC

        val currentTick = GameWorld.ticks
        val nextSpecial = entity.getAttribute(DUST_CD, 0)

        val useSpecial =
            currentTick >= nextSpecial &&
                    RandomFunction.random(100) < SPECIAL_CHANCE

        if (useSpecial) {
            entity.setAttribute(DUST_CD, currentTick + SPECIAL_COOLDOWN)
            entity.setAttribute(DUST_FLAG, true)

            state.estimatedHit = 0
            return 4
        }

        entity.removeAttribute(DUST_FLAG)

        if (!isAccurateImpact(entity, victim)) {
            state.estimatedHit = 0
            return 3
        }

        val hit = RandomFunction.random(MAX_HIT + 1)
        state.maximumHit = MAX_HIT
        state.estimatedHit = hit
        return 3
    }

    override fun visualize(entity: Entity, victim: Entity?, state: BattleState?) {
        if (entity.getAttribute(DUST_FLAG, false)) {
            entity.animate(DUST_ATTACK)
            applyDustAttack(entity as NPC)
            return
        }

        entity.animate(MAGIC_ATTACK)
        if (victim != null) {
            Projectile.magic(entity, victim, 1976, 42, 36, 45, 5).send()
        }
    }

    override fun impact(entity: Entity?, victim: Entity?, state: BattleState?) {
        if (entity == null || victim == null || state == null) return
        if (entity.getAttribute(DUST_FLAG, false)) return

        if (state.estimatedHit > 0) {
            state.style.swingHandler.impact(entity, victim, state)
        }
    }

    override fun visualizeImpact(entity: Entity?, victim: Entity?, state: BattleState?) {
        if (victim != null && state?.estimatedHit ?: 0 > 0) {
            victim.animate(victim.properties.defenceAnimation)
        }
    }

    private fun applyDustAttack(phoenix: NPC) {
        for (player in RegionManager.getLocalPlayers(phoenix, DUST_RADIUS)) {
            if (player !is Player) continue

            drainSkill(player, Skills.ATTACK)
            drainSkill(player, Skills.RANGE)
            drainSkill(player, Skills.MAGIC)
        }
    }

    private fun drainSkill(player: Player, skill: Int) {
        val drain = (3 + player.skills.getLevel(skill) / 14).coerceAtMost(10)
        player.skills.updateLevel(skill, -drain, player.skills.getStaticLevel(skill) - drain)
    }

    override fun calculateAccuracy(entity: Entity?) = CombatStyle.MAGIC.swingHandler.calculateAccuracy(entity)

    override fun calculateDefence(victim: Entity?, attacker: Entity?) = CombatStyle.MAGIC.swingHandler.calculateDefence(victim, attacker)

    override fun calculateHit(entity: Entity?, victim: Entity?, modifier: Double) = MAX_HIT

    override fun getSetMultiplier(e: Entity?, skillId: Int) = CombatStyle.MAGIC.swingHandler.getSetMultiplier(e, skillId)
}