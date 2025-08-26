package content.global.plugin.iface

import core.api.*
import core.game.container.access.InterfaceContainer
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.tools.colorize
import shared.consts.Components
import shared.consts.Items

private val POLAR_CAMO_TOP = Item(Items.POLAR_CAMO_TOP_10065)
private val POLAR_CAMO_LEGS = Item(Items.POLAR_CAMO_LEGS_10067)

private val WOOD_CAMO_TOP = Item(Items.WOOD_CAMO_TOP_10053)
private val WOOD_CAMO_LEGS = Item(Items.WOOD_CAMO_LEGS_10055)

private val JUNGLE_CAMO_TOP = Item(Items.JUNGLE_CAMO_TOP_10057)
private val JUNGLE_CAMO_LEGS = Item(Items.JUNGLE_CAMO_LEGS_10059)

private val DESERT_CAMO_TOP = Item(Items.DESERT_CAMO_TOP_10061)
private val DESERT_CAMO_LEGS = Item(Items.DESERT_CAMO_LEGS_10063)

private val LARUPIA_HAT = Item(Items.LARUPIA_HAT_10045)
private val LARUPIA_TOP = Item(Items.LARUPIA_TOP_10043)
private val LARUPIA_LEGS = Item(Items.LARUPIA_LEGS_10041)

private val GRAAHK_HEADDRESS = Item(Items.GRAAHK_HEADDRESS_10051)
private val GRAAHK_TOP = Item(Items.GRAAHK_TOP_10049)
private val GRAAHK_LEGS = Item(Items.GRAAHK_LEGS_10047)

private val KYATT_HAT = Item(Items.KYATT_HAT_10039)
private val KYATT_TOP = Item(Items.KYATT_TOP_10037)
private val KYATT_LEGS = Item(Items.KYATT_LEGS_10035)

private val GLOVES_OF_SILENCE = Item(Items.GLOVES_OF_SILENCE_10075)
private val SPOTTED_CAPE = Item(Items.SPOTTED_CAPE_10069)
private val SPOTTIER_CAPE = Item(Items.SPOTTIER_CAPE_10071)

private val OPTIONS = arrayOf("Buy 10", "Buy 5", "Buy 1", "Value")
private const val FUR_CLOTHING_COMPONENT_ID = Components.CUSTOM_FUR_CLOTHING_477

private val ITEMS = arrayOf(
    POLAR_CAMO_TOP, POLAR_CAMO_LEGS, null, WOOD_CAMO_TOP, WOOD_CAMO_LEGS, null, JUNGLE_CAMO_TOP, JUNGLE_CAMO_LEGS, null, DESERT_CAMO_TOP, DESERT_CAMO_LEGS,
    LARUPIA_HAT, LARUPIA_TOP, LARUPIA_LEGS, null, GRAAHK_HEADDRESS, GRAAHK_TOP, GRAAHK_LEGS, null, KYATT_HAT, KYATT_TOP, KYATT_LEGS,
    null, GLOVES_OF_SILENCE, null, null, null, SPOTTED_CAPE, null, null, null, SPOTTIER_CAPE
)

/**
 * Handles custom fur clothing interface.
 */
class FurClothingInterface : InterfaceListener {

    override fun defineInterfaceListeners() {
        onOpen(FUR_CLOTHING_COMPONENT_ID) { player, _ ->
            InterfaceContainer.generateItems(player, ITEMS, OPTIONS, FUR_CLOTHING_COMPONENT_ID, 26, 3, 11, 5000)
            val checkedFurs = arrayListOf<String>()
            for (clothing in FurClothing.values()) {
                if (checkedFurs.contains(clothing.textContent)) continue
                if (inInventory(player, clothing.type.id, clothing.type.amount)) {
                    sendString(
                        player,
                        colorize("%G${clothing.textContent}"),
                        FUR_CLOTHING_COMPONENT_ID,
                        clothing.textChildID
                    )
                    checkedFurs.add(clothing.textContent)
                }
            }
            return@onOpen true
        }

        on(FUR_CLOTHING_COMPONENT_ID) { player, _, opcode, _, slotID, _ ->
            val itemMap = mapOf(
                0  to FurClothing.POLAR_KEBBIT_TOP,
                1  to FurClothing.POLAR_KEBBIT_BOT,
                3  to FurClothing.COMMON_KEBBIT_TOP,
                4  to FurClothing.COMMON_KEBBIT_BOT,
                6  to FurClothing.FELDIP_WEASEL_TOP,
                7  to FurClothing.FELDIP_WEASEL_LEGS,
                9  to FurClothing.DESERT_DEVIL_TOP,
                10 to FurClothing.DESERT_DEVIL_LEGS,
                11 to FurClothing.LARUPIA_HEAD,
                12 to FurClothing.LARUPIA_CHEST,
                13 to FurClothing.LARUPIA_BOTTOM,
                15 to FurClothing.GRAAHK_HEAD,
                16 to FurClothing.GRAAHK_CHEST,
                17 to FurClothing.GRAAHK_BOTTOM,
                19 to FurClothing.KYATT_HEAD,
                20 to FurClothing.KYATT_CHEST,
                21 to FurClothing.KYATT_BOTTOM,
                23 to FurClothing.GLOVES_SILENCE,
                27 to FurClothing.SPOT_CAPE,
                31 to FurClothing.DASH_CAPE
            )
            val clothing = itemMap[slotID] ?: return@on true
            when (opcode) {
                196 -> buy(player, clothing, 1)
                124 -> buy(player, clothing, 5)
                199 -> buy(player, clothing, 10)
                155 -> sendMessage(
                    player,
                    "${clothing.product.name} requires ${clothing.type.amount} ${clothing.type.name.lowercase()} and costs ${clothing.price} coins."
                )
                9 -> sendMessage(player, itemDefinition(clothing.product.id).examine)
            }
            return@on true
        }
    }

