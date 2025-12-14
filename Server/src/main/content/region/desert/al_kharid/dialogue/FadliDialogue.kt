package content.region.desert.al_kharid.dialogue

import core.api.openBankAccount
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE

/**
 * Represents the Fadli dialogue.
 */
class FadliDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player(FaceAnim.FRIENDLY, "Hi!").also { stage++ }
            1 -> npcl(FaceAnim.ASKING, "What?").also { stage++ }
            2 -> showTopics(
                Topic("What do you do?", 101),
                Topic("What is this place?", 201),
                Topic(FaceAnim.FRIENDLY, "I'd like to store some items, please.", 301),
                Topic("Do you watch any matches?", 401),
            )
            101 -> npcl(FaceAnim.FRIENDLY, "You can store your stuff here if you want. You can dump anything you don't want to carry whilst your fighting duels and then pick it up again on the way out.").also { stage++ }
            102 -> npcl(FaceAnim.SAD, "To be honest I'm wasted here.").also { stage++ }
            103 -> npcl(FaceAnim.EVIL_LAUGH, "I should be winning duels in an arena! I'm the best warrior in Al-Kharid!").also { stage++ }
            104 -> player(FaceAnim.HALF_WORRIED, "Easy, tiger!").also { stage = END_DIALOGUE }
            201 -> npcl(FaceAnim.LAUGH, "Isn't it obvious?").also { stage++ }
            202 -> npcl(FaceAnim.ANNOYED, "This is the Duel Arena...duh!").also { stage = END_DIALOGUE }
            301 -> npcl(FaceAnim.FRIENDLY, "Sure.").also { stage++ }
            302 -> {
                end()
                openBankAccount(player!!)
                stage = END_DIALOGUE
            }
            401 -> npcl(FaceAnim.LAUGH, "Most aren't any good so I throw rotten fruit at them!").also { stage = END_DIALOGUE }
        }
    }
}
