package content.global.skill.hunter.imp

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.item.Item
import core.net.packet.PacketRepository
import core.net.packet.context.ContainerContext
import core.net.packet.out.ContainerPacket
import shared.consts.Components
import shared.consts.Items

/**
 * Handles imp box related interactions.
 */
class ImpBoxPlugin : InteractionListener, InterfaceListener {

    override fun defineListeners() {
        on(IMP_BOX_ITEM_IDS, IntType.ITEM, "talk-to", "bank") { player, node ->
            val option = getUsedOption(player)

            when (option.lowercase()) {
                "bank" -> {
                    if (player.interfaceManager.hasChatbox()) {
                        closeAllInterfaces(player)
                        openInterface(player, Components.IMP_BOX_478)
                    }
                }

                "talk-to" -> {
                    if (node.id == Items.IMP_IN_A_BOX1_10028) {
                        openDialogue(player, ImpDialogue())
                    } else {
                        openDialogue(player, ImpDialogueExtension())
                    }
                }

            }
            return@on true
        }
    }

    override fun defineInterfaceListeners() {

        /*
         * Handles opening the imp box.
         */
        onOpen(Components.IMP_BOX_478) { player, _ ->
            PacketRepository.send(
                ContainerPacket::class.java,
                ContainerContext(player, Components.IMP_BOX_478, 61, 91, player.inventory, true)
            )
            return@onOpen true
        }

        /*
         * Handles interaction with objects in the interface.
         */
        on(Components.IMP_BOX_478) { player, _, _, _, slot, _ ->
            val item = player.inventory.get(slot) ?: return@on true
            val boxSlot = player.inventory.getSlot(item)
            if (boxSlot < 0) return@on true

            if (player.bank.canAdd(item) && item.id !in IMP_BOX_ITEM_IDS) {
                player.dialogueInterpreter.close()
                player.inventory.remove(item)
                player.bank.add(item)

                when (item.id) {
                    IMP_BOX_ITEM_IDS[1] -> {
                        replaceSlot(player, boxSlot, Item(IMP_BOX_ITEM_IDS[0]))
                    }

                    IMP_BOX_ITEM_IDS[0] -> {
                        replaceSlot(player, boxSlot, Item(Items.MAGIC_BOX_10025))
                        closeInterface(player)
                    }
                }
            } else {
                sendMessage(player, "You cannot add this item to your bank.")
            }

            return@on true
        }
    }

    companion object {
        private val IMP_BOX_ITEM_IDS = intArrayOf(
            Items.IMP_IN_A_BOX1_10028,
            Items.IMP_IN_A_BOX2_10027
        )
    }
}
