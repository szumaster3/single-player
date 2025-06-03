package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

/**
 * Represents the Varc update packet context.
 *
 * @author Ceikry
 */
public class VarcUpdateContext implements Context {

    /**
     * The varc id.
     */
    private final int varcId;

    /**
     * The value to update the varc with.
     */
    private final int value;

    /**
     * The player reference.
     */
    private final Player player;

    /**
     * Constructs a new {@code VarcUpdateContext}.
     *
     * @param player The player involved in the update.
     * @param varcId The varc id.
     * @param value  The new value for the varc.
     */
    public VarcUpdateContext(Player player, int varcId, int value) {
        this.player = player;
        this.varcId = varcId;
        this.value = value;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the varc id.
     *
     * @return The varc id.
     */
    public int getVarcId() {
        return varcId;
    }

    /**
     * Gets the value to update the varc with.
     *
     * @return The varc value.
     */
    public int getValue() {
        return value;
    }
}
