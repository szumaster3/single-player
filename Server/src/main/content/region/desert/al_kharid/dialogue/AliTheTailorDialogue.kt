package content.region.desert.al_kharid.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE

/**
 * Represents the Ali The Tailor dialogue.
 */
class AliTheTailorDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> playerl(FaceAnim.FRIENDLY, "Hello there!").also { stage++ }
            1 -> npcl(FaceAnim.ANNOYED, "I'm a little busy at the moment. What is it?").also { stage++ }
            2 -> showTopics(
                Topic("What can you tell me about Al Kharid?",10),
                Topic("So, what do you do here?", 15),
                Topic("I hear you work for Ali Morrisane...", 19),
                Topic("I hear you've been threatening the other shopkeepers.", 24),
            )
            10 -> npcl(FaceAnim.FRIENDLY, "Oh, it has wonderful weather.").also { stage++ }
            11 -> playerl(FaceAnim.HALF_ASKING, "Wonderful? It's hot and dry all the time!").also { stage++ }
            12 -> npcl(FaceAnim.FRIENDLY, "Not quite as hot as back home.").also { stage++ }
            13 -> playerl(FaceAnim.FRIENDLY, "Where's home, then?").also { stage++ }
            14 -> npcl(FaceAnim.FRIENDLY, "Oh, it's a town to the south of the pass, called Pollnivneach.").also { stage = END_DIALOGUE }
            15 -> npcl(FaceAnim.FRIENDLY, "If I had cloth, patterns and customers, I'd be a tailor. As it is, I'm a tailor with nothing to do.").also { stage++ }
            16 -> npcl(FaceAnim.FRIENDLY, "The silk merchant won't even sell me any silks, because he doesn't trust me!").also { stage++ }
            17 -> player(FaceAnim.HALF_ASKING, "Anything I can do to help?").also { stage++ }
            18 -> npcl(FaceAnim.FRIENDLY, "No, no, it's all being dealt with.").also { stage = END_DIALOGUE }
            19 -> npcl(FaceAnim.FRIENDLY, "Of course, he's the one who's going to obtain cloth and clothes patterns so I can set up shop here.").also { stage++ }
            20 -> npcl(FaceAnim.FRIENDLY, "And in such a good location, too. Not out of the way like back home.").also { stage++ }
            21 -> npcl(FaceAnim.FRIENDLY, "The customers will soon pour in!").also { stage++ }
            22 -> player("Maybe I should talk to him...").also { stage++ }
            23 -> npcl(FaceAnim.FRIENDLY, "Why not? He's always happy to talk to potential business partners.").also { stage = END_DIALOGUE }
            24 -> npc(FaceAnim.ASKING,"Me, threaten people?").also { stage++ }
            25 -> playerl(FaceAnim.FRIENDLY, "One of the shopkeepers did say they were threatened by a man with large scissors...").also { stage++ }
            26 -> npcl(FaceAnim.FURIOUS, "Oh, them. Don't mind them. I think they're worried about what effect our shops will have.").also { stage++ }
            27 -> npcl(FaceAnim.FRIENDLY, "For all they know, when we open our shops their wares will look cheap and shabby by comparison!").also { stage = END_DIALOGUE }
        }
    }
}
