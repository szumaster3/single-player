package content.region.asgarnia.taverley.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Tegid dialogue.
 */
class TegidDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player(FaceAnim.HALF_GUILTY, "So, you're doing laundry, eh?").also { stage++ }
            1 -> npc(FaceAnim.HALF_GUILTY, "Yeah. What is it to you?").also { stage++ }
            2 -> player(FaceAnim.HALF_GUILTY, "Nice day for it.").also { stage++ }
            3 -> npc(FaceAnim.HALF_GUILTY, "Suppose it is.").also { stage = END_DIALOGUE }
        }
    }
}
