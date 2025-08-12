package content.data

import core.ServerStore
import core.api.hasAnItem
import core.api.sendMessage
import core.game.node.entity.player.Player
import shared.consts.Items

/**
 * A class that checks whether an item has been regenerated
 * after the next login (restart) or after the expiration of a given time.
 */
object GlobalStore {
    @JvmStatic
    fun check(player: Player) {
        val ringStore = ServerStore.getArchive("daily-explorer-ring")
        val hasExplorerRing =
            hasAnItem(
                player,
                Items.EXPLORERS_RING_1_13560,
                Items.EXPLORERS_RING_2_13561,
                Items.EXPLORERS_RING_3_13562,
            ).container != null
        val runValue = ringStore[player.username.lowercase() + "run"]?.asInt ?: 0
        val alchsValue = ringStore[player.username.lowercase() + "alchs"]?.asInt ?: 0

        if (hasExplorerRing &&
            (runValue != 0 || alchsValue == 30)
        ) {
            sendMessage(player, "Your explorer's ring has been recharged.")
        }
    }
}
