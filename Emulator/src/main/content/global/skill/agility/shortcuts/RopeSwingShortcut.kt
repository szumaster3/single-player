package content.global.skill.agility.shortcuts

import content.global.skill.agility.AgilityHandler
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.MovementPulse
import core.game.node.Node
import core.game.node.entity.impl.PulseType
import core.game.node.entity.player.Player
import core.game.node.scenery.SceneryBuilder
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.game.world.map.path.Pathfinder
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery
import org.rs.consts.Sounds

class RopeSwingShortcut : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles use rope on branch.
         */

        onUseWith(IntType.SCENERY, Items.ROPE_954, Scenery.BRANCH_2326) { player, used, with ->
            walkTo(player, SOUTH_SIDE) {
                if (!removeItem(player, used.asItem())) {
                    sendMessage(player, "You need a rope to do that.")
                    return@walkTo
                }

                playAudio(player, Sounds.TIGHTROPE_2495)
                animate(player, Animations.SUMMON_ROPE_SWING_775)
                sendMessage(player, "You tie the rope to the tree...")

                val branch = with.asScenery()
                if (branch.isActive)
                    SceneryBuilder.replace(branch, branch.transform(Scenery.ROPESWING_2325))

                ropeSwing(player, branch, SOUTH_LANDING)
            }
            return@onUseWith true
        }

        /*
         * Handles rope swing.
         */

        registerRopeSwing(Scenery.ROPESWING_2324, NORTH_SIDE, NORTH_LANDING)
        registerRopeSwing(Scenery.ROPESWING_2325, SOUTH_SIDE, SOUTH_LANDING)
    }

    private fun registerRopeSwing(sceneryId: Int, location: Location, destination: Location) {
        on(sceneryId, IntType.SCENERY, "swing-on") { player, node ->
            if (!player.location.withinDistance(node.location, 2)) {
                sendMessage(player, "You cannot do that from here.")
                return@on true
            }
            if (ropeDelay > GameWorld.ticks) {
                sendMessage(player, "The rope is being used.")
                return@on true
            }

            walkTo(player, location) {
                ropeSwing(player, node, destination)
            }
            return@on true
        }
    }

    companion object {
        private val NORTH_SIDE = Location(2511, 3092, 0)
        private val SOUTH_SIDE = Location(2501, 3087, 0)
        private val NORTH_LANDING = Location(2511, 3096, 0)
        private val SOUTH_LANDING = Location(2505, 3087, 0)

        /**
         * Rope swing animation.
         */
        private val ROPE_ANIMATION = Animation.create(497)

        /**
         * Rope usage delay to prevent overlapping animations.
         */
        private var ropeDelay = 0

        /**
         * Walks to a location using [MovementPulse].
         */
        private fun walkTo(player: Player, target: Location, onArrive: () -> Unit) {
            player.pulseManager.run(object : MovementPulse(player, target, Pathfinder.SMART) {
                override fun pulse(): Boolean {
                    if (player.location == target) {
                        onArrive()
                        return true
                    }
                    return false
                }
            }, PulseType.STANDARD)
        }

        private fun ropeSwing(player: Player, node: Node, destination: Location) {
            lock(player, 3)
            faceLocation(player, destination)

            playAudio(player, Sounds.SWING_ACROSS_2494)
            animateScenery(player, node.asScenery(), ROPE_ANIMATION.id, true)

            ropeDelay = GameWorld.ticks + animationDuration(ROPE_ANIMATION)

            AgilityHandler.forceWalk(
                player,
                -1,
                player.location,
                destination,
                Animation.create(Animations.ROPE_SWING_751),
                50,
                22.0,
                "You skillfully swing across.",
                1
            )
        }
    }
}
