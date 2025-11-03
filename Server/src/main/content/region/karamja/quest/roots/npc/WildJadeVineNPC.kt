package content.region.karamja.quest.roots.npc

import content.data.GameAttributes
import core.api.clearLogoutListener
import core.api.getQuestStage
import core.api.setQuestStage
import core.game.node.entity.Entity
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.Player
import core.game.world.map.Location
import shared.consts.NPCs
import shared.consts.Quests

class WildJadeVineNPC(id: Int = NPCs.WILD_JADE_VINE_3409,location: Location? = Location.create(2639, 3313, 2)) : AbstractNPC(id, location) {
    var target: Player? = null

    override fun construct(id: Int, location: Location, vararg objects: Any): AbstractNPC = WildJadeVineNPC(id, location)

    override fun getIds(): IntArray = intArrayOf(NPCs.WILD_JADE_VINE_3409)

    init {
        isWalks = false
        isRespawn = false
        isAggressive = true
    }

    override fun finalizeDeath(killer: Entity) {
        super.finalizeDeath(killer)

        if (killer is Player) {
            if (getQuestStage(killer, Quests.BACK_TO_MY_ROOTS) == 7) {
                killer.unlock()
                setQuestStage(killer, Quests.BACK_TO_MY_ROOTS, 8)
                clearLogoutListener(killer, GameAttributes.VINE_FIGHT)
            }
        }
    }
}