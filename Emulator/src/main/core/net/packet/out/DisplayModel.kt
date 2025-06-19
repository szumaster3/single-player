package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.DisplayModelContext
import core.net.packet.context.DisplayModelContext.ModelType

/**
 * Represents the outgoing packet for the displaying of a node model on an interface.
 *
 * @author Emperor
 */
class DisplayModel : OutgoingPacket<DisplayModelContext> {
    override fun send(context: DisplayModelContext) {
        val buffer: IoBuffer
        when (context.type) {
            ModelType.PLAYER -> {
                buffer = IoBuffer(66)
                buffer.putLEShortA(context.player.interfaceManager.getPacketCount(1))
                buffer.putIntA(context.interfaceId shl 16 or context.childId)
            }

            ModelType.NPC -> {
                buffer = IoBuffer(73)
                buffer.putShortA(context.nodeId)
                buffer.putLEInt((context.interfaceId shl 16) or context.childId)
                buffer.putLEShort(context.player.interfaceManager.getPacketCount(1))
            }

            ModelType.ITEM -> {
                val value = if (context.amount > 0) context.amount else context.zoom
                buffer = IoBuffer(50)
                buffer.putInt(value)
                buffer.putIntB((context.interfaceId shl 16) or context.childId)
                buffer.putLEShortA(context.nodeId)
                buffer.putLEShort(context.player.interfaceManager.getPacketCount(1))
            }

            ModelType.MODEL -> {
                buffer = IoBuffer(130)
                buffer.putLEInt(context.interfaceId shl 16 or context.childId)
                buffer.putLEShortA(context.player.interfaceManager.getPacketCount(1))
                buffer.putShortA(context.nodeId)
            }

            else -> return
        }
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.session.write(buffer)
    }
}