package content.global.skill.construction.decoration.costumeroom

import core.api.*
import core.api.allInInventory
import core.api.sendInterfaceConfig
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

private sealed class BoxPages(val displayId: Int) {
    class Normal(val item: ToyBox) : BoxPages(item.displayId)
    object More : BoxPages(ToyBox.More.displayId)
    object Back : BoxPages(ToyBox.Back.displayId)
}

private fun ToyBox.toToyboxItem(): BoxPages = when (this) {
    ToyBox.More -> BoxPages.More
    ToyBox.Back -> BoxPages.Back
    else -> BoxPages.Normal(this)
}

private fun <T> Array<T>.toLabelIconIds(getLabelId: (T) -> Int, getIconId: (T) -> Int): List<Int> =
    this.flatMap { listOf(getLabelId(it), getIconId(it)) }

class StorableBoxPlugin : InterfaceListener , InteractionListener {
    private val INTERFACE = 467

    private val bookcaseClosed = intArrayOf(Scenery.BOOKCASE_13597, Scenery.BOOKCASE_13598, Scenery.BOOKCASE_13599)
    private val capeRack = intArrayOf(Scenery.OAK_CAPE_RACK_18766, Scenery.TEAK_CAPE_RACK_18767, Scenery.MAHOGANY_CAPE_RACK_18768, Scenery.GILDED_CAPE_RACK_18769, Scenery.MARBLE_CAPE_RACK_18770, Scenery.MAGIC_CAPE_RACK_18771)
    private val fancyDressClosed = intArrayOf(Scenery.FANCY_DRESS_BOX_18772, Scenery.FANCY_DRESS_BOX_18774, Scenery.FANCY_DRESS_BOX_18776)
    private val fancyDressOpen = intArrayOf(Scenery.FANCY_DRESS_BOX_18773, Scenery.FANCY_DRESS_BOX_18775, Scenery.FANCY_DRESS_BOX_18777)
    private val toyBoxClosed = intArrayOf(Scenery.TOY_BOX_18798, Scenery.TOY_BOX_18800, Scenery.TOY_BOX_18802)
    private val toyBoxOpen = intArrayOf(Scenery.TOY_BOX_18799, Scenery.TOY_BOX_18801, Scenery.TOY_BOX_18803)
    private val toyboxItems = ToyBox.values().map { it.toToyboxItem() }

    override fun defineInterfaceListeners() {
        on(INTERFACE) { player, _, _, buttonID, _, _ ->

            val storages = arrayOf(
                StorageInterface(
                    attribute = "con:bookcase",
                    validIds = BookStorage.values().map { it.labelId },
                    handler = { id ->
                        val item = BookStorage.values().firstOrNull { it.labelId == id } ?: return@StorageInterface
                        handleStorage(
                            player, "bookcase:${item.ordinal}", intArrayOf(item.takeId),
                            "You take the item from the bookcase.", "You put the item into the bookcase."
                        )
                    }
                ),
                StorageInterface(
                    attribute = "con:fancy-dress-box",
                    validIds = FancyDressBox.values().toLabelIconIds({ it.labelId }, { it.iconId }),
                    handler = { id ->
                        val index = FancyDressBox.values().indexOfFirst { it.labelId == id || it.iconId == id }
                        if (index == -1) return@StorageInterface
                        val item = FancyDressBox.values()[index]
                        val set = if (item == FancyDressBox.RoyalFrogCostume) {
                            if (player.isMale) intArrayOf(Items.PRINCE_TUNIC_6184, Items.PRINCE_LEGGINGS_6185)
                            else intArrayOf(Items.PRINCESS_BLOUSE_6186, Items.PRINCESS_SKIRT_6187)
                        } else item.takeId
                        handleStorage(
                            player, "fancy-dress:$index", set,
                            "You take the outfit from the wardrobe.", "You put your outfit into the wardrobe."
                        )
                    }
                ),
                StorageInterface(
                    attribute = "con:cape-rack",
                    validIds = CapeRack.values().toLabelIconIds({ it.labelId }, { it.iconId }),
                    handler = { id ->
                        val index = CapeRack.values().indexOfFirst { it.labelId == id || it.iconId == id }
                        if (index == -1) return@StorageInterface
                        val item = CapeRack.values()[index]
                        handleStorage(
                            player, "cape:$index", item.takeId,
                            "You take the cape from the rack.", "You put the cape into the rack."
                        )
                    }
                ),
                StorageInterface(
                    attribute = "con:toy-box",
                    validIds = ToyBox.values().toLabelIconIds({ it.labelId }, { it.iconId }),
                    handler = { id -> handleToyBox(player, id) }
                )
            )

            storages.firstOrNull { getAttribute(player, it.attribute, false) }?.let { storage ->
                if (buttonID in storage.validIds) storage.handler(buttonID)
                else player.debug("Invalid button ID [$buttonID] for ${storage.attribute}")
            }
            return@on true
        }

        onClose(INTERFACE) { player, _ ->
            removeAttributes(player, "con:fancy-dress-box", "con:bookcase", "con:cape-rack", "con:toy-box", "con:toy-box:page")
            return@onClose true
        }
    }

    private fun handleStorage(player: Player, key: String, itemIds: IntArray, takeMsg: String, putMsg: String) {
        val storageKey = "/save:$key"
        if (getAttribute(player, storageKey, false)) {
            if (freeSlots(player) >= itemIds.size) {
                itemIds.forEach { addItem(player, it, 1) }
                removeAttribute(player, storageKey)
                sendMessage(player, takeMsg)
            } else {
                sendMessage(player, "You don't have enough inventory space.")
            }
        } else {
            if (allInInventory(player, *itemIds)) {
                itemIds.forEach { removeItem(player, Item(it, 1)) }
                setAttribute(player, storageKey, true)
                sendMessage(player, putMsg)
            } else {
                sendMessage(player, "That isn't currently stored.")
            }
        }
        handlePage(player)
    }

