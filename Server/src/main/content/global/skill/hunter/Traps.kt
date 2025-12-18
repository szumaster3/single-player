package content.global.skill.hunter

import content.global.skill.hunter.NetTrapSetting.NetTrap
import core.api.log
import core.api.sendMessage
import core.game.node.Node
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.tools.Log

/**
 * Enum representing all hunter traps in the game.
 *
 * Each trap type holds its configuration [TrapSetting] and associated trap nodes [TrapNode].
 */
enum class Traps(@JvmField val settings: TrapSetting, vararg nodes: TrapNode) {

    BIRD_SNARE(
        TrapSetting(10006, intArrayOf(19175), intArrayOf(), "lay", 19174, Animation.create(5208), Animation.create(5207), 1),
        TrapNode(intArrayOf(5073), 1, 34.0, intArrayOf(19179, 19180), arrayOf(Item(10088, 8), Item(9978), Item(526))),
        TrapNode(intArrayOf(5075), 5, 48.0, intArrayOf(19183, 19184), arrayOf(Item(10090, 8), Item(9978), Item(526))),
        TrapNode(intArrayOf(5076), 9, 61.0, intArrayOf(19185, 19186), arrayOf(Item(10091, 8), Item(9978), Item(526))),
        TrapNode(intArrayOf(5074), 11, 64.7, intArrayOf(19181, 19182), arrayOf(Item(10089, 8), Item(9978), Item(526))),
        TrapNode(intArrayOf(5072), 19, 95.2, intArrayOf(19177, 19178), arrayOf(Item(10087, 8), Item(9978), Item(526))),
        object : TrapNode(intArrayOf(7031), 39, 167.0, intArrayOf(28931, 28930), arrayOf(Item(11525, 8), Item(9978), Item(526))) {
            override fun canCatch(wrapper: TrapWrapper, npc: NPC): Boolean = false
        },
    ),

    BOX_TRAP(
        TrapSetting(10008, intArrayOf(19187), intArrayOf(1963, 12579, 1869, 9996, 5972, 12535), "lay", 19192, Animation.create(5208), Animation(9726), 27),
        BoxTrapNode(intArrayOf(5081), 27, 100.0, arrayOf(Item(10092)), 1),
        BoxTrapNode(intArrayOf(6918, 7289, 7290, 7291, 7292), 27, 100.0, arrayOf(Item(12184)), 10),
        BoxTrapNode(intArrayOf(1487), 27, 100.0, arrayOf(Item(4033, 1)), 95),
        BoxTrapNode(intArrayOf(7021, 7022, 7023), 48, 150.0, arrayOf(Item(12551, 1)), 1),
        BoxTrapNode(intArrayOf(5079), 53, 198.0, arrayOf(Item(10033, 1)), 1),
        BoxTrapNode(intArrayOf(5428, 5430, 5449, 5450, 5451), 56, 150.0, arrayOf(Item(12188)), 1),
        BoxTrapNode(intArrayOf(5080), 63, 265.0, arrayOf(Item(10034, 1)), 1),
        BoxTrapNode(intArrayOf(7012, 7014), 66, 400.0, arrayOf(Item(12535)), 1),
        BoxTrapNode(intArrayOf(8654), 73, 315.0, arrayOf(Item(14861)), 1),
        object : BoxTrapNode(intArrayOf(7010, 7011), 77, 0.0, arrayOf(Item(12539, 1)), 1) {
            override fun canCatch(wrapper: TrapWrapper, npc: NPC): Boolean = super.canCatch(wrapper, npc)
        },
    ),

    RABBIT_SNARE(
        TrapSetting(10031, intArrayOf(19333), intArrayOf(), "lay", -1, Animation.create(5208), Animation.create(9726), 27),
    ),

    IMP_BOX(
        MagicBoxSetting(),
        TrapNode(intArrayOf(708, 709, 1531), 71, 450.0, intArrayOf(-1, 19226), arrayOf(Item(10027))),
    ),

    DEAD_FALL(
        DeadfallSetting(),
        TrapNode(intArrayOf(5089), 23, 128.0, intArrayOf(19213, 19214, 19218), arrayOf(Item(10113), Item(526))),
        TrapNode(intArrayOf(5088), 33, 168.0, intArrayOf(19211, 19212, 19217), arrayOf(Item(10129), Item(526))),
        TrapNode(intArrayOf(5086), 37, 204.0, intArrayOf(19208, 19208, 19217), arrayOf(Item(10105), Item(526))),
        TrapNode(intArrayOf(7039), 44, 200.0, intArrayOf(28939, 28940, 28941), arrayOf(Item(12567), Item(526))),
        TrapNode(intArrayOf(5087), 51, 200.0, intArrayOf(19209, 19210, 19216), arrayOf(Item(10109), Item(526))),
    ),

