package content.region.other.dorgeshuun.dialogue

import core.api.openNpcShop
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Miltog dialogue.
 */
@Initializable
class MiltogDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            START_DIALOGUE -> npcl(FaceAnim.OLD_NORMAL, "Do you want to buy a lamp, surface-dweller?").also { stage = 1 }
            1 -> showTopics(
                Topic("Yes please.", 2, false),
                Topic("No thanks.", 3, false),
                Topic("Can you fill a lamp with oil please?", 4, false),
                Topic("How do the lamps around the city work?", 5, false)
            )
            2 -> {
                end()
                openNpcShop(player, NPCs.MILTOG_5781)
            }
            3 -> npcl(FaceAnim.OLD_NORMAL, "Suit yourself.").also { stage = END_DIALOGUE }
            4 -> npcl(FaceAnim.OLD_NORMAL, "You can do that yourself! Just put some swamp tar in the lamp oil still here and then use the lamp on the still.").also { stage = END_DIALOGUE }
            5 -> npcl(FaceAnim.OLD_NORMAL, "They work by our very own Dorgeshuun magic!").also { stage = 7 }
            6 -> npcl(FaceAnim.OLD_NORMAL, "Sometimes they don't work very well, though. The light orbs blow and they have to be replaced.").also { stage++ }
            7 -> npcl(FaceAnim.OLD_NORMAL, "If you ever see a lamp that's gone out, you can fix it by replacing the light orb.").also { stage++ }
            8 -> npcl(FaceAnim.OLD_NORMAL, "You can make new light orbs out of glass, and use a piece of wire on them to add the filament.").also { stage++ }
            9 -> npcl(FaceAnim.OLD_NORMAL, "There's a machine in the south of the city that makes the wire you need.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = MiltogDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.MILTOG_5781)
}
