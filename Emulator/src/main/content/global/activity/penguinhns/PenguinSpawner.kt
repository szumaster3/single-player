package content.global.activity.penguinhns

import core.game.node.entity.npc.NPC
import core.game.world.map.Location
import org.rs.consts.NPCs

/**
 * Represents the spawning penguin NPCs in the world.
 */
class PenguinSpawner {

    /**
     * Spawns a specified number of unique penguins randomly.
     *
     * @param amount The number of penguins to spawn.
     * @return A list of the ordinals of the spawned penguins.
     */
    fun spawnPenguins(amount: Int): ArrayList<Int> {
        val availablePenguins = Penguin.values().toMutableList()
        val spawnedOrdinals = ArrayList<Int>()

        repeat(amount.coerceAtMost(availablePenguins.size)) {
            val penguin = availablePenguins.random()
            availablePenguins.remove(penguin)
            val location = penguin.location.random()
            spawnedOrdinals.add(penguin.ordinal)

            NPC(penguin.id, location).also {
                it.isNeverWalks = true
                it.isWalks = false
                PenguinManager.npcs.add(it)
            }.init()
        }

        return spawnedOrdinals
    }

    /**
     * Spawns penguins specified by their ordinals.
     *
     * @param ordinals List of penguin ordinals to spawn.
     */
    fun spawnPenguins(ordinals: List<Int>) {
        ordinals.forEach { ordinal ->
            val penguin = Penguin.values()[ordinal]
            val location = penguin.location.random()

            NPC(penguin.id, location).also {
                it.isNeverWalks = true
                it.isWalks = false
                PenguinManager.npcs.add(it)
            }.init()
        }
    }
}

/**
 * Represents all possible penguins for the Penguin Hunter activity.
 *
 * @property id The [NPC] id.
 * @property hint Text hint describing location of penguin.
 * @property location The [Location] of the penguin [NPC].
 */
