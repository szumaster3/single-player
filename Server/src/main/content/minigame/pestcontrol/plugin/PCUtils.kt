package content.minigame.pestcontrol.plugin

import content.minigame.pestcontrol.bots.PestControlScript
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import shared.consts.Scenery

object PCUtils {
    val GATE_ENTRIES = intArrayOf(
        Scenery.GATE_14233,
        Scenery.GATE_14234,
        Scenery.GATE_14235
    )

    enum class LanderZone(
        val boatBorder: ZoneBorders,
        val outsideBoatBorder: ZoneBorders,
        val ladderId: Int,
        val landerLocation: Location
    ) {
        NOVICE(
            ZoneBorders(2660, 2638, 2663, 2643),
            ZoneBorders(2658, 2635, 2656, 2646),
            14315,
            Location.create(2657, 2642, 0)
        ),
        INTERMEDIATE(
            ZoneBorders(2638, 2642, 2641, 2647),
            ZoneBorders(2645, 2639, 2643, 2652),
            25631,
            Location.create(2644, 2646, 0)
        ),
        VETERAN(
            ZoneBorders(2632, 2649, 2635, 2654),
            ZoneBorders(2638, 2652, 2638, 2655),
            25632,
            Location.create(2630, 2648, 0)
        )
    }

    fun isInLander(loc: Location?, lander: LanderZone): Boolean =
        loc != null && lander.boatBorder.insideBorder(loc)

    fun isOutsideGangplank(loc: Location?, lander: LanderZone): Boolean =
        loc != null && lander.outsideBoatBorder.insideBorder(loc)

    fun isInPestControlInstance(p: Player): Boolean = p.getAttribute<Any?>("pc_zeal") != null

    fun getMyPestControlSession(p: PestControlScript): PestControlSession? =
        p.getExtension(PestControlSession::class.java)

    /**
     * Returns the closest active portal within range of the given bot.
     */
    fun getClosestActivePortal(bot: PestControlScript, range: Int): Node? {
        val session = getMyPestControlSession(bot) ?: return null
        return session.aportals.firstOrNull {
            it.isActive && it.location.withinDistance(bot.location, range)
        }
    }

    /**
     * Returns a sample central position near portals for random movement.
     */
    fun getCentralPortalLocation(lander: LanderZone): Location =
        when (lander) {
            LanderZone.NOVICE -> Location.create(2660, 2608, 0)
            LanderZone.INTERMEDIATE -> Location.create(2645, 2592, 0)
            LanderZone.VETERAN -> Location.create(2628, 2596, 0)
        }
    /**
     * Searches for the nearest enemy NPC within range.
     */
    fun getClosestNPC(bot: PestControlScript, range: Int): core.game.node.entity.Entity? {
        val npcs = core.game.world.map.RegionManager.getLocalNpcs(bot)
        return npcs
            .filter { it.isActive && it.location.withinDistance(bot.location, range) }
            .minByOrNull { it.location.getDistance(bot.location) }
    }
}
