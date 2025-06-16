package core.game.container.access

import core.api.IfaceSettingsBuilder
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.net.packet.PacketRepository
import core.net.packet.context.ContainerContext
import core.net.packet.out.ContainerPacket

/**
 * Utility for generating item containers and options on game interfaces.
 *
 * @author Stacx (05.02.2013)
 */
object InterfaceContainer {

    /**
     * The client script index used for setting options.
     */
    private const val CLIENT_SCRIPT_INDEX = 150

    /**
     * This index will increase each time a set is generated.
     */
    private var index = 600 // 93

    /**
     * Generates an item container on an interface.
     *
     * @param player            The player to send the interface to.
     * @param itemArray         The array of items to display.
     * @param options           The array of string options.
     * @param interfaceIndex    The interface id.
     * @param childIndex        The child component id within the interface.
     * @param x                 The x-offset for item placement.
     * @param y                 The y-offset for item placement.
     * @param key               The unique key for the container.
     * @return                  The key used for this container.
     */
    private fun generate(player: Player, itemArray: Array<Item>, options: Array<String>, interfaceIndex: Int, childIndex: Int, x: Int, y: Int, key: Int): Int {
        val clientScript = arrayOfNulls<Any>(options.size + 7)
        player.packetDispatch.sendRunScript(
            CLIENT_SCRIPT_INDEX,
            generateScriptArguments(options.size),
            populateScript(clientScript, options, (interfaceIndex shl 16) or childIndex, x, y, key)
        )
        val settings = IfaceSettingsBuilder().enableAllOptions().build()
        player.packetDispatch.sendIfaceSettings(settings, childIndex, interfaceIndex, 0, itemArray.size)
        PacketRepository.send(
            ContainerPacket::class.java, ContainerContext(player, -1, -2, key, itemArray, itemArray.size, false)
        )
        return increment()
    }

    /**
     * Generates options on the interface component.
     *
     * @param player            The player to send the interface to.
     * @param options           The array of string options.
     * @param interfaceIndex    The interface id.
     * @param childIndex        The child component id.
     * @param x                 The x-offset for placement.
     * @param y                 The y-offset for placement.
     * @param key               The unique key for this setup.
     * @return                  The generated key.
     */
    fun generateOptions(player: Player, options: Array<String>, interfaceIndex: Int, childIndex: Int, x: Int, y: Int, key: Int): Int {
        player.packetDispatch.sendRunScript(
            CLIENT_SCRIPT_INDEX,
            generateScriptArguments(options.size),
            populateScript(arrayOfNulls(options.size + 7), options, (interfaceIndex shl 16) or childIndex, x, y, key)
        )
        val settings = IfaceSettingsBuilder().enableAllOptions().build()
        player.packetDispatch.sendIfaceSettings(settings, childIndex, interfaceIndex, 0, 28)
        return increment()
    }

    /**
     * Generates options for the interface item container.
     */
    @JvmName("generateOptions")
    fun generate(player: Player, interfaceId: Int, childId: Int, itemLength: Int, vararg options: String): Int =
        generate(player, interfaceId, childId, itemLength, 7, 3, *options)
    /**
     * Generates options for the interface item container.
     */
    @JvmName("generateOption")
    fun generate(player: Player, interfaceId: Int, childId: Int, itemLength: Int, x: Int, y: Int, vararg options: String): Int = generate(player, interfaceId, childId, itemLength, x, y, options as Array<String>)

    /**
     * Generates options for an item container on an interface.
     */
    private fun generate(player: Player, interfaceId: Int, childId: Int, itemLength: Int, x: Int, y: Int, options: Array<String>): Int {
        val key = increment()
        val clientScript = arrayOfNulls<Any>(options.size + 7)
        player.packetDispatch.sendRunScript(CLIENT_SCRIPT_INDEX, generateScriptArguments(options.size), populateScript(clientScript, options, (interfaceId shl 16) or childId, x, y, key))
        val settings = IfaceSettingsBuilder().enableAllOptions().build()
        player.packetDispatch.sendIfaceSettings(settings, childId, interfaceId, 0, itemLength)
        return key
    }

    /**
     * Increments the current index.
     */
    private fun increment(): Int {
        if (index == 6999) {
            index = 600
        }
        return index++
    }

    /**
     * Populates the script argument array for the client script.
     *
     * @param script            The target array to populate.
     * @param options           The options to include.
     * @param hash              The unique hash for the interface component.
     * @param x                 The x-offset.
     * @param y                 The y-offset.
     * @param key               The unique key.
     * @return                  The populated script array.
     */
    private fun populateScript(script: Array<Any?>, options: Array<String>, hash: Int, x: Int, y: Int, key: Int): Array<Any?> {
        var offset = 0
        for (option in options) {
            script[offset++] = option
        }
        arrayOf(-1, 0, x, y, key, hash).copyInto(script, offset)
        return script
    }

    /**
     * Generates the script argument type string for the client script.
     *
     * @param length            The number of options.
     * @return                  The generated argument type string.
     */
    private fun generateScriptArguments(length: Int): String {
        val builder = StringBuilder("IviiiI")
        repeat(length) {
            builder.append('s')
        }
        return builder.toString()
    }

    /**
     * Generate and send an item array for the client.
     */
    fun generateItems(player: Player, itemArray: Array<Item>, options: Array<String>, interfaceIndex: Int, childIndex: Int): Int = generateItems(player, itemArray, options, interfaceIndex, childIndex, 7, 3, increment())

    /**
     * Generates and sends an item container with options using default layout and specified key.
     */
    fun generateItems(player: Player, itemArray: Array<Item>, options: Array<String>, interfaceIndex: Int, childIndex: Int, key: Int): Int = generateItems(player, itemArray, options, interfaceIndex, childIndex, 7, 3, key)

    /**
     * Generates and sends an item container with options and specified layout, using a new unique key.
     */
    fun generateItems(player: Player, itemArray: Array<Item>, options: Array<String>, interfaceIndex: Int, childIndex: Int, x: Int, y: Int): Int = generateItems(player, itemArray, options, interfaceIndex, childIndex, x, y, increment())

    /**
     * Generates and sends an item container with options and specified layout and key.
     */
    fun generateItems(player: Player, itemArray: Array<Item>, options: Array<String>, interfaceIndex: Int, childIndex: Int, x: Int, y: Int, key: Int): Int = generate(player, itemArray, options, interfaceIndex, childIndex, x, y, key)
}
