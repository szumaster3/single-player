package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.MusicContext

/**
 * The type Music packet.
 */
class MusicPacket : OutgoingPacket<MusicContext> {
    override fun send(context: MusicContext) {
        var buffer: IoBuffer? = null
        if (context.isSecondary) {
            buffer = IoBuffer(208)
            buffer.putTri(255)
            buffer.putLEShort(context.musicId)
        } else {
            buffer = IoBuffer(4)
            buffer.putLEShortA(context.musicId)
        }
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.details.session.write(buffer)
    }
}
