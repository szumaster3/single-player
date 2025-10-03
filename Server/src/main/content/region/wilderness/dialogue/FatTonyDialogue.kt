package content.region.wilderness.dialogue

import core.api.openNpcShop
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Fat Tony dialogue.
 */
@Initializable
class FatTonyDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc(FaceAnim.HALF_GUILTY, "Go away, I'm very busy.")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> showTopics(
                Topic("Sorry to disturb you.", 12, false),
                Topic("What are you busy doing?", 1, false),
                Topic("Have you anything to sell?", 10, false)
            )
            1 -> npc(FaceAnim.HALF_GUILTY, "I'm cooking pizzas for the people in this camp.").also { stage++ }
            2 -> npc(FaceAnim.HALF_GUILTY, "Not that these louts appreciate my gourmet cooking!").also { stage++ }
            3 -> showTopics(
                Topic("So what is a gourmet chef doing cooking for bandits?", 4, false),
                Topic("Can I have some pizza too?", 7, false),
                Topic("Okay, I'll leave you to it.", 12, false)
            )
            4 -> npc(FaceAnim.HALF_GUILTY, "Well, I'm an outlaw. I was accused of giving the king food", "poisoning!").also { stage++ }
            5 -> npc(FaceAnim.HALF_GUILTY, "The thought of it! I think he just drank too much wine", "that night.").also { stage++ }
            6 -> npc(FaceAnim.HALF_GUILTY, "I had to flee the kingdom of Misthalin. The bandits", "give me refuge here as long as I cook for them.").also { stage = 3 }
            7 -> npc(FaceAnim.HALF_GUILTY, "Well, this pizza is really meant to be for the bandits.", "I guess I could sell you some pizza bases though.").also { stage++ }
            8 -> options("Yes, okay.", "Oh, if I have to pay I don't want any.").also { stage++ }
            9 -> when (buttonId) {
                1 -> {
                    end()
                    openNpcShop(player, NPCs.FAT_TONY_596)
                }
                2 -> player("Oh, if I have to pay I don't want any.").also { stage = END_DIALOGUE }
            }
            10 -> npc(FaceAnim.HALF_GUILTY, "Well, I guess I can sell you some half-made pizzas.").also { stage++ }
            11 -> {
                end()
                openNpcShop(player, NPCs.FAT_TONY_596)
            }
            12 -> end()
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = FatTonyDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.FAT_TONY_596)
}
