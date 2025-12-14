package content.region.asgarnia.rimmington.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Brian dialogue.
 */
class BrianDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.HAPPY, "Would you like to buy some archery equipment?").also { stage++ }
            1 -> showTopics(
                Topic("No thanks, I've got all the archery equipment I need.",2),
                Topic("Let's see what you've got, then.",3),
            )
            2 -> npc(FaceAnim.FRIENDLY, "Okay. Fare well on your travels.").also { stage = END_DIALOGUE }
            3 -> end().also { openNpcShop(player!!, NPCs.BRIAN_1860) }
        }
    }

}
