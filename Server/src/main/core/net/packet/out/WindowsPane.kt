package core.net.packet.out

import core.net.packet.IoBuffer
import core.net.packet.OutgoingPacket
import core.net.packet.context.WindowsPaneContext
import shared.consts.Network

/**
 * Handles the windows pane outgoing packet.
 * @author Emperor
 */
class WindowsPane : OutgoingPacket<WindowsPaneContext> {
    override fun send(context: WindowsPaneContext) {
        val buffer = IoBuffer(Network.WINDOW_PANE)
        buffer.cypherOpcode(context.player.session.isaacPair.output)
        buffer.putLEShortA(context.windowId)
        buffer.putS(context.type)
        buffer.putLEShortA(context.player.interfaceManager.getPacketCount(1))
        context.player.details.session.write(buffer)
    }
}