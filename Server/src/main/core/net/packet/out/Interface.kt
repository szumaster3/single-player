package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.InterfaceContext
import shared.consts.Network

/**
 * The interface outgoing packet.
 * @author Emperor
 */
class Interface : OutgoingPacket<InterfaceContext> {
    override fun send(context: InterfaceContext) {
        val buffer = IoBuffer(Network.SEND_INTERFACE)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        buffer.put(if (context.isWalkable) 1 else 0)
        buffer.putIntB(context.windowId shl 16 or context.componentId)
            .putShortA(context.player.interfaceManager.getPacketCount(1)).putShort(context.interfaceId)
        context.player.details.session.write(buffer)
    }
}