package content.region.morytania.handlers.taxidermist

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.splitLines
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.npc.NPC
import core.game.node.item.Item
import org.rs.consts.Items
import org.rs.consts.NPCs

class TaxidermistListener : InteractionListener {
    override fun defineListeners() {
        /*
         * Handles stuffing the remains of specific items.
         * https://runescape.fandom.com/wiki/Taxidermist
         */

        onUseWith(IntType.NPC, ITEM_IDS, NPCs.TAXIDERMIST_4246) { player, used, _ ->
            val stuffed = StuffedItem.product[used.id] ?: return@onUseWith true
            openDialogue(player, object : DialogueFile() {
                    override fun handle(componentID: Int, buttonID: Int) {
                        npc = NPC(NPCs.TAXIDERMIST_4246)
                        when (stage) {
                        0 -> {
                            npc(*splitLines(stuffed.message))
                            stage++
                        }
                        1 -> {
                            npc("I can preserve that for you for ${stuffed.price} coins.")
                            stage++
                        }
                        2 -> {
                            options("Yes please.", "No thanks.")
                            stage++
                        }
                        3 -> {
                            when (buttonID) {
                                1 -> {
                                    end()

                                    if (stuffed.dropId != used.id) {
                                        npc("Don't be silly, I can't preserve that!")
                                        return
                                    }

                                    if (!removeItem(player, Item(Items.COINS_995, stuffed.price))) {
                                        sendDialogue(player, "You don't have enough coins in order to do that.")
                                        return
                                    }

                                    replaceSlot(player, used.index, Item(stuffed.stuffedId, 1))
                                    npc("There you go!")
                                }

                                2 -> {
                                    end()
                                    npc("All right, come back if you change your mind, eh?")
                                }
                            }
                        }
                    }
                }
            })

            return@onUseWith true
        }
    }

    companion object {
        private val ITEM_IDS = StuffedItem.values.map { it.dropId }.toIntArray()
    }
}
