package content.global.skill.agility.shortcuts

import content.global.skill.agility.AgilityHandler
import content.global.skill.agility.AgilityShortcut
import core.api.finishDiaryTask
import core.api.teleport
import core.api.unlock
import core.api.visualize
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.scenery.Scenery
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.plugin.Initializable
import shared.consts.Animations
import shared.consts.Scenery as Objects

/**
 * Handles the GE underwall tunnel shortcut.
 */
@Initializable
class GrandExchangeShortcut : AgilityShortcut(
    intArrayOf(
        Objects.UNDERWALL_TUNNEL_9311,
        Objects.UNDERWALL_TUNNEL_9312
    ),
    21,
    0.0,
    "climb-into"
) {

    override fun run(player: Player, scenery: Scenery, option: String, failed: Boolean) {
        if (failed) return
        val path = SHORTCUTS[scenery.id] ?: return
        player.locks.lockComponent(4)
        AgilityHandler.forceWalk(
            player, -1,
            player.location,
            scenery.location,
            CLIMB_DOWN,
            15,
            0.0,
            null
        )

        GameWorld.Pulser.submit(object : Pulse(1, player) {
            private var step = 0

            override fun pulse(): Boolean {
                when (step) {
                    0 -> if (player.location == path[0]) step++ else return false

                    1 -> {
                        teleport(player, path[1])
                        visualize(player, CRAWL_THROUGH, -1)
                        step++
                        return false
                    }

                    2 -> {
                        AgilityHandler.forceWalk(
                            player, -1,
                            path[1],
                            path[2],
                            CLIMB_UP,
                            15,
                            0.0,
                            null,
                            0
                        )

                        finishDiaryTask(player, DiaryType.VARROCK, 1, 8)
                        unlock(player)
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun getDestination(node: Node, n: Node): Location? {
        return SHORTCUTS[node.id]?.firstOrNull()
    }

    companion object {
        private val SHORTCUTS = mapOf(
            Objects.UNDERWALL_TUNNEL_9311 to listOf(
                Location.create(3138, 3516, 0),
                Location.create(3143, 3514, 0),
                Location.create(3144, 3514, 0),
            ),
            Objects.UNDERWALL_TUNNEL_9312 to listOf(
                Location.create(3144, 3514, 0),
                Location.create(3139, 3516, 0),
                Location.create(3138, 3516, 0),
            ),
        )

        private val CLIMB_DOWN = Animation.create(Animations.CRAWL_UNDER_WALL_A_2589)
        private val CRAWL_THROUGH = Animation.create(Animations.CRAWL_UNDER_WALL_B_2590)
        private val CLIMB_UP = Animation.create(Animations.CRAWL_UNDER_WALL_C_2591)
    }
}
