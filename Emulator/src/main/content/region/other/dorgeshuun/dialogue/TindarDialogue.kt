package content.region.other.dorgeshuun.dialogue

import core.api.*
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Tindar dialogue for buying frogs' legs.
 */
@Initializable
class TindarDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            START_DIALOGUE -> npcl(FaceAnim.OLD_NORMAL, "Creeespy frogs' legs! Get your creeeespy frogs' legs! You want some crispy frogs' legs? Just 10gp.").also { stage = 1 }
            1 -> showTopics(
                Topic("Yes please.", 2, false),
                Topic("No thanks.", 3, false)
            )
            2 -> {
                end()
                if (freeSlots(player) == 0) {
                    npcl(FaceAnim.OLD_NORMAL, "Looks like your hands are full. You'll have to free up some inventory space before I sell you anything.")
                } else if (amountInInventory(player, Items.COINS_995) < 10) {
                    player(FaceAnim.NEUTRAL, "But I don't have enough money on me.")
                } else {
                    npcl(FaceAnim.OLD_NORMAL, "There you go.")
                    removeItem(player, Item(Items.COINS_995, 10), Container.INVENTORY)
                    addItem(player, Items.COATED_FROGS_LEGS_10963)
                }
            }
            3 -> npcl(FaceAnim.OLD_NORMAL, "Have a good day!").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = TindarDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.TINDAR_5795)
}