package core.net.registry

import core.ServerConstants
import core.api.log
import core.auth.UserAccountInfo.Companion.createDefault
import core.cache.misc.buffer.ByteBufferUtils.getString
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.GameWorld.authenticator
import core.net.IoSession
import core.net.packet.`in`.Login
import core.net.packet.`in`.Login.decryptRSABuffer
import core.tools.Log
import java.nio.ByteBuffer
import java.util.*
import java.util.regex.Pattern

/**
 * Handles new account registration processes.
 */
object AccountRegister {
    private val PATTERN: Pattern = Pattern.compile("[a-z0-9_]{1,12}")

    /**
     * Reads and handles registration packets based on their opcode.
     *
     * @param session The client's IO session.
     * @param opcode The operation code representing the type of registration action.
     * @param buffer The buffer containing the incoming data.
     */
    @JvmStatic
    fun read(session: IoSession, opcode: Int, buffer: ByteBuffer) {
        var buffer = buffer
        val day: Int
        val month: Int
        val year: Int
        val country: Int
        val info = createDefault()
        when (opcode) {
            147 -> {
                day = buffer.get().toInt()
                month = buffer.get().toInt()
                year = buffer.getShort().toInt()
                country = buffer.getShort().toInt()
                response(session, RegistryResponse.SUCCESS)
            }

            186 -> {
                val username = getString(buffer).replace(" ", "_").lowercase(Locale.getDefault()).replace("|", "")
                info.username = username
                if (username.length <= 0 || username.length > 12) {
                    response(session, RegistryResponse.INVALID_USERNAME)
                    return
                }
                if (invalidUsername(username)) {
                    response(session, RegistryResponse.INVALID_USERNAME)
                    return
                }

                if (!authenticator.canCreateAccountWith(info)) {
                    response(session, RegistryResponse.NOT_AVAILABLE_USER)
                    return
                }
                response(session, RegistryResponse.SUCCESS)
            }

            36 -> {
                buffer.get() //Useless size being written that is already written in the RSA block
                buffer = Login.decryptRSABuffer(buffer, ServerConstants.EXPONENT, ServerConstants.MODULUS)
                if (buffer.get().toInt() != 10) { //RSA header (aka did this decrypt properly)
                    log(AccountRegister::class.java, Log.ERR, "Decryption failed during registration.")
                    response(session, RegistryResponse.CANNOT_CREATE)
                    return
                }
                buffer.getShort() // random data
                val revision = buffer.getShort().toInt() //revision?
                if (revision != ServerConstants.REVISION) {
                    response(session, RegistryResponse.CANNOT_CREATE)
                    return
                }
                val name = getString(buffer).replace(" ", "_").lowercase(Locale.getDefault()).replace("|", "")
                buffer.getInt()
                val password = getString(buffer)
                info.username = name
                info.password = password
                if (password.length < 5 || password.length > 20) {
                    response(session, RegistryResponse.INVALID_PASS_LENGTH)
                    return
                }
                if (password == name) {
                    response(session, RegistryResponse.PASS_SIMILAR_TO_USER)
                    return
                }
                if (invalidUsername(name)) {
                    response(session, RegistryResponse.INVALID_USERNAME)
                    return
                }
                buffer.getInt()
                buffer.getShort()
                day = buffer.get().toInt()
                month = buffer.get().toInt()
                buffer.getInt()
                year = buffer.getShort().toInt()
                country = buffer.getShort().toInt()
                buffer.getInt()
                if (!authenticator.canCreateAccountWith(info)) {
                    response(session, RegistryResponse.CANNOT_CREATE)
                    return
                }
                authenticator.createAccountWith(info)
                Pulser.submit(object : Pulse() {
                    override fun pulse(): Boolean {
                        response(session, RegistryResponse.SUCCESS)
                        return true
                    }
                })
            }

            else -> log(AccountRegister::class.java, Log.ERR, "Unhandled account registry opcode = $opcode")
        }
    }

    /**
     * Sends a registration response back to the client.
     *
     * @param session The client's IO session.
     * @param response The registration response to send.
     */
    private fun response(session: IoSession, response: RegistryResponse) {
        val buf = ByteBuffer.allocate(100)
        buf.put(response.id.toByte())
        session.queue(buf.flip())
    }

    /**
     * Checks if the provided username is invalid based on a regex pattern.
     *
     * @param username The username to validate.
     * @return True if the username is invalid, false otherwise.
     */
    private fun invalidUsername(username: String): Boolean {
        val matcher = PATTERN.matcher(username)
        return !matcher.matches()
    }
}
