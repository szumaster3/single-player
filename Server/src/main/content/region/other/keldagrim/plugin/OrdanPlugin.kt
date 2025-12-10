package content.region.other.keldagrim.plugin

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

class OrdanPlugin : InteractionListener {

    companion object {
        val ITEM_PRICE_MAP = hashMapOf(
            Items.IRON_ORE_441 to 8,
            Items.COPPER_ORE_437 to 10,
            Items.TIN_ORE_439 to 10,
            Items.COAL_454 to 22,
            Items.MITHRIL_ORE_448 to 81,
            Items.ADAMANTITE_ORE_450 to 330,
            Items.SILVER_ORE_443 to 37,
            Items.GOLD_ORE_445 to 75,
            Items.RUNITE_ORE_452 to 1000,
        )
    }

    override fun defineListeners() {
        onUseWith(IntType.NPC, ITEM_PRICE_MAP.keys.toIntArray(), NPCs.ORDAN_2564) { player, noteType, npc ->
            openDialogue(player, OrdanUnnoteDialogue(noteType.id, noteType.name), npc.asNpc())
            return@onUseWith true
        }
    }

    private class OrdanUnnoteDialogue(private val noteTypeId: Int, private val noteTypeName: String) : DialogueFile()
    {

        private var unnoteAmount = 0
        private var unnotePrice = 0

        override fun handle(componentID: Int, buttonID: Int)
        {
            when (stage) {
                START_DIALOGUE -> {
                    setTitle(player!!, 4)
                    sendOptions(player!!, "How much $noteTypeName would you like to un-note?", "1", "5", "10", "X")
                    stage++
                }

                1 -> when (buttonID) {
                    1 -> {
                        unnoteAmount = 1
                        unnotePrice = 1 * ITEM_PRICE_MAP[noteTypeId]!!
                        npcl(FaceAnim.OLD_HAPPY, "I can un-note those for $unnotePrice gold pieces, is that okay?").also { stage++ }
                    }

                    2 -> {
                        unnoteAmount = 5
                        unnotePrice = 5 * ITEM_PRICE_MAP[noteTypeId]!!
                        npcl(FaceAnim.OLD_HAPPY, "I can un-note those for $unnotePrice gold pieces, is that okay?").also { stage++ }
                    }

                    3 -> {
                        unnoteAmount = 10
                        unnotePrice = 10 * ITEM_PRICE_MAP[noteTypeId]!!
                        npcl(FaceAnim.OLD_HAPPY, "I can un-note those for $unnotePrice gold pieces, is that okay?").also { stage++ }
                    }

                    4 -> {
                        sendInputDialogue(player!!, true, "Enter amount:") { value ->
                            unnoteAmount = (value as Int).coerceAtMost(freeSlots(player!!))
                            unnotePrice = unnoteAmount * ITEM_PRICE_MAP[noteTypeId]!!

                            npcl(FaceAnim.OLD_HAPPY, "I can un-note $unnoteAmount $noteTypeName for $unnotePrice gold pieces, is that okay?")
                            stage = 2
                        }
                    }
                }
                2 -> showTopics(
                    Topic(FaceAnim.HAPPY, "It's a deal.", 3),
                    Topic("No, that's too expensive.", END_DIALOGUE)
                )
                3 -> {
                    end()
                    if (freeSlots(player!!) < unnoteAmount)
                    {
                        npc(FaceAnim.OLD_NORMAL, "You don't have enough room in your inventory for that", "number of un-noted items. Clear some space and try", "again.")
                    }
                    else if (amountInInventory(player!!, noteTypeId) < unnoteAmount)
                    {
                        sendDialogueLines(player!!, "You do not have enough notes to un-note", "$unnoteAmount $noteTypeName.")
                    }
                    else if (amountInInventory(player!!, Items.COINS_995) < unnotePrice)
                    {
                        sendDialogueLines(player!!, "You do not have enough coins to afford un-noting", "$unnoteAmount $noteTypeName.")
                    }
                    else
                    {
                        if (removeItem(player!!, Item(Items.COINS_995, unnotePrice), Container.INVENTORY) && removeItem(player!!, Item(noteTypeId, unnoteAmount), Container.INVENTORY))
                        {
                            addItem(player!!, noteTypeId - 1, unnoteAmount)
                        }
                    }

                    stage = END_DIALOGUE
                }
            }
        }
    }
}