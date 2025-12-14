package content.region.desert.al_kharid.dialogue

import core.api.addItem
import core.api.inInventory
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE
import shared.consts.Items

/**
 * Represents the Ali The Leaflet dialogue.
 */
class AliTheLeafletDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npcl(FaceAnim.CHILD_NORMAL, "I don't have the time to talk right now! Ali Morrisane is paying me to hand out these flyers.").also { stage++ }
            1 -> showTopics(
                Topic("Who is Ali Morissane?",201),
                Topic("What are the flyers for?", 301),
                Topic( "What is there to do round here, boy?", 401),
            )
            201 -> npcl(FaceAnim.CHILD_FRIENDLY, "Ali Morrisane is the greatest merchant in the east!").also { stage++ }
            202 -> playerl(FaceAnim.HALF_ASKING, "Were you paid to say that?").also { stage++ }
            203 -> npcl(FaceAnim.CHILD_LOUDLY_LAUGHING, "Of course I was! You can find him on the north edge of town.").also { stage = END_DIALOGUE }
            301 -> npcl(FaceAnim.CHILD_THINKING, "Well, Ali Morrisane isn't too popular with the other traders in Al Kharid, mainly because he's from Pollnivneach and they feel he has no business trading in their town.").also { stage++ }
            302 -> npcl(FaceAnim.CHILD_FRIENDLY, "I think they're just sour because he's better at making money than them.").also { stage++ }
            303 -> npcl(FaceAnim.CHILD_FRIENDLY, "The flyer advertises the different shops you can find in Al Kharid.").also { stage++ }
            304 -> npcl(FaceAnim.CHILD_THINKING, "It also entitles you to money off your next purchase in any of the shops listed on it. It's Ali's way of getting on the good side of the traders.").also { stage++ }
            305 -> playerl(FaceAnim.ASKING, "Which shops?").also {
                end()
                if (inInventory(player!!, Items.AL_KHARID_FLYER_7922)) {
                    npcl(FaceAnim.CHILD_SUSPICIOUS, "Are you trying to be funny or has age turned your brain to mush? Look at the flyer you already have!")
                    stage = END_DIALOGUE
                } else {
                    addItem(player!!, Items.AL_KHARID_FLYER_7922)
                    npcl(FaceAnim.CHILD_FRIENDLY, "Here! Take one and let me get back to work.")
                    stage = END_DIALOGUE
                }
            }
            401 -> npcl(FaceAnim.CHILD_NORMAL, "I'm very busy, so listen carefully! I shall say this only once.").also { stage++ }
            402 -> npcl(FaceAnim.CHILD_THINKING, "Apart from a busy and wonderous market place in Al Kharid to the south, there is the Duel Arena to the south-east where you can challenge other players to a fight.").also { stage++ }
            403 -> npcl(FaceAnim.CHILD_NORMAL, "If you're here to make money, there is a mine to the south.").also { stage++ }
            404 -> npcl(FaceAnim.CHILD_SUSPICIOUS, "Watch out for scorpions though, they'll take a pop at you if you go too near them. To avoid them just follow the western fence as you travel south.").also { stage++ }
            405 -> npcl(FaceAnim.CHILD_FRIENDLY, "If you're in the mood for a little rest and relaxation, there are a couple of nice fishing spots south of the town.").also { stage++ }
            406 -> playerl(FaceAnim.FRIENDLY, "Thanks for the help!").also { stage = END_DIALOGUE }
        }
    }
}
