package ms.world

import ms.ServerConstants
import ms.system.communication.ClanRepository
import ms.system.communication.CommunicationInfo
import ms.system.mysql.SQLManager.close
import ms.system.mysql.SQLManager.connection
import ms.world.info.UIDInfo
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * Represents a player session.
 *
 * @author Emperor
 */
class PlayerSession(
    val username: String, val password: String, var uid: UIDInfo
) {

    /**
     * The player's communication info.
     */
    var communication: CommunicationInfo = CommunicationInfo(this)

    /**
     * The game server the player is currently in.
     */
    var world: GameServer? = null

    /**
     * The current clan.
     */
    var clan: ClanRepository? = null

    /**
     * The player's rights.
     */
    var rights: Int = 0

    /**
     * The world id.
     */
    var worldId: Int = 0

    /**
     * If the player session is active.
     */
    var isActive: Boolean = false

    /**
     * The time stamp of last disconnection.
     */
    var disconnectionTime: Long = 0L
        private set

    /**
     * How long the player is banned for.
     */
    var banTime: Long = 0
        private set

    /**
     * How long the player is muted for.
     */
    var muteTime: Long = 0

    /**
     * The last world the player logged in.
     */
    var lastWorld: Int = -1

    /**
     * The chat icon.
     */
    var chatIcon: Int = 0

    /**
     * Parses the session's data from the database.
     *
     * @return `True` if parsed.
     */
    fun parse(): Boolean {
        val connection = connection ?: return false
        var result: ResultSet? = null
        val statement: PreparedStatement
        try {
            statement = connection.prepareStatement(
                "SELECT * FROM " + "members" + " WHERE " + "username" + "='" + username.lowercase(Locale.getDefault()) + "' LIMIT 1"
            )
            result = statement.executeQuery()
            if (result == null || !result.next()) {
                close(connection)
                return false
            }
            rights = result.getInt("rights")
            disconnectionTime = result.getLong("disconnectTime")
            lastWorld = result.getInt("lastWorld")
            banTime = result.getLong("banTime")
            communication.parse(result)
            uid.parse(result)
            close(connection)
        } catch (ex: SQLException) {
            ex.printStackTrace()
            close(connection)
            return false
        } finally {
            close(connection)
        }
        return true
    }

    /**
     * Called when a session is removed.
     */
    fun remove() {
        if (world != null && world!!.info.revision == 498) {
            return
        }
        val connection = connection ?: return
        val statement: PreparedStatement
        try {
            statement =
                connection.prepareStatement("UPDATE members SET disconnectTime = ?, lastWorld = ?, contacts = ?, blocked = ?, clanName = ?, currentClan = ?, clanReqs = ?, chatSettings = ?, online= ? WHERE username = ?")
            statement.setLong(1, System.currentTimeMillis())
            statement.setInt(2, worldId)
            communication.save(statement)
            statement.setBoolean(9, false)
            statement.setString(10, username)
            statement.executeUpdate()
            close(connection)
        } catch (e: SQLException) {
            e.printStackTrace()
            close(connection)
        } finally {
            close(connection)
        }
        communication.clear()
    }

    /**
     * Configures the player session.
     */
    fun configure() {
        val clan: ClanRepository = ClanRepository.clans[username]!!
        if (clan != null) {
            clan.setOwner(this)
        }
        val connection = connection ?: return
        val statement: PreparedStatement
        try {
            statement =
                connection.prepareStatement("UPDATE members SET online = ?, lastWorld = ?, lastLogin = ? WHERE username = ?")
            statement.setBoolean(1, true)
            statement.setInt(2, worldId)
            statement.setLong(3, System.currentTimeMillis())
            statement.setString(4, username)
            statement.execute()
            close(connection)
        } catch (e: SQLException) {
            e.printStackTrace()
            close(connection)
        } finally {
            close(connection)
        }
    }

    /**
     * Checks if the player has just moved worlds.
     *
     * @return `True` if so.
     */
    fun hasMovedWorld(): Boolean {
        if (rights == 2) {
            return false
        }
        return System.currentTimeMillis() - disconnectionTime < ServerConstants.WORLD_SWITCH_DELAY
    }

    val ipAddress: String
        /**
         * Gets the ipAddress value.
         *
         * @return The ipAddress.
         */
        get() = uid.ip.toString()

    val macAddress: String
        /**
         * Gets the macAddress value.
         *
         * @return The macAddress.
         */
        get() = uid.mac.toString()

    val computerName: String
        /**
         * Gets the computerName value.
         *
         * @return The computerName.
         */
        get() = uid.compName.toString()

    val serialKey: String
        /**
         * Gets the serialKey value.
         *
         * @return The serialKey.
         */
        get() = uid.serial.toString()

    val isBanned: Boolean
        /**
         * If the player is banned.
         *
         * @return `true` if so.
         */
        get() = banTime > System.currentTimeMillis()

    /**
     * Sets the disconnect time.
     *
     * @param time the time.
     */
    fun setDisconnectTime(time: Long) {
        this.disconnectionTime = time
    }

    fun equalsSession(o: PlayerSession): Boolean {
        return username == o.username
    }

    override fun toString(): String {
        return "player [name=" + username + ", pass=" + password + ", ip=" + uid.ip + ", mac=" + uid.mac + ", comp=" + uid.compName + ", msk=" + uid.serial + "]"
    }

    companion object {
        /**
         * Gets a player session.
         *
         * @param username the username.
         * @return the player session.
         */
        fun get(username: String): PlayerSession? {
            val session = PlayerSession(username, "", UIDInfo())
            if (!session.parse()) {
                return null
            }
            return session
        }
    }
}