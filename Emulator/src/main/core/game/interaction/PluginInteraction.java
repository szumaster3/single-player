package core.game.interaction;

import core.game.node.Node;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.plugin.Plugin;

/**
 * Represents an abstract interaction plugin that can handle various types of interactions
 * between a player and different game entities such as nodes, NPCs, and items.
 */
public abstract class PluginInteraction implements Plugin<Object> {

    /**
     * The IDs associated with this interaction, typically used for identifying specific entities.
     */
    int[] ids;

    /**
     * The item associated with this interaction, if applicable.
     */
    Item item;

    /**
     * Handles an interaction between a player and a node.
     *
     * @param player the player performing the interaction
     * @param node   the node being interacted with
     * @return {@code true} if the interaction is successful, otherwise {@code false}
     */
    public boolean handle(Player player, Node node) {
        return false;
    }

    /**
     * Handles an interaction between a player and an event.
     *
     * @param player the player performing the interaction
     * @param event  the event being triggered
     * @return {@code true} if the interaction is successful, otherwise {@code false}
     */
    public boolean handle(Player player, NodeUsageEvent event) {
        return false;
    }

    /**
     * Handles an interaction between a player and an NPC.
     *
     * @param player the player performing the interaction
     * @param npc    the NPC being interacted with
     * @param option the option selected for the interaction
     * @return {@code true} if the interaction is successful, otherwise {@code false}
     */
    public boolean handle(Player player, NPC npc, Option option) {
        return false;
    }

    /**
     * Handles an interaction between a player and an item.
     *
     * @param player the player performing the interaction
     * @param item   the item being interacted with
     * @param option the option selected for the interaction
     * @return {@code true} if the interaction is successful, otherwise {@code false}
     */
    public boolean handle(Player player, Item item, Option option) {
        return false;
    }

    /**
     * Constructs a new PluginInteraction with specified entity IDs.
     *
     * @param ids the IDs of entities associated with this interaction
     */
    public PluginInteraction(int... ids) {
        this.ids = ids;
    }

    /**
     * Constructs a new PluginInteraction with a specific item.
     *
     * @param item the item associated with this interaction
     */
    public PluginInteraction(Item item) {
        this.item = item;
    }

    /**
     * Sets the IDs associated with this interaction.
     *
     * @param ids the new IDs to be associated
     */
    public void setIds(int[] ids) {
        this.ids = ids;
    }

    /**
     * Sets the item associated with this interaction.
     *
     * @param item the new item to be associated
     */
    public void setItem(Item item) {
        this.item = item;
    }
}