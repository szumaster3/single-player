package core.game.node.scenery

import core.cache.buffer.read.BufferReader
import core.cache.buffer.write.BufferWriter
import core.cache.def.impl.ItemDefinition
import core.game.node.item.Item
import core.game.world.map.Location
import java.nio.ByteBuffer

class Constructed(
    id: Int,
    location: Location,
    type: Int,
    rotation: Int,
    var replaced: Scenery? = null,
    var items: Array<Item>? = null,
) : Scenery(id, location, type, rotation), BufferWriter {

    constructor(id: Int, location: Location, type: Int, rotation: Int) : this(id, location, type, rotation, null, null)

    constructor(id: Int, x: Int, y: Int, z: Int, type: Int, rotation: Int) : this(id, Location.create(x, y, z), type, rotation)

    constructor(id: Int, location: Location, rotation: Int) : this(id, location, 10, rotation)

    constructor(id: Int, location: Location) : this(id, location, 10, 0)

    constructor(id: Int, x: Int, y: Int, z: Int) : this(id, Location.create(x, y, z), 10, 0)

    constructor(id: Int, type: Int, rotation: Int) : this(id, Location.create(0, 0, 0), type, rotation)

    override fun isPermanent(): Boolean = false

    override fun asConstructed(): Constructed = this

    override fun encode(buffer: ByteBuffer) {
        buffer.putInt(id)
        buffer.putInt(location.x)
        buffer.putInt(location.y)
        buffer.put(location.z.toByte())
        buffer.put(type.toByte())
        buffer.put(rotation.toByte())

        if (replaced == null) {
            buffer.put(0.toByte())
        } else {
            buffer.put(1.toByte())
            buffer.putInt(replaced!!.id)
        }

        if (items == null) {
            buffer.putInt(0)
        } else {
            buffer.putInt(items!!.size)
            for (item in items!!) {
                buffer.putInt(item.id)
            }
        }
    }

    companion object : BufferReader<Constructed> {
        override fun decode(buffer: ByteBuffer): Constructed {
            val id = buffer.int
            val x = buffer.int
            val y = buffer.int
            val z = buffer.get().toInt() and 0xFF
            val type = buffer.get().toInt() and 0xFF
            val rotation = buffer.get().toInt() and 0xFF

            val constructed = Constructed(id, Location.create(x, y, z), type, rotation)

            val hasReplaced = buffer.get().toInt() and 0xFF
            if (hasReplaced == 1) {
                val replacedId = buffer.int
                val replacedObj = SceneryManager.getById(replacedId)
                constructed.replaced = replacedObj
            }

            val itemsLength = buffer.int
            if (itemsLength > 0) {
                val items = Array(itemsLength) {
                    val itemId = buffer.int
                    val def = ItemDefinition.forId(itemId)
                    if (def != null) Item(def.id) else Item(itemId)
                }
                constructed.items = items
            }

            return constructed
        }
    }
}
