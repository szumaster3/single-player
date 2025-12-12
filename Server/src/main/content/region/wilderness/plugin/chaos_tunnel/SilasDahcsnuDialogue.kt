package content.region.wilderness.plugin.chaos_tunnel

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Silas Dahcsnu dialogue.
 */
class SilasDahcsnuDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player("Hello there.").also { stage++ }
            1 -> npc(FaceAnim.ANNOYED,"Can't you see that I'm busy here?").also { stage++ }
            2 -> player(FaceAnim.HALF_GUILTY, "Oh, Sorry, you don't look very busy.").also { stage++ }
            3 -> npc(FaceAnim.ASKING, "Don't look busy? I've got a lot of important work to do", "here.").also { stage++ }
            4 -> player(FaceAnim.HALF_ASKING, "Really? What do you do?").also { stage++ }
            5 -> npc(FaceAnim.ASKING,"That doesn't concern you. What are you doing", "here anyway?").also { stage++ }
            6 -> player(FaceAnim.FRIENDLY,"None of your business!").also { stage = END_DIALOGUE }
        }
    }
}
