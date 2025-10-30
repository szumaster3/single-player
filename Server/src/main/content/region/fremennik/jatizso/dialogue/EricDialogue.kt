package content.region.fremennik.jatizso.dialogue

import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Eric dialogue.
 */
@Initializable
class EricDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npcl(FaceAnim.HALF_GUILTY, "Spare us a few coppers, mister?")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        playerl(FaceAnim.ANGRY, "No, go away!").also { stage = END_DIALOGUE }

        // Collecting the window tax during The Fremennik Isles
        /*
            player("Hello, I'm the tax collector. Got any windows I can tax?")
            npc(" I dream of windows.")
        */

        // Collecting the beard tax during The Fremennik Isles
        /*
            player("Hello, I'm the tax collector. I'm taxing beards and I can't help noticing you have a beard.")
            npc(" What? I'm a beggar. Do I look as if I have any money? Look, take everything I own why don't you?")
            sendItemDialogue(player, Items.CABBAGE_1965, "The beggar throws a cabbage at you with disdain.")
            addItem(player, Items.CABBAGE_1965, 1)
            player("Err, thanks. Maybe I don't need to tax you.")
            npc("No, take it!")
            npc("I don't care any more.")
        */
        return true
    }

    override fun newInstance(player: Player?): Dialogue = EricDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.ERIC_5499)
}
