package core.game.world.map

import core.api.utils.Vector
import core.game.interaction.DestinationFlag
import core.game.node.Node
import core.game.world.map.path.Pathfinder
import core.tools.RandomFunction

class Location(
    x: Int,
    y: Int,
    z: Int = 0,
) : Node(null, null) {

    var x: Int = x
        private set

    var y: Int = y
        private set

    var z: Int = if (z < 0) z + 4 else z
        private set

    init {
        super.destinationFlag = DestinationFlag.LOCATION
    }

    constructor(x: Int, y: Int) : this(x, y, 0)

    constructor(x: Int, y: Int, z: Int, randomizer: Int) : this(
        x + RandomFunction.getRandom(randomizer), y + RandomFunction.getRandom(randomizer), z
    )

    companion object {
        @JvmStatic
        fun create(x: Int, y: Int, z: Int): Location = Location(x, y, z)

        @JvmStatic
        fun create(x: Int, y: Int): Location = Location(x, y, 0)

        @JvmStatic
        fun create(location: Location): Location = create(location.x, location.y, location.z)

        @JvmStatic
        fun getDelta(location: Location, other: Location): Location =
            create(other.x - location.x, other.y - location.y, other.z - location.z)

        @JvmStatic
        fun getRandomLocation(main: Location, radius: Int, reachable: Boolean): Location {
            var location = RegionManager.getTeleportLocation(main, radius)
            if (!reachable) {
                return location
            }
            val path = Pathfinder.find(main, location, false, Pathfinder.DUMB)
            if (!path.isSuccessful) {
                location = main
                if (path.points.isNotEmpty()) {
                    val p = path.points.last()
                    location = create(p.x, p.y, main.z)
                }
            }
            return location
        }

        @JvmStatic
        fun fromString(locString: String): Location {
            val trimmed = locString.replace("[", "").replace("]", "")
            val tokens = trimmed.split(",")
            return create(
                tokens[0].trim().toInt(), tokens[1].trim().toInt(), tokens[2].trim().toInt()
            )
        }
    }

    override fun getLocation(): Location = this

    fun isNextTo(node: Node): Boolean {
        val l = node.location
        if (l.y == y) {
            return l.x - x == -1 || l.x - x == 1
        }
        if (l.x == x) {
            return l.y - y == -1 || l.y - y == 1
        }
        return false
    }

    fun getRegionId(): Int = (x shr 6) shl 8 or (y shr 6)

    fun isInRegion(region: Int): Boolean = getRegionId() == region

    fun transform(dir: Direction, steps: Int = 1): Location = Location(x + dir.stepX * steps, y + dir.stepY * steps, z)

    fun transform(l: Location): Location = Location(x + l.x, y + l.y, z + l.z)

    fun transform(diffX: Int, diffY: Int, diffZ: Int): Location = Location(x + diffX, y + diffY, z + diffZ)

    fun withinDistance(other: Location, dist: Int = MapDistance.RENDERING.distance): Boolean {
        if (other.z != z) return false
        val a = other.x - x
        val b = other.y - y
        val product = kotlin.math.sqrt((a * a + b * b).toDouble())
        return product <= dist
    }

    fun withinMaxnormDistance(other: Location, dist: Int): Boolean {
        if (other.z != z) return false
        val a = kotlin.math.abs(other.x - x)
        val b = kotlin.math.abs(other.y - y)
        return maxOf(a, b) <= dist
    }

    fun getDistance(other: Location): Double {
        val xdiff = x - other.x
        val ydiff = y - other.y
        return kotlin.math.sqrt((xdiff * xdiff + ydiff * ydiff).toDouble())
    }

    fun getSurroundingTiles(): List<Location> = listOf(
        transform(-1, -1, 0),   // SW
        transform(0, -1, 0),    // S
        transform(1, -1, 0),    // SE
        transform(1, 0, 0),     // E
        transform(1, 1, 0),     // NE
        transform(0, 1, 0),     // N
        transform(-1, 1, 0),    // NW
        transform(-1, 0, 0)     // W
    )

    fun getCardinalTiles(): List<Location> = listOf(
        transform(-1, 0, 0), transform(0, -1, 0), transform(1, 0, 0), transform(0, 1, 0)
    )

    fun get3x3Tiles(): List<Location> = listOf(
        transform(0, 0, 0),      // Center
        transform(0, 1, 0),      // N
        transform(1, 1, 0),      // NE
        transform(1, 0, 0),      // E
        transform(1, -1, 0),     // SE
        transform(0, -1, 0),     // S
        transform(-1, -1, 0),    // SW
        transform(-1, 0, 0),     // W
        transform(-1, 1, 0)      // NW
    )

    val chunkOffsetX: Int
        get() = getLocalX() and 7

    val chunkOffsetY: Int
        get() = getLocalY() and 7

    val chunkBase: Location
        get() = create(regionX shl 3, regionY shl 3, z)

    val regionX: Int
        get() = x shr 3

    val regionY: Int
        get() = y shr 3

    fun getLocalX(): Int = x and 63

    fun getLocalY(): Int = y and 63

    fun getSceneX(): Int = x - ((regionX - 6) shl 3)

    fun getSceneY(): Int = y - ((regionY - 6) shl 3)

    fun getSceneX(loc: Location): Int = x - ((loc.regionX - 6) shl 3)

    fun getSceneY(loc: Location): Int = y - ((loc.regionY - 6) shl 3)

    val chunkX: Int
        get() = getLocalX() shr 3

    val chunkY: Int
        get() = getLocalY() shr 3

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Location) return false
        return x == other.x && y == other.y && z == other.z
    }

    fun equals(x: Int, y: Int, z: Int): Boolean = this.x == x && this.y == y && this.z == z

    override fun toString(): String = "[$x, $y, $z]"

    override fun hashCode(): Int = (z shl 30) or (x shl 15) or y

    fun setX(value: Int) {
        x = value
    }

    fun setY(value: Int) {
        y = value
    }

    fun setZ(value: Int) {
        z = value
    }

    fun getStepComponents(dir: Direction): List<Location> {
        val output = mutableListOf<Location>()
        val stepX = dir.stepX
        val stepY = dir.stepY

        if (stepX != 0) output.add(transform(stepX, 0, 0))
        if (stepY != 0) output.add(transform(0, stepY, 0))

        return output
    }

    fun deriveDirection(location: Location): Direction? {
        var diffX = location.x - x
        var diffY = location.y - y

        diffX = if (diffX >= 0) kotlin.math.min(diffX, 1) else -1
        diffY = if (diffY >= 0) kotlin.math.min(diffY, 1) else -1

        val sb = StringBuilder()

        if (diffY != 0) {
            sb.append(if (diffY > 0) "NORTH" else "SOUTH")
        }

        if (diffX != 0) {
            if (sb.isNotEmpty()) sb.append("_")
            sb.append(if (diffX > 0) "EAST" else "WEST")
        }

        if (sb.isEmpty()) return null
        return Direction.valueOf(sb.toString())
    }

    fun transform(vector: Vector): Location =
        create(x + kotlin.math.floor(vector.x).toInt(), y + kotlin.math.floor(vector.y).toInt())
}
