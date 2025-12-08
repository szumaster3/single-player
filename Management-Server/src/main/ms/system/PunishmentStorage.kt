package ms.system

import ms.net.packet.WorldPacketRepository.sendPlayerMessage
import ms.net.packet.WorldPacketRepository.sendPunishUpdate
import ms.system.mysql.SQLManager.close
import ms.system.mysql.SQLManager.connection
import ms.world.PlayerSession
import ms.world.WorldDatabase.getPlayer
import ms.world.WorldDatabase.worlds
import ms.world.info.UIDInfo
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * Used for storing and handling punishment data.
 *
 * @author Emperor
 * @author Vexia
 */
object PunishmentStorage {
    /**
     * The type ids for IP, MAC and SERIAL bans.
     */
    const val IP: Int = 2
    const val MAC: Int = 3
    const val SERIAL: Int = 4

    /**
     * Handles a punishment.
     *
     * @param name     The name of the staff member dealing the punishment.
     * @param target   The target.
     * @param type     The punishment type.
     * @param duration The duration of the punishment (in milliseconds).
     */
    fun handlePunishment(name: String?, target: String, type: Int, duration: Long) {
        val staff = getPlayer(name)
        val player = getPlayer(target, true)
        if (player == null) {
            sendPlayerMessage(staff, "Player $target is invalid!")
            return
        }
        var end = Long.MAX_VALUE
        if (duration != -1L && duration != 0L) {
            end = System.currentTimeMillis() + duration
        } else if (duration == 0L) {
            end = 0L
        }
        var key: String? = "null"
        when (type) {
            0, 1 -> {
                if (type == 1 && end == 0L) {
                    unban(player.ipAddress)
                    unban(player.macAddress)
                    unban(player.serialKey)
                }
                if (player.isActive) {
                    sendPunishUpdate(player.world!!, player.username, type, end)
                }
                notify(staff, if (type == 0) "mute" else "ban", target, end)
                val connection = connection ?: return
                val statement: PreparedStatement
                try {
                    statement =
                        connection.prepareStatement("UPDATE members SET " + (if (type == 0) "muteTime" else "banTime") + "='" + end + "' WHERE username ='" + target + "'")
                    statement.executeUpdate()
                } catch (e: SQLException) {
                    e.printStackTrace()
                    close(connection)
                    return
                } finally {
                    close(connection)
                }
                return
            }

            2 -> {
                ban(player.ipAddress, type)
                notify(staff, "IP-ban", target, end)
                notifyServers(player.ipAddress.also { key = it }, type, end)
            }

            3 -> {
                ban(player.macAddress, type)
                notify(staff, "MAC-ban", target, end)
                notifyServers(player.macAddress.also { key = it }, type, end)
            }

            4 -> {
                ban(player.serialKey, type)
                notify(staff, "UID-ban", target, end)
                notifyServers(player.serialKey.also { key = it }, type, end)
            }

            5 -> {
                ban(player.ipAddress, 2)
                notifyServers(player.ipAddress.also { key = it }, 2, end)
                ban(player.macAddress, 3)
                notifyServers(player.macAddress.also { key = it }, 3, end)
                ban(player.serialKey, 4)
                notifyServers(player.serialKey.also { key = it }, 4, end)
                notify(staff, "full ban", target, end)
                return
            }

            6 -> if (player.isActive) {
                sendPunishUpdate(player.world!!, player.username, 6, end)
                sendPlayerMessage(staff, "Successfully kicked player " + target + " from world " + player.worldId + ".")
            } else {
                sendPlayerMessage(staff, "Player $target was already inactive.")
            }

            7 -> {
                sendPlayerMessage(staff, "[----------Player info----------]")
                sendPlayerMessage(staff, "Name: " + player.username)
                sendPlayerMessage(staff, "IP address: " + player.ipAddress)
                sendPlayerMessage(staff, "MAC address: " + player.macAddress)
                sendPlayerMessage(staff, "Serial key: " + player.serialKey)
                sendPlayerMessage(staff, "Computer name: " + player.computerName)
                sendPlayerMessage(staff, "[-------------------------------]")
                return
            }
        }
        notifyServers(key, type, end)
    }

    /**
     * Bans an address.
     *
     * @param address the address.
     * @return `True` if banned.
     */
    fun ban(address: String?, type: Int): Boolean {
        if (address == null || address.length == 0 || address == "To be filled by O.E.M." || address == "To be filled by O.E.M" || address == "Base Board Serial Number") {
            println("Error! Can't ban address $address type = $type!")
            return false
        }
        if (isBanned(address)) {
            return false
        }
        val connection = connection ?: return false
        val statement: PreparedStatement
        try {
            statement = connection.prepareStatement("INSERT INTO punishments VALUES('$address','$type')")
            statement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            close(connection)
            return false
        } finally {
            close(connection)
        }
        return true
    }

    /**
     * Unbans an address.
     *
     * @param address the address.
     * @return `True` if unbanned.
     */
    fun unban(address: String): Boolean {
        val connection = connection ?: return false
        val statement: PreparedStatement
        try {
            statement = connection.prepareStatement("DELETE from punishments WHERE address ='$address'")
            statement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
            close(connection)
            return false
        } finally {
            close(connection)
        }
        return true
    }

    /**
     * Notifies the punishing player of success.
     *
     * @param staff  The punishing player.
     * @param type   The punishment type.
     * @param target The target name.
     * @param end    The end time stamp of the punishment.
     */
    private fun notify(staff: PlayerSession?, type: String, target: String, end: Long) {
        if (end <= System.currentTimeMillis()) {
            sendPlayerMessage(staff, "Successfully removed punishment [type=$type, player=$target].")
            return
        }
        sendPlayerMessage(
            staff,
            "Successfully punished player " + target + " [type=" + type + ", duration=" + getDuration(end) + "]."
        )
    }

    /**
     * Notifies the game servers of a punishment update.
     *
     * @param key      The punishment key.
     * @param type     The type.
     * @param duration The duration.
     */
    fun notifyServers(key: String?, type: Int, duration: Long) {
        for (server in worlds) {
            if (server != null && server.isActive) {
                sendPunishUpdate(server, key!!, type, duration)
            }
        }
    }

    /**
     * Checks if the UID Info is banned.
     *
     * @param info the info.
     * @return `True` if banned.
     */
    fun isSystemBanned(info: UIDInfo): Boolean {
        return isBanned(info.ip) || isBanned(info.mac) || isBanned(info.serial)
    }

    /**
     * Checked if an address is banned.
     *
     * @param address the address.
     * @param type    the type.
     * @return `True` if so.
     */
    fun isBanned(address: String?): Boolean {
        val connection = connection ?: return false
        try {
            val set = connection.createStatement().executeQuery("SELECT * FROM punishments WHERE address ='$address'")
            if (set == null || !set.next()) {
                close(connection)
                return false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            close(connection)
            return false
        } finally {
            close(connection)
        }
        return true
    }

    /**
     * Gets the duration string representation.
     *
     * @param end The end time.
     * @return The string.
     */
    private fun getDuration(end: Long): String {
        var time = "indefinite time"
        if (end != Long.MAX_VALUE) {
            val days = ((System.currentTimeMillis()) / (24 * 60 * 60000)).toInt()
            val hours = (((24L * days * 60 * 60000)) / (60 * 60000)).toInt()
            val minutes = ((hours * (60 * 60000))) / 60000
            time = days.toString() + "d, " + hours + "h, " + minutes + "m"
        }
        return time
    }
}