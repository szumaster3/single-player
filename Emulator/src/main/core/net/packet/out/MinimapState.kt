package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.MinimapStateContext

/**
 * Handles the sending of the minimap state outgoing packet.
 * @author Emperor
 */
class MinimapState : OutgoingPacket<MinimapStateContext> {
    override fun send(context: MinimapStateContext) {
        val buffer = IoBuffer(192).put(context.state)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.details.session.write(buffer)
    }
}