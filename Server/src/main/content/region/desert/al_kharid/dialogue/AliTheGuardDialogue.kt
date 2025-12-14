package content.region.desert.al_kharid.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE

/**
 * Represents the Ali The Guard dialogue.
 */
class AliTheGuardDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> playerl(FaceAnim.FRIENDLY, "Hello there!").also { stage++ }
            1 -> npcl(FaceAnim.ANNOYED, "I'm working. What do you have to say that's so urgent?").also { stage++ }
            2 -> showTopics(
                Topic("What can you tell me about Al Kharid?",10),
                Topic("So, what do you do here?", 20),
                Topic("I hear you work for Ali Morrisane...", 30),
                Topic("I hear you've been threatening the other shopkeepers.", 40),
            )
            10 -> npcl(FaceAnim.FRIENDLY, "There's a lot of space here. More open space than back home.").also { stage++ }
            11 -> playerl(FaceAnim.ASKING, "So where is back home?").also { stage++ }
            12 -> npcl(FaceAnim.FRIENDLY, "Pollnivneach. It's a town to the south of the Shantay Pass.").also { stage = 1 }
            20 -> npcl(FaceAnim.ANNOYED, "I'm on guard duty. Making sure nobody tries to steal anything from the house and tents in the middle of town.").also { stage++ }
            21 -> playerl(FaceAnim.ASKING, "Why are you only guarding those buildings?").also { stage++ }
            22 -> npcl(FaceAnim.SUSPICIOUS, "That's all I've been hired to guard.").also { stage = 2 }
            30 -> npcl(FaceAnim.FRIENDLY, "Yeah, he hired me. He owns this house and these two tents, too.").also { stage++ }
            31 -> playerl(FaceAnim.HALF_ASKING, "Is the work good?").also { stage++ }
            32 -> npcl(FaceAnim.FRIENDLY, "It pays better than back home.").also { stage++ }
            33 -> playerl(FaceAnim.ASKING, "Why, what did you do back home?").also { stage++ }
            34 -> npcl(FaceAnim.SUSPICIOUS, "Never you mind.").also { stage++ }
            35 -> npcl(FaceAnim.FRIENDLY, "But Ali Morrisane pays us well, at least.").also { stage++ }
            36 -> playerl(FaceAnim.ASKING, "Maybe I should talk to him...").also { stage++ }
            37 -> npcl(FaceAnim.FRIENDLY, "Why not? He always likes to meet potential business partners.").also { stage = 2 }
            40 -> npcl(FaceAnim.ANNOYED, "So? They talk too much.").also { stage++ }
            41 -> playerl(FaceAnim.HALF_ASKING, "You're not going to deny it?").also { stage++ }
            42 -> npcl(FaceAnim.LOUDLY_LAUGHING, "Why bother? None of them can fight back, after all.").also { stage = END_DIALOGUE }
        }
    }
}
