package content.region.desert.al_kharid.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Dommik dialogue.
 */
class DommikDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npcl(FaceAnim.HAPPY, "Would you like to buy some Crafting equipment?").also { stage++ }
            1 -> showTopics(
                Topic("Let's see what you've got, then.",2),
                Topic("No, thanks, I've got all the Crafting equipment I need.", 3),
            )
            2 -> {
                end()
                openNpcShop(player!!, NPCs.DOMMIK_545)
            }
            3 -> npcl(FaceAnim.FRIENDLY, "Okay. Fare well on your travels.").also { stage = END_DIALOGUE }
        }
    }
}
