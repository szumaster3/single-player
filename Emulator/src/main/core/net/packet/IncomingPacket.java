package core.net.packet;

import core.game.node.entity.player.Player;

/**
 * Represents an incoming packet.
 *
 * @author Emperor
 */
public interface IncomingPacket {

    /**
     * Decodes the incoming packet.
     *
     * @param player The player.
     * @param opcode The opcode.
     * @param buffer The buffer.
     */
    public void decode(Player player, int opcode, IoBuffer buffer);

}