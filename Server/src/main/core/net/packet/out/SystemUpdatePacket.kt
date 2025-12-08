package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.SystemUpdateContext

/**
 * Handles the system update packet.
 * @author Vexia
 */
class SystemUpdatePacket : OutgoingPacket<SystemUpdateContext> {
    override fun send(context: SystemUpdateContext) {
        val buffer = IoBuffer(85).putShort(context.time)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.details.session.write(buffer)
    }
}
