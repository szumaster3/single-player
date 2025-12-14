package content.region.desert.nardah.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Kazemde dialogue.
 */
class KazemdeDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc("Can I help you at all?").also { stage++ }
            1 -> options("Yes please. What are you selling?", "No thanks.").also { stage++ }
            2 -> when (buttonID) {
                1 -> {
                    end()
                    openNpcShop(player!!, NPCs.KAZEMDE_3039)
                }

                2 -> player("No thanks.").also { stage = END_DIALOGUE }
            }
        }
    }
}
