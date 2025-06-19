package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.PacketHeader
import core.net.packet.context.ClanContext
import core.tools.StringUtils.stringToLong

/**
 * Handles the update clan chat outgoing packet.
 *
 * @author Emperor
 */
class UpdateClanChat : OutgoingPacket<ClanContext> {
    override fun send(context: ClanContext) {
        val buffer = IoBuffer(55, PacketHeader.SHORT)
        val clan = context.clan
        if (!context.isLeave) {
            buffer.putLong(stringToLong(clan.owner))
            buffer.putLong(stringToLong(clan.name))
            buffer.put(clan.kickRequirement.value)
            var size = clan.players.size
            if (size > 100) {
                size = 100
            }
            buffer.put(size)
            for (i in 0 until size) {
                val entry = clan.players[i]
                val name = entry.name
                buffer.putLong(stringToLong(name)).putShort(entry.worldId)
                buffer.put(clan.getRank(entry).value)
                buffer.putString("World " + entry.worldId)
            }
        } else {
            buffer.putLong(0)
        }
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        context.player.session.write(buffer)
    }
}