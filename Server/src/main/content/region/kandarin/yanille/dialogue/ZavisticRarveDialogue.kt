package content.region.kandarin.yanille.dialogue

import content.region.kandarin.feldip.jiggig.quest.zogre.dialogue.ZavisticRarveDialogueFile
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import shared.consts.NPCs

/**
 * Represents the Zavistic Rarve dialogue.
 */
@Initializable
class ZavisticRarveDialogue(player: Player? = null) : Dialogue(player) {
    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        openDialogue(player, ZavisticRarveDialogueFile(), npc)
        return false
    }
    override fun newInstance(player: Player?): Dialogue = ZavisticRarveDialogue(player)
    override fun getIds(): IntArray = intArrayOf(NPCs.ZAVISTIC_RARVE_2059)
}