package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.PacketHeader
import core.net.packet.context.RunScriptContext
import shared.consts.Network

/**
 * The run script outgoing packet.
 * @author Snickerize
 */
class RunScriptPacket : OutgoingPacket<RunScriptContext> {
    override fun send(context: RunScriptContext) {
        val types = context.string
        val objects = context.objects
        val buffer = IoBuffer(Network.RUN_SCRIPT, PacketHeader.SHORT)
        buffer.putShort(context.player.interfaceManager.getPacketCount(1))

        buffer.putString(types)
        for ((j, i) in ((types.length - 1) downTo 0).withIndex()) {
            if (types[i] == 's') {
                buffer.putString(objects[j] as String)
            } else {
                buffer.putInt((objects[j] as Int))
            }
        }

        buffer.putInt(context.id)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.details.session.write(buffer)
    }
}