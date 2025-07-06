package content.global.skill.construction.decoration.costumeroom

import core.api.*
import core.api.allInInventory
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.net.packet.OutgoingContext
import core.net.packet.PacketRepository
import core.net.packet.out.ContainerPacket
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery

class StorableBoxPlugin : InterfaceListener, InteractionListener {

    private val INTERFACE = 467
    private val CONTAINER_COMPONENT = 164
    private val PAGE_SIZE = 28
    private val CONTAINER_SIZE = 30
    private val BUTTON_MORE = Items.MORE_10165
    private val BUTTON_BACK = Items.BACK_10166

    private val bookcaseClosed = intArrayOf(Scenery.BOOKCASE_13597, Scenery.BOOKCASE_13598, Scenery.BOOKCASE_13599)
    private val capeRack = intArrayOf(
        Scenery.OAK_CAPE_RACK_18766, Scenery.TEAK_CAPE_RACK_18767,
        Scenery.MAHOGANY_CAPE_RACK_18768, Scenery.GILDED_CAPE_RACK_18769,
        Scenery.MARBLE_CAPE_RACK_18770, Scenery.MAGIC_CAPE_RACK_18771
    )
    private val fancyDressClosed = intArrayOf(Scenery.FANCY_DRESS_BOX_18772, Scenery.FANCY_DRESS_BOX_18774, Scenery.FANCY_DRESS_BOX_18776)
    private val fancyDressOpen = intArrayOf(Scenery.FANCY_DRESS_BOX_18773, Scenery.FANCY_DRESS_BOX_18775, Scenery.FANCY_DRESS_BOX_18777)
    private val toyBoxClosed = intArrayOf(Scenery.TOY_BOX_18798, Scenery.TOY_BOX_18800, Scenery.TOY_BOX_18802)
    private val toyBoxOpen = intArrayOf(Scenery.TOY_BOX_18799, Scenery.TOY_BOX_18801, Scenery.TOY_BOX_18803)

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

        when (id) {
            BUTTON_MORE -> container.nextPage()
            BUTTON_BACK -> container.prevPage()
            else -> {
                if (id >= 56 && (id - 56) % 2 == 0) {
                    val indexOnPage = (id - 56) / 2
                    if (indexOnPage >= PAGE_SIZE) return
                    val globalIndex = container.currentPage * PAGE_SIZE + indexOnPage
                    val item = allItemsOfType.getOrNull(globalIndex) ?: return
                    val takeId = item.takeIds.firstOrNull() ?: item.displayId

                    when {
                        container.hasItem(item) && freeSlots(player) > 0 -> {
                            addItem(player, takeId, 1)
                            container.withdraw(item)
                        }
                        allInInventory(player, takeId) -> {
                            removeItem(player, Item(takeId))
                            container.store(item)
                        }
                        else -> sendMessage(player, "Item not stored or in inventory.")
                    }
                }
            }
        }
        showPage(player, type)
    }

    private fun showPage(player: Player, type: CostumeRoomStorage.Type) {
        val container = getContainer(player, type)
        val allItemsOfType = CostumeRoomStorage.values().filter { it.type == type }
        val startIndex = container.currentPage * PAGE_SIZE
        val pageItems = allItemsOfType.drop(startIndex).take(PAGE_SIZE)

        setAttribute(player, "con:storage:type", type.name)
        openInterface(player, INTERFACE)
        sendString(player, type.name.lowercase().replaceFirstChar(Char::titlecase), INTERFACE, 225)

        val items = Array(CONTAINER_SIZE) { idx ->
            when (idx) {
                in 0 until pageItems.size -> Item(pageItems[idx].displayId)
                CONTAINER_SIZE - 2 -> Item(BUTTON_MORE)
                CONTAINER_SIZE - 1 -> Item(BUTTON_BACK)
                else -> null
            }
        }.filterNotNull().toTypedArray()

        PacketRepository.send(
            ContainerPacket::class.java,
            OutgoingContext.Container(player, INTERFACE, CONTAINER_COMPONENT, CONTAINER_SIZE, items, false)
        )

        pageItems.forEachIndexed { index, item ->
            sendString(player, getItemName(item.takeIds.firstOrNull() ?: item.displayId), INTERFACE, 55 + index * 2)
            sendInterfaceConfig(player, INTERFACE, 55 + index * 2, false)
        }

        sendInterfaceConfig(player, INTERFACE, BUTTON_MORE, (container.currentPage + 1) * PAGE_SIZE >= allItemsOfType.size)
        sendInterfaceConfig(player, INTERFACE, BUTTON_BACK, container.currentPage == 0)
    }

    override fun defineListeners() {
        fun openStorage(player: Player, type: CostumeRoomStorage.Type) {
            setAttribute(player, "con:storage:type", type.name)
            showPage(player, type)
        }

        on(bookcaseClosed, IntType.SCENERY, "search") { player, _ ->
            openStorage(player, CostumeRoomStorage.Type.BOOK)
            return@on true
        }

        on(capeRack, IntType.SCENERY, "search") { player, _ ->
            openStorage(player, CostumeRoomStorage.Type.CAPE)
            return@on true
        }

        on(fancyDressClosed, IntType.SCENERY, "open") { player, node ->
            animate(player, Animations.OPEN_CHEST_536)
            replaceScenery(node.asScenery(), node.id + 1, -1)
            return@on true
        }

        on(fancyDressOpen, IntType.SCENERY, "search", "close") { player, node ->
            if (getUsedOption(player) == "close") {
                animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                replaceScenery(node.asScenery(), node.id - 1, -1)
            } else {
                openStorage(player, CostumeRoomStorage.Type.FANCY)
            }
            return@on true
        }

        on(toyBoxClosed, IntType.SCENERY, "open") { player, node ->
            animate(player, Animations.OPEN_CHEST_536)
            replaceScenery(node.asScenery(), node.id + 1, -1)
            return@on true
        }

        on(toyBoxOpen, IntType.SCENERY, "search", "close") { player, node ->
            if (getUsedOption(player) == "close") {
                animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                replaceScenery(node.asScenery(), node.id - 1, -1)
            } else {
                openStorage(player, CostumeRoomStorage.Type.TOY)
            }
            return@on true
        }
    }
}
