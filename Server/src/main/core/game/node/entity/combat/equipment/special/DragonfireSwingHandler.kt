package core.game.node.entity.combat.equipment.special

import core.api.*
import core.game.container.impl.EquipmentContainer
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.CombatSwingHandler
import core.game.node.entity.combat.InteractionType
import core.game.node.entity.combat.equipment.SwitchAttack
import core.game.node.entity.impl.Projectile
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player

import core.game.node.item.Item
import core.game.world.GameWorld
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.tools.RandomFunction
import shared.consts.Items
import shared.consts.Sounds
import kotlin.math.floor
import kotlin.random.Random

open class DragonfireSwingHandler(private val meleeRange: Boolean, private val maximumHit: Int, private val attack: SwitchAttack?, private val fire: Boolean) : CombatSwingHandler(CombatStyle.MAGIC) {

    override fun canSwing(entity: Entity, victim: Entity): InteractionType? =
        if (meleeRange) CombatStyle.MELEE.swingHandler.canSwing(entity, victim)
        else CombatStyle.MAGIC.swingHandler.canSwing(entity, victim)

    override fun swing(entity: Entity?, victim: Entity?, state: BattleState?): Int {
        if (entity == null || victim == null || state == null) return 0

        val max = calculateHit(entity, victim, 1.0).coerceAtLeast(0)
        val hit = Random.nextInt(max + 1)
        state.maximumHit = max
        state.style = CombatStyle.MAGIC
        state.estimatedHit = hit

        if (meleeRange) return 1

        val distanceTicks = 2 + floor(entity.location.getDistance(victim.location) * 0.5).toInt()
        entity.setAttribute("fireBreath", GameWorld.ticks + (distanceTicks + 2))
        return distanceTicks
    }

    override fun visualize(entity: Entity, victim: Entity?, state: BattleState?) {
        attack?.let {
            visualize(entity, it.animation, it.startGraphic)
            it.projectile?.copy(entity, victim, 5.0)?.send()
        }
    }

    private fun handleDragonfireShieldAbsorb(p: Player, source: Entity): Boolean {
        val shield = p.equipment[EquipmentContainer.SLOT_SHIELD] ?: return false
        return when (shield.id) {
            Items.DRAGONFIRE_SHIELD_11284 -> {
                // degrade to lower-id shield with emptied charge, then continue as 11283.
                p.equipment.replace(Item(Items.DRAGONFIRE_SHIELD_11283), EquipmentContainer.SLOT_SHIELD)
                val newShield = p.equipment[EquipmentContainer.SLOT_SHIELD]
                newShield?.charge = 0
                // fall-through to handle charging below with the new shield.
                handleShieldCharging(p, source)
            }

            Items.DRAGONFIRE_SHIELD_11283 -> handleShieldCharging(p, source)
            else -> false
        }
    }

    private fun handleShieldCharging(p: Player, source: Entity): Boolean {
        val shield = p.equipment[EquipmentContainer.SLOT_SHIELD] ?: return false
        if (shield.charge < 1000) {
            shield.charge += 20
            EquipmentContainer.updateBonuses(p)
            sendMessage(p, "Your dragonfire shield glows more brightly.")
            playAudio(p, Sounds.DRAGONSLAYER_ABSORB_FIRE_3740)
            faceLocation(p, source.getCenterLocation())
            visualize(p, Animation(6695), Graphics(1163))
            return true
        } else {
            sendMessage(p, "Your dragonfire shield is already fully charged.")
            return true
        }
    }

    override fun visualizeImpact(entity: Entity?, victim: Entity?, state: BattleState?) {
        if (entity is NPC && victim is Player) {
            if (handleDragonfireShieldAbsorb(victim, entity)) return
        }
        if (!fire && victim != null && !hasTimerActive(victim, "frozen:immunity") && Random.nextInt(4) == 2) {
            registerTimer(victim, spawnTimer("frozen", 16, true))
            visualize(victim, null, Graphics(502))
        }

        val graphic = attack?.endGraphic
        victim!!.visualize(victim.properties.defenceAnimation, graphic)
    }

    override fun impact(entity: Entity?, victim: Entity?, state: BattleState?) {
        val st = state ?: return
        val est = st.estimatedHit
        if (est > -1) {
            victim?.impactHandler?.handleImpact(entity, est, CombatStyle.MAGIC, st)
        }
        val sec = st.secondaryHit
        if (sec > -1) {
            victim?.impactHandler?.handleImpact(entity, sec, CombatStyle.MAGIC, st)
        }
    }

    override fun adjustBattleState(entity: Entity, victim: Entity, state: BattleState) {
        if (victim.isPlayer && !fire) {
            val item = victim.asPlayer().equipment[EquipmentContainer.SLOT_SHIELD]
            if (item != null && (item.id == Items.ELEMENTAL_SHIELD_2890 || item.id == Items.MIND_SHIELD_9731) && state.estimatedHit > 10) {
                state.estimatedHit = RandomFunction.random(10)
            }
        }
        val currentStyle = state.style
        super.adjustBattleState(entity, victim, state)
        state.style = currentStyle
    }

    override fun getFormattedHit(attacker: Entity, victim: Entity, state: BattleState, rawHit: Int): Int =
        formatHit(victim, rawHit)

    override fun calculateAccuracy(entity: Entity?): Int = 4000

    override fun calculateHit(entity: Entity?, victim: Entity?, modifier: Double): Int {
        return calculateDragonFireMaxHit(victim ?: return 0, maximumHit, !fire, 0, true)
    }

    override fun calculateDefence(victim: Entity?, attacker: Entity?): Int =
        CombatStyle.MAGIC.swingHandler.calculateDefence(victim, attacker)

    override fun getSetMultiplier(e: Entity?, skillId: Int): Double = 1.0

    companion object {
        @JvmStatic
        fun get(meleeRange: Boolean, maximumHit: Int, animation: Animation?, startGraphic: Graphics?, endGraphic: Graphics?, projectile: Projectile?): SwitchAttack {
            val attack = SwitchAttack(null, animation, startGraphic, endGraphic, projectile).setUseHandler(true)
            attack.handler = DragonfireSwingHandler(meleeRange, maximumHit, attack, true)
            return attack
        }

        fun get(meleeRange: Boolean, maximumHit: Int, animation: Animation?, startGraphic: Graphics?, endGraphic: Graphics?, projectile: Projectile?, fire: Boolean): SwitchAttack {
            val attack = SwitchAttack(null, animation, startGraphic, endGraphic, projectile).setUseHandler(true)
            attack.handler = DragonfireSwingHandler(meleeRange, maximumHit, attack, fire)
            return attack
        }
    }
}