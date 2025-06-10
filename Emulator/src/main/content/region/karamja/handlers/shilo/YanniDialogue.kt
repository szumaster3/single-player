package content.region.karamja.handlers.shilo

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.START_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs

/**
 * Represents Yanni dialogue extension for exchanging antique items.
 */
class YanniDialogue(val antique: Int) : DialogueFile() {

    private val item = YanniAntiqueItem.getAntiqueItem(antique)

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.YANNI_SALIKA_515)

        val itemName = getItemName(item!!.antique)

        val dialogue = when (item.antique) {
            Items.BEADS_OF_THE_DEAD_616 -> "Impressive necklace there."
            Items.BLACK_PRISM_4808 -> "Ah you'd like to sell this to me would you? I can offer you 5000 coins!"
            Items.BERVIRIUS_NOTES_624 -> "That's a great copy of Bervirius notes."
            else -> "That's a great $itemName."
        }

        when (stage) {
            START_DIALOGUE -> {
                npcl(FaceAnim.FRIENDLY, dialogue)
                stage++
            }
            1 -> {
                npcl(FaceAnim.FRIENDLY, "I'll give you ${item.price} coins for your $itemName...")
                stage++
            }
            2 -> {
                setTitle(player!!, 2)
                sendDialogueOptions(player!!, "Sell the $itemName?", "Yes.", "No.")
                stage++
            }
            3 -> {
                if (buttonID != 1) {
                    end()
                    return
                }
                end()
                if (removeItem(player!!, item.antique)) {
                    npc("Here's ${item.price} for it.")
                    sendMessage(player!!, "You sell the $itemName for ${item.price} gold.")
                    addItemOrDrop(player!!, Items.COINS_995, item.price)
                } else {
                    sendMessage(player!!, "You don't have the $itemName.")
                }
            }
        }
    }
}