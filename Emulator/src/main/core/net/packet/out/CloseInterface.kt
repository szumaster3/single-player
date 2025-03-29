package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.InterfaceContext

/**
 * The type Close interface.
 */
class CloseInterface : OutgoingPacket<InterfaceContext> {
    override fun send(context: InterfaceContext) {
        val buffer = IoBuffer(149)
        buffer.putShort(context.player.interfaceManager.getPacketCount(1))
        buffer.putShort(context.windowId)
        buffer.putShort(context.componentId)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.session.write(buffer)
    }
}