package content.minigame.pest_control.plugin

import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.world.map.zone.MapZone
import core.game.world.map.zone.ZoneRestriction

/**
 * Represents the Pest Control island zone.
 */
class PCIslandZone : MapZone("pest control island", true, ZoneRestriction.CANNON, ZoneRestriction.FIRES, ZoneRestriction.RANDOM_EVENTS) {

    override fun death(e: Entity, killer: Entity): Boolean {
        if (e is Player) {
            e.getProperties().teleportLocation = e.getLocation()
            return true
        }
        return false
    }

    override fun configure() {
        registerRegion(10537)
    }
}