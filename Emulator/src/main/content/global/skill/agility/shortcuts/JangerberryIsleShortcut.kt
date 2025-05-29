package content.global.skill.agility.shortcuts

import content.global.skill.agility.AgilityHandler
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.scenery.SceneryBuilder
import core.game.system.task.Pulse
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery
import org.rs.consts.Sounds

/**
 * Handles the rope swing shortcut on the island west of Yanille (Jangerberry Isle)
 */
class JangerberryIsleShortcut : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles using a rope on the branch.
         */

        onUseWith(IntType.SCENERY, Items.ROPE_954, Scenery.BRANCH_2326) { player, used, with ->
            val target = Location(2501, 3087, 0)

            submitIndividualPulse(player, object : Pulse(0, player) {
                private var state = 0
                private var moving = true

                override fun pulse(): Boolean {
                    if (moving) {
                        if (player.location != target) {
                            forceWalk(player, target, "smart")
                            return false
                        }
                        moving = false
                    }

                    val branch = with.asScenery()

                    return when (++state) {
                        1 -> {
                            if (!removeItem(player, used.asItem())) {
                                sendMessage(player, "You need a rope to do that.")
                                return true
                            }

                            playAudio(player, Sounds.TIGHTROPE_2495)
                            animate(player, Animations.SUMMON_ROPE_SWING_775)

                            if (branch.isActive) SceneryBuilder.replace(branch, branch.transform(Scenery.ROPESWING_2325))
                            sendMessage(player, "You tie the rope to the tree...")
                            false
                        }

                        2 -> {
                            ropeSwing(player, branch)
                            true
                        }
                        else -> false
                    }
                }
            })

            return@onUseWith true
        }

        /*
         * Handles rope swing from the north side.
         */

        on(Scenery.ROPESWING_2324, IntType.SCENERY, "swing-on") { player, node ->
            val target = Location(2511, 3092, 0)

            submitIndividualPulse(player, object : Pulse(0, player) {
                private var state = 0
                private var moving = true

                override fun pulse(): Boolean {
                    if (moving) {
                        if (player.location != target) {
                            forceWalk(player, target, "smart")
                            return false
                        }
                        moving = false
                    }

                    return when (++state) {
                        1 -> {
                            ropeSwing(player, node)
                            true
                        }
                        else -> false
                    }
                }
            })

            return@on true
        }

        /*
         * Handles rope swing from the west side.
         */

        on(Scenery.ROPESWING_2325, IntType.SCENERY, "swing-on") { player, node ->
            ropeSwing(player, node)
            return@on true
        }

    }

    companion object {
        /**
         * Moves the player across the rope swing.
         */
        private fun ropeSwing(player: Player, node: Node) {
            if (!player.location.withinDistance(node.location, 2)) {
                sendMessage(player, "You cannot do that from here.")
                return
            }

            val destination = if (node.location != Location(2511, 3090, 0)) Location(2505, 3087, 0) else Location(2511, 3096, 0)

            lock(player, 3)
            faceLocation(player, destination)

            playAudio(player, Sounds.SWING_ACROSS_2494)
            animateScenery(player, node.asScenery(), 497, true)

            AgilityHandler.forceWalk(player, -1, player.location, destination, Animation.create(Animations.ROPE_SWING_751), 20, 22.0, "You skillfully swing across.")
        }

    }

}