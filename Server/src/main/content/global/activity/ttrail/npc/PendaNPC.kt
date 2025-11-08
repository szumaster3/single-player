package content.global.activity.ttrail.npc

import core.api.hasAnItem
import core.api.produceGroundItem
import core.game.node.entity.Entity
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.map.Location
import core.plugin.Initializable
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Penda NPC (Burthope).
 */
@Initializable
class PendaNPC(id: Int = 0, location: Location? = null) : AbstractNPC(id, location) {

    override fun construct(id: Int, location: Location, vararg objects: Any): AbstractNPC = PendaNPC(id, location)

    /**
     * Handles drop clue key (medium) required to open locked chest (Treasure Trail).
     */
    override fun finalizeDeath(killer: Entity?) {
        val player = killer as? Player ?: return super.finalizeDeath(killer)

        val hasKey = hasAnItem(player, Items.KEY_2836)?.container != null
        val hasClue = player.inventory.containsItem(Item(Items.CLUE_SCROLL_10236, 1))


        if (!hasKey && hasClue) {
            produceGroundItem(player, Items.KEY_2836, 1, location)
        }

        super.finalizeDeath(player)
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.PENDA_1087)
}
