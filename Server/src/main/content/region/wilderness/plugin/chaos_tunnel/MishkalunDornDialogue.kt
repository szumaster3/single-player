package content.region.wilderness.plugin.chaos_tunnel

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Mishkalun Dorn dialogue.
 */
class MishkalunDornDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0  -> npc(FaceAnim.NEUTRAL, "You are excused. And you are welcome.").also { stage++ }
            1  -> player(FaceAnim.STRUGGLE, "Excuse me...er..thanks.").also { stage++ }
            2  -> npc(FaceAnim.NEUTRAL, "We are the Order of the Dagon'hai.").also { stage++ }
            3  -> player(FaceAnim.HALF_ASKING, "Who are you?").also { stage++ }
            4  -> npc(FaceAnim.NEUTRAL, "Through my magic, I can see a short way into", "the future.").also { stage++ }
            5  -> player(FaceAnim.HALF_ASKING, "How do you seem to know what i'm going to", "say? ...Er...oh.").also { stage++ }
            6  -> npc(FaceAnim.NEUTRAL, "These are the Tunnels of Chaos.").also { stage++ }
            7  -> player(FaceAnim.HALF_THINKING, "What is...uh..aha! I'm not going to ask that. So you got it", "wrong!").also { stage++ }
            8  -> npc(FaceAnim.NEUTRAL, "Indeed. You are very clever.").also { stage++ }
            9  -> player(FaceAnim.HAPPY, "So I won!").also { stage++ }
            10 -> npc(FaceAnim.NEUTRAL, "Yes.").also { stage = END_DIALOGUE }
        }
    }
}