    private fun handleToyBox(player: Player, id: Int) {
        val page = getAttribute(player, "con:toy-box:page", 0)
        val itemsPerPage = 30
        val itemsOnPage = toyboxItems.drop(page * itemsPerPage).take(itemsPerPage)

        val item = itemsOnPage.firstOrNull { it.displayId == id || it is BoxPages.Normal && (it.item.labelId == id || it.item.iconId == id) }
            ?: return player.debug("Invalid toybox button: [$id]")

        when (item) {
            is BoxPages.More -> {
                setAttribute(player, "con:toy-box:page", page + 1)
                handlePage(player)
            }
            is BoxPages.Back -> {
                setAttribute(player, "con:toy-box:page", maxOf(0, page - 1))
                handlePage(player)
            }
            is BoxPages.Normal -> handleStorage(
                player,
                "toy-box:${item.item.ordinal}",
                item.item.takeId,
                "You take the item from the toy box.",
                "You put the item into the toy box."
            )
        }
    }

    private fun handlePage(player: Player) {
        val page = getAttribute(player, "con:toy-box:page", 0)
        val itemsPerPage = 30
        val offset = page * itemsPerPage
        ToyBox.values().forEachIndexed { index, item ->
            sendInterfaceConfig(player, INTERFACE, item.displayId, index in offset until offset + itemsPerPage)
        }
    }

    private data class StorageInterface(
        val attribute: String,
        val validIds: List<Int>,
        val handler: (Int) -> Unit
    )

    override fun defineListeners() {
        on(bookcaseClosed, IntType.SCENERY, "search") { player, _ ->
            animate(player, Animations.USE_OBJECT_POH_3659)
            setAttribute(player, "con:bookcase", true)
            openStorageInterface(player, "Bookcase", BookStorage.values(), itemToIds = { intArrayOf(it.takeId) })
            return@on true
        }
        on(capeRack, IntType.SCENERY, "search") { player, _ ->
            setAttribute(player, "con:cape-rack", true)
            openStorageInterface(player, "Cape rack", CapeRack.values(),
                itemToIds = { it.takeId },
                extraConfigs = { player, index, item ->
                    val key = "cape:$index"
                    val hidden = getAttribute(player, key, false)
                    sendInterfaceConfig(player, INTERFACE, item.labelId, hidden)
                    sendInterfaceConfig(player, INTERFACE, item.iconId + 1, hidden)
                }
            )
            return@on true
        }
        on(fancyDressClosed, IntType.SCENERY, "open") { player, node ->
            animate(player, Animations.OPEN_CHEST_536)
            replaceScenery(node.asScenery(), node.id + 1, -1)
            return@on true
        }
        on(fancyDressOpen, IntType.SCENERY, "search", "close") { player, node ->
            when (getUsedOption(player)) {
                "close" -> {
                    animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                    replaceScenery(node.asScenery(), node.id - 1, -1)
                }
                else -> {
                    setAttribute(player, "con:fancy-dress-box", true)
                    openStorageInterface(player, "Fancy dress box", FancyDressBox.values(),
                        itemToIds = { intArrayOf(it.displayId) },
                        extraConfigs = { player, index, item ->
                            val key = "set:$index"
                            val hidden = getAttribute(player, key, false)
                            sendInterfaceConfig(player, INTERFACE, item.labelId, hidden)
                            sendInterfaceConfig(player, INTERFACE, item.iconId + 1, hidden)
                        }
                    )
                }
            }
            return@on true
        }
        on(toyBoxClosed, IntType.SCENERY, "open") { player, node ->
            animate(player, Animations.OPEN_CHEST_536)
            replaceScenery(node.asScenery(), node.id + 1, -1)
            return@on true
        }
        on(toyBoxOpen, IntType.SCENERY, "search", "close") { player, node ->
            when (getUsedOption(player)) {
                "close" -> {
                    animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                    replaceScenery(node.asScenery(), node.id - 1, -1)
                }
                else -> {
                    setAttribute(player, "con:toy-box", true)
                    val visibleItems = ToyBox.values().filterNot { it == ToyBox.More || it == ToyBox.Back }
                    openStorageInterface(player, "Toy box", visibleItems.toTypedArray(),
                        itemToIds = { intArrayOf(it.displayId) },
                        extraConfigs = { player, index, item ->
                            val key = "toy-box:${item.name}"
                            val hidden = getAttribute(player, key, false)
                            sendInterfaceConfig(player, INTERFACE, item.iconId + 1, hidden)
                        }
                    )
                }
            }
            return@on true
        }
    }

    private fun <T> openStorageInterface(
        player: Player,
        title: String,
        items: Array<T>,
        itemToIds: (T) -> IntArray,
        extraConfigs: ((Player, Int, T) -> Unit)? = null
    ) {
        openInterface(player, INTERFACE)
        sendString(player, title, INTERFACE, 225)

        val contentItems = items.flatMap { itemToIds(it).map { id -> Item(id) } }.toTypedArray()

        PacketRepository.send(
            ContainerPacket::class.java,
            OutgoingContext.Container(player, INTERFACE, 164, 30, contentItems, false)
        )

        items.forEachIndexed { index, item ->
            val itemName = when (item) {
                is BookStorage -> getItemName(item.takeId)
                is CapeRack -> getItemName(item.displayId)
                is FancyDressBox -> getItemName(item.displayId)
                is ToyBox -> getItemName(item.displayId)
                else -> "Unknown"
            }
            sendString(player, itemName, INTERFACE, 55 + index * 2)
            extraConfigs?.invoke(player, index, item)
        }
    }

}