package content.region.asgarnia.rimmington.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.item.Item
import core.tools.END_DIALOGUE
import core.tools.RandomFunction

/**
 * Represents the Anja dialogue.
 */
class AnjaDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player("Hello.").also { stage++ }
            1 -> npc(FaceAnim.HALF_ASKING, "Hello sir. What are you doing in my house?").also { stage++ }
            2 -> options("I'm just wandering around.", "I was hoping you'd give me some free stuff.", "I've come to kill you.").also { stage++ }
            3 -> when (buttonID) {
                1 -> player("I'm just wondering around.").also { stage = 4 }
                2 -> player("I was hoping you'd give me some free stuff.").also { stage = 11 }
                3 -> player("I've come to kill you.").also { stage = 14 }
            }

            4 -> npc(FaceAnim.HALF_ASKING,"Oh dear are you lost?").also { stage++ }
            5 -> options("Yes, I'm lost.", "No, I know where I am.").also { stage++ }
            6 -> when (buttonID) {
                1 -> player("Yes, I'm lost.").also { stage++ }
                2 -> player("No I know where I am.").also { stage = 9 }
            }
            7 -> npc("Okay, just walk north-east when you leave this house,", "and soon you'll reach the big city of Falador.").also { stage++ }
            8 -> player("Thanks a lot.").also { stage = END_DIALOGUE }
            9 -> npc("Oh? Well, would you mind wandering somewhere else?", "This is my house.").also { stage++ }
            10 -> player("Meh!").also { stage = END_DIALOGUE }
            11 -> {
                val dialogues = arrayOf("Do you REALLY need it", "I don't have much on me...", "I don't know...")
                npc(dialogues[RandomFunction.random(0, 2)]).also { stage++ }
            }
            12 -> player(FaceAnim.SAD, "I promise I'll stop bothering you!", "Pleeease!", "Pwetty pleathe wiv thugar on top!").also { stage++ }
            13 -> {
                end()
                npc("Oh, alright. Here you go.")
                player!!.inventory.add(Item(995, RandomFunction.random(1, 3)))
            }
            14 -> {
                if (player!!.name.equals("Hengel", ignoreCase = true)) {
                    npc!!.sendChat("Aaaaarrgh!")
                } else {
                    npc!!.sendChat("Eeeek!")
                }
                end()
            }
        }
    }
}
