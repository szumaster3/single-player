package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.PacketHeader
import core.net.packet.context.GameMessageContext
import shared.consts.Network

/**
 * The game message outgoing packet.
 * @author Emperor
 */
class GameMessage : OutgoingPacket<GameMessageContext> {
    override fun send(context: GameMessageContext) {
        val buffer = IoBuffer(Network.SEND_GAME_MESSAGE, PacketHeader.BYTE)
        buffer.putString(context.message)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.session.write(buffer)
    }
}