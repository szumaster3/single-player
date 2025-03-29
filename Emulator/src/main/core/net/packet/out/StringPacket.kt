package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.PacketHeader
import core.net.packet.context.StringContext

/**
 * The type String packet.
 */
class StringPacket : OutgoingPacket<StringContext> {
    override fun send(context: StringContext) {
        val buffer = IoBuffer(171, PacketHeader.SHORT)
        buffer.putIntB((context.interfaceId shl 16) or context.lineId)
        buffer.putString(context.string)
        buffer.putShortA(context.player.interfaceManager.getPacketCount(1))
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.details.session.write(buffer)
    }
}
