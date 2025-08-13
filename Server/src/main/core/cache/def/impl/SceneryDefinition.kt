package core.cache.def.impl

import core.api.getVarp
import core.api.log
import core.cache.Cache.getData
import core.cache.Cache.getIndexCapacity
import core.cache.CacheIndex
import core.cache.buffer.read.BufferReader
import core.cache.def.Definition
import core.game.interaction.OptionHandler
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.world.GameWorld.prompt
import core.net.g1
import core.net.g2
import core.net.g4
import core.net.gjstr
import core.tools.Log
import java.nio.ByteBuffer

/**
 * Represents the scenery definition.
 */
class SceneryDefinition : Definition<Scenery?>() {
    @JvmField var id: Int = -1
    @JvmField var name: String = ""
    @JvmField var options: Array<String?> = arrayOfNulls(5)
    @JvmField var modelIds: IntArray? = null
    @JvmField var modelTypes: IntArray? = null
    @JvmField var sizeX: Int = 1
    @JvmField var sizeY: Int = 1
    @JvmField var blockwalk: Int = 2
    @JvmField var blockRange: Boolean = true
    @JvmField var blocksides: Int = 0
    @JvmField var forcedecor: Boolean = false
    @JvmField var breakroutefinding: Boolean = false
    @JvmField var supportitems: Int = -1
    @JvmField var delayShading: Boolean = false
    @JvmField var occlude: Boolean = false
    @JvmField var walloff: Int = 0
    @JvmField var ambient: Int = 0
    @JvmField var contrast: Int = 0
    @JvmField var recol_s: ShortArray? = null
    @JvmField var recol_d: ShortArray? = null
    @JvmField var retex_s: ShortArray? = null
    @JvmField var retex_d: ShortArray? = null
    @JvmField var recol_p: ByteArray? = null
    @JvmField var mirror: Boolean = false
    @JvmField var active: Boolean = true
    @JvmField var resizex: Int = 128
    @JvmField var resizey: Int = 128
    @JvmField var resizez: Int = 128
    @JvmField var xoff: Int = 0
    @JvmField var yoff: Int = 0
    @JvmField var zoff: Int = 0
    @JvmField var render: Boolean = false
    @JvmField var castshadow: Boolean = true
    @JvmField var allowrandomizedanimation: Boolean = true
    @JvmField var hasanimation: Boolean = false
    @JvmField var mapSceneRotated: Boolean = false
    @JvmField var aBoolean214: Boolean = false
    @JvmField var interactable: Int = -1
    @JvmField var cursor1Op: Int = 0
    @JvmField var cursor1: Int = 0
    @JvmField var cursor2Op: Int = 0
    @JvmField var cursor2: Int = 0
    @JvmField var mapSceneAngleOffset: Int = 0
    @JvmField var mapscene: Int = -1
    @JvmField var animations: Int = -1
    @JvmField var hillskewType: Int = 0
    @JvmField var hillskewAmount: Short = 0
    @JvmField var mapFunction: Int = -1
    @JvmField var multiLocVarbit: Int = -1
    @JvmField var multiLocVarp: Int = -1
    @JvmField var multiLocs: IntArray? = null
    @JvmField var isInteractable: Boolean = false
    @JvmField var members: Boolean = false
    @JvmField var bgsound: Int = -1
    @JvmField var bgsoundrange: Int = 0
    @JvmField var bgsoundmin: Int = 0
    @JvmField var bgsoundmax: Int = 0
    @JvmField var bgsounds: IntArray? = null
    @JvmField var params: MutableMap<Int, Any>? = null

    /**
     * Initializes default values for a new SceneryDefinition instance.
     */
    init {
        id = -1
        interactable = -1
        supportitems = -1
        mapFunction = -1
        options = arrayOfNulls(5)
        walloff = 64
    }

