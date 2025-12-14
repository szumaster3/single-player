package content.region.desert.al_kharid.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Ranael dialogue.
 */
class RanaelDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.HAPPY, "Do you want to buy any armoured skirts? Designed", "especially for ladies who like to fight.").also { stage++ }
            1 -> showTopics(
                Topic("Yes, please.", 2),
                Topic("No, thank you, that's not my scene.", END_DIALOGUE),
            )
            2 -> {
                end()
                openNpcShop(player!!, NPCs.RANAEL_544)
            }
        }
    }
}
