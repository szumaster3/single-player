package content.global.skill.construction.decoration.costumeroom

import core.api.*
import core.api.allInInventory
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.net.packet.OutgoingContext
import core.net.packet.PacketRepository
import core.net.packet.out.ContainerPacket
import org.rs.consts.Items

class StorageBoxInterface : InterfaceListener {
    private val INTERFACE = 467
    private val CONTAINER_COMPONENT = 164
    private val TOTAL_SLOTS = 30
    private val BUTTON_MORE = Items.MORE_10165
    private val BUTTON_BACK = Items.BACK_10166

    override fun defineInterfaceListeners() {
        on(INTERFACE) { player, _, _, buttonID, _, _ ->
            val typeName = getAttribute(player, "con:storage:type", null) as? String ?: return@on true
            val type = CostumeRoomStorage.Type.valueOf(typeName.uppercase())
            handleStorageInteraction(player, buttonID, type)
            return@on true
        }
    }

    private fun getContainer(player: Player, type: CostumeRoomStorage.Type) =
        player.getCostumeRoomState().containers.getOrPut(type) { CostumeRoomContainer() }

    private fun handleStorageInteraction(player: Player, id: Int, type: CostumeRoomStorage.Type) {
        val container = getContainer(player, type)
        val allItemsOfType = CostumeRoomStorage.values().filter { it.type == type }
        val startIndex = container.currentPage * (TOTAL_SLOTS - 1)
        val itemsOnPage = allItemsOfType.drop(startIndex).take(TOTAL_SLOTS - 1)

        val pageItems = buildList<Any> {
            addAll(itemsOnPage)
            val hasNext = allItemsOfType.size > startIndex + (TOTAL_SLOTS - 1)
            val hasPrev = container.currentPage > 0
            if (hasNext) add("MORE")
            if (hasPrev) add("BACK")
        }

        if (id >= 56 && (id - 56) % 2 == 0) {
            val indexOnPage = (id - 56) / 2
            val clicked = pageItems.getOrNull(indexOnPage) ?: return

            when (clicked) {
                is String -> {
                    when (clicked) {
                        "MORE" -> {
                            container.nextPage()
                            openInterface(player, INTERFACE) // "refresh"
                            renderPage(player, type)
                        }
                        "BACK" -> {
                            container.prevPage()
                            openInterface(player, INTERFACE) // "refresh"
                            renderPage(player, type)
                        }
                    }
                }
                is CostumeRoomStorage -> {
                    val takeId = clicked.takeIds.firstOrNull() ?: clicked.displayId
                    val boxName = type.name.lowercase()

                    when {
                        container.hasItem(clicked) -> {
                            if (freeSlots(player) <= 0) {
                                sendMessage(player, "You don't have enough inventory space.")
                                return
                            }
                            sendMessage(player, "You take the item from the $boxName box.")
                            addItem(player, takeId, 1)
                            container.withdraw(clicked)
                        }
                        allInInventory(player, takeId) -> {
                            sendMessage(player, "You put the item into the $boxName box.")
                            removeItem(player, Item(takeId))
                            container.store(clicked)
                        }
                        else -> sendMessage(player, "That isn't currently stored in the $boxName box.")
                    }
                    renderPage(player, type)
                }
            }
        }
    }

    private fun renderPage(player: Player, type: CostumeRoomStorage.Type) {
        val container = getContainer(player, type)
        val allItemsOfType = CostumeRoomStorage.values().filter { it.type == type }
        val storedItems = container.storedItems.toSet()
        val startIndex = container.currentPage * (TOTAL_SLOTS - 1)
        val itemsOnPage = allItemsOfType.drop(startIndex).take(TOTAL_SLOTS - 1)

        val hasNext = allItemsOfType.size > startIndex + (TOTAL_SLOTS - 1)
        val hasPrev = container.currentPage > 0

        val pageItems = mutableListOf<Any>()
        pageItems.addAll(itemsOnPage)

        if (hasNext) pageItems.add("MORE")
        else if (hasPrev) pageItems.add("BACK")

        sendString(player, type.name.lowercase().replaceFirstChar(Char::titlecase) + " box", INTERFACE, 225)

        val itemsArray = Array<Item?>(TOTAL_SLOTS) { null }
        pageItems.forEachIndexed { index, obj ->
            itemsArray[index] = when (obj) {
                is CostumeRoomStorage -> Item(obj.displayId)
                "MORE" -> Item(BUTTON_MORE)
                "BACK" -> Item(BUTTON_BACK)
                else -> null
            }
        }

        PacketRepository.send(
            ContainerPacket::class.java,
            OutgoingContext.Container(
                player,
                INTERFACE,
                CONTAINER_COMPONENT,
                TOTAL_SLOTS,
                itemsArray.filterNotNull().toTypedArray(),
                false
            )
        )

        repeat(TOTAL_SLOTS) { index ->
            val nameComponent = 55 + index * 2
            val iconComponent = 56 + index * 2
            val obj = pageItems.getOrNull(index)

            if (obj == null) {
                sendString(player, "", INTERFACE, nameComponent)
                sendInterfaceConfig(player, INTERFACE, nameComponent, false)
                sendInterfaceConfig(player, INTERFACE, iconComponent, true)
            } else {
                val name = when (obj) {
                    is CostumeRoomStorage -> getItemName(obj.takeIds.firstOrNull() ?: obj.displayId)
                    "MORE" -> "More..."
                    "BACK" -> "Back..."
                    else -> ""
                }

                val hidden = when (obj) {
                    is CostumeRoomStorage -> obj in storedItems
                    else -> true
                }

                sendString(player, name, INTERFACE, nameComponent)
                sendInterfaceConfig(player, INTERFACE, nameComponent, false)
                sendInterfaceConfig(player, INTERFACE, iconComponent, hidden)
            }
        }
    }

    private fun openStorageInterface(player: Player, type: CostumeRoomStorage.Type) {
        setAttribute(player, "con:storage:type", type.name)
        openInterface(player, INTERFACE)
        renderPage(player, type)
    }

    companion object {
        lateinit var instance: StorageBoxInterface
            private set

        fun openStorage(player: Player, type: CostumeRoomStorage.Type) {
            instance.openStorageInterface(player, type)
        }
    }

    init {
        instance = this
    }
}
