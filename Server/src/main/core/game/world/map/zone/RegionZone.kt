package core.game.world.map.zone

import java.util.*

/**
 * Represents a zone inside a single region of the world map.
 *
 * @author Emperor
 */
class RegionZone(
    val zone: MapZone,
    val borders: ZoneBorders
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as RegionZone
        return zone == that.zone && borders == that.borders
    }

    override fun hashCode(): Int {
        return Objects.hash(zone, borders)
    }
}