package core.net.packet.out

import core.game.world.map.Location
import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.CameraContext
import core.net.packet.context.CameraContext.CameraType

/**
 * Handles the outgoing camera view packets.
 *
 * @author Emperor
 */
class CameraViewPacket : OutgoingPacket<CameraContext> {
    override fun send(context: CameraContext) {
        val type = context.type
        val buffer = IoBuffer(type.opcode())
        val l = Location.create(context.x, context.y, 0)
        val p = context.player
        when (type) {
            CameraType.ROTATION, CameraType.POSITION -> {
                buffer.putShort(context.player.interfaceManager.getPacketCount(1))
                val x = l.getSceneX(p.playerFlags.lastSceneGraph)
                val y = l.getSceneY(p.playerFlags.lastSceneGraph)
                buffer.put(x).put(y).putShort(context.height).put(context.speed).put(context.zoomSpeed)
            }

            CameraType.SET -> buffer.putLEShort(context.x)
                .putShort(context.player.interfaceManager.getPacketCount(1)).putShort(context.y)

            CameraType.SHAKE -> {
                buffer.putShort(context.player.interfaceManager.getPacketCount(1))
                buffer.put(l.x).put(l.y).put(context.speed).put(context.zoomSpeed).putShort(context.height)
            }

            CameraType.RESET -> buffer.putShort(context.player.interfaceManager.getPacketCount(1))
        }
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        p.session.write(buffer)
    }
}
