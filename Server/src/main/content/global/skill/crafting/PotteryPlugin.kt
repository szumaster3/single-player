package content.global.skill.crafting

import core.api.*
import core.game.interaction.*
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.map.Location
import core.tools.StringUtils
import shared.consts.Animations
import shared.consts.Items
import shared.consts.Quests
import shared.consts.Scenery
import java.util.*

private const val SOFT_CLAY = Items.SOFT_CLAY_1761
private val UNFIRED_POTTERY_ID = intArrayOf(
    Items.UNFIRED_POT_1787,
    Items.UNFIRED_PIE_DISH_1789,
    Items.UNFIRED_BOWL_1791,
    Items.UNFIRED_PLANT_POT_5352,
    Items.UNFIRED_POT_LID_4438
)

class PotteryPlugin : InteractionListener {

    /**
     * Represents the different pottery crafting.
     */
    enum class Pottery(val unfinished: Item, val product: Item, val level: Int, val exp: Double, val fireExp: Double) {
        POT(Item(Items.UNFIRED_POT_1787), Item(Items.EMPTY_POT_1931), 1, 6.3, 6.3),
        DISH(Item(Items.UNFIRED_PIE_DISH_1789), Item(Items.PIE_DISH_2313), 7, 15.0, 10.0),
        BOWL(Item(Items.UNFIRED_BOWL_1791), Item(Items.BOWL_1923), 8, 18.0, 15.0),
        PLANT(Item(Items.UNFIRED_PLANT_POT_5352), Item(Items.PLANT_POT_5350), 19, 20.0, 17.5),
        LID(Item(Items.UNFIRED_POT_LID_4438), Item(Items.POT_LID_4440), 25, 20.0, 20.0),
        ;

        companion object {
            private val unfinishedMap: Map<Int, Pottery> = values().associateBy { it.unfinished.id }
            fun forId(id: Int): Pottery? = unfinishedMap[id]
        }
    }

