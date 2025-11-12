package content.global.activity.creation

import core.api.*
import core.game.global.action.DropListener
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import shared.consts.Items

class SatchelPlugin : InteractionListener {

    companion object {
        const val BASE_CHARGE_AMOUNT = 1000

        private val FOOD_ITEMS = listOf(
            Items.CAKE_1891,
            Items.BANANA_1963,
            Items.TRIANGLE_SANDWICH_6962
        )

        val SATCHEL_IDS = intArrayOf(
            Items.PLAIN_SATCHEL_10877,
            Items.GREEN_SATCHEL_10878,
            Items.RED_SATCHEL_10879,
            Items.BLACK_SATCHEL_10880,
            Items.GOLD_SATCHEL_10881,
            Items.RUNE_SATCHEL_10882
        )

        fun getMaskFor(foodId: Int): Int {
            val index = FOOD_ITEMS.indexOf(foodId)
            if (index == -1) return 0
            return 1 shl index
        }

        fun getItemsFromMask(mask: Int): List<Int> {
            val items = mutableListOf<Int>()
            FOOD_ITEMS.forEachIndexed { index, id ->
                if ((mask and (1 shl index)) != 0) items.add(id)
            }
            return items
        }

        private fun getMaskFromItems(items: Collection<Int>): Int {
            var mask = 0
            items.forEach { mask = mask or getMaskFor(it) }
            return mask
        }
    }

    override fun defineListeners() {

        /*
         * Handles adding food to satchel.
         */

        onUseWith(IntType.ITEM, FOOD_ITEMS.toIntArray(), *SATCHEL_IDS) { player, used, with ->
            add(player, used.asItem(), with.asItem())
            return@onUseWith true
        }

        /*
         * Handles satchel interaction options.
         */

        on(SATCHEL_IDS, IntType.ITEM, "inspect", "empty", "drop") { player, node ->
            val item = node.asItem()
            when (getUsedOption(player)) {
                "inspect" -> inspect(player, item)
                "empty" -> empty(player, item)
                "drop" -> drop(player, item)
            }
            return@on true
        }
    }

    private fun add(player: Player, food: Item, satchel: Item) {
        val currentCharge = getCharge(satchel)
        val currentMask = currentCharge - BASE_CHARGE_AMOUNT
        val currentItems = getItemsFromMask(currentMask).toMutableSet()

        if (currentItems.contains(food.id)) {
            sendMessage(player, "You already have a ${getItemName(food.id).lowercase().removePrefix("triangle ").trim()} in there.")
            return
        }

        if (currentItems.size >= 3) {
            sendMessage(player, "Your satchel is already full.")
            return
        }

        currentItems.add(food.id)
        val newMask = getMaskFromItems(currentItems)
        setCharge(satchel, BASE_CHARGE_AMOUNT + newMask)

        replaceSlot(player, food.slot, Item())
        sendMessage(player, "You add a ${getItemName(food.id).lowercase().removePrefix("triangle ").trim()} to the satchel.")
    }

    // TODO: Right now, the satchel uses bitmasks, so the items always show in a fixed order.
    //  The items should stay in the order the player added them.
    private fun inspect(player: Player, item: Item) {
        val mask = getCharge(item) - BASE_CHARGE_AMOUNT
        val contents = getItemsFromMask(mask)
        val names = contents.map { getItemName(it).lowercase().removePrefix("triangle ").trim() }

        val message = when (names.size) {
            0 -> "Empty!"
            1 -> "one ${names[0]}"
            2 -> names.joinToString(", ") { "one $it" }
            3 -> "${names.take(2).joinToString(", ") { "one $it" }} and one <br>${names[2]}"
            else -> return
        }

        player.dialogueInterpreter.sendItemMessage(item.id, "The ${getItemName(item.id)}!", "(Containing: $message)")
    }

    private fun empty(player: Player, item: Item) {
        val mask = getCharge(item) - BASE_CHARGE_AMOUNT
        val contents = getItemsFromMask(mask)

        if (contents.isEmpty()) {
            sendMessage(player, "It's already empty.")
            return
        }

        if (freeSlots(player) < contents.size) {
            sendMessage(player, "You don't have enough inventory space.")
            return
        }

        contents.forEach { addItem(player, it, 1) }
        setCharge(item, BASE_CHARGE_AMOUNT)
        sendMessage(player, "You empty the contents of the satchel.")
    }

    private fun drop(player: Player, satchel: Item) {
        val mask = getCharge(satchel) - BASE_CHARGE_AMOUNT
        val contents = getItemsFromMask(mask)
        contents.forEach { DropListener.drop(player, Item(it)) }

        setCharge(satchel, BASE_CHARGE_AMOUNT)
        DropListener.drop(player, satchel)
        sendMessage(player, "The contents of the satchel fell out as you dropped it!")
    }
}
