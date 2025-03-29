package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.PacketHeader
import core.net.packet.context.ContactContext
import core.tools.StringUtils.stringToLong

/**
 * The type Contact packets.
 */
class ContactPackets : OutgoingPacket<ContactContext> {
    override fun send(context: ContactContext) {
        var buffer: IoBuffer? = null
        val player = context.player
        when (context.type) {
            ContactContext.UPDATE_STATE_TYPE -> buffer = IoBuffer(197).put(2) //always put the AVAILABLE state.
            ContactContext.IGNORE_LIST_TYPE -> {
                buffer = IoBuffer(126, PacketHeader.SHORT)
                for (string in player.communication.blocked) {
                    if (string.length == 0) {
                        continue
                    }
                    buffer.putLong(stringToLong(string))
                }
            }

            ContactContext.UPDATE_FRIEND_TYPE -> {
                buffer = IoBuffer(62, PacketHeader.BYTE)
                buffer.putLong(stringToLong(context.name))
                buffer.putShort(context.worldId)
                val c = player.communication.contacts[context.name]
                if (c != null) {
                    buffer.put(c.rank.value.toByte().toInt())
                } else {
                    buffer.put(0.toByte().toInt())
                }
                if (context.isOnline) {
                    buffer.putString("World " + context.worldId)
                }
            }
        }
        if (buffer != null) {
            buffer.cypherOpcode(context.player.session.isaacPair.output)
            player.session.write(buffer)
        }
    }
}