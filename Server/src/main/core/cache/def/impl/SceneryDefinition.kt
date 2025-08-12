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
 * The type Scenery definition.
 */
class SceneryDefinition : Definition<Scenery?>() {
    @JvmField var originalColors: ShortArray? = null
    @JvmField var childrenIds: IntArray? = null
    @JvmField var modelIds: IntArray? = null
    @JvmField var modelTypes: IntArray? = null
    @JvmField var mirrored: Boolean
    @JvmField var contrast: Int
    @JvmField var modelSizeZ: Int
    @JvmField var blocksLand: Boolean
    @JvmField var supportItems: Int
    @JvmField var breakRouteFinding: Boolean
    @JvmField var recolourPalette: ByteArray? = null
    @JvmField var alternateModelIds: IntArray? = null
    @JvmField var ambientSoundId: Int
    @JvmField var varbitID: Int
    @JvmField var modifiedColors: ShortArray? = null
    @JvmField var delayShading: Boolean = false
    @JvmField var blockRange: Boolean
    @JvmField var isInteractable: Boolean
    @JvmField var sizeY: Int
    @JvmField var castsShadow: Boolean = true
    @JvmField var members: Boolean
    @JvmField var hide: Boolean
    @JvmField var animations: Int
    @JvmField var brightness: Int
    @JvmField var blockwalk: Int
    @JvmField var offsetX: Int
    @JvmField var offsetZ: Int
    @JvmField var sizeX: Int
    @JvmField var offsetMultiplier: Int
    @JvmField var interactable: Int
    @JvmField var forceAnimation: Boolean
    @JvmField var configId: Int
    @JvmField var ambientSoundMaxDelay: Int
    @JvmField var modelSizeX: Int
    @JvmField var ambientSoundMinDelay: Int
    @JvmField var animationId: Int
    @JvmField var contouredGround: Byte
    @JvmField var offsetY: Int
    @JvmField var modelSizeY: Int
    @JvmField var modifiedTextureColours: ShortArray? = null
    @JvmField var originalTextureColours: ShortArray? = null
    @JvmField var blockFlag: Int
    @JvmField var hasHiddenOptions = false
    @JvmField var mapFunction: Short
    @JvmField var isSolidFlag: Boolean
    @JvmField var render: Boolean
    @JvmField var hasAnimation: Boolean
    @JvmField var mapSceneRotated: Boolean

    /**
     * Instantiates a new Scenery definition.
     */
    init {
        ambientSoundId = -1
        ambientSoundMinDelay = 0
        ambientSoundMaxDelay = 0

        varbitID = -1
        configId = -1
        animationId = 0
        animations = -1

        options = arrayOfNulls(5)
        hasHiddenOptions = false

        mirrored = false
        castsShadow = true

        sizeX = 1
        sizeY = 1

        offsetX = 0
        offsetY = 0
        offsetZ = 0
        offsetMultiplier = 64

        blockRange = true
        blocksLand = false
        blockwalk = 2

        interactable = -1

        contouredGround = 0.toByte()

        hide = false
        render = false

        isSolidFlag = true

        forceAnimation = true

        supportItems = -1
        blockFlag = 0
        brightness = 0
        contrast = 0

        mapFunction = -1
        members = false

        modelSizeX = 128
        modelSizeY = 128
        modelSizeZ = 128

        breakRouteFinding = false
        isInteractable = false
        hasAnimation = false
        mapSceneRotated = false
    }

    /**
     * Configure object.
     */
    fun configureObject() {
        if (interactable == -1) {
            interactable = 0
            if (modelIds != null && (modelTypes == null || modelTypes!![0] == 10)) {
                interactable = 1
            }
            for (i in 0..4) {
                if (options[i] != null) {
                    interactable = 1
                    break
                }
            }
        }
        if (childrenIds != null) {
            for (i in childrenIds!!.indices) {
                val def = forId(childrenIds!![i])
                def.varbitID = varbitID
            }
        }
        if (supportItems == -1) {
            supportItems = if (blockwalk == 0) 0 else 1
        }
        // Manual changes
        if (id == shared.consts.Scenery.TENT_31017) {
            sizeY = 2
            sizeX = sizeY
        }
        if (id == 29292) {
            blockRange = false
        }
    }

