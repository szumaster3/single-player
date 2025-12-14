package content.region.desert.al_kharid.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE

/**
 * Represents the Sabreen dialogue.
 */
class SabreenDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player(FaceAnim.FRIENDLY, "Hi!").also { stage++ }
            1 -> showTopics(
                Topic("Can you heal me?", AlKharidHealDialogue(true)),
                Topic("Do you see a lot of injured fighters?", 2),
                Topic("Do you come here often?", 3),
            )
            2 -> npcl(FaceAnim.FRIENDLY, "I work here, so yes!").also { stage = END_DIALOGUE }
            3 -> npcl(FaceAnim.HALF_THINKING, "Yes I do. Thankfully we can cope with almost anything. Jaraah really is a wonderful surgeon, his methods are a little unorthodox but he gets the job done.").also { stage++ }
            4 -> npcl(FaceAnim.HALF_GUILTY, "I shouldn't tell you this but his nickname is 'The Butcher'.").also { stage++ }
            5 -> playerl(FaceAnim.HALF_WORRIED, "That's reassuring.").also { stage = END_DIALOGUE }
        }
    }
}
