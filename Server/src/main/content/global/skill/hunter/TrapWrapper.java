package content.global.skill.hunter;

import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.world.GameWorld;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wrapper around a Hunter trap.
 */
public final class TrapWrapper {

    /** List of items currently in the trap. */
    private final List<Item> items = new ArrayList<>(10);

    /** The player who owns this trap. */
    private final Player player;

    /** The trap type (BIRD_SNARE, BOX_TRAP, etc.). */
    private final Traps type;

    /** The net trap type if applicable. */
    private NetTrapSetting.NetTrap netType;

    /** The original scenery ID when the trap was placed. */
    private final int originalId;

    /** The main scenery object representing the trap. */
    private Scenery scenery;

    /** A secondary scenery object if used (e.g., nets). */
    private Scenery secondary;

    /** Associated trap hook for NPC interactions. */
    private TrapHook hook;

    /** The trap node representing the reward when caught. */
    private TrapNode reward;

    /** Whether the trap has been smoked. */
    private boolean smoked;

    /** Whether the trap has been baited. */
    private boolean baited;

    /** Whether the trap has failed. */
    private boolean failed;

    /** Ticks until the trap becomes busy. */
    private int busyTicks;

    /** The game tick when the trap expires. */
    private int ticks;

    /** The HunterManager instance for the owning player. */
    private final HunterManager instance;

    /**
     * Creates a new TrapWrapper for a given player, trap type, and scenery.
     *
     * @param player  the owner of the trap
     * @param type    the trap type
     * @param scenery the scenery object representing the trap
     */
    public TrapWrapper(final Player player, Traps type, Scenery scenery) {
        this.player = player;
        this.type = type;
        this.scenery = scenery;
        this.originalId = scenery.getId();
        this.ticks = GameWorld.getTicks() + 100;
        this.instance = HunterManager.getInstance(player);
        this.scenery.getAttributes().setAttribute("trap-uid", instance.getUid());
    }

    /**
     * Checks if the trap cycle has expired and handles clearing.
     *
     * @return true if the trap was cleared, false otherwise
     */
    public boolean cycle() {
        if (isTimeUp() && type.settings.clear(this, 0)) {
            if (!isCaught()) {
                player.sendMessage(type.settings.getTimeUpMessage());
            }
            return true;
        }
        return false;
    }

    /**
     * Replaces the trap's scenery object with a new object id.
     *
     * @param id the new object id
     */
    public void setObject(final int id) {
        Scenery newScenery = scenery.transform(id);
        SceneryBuilder.remove(scenery);
        this.scenery = SceneryBuilder.add(newScenery);
        this.scenery.getAttributes().setAttribute("trap-uid", instance.getUid());
    }

    /**
     * Smokes the trap to remove the player's scent.
     */
    public void smoke() {
        if (smoked) {
            player.sendMessage("This trap has already been smoked.");
            return;
        }
        if (player.skills.getStaticLevel(Skills.HUNTER) < 39) {
            player.sendMessage("You need a Hunter level of at least 39 to be able to smoke traps.");
            return;
        }
        smoked = true;
        player.lock(4);
        player.visualize(new Animation(5208), new Graphics(931));
        player.sendMessage("You use the smoke from the torch to remove your scent from the trap.");
    }

    /**
     * Baits the trap with the specified item.
     *
     * @param bait the item used as bait
     */
    public void bait(Item bait) {
        if (baited) {
            player.sendMessage("This trap has already been baited.");
            return;
        }
        if (!type.settings.hasBait(bait)) {
            player.sendMessage("You can't use that on this trap.");
            return;
        }
        baited = true;
        bait = new Item(bait.getId(), 1);
        player.getInventory().remove(new Item(bait.getId(), 1));
    }

    /**
     * Calculates the current chance modifier for catching.
     *
     * @return the chance rate modifier
     */
    public double getChanceRate() {
        double chance = 0.0;
        if (baited) {
            chance += 1.0;
        }
        if (smoked) {
            chance += 1.0;
        }
        chance += HunterGear.getChanceRate(player);
        return chance;
    }

    /**
     * Adds multiple items to the trap's reward list.
     *
     * @param items the items to add
     */
    public void addItem(Item... items) {
        for (Item item : items) {
            addItem(item);
        }
    }

    /**
     * Adds a single item to the trap's reward list.
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * @return the trap type
     */
    public Traps getType() {
        return type;
    }

    /**
     * @return the primary scenery object
     */
    public Scenery getObject() {
        return scenery;
    }

    /**
     * @return the original object ID when the trap was placed
     */
    public int getOriginalId() {
        return originalId;
    }

    /**
     * @return the player who owns this trap
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the game tick when the trap expires
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * Sets the expiration tick for the trap
     */
    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    /**
     * @return whether the trap has been smoked
     */
    public boolean isSmoked() {
        return smoked;
    }

    public void setSmoked(boolean smoked) {
        this.smoked = smoked;
    }

    /**
     * @return the associated trap hook
     */
    public TrapHook getHook() {
        return hook;
    }

    public void setHook(TrapHook hook) {
        this.hook = hook;
    }

    /**
     * @return whether the trap has been baited
     */
    public boolean isBaited() {
        return baited;
    }

    public void setBaited(boolean baited) {
        this.baited = baited;
    }

    /**
     * @return whether the trap has caught an NPC
     */
    public boolean isCaught() {
        return getReward() != null;
    }

    /**
     * @return the trap node representing the reward
     */
    public TrapNode getReward() {
        return reward;
    }

    public void setReward(TrapNode reward) {
        this.reward = reward;
        this.addItem(reward.rewards);
    }

    /**
     * @return whether the trap is busy
     */
    public boolean isBusy() {
        return getBusyTicks() > GameWorld.getTicks();
    }

    /**
     * @return the busy ticks counter
     */
    public int getBusyTicks() {
        return busyTicks;
    }

    public void setBusyTicks(int busyTicks) {
        this.busyTicks = GameWorld.getTicks() + busyTicks;
    }

    /**
     * @return the list of items in the trap
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * @return the secondary scenery object, if any
     */
    public Scenery getSecondary() {
        return secondary;
    }

    public void setSecondary(Scenery secondary) {
        this.secondary = secondary;
        this.secondary.getAttributes().setAttribute("trap-uid", player.getName().hashCode());
    }

    /** @return the net trap type, if applicable */
    public NetTrapSetting.NetTrap getNetType() {
        return netType;
    }

    public void setNetType(NetTrapSetting.NetTrap netType) {
        this.netType = netType;
    }

    public void setObject(Scenery scenery) {
        this.scenery = scenery;
    }

    /** @return whether the trap has failed */
    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    /**
     * @return true if the trap's time has expired
     */
    private boolean isTimeUp() {
        return ticks < GameWorld.getTicks();
    }
}
