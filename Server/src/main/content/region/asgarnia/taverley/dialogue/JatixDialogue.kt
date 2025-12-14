package content.region.asgarnia.taverley.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Jatix dialogue.
 */
class JatixDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.HALF_GUILTY, "Hello, how can I help you?").also { stage++ }
            1 -> showTopics(
                Topic("What are you selling?",2),
                Topic( "You can't; I'm beyond help.",END_DIALOGUE),
                Topic("I'm okay, thank you.", END_DIALOGUE),
            )
            2 -> end().also { openNpcShop(player!!, NPCs.JATIX_587) }
        }
    }
}
