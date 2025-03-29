package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.ChildPositionContext

/**
 * The type Reposition child.
 */
class RepositionChild : OutgoingPacket<ChildPositionContext> {
    override fun send(context: ChildPositionContext) {
        val buffer = IoBuffer(119)
            .putShortA(context.player.interfaceManager.getPacketCount(1))
            .putLEInt(context.interfaceId shl 16 or context.childId)
            .putShort(context.position.x)
            .putShortA(context.position.y)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.session.write(buffer)
    }
}