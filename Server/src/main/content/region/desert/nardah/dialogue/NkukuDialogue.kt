package content.region.desert.nardah.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Nkuku dialogue.
 */
class NkukuDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player(FaceAnim.FRIENDLY, "Good day to you.").also { stage++ }
            1 -> npc(FaceAnim.FRIENDLY, "May Saradomin be with you.").also { stage = END_DIALOGUE }
        }
    }
}
