package content.region.asgarnia.rimmington.dialogue

import core.api.openNpcShop
import core.api.sendOptions
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Rommik dialogue.
 */
class RommikDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.HAPPY, "Would you like to buy some Crafting equipment?").also { stage++ }
            1 -> sendOptions(player!!, "Choose an option:", "Let's see what you've got, then.", "No thanks.").also { stage++ }
            2 -> when (buttonID) {
                1 -> end().also { openNpcShop(player!!, NPCs.ROMMIK_585) }
                2 -> player(FaceAnim.HALF_GUILTY, "No thanks, I've got all the crafting equipment I need.").also { stage++ }
            }
            3 -> npc(FaceAnim.FRIENDLY, "Okay. Fare well on your travels.").also { stage = END_DIALOGUE }
        }
    }
}
