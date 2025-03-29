package core.net.packet.out

import core.net.packet.OutgoingPacket
import core.net.packet.context.WalkOptionContext

/**
 * The type Set walk option.
 */
class SetWalkOption : OutgoingPacket<WalkOptionContext> {
    override fun send(context: WalkOptionContext) {
        // TODO IoBuffer buffer = new IoBuffer(10,
        // PacketHeader.BYTE).putString(context.getOption());
        // context.getPlayer().getDetails().getSession().write(buffer);
    }
}