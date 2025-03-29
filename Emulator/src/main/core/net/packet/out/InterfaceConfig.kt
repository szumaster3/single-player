package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.InterfaceConfigContext

/**
 * The type Interface config.
 */
class InterfaceConfig : OutgoingPacket<InterfaceConfigContext> {
    override fun send(context: InterfaceConfigContext) {
        val buffer = IoBuffer(21)
        buffer.putC(if (context.isHidden) 1 else 0)
        buffer.putShort(context.player.interfaceManager.getPacketCount(1))
        buffer.putLEInt(context.interfaceId shl 16 or context.childId)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.session.write(buffer)
    }
}
