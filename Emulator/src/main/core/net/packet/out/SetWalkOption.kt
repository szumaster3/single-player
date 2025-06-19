package core.net.packet.out

import core.net.packet.OutgoingPacket
import core.net.packet.context.WalkOptionContext

/**
 * Handles the sending of the "Set walk-to option" packet.
 * @author Emperor
 */
class SetWalkOption : OutgoingPacket<WalkOptionContext> {
    override fun send(context: WalkOptionContext) {
        // TODO IoBuffer buffer = new IoBuffer(10,
        // PacketHeader.BYTE).putString(context.getOption());
        // context.getPlayer().getDetails().getSession().write(buffer);
    }
}