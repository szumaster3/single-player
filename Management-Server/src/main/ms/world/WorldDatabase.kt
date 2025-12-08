package ms.world

import ms.ServerConstants
import ms.net.IoSession
import ms.net.packet.IoBuffer
import ms.world.info.CountryFlag
import ms.world.info.WorldInfo
import java.nio.ByteBuffer
import java.util.*

/**
 * Holds all the world servers.
 *
 * @author Emperor
 */
object WorldDatabase {
    /**
     * Gets all the game servers.
     *
     * @return The list of game servers.
     */
    /**
     * The game servers.
     */
    val worlds: Array<GameServer?> = arrayOfNulls(ServerConstants.WORLD_LIMIT)

    /**
     * Gets the updateStamp.
     *
     * @return the updateStamp
     */
    /**
     * The update time stamp.
     */
    var updateStamp: Long = System.currentTimeMillis()
        private set

    /**
     * Gets the packet to update the world list in the lobby.
     *
     * @param player             The player.
     * @param worldConfiguration If the configuration should be added.
     * @param worldStatus        If the status should be added.
     * @return The `OutgoingPacket` to write.
     */
    @JvmStatic
    fun sendUpdate(session: IoSession, updateStamp: Int) {
        val buf = ByteBuffer.allocate(1024)
        buf.put(0.toByte())
        buf.putShort(0.toShort())
        buf.put(1.toByte())
        val buffer = IoBuffer()
        if (updateStamp == WorldDatabase.updateStamp.toInt()) {
            buf.put(0.toByte())
        } else {
            buf.put(1.toByte()) // Indicates an update occured.
            putWorldListinfo(buffer)
        }
        putPlayerInfo(buffer)
        if (buffer.toByteBuffer().position() > 0) {
            buf.put(buffer.toByteBuffer().flip())
        }
        buf.putShort(1, (buf.position() - 3).toShort())
        session.queue(buf.flip())
    }

    /**
     * Adds the world status on the packet.
     *
     * @param buffer The current packet.
     */
    @JvmStatic
    private fun putPlayerInfo(buffer: IoBuffer) {
        for (server in worlds) {
            if (server != null) {
                val w: WorldInfo = server.info
                buffer.putSmart(w.worldId)
                buffer.putShort(if (server.isActive) server.playerAmount else -1)
            }
        }
    }

    /**
     * Sets the countries for each world.
     *
     * @param buffer The current packet.
     */
    @JvmStatic
    private fun putCountryInfo(buffer: IoBuffer) {
        for (country in CountryFlag.values()) {
            buffer.putSmart(country.id)
            buffer.putJagString(capitalize(country.name.lowercase(Locale.getDefault())))
        }
    }

    /**
     * Converts a string to a capitalized string
     *
     * @param name
     * @return
     */
    @JvmStatic
    private fun capitalize(name: String?): String? {
        if (name == null || name.length == 0) {
            return name
        } else {
            val chars = name.toCharArray()
            chars[0] = chars[0].uppercaseChar()
            return String(chars)
        }
    }

    /**
     * Adds the world configuration on the packet.
     *
     * @param buffer The current packet.
     */
    @JvmStatic
    private fun putWorldListinfo(buffer: IoBuffer) {
        buffer.putSmart(CountryFlag.values().size)
        putCountryInfo(buffer)
        buffer.putSmart(0)
        buffer.putSmart(worlds.size)
        buffer.putSmart(registeredAmount)
        for (server in worlds) {
            if (server != null) {
                val w: WorldInfo = server.info
                buffer.putSmart(w.worldId)
                buffer.put(w.country.ordinal)
                buffer.putInt(w.settings)
                buffer.putJagString(w.activity)
                buffer.putJagString(w.address)
            }
        }
        buffer.putInt(updateStamp.toInt())
    }

    val registeredAmount: Int
        /**
         * Gets the amount of worlds registered.
         *
         * @return The amount of worlds registered.
         */
        get() {
            var count = 0
            for (server in worlds) {
                if (server != null) {
                    count++
                }
            }
            return count
        }

    /**
     * Registers a game server.
     *
     * @param info The game world info.
     */
    @JvmStatic
    fun register(info: WorldInfo): GameServer {
        val server = worlds[info.worldId]
        check(!(server != null && server.session?.isActive == true && server.session?.address != info.address)) { "World " + info.worldId + " is already registered!" }
        flagUpdate()
        println(
            "Registered world - [id=" + info.worldId + ", ip=" + info.address + ", country=" + info.country.name.lowercase(
                Locale.getDefault()
            ) + ", revision=" + info.revision + "]!"
        )
        return GameServer(info).also { worlds[info.worldId] = it }
    }

    @JvmStatic
    fun unRegister(server: GameServer) {
        var index = -1
        for (i in worlds.indices) {
            val s = worlds[i]
            if (s == server) {
                index = i
                break
            }
        }
        if (index != -1) {
            worlds[index] = null
            val info: WorldInfo = server.info
            println(
                "Unregistered world - [id=" + info.worldId + ", ip=" + info.address + ", country=" + info.country.name.lowercase(
                    Locale.getDefault()
                ) + ", revision=" + info.revision + "]!"
            )
        }
    }

    /**
     * Gets the world id of the player.
     *
     * @param username The username of the player.
     * @return The world id.
     */
    @JvmStatic
    fun getWorldId(username: String?): Int {
        return getWorldId(getPlayer(username))
    }

    /**
     * Gets the world id of the player.
     *
     * @param player The player.
     * @return The world id.
     */
    @JvmStatic
    fun getWorldId(player: PlayerSession?): Int {
        if (player == null || !player.isActive) {
            return 0
        }
        return player.worldId
    }

    /**
     * Checks if the game world is active.
     *
     * @param worldId The world id.
     * @return `True` if so.
     */
    @JvmStatic
    fun isActive(worldId: Int): Boolean {
        val server = get(worldId)
        return server != null && server.isActive
    }

    /**
     * Checks if the player session for the given name is active.
     *
     * @param username The player's username.
     * @return `True` if so.
     */
    fun isActivePlayer(username: String?): Boolean {
        val session = getPlayer(username)
        return session != null && session.isActive
    }

    /**
     * Gets the player session for the given name.
     *
     * @param username The player's username.
     * @return The player session.
     */
    @JvmStatic
    fun getPlayer(username: String?): PlayerSession? {
        return getPlayer(username, false)
    }

    /**
     * Gets the player session for the given name.
     *
     * @param username The player's username.
     * @param load     If we load the users data.
     * @return The player session.
     */
    @JvmStatic
    fun getPlayer(username: String?, load: Boolean): PlayerSession? {
        for (server in worlds) {
            if (server != null && server.isActive) {
                val player = server.players[username]
                if (player != null) {
                    return player
                }
            }
        }
        if (load) {
            return username?.let { PlayerSession.get(it) }
        }
        return null
    }

    /**
     * Gets the game server for the given world id.
     *
     * @param worldId The world id.
     * @return The game server.
     */
    @JvmStatic
    fun get(worldId: Int): GameServer? {
        return worlds[worldId]
    }

    /**
     * Sets the updateStamp.
     *
     * @param updateStamp the updateStamp to set.
     */
    @JvmStatic
    fun flagUpdate() {
        updateStamp = System.currentTimeMillis()
    }
}