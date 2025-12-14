package content.region.desert.al_kharid.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Captain Daerkin dialogue.
 */
class CaptainDaerkinDialogue : DialogueFile() {

    /*
     * He is found on the viewing walls of the Duel Arena near Jadid.
     */

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npcl(FaceAnim.OLD_DEFAULT, "Hello old chap.").also { stage++ }
            1 -> playerl(FaceAnim.HALF_ASKING, "What are you doing here? Shouldn't you be looking after your glider?").also { stage++ }
            2 -> npcl(FaceAnim.OLD_DEFAULT, "I'm pretty much retired these days old fellow. My test piloting days are over. I'm just relaxing here and enjoying the primal clash between man and man.").also { stage++ }
            3 -> playerl(FaceAnim.HALF_ASKING, "You're watching the duels then. Are you going to challenge someone yourself?").also { stage++ }
            4 -> npcl(FaceAnim.OLD_DEFAULT, "I do find the duels entertaining to watch, but I suspect that actually being involved would be a lot less fun for me. I'm a lover, not a fighter!").also { stage++ }
            5 -> playerl(FaceAnim.STRUGGLE, "Errm, I suppose you are.").also { stage = END_DIALOGUE }
        }
    }
}
