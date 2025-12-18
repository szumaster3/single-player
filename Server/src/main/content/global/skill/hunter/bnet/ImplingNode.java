package content.global.skill.hunter.bnet;

import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.ChanceItem;
import core.game.node.item.Item;
import core.tools.RandomFunction;

import java.util.Random;

/**
 * Represents an impling NPC node in the Hunter skill.
 */
public final class ImplingNode extends BNetNode {

    /**
     * The loot table for this impling node.
     */
    private final ChanceItem[] loot;

    /**
     * The time it takes for the impling to respawn.
     */
    private final int respawnTime;

    /**
     * Constructs a new impling node with a respawn time.
     *
     * @param npcs        the NPC IDs representing this impling
     * @param level       the Hunter level required to catch the impling
     * @param exp         the experience granted outside Puro-Puro
     * @param puroExp     the experience granted inside Puro-Puro
     * @param reward      the reward item given upon catching
     * @param respawnTime the time in ticks before the impling respawns
     * @param loot        the loot table for this impling
     */
    public ImplingNode(int[] npcs, int level, double exp, double puroExp, Item reward, final int respawnTime, final ChanceItem... loot) {
        super(npcs, new int[]{level}, new double[]{exp, puroExp}, null, reward);
        this.loot = loot;
        this.respawnTime = respawnTime;
    }

    /**
     * Constructs a new impling node with a default respawn time of 16 ticks.
     *
     * @param npcs    the NPC IDs representing this impling
     * @param level   the Hunter level required to catch the impling
     * @param exp     the experience granted outside Puro-Puro
     * @param puroExp the experience granted inside Puro-Puro
     * @param reward  the reward item given upon catching
     * @param loot    the loot table for this impling
     */
    public ImplingNode(int[] npcs, int level, double exp, double puroExp, Item reward, final ChanceItem... loot) {
        this(npcs, level, exp, puroExp, reward, 16, loot);
    }

    /**
     * Handles the looting of the impling.
     * <p>
     * Removes the impling item from the player's inventory and grants a random
     * reward from the loot table. The jar may break based on the player's
     * Strength level.
     * </p>
     *
     * @param player the player attempting to loot the impling
     * @param item   the impling jar item being used
     */
    public void loot(final Player player, final Item item) {
        player.lock(1);
        if (player.getInventory().freeSlots() < 1) {
            player.getPacketDispatch().sendMessage("You don't have enough inventory space.");
            return;
        }
        final Item reward = RandomFunction.getChanceItem(getLoot()).getRandomItem();
        if (player.getInventory().remove(item)) {
            if (isBroken(player)) {
                player.sendMessage("You break the jar as you try and open it. You throw the shattered remains away.");
            } else {
                player.getInventory().add(IMPLING_JAR);
            }
            player.getInventory().add(reward, player);
        }
    }

    /**
     * Determines whether the jar breaks based on the player's Strength level
     * and randomness.
     *
     * @param player the player opening the jar
     * @return {@code true} if the jar breaks, {@code false} otherwise
     */
    private boolean isBroken(Player player) {
        int strengthLevel = player.getSkills().getLevel(Skills.STRENGTH);
        strengthLevel /= 0.5;
        int level = getLevel();
        int currentLevel = RandomFunction.random(strengthLevel) + 1;
        double ratio = (double) currentLevel / (new Random().nextInt(level + 5) + 1);
        return Math.round(ratio * strengthLevel) < level;
    }

    /**
     * Sends a message to the player after catching the impling.
     *
     * @param player  the player to message
     * @param type    the type of action performed
     * @param success whether the action succeeded
     */
    @Override
    public void message(Player player, int type, boolean success) {
        if (!success) {
            return;
        }
        if (type == 1) {
            player.sendMessage("You manage to catch the impling and squeeze it into a jar.");
        }
    }

    /**
     * Returns the experience the player receives for catching this impling.
     * Experience differs if the player is in the Puro-Puro minigame.
     *
     * @param player the player catching the impling
     * @return the experience gained
     */
    @Override
    public double getExperience(Player player) {
        return player.getZoneMonitor().isInZone("puro puro") ? getExperiences()[1] : super.getExperience(player);
    }

    /**
     * Determines if catching the impling requires bare hands.
     *
     * @param player the player attempting to catch the impling
     * @return {@code false} as all implings require a jar
     */
    @Override
    public boolean isBareHand(Player player) {
        return false;
    }

    /**
     * Returns the impling jar item used for this node.
     *
     * @return the jar item
     */
    @Override
    public Item getJar() {
        return IMPLING_JAR;
    }

    /**
     * Returns the loot table for this impling node.
     *
     * @return an array of chance items
     */
    public ChanceItem[] getLoot() {
        return loot;
    }

    /**
     * Returns the respawn time for this impling node.
     *
     * @return respawn time in ticks
     */
    public int getRespawnTime() {
        return respawnTime;
    }
}
