package content.region.asgarnia.rimmington.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Hengel dialogue.
 */
class HengelDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player("Hello.").also { stage++ }
            1 -> npc(FaceAnim.HALF_ASKING, "What are you doing here?").also { stage++ }
            2 -> options("I'm just wandering around.", "I was hoping you'd give me some free stuff.", "I've come to kill you.").also { stage++ }
            3 -> when (buttonID) {
                1 -> player("I'm just wondering around.").also { stage++ }
                2 -> player("I was hoping you'd give me some free stuff.").also { stage = 8 }
                3 -> player("I've come to kill you.").also { stage = 10 }
            }
            4 -> npc(FaceAnim.HALF_ASKING,"You do realise you're wandering around in my house?").also { stage++ }
            5 -> player("Yep.").also { stage++ }
            6 -> npc("Well please get out!").also { stage++ }
            7 -> player("Sheesh, keep your wig on!").also { stage = END_DIALOGUE }
            8 -> npc(FaceAnim.ANGRY,"No, I jolly well wouldn't!", "Get out of my house!").also { stage++ }
            9 -> player("Meanie!").also { stage = END_DIALOGUE }
            10 -> {
                if (player!!.name.equals("Anja", ignoreCase = true)) {
                    npc!!.sendChat("Eeeek!")
                } else {
                    npc!!.sendChat("Aaaaarrgh!")
                }
                end()
            }
        }
    }
}
