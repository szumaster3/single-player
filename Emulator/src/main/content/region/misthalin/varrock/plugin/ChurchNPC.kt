package content.region.misthalin.varrock.plugin

import core.api.visualize
import core.game.node.entity.npc.AbstractNPC
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.RandomFunction
import org.rs.consts.NPCs

@Initializable
class ChurchNPC(
    id: Int = 0,
    location: Location? = null
) : AbstractNPC(id, location) {

    override fun construct(id: Int, location: Location, vararg objects: Any): AbstractNPC =
        ChurchNPC(id, location)

    override fun getIds(): IntArray = intArrayOf(
        NPCs.MARTINA_SCORSBY_3326,
        NPCs.JEREMY_CLERKSIN_3327
    )

    override fun handleTickActions() {
        if (RandomFunction.roll(8)) {
            visualize(this,-1, 1056)
        }
    }
}
