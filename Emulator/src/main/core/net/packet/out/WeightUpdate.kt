package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.PlayerContext

/**
 * Handles the outgoing weight update packet.
 * @author Emperor
 */
class WeightUpdate : OutgoingPacket<PlayerContext> {
    override fun send(context: PlayerContext) {
        val buffer = IoBuffer(174)
        buffer.putShort(context.player.settings.weight.toInt())
        // TODO context.getPlayer().getSession().write(buffer);
    }
}