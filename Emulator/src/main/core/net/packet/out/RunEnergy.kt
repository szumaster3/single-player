package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.PlayerContext

/**
 * The type Run energy.
 */
class RunEnergy : OutgoingPacket<PlayerContext> {
    override fun send(context: PlayerContext) {
        val buffer = IoBuffer(234)
        buffer.put(context.player.settings.runEnergy.toInt().toByte().toInt())
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.details.session.write(buffer)
    }
}