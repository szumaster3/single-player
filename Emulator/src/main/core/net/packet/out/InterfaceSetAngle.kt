package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.DefaultContext

/**
 * The type Interface set angle.
 */
class InterfaceSetAngle : OutgoingPacket<DefaultContext> {
    override fun send(context: DefaultContext) {
        val player = context.player
        val objects = context.objects
        val pitch = objects[0] as Int
        val scale = objects[1] as Int
        val yaw = objects[2] as Int
        val interfaceId = objects[3] as Int
        val childId = objects[4] as Int
        val buffer = IoBuffer(132)
        buffer.putShort(pitch)
        buffer.putShortA(player.interfaceManager.getPacketCount(1))
        buffer.putLEShortA(scale)
        buffer.putLEShortA(yaw)
        buffer.putInt(interfaceId shl 16 or childId)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.session.write(buffer)
    }
}