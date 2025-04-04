package content.region.kandarin.quest.merlin.handlers

import core.api.quest.hasRequirement
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.Player
import core.game.world.map.Location
import core.plugin.Initializable
import org.rs.consts.NPCs
import org.rs.consts.Quests

/**
 * Represents the Merlin NPC (Camelot Castle).
 */
@Initializable
class MerlinNPC : AbstractNPC {
    constructor() : super(0, null)

    private constructor(id: Int, location: Location) : super(id, location)

    override fun construct(
        id: Int,
        location: Location,
        vararg objects: Any,
    ): AbstractNPC = MerlinNPC(id, location)

    override fun isHidden(player: Player): Boolean = !hasRequirement(player, Quests.HOLY_GRAIL, false)

    override fun getIds(): IntArray = intArrayOf(NPCs.MERLIN_213)
}