package content.region.desert.al_kharid.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Louie Legs dialogue.
 */
class LouieLegsDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.HAPPY, "Hey, wanna buy some armour?").also { stage++ }
            1 -> showTopics(
                Topic("What have you got?", 2),
                Topic("No, thank you.", END_DIALOGUE),
            )
            2 -> npc(FaceAnim.HAPPY, "I provide items to help you keep your legs!").also { stage++ }
            3 -> {
                end()
                openNpcShop(player!!, NPCs.LOUIE_LEGS_542)
            }
        }
    }
}
