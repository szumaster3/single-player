package content.region.desert.al_kharid.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Zeke dialogue.
 */
class ZekeDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.HAPPY, "A thousand greetings, sir.").also { stage++ }
            1 -> showTopics(
                Topic("Do you want to trade?", 10),
                Topic("Nice cloak.", 20),
                Topic("Could you sell me a dragon scimitar?", 30),
            )
            10 -> npc(FaceAnim.HAPPY, "Yes, certainly. I deal in scimitars.").also { stage++ }
            11 -> {
                end()
                openNpcShop(player!!, NPCs.ZEKE_541)
            }
            20 -> npc(FaceAnim.HAPPY, "Thank you.").also { stage = END_DIALOGUE }
            30 -> npc(FaceAnim.EXTREMELY_SHOCKED, "A dragon scimitar? A DRAGON scimitar?").also { stage++ }
            31 -> npc(FaceAnim.EXTREMELY_SHOCKED, "No way, man!").also { stage++ }
            32 -> npc(FaceAnim.ANGRY, "The banana-brained nitwits who make them would never", "dream of selling any to me.").also { stage++ }
            33 -> npc(FaceAnim.FRIENDLY, "Seriously, you'll be a monkey's uncle before you'll ever", "hold a dragon scimitar.").also { stage++ }
            34 -> player(FaceAnim.SUSPICIOUS, "Hmmm, funny you should say that...").also { stage++ }
            35 -> npc(FaceAnim.ASKING, "Perhaps you'd like to take a look at my stock?").also { stage++ }
            36 -> showTopics(
                Topic("Yes, please, Zeke.", 11),
                Topic("Not today, thank you.", END_DIALOGUE),
            )
        }
    }
}
