package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.PlayerContext

/**
 * The type Clear minimap flag.
 */
class ClearMinimapFlag : OutgoingPacket<PlayerContext> {
    override fun send(context: PlayerContext) {
        val buffer = IoBuffer(153)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.details.session.write(buffer)
    }
}