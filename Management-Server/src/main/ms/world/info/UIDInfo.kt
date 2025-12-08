package ms.world.info

import ms.system.util.ByteBufferUtils
import java.nio.ByteBuffer
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * The unique info of a player.
 *
 * @author Vexia
 */
class UIDInfo {
    /**
     * The ip address.
     */
    var ip: String? = null

    /**
     * The computer name.
     */
    var compName: String? = null

    /**
     * The mac-address.
     */
    var mac: String? = null

    /**
     * The motherboard serial of the user.
     */
    var serial: String? = null


    /**
     * Constructs a new `UIDInfo` `Object`
     */
    constructor()

    /**
     * Constructs a new `UIDInfo` `Object`
     *
     * @param ip       the ip.
     * @param compName the computer name.
     * @param mac      the mac.
     * @param serial   the serial.
     */
    constructor(ip: String?, compName: String?, mac: String?, serial: String?) {
        this.ip = ip
        this.compName = compName
        this.mac = mac
        this.serial = serial
    }

    /**
     * Parses the data from a prepared statement.
     *
     * @param set The result set
     * @throws SQLException The exception if thrown.
     */
    @Throws(SQLException::class)
    fun parse(set: ResultSet) {
        ip = parseFormat(set.getString("ip"))
        compName = parseFormat(set.getString("computerName"))
        mac = parseFormat(set.getString("mac"))
        serial = parseFormat(set.getString("serial"))
    }

    /**
     * Saves the UID data on the buffer.
     *
     * @param buffer The buffer.
     */
    fun save(buffer: ByteBuffer) {
        save(buffer, ip, 1)
        save(buffer, compName, 2)
        save(buffer, mac, 3)
        save(buffer, serial, 4)
        buffer.put(0.toByte())
    }

    /**
     * Parses the UID data from the buffer.
     *
     * @param buffer The buffer.
     */
    fun parse(buffer: ByteBuffer) {
        var opcode: Int
        while (buffer.get().also { opcode = it.toInt() }.toInt() != 0) {
            when (opcode) {
                1 -> ip = ByteBufferUtils.getString(buffer)
                2 -> compName = ByteBufferUtils.getString(buffer)
                3 -> mac = ByteBufferUtils.getString(buffer)
                4 -> serial = ByteBufferUtils.getString(buffer)
                else -> {}
            }
        }
    }

    /**
     * Parses a string with a certain format.
     *
     * @param string the string.
     * @return the string.
     */
    private fun parseFormat(string: String?): String? {
        if (string == null || string == "") {
            return null
        }
        val token = StringTokenizer(string, "|")
        var s: String? = ""
        val t = token.countTokens()
        for (i in 0 until t) {
            s = token.nextToken()
        }
        return s
    }

    /**
     * Saves a string value to the buffer.
     *
     * @param buffer the buffer.
     * @param value  the value.
     * @param opcode the opcode.
     */
    private fun save(buffer: ByteBuffer, value: String?, opcode: Int) {
        if (value == null) {
            return
        }
        ByteBufferUtils.putString(value, buffer.put(opcode.toByte()))
    }

    /**
     * Converts a to string in format mode for an admin or mod.
     *
     * @param admin the admin.
     * @return the string.
     */
    fun toString(admin: Boolean): String {
        var format = toString()
        if (!admin) { //formats for non-admins.
            val tokens = format.split("serial=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            format = format.replace("serial=", "uid=").replace(tokens[tokens.size - 1], "*****")
        }
        return format
    }

    override fun toString(): String {
        return "[ip=$ip, compName=$compName, mac=$mac, serial=$serial]"
    }
}
