package content.region.misthalin.draynor.wizardstower.rc_guild.plugin

import core.api.*
import core.cache.def.impl.ItemDefinition
import core.game.dialogue.InputType
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import shared.consts.Components
import shared.consts.Items

class ElrissStorePlugin : InterfaceListener {

    override fun defineInterfaceListeners() {

        /*
         * Handles the opening of the RC Guild Rewards interface.
         */

        onOpen(Components.RCGUILD_REWARDS_779) { player, _ ->
            sendTokens(player)
            return@onOpen true
        }

        /*
         * Handles interaction with the RC Guild Rewards interface.
         */

        on(Components.RCGUILD_REWARDS_779) { player, _, opcode, button, _, _ ->
            val stock = ElrissStock.fromButton(button)
            when (opcode) {
                155 -> {
                    if (stock != null) {
                        val shopItem = stock.itemId
                        player.setAttribute("rc-selected-item", shopItem)
                        player.setAttribute("rc-selected-amount", 1)
                        selectObject(shopItem, 1, player)
                    }

                    if (button == 163) {
                        val selected = player.getAttribute<ShopItem>("rc-selected-item")
                        val amount = player.getAttribute<Int>("rc-selected-amount") ?: 1

                        if (selected != null) {
                            /* [Variant for buy as much as we can.]

                            val isStackable = ItemDefinition.forId(selected.id).isStackable()
                            val amt = if (isStackable) {
                                amount
                            } else {
                                val free = freeSlots(player)
                                if (free <= 0) {
                                    sendMessage(player, "You don't have enough space in your inventory.")
                                    return@on true
                                }
                                if (amount > free) free else amount
                            }
                            */

                            val isStackable = ItemDefinition.forId(selected.id).isStackable()
                            if (!isStackable && amount > freeSlots(player)) {
                                sendMessage(player, "You don't have enough space in your inventory.")
                                return@on true
                            }

                            handleBuyOption(selected, amount, player)
                            player.removeAttribute("rc-selected-item")
                            player.removeAttribute("rc-selected-amount")
                        } else {
                            sendMessage(
                                player,
                                "You must select something to buy before you can confirm your purchase."
                            )
                        }
                    }
                }

                196 -> {
                    if (stock != null) {
                        val shopItem = stock.itemId
                        sendInputDialogue(player, InputType.AMOUNT, "Enter the amount to buy:") { value ->
                            val amt = value.toString().toIntOrNull()
                            if (amt == null || amt <= 0) {
                                sendDialogue(player, "Please enter a valid amount greater than zero.")
                                return@sendInputDialogue
                            }

                            player.setAttribute("rc-selected-item", shopItem)
                            player.setAttribute("rc-selected-amount", amt)
                            selectObject(shopItem, amt, player)
                        }
                    }
                }
            }
            return@on true
        }

        onClose(Components.RCGUILD_REWARDS_779) { player, _ ->
            player.attributes.remove("rc-selected-item")
            player.attributes.remove("rc-selected-amount")
            return@onClose true
        }
    }

    private fun handleBuyOption(item: ShopItem, amount: Int, player: Player) {
        val neededTokens = Item(Items.RUNECRAFTING_GUILD_TOKEN_13650, item.price * amount)
        if (freeSlots(player) == 0) {
            sendMessage(player, "You don't have enough space in your inventory.")
            return
        }
        if (!player.inventory.containsItem(neededTokens)) {
            sendMessage(player, "You don't have enough tokens to purchase that.")
            return
        }

        sendMessage(player, "Your purchase has been added to your inventory.")
        player.inventory.remove(neededTokens)
        player.inventory.add(Item(item.id, amount))
        sendString(player, " ", Components.RCGUILD_REWARDS_779, 136)
        sendTokens(player)
    }


    private fun sendTokens(player: Player) {
        sendString(player, "Tokens: ${amountInInventory(player, Items.RUNECRAFTING_GUILD_TOKEN_13650)}", Components.RCGUILD_REWARDS_779, 135)
    }

    private fun selectObject(item: ShopItem, amount: Int, player: Player) {
        sendString(player, "${getItemName(item.id)}($amount)", Components.RCGUILD_REWARDS_779, 136)
    }
}

