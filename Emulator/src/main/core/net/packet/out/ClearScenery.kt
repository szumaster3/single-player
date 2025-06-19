package core.net.packet.out

import core.game.node.scenery.Scenery
import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.BuildSceneryContext

/**
 * The outgoing packet for clearing scenery.
 *
 * @author Emperor
 */
class ClearScenery : OutgoingPacket<BuildSceneryContext> {
    override fun send(context: BuildSceneryContext) {
        val player = context.player
        val o = context.scenery
        val buffer = write(UpdateAreaPosition.getBuffer(player, o.location.chunkBase), o)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        player.session.write(buffer)
    }

    companion object {
        /**
         * Writes the clear scenery data to the given buffer.
         *
         * @param buffer The buffer to write to.
         * @param scenery The scenery to clear.
         * @return The buffer with the written data.
         */
        @JvmStatic
        fun write(buffer: IoBuffer, `object`: Scenery): IoBuffer {
            val l = `object`.location
            buffer.put(195) // Opcode
                .putC((`object`.type shl 2) + (`object`.rotation and 3))
                .put((l.chunkOffsetX shl 4) or (l.chunkOffsetY and 0x7))
            return buffer
        }
    }
}