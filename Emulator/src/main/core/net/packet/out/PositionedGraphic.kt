package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.PositionedGraphicContext

/**
 * The positioned graphic outgoing packet.
 *
 * @author Emperor
 */
class PositionedGraphic : OutgoingPacket<PositionedGraphicContext> {
    override fun send(context: PositionedGraphicContext) {
        val l = context.location
        val g = context.graphic
        val offsetHash = (context.offsetX shl 4) or context.offsetY
        val buffer = IoBuffer()
            .put(26)                 // update current scene x and scene y client-side
            .putC(context.sceneX)        // this has to be done for each graphics being sent
            .put(context.sceneY)         // opcode 26 is the lastSceneX/lastSceneY update packet
            .put(17).put(offsetHash) // send the graphics
            .putShort(g.id)
            .put(g.height)
            .putShort(g.delay)
        context.player.session.write(buffer)
    }
}