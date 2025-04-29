package content.region.kandarin.miniquest.zaros

import content.data.GameAttributes
import content.global.skill.summoning.familiar.BurdenBeast
import core.api.*
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.world.map.Location
import org.rs.consts.NPCs

/**
 * The Curse of Zaros (miniquest).
 */
object CurseOfZaros {

    /**
     * Checks if the player has completed the miniquest related to Zaros.
     *
     * @param player The player to check.
     * @return `true` if the player has completed the miniquest; `false` otherwise.
     */
    fun hasComplete(player: Player): Boolean = getAttribute(player, GameAttributes.ZAROS_COMPLETE, false)

    /**
     * Checks if the player has a specific item in their inventory, equipment, or familiar's container.
     *
     * @param player The player to check.
     * @param item The item ID to check for.
     * @return `true` if the player has the item; `false` otherwise.
     */
    fun hasItems(
        player: Player,
        item: Int,
    ): Boolean {
        val familiar = player.familiarManager.familiar as? BurdenBeast
        return familiar?.container?.containItems(item) == true ||
            inInventory(player, item) ||
            inEquipment(player, item) ||
            inBank(player, item)
    }
}
