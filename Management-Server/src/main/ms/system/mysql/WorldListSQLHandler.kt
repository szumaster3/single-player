package ms.system.mysql

import ms.system.mysql.SQLManager.close
import ms.system.mysql.SQLManager.connection
import ms.world.GameServer
import java.sql.Connection
import java.sql.SQLException

/**
 * Handles the world list SQL database.
 *
 * @author Emperor
 */
class WorldListSQLHandler
/**
 * Constructs a new `WorldListSQLHandler` `Object`.
 *
 * @param entry The game server entry.
 */
    (entry: GameServer) : SQLEntryHandler<GameServer?>(entry, TABLE, "world", "" + entry.info.worldId) {
    @Throws(SQLException::class)
    override fun parse() {
    }

    @Throws(SQLException::class)
    override fun create() {
        val statement =
            connection.prepareStatement("INSERT " + table + "(world, ip, players, country, member, revision) VALUES('" + value + "', '" + entry!!.info.address + "', '" + entry!!.playerAmount + "', '" + entry!!.info.country.id + "', '" + (if (entry!!.info.isMembers) 1 else 0) + "', '" + entry!!.info.revision + "')")
        statement.executeUpdate()
        close(statement.connection)
    }

    @Throws(SQLException::class)
    override fun save() {
        super.read()
        if (result == null || !result.next()) {
            create()
            return
        }
        var players = entry!!.playerAmount
        val info = entry!!.info
        if (!entry!!.isActive) {
            players = -1
        }
        if (players <= 0) {
            val statement = connection.prepareStatement("UPDATE members SET online='0' WHERE lastWorld='$value'")
            statement.executeUpdate()
        }
        val statement =
            connection.prepareStatement("UPDATE " + table + " SET players='" + players + "', ip='" + info.address + "', country='" + info.country.id + "', member='" + (if (info.isMembers) 1 else 0) + "', revision='" + info.revision + "' WHERE world='" + value + "'")
        statement.executeUpdate()
        close(statement.connection)
    }

    override fun getConnection(): Connection {
        return SQLManager.connection!!
    }

    companion object {
        /**
         * The table for this sql entry.
         */
        private const val TABLE = "worlds"

        /**
         * Clears the world list.
         */
        fun clearWorldList() {
            val connection = connection ?: return
            try {
                val statement = connection.prepareStatement("DELETE FROM " + TABLE)
                statement.executeUpdate()
            } catch (e: Exception) {
                e.printStackTrace()
                close(connection)
            } finally {
                close(connection)
            }
        }
    }
}