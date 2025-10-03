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
 * Represents the Noterazzo dialogue.
 */
@Initializable
class NoterazzoDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc(FaceAnim.HALF_ASKING, "Hey, wanna trade? I'll give the best deals you can find.")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> showTopics(
                Topic("Yes please.", 1, false),
                Topic("No thanks.", 4, false),
                Topic("How can you afford to give such good deals?", 2, false)
            )
            1 -> end().also { openNpcShop(player, NPCs.NOTERAZZO_597) }
            2 -> npc("The general stores in Asgarnia and Misthalin are heavily", "taxed.").also { stage++ }
            3 -> npc("It really makes it hard for them to run an effective", "business. For some reason taxmen don't visit my store.").also { stage = END_DIALOGUE }
            4 -> player("No, thanks.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = NoterazzoDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.NOTERAZZO_597)
}
