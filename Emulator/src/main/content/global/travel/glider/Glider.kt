package content.global.travel.glider

import core.api.setVarp
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.world.map.Location
import org.rs.consts.NPCs

/**
 * Enum representing the different glider locations available in the game.
 * Each glider has a button ID, location, configuration ID, and associated NPC.
 *
 * @property button The button ID used to interact with the glider.
 * @property location The [Location] where the glider can be found.
 * @property config The configuration ID associated with the glider.
 * @property npc The NPC ID associated with the glider.
 */
enum class Glider(
    val button: Int,
    val location: Location,
    val config: Int,
    val npc: Int,
) {
    CRASH_ISLAND(14, Location(2894, 2726, 0), 8, NPCs.CAPTAIN_ERRDO_3811), GANDIUS(15, Location(2972, 2969, 0), 8, NPCs.CAPTAIN_KLEMFOODLE_3812),
    TA_QUIR_PRIW(16, Location(2465, 3501, 3), 9, NPCs.CAPTAIN_DALBUR_3809), SINDARPOS(17, Location(2848, 3497, 0), 1, NPCs.CAPTAIN_BLEEMADGE_3810),
    LEMANTO_ADRA(18, Location(3321, 3427, 0), 3, NPCs.CAPTAIN_ERRDO_3811), KAR_HEWO(19, Location(3278, 3212, 0), 4, NPCs.CAPTAIN_KLEMFOODLE_3812),
    LEMANTOLLY_UNDRI(20, Location(2544, 2970, 0), 10, NPCs.GNORMADIUM_AVLAFRIM_1800), ;

    companion object {
        /**
         * Sends the configuration to the player based on the associated glider NPC.
         *
         * @param npc The NPC representing the glider.
         * @param player The player interacting with the glider.
         */
        fun sendConfig(
            npc: NPC,
            player: Player,
        ) {
            val g = forNpc(npc.id)
            if (g != null) {
                setVarp(player, 153, g.config)
            }
        }

        /**
         * Finds the glider associated with the given NPC ID.
         *
         * @param npcId The NPC ID to search for.
         * @return The [Glider] associated with the given NPC ID, or null if not found.
         */
        fun forNpc(npcId: Int): Glider? {
            for (data in values()) {
                if (data.npc == npcId) {
                    return data
                }
            }
            return null
        }

        /**
         * Finds the glider associated with the given button ID.
         *
         * @param id The button ID to search for.
         * @return The [Glider] associated with the given button ID, or null if not found.
         */
        fun forId(id: Int): Glider? {
            for (data in values()) {
                if (data.button == id) {
                    return data
                }
            }
            return null
        }
    }
}
