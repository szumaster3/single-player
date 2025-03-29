package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.PlayerContext

/**
 * The type Update random file.
 */
class UpdateRandomFile : OutgoingPacket<PlayerContext> {
    override fun send(context: PlayerContext) {
        val buffer = IoBuffer(211)
        buffer.putInt(1) // Let's assume this is UID.
        buffer.put(0)
        buffer.put(0)
        buffer.put(0)
        buffer.put(0)
        for (i in 0..3) {
            buffer.putInt(i + 100)
        }
        // TODO context.getPlayer().getSession().write(buffer);
    }
}