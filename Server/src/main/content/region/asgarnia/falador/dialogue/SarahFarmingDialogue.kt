package content.region.asgarnia.falador.dialogue

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
 * Represents the Sarah Farming dialogue.
 */
@Initializable
class SarahFarmingDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc(FaceAnim.HALF_GUILTY, "Hello. How can I help you?")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> showTopics(
                Topic("What are you selling?",openNpcShop(player, NPCs.SARAH_2304), true),
                Topic("Can you give me any Farming advice?", 1),
                Topic("Can you tell me how to use the loom?", 2),
                Topic("I'm okay, thank you.", END_DIALOGUE),
            )
            1 -> npc(FaceAnim.HALF_GUILTY, "Yes - ask a gardener.").also { stage = END_DIALOGUE }
            2 -> npcl(FaceAnim.FRIENDLY, "Well, it's actually my loom, but I don't mind you using it, if you like. You can use it to weave sacks and baskets in which you can put vegetables and fruit.").also { stage++ }
            3 -> showTopics(
                Topic("What do I need to weave sacks?", 4),
                Topic("What do I need to weave baskets?",5),
                Topic("Thank you, that's very kind.", END_DIALOGUE),
            )
            4 -> npcl(FaceAnim.HAPPY, "Well, the best sacks are made with jute fibres; you can grow jute yourself in a hops patch. I'd say about 4 jute fibres should be enough to weave a sack.").also { stage = 7 }
            5 -> npcl(FaceAnim.HAPPY,"Well, the best baskets are made with young branches cut from a willow tree. You'll need a very young willow tree; otherwise, the branches will have grown too thick to be able to weave. I suggest growing your own.").also { stage++ }
            6 -> npcl(FaceAnim.HAPPY, "You can cut the branches with a standard pair of secateurs. You will probably need about 6 willow branches to weave a complete basket.").also { stage++ }
            7 -> player("Thank you, that's very kind.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.SARAH_2304)
}
