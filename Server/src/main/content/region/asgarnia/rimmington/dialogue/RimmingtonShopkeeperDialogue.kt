package content.region.asgarnia.rimmington.dialogue

import core.api.openNpcShop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.tools.END_DIALOGUE

/**
 * Represents the Rimmington shopkeeper dialogue.
 */
class RimmingtonShopkeeperDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc(FaceAnim.HALF_ASKING, "Can I help you at all?").also { stage++ }
            1 -> showTopics(
                Topic("Yes please, what are you selling?", 3),
                Topic("How should I use your shop?", 2),
                Topic("No, thanks.", END_DIALOGUE)
            )
            2 -> npc(FaceAnim.HAPPY, "I'm glad you ask! You can buy as many of the items", "stocked as you wish. You can also sell most items to", "the shop.").also { stage = 1 }
            3 -> {
                end()
                openNpcShop(player!!, npc!!.id)
            }
        }
    }
}
