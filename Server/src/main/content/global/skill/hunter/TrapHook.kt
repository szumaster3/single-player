package content.global.skill.hunter

import core.game.world.GameWorld.settings
import core.game.world.map.Location
import core.tools.RandomFunction

/**
 * Represents a hook for a trap that can catch NPCs at specific locations.
 *
 * @property wrapper the trap wrapper associated with this hook
 * @property locations possible locations where the trap can be triggered
 */
class TrapHook(val wrapper: TrapWrapper, val locations: Array<Location>) {

    /**
     * Randomly determines a location where the trap succeeds based on chance.
     *
     * @return a random location from [locations] if the trap succeeds, null otherwise
     */
    val chanceLocation: Location?
        get() {
            val chance = wrapper.chanceRate
            val roll = RandomFunction.random(99)
            val successChance = (if (settings!!.isDevMode) 100.0 else 55.0) + chance
            if (successChance > roll) {
                return RandomFunction.getRandomElement(locations)
            }
            return null
        }

    /**
     * Checks if a given location is part of this trap hook.
     *
     * @param location the location to check
     * @return true if the location is in [locations], false otherwise
     */
    fun isHooked(location: Location): Boolean {
        for (l in locations) {
            if (l == location) {
                return true
            }
        }
        return false
    }
}