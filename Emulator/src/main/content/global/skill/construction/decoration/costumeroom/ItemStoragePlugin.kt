package content.global.skill.construction.decoration.costumeroom

import content.global.skill.construction.decoration.costumeroom.data.Bookcase
import content.global.skill.construction.decoration.costumeroom.data.Cape
import content.global.skill.construction.decoration.costumeroom.data.FancyDress
import content.global.skill.construction.decoration.costumeroom.data.Toy
import core.api.*
import core.api.item.allInInventory
import core.api.ui.sendInterfaceConfig
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import org.rs.consts.Items

private sealed class ToyboxItem(val displayId: Int) {
    class Normal(val item: Toy) : ToyboxItem(item.displayId)
    object More : ToyboxItem(Toy.More.displayId)
    object Back : ToyboxItem(Toy.Back.displayId)
}

private fun Toy.toToyboxItem(): ToyboxItem = when (this) {
    Toy.More -> ToyboxItem.More
    Toy.Back -> ToyboxItem.Back
    else -> ToyboxItem.Normal(this)
}

class ItemStoragePlugin : InterfaceListener {

    private val toyboxItems: List<ToyboxItem> by lazy {
        Toy.values().map { it.toToyboxItem() }
    }

    override fun defineInterfaceListeners() {
        on(INTERFACE) { player, _, _, buttonID, _, _ ->
            val interfaces = listOf(
                StorageInterface("con:bookcase", Bookcase.values().map { it.labelId }) { labelId ->
                    Bookcase.values().firstOrNull { it.labelId == labelId }?.let {
                        addItem(player, it.takeId)
                    }
                },
                StorageInterface("con:fancy-dress-box", FancyDress.values().flatMap { listOf(it.labelId, it.iconId) }) { id ->
                    FancyDress.values().firstOrNull { it.labelId == id || it.iconId == id }?.let {
                        handleFancyDressBox(player, it.ordinal)
                    }
                },
                StorageInterface("con:cape-rack", Cape.values().flatMap { listOf(it.labelId, it.iconId) }) { id ->
                    Cape.values().firstOrNull { it.labelId == id || it.iconId == id }?.let {
                        handleCapeRack(player, it.ordinal)
                    }
                },
                StorageInterface("con:toybox", toyboxItems.map { it.displayId }) { id ->
                    handleToybox(player, id)
                }
            )

            interfaces.firstOrNull { getAttribute(player, it.attribute, false) }?.let { storage ->
                if (buttonID in storage.validIds) storage.handler(buttonID)
                else player.debug("Invalid button ID [$buttonID] for ${storage.attribute}")
            }
            true
        }

        onClose(INTERFACE) { player, _ ->
            removeAttributes(player, "con:fancy-dress-box", "con:bookcase", "con:cape-rack", "con:toybox", "con:toybox:page")
            true
        }
    }

    private fun handleToybox(player: Player, id: Int) {
        val page = getAttribute(player, "con:toybox:page", 0)
        val itemsPerPage = 30
        val itemsOnPage = toyboxItems.drop(page * itemsPerPage).take(itemsPerPage)

        val item = itemsOnPage.firstOrNull { it.displayId == id }
            ?: return player.debug("Invalid toybox button: [$id]")

        when (item) {
            is ToyboxItem.More -> {
                setAttribute(player, "con:toybox:page", page + 1)
                handlePage(player)
            }
            is ToyboxItem.Back -> {
                setAttribute(player, "con:toybox:page", maxOf(0, page - 1))
                handlePage(player)
            }
            is ToyboxItem.Normal -> handleToyItem(player, item.item)
        }
    }

    private fun handlePage(player: Player) {
        val page = getAttribute(player, "con:toybox:page", 0)
        val itemsPerPage = 30
        val offset = page * itemsPerPage
        Toy.values().forEachIndexed { index, item ->
            sendInterfaceConfig(player, INTERFACE, item.displayId, index in offset until offset + itemsPerPage)
        }
    }

    private fun handleToyItem(player: Player, item: Toy) {
        val key = "toybox:${item.name}"

        if (getAttribute(player, key, false)) {
            if (freeSlots(player) >= item.takeId.size) {
                item.takeId.forEach { addItem(player, it, 1) }
                removeAttribute(player, key)
                sendMessage(player, "You take the item from the toy box.")
                sendInterfaceConfig(player, INTERFACE, item.displayId, false)
            } else sendMessage(player, "You don't have enough inventory space.")
        } else {
            if (allInInventory(player, *item.takeId)) {
                item.takeId.forEach { removeItem(player, Item(it, 1)) }
                setAttribute(player, "/save:$key", true)
                sendMessage(player, "You put the item into the toy box.")
                sendInterfaceConfig(player, INTERFACE, item.displayId, true)
            } else sendMessage(player, "That isn't currently stored in the toy box.")
        }
        handlePage(player)
    }

    private fun handleFancyDressBox(player: Player, setIndex: Int) {
        val item = FancyDress.values().getOrNull(setIndex) ?: return player.debug("Invalid fancy dress index: [$setIndex]")
        val set = if (item == FancyDress.RoyalFrogCostume) {
            if (player.isMale) intArrayOf(Items.PRINCE_TUNIC_6184, Items.PRINCE_LEGGINGS_6185)
            else intArrayOf(Items.PRINCESS_BLOUSE_6186, Items.PRINCESS_SKIRT_6187)
        } else item.takeId
        val key = "set:$setIndex"

        if (getAttribute(player, key, false)) {
            if (freeSlots(player) >= set.size) {
                set.forEach { addItem(player, it, 1) }
                removeAttribute(player, key)
                sendMessage(player, "You take the outfit from the wardrobe.")
            } else sendMessage(player, "You don't have enough inventory space.")
        } else {
            if (allInInventory(player, *set)) {
                set.forEach { removeItem(player, Item(it, 1)) }
                setAttribute(player, "/save:$key", true)
                sendMessage(player, "You put your outfit into the wardrobe.")
            } else sendMessage(player, "That isn't currently stored in the fancy dress box.")
        }
        handlePage(player)
    }

    private fun handleCapeRack(player: Player, setIndex: Int) {
        val item = Cape.values().getOrNull(setIndex) ?: return player.debug("Invalid cape index: [$setIndex]")
        val key = "cape:$setIndex"

        if (getAttribute(player, key, false)) {
            if (freeSlots(player) >= 1) {
                addItem(player, item.takeId.first(), 1)
                removeAttribute(player, key)
                sendMessage(player, "You take the cape from the rack.")
            } else sendMessage(player, "You don't have enough inventory space.")
        } else {
            if (removeItem(player, item.takeId.first())) {
                setAttribute(player, key, true)
                sendMessage(player, "You put the cape into the rack.")
            } else sendMessage(player, "That isn't currently stored in the cape rack.")
        }
        handlePage(player)
    }

    private data class StorageInterface(
        val attribute: String,
        val validIds: List<Int>,
        val handler: (Int) -> Unit
    )

    companion object {
        private const val INTERFACE = 467
    }
}
