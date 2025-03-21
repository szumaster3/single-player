package core.game.global.action

import content.global.handlers.item.SpadeDigUtils.runListener
import core.api.log
import core.game.node.entity.player.Player
import core.game.system.communication.CommunicationInfo
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.tools.Log
import org.rs.consts.Animations

/**
 * The `DigSpadeHandler` object manages the logic for digging actions that occur when a player uses a spade in certain locations.
 * It handles the animation of digging, submitting the task for the action to be processed, and registering actions to specific locations.
 */
object DigSpadeHandler {

    /**
     * A map holding the actions for each location where a digging action is registered.
     * Each location corresponds to a specific `DigAction` that will be executed when a player digs there.
     */
    private val ACTIONS: MutableMap<Location, DigAction> = HashMap()

    /**
     * The animation used for the digging action when the player uses a spade.
     */
    val ANIMATION: Animation = Animation.create(Animations.DIG_SPADE_830)

    /**
     * Handles the digging action for a player.
     * This method locks the player for a brief moment, triggers the digging animation,
     * and runs a listener for any custom actions associated with the player's location.
     *
     * If an action is registered for the player's location, the action will be queued to run via a `Pulse`.
     *
     * @param player The player who is performing the digging action.
     * @return `true` if the digging action is successful and a listener or action was triggered, otherwise `false`.
     */
    @JvmStatic
    fun dig(player: Player): Boolean {
        // Retrieve the action associated with the player's current location.
        val action = ACTIONS[player.location]

        // Trigger the digging animation and lock the player for a brief moment (1 tick).
        player.animate(ANIMATION)
        player.lock(1)

        // Check if there is a listener associated with the current location and run it if found.
        if (runListener(player.location, player)) {
            return true
        }

        // If an action is found for the current location, submit a pulse to execute the action.
        if (action != null) {
            Pulser.submit(
                object : Pulse(1, player) {
                    override fun pulse(): Boolean {
                        action.run(player)
                        return true
                    }
                },
            )
            return true
        }
        return false
    }

    /**
     * Registers a new digging action for a specific location.
     * If an action is already registered for the location, it will not be added again, and an error will be logged.
     *
     * @param location The location where the digging action will be triggered.
     * @param action The `DigAction` to be executed when a player digs at the specified location.
     * @return `true` if the action was successfully registered, otherwise `false`.
     */
    @JvmStatic
    fun register(
        location: Location,
        action: DigAction,
    ): Boolean {
        // Check if an action is already registered for the location.
        if (ACTIONS.containsKey(location)) {
            log(CommunicationInfo::class.java, Log.ERR, "Already contained dig reward for location $location.")
            return false
        }
        // Register the new action for the location.
        ACTIONS[location] = action
        return true
    }
}
