package core.net.packet.out

import core.net.packet.OutgoingPacket
import core.net.packet.context.PlayerContext

/**
 * The type Ping packet.
 */
class PingPacket : OutgoingPacket<PlayerContext> {
    override fun send(context: PlayerContext) {
        // TODO: Find real ping packet, this is actually clear minimap flag
        // packet.
        // context.getPlayer().getDetails().getSession().write(new
        // IoBuffer(47));
    }
}