enum class Penguin(
    val id: Int,
    val hint: String,
    val location: List<Location>,
) {
    CACTUS(NPCs.CACTUS_8107, "located in the northern desert.", listOf(
        Location(3310, 3157, 0),
        Location(3350, 3311, 0)
    )),
    CACTUS_SOUTH(NPCs.CACTUS_8107, "located in the southern desert.", listOf(
        Location.create(3259, 3052, 0)
    )),

    BUSH_FREMENNIK(NPCs.BUSH_8105, "located between Fremennik and barbarians.", listOf(
        Location.create(2532, 3588, 0)
    )),
    BUSH_BANANA(NPCs.BUSH_8105, "located where banana smugglers dwell.", listOf(
        Location.create(2740, 3233, 0)
    )),
    BUSH_SOUTH_ARDOUGNE(NPCs.BUSH_8105, "located south of Ardougne.", listOf(
        Location.create(2456, 3092, 0),
        Location.create(2513, 3154, 0),
        Location.create(2440, 3206, 0),
        Location.create(2482, 3126, 0)
    )),
    BUSH_JUNGLE(NPCs.BUSH_8105, "located deep in the jungle.", listOf(
        Location.create(2832, 3053, 0),
        Location.create(2938, 2978, 0)
    )),
    BUSH_EAGLES(NPCs.BUSH_8105, "located where eagles fly.", listOf(
        Location.create(2326, 3516, 0)
    )),
    BUSH_TREE_GNOMES(NPCs.BUSH_8105, "located near a big tree surrounded by short people.", listOf(
        Location.create(2387, 3451, 0)
    )),
    BUSH_ASGARNIA(NPCs.BUSH_8105, "located in the kingdom of Asgarnia.", listOf(
        Location.create(2951, 3511, 0)
    )),
    BUSH_KANDARIN(NPCs.BUSH_8105, "located somewhere in the kingdom of Kandarin.", listOf(
        Location.create(2633, 3501, 0)
    )),
    BUSH_WIZARDS(NPCs.BUSH_8105, "located where wizards study.", listOf(
        Location.create(3112, 3149, 0)
    )),
    BUSH_BLOODSUCKERS(NPCs.BUSH_8105, "located where bloodsuckers rule.", listOf(
        Location.create(3457, 3387, 0)
    )),
    BUSH_MONKEYS(NPCs.BUSH_8105, "located where monkeys rule.", listOf(
        Location.create(2809, 2775, 0)
    )),
    BUSH_FELDIP(NPCs.BUSH_8105, "located around Feldip Hills, near Gu'Tanoth.", listOf(
        Location.create(2606, 3005, 0),
        Location.create(2438, 3045, 0),
        Location.create(2340, 3064, 0)
    )),
    BUSH_BROTHERS(NPCs.BUSH_8105, "located on islands where brothers quarrel.", listOf(
        Location.create(2356, 3848, 0)
    )),
    BUSH_WEREWOLVES(NPCs.BUSH_8105, "located near a town of werewolves.", listOf(
        Location.create(3600, 3485, 0)
    )),

    ROCK_GUARD(NPCs.ROCK_8109, "located where the Imperial Guard train.", listOf(
        Location.create(2852, 3578, 0)
    )),
    ROCK_MISTHALIN(NPCs.ROCK_8109, "located in the kingdom of Misthalin.", listOf(
        Location.create(3356, 3416, 0)
    )),
    ROCK_OGRES(NPCs.ROCK_8109, "located near some ogres.", listOf(
        Location.create(2631, 2980, 0)
    )),
    ROCK_ASGARNIA(NPCs.ROCK_8109, "located in the Kingdom of Asgarnia.", listOf(
        Location.create(3013, 3501, 0)
    )),
    ROCK_FREMENNIK(NPCs.ROCK_8109, "located between Fremennik and barbarians.", listOf(
        Location.create(2532, 3630, 0)
    )),
    ROCK_BROTHERS(NPCs.ROCK_8109, "located on islands where brothers quarrel.", listOf(
        Location.create(2312, 3814, 0),
        Location.create(2413, 3846, 0)
    )),
    ROCK_CRESCENT(NPCs.ROCK_8109, "located on a large crescent island.", listOf(
        Location.create(2152, 3934, 0)
    )),
    ROCK_COAST(NPCs.ROCK_8109, "located near the coast, east of Ardougne.", listOf(
        Location.create(2733, 3282, 0)
    )),

    CRATE_MISTHALIN(NPCs.CRATE_8108, "located in the kingdom of Misthalin.", listOf(
        Location.create(3112, 3332, 0),
        Location.create(3305, 3508, 0)
    )),
    CRATE_WEREWOLVES(NPCs.CRATE_8108, "located near a town of werewolves.", listOf(
        Location.create(3637, 3486, 0)
    )),

    // CRATE_DRAGONTOOTH(NPCs.CRATE_8108, "located near the island of Dragontooth.", listOf(
    //     Location.create(3824, 3562, 0)
    // )),

    BARREL_WEAPONLESS(NPCs.CRATE_8108, "located where no weapons may go.", listOf(
        Location.create(2806, 3383, 0)
    )),
    BARREL_ARDOUGNE(NPCs.CRATE_8108, "located south of Ardougne.", listOf(
        Location.create(2662, 3152, 0)
    )),
    BARREL_GHOSTS(NPCs.CRATE_8108, "located near the city of ghosts.", listOf(
        Location.create(3654, 3491, 0)
    )),
    BARREL_FISHERS(NPCs.CRATE_8108, "located where fishers colonise.", listOf(
        Location.create(2322, 3658, 0)
    )),

    TOADSTOOL_MISTHALIN(NPCs.TOADSTOOL_8110, "located in the kingdom of Misthalin.", listOf(
        Location.create(3156, 3178, 0)
    )),
    TOADSTOOL_FAIRY(NPCs.TOADSTOOL_8110, "located in the fairy realm.", listOf(
        Location.create(2409, 4462, 0)
    )),
    TOADSTOOL_ELVES(NPCs.TOADSTOOL_8110, "located near the pointy-eared ones.", listOf(
        Location.create(2311, 3177, 0),
        Location.create(2219, 3227, 0),
        Location.create(2296, 3271, 0)
    ));

    companion object {
        /**
         * Maps string representations of locations to their corresponding [Penguin] enum entries.
         */
        private val locationMap: Map<String, Penguin> = buildMap {
            for (penguin in values()) {
                for (loc in penguin.location) {
                    put(loc.toString(), penguin)
                }
            }
        }

        /**
         * Returns the [Penguin] associated with the given [location], or `null` if none match.
         *
         * @param location The location to look up.
         * @return The [Penguin] associated with the location, or `null` if not found.
         */
        fun forLocation(location: Location): Penguin? = locationMap[location.toString()]
    }
}