    /**
     * Has actions boolean.
     *
     * @return the boolean
     */
    fun hasActions(): Boolean {
        if (interactable > 0) {
            return true
        }
        if (childrenIds == null) {
            return hasOptions(false)
        }
        for (i in childrenIds!!.indices) {
            if (childrenIds!![i] != -1) {
                val def = forId(childrenIds!![i])
                if (def.hasOptions(false)) {
                    return true
                }
            }
        }
        return hasOptions(false)
    }

    /**
     * Gets child object.
     *
     * @param player the player
     * @return the child object
     */
    fun getChildObject(player: Player?): SceneryDefinition? {
        if (childrenIds == null || childrenIds!!.size < 1) {
            return this
        }
        var configValue = -1
        if (player != null) {
            if (varbitID != -1) {
                val def = VarbitDefinition.forSceneryId(varbitID)
                if (def != null) {
                    configValue = def.getValue(player)
                }
            } else if (configId != -1) {
                configValue = getVarp(player, configId)
            }
        } else {
            configValue = 0
        }
        val childDef = getChildObjectAtIndex(configValue)
        if (childDef != null) childDef.varbitID = this.varbitID
        return childDef
    }

    /**
     * Gets child object at index.
     *
     * @param index the index
     * @return the child object at index
     */
    fun getChildObjectAtIndex(index: Int): SceneryDefinition {
        if (childrenIds == null || childrenIds!!.size < 1) {
            return this
        }
        if (index < 0 || index >= childrenIds!!.size - 1 || childrenIds!![index] == -1) {
            val objectId = childrenIds!![childrenIds!!.size - 1]
            if (objectId != -1) {
                return forId(objectId)
            }
            return this
        }
        return forId(childrenIds!![index])
    }

    val configFile: VarbitDefinition?
        /**
         * Gets config file.
         *
         * @return the config file
         */
        get() {
            if (varbitID != -1) {
                return VarbitDefinition.forSceneryId(varbitID)
            }
            return null
        }

    override fun getOptions(): Array<String> = options

    override fun getName(): String = name

