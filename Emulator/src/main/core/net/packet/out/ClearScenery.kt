package core.net.packet.out

import core.game.node.scenery.Scenery
import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.BuildSceneryContext

/**
 * The type Clear scenery.
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
         * Write io buffer.
         *
         * @param buffer the buffer
         * @param object the object
         * @return the io buffer
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