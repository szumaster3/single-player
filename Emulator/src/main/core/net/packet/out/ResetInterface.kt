package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.InterfaceContext

/**
 * Sends a reset interface packet to the client.
 *
 * @author Emperor
 */
class ResetInterface : OutgoingPacket<InterfaceContext> {
    override fun send(ic: InterfaceContext) {
        val buffer = IoBuffer(149)
        buffer.putShort(ic.player.interfaceManager.getPacketCount(1))
        buffer.putInt(ic.interfaceId)
        ic.player.session.write(buffer)
    }
}
