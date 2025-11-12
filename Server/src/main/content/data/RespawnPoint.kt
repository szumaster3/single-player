package content.data

import core.ServerConstants
import core.api.setAttribute
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.world.map.Location

/**
 * Enum representing different respawn points.
 */
enum class RespawnPoint(val location: Location) {
    LUMBRIDGE(ServerConstants.HOME_LOCATION!!.location),
    FALADOR(Location(2972, 3337, 0)),
    CAMELOT(Location(2757, 3477, 0));

    companion object {
        fun testRespawnPoint(player: Player) {
            player.setRespawnLocation(RespawnPoint.FALADOR)
            player.setRespawnLocation(RespawnPoint.CAMELOT)
            player.setRespawnLocation(RespawnPoint.LUMBRIDGE)
        }
    }
}

fun Entity.setRespawnLocation(respawnPoint: RespawnPoint) {
    val newLocation = respawnPoint.location
    this.setAttribute("/save:spawnLocation", newLocation)
    this.properties.spawnLocation = newLocation
}

fun Entity.getRespawnLocation(): Location {
    return this.getAttribute("/save:spawnLocation") as? Location
        ?: RespawnPoint.LUMBRIDGE.location
}