private enum class ElrissStock(val buttonId: Int, val itemId: ShopItem) {
    AIR_TALISMAN(		6,    ShopItem(Items.AIR_TALISMAN_1438,				50,		1)),
    MIND_TALISMAN(		13,   ShopItem(Items.MIND_TALISMAN_1448,			50,		1)),
    WATER_TALISMAN(		15,   ShopItem(Items.WATER_TALISMAN_1444,			50,		1)),
    EARTH_TALISMAN(		10,   ShopItem(Items.EARTH_TALISMAN_1440,			50,		1)),
    FIRE_TALISMAN(		11,   ShopItem(Items.FIRE_TALISMAN_1442,			50,		1)),
    BODY_TALISMAN(		7,    ShopItem(Items.BODY_TALISMAN_1446,			50,		1)),
    COSMIC_TALISMAN(	9,    ShopItem(Items.COSMIC_TALISMAN_1454,			125,	1)),
    CHAOS_TALISMAN(		8,    ShopItem(Items.CHAOS_TALISMAN_1452,			125,	1)),
    NATURE_TALISMAN(	14,   ShopItem(Items.NATURE_TALISMAN_1462,			125,	1)),
    LAW_TALISMAN(		12,   ShopItem(Items.LAW_TALISMAN_1458,				125,	1)),
    BLUE_RC_HAT(		36,   ShopItem(Items.RUNECRAFTER_HAT_13626,			1000,	1)),
    YELLOW_RC_HAT(		37,   ShopItem(Items.RUNECRAFTER_HAT_13616,			1000,	1)),
    GREEN_RC_HAT(		38,   ShopItem(Items.RUNECRAFTER_HAT_13621,			1000,	1)),
    BLUE_RC_ROBE(		39,   ShopItem(Items.RUNECRAFTER_ROBE_13624,		1000,	1)),
    YELLOW_RC_ROBE(		40,   ShopItem(Items.RUNECRAFTER_ROBE_13614,		1000,	1)),
    GREEN_RC_ROBE(		41,   ShopItem(Items.RUNECRAFTER_ROBE_13619,		1000,	1)),
    BLUE_RC_BOTTOM(		42,   ShopItem(Items.RUNECRAFTER_SKIRT_13627,		1000,	1)),
    YELLOW_RC_BOTTOM(	43,   ShopItem(Items.RUNECRAFTER_SKIRT_13617,		1000,	1)),
    GREEN_RC_BOTTOM(	44,   ShopItem(Items.RUNECRAFTER_SKIRT_13622,		1000,	1)),
    BLUE_RC_GLOVES(		45,   ShopItem(Items.RUNECRAFTER_GLOVES_13628,		1000,	1)),
    YELLOW_RC_GLOVES(	46,   ShopItem(Items.RUNECRAFTER_GLOVES_13618,		1000,	1)),
    GREEN_RC_GLOVES(	47,   ShopItem(Items.RUNECRAFTER_GLOVES_13623,		1000,	1)),
    RC_STAFF(			114,  ShopItem(Items.RUNECRAFTING_STAFF_13629,		10000,	1)),
    PURE_ESSENCE(		115,  ShopItem(Items.PURE_ESSENCE_7937,				100,	1)),
    AIR_TABLET( 		72,   ShopItem(Items.AIR_ALTAR_TP_13599,			30,		1)),
    MIND_TABLET(		80,   ShopItem(Items.MIND_ALTAR_TP_13600,			32,		1)),
    WATER_TABLET(		83,   ShopItem(Items.WATER_ALTAR_TP_13601,			34,		1)),
    EARTH_TABLET(		77,   ShopItem(Items.EARTH_ALTAR_TP_13602,			36,		1)),
    FIRE_TABLET(		78,   ShopItem(Items.FIRE_ALTAR_TP_13603,			37,		1)),
    BODY_TABLET(		73,   ShopItem(Items.BODY_ALTAR_TP_13604,			38,		1)),
    COSMIC_TABLET(		75,   ShopItem(Items.COSMIC_ALTAR_TP_13605,			39,		1)),
    CHAOS_TABLET(		74,   ShopItem(Items.CHAOS_ALTAR_TP_13606,			40,		1)),
    ASTRAL_TABLET(		81,   ShopItem(Items.ASTRAL_ALTAR_TP_13611,			41,		1)),
    NATURE_TABLET(		82,   ShopItem(Items.NATURE_ALTAR_TP_13607,			42,		1)),
    LAW_TABLET(			79,   ShopItem(Items.LAW_ALTAR_TP_13608,			43,		1)),
    DEATH_TABLET(		76,   ShopItem(Items.DEATH_ALTAR_TP_13609,			44,		1)),
    BLOOD_TABLET(		84,   ShopItem(Items.BLOOD_ALTAR_TP_13610,			45,		1)),
    GUILD_TABLET(		85,   ShopItem(Items.RUNECRAFTING_GUILD_TP_13598,	15,		1));


    companion object {
        private val byButton = values().associateBy { it.buttonId }
        fun fromButton(id: Int): ElrissStock? = byButton[id]
    }
}

private data class ShopItem(val id: Int, val price: Int, val amount: Int)