package content.region.fremennik.plugin

import core.api.addDialogueAction
import core.api.quest.isQuestComplete
import core.api.quest.requireQuest
import core.api.removeItem
import core.api.sendDialogueOptions
import core.api.setTitle
import core.api.ui.closeDialogue
import core.cache.def.impl.NPCDefinition
import core.cache.def.impl.SceneryDefinition
import core.game.dialogue.FaceAnim
import core.game.dialogue.SequenceDialogue.dialogue
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.map.Location
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests
import org.rs.consts.Scenery

/**
 * Handles Fremennik region travel options.
 */
@Initializable
class FremennikPlugin : OptionHandler() {

    override fun newInstance(arg: Any?): Plugin<Any> {
        SceneryDefinition.forId(Scenery.BOAT_21176).handlers["option:travel"] = this
        SceneryDefinition.forId(Scenery.BOAT_21175).handlers["option:travel"] = this

        NPCDefinition.forId(NPCs.MARIA_GUNNARS_5508).handlers["ferry-neitiznot"] = this
        NPCDefinition.forId(NPCs.MARIA_GUNNARS_5507).handlers["ferry-rellekka"] = this

        NPCDefinition.forId(NPCs.MORD_GUNNARS_5481).handlers["ferry-jatizso"] = this
        NPCDefinition.forId(NPCs.MORD_GUNNARS_5482).handlers["ferry-rellekka"] = this

        NPCDefinition.forId(NPCs.SAILOR_1385).handlers["travel"] = this
        NPCDefinition.forId(NPCs.SAILOR_1304).handlers["travel"] = this

        NPCDefinition.forId(2435).handlers["option:travel"] = this
        NPCDefinition.forId(NPCs.JARVALD_2436).handlers["option:travel"] = this
        NPCDefinition.forId(NPCs.JARVALD_2437).handlers["option:travel"] = this
        NPCDefinition.forId(NPCs.JARVALD_2438).handlers["option:travel"] = this
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        val loc = node.location
        when (node.id) {
            // Maria Gunnars - Rellekka - Neitiznot
            NPCs.MARIA_GUNNARS_5508 -> {
                if (!requireQuest(player, Quests.THE_FREMENNIK_TRIALS, "")) return true
                FremennikTransportation.sail(player, Travel.RELLEKKA_TO_NEITIZNOT)
            }
            // Mord Gunnars - Rellekka - Jatizso
            NPCs.MARIA_GUNNARS_5507 -> FremennikTransportation.sail(player, Travel.NEITIZNOT_TO_RELLEKKA)
            NPCs.MORD_GUNNARS_5482 -> FremennikTransportation.sail(player, Travel.JATIZSO_TO_RELLEKKA)
            NPCs.MORD_GUNNARS_5481 -> {
                if (!requireQuest(player, Quests.THE_FREMENNIK_TRIALS, "")) return true
                FremennikTransportation.sail(player, Travel.RELLEKKA_TO_JATIZSO)
            }
            // Sailors - Rellekka - Miscellania
            NPCs.SAILOR_1385 -> {
                if (!requireQuest(player, Quests.THE_FREMENNIK_TRIALS, "")) return true
                FremennikTransportation.sail(player, Travel.RELLEKKA_TO_MISC)
            }
            NPCs.SAILOR_1304 -> {
                if (!requireQuest(player, Quests.THE_FREMENNIK_TRIALS, "")) return true
                FremennikTransportation.sail(player, Travel.MISC_TO_RELLEKKA)
            }
            // Iceberg - Rellekka
            Scenery.BOAT_21175 -> {
                if (loc == Location(2654, 3985, 1)) {
                    FremennikTransportation.sail(player, Travel.ICEBERG_TO_RELLEKKA)
                }
            }
            // Rellekka - Iceberg
            Scenery.BOAT_21176 -> {
                if (loc == Location(2708, 3732)) {
                    when (option.lowercase()) {
                        "iceberg" -> FremennikTransportation.sail(player, Travel.RELLEKKA_TO_ICEBERG)
                        "travel" -> {
                            setTitle(player, 2)
                            sendDialogueOptions(player, "Where would you like to travel?", "Iceberg", "Stay here")
                            addDialogueAction(player) { _, button ->
                                if (button == 1) FremennikTransportation.sail(player, Travel.RELLEKKA_TO_ICEBERG)
                                else closeDialogue(player)
                            }
                        }
                    }
                }
            }
            // Jarvald - Waterbirth - Rellekka
            2435, NPCs.JARVALD_2436, NPCs.JARVALD_2437, NPCs.JARVALD_2438 -> {
                dialogue(player) {
                    options("Leave island?", "YES", "NO") { opt ->
                        if (node.id == 2438 && opt == 1) FremennikTransportation.sail(
                            player,
                            Travel.WATERBIRTH_TO_RELLEKKA
                        ) else
                            if (!isQuestComplete(player, Quests.THE_FREMENNIK_TRIALS)) {
                            npc(node.id, FaceAnim.HALF_ASKING, "So do you have the 1000 coins for my service, and are you ready to leave now?")
                            options(null, "YES", "NO") { option ->
                                if (option == 1) removeItem(player, Item(Items.COINS_995, 1000))
                                FremennikTransportation.sail(player, Travel.RELLEKKA_TO_WATERBIRTH)
                            }
                        }
                    }
                }
            }
        }
        return true
    }
}