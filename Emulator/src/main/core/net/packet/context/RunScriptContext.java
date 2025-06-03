package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

/**
 * Represents a context for running a client script.
 *
 * @author Emperor
 */
public class RunScriptContext implements Context {

    /**
     * The player reference.
     */
    private Player player;

    /**
     * The run script id.
     */
    private final int id;

    /**
     * The script parameters.
     */
    private final Object[] objects;

    /**
     * An optional string parameter for the script.
     */
    private final String string;

    /**
     * Constructs a new {@code RunScriptContext}.
     *
     * @param player  The player who is the context owner.
     * @param id      The run script id.
     * @param string  An optional string parameter for the script.
     * @param objects The parameters for the script.
     */
    public RunScriptContext(Player player, int id, String string, Object... objects) {
        this.player = player;
        this.id = id;
        this.objects = objects;
        this.string = string;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player for this context.
     *
     * @param player The player to set.
     * @return This context instance.
     */
    public Context setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Gets the run script id.
     *
     * @return The run script id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the script parameters.
     *
     * @return The parameters.
     */
    public Object[] getObjects() {
        return objects;
    }

    /**
     * Gets the optional string parameter.
     *
     * @return The string parameter.
     */
    public String getString() {
        return string;
    }
}
