package content.global.skill.hunter;

import core.api.ContentAPIKt;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.npc.AbstractNPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.TeleportManager;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.tools.RandomFunction;
import shared.consts.NPCs;

import java.util.ArrayList;
import java.util.List;

import static core.api.ContentAPIKt.getPathableRandomLocalCoordinate;
import static core.api.ContentAPIKt.sendGraphics;

/**
 * Represents a Hunter NPC used in the Hunter skill.
 */
public final class HunterNPC extends AbstractNPC {

    /** Chance for an imp to teleport when hit (percentage). */
    private static final int IMP_TELEPORT_CHANCE_ON_HIT = 10;

    /** Chance for an imp to teleport each tick (percentage). */
    private static final int IMP_TELEPORT_CHANCE_ON_TICK = 1000;

    /** The trap type associated with this NPC. */
    private final Traps trap;

    /** The node/type of this NPC for trap catching logic. */
    private final TrapNode type;

    /**
     * Default constructor for a generic Hunter NPC.
     */
    public HunterNPC() {
        this(0, null, null, null);
        this.setWalks(true);
    }

    /**
     * Constructs a Hunter NPC with an id, location, trap, and type.
     *
     * @param id the NPC id
     * @param location the spawn location
     * @param trap the associated trap
     * @param type the trap node type
     */
    public HunterNPC(int id, Location location, Traps trap, TrapNode type) {
        super(id, location);
        this.trap = trap;
        this.type = type;
    }

    /**
     * Constructs a new Hunter NPC instance for a given id and location.
     *
     * @param id the NPC id
     * @param location the spawn location
     * @param objects optional additional objects
     * @return a new HunterNPC instance
     */
    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        Object[] data = Traps.getNode(id);
        return new HunterNPC(id, location, (Traps) data[0], (TrapNode) data[1]);
    }

    /**
     * Updates the NPC location and checks for trap interactions.
     *
     * @param last the previous location
     */
    @Override
    public void updateLocation(Location last) {
        final TrapWrapper wrapper = trap.getByHook(getLocation());
        if (wrapper != null) {
            wrapper.getType().catchNpc(wrapper, this);
        }
    }

    /**
     * Determines a movement destination for the NPC.
     * Chooses a trap hook location if available and valid.
     *
     * @return the movement destination
     */
    @Override
    protected Location getMovementDestination() {
        if (trap.getHooks().size() == 0 || RandomFunction.random(170) > 5) {
            return super.getMovementDestination();
        }
        TrapHook hook = trap.getHooks().get(RandomFunction.random(trap.getHooks().size()));
        if (hook == null || !type.canCatch(hook.getWrapper(), this)) {
            return super.getMovementDestination();
        }
        Location destination = hook.getChanceLocation();
        return destination != null && destination.getDistance(getLocation()) <= 24 ? destination : super.getMovementDestination();
    }

    /**
     * Handles drops when the NPC dies.
     * Prevents multiple drops if already caught or processed.
     *
     * @param p the player who killed the NPC
     * @param killer the entity that killed the NPC
     */
    @Override
    public void handleDrops(Player p, Entity killer) {
        int ticks = getAttribute("hunter", 0);
        if (ticks < GameWorld.getTicks()) {
            super.handleDrops(p, killer);
        }
    }

    /**
     * Returns all NPC IDs associated with Hunter traps.
     *
     * @return array of Hunter NPC IDs
     */
    @Override
    public int[] getIds() {
        List<Integer> ids = new ArrayList<>(10);
        for (Traps t : Traps.values()) {
            for (TrapNode node : t.nodes) {
                for (int id : node.getNpcIds()) {
                    ids.add(id);
                }
            }
        }
        int[] array = new int[ids.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = ids.get(i);
        }
        return array;
    }

    /**
     * Checks the impact of combat on the NPC.
     * Imps may randomly teleport when hit.
     *
     * @param state the battle state
     */
    @Override
    public void checkImpact(BattleState state) {
        super.checkImpact(state);

        if (this.getId() == NPCs.IMP_708 || this.getId() == NPCs.IMP_709 || this.getId() == NPCs.IMP_1531) {
            if (RandomFunction.roll(IMP_TELEPORT_CHANCE_ON_HIT)) {
                getRandomLocAndTeleport();
            }
        }
    }

    /**
     * Ticks the NPC each game cycle.
     * Imps may randomly teleport each tick.
     */
    @Override
    public void tick() {
        super.tick();

        if (this.getId() == NPCs.IMP_708 || this.getId() == NPCs.IMP_709 || this.getId() == NPCs.IMP_1531) {
            if (RandomFunction.roll(IMP_TELEPORT_CHANCE_ON_TICK)) {
                getRandomLocAndTeleport();
            }
        }
    }

    /**
     * Gets the type of this NPC for trap logic.
     *
     * @return the trap node type
     */
    public TrapNode getType() {
        return type;
    }

    /**
     * Gets the trap associated with this NPC.
     *
     * @return the trap
     */
    public Traps getTrap() {
        return trap;
    }

    /**
     * Teleports the NPC to a random nearby pathable location and displays graphics.
     */
    private void getRandomLocAndTeleport() {
        Location teleportLocation = getPathableRandomLocalCoordinate(this, walkRadius, getProperties().getSpawnLocation(), 3);

        if (getLocation() != teleportLocation) {
            sendGraphics(1119, getLocation());
            ContentAPIKt.teleport(this, teleportLocation, TeleportManager.TeleportType.INSTANT);
        }
    }
}