    override fun defineListeners() {

        /*
         * Handles crafting on pottery wheel.
         */

        onUseWith(IntType.ITEM, SOFT_CLAY, *CraftingObject.POTTERY_WHEELS) { player, _, _ ->
            val items = Pottery.values().map { it.unfinished }.toTypedArray()
            sendSkillDialogue(player) {
                withItems(*items)
                create { itemId, amount ->
                    val pottery = Pottery.forId(itemId) ?: run {
                        sendMessage(player, "Invalid pottery selection.")
                        return@create
                    }

                    if (getStatLevel(player, Skills.CRAFTING) < pottery.level) {
                        sendMessage(player, "You need a crafting level of ${pottery.level} to make this.")
                        return@create
                    }
                    if (!inInventory(player, SOFT_CLAY)) {
                        sendMessage(player, "You have run out of clay.")
                        return@create
                    }

                    var remaining = amount
                    queueScript(player, 0, QueueStrength.WEAK) { stage ->
                        if (remaining <= 0) return@queueScript stopExecuting(player)

                        when (stage) {
                            0 -> {
                                animate(player, Animations.HUMAN_MAKE_PIZZA_883)
                                delayScript(player, 5)
                            }
                            else -> {
                                delayClock(player, Clocks.SKILLING, 5)
                                if (removeItem(player, SOFT_CLAY)) {
                                    addItem(player, pottery.unfinished.id)
                                    rewardXP(player, Skills.CRAFTING, pottery.exp)
                                    val article = if (StringUtils.isPlusN(pottery.unfinished.name)) "an" else "a"
                                    sendMessage(player, "You make the clay into $article ${pottery.unfinished.name.lowercase(Locale.getDefault())}.")

                                    // Varrock diary.
                                    when (pottery) {
                                        Pottery.BOWL -> if (withinDistance(player, Location(3086, 3410, 0))) {
                                            setAttribute(player, "/save:diary:varrock:spun-bowl", true)
                                        }
                                        Pottery.POT -> if (withinDistance(player, Location(3086, 3410, 0))) {
                                            finishDiaryTask(player, DiaryType.LUMBRIDGE, 0, 7)
                                        }
                                        else -> {}
                                    }
                                    remaining--
                                }

                                if (remaining > 0) {
                                    setCurrentScriptState(player, 0)
                                    delayScript(player, 5)
                                } else stopExecuting(player)
                            }
                        }
                    }
                }
                calculateMaxAmount { amountInInventory(player, SOFT_CLAY) }
            }
            return@onUseWith true
        }

        /*
         * Handles firing in ovens.
         */

        fun firePottery(player: Player, pottery: Pottery, oven: Node): Boolean {
            if (oven.id == Scenery.POTTERY_OVEN_4308 && !isQuestComplete(player, Quests.THE_FREMENNIK_TRIALS)) {
                sendMessage(player, "Only Fremenniks may use this ${oven.name.lowercase(Locale.getDefault())}.")
                return false
            }

            if (getStatLevel(player, Skills.CRAFTING) < pottery.level) {
                sendMessage(player, "You need a crafting level of ${pottery.level} to do this.")
                return false
            }
            if (!inInventory(player, pottery.unfinished.id)) {
                sendMessage(player, "You don't have any ${pottery.unfinished.name.lowercase(Locale.getDefault())} to fire.")
                return false
            }

            sendSkillDialogue(player) {
                withItems(pottery.unfinished)
                create { _, amount ->
                    var remaining = amount
                    queueScript(player, 0, QueueStrength.WEAK) { stage ->
                        if (remaining <= 0) return@queueScript stopExecuting(player)

                        when (stage) {
                            0 -> {
                                animate(player, Animations.HUMAN_FURNACE_SMELT_3243)
                                playAudio(player, 2588)
                                sendMessage(player, "You put ${pottery.unfinished.name.lowercase(Locale.getDefault())} in the oven.")
                                delayScript(player, 5)
                            }
                            1 -> {
                                sendMessage(player, "The ${pottery.product.name.lowercase(Locale.getDefault())} hardens in the oven.")
                                delayScript(player, 5)
                            }
                            else -> {
                                delayClock(player, Clocks.SKILLING, 5)
                                if (removeItem(player, pottery.unfinished)) {
                                    addItem(player, pottery.product.id)
                                    rewardXP(player, Skills.CRAFTING, pottery.fireExp)
                                    sendMessage(player, "You remove ${pottery.product.name.lowercase(Locale.getDefault())} from the oven.")

                                    // Varrock diary.
                                    when (pottery) {
                                        Pottery.BOWL -> if (withinDistance(player, Location(3085, 3408, 0)) &&
                                            getAttribute(player, "diary:varrock:spun-bowl", false)) {
                                            finishDiaryTask(player, DiaryType.VARROCK, 0, 9)
                                        }
                                        Pottery.POT -> if (withinDistance(player, Location(3085, 3408, 0))) {
                                            finishDiaryTask(player, DiaryType.LUMBRIDGE, 0, 8)
                                        }
                                        else -> {}
                                    }
                                    remaining--
                                }

                                if (remaining > 0) {
                                    setCurrentScriptState(player, 0)
                                    delayScript(player, 5)
                                } else stopExecuting(player)
                            }
                        }
                    }
                }
                calculateMaxAmount { player.inventory.getAmount(pottery.unfinished) }
            }
            return true
        }

        onUseWith(IntType.ITEM, UNFIRED_POTTERY_ID, *CraftingObject.POTTERY_OVENS) { player, used, oven ->
            val pottery = Pottery.forId(used.id) ?: return@onUseWith false
            firePottery(player, pottery, oven)
            return@onUseWith true
        }

        /*
         * Handles interaction with fire ovens.
         */

        on(CraftingObject.POTTERY_OVENS, IntType.SCENERY, "fire") { player, node ->
            sendSkillDialogue(player) {
                val potteryMap = mapOf(
                    Items.UNFIRED_POT_1787 to Pottery.POT,
                    Items.UNFIRED_PIE_DISH_1789 to Pottery.DISH,
                    Items.UNFIRED_BOWL_1791 to Pottery.BOWL,
                    Items.UNFIRED_PLANT_POT_5352 to Pottery.PLANT,
                    Items.UNFIRED_POT_LID_4438 to Pottery.LID
                )

                withItems(*potteryMap.keys.toIntArray())

                create { selectedItemId, _ ->
                    val pottery = potteryMap[selectedItemId]
                    if (pottery != null) {
                        firePottery(player, pottery, node)
                    }
                }

                calculateMaxAmount { selectedItemId ->
                    amountInInventory(player, selectedItemId)
                }
            }

            return@on true
        }

        /*
         * Handles interacting with the cooking range.
         */

        on(CraftingObject.RANGE, IntType.SCENERY, "fire") { player, node ->
            if (inInventory(player, Items.UNCOOKED_STEW_2001, 1)) {
                faceLocation(player, node.location)
                openDialogue(player, 43989, Items.UNCOOKED_STEW_2001, "stew")
            }

            return@on true
        }
    }

}
