package content.region.kandarin.yanille.dialogue

import content.region.kandarin.feldip.jiggig.quest.zogre.dialogue.BartenderDialogueFile
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import shared.consts.NPCs

/**
 * Represents the Bartender dialogue (Yanille).
 */
@Initializable
class BartenderDialogue(player: Player? = null) : Dialogue(player) {
    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        openDialogue(player, BartenderDialogueFile(), npc)
        return false
    }

    override fun newInstance(player: Player?): Dialogue = BartenderDialogue(player)
    override fun getIds(): IntArray = intArrayOf(NPCs.BARTENDER_739)
}