    NET_TRAP(
        NetTrapSetting(),
        TrapNode(intArrayOf(5117), 29, 152.0, intArrayOf(), arrayOf(Item(10149))),
        TrapNode(intArrayOf(5114), 47, 224.0, intArrayOf(), arrayOf(Item(10146))),
        TrapNode(intArrayOf(6921), 29, 152.0, intArrayOf(), arrayOf(Item(12130))),
        TrapNode(intArrayOf(5115), 59, 272.0, intArrayOf(), arrayOf(Item(10147))),
        TrapNode(intArrayOf(5116), 67, 304.0, intArrayOf(), arrayOf(Item(10148))),
    );

    /** List of all hooks currently created for this trap instance */
    private val hooks: MutableList<TrapHook> = ArrayList(5)

    /** Array of trap nodes associated with this trap */
    @JvmField
    val nodes: Array<TrapNode> = nodes as Array<TrapNode>

    /**
     * Creates a trap in the world for the player.
     *
     * @param player the player creating the trap
     * @param node the node object representing the trap location
     */
    fun create(player: Player, node: Node) {
        player.pulseManager.run(TrapCreatePulse(player, node, this))
    }

    /**
     * Dismantles a trap if the player is the owner.
     *
     * @param player the player dismantling the trap
     * @param scenery the scenery object representing the trap
     */
    fun dismantle(player: Player, scenery: Scenery) {
        val instance = HunterManager.getInstance(player)
        if (!instance.isOwner(scenery)) {
            sendMessage(player, "This isn't your trap!")
            return
        }
        if (instance.getWrapper(scenery) == null) {
            log(this.javaClass, Log.ERR, "NO WRAPPER (HUNTER DISMANTLE)")
            return
        }
        player.faceLocation(scenery.location)
        player.pulseManager.run(TrapDismantlePulse(player, scenery, instance.getWrapper(scenery)!!))
    }

    /**
     * Investigates a trap.
     *
     * @param player the player investigating
     * @param scenery the trap scenery
     */
    fun investigate(player: Player, scenery: Scenery) {
        settings.investigate(player, scenery)
    }

    /**
     * Attempts to catch an NPC using this trap.
     *
     * @param wrapper the trap wrapper
     * @param npc the NPC to catch
     */
    fun catchNpc(wrapper: TrapWrapper, npc: NPC) {
        val trapNode = forNpc(npc)
        if (trapNode == null || !trapNode.canCatch(wrapper, npc) || !settings.canCatch(wrapper, npc)) return
        settings.catchNpc(wrapper, trapNode, npc)
    }

    /**
     * Adds a hook to the trap.
     *
     * @param wrapper the trap wrapper
     * @return the created [TrapHook]
     */
    fun addHook(wrapper: TrapWrapper): TrapHook {
        val hook = settings.createHook(wrapper)
        hooks.add(hook)
        return hook
    }

    /**
     * Gets the trap node corresponding to the given NPC.
     *
     * @param npc the NPC to check
     * @return the associated [TrapNode] or null if none match
     */
    fun forNpc(npc: NPC): TrapNode? {
        for (node in nodes) {
            if (npc.id in node.npcIds) return node
        }
        return null
    }

    /**
     * Gets the trap wrapper associated with a specific hook location.
     *
     * @param location the location of the hook
     * @return the [TrapWrapper] or null if none found
     */
    fun getByHook(location: Location): TrapWrapper? {
        for (hook in hooks) if (hook.isHooked(location)) return hook.wrapper
        return null
    }

    /**
     * @return list of all hooks currently active
     */
    fun getHooks(): List<TrapHook> = hooks

    companion object {
        /**
         * Gets the trap associated with a world node.
         *
         * @param node the node to check
         * @return the [Traps] instance or null if none match
         */
        @JvmStatic
        fun forNode(node: Node): Traps? {
            for (trap in values()) {
                if (node.id in trap.settings.nodeIds) return trap
                if (node.id in trap.settings.objectIds) return trap
                for (n in trap.nodes) if (node.id in n.objectIds) return trap
                if (trap.settings.failId == node.id) return trap
                if (trap == NET_TRAP) {
                    for (net in NetTrap.values()) {
                        if (node.id == net.original || node.id == net.failed || node.id == net.net || node.id == net.bent || node.id == net.caught) return trap
                    }
                }
            }
            return null
        }

        /**
         * Gets the trap and trap node for a given NPC id.
         *
         * @param id the NPC id
         * @return an array containing the trap and node, or null if not found
         */
        @JvmStatic
        fun getNode(id: Int): Array<Any>? {
            for (trap in values()) {
                for (t in trap.nodes) {
                    if (id in t.npcIds) return arrayOf(trap, t)
                }
            }
            return null
        }
    }
}