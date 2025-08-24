package core.game.worldevents.events.halloween

import core.api.openDialogue
import core.game.dialogue.DialogueFile
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.NPCs

class ZombieHalloweenPlugin : InteractionListener {

    override fun defineListeners() {

        on(intArrayOf(NPCs.ZOMBIE_2863,NPCs.ZOMBIE_2866,NPCs.ZOMBIE_2869), IntType.NPC, "call-over") { player, node ->
            openDialogue(player, ZombieHalloweenDialogue(), node.id)
            return@on true
        }
    }

    inner class ZombieHalloweenDialogue : DialogueFile() {
        override fun handle(componentID: Int, buttonID: Int) {
            when(stage) {
                0 -> when(npc?.id){
                    NPCs.ZOMBIE_2863 -> npc("Durrrr...").also { stage++ }
                    NPCs.ZOMBIE_2866 -> npc("Braaaaains...").also { stage = 10 }
                    else -> npc("Traaa laaa laaa...")
                }

                1 -> player("Hey! Come over here!").also { stage++ }
                2 -> npc("Who? Me?").also { stage++ }
                3 -> player("Yes, you!").also { stage++ }
                4 -> npc("Give me a moment...I can't move that fast.").also { stage++ }
                5 -> player("Why not?").also { stage++ }
                6 -> npc("I used to be a magician's assistant until the accident...").also { stage++ }
                7 -> player("What accident?").also { stage++ }
                8 -> npc("Looook...").also { stage++ }

                9 -> {
                    end()
                }

                10 -> player("Zombie, it has come to my attention that you are a fool, a nitwit if you will.").also { stage++ }
                11 -> player("I propose that your head, instead of being filled with the brains you so crave, is instead filled with a mixture of crushed lemons and kelp!").also { stage++ }
                12 -> npc("Oh noooo..I'd best take it off then...").also { stage++ }
                13 -> player("What?").also { stage = 9 }

                14 -> player("Oogaboogabooga!").also { stage++ }
                15 -> npc("Aaaaaargh!").also { stage = 9 }
            }
        }
    }
}