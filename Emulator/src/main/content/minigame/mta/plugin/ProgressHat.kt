package content.minigame.mta.plugin

import core.api.*
import core.game.node.entity.player.Player
import core.game.node.item.Item
import org.rs.consts.Items

/**
 * Handles the interaction with progress hat.
 */
object ProgressHat {

    /**
     * An array of progress hat item ids.
     */
    val hatIds = intArrayOf(
        Items.PROGRESS_HAT_6885, Items.PROGRESS_HAT_6886, Items.PROGRESS_HAT_6887
    )

    /**
     * Varbit ids corresponding to pizazz slots.
     */
    private val pizazzVarbitIds = intArrayOf(1485, 1489, 1488, 1486)

    /**
     * Updates the player progress hat based on their total pizazz points.
     */
    @JvmStatic
    fun progress(player: Player) {
        val totalPoints = pizazzVarbitIds.sumOf { getVarbit(player, it) }

        val hatToAdd = when {
            totalPoints > 600 -> Items.PROGRESS_HAT_6887
            totalPoints in 301..600 -> Items.PROGRESS_HAT_6886
            else -> Items.PROGRESS_HAT_6885
        }

        hatIds.forEach { hat ->
            if (inInventory(player, hat)) {
                removeItem(player, hat)
            }
        }
        addItem(player, hatToAdd, 1)
        sendMessage(player, "Your progress hat updates to reflect your achievements.")
    }

    /**
     * Resets the player's pizazz points stored in varbits.
     */
    @JvmStatic
    fun resetProgress(player: Player): Boolean {
        pizazzVarbitIds.forEach { varbitId ->
            val currentPoints = getVarbit(player, varbitId)
            if (currentPoints > 0) {
                setVarbit(player, varbitId, 0, true)
            }
        }
        sendDialogueLines(
            player,
            "The hat whispers as you destroy it. You can get another from the",
            "Entrance Guardian."
        )
        return true
    }
}
