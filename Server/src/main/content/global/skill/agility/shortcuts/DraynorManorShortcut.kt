package content.global.skill.agility.shortcuts

import content.global.skill.agility.AgilityShortcut
import core.api.forceMove
import core.api.sendMessage
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.world.map.Direction
import core.plugin.Initializable

/**
 * Handles the drayor manor wall shortcut.
 */
@Initializable
class DraynorManorShortcut : AgilityShortcut(intArrayOf(37703), 28, 0.0, "squeeze-through") {

    override fun run(player: Player, scenery: Scenery, option: String, failed: Boolean) {
        val direction = if (player.location.x >= 3086) Direction.WEST else Direction.EAST
        val destination = player.location.transform(direction, 1)

        player.lock(3)
        forceMove(
            player,
            player.location,
            destination,
            30,
            60,
            null,
            3844,
        ){
            sendMessage(player, "You squeeze through the loose railing.")
        }
    }
}
