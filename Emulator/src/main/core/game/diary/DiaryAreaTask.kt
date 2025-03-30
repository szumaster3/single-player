package core.game.diary

import core.api.inBorders
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders

/**
 * Represents a task within a diary that requires the player to be inside a specific zone.
 *
 * @property zoneBorders The defined area where the task is valid.
 * @property diaryLevel The level of the diary this task belongs to.
 * @property taskId The unique identifier for the task.
 * @property condition An optional condition that must be met in addition to being inside the zone.
 */
class DiaryAreaTask(
    val zoneBorders: ZoneBorders,
    val diaryLevel: DiaryLevel,
    val taskId: Int,
    private val condition: ((player: Player) -> Boolean)? = null,
) {
    /**
     * Checks if the player satisfies the task conditions and executes the provided action if they do.
     *
     * @param player The player whose status is being checked.
     * @param then The action to execute if the task conditions are met.
     */
    fun whenSatisfied(
        player: Player,
        then: () -> Unit,
    ) {
        var result = inBorders(player, zoneBorders)

        condition?.let {
            result = it(player)
        }

        if (result) then()
    }
}