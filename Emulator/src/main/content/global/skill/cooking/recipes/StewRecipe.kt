package content.global.skill.cooking.recipes

import core.api.*
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import org.rs.consts.Items
import kotlin.math.min

class StewRecipe : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles creating incomplete stews.
         */

        onUseWith(IntType.ITEM, BOWL_OF_WATER, *stewIngredients) { player, used, ingredient ->
            if (!hasLevelDyn(player, Skills.COOKING, 25)) {
                sendMessage(player, "You need a Cooking level of 25 to make that.")
                return@onUseWith true
            }

            val stew = when (ingredient.id) {
                POTATO -> POTATO_STEW
                COOKED_MEAT -> MEAT_STEW
                else -> return@onUseWith true
            }

            fun makeIncompleteStew(): Boolean {
                if (removeItem(player, Item(used.id, 1), Container.INVENTORY) &&
                    removeItem(player, Item(ingredient.id, 1), Container.INVENTORY)
                ) {
                    addItem(player, stew, 1, Container.INVENTORY)
                    val base = ingredient.name.lowercase()
                    sendMessage(player, "You cut up the $base and put it into the bowl.")
                    return true
                }
                return false
            }

            val amountUsed = amountInInventory(player, used.id)
            val amountWith = amountInInventory(player, ingredient.id)

            if (amountUsed == 1 || amountWith == 1) {
                return@onUseWith makeIncompleteStew()
            }

            sendSkillDialogue(player) {
                withItems(stew)
                create { _, amount ->
                    runTask(player, 2, amount) {
                        if (amount > 0) makeIncompleteStew()
                    }
                }
                calculateMaxAmount {
                    min(amountWith, amountUsed)
                }
            }

            return@onUseWith true
        }

        /*
         * Handles creating an uncooked stew.
         */

        onUseWith(IntType.ITEM, incompleteStew, *stewIngredients) { player, used, ingredient ->
            if (!hasLevelDyn(player, Skills.COOKING, 25)) {
                sendMessage(player, "You need a Cooking level of 25 to make that.")
                return@onUseWith true
            }

            fun makeUncookedStew(): Boolean {
                if (removeItem(player, Item(used.id, 1), Container.INVENTORY) &&
                    removeItem(player, Item(ingredient.id, 1), Container.INVENTORY)
                ) {
                    addItem(player, UNCOOKED_STEW, 1, Container.INVENTORY)
                    val base = ingredient.name.lowercase().replace("cooked", "").trim()
                    sendMessage(player, "You cut up the $base and put it into the stew.")
                    return true
                }
                return false
            }

            val amountUsed = amountInInventory(player, used.id)
            val amountWith = amountInInventory(player, ingredient.id)

            if (amountUsed == 1 || amountWith == 1) {
                return@onUseWith makeUncookedStew()
            }

            sendSkillDialogue(player) {
                withItems(UNCOOKED_STEW)
                create { _, amount ->
                    runTask(player, 2, amount) {
                        if (amount > 0) makeUncookedStew()
                    }
                }
                calculateMaxAmount {
                    min(amountWith, amountUsed)
                }
            }

            return@onUseWith true
        }

        /*
         * Handles creating an uncooked curry using spice on stew.
         */

        onUseWith(IntType.ITEM, SPICE, UNCOOKED_STEW) { player, used, with ->
            if (!hasLevelDyn(player, Skills.COOKING, 60)) {
                sendMessage(player, "You need a Cooking level of 60 to make that.")
                return@onUseWith true
            }

            if (amountInInventory(player, used.id) == 1 || amountInInventory(player, with.id) == 1) {
                if (removeItem(player, Item(used.id, 1), Container.INVENTORY) && removeItem(player, Item(with.id, 1), Container.INVENTORY)) {
                    addItem(player, UNCOOKED_CURRY, 1, Container.INVENTORY)
                    sendMessage(player, "You mix the spice with the stew.")
                }
                return@onUseWith true
            }

            sendSkillDialogue(player) {
                withItems(UNCOOKED_CURRY)
                create { _, amount ->
                    runTask(player, 2, amount) {
                        if (amount < 1) return@runTask
                        if (removeItem(player, Item(used.id, 1), Container.INVENTORY) && removeItem(
                                player,
                                Item(with.id, 1),
                                Container.INVENTORY
                            )
                        ) {
                            addItem(player, UNCOOKED_CURRY, 1, Container.INVENTORY)
                            sendMessage(player, "You mix the spice with the stew.")
                        }
                    }
                }

                calculateMaxAmount { _ ->
                    min(amountInInventory(player, with.id), amountInInventory(player, used.id))
                }
            }

            return@onUseWith true
        }

        /*
         * Handles creating an uncooked curry using curry leaves.
         */

        onUseWith(IntType.ITEM, UNCOOKED_STEW, CURRY_LEAF) { player, used, with ->
            if (!hasLevelDyn(player, Skills.COOKING, 60)) {
                sendMessage(player, "You need a Cooking level of 60 to make that.")
                return@onUseWith true
            }

            val requiredCurryLeaves = 3

            if (amountInInventory(player, with.id) < requiredCurryLeaves) {
                sendMessage(player, "You need $requiredCurryLeaves curry leaves to mix with the stew.")
                return@onUseWith true
            }

            if (removeItem(player, Item(used.id, 1), Container.INVENTORY) &&
                removeItem(player, Item(with.id, requiredCurryLeaves), Container.INVENTORY)) {
                addItem(player, UNCOOKED_CURRY, 1, Container.INVENTORY)
                sendMessage(player, "You mix the curry leaves with the stew.")
            }

            return@onUseWith true
        }

    }

    companion object {
        private const val BOWL_OF_WATER = Items.BOWL_OF_WATER_1921
        private const val UNCOOKED_STEW = Items.UNCOOKED_STEW_2001
        private const val UNCOOKED_CURRY = Items.UNCOOKED_CURRY_2009
        private const val SPICE = Items.SPICE_2007
        private const val CURRY_LEAF = Items.CURRY_LEAF_5970
        private const val POTATO = Items.POTATO_1942
        private const val POTATO_STEW = Items.INCOMPLETE_STEW_1997
        private const val MEAT_STEW = Items.INCOMPLETE_STEW_1999
        private const val COOKED_MEAT = Items.COOKED_MEAT_2142
        private val stewIngredients = intArrayOf(Items.COOKED_MEAT_2142, Items.POTATO_1942)
        private val incompleteStew = intArrayOf(Items.INCOMPLETE_STEW_1997, Items.INCOMPLETE_STEW_1999)
    }
}