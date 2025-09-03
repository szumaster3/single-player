package content.global.skill.cooking

import core.api.*
import core.game.interaction.Clocks
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction
import shared.consts.Items
import kotlin.math.min

class CookingRecipePlugin : InteractionListener {

    override fun defineListeners() {
        CookingRecipe.recipeMap.forEach { (k, recipes) ->
            val ingredient = k shr 16
            val secondary = k and 0xFFFF

            onUseWith(IntType.ITEM, ingredient, secondary) { player, _, _ ->
                val validRecipe = recipes.firstOrNull { hasRequirements(player, it) }
                validRecipe?.let { handle(player, it) }
                return@onUseWith true
            }
        }

        onUseWith(IntType.ITEM, Items.PITTA_BREAD_1865, Items.KEBAB_MIX_1881) { player, _, _ ->
            handleSpecialRecipe(player)
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, Items.CHEESE_1985, Items.POTATO_1942) { player, _, _ ->
            sendMessage(player, "You must add butter to the baked potato before adding toppings.")
            return@onUseWith true
        }
    }

    companion object {
        fun hasRequirements(player: Player, recipe: CookingRecipe): Boolean {
            if (!hasLevelDyn(player, Skills.COOKING, recipe.requiredLevel)) {
                sendMessage(player, "You need a Cooking level of at least ${recipe.requiredLevel} to make this.")
                return false
            }

            if (recipe.requiresKnife && !inInventory(player, Items.KNIFE_946)) {
                sendMessage(player, "You need a knife to prepare this recipe.")
                return false
            }

            val missingIngredients = recipe.ingredientIds.filterNot { inInventory(player, it) }
            val missingSecondary = if (!inInventory(player, recipe.secondaryId)) listOf(recipe.secondaryId) else emptyList()

            if (missingIngredients.isNotEmpty() || missingSecondary.isNotEmpty()) {
                sendMessage(player, "You don't have the required ingredients.")
                return false
            }
            return true
        }

        private fun getMaxRecipeAmount(player: Player, recipe: CookingRecipe): Int {
            val ingredientCount = recipe.ingredientIds.map { amountInInventory(player, it) }.minOrNull() ?: 0
            val secondaryCount = amountInInventory(player, recipe.secondaryId)
            return min(ingredientCount, secondaryCount)
        }

        fun handle(player: Player, recipe: CookingRecipe) {
            if (!hasRequirements(player, recipe)) return

            val maxAmount = getMaxRecipeAmount(player, recipe)
            if (maxAmount <= 0) {
                sendMessage(player, "You don't have the required ingredients.")
                return
            }

            fun processNext(remaining: Int) {
                if (remaining <= 0) return
                processRecipe(player, recipe, 1)
                if (freeSlots(player) < 1) return

                queueScript(player, 2, QueueStrength.NORMAL) {
                    processNext(remaining - 1)
                    true
                }
            }

            if (maxAmount == 1) {
                processNext(1)
                return
            }

            sendSkillDialogue(player) {
                recipe.productId?.let { withItems(it) }
                create { _, amount -> processNext(amount) }
                calculateMaxAmount { maxAmount }
            }
        }

        fun handleSpecialRecipe(player: Player) {
            if (!hasLevelDyn(player, Skills.COOKING, 58)) {
                sendDialogue(player, "You need a Cooking level of at least 58 to make that.")
                return
            }
            if (!inInventory(player, Items.PITTA_BREAD_1865) || !inInventory(player, Items.KEBAB_MIX_1881)) {
                sendMessage(player, "You don't have the required ingredients.")
                return
            }
            if (freeSlots(player) < 1) {
                sendMessage(player, "You don't have enough space in your inventory.")
                return
            }

            removeItem(player, Items.PITTA_BREAD_1865)
            removeItem(player, Items.KEBAB_MIX_1881)
            addItem(player, Items.BOWL_1923, 1)

            if (RandomFunction.roll(50)) {
                addItem(player, Items.UGTHANKI_KEBAB_1885)
                sendMessage(player, "Your kebab smells a bit off, but you keep it.")
            } else {
                rewardXP(player, Skills.COOKING, 40.0)
                addItem(player, Items.UGTHANKI_KEBAB_1883, 1)
                sendMessage(player, "You make a delicious ugthanki kebab.")
            }
        }

        private fun processRecipe(player: Player, recipe: CookingRecipe, amount: Int) {
            if (!clockReady(player, Clocks.SKILLING)) return

            recipe.animation?.let {
                lock(player, 1)
                animate(player, it)
            }

            repeat(amount) {
                recipe.ingredientIds.forEach { ingredient ->
                    if (amountInInventory(player, ingredient) > 0) removeItem(player, ingredient)
                }
                if (amountInInventory(player, recipe.secondaryId) > 0) removeItem(player, recipe.secondaryId)

                if (recipe == CookingRecipe.UNCOOKED_CAKE) {
                    addItemOrDrop(player, Items.EMPTY_POT_1931, 1)
                }
            }

            recipe.productId?.let { addItem(player, it, amount) }
            recipe.returnsContnainer?.let { addItemOrDrop(player, it, amount) }
            recipe.xpReward?.let { rewardXP(player, Skills.COOKING, it * amount) }
            recipe.message?.let { sendMessage(player, it) }
            recipe.animation?.let { animate(player, Animation.RESET) }
            delayClock(player, Clocks.SKILLING, 2)
        }
    }
}