    /**
     * Configures the object after loading.
     */
    fun configureObject() {
        if (interactable == -1) {
            interactable = 0

            if (modelIds != null && (modelTypes == null || modelTypes?.firstOrNull() == 10)) {
                interactable = 1
            }

            if (options.size >= 5 && options.take(5).any { it != null }) {
                interactable = 1
            }
        }

        multiLocs?.forEach { childId ->
            val def = forId(childId)
            def.multiLocVarbit = multiLocVarbit
        }

        if (supportitems == -1) {
            supportitems = if (blockwalk == 0) 0 else 1
        }

        when (id) {
            shared.consts.Scenery.TENT_31017 -> {
                sizeY = 2
                sizeX = sizeY
            }
            29292 -> blockRange = false
        }
    }

    /**
     * Checks whether the object has any usable options.
     *
     * @return `true` if the object is interactable or has valid options; otherwise `false`.
     */
    override fun hasOptions(): Boolean {
        if (interactable > 0) return true

        val children = multiLocs
        if (children == null || children.isEmpty()) {
            return hasOptions(false)
        }

        for (childId in children) {
            if (childId != -1) {
                val def = forId(childId)
                if (def.hasOptions(false)) return true
            }
        }

        return hasOptions(false)
    }

    /**
     * Gets the child object for this scenery based on the given configuration.
     *
     * @param player The player for whom to get the child object, or `null` for default.
     * @return The child [SceneryDefinition] corresponding to the player config or this instance if none.
     */
    fun getChildObject(player: Player?): SceneryDefinition {
        val children = multiLocs
        if (children == null || children.isEmpty()) return this

        var configValue = -1
        if (player != null) {
            if (multiLocVarbit != -1) {
                val varbitDef = VarbitDefinition.forSceneryId(multiLocVarbit)
                if (varbitDef != null) {
                    configValue = varbitDef.getValue(player)
                }
            } else if (multiLocVarp != -1) {
                configValue = getVarp(player, multiLocVarp)
            }
        } else {
            configValue = 0
        }

        val childDef = getChildObjectAtIndex(configValue)
        if (childDef != null) childDef.multiLocVarbit = this.multiLocVarbit
        return childDef
    }


    /**
     * Gets the child object at the specified index from the multiLocs array.
     *
     * @param index The index of the child object to retrieve.
     * @return The child [SceneryDefinition] at the index or the default child or this instance if invalid.
     */
    fun getChildObjectAtIndex(index: Int): SceneryDefinition {
        val children = multiLocs
        if (children == null || children.isEmpty()) return this

        if (index < 0 || index >= children.size - 1 || children[index] == -1) {
            val defaultId = children[children.size - 1]
            if (defaultId != -1) return forId(defaultId)
            return this
        }

        return forId(children[index])
    }

    /**
     * The VarbitDefinition linked to this scenery, if any.
     */
    val configFile: VarbitDefinition?
        get() {
            if (multiLocVarbit != -1) {
                return VarbitDefinition.forSceneryId(multiLocVarbit)
            }
            return null
        }

    /**
     * Gets the interaction options available on this scenery object.
     *
     * @return An array of option strings.
     */
    override fun getOptions(): Array<String> = options

    /**
     * Gets the display name of this scenery object.
     *
     * @return The name.
     */
    override fun getName(): String = name

    /**
     * Checks if this scenery has a specific option.
     *
     * @param option The option name to check.
     * @return `true` if the option exists (case-insensitive), otherwise `false`.
     */
    fun hasOption(option: String?): Boolean {
        if (option == null || options == null) return false
        return options.any { it?.equals(option, ignoreCase = true) == true }
    }

