package content.global.skill.construction

import core.api.log
import core.game.node.entity.player.Player
import core.game.node.scenery.Constructed
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.game.world.map.*
import core.tools.Log

/**
 * Represents a room in a house construction system.
 *
 * @property properties The rooms properties and configuration.
 */
class Room(
    var properties: RoomProperties
) {

    companion object {
        /**
         * Room type representing a chamber.
         */
        const val CHAMBER = 0x0

        /**
         * Room type representing a roof.
         */
        const val ROOF = 0x1

        /**
         * Room type representing a dungeon.
         */
        const val DUNGEON = 0x2

        /**
         * Room type representing land.
         */
        const val LAND = 0x4

        /**
         * Creates a new [Room] instance.
         *
         * @param player The player.
         * @param properties The properties.
         * @return Room instance.
         */
        @JvmStatic
        fun create(player: Player, properties: RoomProperties): Room {
            val room = Room(properties)
            room.configure(player.houseManager.style)
            return room
        }
    }

    var chunk: RegionChunk? = null
    var hotspots: Array<Hotspot> = emptyArray()
    var rotation: Direction = Direction.NORTH

    /**
     * Init the room hotspots and apply decorations based on house style.
     *
     * @param style The housing style used for decoration.
     */
    fun configure(style: HousingStyle) {
        hotspots = Array(properties.hotspots.size) { i -> properties.hotspots[i].copy() }
        decorate(style)
    }

    /**
     * Loads and add the room chunk and prepares decorations.
     *
     * @param style The housing style for the room.
     */
    fun decorate(style: HousingStyle) {
        val region = RegionManager.forId(style.regionId)
        Region.load(region, true)
        chunk = region.planes[style.plane].getRegionChunk(properties.chunkX, properties.chunkY)
    }

    /**
     * Gets the hotspot matching the [BuildHotspot].
     *
     * @param hotspot The hotspot to find.
     * @return The matching [Hotspot] or null.
     */
    fun getHotspot(hotspot: BuildHotspot?): Hotspot? {
        return hotspots.firstOrNull { it.hotspot == hotspot }
    }

    /**
     * Checks if the hotspot has been built (decoration index set).
     *
     * @param hotspot The hotspot to check.
     * @return True if built, false otherwise.
     */
    fun isBuilt(hotspot: BuildHotspot): Boolean {
        val h = getHotspot(hotspot)
        return h != null && h.decorationIndex > -1
    }

    /**
     * Loads decorations into the chunk for this room to the house manager.
     *
     * @param housePlane The floor level of the house.
     * @param chunk The region chunk to decorate.
     * @param house The house manager controlling styles and modes.
     */
    fun loadDecorations(housePlane: Int, chunk: BuildRegionChunk, house: HouseManager) {
        for (spot in hotspots) {
            val x = spot.chunkX
            val y = spot.chunkY
            if (spot.hotspot == null) continue

            val index = chunk.getIndex(x, y, spot.hotspot.getObjectId(house.style))
            val sceneries = chunk.getObjects(index)
            val scenery = sceneries[x][y]
            if (scenery != null && scenery.id == spot.hotspot.getObjectId(house.style)) {
                if (spot.decorationIndex in spot.hotspot.decorations.indices) {
                    var id = spot.hotspot.decorations[spot.decorationIndex].getObjectId(house.style)
                    if (spot.hotspot.type == BuildHotspotType.CREST) {
                        id += house.crest.ordinal
                    }
                    SceneryBuilder.replace(
                        scenery,
                        scenery.transform(id, scenery.rotation, chunk.currentBase.transform(x, y, 0))
                    )
                } else if (
                    scenery.id == BuildHotspot.WINDOW.getObjectId(house.style) ||
                    (!house.isBuildingMode && scenery.id == BuildHotspot.CHAPEL_WINDOW.getObjectId(house.style))
                ) {
                    SceneryBuilder.replace(
                        scenery,
                        scenery.transform(house.style.window.getObjectId(house.style), scenery.rotation, scenery.type)
                    )
                }
                val pos = RegionChunk.getRotatedPosition(x, y, scenery.sizeX, scenery.sizeY, 0, rotation.toInteger())
                spot.currentX = pos[0]
                spot.currentY = pos[1]
            }
        }

        chunk.let {
            if (rotation != Direction.NORTH && it.rotation == 0) {
                it.rotate(rotation)
            }
        }

        if (!house.isBuildingMode) {
            placeDoors(housePlane, house, chunk)
            removeHotspots(housePlane, house, chunk)
        }
    }

    private fun removeHotspots(housePlane: Int, house: HouseManager, chunk: BuildRegionChunk) {
        if (properties.isRoof) return
        for (x in 0 until 8) {
            for (y in 0 until 8) {
                for (i in 0 until BuildRegionChunk.ARRAY_SIZE) {
                    val scenery = chunk.get(x, y, i)
                    if (scenery != null) {
                        val isBuilt = scenery is Constructed
                        val isWall = scenery.id == 13065 || scenery.id == house.style.wallId
                        val isDoor = scenery.id == house.style.doorId || scenery.id == house.style.secondDoorId
                        if (!isBuilt && !isWall && !isDoor) {
                            SceneryBuilder.remove(scenery)
                            chunk.remove(scenery)
                        }
                    }
                }
            }
        }
    }

    private fun placeDoors(housePlane: Int, house: HouseManager, chunk: BuildRegionChunk) {
        val rooms = house.rooms
        val rx = chunk.currentBase.chunkX
        val ry = chunk.currentBase.chunkY

        val maxX = rooms[0].size - 1
        val maxY = rooms[0][0].size - 1

        for (i in 0 until BuildRegionChunk.ARRAY_SIZE) {
            for (x in 0 until 8) {
                for (y in 0 until 8) {
                    val scenery = chunk.get(x, y, i) ?: continue
                    if (!BuildingUtils.isDoorHotspot(scenery)) continue

                    var edge = false
                    var otherRoom: Room? = null

                    val globalX = rx * 8 + x
                    val globalY = ry * 8 + y

                    when (scenery.rotation) {
                        0 -> {
                            edge = globalX == 0
                            if (!edge && globalX - 1 in 0..maxX && globalY in 0..maxY) {
                                otherRoom = rooms[housePlane][globalX - 1][globalY]
                            }
                        }
                        1 -> {
                            edge = globalY == maxY
                            if (!edge && globalX in 0..maxX && globalY + 1 in 0..maxY) {
                                otherRoom = rooms[housePlane][globalX][globalY + 1]
                            }
                        }
                        2 -> {
                            edge = globalX == maxX
                            if (!edge && globalX + 1 in 0..maxX && globalY in 0..maxY) {
                                otherRoom = rooms[housePlane][globalX + 1][globalY]
                            }
                        }
                        3 -> {
                            edge = globalY == 0
                            if (!edge && globalX in 0..maxX && globalY - 1 in 0..maxY) {
                                otherRoom = rooms[housePlane][globalX][globalY - 1]
                            }
                        }
                        else -> log(this::class.java, Log.ERR, "Impossible rotation when placing doors.")
                    }

                    val replaceId = getReplaceId(housePlane, house, this, edge, otherRoom, scenery)
                    if (replaceId == -1) continue

                    SceneryBuilder.replace(scenery, scenery.transform(replaceId))
                }
            }
        }
    }

    /**
     * Sets the replacement object id.
     */
    private fun getReplaceId(
        housePlane: Int,
        house: HouseManager,
        room: Room,
        edge: Boolean,
        otherRoom: Room?,
        scenery: Scenery
    ): Int {
        val thisOutside = !room.properties.isChamber
        if (edge && thisOutside) {
            return -1
        }
        if (!edge) {
            val otherOutside = otherRoom == null || !otherRoom.properties.isChamber
            if (thisOutside == otherOutside) {
                if (otherRoom == null) return -1
                val exit = otherRoom.exits[scenery.rotation]
                if (exit) return -1
            }
            if (thisOutside != otherOutside && housePlane == 0) {
                if (thisOutside) return -1
                return if (scenery.id % 2 != 0) house.style.doorId else house.style.secondDoorId
            }
        }
        return if (room.properties.isDungeon) 13065 else house.style.wallId
    }

    /**
     * Sets the decoration index for all hotspots matching the given type.
     *
     * @param index Decoration index to set.
     * @param hs Hotspot type to match.
     */
    fun setAllDecorationIndex(index: Int, hs: BuildHotspot) {
        for (h in hotspots) {
            if (h.hotspot == hs) {
                h.decorationIndex = index
            }
        }
    }

    /**
     * Gets the stairs or equivalent hotspots in this room.
     *
     * @return The stairs hotspot or null if none found.
     */
    fun getStairs(): Hotspot? {
        return hotspots.firstOrNull {
            it.hotspot.type == BuildHotspotType.STAIRCASE ||
            it.hotspot == BuildHotspot.LADDER || it.hotspot == BuildHotspot.TRAPDOOR ||
            (it.hotspot == BuildHotspot.CENTREPIECE_1 && it.decorationIndex == 4) ||
            (it.hotspot == BuildHotspot.CENTREPIECE_2 && it.decorationIndex == 2)
        }
    }

    /**
     * Returns the exits adjusted for current rotation.
     */
    val exits: BooleanArray
        get() = getExits(rotation)

    /**
     * Gets the exits array adjusted for a rotation.
     *
     * @param rotation The rotation to adjust exits for.
     * @return A boolean array indicating exits on each side.
     */
    fun getExits(rotation: Direction): BooleanArray {
        val exits = properties.exits
        if (chunk?.rotation != rotation.toInteger()) {
            val exit = BooleanArray(exits.size)
            val offset = rotation.toInteger() - (chunk?.rotation ?: 0)
            for (i in exits.indices) {
                exit[(i + offset) % exits.size] = exits[i]
            }
            return exit
        }
        return exits
    }

    /**
     * Finds a hotspot at given coordinates and type.
     *
     * @param build The build hotspot type.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The matching hotspot or null.
     */
    fun getHotspot(build: BuildHotspot, x: Int, y: Int): Hotspot? {
        return hotspots.firstOrNull { it.currentX == x && it.currentY == y && it.hotspot == build }
    }

    /**
     * Updates room properties and refreshes decorations while preserving hotspot states.
     *
     * @param player The player owning the house.
     * @param properties The new properties to apply.
     */
    fun updateProperties(player: Player, properties: RoomProperties) {
        this.properties = properties
        decorate(player.houseManager.style)
        if (hotspots.size != properties.hotspots.size) return

        for (i in hotspots.indices) {
            val h = hotspots[i]
            val hs = properties.hotspots[i].copy()
            hs.currentX = h.currentX
            hs.currentY = h.currentY
            hs.decorationIndex = h.decorationIndex
            hotspots[i] = hs
        }
    }
}
