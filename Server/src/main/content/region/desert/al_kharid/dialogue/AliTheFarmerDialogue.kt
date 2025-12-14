package content.region.desert.al_kharid.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE

/**
 * Represents the Ali The Farmer dialogue.
 */
class AliTheFarmerDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> playerl(FaceAnim.FRIENDLY, "Hello there!").also { stage++ }
            1 -> npcl(FaceAnim.HALF_ASKING, "Oh, er, hello. What do you want?").also { stage++ }
            2 -> showTopics(
                Topic("What can you tell me about Al Kharid?",10),
                Topic("So, what do you do here?", 19),
                Topic("I hear you work for Ali Morrisane...", 30),
                Topic("I hear you've been threatening the other shopkeepers.", 40),
            )
            10 -> npcl(FaceAnim.FRIENDLY, "There's not much farming land around here. Only that little patch outside.").also { stage++ }
            11 -> playerl(FaceAnim.HALF_ASKING, "Can you give me any advice on farming here in the desert?").also { stage++ }
            12 -> npcl(FaceAnim.FRIENDLY, "Like I said, I only know about cactuses...").also { stage++ }
            13 -> playerl(FaceAnim.FRIENDLY, "Just tell me about cactuses, then.").also { stage++ }
            14 -> npcl(FaceAnim.FRIENDLY, "First you have to weed the patch using a rake.").also { stage++ }
            15 -> playerl(FaceAnim.ASKING, "Can you give me any other advice?").also { stage++ }
            16 -> npcl(FaceAnim.FRIENDLY, "Not really. I've not done much farming recently.").also { stage++ }
            17 -> playerl(FaceAnim.HALF_ASKING, "Well, can you at least sell me any gardening tools or seeds?").also { stage++ }
            18 -> npcl(FaceAnim.GUILTY, "Sorry. They haven't been delivered yet.").also { stage = 2 }
            19 -> npcl(FaceAnim.FRIENDLY, "I'm going to set up a shop selling farming implements. That patch out there may be small, but it's all we've got.").also { stage = 11 }
            30 -> npcl(FaceAnim.FRIENDLY, "Yes, he bought these tents and had them put up for us. He says he'll also get our goods in, so we can start selling them soon.").also { stage++ }
            31 -> npcl(FaceAnim.FRIENDLY, "I only know how to farm cactuses, so this spot is perfect.").also { stage++ }
            32 -> playerl(FaceAnim.THINKING, "Maybe I should talk to him...").also { stage++ }
            33 -> npcl(FaceAnim.FRIENDLY, "Of course. He's always happy to talk to possible business partners.").also { stage = 2 }
            40 -> npcl(FaceAnim.ASKING, "Now why would you think that?").also { stage++ }
            41 -> playerl(FaceAnim.FRIENDLY, "One of the shopkeepers told they were threatened by a man with a rake...").also { stage++ }
            42 -> npcl(FaceAnim.FRIENDLY, "Those people just don't want us to succeed. Don't listen to them!").also { stage = END_DIALOGUE }
        }
    }
}
