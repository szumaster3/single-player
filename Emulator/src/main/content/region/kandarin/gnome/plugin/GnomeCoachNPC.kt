package content.region.kandarin.gnome.plugin

import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.world.map.Location
import org.rs.consts.NPCs

class GnomeCoachNPC : NPCBehavior(NPCs.GNOME_COACH_2802) {

    override fun onCreation(self: NPC) {
        val movementPath = arrayOf(
            Location.create(2392, 3498, 0),
            Location.create(2398, 3498, 0),
            Location.create(2403, 3498, 0),
            Location.create(2397, 3498, 0),
            Location.create(2391, 3498, 0)
        )
        self.configureMovementPath(*movementPath)
        self.isWalks = true
    }
}