    /**
     * Has action boolean.
     *
     * @param action the action
     * @return the boolean
     */
    fun hasAction(action: String?): Boolean {
        if (options == null) {
            return false
        }
        for (option in options) {
            if (option == null) {
                continue
            }
            if (option.equals(action, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    companion object {
        /**
         * Gets definitions.
         *
         * @return the definitions
         */
        val definitions: MutableMap<Int, SceneryDefinition> = HashMap()
        private val OPTION_HANDLERS: MutableMap<String, OptionHandler?> = HashMap()

        /**
         * The entry point of application.
         *
         * @param args the input arguments
         * @throws Throwable the throwable
         */
        @Throws(Throwable::class)
        @JvmStatic
        fun main(args: Array<String>) {
            prompt(false)
        }

        /**
         * Parse.
         *
         * @throws Throwable the throwable
         */
        @Throws(Throwable::class)
        fun parse() {
            for (objectId in 0 until getIndexCapacity(CacheIndex.SCENERY_CONFIGURATION)) {
                var data = getData(CacheIndex.SCENERY_CONFIGURATION, objectId ushr 8, objectId and 0xFF)
                if (data == null) {
                    definitions[objectId] = SceneryDefinition()
                    continue
                }
                val def = decode(objectId, ByteBuffer.wrap(data))
                definitions[objectId] = def
            }
        }

        /**
         * For id scenery definition.
         *
         * @param objectId the object id
         * @return the scenery definition
         */
        @JvmStatic
        fun forId(objectId: Int): SceneryDefinition {
            var def = definitions[objectId]
            if (def != null) {
                return def
            }
            definitions[objectId] = SceneryDefinition().also { def = it }
            def!!.id = objectId
            return def!!
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

                    21 -> def.contouredGround = 1.toByte()
                    22 -> def.delayShading = true
                    23 -> def.hide = true

                    24 -> {
                        def.animations = buffer.g2()
                        if (def.animations == 65535) def.animations = -1
                    }

                    27 -> def.blockwalk = 1

                    28 -> def.offsetMultiplier = buffer.g1() shl 2

                    29 -> def.brightness = buffer.get().toInt()

                    30, 31, 32, 33, 34 -> {
                        val idx = opcode - 30
                        def.options[idx] = buffer.gjstr()
                        if (def.options[idx] == "Hidden") {
                            def.options[idx] = null
                            def.hasHiddenOptions = true
                        }
                    }

                    39 -> def.contrast = buffer.get().toInt() * 5

                    40 -> {
                        val length = buffer.g1()
                        def.originalColors = ShortArray(length)
                        def.modifiedColors = ShortArray(length)
                        repeat(length) {
                            def.originalColors!![it] = buffer.getShort()
                            def.modifiedColors!![it] = buffer.getShort()
                        }
                    }

                    41 -> {
                        val length = buffer.g1()
                        def.originalTextureColours = ShortArray(length)
                        def.modifiedTextureColours = ShortArray(length)
                        repeat(length) {
                            def.originalTextureColours!![it] = buffer.getShort()
                            def.modifiedTextureColours!![it] = buffer.getShort()
                        }
                    }

                    42 -> {
                        val length = buffer.g1()
                        def.recolourPalette = ByteArray(length)
                        repeat(length) {
                            def.recolourPalette!![it] = buffer.get()
                        }
                    }
                    60 -> def.mapFunction = buffer.getShort()
                    62 -> def.mirrored = true
                    64 -> def.castsShadow = false
                    65 -> def.modelSizeX = buffer.getShort().toInt() and 0xFFFF
                    66 -> def.modelSizeZ = buffer.getShort().toInt() and 0xFFFF
                    67 -> def.modelSizeY = buffer.getShort().toInt() and 0xFFFF
                    69 -> def.blockFlag = buffer.get().toInt() and 0xFF
                    70 -> def.offsetX = (buffer.getShort().toInt() and 0xFFFF) shl 2
                    71 -> def.offsetZ = (buffer.getShort().toInt() and 0xFFFF) shl 2
                    72 -> def.offsetY = (buffer.getShort().toInt() and 0xFFFF) shl 2
                    73 -> def.blocksLand = true
                    74 -> def.breakRouteFinding = true
                    75 -> def.supportItems = buffer.get().toInt() and 0xFF

                    77, 92 -> {
                        def.varbitID = buffer.g2()
                        if (def.varbitID == 65535) def.varbitID = -1

                        def.configId = buffer.g2()
                        if (def.configId == 65535) def.configId = -1

                        var defaultId = -1
                        if (opcode == 92) {
                            defaultId = buffer.g2()
                            if (defaultId == 65535) defaultId = -1
                        }

                        val childrenAmount = buffer.g1()
                        def.childrenIds = IntArray(childrenAmount + 2)
                        for (index in 0..childrenAmount) {
                            def.childrenIds!![index] = buffer.g2()
                            if (def.childrenIds!![index] == 65535) def.childrenIds!![index] = -1
                        }
                        def.childrenIds!![childrenAmount + 1] = defaultId
                    }

                    78 -> {
                        def.ambientSoundId = buffer.g2()
                        def.ambientSoundMinDelay = buffer.g1()
                    }

                    79 -> {
                        def.ambientSoundMaxDelay = buffer.g2()
                        def.animationId = buffer.g2()
                        def.ambientSoundMinDelay = buffer.g1()
                        val length = buffer.g1()
                        def.alternateModelIds = IntArray(length)
                        repeat(length) { i ->
                            def.alternateModelIds!![i] = buffer.g2()
                        }
                    }

                    81 -> {
                        def.contouredGround = 2.toByte()
                        def.configId = buffer.g1() * 256
                    }
                    82 -> def.render = true
                    88 -> def.isSolidFlag = false
                    89 -> def.forceAnimation = false
                    90 -> def.isInteractable = true
                    91 -> def.members = true

                    93 -> {
                        def.contouredGround = 3.toByte()
                        def.configId = buffer.g2()
                    }
                    94 -> def.contouredGround = 4.toByte()
                    95 -> def.contouredGround = 5.toByte()
                    96 -> def.hasAnimation = true
                    97 -> def.mapSceneRotated = true
                    100 -> {
                        buffer.get() // cursor2Op
                        buffer.getShort() // cursor2
                    }
                    101 -> buffer.get() // mapSceneAngleOffset
                    102 -> buffer.getShort() // mapScene
                    249 -> {
                        val length = buffer.g1()
                        repeat(length) {
                            val isString = buffer.g1() == 1
                            BufferReader.getMedium(buffer) // script id
                            if (isString) {
                                buffer.gjstr()
                            } else {
                                buffer.g4()
                            }
                        }
                    }

                    else -> {
                        log(SceneryDefinition::class.java, Log.ERR, "Unhandled object definition opcode: $opcode")
                        break
                    }
                }
            }

            def.configureObject()

            if (def.breakRouteFinding) {
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
