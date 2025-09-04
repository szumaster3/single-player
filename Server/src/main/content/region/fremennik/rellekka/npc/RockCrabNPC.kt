package content.region.fremennik.rellekka.npc

import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.npc.agg.AggressiveBehavior
import core.game.node.entity.npc.agg.AggressiveHandler
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.RandomFunction
import shared.consts.NPCs

/**
 * Represents a Rock Crab NPC.
 */
@Initializable
class RockCrabNPC(id: Int = -1, location: Location? = null) : AbstractNPC(id, location, false) {

    private var aggressor = false
    private var target: Entity? = null

    init {
        if (id != -1 && location != null) {
            aggressiveHandler = AggressiveHandler(this, AGGRO_BEHAVIOR)
            isAggressive = true
            isWalks = false
        }
    }

    override fun onAttack(target: Entity) {
        this.aggressor = true
        this.target = target
        if (id == originalId) {
            transform(originalId - 1)
        }
    }

    override fun handleTickActions() {
        super.handleTickActions()
        val targetEntity = target
        if (aggressor && !inCombat() && (targetEntity?.location?.getDistance(location) ?: 0.0) > 12 || isInvisible) {
            reTransform()
            aggressor = false
            target = null
            walkingQueue.reset()
            isWalks = false
        }
    }

    override fun sendImpact(state: BattleState) {
        val hit = state.estimatedHit
        if ((hit >= 3 && RandomFunction.random(30) != 5) || (hit == 2 && RandomFunction.random(30) != 5)) {
            state.estimatedHit = 0
        }
    }

    override fun construct(id: Int, location: Location, vararg objects: Any): AbstractNPC {
        return RockCrabNPC(id, location)
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.ROCKS_1266, NPCs.ROCKS_1268, NPCs.BOULDER_2453, NPCs.ROCK_2890)

    companion object {
        private val AGGRO_BEHAVIOR = object : AggressiveBehavior() {
            override fun ignoreCombatLevelDifference(): Boolean = true

            override fun canSelectTarget(entity: Entity, target: Entity): Boolean {
                return super.canSelectTarget(entity, target) &&
                        entity.location.withinDistance(target.location, 3)
            }
        }
    }
}
