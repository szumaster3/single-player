package content.global.skill.hunter;

import core.api.Event;
import core.api.LoginListener;
import core.api.LogoutListener;
import core.game.event.EventHook;
import core.game.event.TickEvent;
import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.scenery.Scenery;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static core.api.ContentAPIKt.setAttribute;

/**
 * Manages Hunter traps and related functionality.
 */
public final class HunterManager implements LoginListener, LogoutListener, EventHook<TickEvent> {

    /**
     * List of traps currently owned by the player.
     */
    private final List<TrapWrapper> traps = new ArrayList<>(20);

    /**
     * The player associated with this HunterManager.
     */
    private final Player player;

    /**
     * Creates a new HunterManager for a player.
     *
     * @param player the player
     */
    public HunterManager(final Player player) {
        this.player = player;
    }

    /**
     * Default constructor. The player is null.
     */
    public HunterManager() {
        this.player = null;
    }

    /**
     * Initializes a HunterManager on player login.
     *
     * @param player the logging-in player
     */
    @Override
    public void login(@NotNull Player player) {
        HunterManager instance = new HunterManager(player);
        player.hook(Event.getTick(), instance);
        setAttribute(player, "hunter-manager", instance);
    }

    /**
     * Cleans up traps on player logout.
     *
     * @param player the logging-out player
     */
    @Override
    public void logout(@NotNull Player player) {
        HunterManager instance = getInstance(player);
        if (instance == null) return;

        Iterator<TrapWrapper> iterator = instance.traps.iterator();
        while (iterator.hasNext()) {
            TrapWrapper wrapper = iterator.next();
            if (wrapper.getType().settings.clear(wrapper, 0)) {
                iterator.remove();
            }
        }
    }

    /**
     * Processes trap cycles on each game tick.
     *
     * @param entity the entity for the tick
     * @param event  the tick event
     */
    @Override
    public void process(@NotNull Entity entity, @NotNull TickEvent event) {
        if (traps.isEmpty()) {
            return;
        }
        Iterator<TrapWrapper> iterator = traps.iterator();
        while (iterator.hasNext()) {
            TrapWrapper wrapper = iterator.next();
            if (wrapper.cycle()) {
                iterator.remove();
            }
        }
    }

    /**
     * Registers a new trap for the player.
     *
     * @param trap    the trap type
     * @param node    the node the trap is placed on
     * @param scenery the scenery object representing the trap
     * @return true if registration was successful
     */
    public boolean register(Traps trap, Node node, final Scenery scenery) {
        final TrapWrapper wrapper = new TrapWrapper(player, trap, scenery);
        trap.settings.reward(player, node, wrapper);
        wrapper.setHook(trap.addHook(wrapper));
        return traps.add(wrapper);
    }

    /**
     * Deregisters a trap.
     *
     * @param wrapper the trap wrapper to remove
     * @return true if successfully removed
     */
    public boolean deregister(final TrapWrapper wrapper) {
        return traps.remove(wrapper);
    }

    /**
     * Checks if the player owns a specific trap.
     *
     * @param scenery the scenery object
     * @return true if the player is the owner
     */
    public boolean isOwner(Scenery scenery) {
        return getUid(scenery) == getUid();
    }

    /**
     * Gets the trap wrapper associated with a scenery object.
     *
     * @param scenery the scenery object
     * @return the trap wrapper, or null if not found
     */
    public TrapWrapper getWrapper(Scenery scenery) {
        for (TrapWrapper wrapper : traps) {
            if (wrapper.getObject() == scenery || (wrapper.getSecondary() != null && wrapper.getSecondary() == scenery)) {
                return wrapper;
            }
        }
        return null;
    }

    /**
     * Checks if the player exceeds the trap limit for a given trap type.
     *
     * @param trap the trap type
     * @return true if the trap limit is exceeded
     */
    public boolean exceedsTrapLimit(Traps trap) {
        if (trap.settings.exceedsLimit(player)) {
            return true;
        }
        return traps.size() + 1 > getMaximumTraps();
    }

    /**
     * Gets the current number of traps the player has placed.
     *
     * @return the number of traps
     */
    public int getTrapAmount() {
        return traps.size();
    }

    /**
     * Calculates the maximum number of traps a player can place based on Hunter level.
     *
     * @return the maximum trap count
     */
    public int getMaximumTraps() {
        final int level = getStaticLevel();
        return level >= 80 ? 5 : level >= 60 ? 4 : level >= 40 ? 3 : level >= 20 ? 2 : 1;
    }

    /**
     * Gets the id of a scenery object.
     *
     * @param scenery the scenery object
     * @return the id
     */
    public int getUid(Scenery scenery) {
        return scenery.getAttributes().getAttribute("trap-uid", 0);
    }

    /**
     * Gets the uid of the player.
     *
     * @return the uid
     */
    public int getUid() {
        return player.getName().hashCode();
    }

    /**
     * Gets the player hunter level.
     *
     * @return the Hunter level
     */
    public int getStaticLevel() {
        return player.getSkills().getStaticLevel(Skills.HUNTER);
    }

    /**
     * @return the player associated with this HunterManager
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the list of traps owned by the player
     */
    public List<TrapWrapper> getTraps() {
        return traps;
    }

    /**
     * Gets the HunterManager instance for a player.
     *
     * @param player the player
     * @return the HunterManager instance
     */
    public static HunterManager getInstance(Player player) {
        return player.getAttribute("hunter-manager");
    }
}