package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

/**
 * Represents the varbit packet context.
 *
 * @author Ceikry
 */
public class VarbitContext implements Context {

    /**
     * The varbit id.
     */
    private final int varbitId;

    /**
     * The value to set the varbit to.
     */
    private final int value;

    /**
     * The player reference.
     */
    private final Player player;

    /**
     * Constructs a new {@code VarbitContext}.
     *
     * @param player   The player involved in the context.
     * @param varbitId The varbit id.
     * @param value    The value to assign to the varbit.
     */
    public VarbitContext(Player player, int varbitId, int value) {
        this.player = player;
        this.varbitId = varbitId;
        this.value = value;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the varbit id.
     *
     * @return The varbit id.
     */
    public int getVarbitId() {
        return varbitId;
    }

    /**
     * Gets the value to set the varbit to.
     *
     * @return The varbit value.
     */
    public int getValue() {
        return value;
    }
}