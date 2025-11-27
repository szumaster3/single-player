package content.region.misthalin.varrock.museum.plugin

import content.data.GameAttributes
import core.api.*
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.tools.colorize
import shared.consts.Components
import shared.consts.NPCs
import shared.consts.Sounds
import shared.consts.Vars

class MusemInterfaceListener : InterfaceListener {

    override fun defineInterfaceListeners() {

        /*
         * Handles opening kudos overlay.
         */

        onOpen(Components.VM_KUDOS_532) { player, _ ->
            val kudosAmount = getVarbit(player, Vars.VARBIT_KUDOS_VALUE_3637)
            sendString(
                player,
                if (kudosAmount == 163) {
                    colorize("%G$kudosAmount/163")
                } else {
                    "$kudosAmount/163"
                },
                Components.VM_KUDOS_532,
                1,
            )
            return@onOpen true
        }

        /*
         * Handles opening the museum map interface.
         */

        onOpen(Components.VM_MUSEUM_MAP_527) { player, _ ->
            showMapFloor(player, getAttribute(player, GameAttributes.MUSEUM_FLOOR_MAP_ATTRIBUTE, "main"))
            removeAttribute(player, GameAttributes.MUSEUM_FLOOR_MAP_ATTRIBUTE)
            return@onOpen true
        }

        /*
         * Handles switching between floors in the museum map interface.
         */

        on(Components.VM_MUSEUM_MAP_527) { player, _, _, buttonID, _, _ ->
            showMapFloor(
                player,
                when (buttonID) {
                    in mapButtonsToBasement -> "basement"
                    in mapButtonsToMainFloor -> "main"
                    in mapButtonsToSecondFloor -> "second"
                    in mapButtonsToTopFloor -> "top"
                    else -> return@on true
                },
            )
            return@on true
        }

        /*
         * Handles the response to the Natural History exam questions.
         */

        on(NATURAL_HISTORY_EXAM_533) { player, _, _, buttonID, _, _ ->
            if (buttonID in 29..31) {
                closeInterface(player)
                setVarbit(player, 3637, 1, false)
                playAudio(player, Sounds.VM_GAIN_KUDOS_3653)
                sendNPCDialogue(player, NPCs.ORLANDO_SMITH_5965, "Nice job, mate. That looks about right.")
            }
            return@on true
        }

        /*
         * Handles actions related to the lectern interface in the museum.
         */

        on(Components.VM_LECTERN_794) { player, _, _, buttonID, _, _ ->
            when (buttonID) {
                2 -> updateVarbit(player, 1)
                3 -> updateVarbit(player, -1)
                else -> return@on true
            }
            return@on true
        }

        /*
         * Handles closing the lectern interface in the museum.
         */

        onClose(Components.VM_LECTERN_794) { player, _ ->
            resetVarbit(player)
            return@onClose true
        }
    }

    private fun updateVarbit(player: Player, value: Int) {
        val currentVarbitValue = getVarbit(player, Vars.VARBIT_VARROCK_MUSEUM_CENSUS_5390)
        setVarbit(player, Vars.VARBIT_VARROCK_MUSEUM_CENSUS_5390, currentVarbitValue + value)
    }

    private fun resetVarbit(player: Player) {
        setVarbit(player, Vars.VARBIT_VARROCK_MUSEUM_CENSUS_5390, 0)
    }

    private fun showMapFloor(player: Player, floor: String) {// Author: Bonesy.
        when (floor) {
            "basement" -> {
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 2, true)
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 7, false)
            }

            "main" -> {
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 3, true)
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 7, true)
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 2, false)
            }

            "second" -> {
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 2, true)
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 5, true)
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 3, false)
            }

            "top" -> {
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 3, true)
                setComponentVisibility(player, Components.VM_MUSEUM_MAP_527, 5, false)
            }
        }
    }

    companion object {
        const val NATURAL_HISTORY_EXAM_533 = 533
        private val mapButtonsToBasement    = intArrayOf(41, 186)
        private val mapButtonsToMainFloor   = intArrayOf(117, 120, 187, 188)
        private val mapButtonsToSecondFloor = intArrayOf(42, 44, 152, 153)
        private val mapButtonsToTopFloor    = intArrayOf(42, 44, 118, 119)
    }
}