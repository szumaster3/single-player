package content.global.dialogue

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
 * Represents the Shopkeeper dialogue.
 * @author Vexia
 */
@Initializable
class ShopkeeperDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> npc(FaceAnim.HALF_ASKING, "Can I help you at all?").also { stage++ }
            1 -> showTopics(
                Topic(FaceAnim.HALF_ASKING,"Yes please, what are you selling?", 3),
                Topic("How should I use your shop?", 2),
                Topic("No, thanks.", END_DIALOGUE)
            )
            2 -> npc(FaceAnim.HAPPY, "I'm glad you ask! You can buy as many of the items", "stocked as you wish. You can also sell most items to", "the shop.").also { stage = 1 }
            3 -> {
                end()
                openNpcShop(player, npc.id)
            }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = ShopkeeperDialogue(player)

    override fun getIds(): IntArray = intArrayOf(
        NPCs.SHOPKEEPER_520, NPCs.SHOP_ASSISTANT_521, // Lumbridge
        NPCs.SHOPKEEPER_522, NPCs.SHOP_ASSISTANT_523, // Varrock
        NPCs.SHOPKEEPER_524, NPCs.SHOP_ASSISTANT_525, // Al Kharid
        NPCs.SHOPKEEPER_526, NPCs.SHOP_ASSISTANT_527, // Falador
        NPCs.SHOPKEEPER_528, NPCs.SHOP_ASSISTANT_529, // Edgeville
        NPCs.SHOPKEEPER_530, NPCs.SHOP_ASSISTANT_531, // Rimmington
        NPCs.SHOPKEEPER_532, NPCs.SHOP_ASSISTANT_533, // Karamja
        NPCs.SHOPKEEPER_555 // Port Khazard
    )
}
