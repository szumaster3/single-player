package content.region.kandarin.yanille.npc

import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.npc.AbstractNPC
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.RandomFunction
import shared.consts.NPCs

/**
 * Represents the Salarin Twisted npc.
 */
@Initializable
class SalarinTwistedNPC : AbstractNPC {

    companion object {
        private val SPELL_IDS = setOf(1, 4, 6, 8)
    }

    constructor() : super(-1, null)

    constructor(id: Int, location: Location) : super(id, location) {
        isAggressive = true
        isWalks = true
        isNeverWalks = false
    }

    override fun construct(id: Int, location: Location, vararg objects: Any?): AbstractNPC =
        SalarinTwistedNPC(id, location)

    override fun checkImpact(state: BattleState) {
        if (state.style != CombatStyle.MAGIC || state.spell == null) {
            if (RandomFunction.random(100) < 50) {
                sendChat("Your pitiful attacks cannot hurt me!")
            }
            state.neutralizeHits()
            return
        }

        if (state.spell.spellId in SPELL_IDS) {
            state.estimatedHit = state.maximumHit
        } else {
            state.neutralizeHits()
        }
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.SALARIN_THE_TWISTED_205)
}
