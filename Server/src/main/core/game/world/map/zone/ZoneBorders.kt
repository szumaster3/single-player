package core.game.world.map.zone

import core.game.node.Node
import core.game.world.map.Location
import core.game.world.map.RegionManager.isTeleportPermitted
import core.tools.RandomFunction
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Represents the borders of a zone.
 *
 * @author Emperor
 */
class ZoneBorders {
    /**
     * The south-west x-coordinate.
     */
    val southWestX: Int

    /**
     * The south-west y-coordinate.
     */
    val southWestY: Int

    /**
     * The north-east x-coordinate.
     */
    val northEastX: Int

    /**
     * The north-east y-coordinate.
     */
    val northEastY: Int

    /**
     * The plane required to be on.
     */
    var plane: Int = 0

    /**
     * The list of exceptions.
     */
    private var exceptions: MutableList<ZoneBorders>? = null

    /**
     * If we need to do a zero plane check.
     */
    private var zeroPlaneCheck = false

    /**
     * Constructs a new `ZoneBorders` `Object`.
     *
     * @param x1 The south-west x-coordinate.
     * @param y1 The south-west y-coordinate.
     * @param x2 The north-east x-coordinate.
     * @param y2 The north-east y-coordinate.
     * Invariant enforced at runtime.
     */
    constructor(x1: Int, y1: Int, x2: Int, y2: Int) {
        this.southWestX = min(x1.toDouble(), x2.toDouble()).toInt()
        this.southWestY = min(y1.toDouble(), y2.toDouble()).toInt()
        this.northEastX = max(x1.toDouble(), x2.toDouble()).toInt()
        this.northEastY = max(y1.toDouble(), y2.toDouble()).toInt()
    }

    /**
     * Constructs a new `ZoneBorders` `Object`.
     *
     * @param x1    The south-west x-coordinate.
     * @param y1    The south-west y-coordinate.
     * @param x2    The north-east x-coordinate.
     * @param y2    The north-east y-coordinate.
     * Invariant enforced at runtime.
     * @param plane the plane.
     */
    constructor(x1: Int, y1: Int, x2: Int, y2: Int, plane: Int) {
        this.southWestX = min(x1.toDouble(), x2.toDouble()).toInt()
        this.southWestY = min(y1.toDouble(), y2.toDouble()).toInt()
        this.northEastX = max(x1.toDouble(), x2.toDouble()).toInt()
        this.northEastY = max(y1.toDouble(), y2.toDouble()).toInt()
        this.plane = plane
    }

    /**
     * Constructs a new `ZoneBorders` `Object`.
     *
     * @param x1             The south-west x-coordinate.
     * @param y1             The south-west y-coordinate.
     * @param x2             The north-east x-coordinate.
     * @param y2             The north-east y-coordinate.
     * Invariant enforced at runtime.
     * @param plane          the plane.
     * @param zeroPlaneCheck the plane check.
     */
    constructor(x1: Int, y1: Int, x2: Int, y2: Int, plane: Int, zeroPlaneCheck: Boolean) : this(x1, y1, x2, y2, plane) {
        this.zeroPlaneCheck = zeroPlaneCheck
    }

    constructor(l1: Location, l2: Location) : this(l1.x, l1.y, l2.x, l2.y, l1.z)

    /**
     * Checks if the location is inside the borders.
     *
     * @param location The location.
     * @return `True` if the location is inside the zone borders.
     */
    fun insideBorder(location: Location): Boolean {
        return insideBorder(location.x, location.y, location.z)
    }

    /**
     * Checks if the node is inside the borders.
     *
     * @param node the node.
     * @return `True` if so.
     */
    fun insideBorder(node: Node): Boolean {
        return insideBorder(node.location)
    }

    /**
     * Checks if the coordinates are inside the borders.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z coordinate.
     * @return `True` if the coordinates lay in the zone borders.
     */
    @JvmOverloads
    fun insideBorder(x: Int, y: Int, z: Int = 0): Boolean {
        if (if (zeroPlaneCheck) z != plane else (plane != 0 && z != plane)) {
            return false
        }
        if (southWestX <= x && southWestY <= y && northEastX >= x && northEastY >= y) {
            if (exceptions != null) {
                val exceptArray: Array<Any> = exceptions!!.toTypedArray()
                val length = exceptArray.size
                for (i in 0 until length) {
                    val exception = exceptArray[i] as ZoneBorders
                    if (exception.insideBorder(x, y, z)) {
                        return false
                    }
                }
            }
            return true
        }
        return false
    }

    val regionIds: List<Int>
        /**
         * Gets the ids of all the regions inside these borders.
         *
         * @return The region ids.
         */
        get() {
            val regionIds: MutableList<Int> = ArrayList(20)
            val neX = (northEastX shr 6) + 1
            val neY = (northEastY shr 6) + 1
            for (x in southWestX shr 6 until neX) {
                for (y in southWestY shr 6 until neY) {
                    val id = y or (x shl 8)
                    regionIds.add(id)
                }
            }
            return regionIds
        }

    /**
     * Gets the exceptions.
     *
     * @return The exceptions.
     */
    fun getExceptions(): List<ZoneBorders>? {
        return exceptions
    }

    fun getWeightedRandomLoc(intensity: Int): Location {
        val x = if (northEastX - southWestX == 0) southWestX else RandomFunction.normalRandDist(
            northEastX - southWestX,
            intensity
        ) + southWestX
        val y = if (northEastY - southWestY == 0) southWestY else RandomFunction.normalRandDist(
            northEastY - southWestY,
            intensity
        ) + southWestY
        return Location(x, y)
    }

    val randomLoc: Location
        get() {
            val x = if (northEastX - southWestX == 0) southWestX else Random()
                .nextInt(northEastX - southWestX + 1) + southWestX
            val y = if (northEastY - southWestY == 0) southWestY else Random()
                .nextInt(northEastY - southWestY + 1) + southWestY
            return Location(x, y, plane)
        }

    val randomWalkableLoc: Location
        get() {
            var loc = randomLoc
            var tries = 0 // prevent bad code from DOSing server
            while (!isTeleportPermitted(loc) && tries < 20) {
                loc = randomLoc
                tries += 1
            }
            return loc
        }

    /**
     * Adds an exception.
     *
     * @param exception The exception to add.
     */
    fun addException(exception: ZoneBorders) {
        if (exceptions == null) {
            this.exceptions = ArrayList(20)
        }
        exceptions!!.add(exception)
    }

    override fun toString(): String {
        return "ZoneBorders [southWestX=$southWestX, southWestY=$southWestY, northEastX=$northEastX, northEastY=$northEastY, exceptions=$exceptions]"
    }

    fun insideRegion(n: Node): Boolean {
        return insideBorder(n.location.regionX, n.location.regionY)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ZoneBorders
        return southWestX == that.southWestX && southWestY == that.southWestY && northEastX == that.northEastX && northEastY == that.northEastY && plane == that.plane && zeroPlaneCheck == that.zeroPlaneCheck && exceptions == that.exceptions
    }

    override fun hashCode(): Int {
        return Objects.hash(southWestX, southWestY, northEastX, northEastY, plane, exceptions, zeroPlaneCheck)
    }

    companion object {
        /**
         * Creates zone borders for the given region id.
         *
         * @param regionId The region id.
         * @return The zone borders.
         */
        @JvmStatic
        fun forRegion(regionId: Int): ZoneBorders {
            val baseX = ((regionId shr 8) and 0xFF) shl 6
            val baseY = (regionId and 0xFF) shl 6
            val size = 64 - 1
            return ZoneBorders(baseX, baseY, baseX + size, baseY + size)
        }
    }
}