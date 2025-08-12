package content.region.wilderness.plugin

import com.google.gson.JsonObject
import core.ServerStore.Companion.getArchive
import core.api.hasRequirement
import core.api.lock
import core.api.visualize
import core.cache.def.impl.SceneryDefinition
import core.game.activity.ActivityManager
import core.game.interaction.Option
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.world.GameWorld.settings
import core.game.world.GameWorld.ticks
import core.game.world.map.Location
import core.game.world.map.zone.MapZone
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneBuilder
import core.game.world.map.zone.ZoneRestriction
import core.game.world.update.flag.context.Graphics
import core.plugin.ClassScanner.definePlugin
import core.plugin.Initializable
import core.plugin.Plugin
import core.tools.Log
import core.tools.RandomFunction
import core.tools.SystemLogger
import shared.consts.Quests
import java.util.*

/**
 * Plugin representing the Chaos Tunnel zone.
 *
 * This plugin manages player interactions with the Chaos Tunnel area, including
 * entering the tunnel, climbing mechanics, and NPC aggression behavior.
 */
@Initializable
class ChaosTunnelPlugin :
    MapZone("Chaos tunnel", true, ZoneRestriction.CANNON),
    Plugin<Any?> {

    /**
     * Initializes and configures the plugin instance.
     */
    @Throws(Throwable::class)
    override fun newInstance(arg: Any?): Plugin<Any?> {
        ZoneBuilder.configure(this)
        definePlugin(
            object : OptionHandler() {
                @Throws(Throwable::class)
                override fun newInstance(arg: Any?): Plugin<Any> {
                    for (i in ENTRANCE_DATA.indices) {
                        SceneryDefinition.forId(ENTRANCE_DATA[i][0] as Int).handlers["option:enter"] = this
                        SceneryDefinition.forId(ENTRANCE_DATA[i][2] as Int).handlers["option:climb-up"] = this
                    }
                    SceneryDefinition.forId(23074).handlers["option:climb"] = this
                    return this
                }

                /**
                 * Handles player interactions with the Chaos Tunnel entrances and climbable scenery.
                 *
                 * @param player The player performing the action.
                 * @param node The game node (object) being interacted with.
                 * @param option The interaction option chosen by the player.
                 * @return True if the interaction was handled, false otherwise.
                 */
                override fun handle(player: Player, node: Node, option: String): Boolean {
                    var data: Array<Any>? = null
                    when (option) {
                        "enter", "climb-up" ->
                            when (node.id) {
                                28891, 28893, 28892, 28782 -> {
                                    if (option == "enter" && player.inCombat()) {
                                        player.sendMessage(
                                            "You can't enter the rift when you've recently been in combat.",
                                        )
                                        return true
                                    }
                                    var i = 0
                                    while (i < ENTRANCE_DATA.size) {
                                        if (ENTRANCE_DATA[i][if (option == "enter") 0 else 2] as Int == node.id &&
                                            player.location.withinDistance(
                                                ENTRANCE_DATA[i][
                                                    if (option == "enter") 3 else 1,
                                                ] as Location,
                                            )
                                        ) {
                                            data = ENTRANCE_DATA[i]
                                            break
                                        }
                                        i++
                                    }
                                    if (data == null) {
                                        return false
                                    }
                                    player.teleport(data[if (option == "enter") 1 else 3] as Location)
                                }
                            }

                        "climb" ->
                            when (node.id) {
                                23074 -> player.teleport(Location(3283, 3467, 0))
                            }
                    }
                    return true
                }
            },
        )
        return this
    }

    /**
     * Handles interaction between an entity and a target node within the zone.
     *
     * @param entity The entity performing the interaction (usually a player).
     * @param target The target node being interacted with.
     * @param option The interaction option chosen.
     * @return True if the interaction was handled, false otherwise.
     */
    override fun interact(entity: Entity, target: Node, option: Option): Boolean {
        if (entity is Player) {
            when (target.id) {
                29537 -> {}
                28779 -> {
                    if (target.location == Location(3326, 5469, 0)) {
                        entity.asPlayer().sendMessage("You can't go back through this portal.")
                        return true
                    }
                    teleport(entity.asPlayer(), target.asScenery())
                }
            }
        }
        return super.interact(entity, target, option)
    }

    /**
     * Called when an entity enters the Chaos Tunnel zone.
     *
     * Sets NPC aggression status upon entering.
     *
     * @param entity The entity entering the zone.
     * @return True if the event was handled, false otherwise.
     */
    override fun enter(entity: Entity): Boolean {
        if (entity is NPC) {
            val n = entity.asNpc()
            if (!n.isAggressive) {
                n.isAggressive = true
            }
        }
        return super.enter(entity)
    }

    /**
     * Handles custom plugin events.
     *
     * @param identifier The event identifier.
     * @param args Arguments associated with the event.
     * @return Result of event processing or null.
     */
    override fun fireEvent(identifier: String, vararg args: Any): Any? = null


    override fun configure() {
        register(ZoneBorders(3116, 5412, 3362, 5584))

        addLink(3158, 5561, 3162, 5557)
        addLink(3162, 5545, 3166, 5553)
        addLink(3147, 5541, 3143, 5535)
        addLink(3148, 5533, 3153, 5537)
        addLink(3152, 5520, 3156, 5523)
        addLink(3165, 5515, 3173, 5530)
        addLink(3169, 5510, 3159, 5501)
        addLink(3181, 5517, 3185, 5518)
        addLink(3182, 5530, 3187, 5531)
        addLink(3190, 5519, 3190, 5515)
        addLink(3196, 5512, 3202, 5515)
        addLink(3197, 5529, 3201, 5531)
        addLink(3190, 5549, 3190, 5554)
        addLink(3180, 5557, 3174, 5558)
        addLink(3171, 5542, 3168, 5541)

        addLink(3206, 5553, 3204, 5546)
        addLink(3226, 5553, 3230, 5547)
        addLink(3214, 5533, 3211, 5533)
        addLink(3208, 5527, 3211, 5523)
        addLink(3238, 5507, 3232, 5501)
        addLink(3241, 5529, 3243, 5526)
        addLink(3261, 5536, 3268, 5534)
        addLink(3252, 5543, 3249, 5546)
        addLink(3262, 5552, 3266, 5552)
        addLink(3256, 5561, 3253, 5561)
        addLink(3297, 5536, 3299, 5533)

        addLink(3285, 5556, 3291, 5555)
        addLink(3288, 5536, 3289, 5533)
        addLink(3285, 5527, 3282, 5531)
        addLink(3285, 5508, 3280, 5501)
        addLink(3300, 5514, 3297, 5510)
        addLink(3325, 5518, 3323, 5531)
        addLink(3321, 5554, 3315, 5552)

        addLink(3142, 5489, 3141, 5480)
        addLink(3142, 5462, 3154, 5462)
        addLink(3143, 5443, 3155, 5449)
        addLink(3167, 5478, 3171, 5478)
        addLink(3171, 5473, 3167, 5471)
        addLink(3168, 5456, 3178, 5460)
        addLink(3187, 5460, 3189, 5444)
        addLink(3192, 5472, 3186, 5472)
        addLink(3185, 5478, 3191, 5482)
        addLink(3197, 5448, 3204, 5445)
        addLink(3191, 5482, 3185, 5478)
        addLink(3191, 5495, 3194, 5490)

        addLink(3214, 5456, 3212, 5452)
        addLink(3229, 5454, 3235, 5457)
        addLink(3233, 5445, 3241, 5445)
        addLink(3239, 5498, 3244, 5495)
        addLink(3233, 5470, 3241, 5469)
        addLink(3241, 5445, 3233, 5445)
        addLink(3259, 5446, 3265, 5491)
        addLink(3260, 5491, 3266, 5446)
        addLink(3218, 5478, 3215, 5475)
        addLink(3208, 5471, 3210, 5477)

        addLink(3283, 5448, 3287, 5448)
        addLink(3296, 5455, 3299, 5450)
        addLink(3302, 5469, 3290, 5463)
        addLink(3286, 5470, 3285, 5474)
        addLink(3322, 5480, 3318, 5481)
        addLink(3317, 5496, 3307, 5496)
        addLink(3299, 5484, 3303, 5477)
        addLink(3280, 5460, 3273, 5460)
        addLink(3285, 5474, 3286, 5470)
        addLink(3222, 5474, 3224, 5479)
        addLink(3222, 5488, 3218, 5497)
    }

    /**
     * Teleports the given player using the specified scenery portal.
     *
     * @param player The player to teleport.
     * @param scenery The scenery portal used for teleportation.
     */
    private fun teleport(player: Player, scenery: Scenery) {
        if (scenery.location.x == 3142 && scenery.location.y == 5545) {
            if (hasRequirement(player, Quests.WHAT_LIES_BELOW)) commenceBorkBattle(player)
            return
        }
        var loc = getLocation(scenery.location)
        if (loc == null) {
            SystemLogger.processLogEntry(this.javaClass, Log.INFO, "Error! Unhandled portal for - $scenery!")
            return
        }
        if (!isFixed(player)) {
            var stained = isStained(scenery)
            if (!stained && RandomFunction.random(100) <= 3) {
                stained = true
                setStainedTime(scenery)
            }
            if (stained) {
                SystemLogger.processLogEntry(this.javaClass, Log.INFO, "The portal is stained with dark magic.")
                return
            }
            if (RandomFunction.random(100) <= 3) {
                loc = randomLocation
                player.sendMessage("The dark magic teleports you into a random location.")
            }
        }
        player.teleport(loc)
        player.graphics(Graphics.create(shared.consts.Graphics.CURSE_IMPACT_110))
    }

    /**
     * Starts the Bork battle sequence for the player.
     *
     * Checks if the portal's magic is weakened for the player and handles
     * locking, visual effects, and starting the Bork cutscene activity.
     *
     * @param player The player who will commence the Bork battle.
     */
    private fun commenceBorkBattle(player: Player) {
        val usernameKey = player.username.lowercase(Locale.getDefault())
        val storeFile = getBorkStoreFile()

        val isPortalWeak = storeFile[usernameKey] as? Boolean ?: false
        if (isPortalWeak && settings?.isHosted == true) {
            player.packetDispatch.sendMessage("The portal's magic is too weak to teleport you right now.")
            return
        }

        lock(player, 10)
        visualize(player, -1, shared.consts.Graphics.CURSE_IMPACT_110)
        storeFile.addProperty(usernameKey, true)
        ActivityManager.start(player, "Bork cutscene", false)
    }

    /**
     * Returns a random teleport location from available portals.
     */
    private val randomLocation: Location
        get() = RandomFunction.getRandomElement<Any>(PORTALS.values.toTypedArray()) as Location

    /**
     * Checks if the given scenery portal is currently stained by dark magic.
     *
     * @param scenery The portal scenery to check.
     * @return True if stained, false otherwise.
     */
    private fun isStained(scenery: Scenery): Boolean = getStainedTime(scenery) > ticks

    /**
     * Sets the stained time attribute for the scenery portal.
     *
     * This marks the portal as stained with dark magic for a random duration.
     *
     * @param scenery The portal scenery to set stained time on.
     */
    private fun setStainedTime(scenery: Scenery) {
        scenery.attributes.setAttribute("stained", ticks + RandomFunction.random(50, 150))
    }

    /**
     * Gets the stained time attribute of the portal scenery.
     *
     * @param scenery The portal scenery to get the stained time from.
     * @return The tick count until which the portal remains stained.
     */
    private fun getStainedTime(scenery: Scenery): Int = scenery.attributes.getAttribute("stained", 0)

    /**
     * Checks if the portal for the player is fixed (immune to random teleport effects).
     *
     * @param player The player to check.
     * @return Always returns false in current implementation.
     */
    private fun isFixed(player: Player): Boolean = false

    /**
     * Gets the linked teleport location for a given location.
     *
     * Searches the PORTALS map for a matching entry or reverse lookup.
     *
     * @param location The location of the portal.
     * @return The linked teleport location, or null if not found.
     */
    fun getLocation(location: Location): Location? {
        val l = PORTALS[location]
        if (l != null) {
            return l
        }
        for ((key, value) in PORTALS) {
            if (value == location) {
                return key
            }
        }
        return null
    }

    /**
     * Adds a portal link between two locations (using coordinates).
     *
     * @param x X-coordinate of first location.
     * @param y Y-coordinate of first location.
     * @param x2 X-coordinate of second location.
     * @param y2 Y-coordinate of second location.
     */
    private fun addLink(
        x: Int,
        y: Int,
        x2: Int,
        y2: Int,
    ) {
        addLink(Location(x, y, 0), Location(x2, y2, 0))
    }

    /**
     * Adds a portal link between two locations.
     *
     * @param location The first location.
     * @param loc The linked location.
     */
    private fun addLink(
        location: Location,
        loc: Location,
    ) {
        PORTALS[location] = loc
    }

    companion object {
        /**
         * Gets the JSON object used to store Bork battle status per player.
         *
         * @return JsonObject representing the Bork status store.
         */
        @JvmStatic
        fun getBorkStoreFile(): JsonObject = getArchive("daily-bork-killed")

        private val ENTRANCE_DATA =
            arrayOf(
                arrayOf(28891, Location(3182, 5471, 0), 28782, Location(3059, 3549, 0)),
                arrayOf(28893, Location(3248, 5489, 0), 28782, Location(3120, 3571, 0)),
                arrayOf(28892, Location(3292, 5479, 0), 28782, Location(3166, 3561, 0)),
                arrayOf(28893, Location(3234, 5558, 0), 28782, Location(3107, 3640, 0)),
                arrayOf(28892, Location(3290, 5538, 0), 28782, Location(3165, 3617, 0)),
            )

        /**
         * Map of portal locations linking one to another
         */
        private val PORTALS: MutableMap<Location, Location> = HashMap()
    }

}
