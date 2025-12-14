package content.region.asgarnia.taverley.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE

/**
 * Represents the Gaius dialogue.
 */
class GaiusDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npcl(FaceAnim.FRIENDLY, "Welcome to my two-handed sword shop.").also { stage++ }
            1 -> showTopics(
                Topic("Let's trade.",2),
                Topic(  "Thanks, but not today.", 3),
            )
            2 -> end().also { openNpcShop(player!!, npc!!.id) }
            3 -> npcl(FaceAnim.FRIENDLY, "Very well, but do please call again.").also { stage = END_DIALOGUE }
        }
    }
}
