package content.region.desert.al_kharid.plugin

import content.region.desert.al_kharid.dialogue.*
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Quests
import shared.consts.Scenery

class AlkharidPlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles crossing the tollgate.
         */

        on(TOLL_GATES, IntType.SCENERY, "open", "pay-toll(10gp)") { player, node ->
            val usedOption = getUsedOption(player)
            val door = node.asScenery()

            if (usedOption == "pay-toll(10gp)") {
                if (getQuestStage(player, Quests.PRINCE_ALI_RESCUE) > 50) {
                    sendMessage(player, "The guards let you through for free.")
                    DoorActionHandler.handleAutowalkDoor(player, door)
                } else if (removeItem(player, Item(Items.COINS_995, 10))) {
                    sendMessage(player, "You quickly pay the 10 gold toll and go through the gates.")
                    DoorActionHandler.handleAutowalkDoor(player, door)
                } else {
                    sendMessage(player, "You need 10 gold to pass through the gates.")
                }
            } else {
                openDialogue(player, BorderGuardDialogue())
            }
            return@on true
        }

        /*
         * Handles options for Faldi NPC.
         */

        on(FADLI_NPC, IntType.NPC, "buy") { player, _ ->
            openNpcShop(player, NPCs.FADLI_958)
            return@on true
        }

        on(FADLI_NPC, IntType.NPC, "bank", "collect") { player, _ ->
            val option = getUsedOption(player)
            when (option) {
                "bank" -> openBankAccount(player)
                else -> openGrandExchangeCollectionBox(player)
            }
            return@on true
        }

        /*
         * Handles taking the leaflet.
         */

        on(LEAFLET_DROPPER, IntType.NPC, "Take-flyer") { player, node ->
            when {
                player.inventory.containItems(Items.AL_KHARID_FLYER_7922) -> {
                    sendNPCDialogue(player, node.id, "Are you trying to be funny or has age turned your brain to mush? You already have a flyer!", FaceAnim.CHILD_SUSPICIOUS)
                }
                else -> {
                    if(freeSlots(player) == 0) {
                        return@on true
                    } else {
                        openDialogue(player, LeafletDialogue(), LEAFLET_DROPPER)
                    }
                }
            }
            return@on true
        }

        /*
         * Handles talking to NPCs around city.
         */

        on(BORDER_GUARD, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, BorderGuardDialogue(), node.asNpc())
            return@on true
        }

        on(HEALERS_NPC, IntType.NPC, "heal") { player, node ->
            openDialogue(player, AlKharidHealDialogue(false), node.asNpc())
            return@on true
        }

        on(NPCs.AABLA_959, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AablaDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.ALI_MORRISANE_1862, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AliMorrisaneDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.ALI_THE_FARMER_2821, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AliTheFarmerDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.ALI_THE_GUARD_2823, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AliTheGuardDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.ALI_THE_LEAFLET_DROPPER_3680, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AliTheLeafletDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.ALI_THE_SMITH_2820, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AliTheSmithDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.ALI_THE_TAILOR_2822, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AliTheTailorDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.CAPTAIN_NINTO_4594, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, CaptainNintoDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.CAPTAIN_DAERKIN_4595, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, CaptainDaerkinDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.DOMMIK_545, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, DommikDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.FADLI_958, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, FadliDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.JARAAH_962, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, JaraahDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.LOUIE_LEGS_542, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, LouieLegsDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.RANAEL_544, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, RanaelDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.SABREEN_960, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, SabreenDialogue(), node.asNpc())
            return@on true
        }

        on(NPCs.ZEKE_541, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, ZekeDialogue(), node.asNpc())
            return@on true
        }
    }

    inner class LeafletDialogue : DialogueFile() {
        override fun handle(componentID: Int, buttonID: Int) {
            when(stage) {
                0 -> npcl(FaceAnim.CHILD_NORMAL, "Here! Take one and let me get back to work.").also { stage++ }
                1 -> npcl(FaceAnim.CHILD_THINKING, "I still have hundreds of these flyers to hand out. I wonder if Ali would notice if I quietly dumped them somewhere?").also { stage++ }
                2 -> {
                    end()
                    addItem(player!!, Items.AL_KHARID_FLYER_7922)
                }
            }
        }
    }

    companion object {
        private const val FADLI_NPC = NPCs.FADLI_958
        private const val LEAFLET_DROPPER = NPCs.ALI_THE_LEAFLET_DROPPER_3680
        private val HEALERS_NPC = intArrayOf(NPCs.AABLA_959, NPCs.SABREEN_960, NPCs.SURGEON_GENERAL_TAFANI_961, NPCs.JARAAH_962)
        private val TOLL_GATES = intArrayOf(Scenery.GATE_35551, Scenery.GATE_35549)
        private const val BORDER_GUARD = NPCs.BORDER_GUARD_7912
    }
}