    companion object {
        val definitions: MutableMap<Int, SceneryDefinition> = HashMap()
        private val OPTION_HANDLERS: MutableMap<String, OptionHandler?> = HashMap()

        @Throws(Throwable::class)
        @JvmStatic
        fun main(args: Array<String>) {
            prompt(false)
        }

        @Throws(Throwable::class)
        fun parse() {
            val capacity = getIndexCapacity(CacheIndex.SCENERY_CONFIGURATION)
            for (objectId in 0 until capacity) {
                val data = getData(CacheIndex.SCENERY_CONFIGURATION, objectId ushr 8, objectId and 0xFF)
                if (data == null) {
                    definitions[objectId] = SceneryDefinition()
                    continue
                }
                val def = decode(objectId, ByteBuffer.wrap(data))
                definitions[objectId] = def
            }
        }

        @JvmStatic
        fun forId(objectId: Int): SceneryDefinition {
            return definitions[objectId] ?: SceneryDefinition().also {
                it.id = objectId
                definitions[objectId] = it
            }
        }

        /**
         * Parses a [SceneryDefinition].
         *
         * @param objectId The id of the scenery object.
         * @param buffer The buffer containing the object data.
         * @return The parsed [SceneryDefinition].
         */
        private fun decode(objectId: Int, buffer: ByteBuffer): SceneryDefinition {
            val def = SceneryDefinition()
            def.id = objectId

            while (buffer.hasRemaining()) {
                val opcode = buffer.g1()
                when (opcode) {
                    0 -> break
                    1 -> {
                        val count = buffer.g1()
                        if (count > 0) {
                            if (def.modelIds == null) {
                                def.modelIds = IntArray(count)
                                def.modelTypes = IntArray(count)
                                for (i in 0 until count) {
                                    def.modelIds!![i] = buffer.g2()
                                    def.modelTypes!![i] = buffer.g1()
                                }
                            } else {
                                buffer.position(buffer.position() + count * 3)
                            }
                        }
                    }

                    2 -> def.name = buffer.gjstr()
                    5 -> {
                        val count = buffer.g1()
                        if (count > 0) {
                            if (def.modelIds == null) {
                                def.modelIds = IntArray(count)
                                def.modelTypes = null
                                for (i in 0 until count) {
                                    def.modelIds!![i] = buffer.g2()
                                }
                            } else {
                                buffer.position(buffer.position() + count * 2)
                            }
                        }
                    }

                    14 -> def.sizeX = buffer.g1()
                    15 -> def.sizeY = buffer.g1()
                    17 -> {
                        def.blockwalk = 0
                        def.blockRange = false
                    }
                    18 -> def.blockRange = false
                    19 -> def.interactable = buffer.g1()
                    21 -> def.hillskewType = 1
                    22 -> def.delayShading = true
                    23 -> def.occlude = true
                    24 -> {
                        def.animations = buffer.g2()
                        if (def.animations == 65535) def.animations = -1
                    }
                    27 -> def.blockwalk = 1
                    28 -> def.walloff = buffer.g1()
                    29 -> def.ambient = buffer.g1()
                    in 30..34 -> {
                        val idx = opcode - 30
                        def.options[idx] = buffer.gjstr()
                        if (def.options[idx]?.equals("Hidden", ignoreCase = true) == true) def.options[idx] = null
                    }
                    39 -> def.contrast = buffer.g1().toByte() * 5
                    40 -> {
                        val count = buffer.g1()
                        def.recol_s = ShortArray(count)
                        def.recol_d = ShortArray(count)
                        repeat(count) {
                            def.recol_s!![it] = buffer.g2().toShort()
                            def.recol_d!![it] = buffer.g2().toShort()
                        }
                    }
                    41 -> {
                        val count = buffer.g1()
                        def.retex_s = ShortArray(count)
                        def.retex_d = ShortArray(count)
                        repeat(count) {
                            def.retex_s!![it] = buffer.g2().toShort()
                            def.retex_d!![it] = buffer.g2().toShort()
                        }
                    }
                    42 -> {
                        val count = buffer.g1()
                        def.recol_p = ByteArray(count)
                        repeat(count) {
                            def.recol_p!![it] = buffer.g1().toByte()
                        }
                    }
                    60 -> def.mapFunction = buffer.g2()
                    62 -> def.mirror = true
                    64 -> def.active = false
                    65 -> def.resizex = buffer.g2()
                    66 -> def.resizey = buffer.g2()
                    67 -> def.resizez = buffer.g2()
                    69 -> def.blocksides = buffer.g1()
                    70 -> def.xoff = buffer.g2()
                    71 -> def.yoff = buffer.g2()
                    72 -> def.zoff = buffer.g2()
                    73 -> def.forcedecor = true
                    74 -> def.breakroutefinding = true
                    75 -> def.supportitems = buffer.g1()
                    77, 92 -> {
                        var count = -1
                        def.multiLocVarbit = buffer.g2()
                        if (def.multiLocVarbit == 65535) def.multiLocVarbit = -1
                        def.multiLocVarp = buffer.g2()
                        if (def.multiLocVarp == 65535) def.multiLocVarp = -1
                        if (opcode == 92) {
                            count = buffer.g2()
                            if (count == 65535) count = -1
                        }
                        val len = buffer.g1()
                        def.multiLocs = IntArray(len + 2)
                        for (i in 0..len) {
                            def.multiLocs!![i] = buffer.g2()
                            if (def.multiLocs!![i] == 65535) def.multiLocs!![i] = -1
                        }
                        def.multiLocs!![len + 1] = count
                    }
                    78 -> {
                        def.bgsound = buffer.g2()
                        def.bgsoundrange = buffer.g1()
                    }
                    79 -> {
                        def.bgsoundmin = buffer.g2()
                        def.bgsoundmax = buffer.g2()
                        def.bgsoundrange = buffer.g1()
                        val count = buffer.g1()
                        def.bgsounds = IntArray(count)
                        for (i in 0 until count) {
                            def.bgsounds!![i] = buffer.g2()
                        }
                    }
                    81 -> {
                        def.hillskewType = 2
                        def.hillskewAmount = (buffer.g1() * 256).toShort()
                    }
                    82 -> def.render = true
                    88 -> def.castshadow = false
                    89 -> def.allowrandomizedanimation = false
                    90 -> def.isInteractable = true
                    91 -> def.members = true
                    93 -> {
                        def.hillskewType = 3
                        def.hillskewAmount = buffer.g2().toShort()
                    }
                    94 -> def.hillskewType = 4
                    95 -> def.hillskewType = 5
                    96 -> def.hasanimation = true
                    97 -> def.mapSceneRotated = true
                    98 -> def.aBoolean214 = true
                    99 -> {
                        def.cursor1Op = buffer.g1()
                        def.cursor1 = buffer.g2()
                    }
                    100 -> {
                        def.cursor2Op = buffer.g1()
                        def.cursor2 = buffer.g2()
                    }
                    101 -> def.mapSceneAngleOffset = buffer.g1()
                    102 -> def.mapscene = buffer.g2()
                    249 -> {
                        val count = buffer.g1()
                        if (def.params == null) {
                            def.params = mutableMapOf()
                        }
                        repeat(count) {
                            val isString = buffer.g1() == 1
                            val key = BufferReader.getMedium(buffer)
                            val value = if (isString) buffer.gjstr() else buffer.g4()
                            def.params!![key] = value
                        }
                    }
                    else -> {
                        log(SceneryDefinition::class.java, Log.ERR, "Unhandled object definition opcode: $opcode")
                        break
                    }
                }
            }
            def.configureObject()

            if (def.breakroutefinding) {
                def.blockwalk = 0
                def.blockRange = false
            }

            return def
        }

        /**
         * Gets option handler.
         *
         * @param nodeId the node id
         * @param name   the name
         * @return the option handler
         */
        @JvmStatic
        fun getOptionHandler(
            nodeId: Int,
            name: String,
        ): OptionHandler? {
            val def = forId(nodeId)
            val handler = def.getConfiguration<OptionHandler>("option:$name")
            if (handler != null) {
                return handler
            }
            return OPTION_HANDLERS[name]
        }

        /**
         * Sets option handler.
         *
         * @param name    the name
         * @param handler the handler
         * @return the option handler
         */
        @JvmStatic
        fun setOptionHandler(
            name: String,
            handler: OptionHandler?,
        ): Boolean = OPTION_HANDLERS.put(name, handler) != null
    }
}
