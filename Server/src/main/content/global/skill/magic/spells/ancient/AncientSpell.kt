package content.global.skill.magic.spells.ancient

import core.api.*
import core.game.container.impl.EquipmentContainer
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.spell.CombatSpell
import core.game.node.entity.combat.spell.SpellType
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager.SpellBook
import core.game.node.entity.skill.Skills
import core.plugin.Initializable
import core.plugin.Plugin
import shared.consts.Animations
import shared.consts.Items

@Initializable
class AncientSpell private constructor(private val definition: AncientSpellDefinition) :
    CombatSpell(
        definition.type,
        SpellBook.ANCIENT,
        definition.level,
        definition.xp,
        definition.castSound,
        definition.impactSound,
        definition.anim,
        definition.start,
        definition.projectile,
        definition.end,
        *definition.runes
    ) {

    constructor() : this(AncientSpellDefinition.SMOKE_RUSH)

    override fun newInstance(type: SpellType?): Plugin<SpellType?> {
        AncientSpellDefinition.values().forEach {
            SpellBook.ANCIENT.register(it.button, AncientSpell(it))
        }
        return this
    }

    override fun visualize(entity: Entity, target: Node) {
        entity.graphics(graphics)
        projectile?.transform(entity, target as Entity, false, 58, 10)?.send()
        entity.animate(animation)
        audio?.let { playGlobalAudio(entity.location, it.id, 20) }
    }

    override fun fireEffect(entity: Entity, victim: Entity, state: BattleState) {
        when {
            definition.name.startsWith("BLOOD") -> {
                if (state.estimatedHit > -1) {
                    val heal = state.estimatedHit / 4
                    if (heal > 0) {
                        entity.skills.heal(heal)
                        if (entity is Player) {
                            sendMessage(entity, "You drain some of your opponent's health.")
                        }
                    }
                }
            }
            definition.name.startsWith("ICE") -> {
                if (!hasTimerActive(victim, "frozen:immunity")) {
                    val ticks = (definition.type.ordinal - SpellType.RUSH.ordinal + 1) * 8
                    registerTimer(victim, spawnTimer("frozen", ticks, true))
                    if (definition.type == SpellType.BARRAGE) state.isFrozen = true
                }
            }
            definition.name.startsWith("MIASMIC") -> {
                if (!hasTimerActive(victim, "miasmic:immunity")) {
                    registerTimer(victim, spawnTimer("miasmic", (definition.button - 15) * 20))
                }
            }
            definition.name.startsWith("SHADOW") -> {
                if (state.estimatedHit > -1) {
                    val level = victim.skills.getStaticLevel(Skills.ATTACK)
                    victim.skills.updateLevel(Skills.ATTACK, -(level * 0.1).toInt(), (level - (level * 0.1)).toInt())
                }
            }
            definition.name.startsWith("SMOKE") -> {
                if (state.estimatedHit > -1) {
                    applyPoison(victim, entity, if (definition.type.ordinal >= SpellType.BLITZ.ordinal) 4 else 2)
                }
            }
        }
    }

    private fun shouldUseMultiHit(entity: Entity, target: Entity, isSingleTarget: Boolean): Boolean {
        if (!entity.properties.isMultiZone || !target.properties.isMultiZone) return false
        return !isSingleTarget
    }

    override fun getTargets(entity: Entity, target: Entity): Array<BattleState> {
        val isSingleTarget =
            when (definition.type) {
                SpellType.RUSH,
                SpellType.BLITZ -> true
                else -> animation?.id == Animations.CAST_SPELL_1978
            }

        if (!shouldUseMultiHit(entity, target, isSingleTarget)) {
            return super.getTargets(entity, target)
        }

        val list = getMultihitTargets(entity, target, 9)
        return list.map { BattleState(entity, it) }.toTypedArray()
    }

    override fun getMaximumImpact(entity: Entity, victim: Entity, state: BattleState): Int {
        val add =
            when {
                definition.name.startsWith("BLOOD") -> 3
                definition.name.startsWith("SHADOW") -> 2
                definition.name.startsWith("ICE") -> 4
                definition.name.startsWith("MIASMIC") ->
                    when (definition.type) {
                        SpellType.RUSH -> 4
                        SpellType.BURST,
                        SpellType.BLITZ -> 6
                        else -> 9
                    }
                definition.name.startsWith("SMOKE") -> 1
                else -> 0
            }
        return definition.type.getImpactAmount(entity, victim, add)
    }

    override fun cast(entity: Entity, target: Node): Boolean {
        if (definition.name.startsWith("MIASMIC")) {
            val player = entity as? Player
            val weapon = player?.equipment?.getNew(EquipmentContainer.SLOT_WEAPON)
            val valid =
                intArrayOf(Items.ZURIELS_STAFF_13867, Items.ZURIELS_STAFF_DEG_13869, Items.NULL_13841, Items.NULL_13843)
                    .any { it == weapon?.id }
            if (!valid && core.game.world.GameWorld.settings?.isDevMode != true) {
                player
                    ?.packetDispatch
                    ?.sendMessage("You need to be wielding Zuriel's staff in order to cast this spell.")
                return false
            }
        }
        if (!meetsRequirements(entity, true, false)) return false
        return super.cast(entity, target)
    }
}