    private fun buy(player: Player, fur: FurClothing, amount: Int) {
        val coins = Item(Items.COINS_995, fur.price * amount)
        val requiredFur = Item(fur.type.id, fur.type.amount * amount)

        if (!inInventory(player, requiredFur.id, requiredFur.amount)) {
            sendDialogue(player, "You don't have enough fur for that.")
            return
        }

        if (!inInventory(player, coins.id, coins.amount)) {
            sendDialogue(player, "You can't afford that.")
            return
        }

        removeItem(player, requiredFur, Container.INVENTORY)
        removeItem(player, coins, Container.INVENTORY)
        addItem(player, fur.product.id, amount)
    }

    /**
     * Represents the custom fur clothing data.
     */
    internal enum class FurClothing(val type: Item, val price: Int, val product: Item, val textChildID: Int, val textContent: String) {
        POLAR_KEBBIT_TOP(Item(Items.POLAR_KEBBIT_FUR_10117, 2), 20, POLAR_CAMO_TOP, 18, "Polar kebbit"),
        POLAR_KEBBIT_BOT(Item(Items.POLAR_KEBBIT_FUR_10117, 2), 20, POLAR_CAMO_LEGS, 18, "Polar kebbit"),
        COMMON_KEBBIT_TOP(Item(Items.COMMON_KEBBIT_FUR_10121, 2), 20, WOOD_CAMO_TOP, 19, "Common kebbit"),
        COMMON_KEBBIT_BOT(Item(Items.COMMON_KEBBIT_FUR_10121, 2), 20, WOOD_CAMO_LEGS, 19, "Common kebbit"),
        FELDIP_WEASEL_TOP(Item(Items.FELDIP_WEASEL_FUR_10119, 2), 20, JUNGLE_CAMO_TOP, 20, "Feldip weasel"),
        FELDIP_WEASEL_LEGS(Item(Items.FELDIP_WEASEL_FUR_10119, 2), 20, JUNGLE_CAMO_LEGS, 20, "Feldip weasel"),
        DESERT_DEVIL_TOP(Item(Items.DESERT_DEVIL_FUR_10123, 2), 20, DESERT_CAMO_TOP, 21, "Desert devil"),
        DESERT_DEVIL_LEGS(Item(Items.DESERT_DEVIL_FUR_10123, 2), 20, DESERT_CAMO_LEGS, 21, "Desert devil"),
        LARUPIA_HEAD(Item(Items.LARUPIA_FUR_10095), 500, LARUPIA_HAT, 15, "Larupia"),
        LARUPIA_CHEST(Item(Items.LARUPIA_FUR_10095), 100, LARUPIA_TOP, 15, "Larupia"),
        LARUPIA_BOTTOM(Item(Items.LARUPIA_FUR_10095), 100, LARUPIA_LEGS, 15, "Larupia"),
        GRAAHK_HEAD(Item(Items.GRAAHK_FUR_10099), 750, GRAAHK_HEADDRESS, 16, "Graahk"),
        GRAAHK_CHEST(Item(Items.GRAAHK_FUR_10099), 150, GRAAHK_TOP, 16, "Graahk"),
        GRAAHK_BOTTOM(Item(Items.GRAAHK_FUR_10099), 150, GRAAHK_LEGS, 16, "Graahk"),
        KYATT_HEAD(Item(Items.KYATT_FUR_10103), 1000, KYATT_HAT, 17, "Kyatt"),
        KYATT_CHEST(Item(Items.KYATT_FUR_10103), 200, KYATT_TOP, 17, "Kyatt"),
        KYATT_BOTTOM(Item(Items.KYATT_FUR_10103), 200, KYATT_LEGS, 17, "Kyatt"),
        GLOVES_SILENCE(Item(Items.DARK_KEBBIT_FUR_10115, 2), 600, GLOVES_OF_SILENCE, 22, "Dark kebbit"),
        SPOT_CAPE(Item(Items.SPOTTED_KEBBIT_FUR_10125, 2), 400, SPOTTED_CAPE, 23, "Spotted kebbit"),
        DASH_CAPE(Item(Items.DASHING_KEBBIT_FUR_10127, 2), 800, SPOTTIER_CAPE, 24, "Dashing kebbit")
    }
}
