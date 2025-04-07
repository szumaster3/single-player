package content.global.ame.maze

import content.data.GameAttributes
import core.api.*
import core.api.ui.restoreTabs
import core.game.event.EventHook
import core.game.event.TickEvent
import core.game.interaction.DestinationFlag
import core.game.interaction.InteractionListener
import core.game.interaction.MovementPulse
import core.game.node.entity.Entity
import core.game.node.entity.impl.PulseType
import core.game.node.entity.player.Player
import core.game.system.timer.impl.AntiMacro
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneRestriction
import org.rs.consts.NPCs

/**
 * Maze interface.
 *
 * Author: [Ovenbread](https://gitlab.com/ovenbreado)
 */
class MazeInterface : InteractionListener, EventHook<TickEvent>, MapArea {
    companion object {
        const val MAZE_TIMER_VARP = 531

        val STARTING_POINTS = arrayOf(
            Location(2928, 4553, 0),
            Location(2917, 4553, 0),
            Location(2908, 4555, 0),
            Location(2891, 4589, 0),
            Location(2891, 4595, 0),
            Location(2891, 4595, 0),
            Location(2926, 4597, 0),
            Location(2931, 4597, 0),
        )
    }

    override fun defineAreaBorders(): Array<ZoneBorders> = arrayOf(getRegionBorders(11591))
    override fun getRestrictions(): Array<ZoneRestriction> =
        arrayOf(ZoneRestriction.RANDOM_EVENTS, ZoneRestriction.FOLLOWERS, ZoneRestriction.TELEPORT)

    override fun areaEnter(entity: Entity) {
        if (entity is Player) {
            val player = entity.asPlayer()
            player.hook(Event.Tick, this)
            val follower = core.game.node.entity.npc.NPC.create(
                NPCs.MYSTERIOUS_OLD_MAN_410, Location.getRandomLocation(player.location, 1, true)
            )
            follower.init()
        }
    }

    override fun areaLeave(entity: Entity, logout: Boolean) {
        if (entity is Player) {
            val player = entity.asPlayer()
            val follower = AntiMacro.getEventNpc(player)
            closeOverlay(player)
            restoreTabs(player)
            player.unhook(this)
            follower!!.clear()
        }
    }

    override fun entityStep(entity: Entity, location: Location, lastLocation: Location) {
        if (entity is Player) {
            val player = entity.asPlayer()
            val follower = AntiMacro.getEventNpc(player)
            // if (!withinDistance(player, follower!!.location, 3))
            //     follower.teleporter?.send(player.location)
            // else
            follower!!.pulseManager.run(
                (object : MovementPulse(follower, player, DestinationFlag.FOLLOW_ENTITY) {
                    override fun pulse(): Boolean {
                        follower.face(player)
                        return false
                    }
                }),
                PulseType.STANDARD,
            )
        }
    }

    override fun process(entity: Entity, event: TickEvent) {
        if (entity is Player) {
            val player = entity.asPlayer()
            var ticks = getAttribute(player, GameAttributes.MAZE_ATTRIBUTE_TICKS_LEFT, 0)
            if (ticks > 0) {
                ticks--
                setAttribute(player, GameAttributes.MAZE_ATTRIBUTE_TICKS_LEFT, ticks)
            }
            setVarp(player, MAZE_TIMER_VARP, ticks / 3, false)
        }
    }
}
