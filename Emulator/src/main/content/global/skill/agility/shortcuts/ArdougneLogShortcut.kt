package content.global.skill.agility.shortcuts

import content.global.skill.agility.AgilityHandler
import content.global.skill.agility.AgilityShortcut
import core.api.*
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.system.task.Pulse
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.plugin.Initializable
import org.rs.consts.Sounds
import kotlin.random.Random

/**
 * Handles Log balance shortcut.
 */
@Initializable
class ArdougneLogShortcut : AgilityShortcut(intArrayOf(35997, 35999), 33, 0.0, "walk-across") {

    override fun run(player: Player, scenery: Scenery, option: String, failed: Boolean) {
        val isWestSide = scenery.id == 35997

        val fallAnim = if (isWestSide) FALLING_LEFT else FALLING_RIGHT
        val failDestinationA = FAIL_DESTINATION_A
        val failDestinationB = FAIL_DESTINATION_B
        val failMiddle = FAIL_MIDDLE
        val splashTile = SPLASH_TILE

        face(player, scenery)
        playAudio(player, Sounds.LOG_BALANCE_2470)

        val start = player.location
        player.logoutListeners["balance-log"] = { it.location = start }

        if (AgilityHandler.hasFailed(player, 33, 0.1)) {
            AgilityHandler.forceWalk(player, -1, start, failMiddle, fallAnim, 10, 0.0, null, 0)
            submitIndividualPulse(player, object : Pulse(11) {
                private var tick = 0
                private val randomFailDest = if (Random.nextBoolean()) failDestinationA else failDestinationB

                override fun pulse(): Boolean {
                    when (tick++) {
                        2 -> {
                            playAudio(player, Sounds.WATERSPLASH_2496)
                            sendGraphics(Graphics(SPLASH.id, 50), player.location)
                            teleport(player, splashTile)
                            animate(player, SWIMMING_LOOP)
                        }

                        4 -> player.walkingQueue.addPoint(failMiddle.x, failMiddle.y, false)
                        7 -> player.walkingQueue.addPoint(randomFailDest.x, randomFailDest.y, false)
                        9, 10 -> {
                            AgilityHandler.fail(
                                player,
                                if (isWestSide) 4 else 0,
                                randomFailDest,
                                SWIMMING,
                                Random.nextInt(1, 7),
                                null
                            )
                            player.logoutListeners.remove("balance-log")
                            player.unlock()
                            return true
                        }
                    }
                    return false
                }
            })
        } else {
            val endX = if (isWestSide) start.x + 4 else start.x - 4
            val end = Location(endX, start.y, start.z)
            AgilityHandler.forceWalk(player, -1, start, end, BALANCE_ANIM, 10, 0.0, null, 0).endAnimation =
                Animation.RESET
            player.logoutListeners.remove("balance-log")
        }
    }

    companion object {
        private val FALLING_LEFT = Animation(2581)
        private val FALLING_RIGHT = Animation(2582)

        private val FAIL_DESTINATION_A = Location(2603, 3330, 0)
        private val FAIL_DESTINATION_B = Location(2598, 3331, 0)
        private val FAIL_MIDDLE = Location(2600, 3331, 0)
        private val SPLASH_TILE = Location(2600, 3334, 0)

        private val SPLASH = Graphics(68)

        private val BALANCE_ANIM = Animation(9908)
        private val SWIMMING = Animation(6988)
        private val SWIMMING_LOOP = Animation(6989)
    }
}
