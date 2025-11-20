package content.global.travel.glider

import core.api.*
import core.api.utils.PlayerCamera
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.system.task.Pulse
import core.game.world.map.Location
import core.net.packet.PacketRepository
import core.net.packet.context.CameraContext
import core.net.packet.out.CameraViewPacket
import shared.consts.Components
import shared.consts.NPCs
import shared.consts.Quests
import shared.consts.Vars

class GliderPlugin : InteractionListener, InterfaceListener {

    private val GNOME_PILOTS = intArrayOf(NPCs.CAPTAIN_DALBUR_3809, NPCs.CAPTAIN_BLEEMADGE_3810, NPCs.CAPTAIN_ERRDO_3811, NPCs.CAPTAIN_KLEMFOODLE_3812)
    private val GNOME_ID = intArrayOf(NPCs.GNORMADIUM_AVLAFRIM_1800, NPCs.CAPTAIN_DALBUR_3809, NPCs.CAPTAIN_BLEEMADGE_3810, NPCs.CAPTAIN_KLEMFOODLE_3812)

    override fun defineListeners() {

        /*
         * Handles option for glider transportation.
         */

        on(GNOME_PILOTS, IntType.NPC, "glider") { player, _ ->
            if (!isQuestComplete(player, Quests.THE_GRAND_TREE)) {
                sendMessage(player, "You must complete The Grand Tree Quest to access the gnome glider.")
            } else {
                openInterface(player, Components.GLIDERMAP_138)
            }
            return@on true
        }

        /*
         * Handles talking to gnomes.
         */

        on(GNOME_ID, IntType.NPC, "talk-to") { player, _ ->
            openDialogue(player, GnomeDialogue())
            return@on true
        }

    }

    override fun defineInterfaceListeners() {

        /*
         * Handles glider interface.
         */

        onOpen(Components.GLIDERMAP_138) { player, _ ->
            setVarp(player, Vars.VARP_IFACE_GLIDER_CONFIG_153, 0)
            return@onOpen true
        }

        on(Components.GLIDERMAP_138) { player, _, _, buttonID, _, _ ->
            val glider = Glider.forId(buttonID) ?: return@on true
            when (buttonID) {
                glider.button -> submitWorldPulse(GliderPulse(1, player, glider))
            }
            return@on true
        }

        onClose(Components.GLIDERMAP_138) { player, _ ->
            unlock(player)
            return@onClose true
        }
    }
}

/**
 * Represents the glider transportation data.
 */
enum class Glider(val button: Int, val location: Location, val config: Int, val npc: Int) {
    CRASH_ISLAND(14, Location(2894, 2726, 0), 8, NPCs.CAPTAIN_ERRDO_3811), GANDIUS(15, Location(2972, 2969, 0), 8, NPCs.CAPTAIN_KLEMFOODLE_3812),
    TA_QUIR_PRIW(16, Location(2465, 3501, 3), 9, NPCs.CAPTAIN_DALBUR_3809), SINDARPOS(17, Location(2848, 3497, 0), 1, NPCs.CAPTAIN_BLEEMADGE_3810),
    LEMANTO_ADRA(18, Location(3321, 3427, 0), 3, NPCs.CAPTAIN_ERRDO_3811), KAR_HEWO(19, Location(3278, 3212, 0), 4, NPCs.CAPTAIN_KLEMFOODLE_3812),
    LEMANTOLLY_UNDRI(20, Location(2544, 2970, 0), 10, NPCs.GNORMADIUM_AVLAFRIM_1800),
    ;

    companion object {
        /**
         * Sends glider config to the player based on NPC.
         */
        @JvmStatic
        fun sendConfig(npc: NPC, player: Player) {
            val g = forNpc(npc.id)
            if (g != null) {
                setVarp(player, Vars.VARP_IFACE_GLIDER_CONFIG_153, g.config)
            }
        }

        /**
         * Gets glider by npc id, or null.
         */
        @JvmStatic
        fun forNpc(npcId: Int): Glider? {
            for (data in values()) {
                if (data.npc == npcId) {
                    return data
                }
            }
            return null
        }

        /**
         * Gets glider by button id, or null.
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

/**
 * Represents the glider pulse.
 */
class GliderPulse(
    delay: Int,
    private val player: Player,
    private val glider: Glider,
) : Pulse(delay, player) {
    private var count: Int = 0

    init {
        lock(player, 100)
    }

    override fun pulse(): Boolean {
        val crash = glider == Glider.LEMANTO_ADRA
        if (count == 1) {
            setVarp(player, Vars.VARP_IFACE_GLIDER_CONFIG_153, glider.config)
            setMinimapState(player, 2)
        } else if (count == 2 && crash) {
            PacketRepository.send(
                CameraViewPacket::class.java,
                CameraContext(player, CameraContext.CameraType.SHAKE, 4, 4, 1200, 4, 4),
            )
            sendMessage(player, "The glider almost gets blown from its path as it withstands heavy winds.")
        }
        when (count) {
            3 -> {
                openOverlay(player, Components.FADE_TO_BLACK_115)
            }

            4 -> {
                unlock(player)
                teleport(player, glider.location)
            }

            5 -> {
                if (crash) {
                    PlayerCamera(player).reset()
                    sendMessage(player, "The glider becomes uncontrollable and crashes down...")
                }
                closeOverlay(player)
                closeInterface(player)
                setMinimapState(player, 0)
                setVarp(player, Vars.VARP_IFACE_GLIDER_CONFIG_153, 0)
                if (!crash && glider == Glider.GANDIUS) {
                    finishDiaryTask(player, DiaryType.KARAMJA, 1, 11)
                }
                return true
            }
        }
        count++
        return false
    }
}