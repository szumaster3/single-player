package content.global.skill.cooking

import core.api.*
import core.game.dialogue.SkillDialogueHandler
import core.game.interaction.Clocks
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.SkillPulse
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.tools.RandomFunction
import shared.consts.Items
import kotlin.math.min

/**
 * Handles cooking recipes.
 */
class CookingRecipeHandler : InteractionListener {

    private val firstIngredient = CookingRecipe.values().map { it.ingredientID }.toIntArray()
    private val secondIngredient = CookingRecipe.values().map { it.secondaryID }.toIntArray()

    override fun defineListeners() {
        onUseWith(IntType.ITEM, firstIngredient, *secondIngredient) { player, used, with ->
            val recipe = CookingRecipe.forId(used.id) ?: return@onUseWith true

            if (!hasLevelDyn(player, Skills.COOKING, recipe.requiredLevel)) {
                sendMessage(player, "You need a Cooking level of at least ${recipe.requiredLevel} to make this.")
                return@onUseWith true
            }

            if (recipe.requiresKnife && !inInventory(player, Items.KNIFE_946)) {
                sendMessage(player, "You need a knife to prepare this recipe.")
                return@onUseWith true
            }

            if (!inInventory(player, recipe.ingredientID) || !inInventory(player, recipe.secondaryID)) {
                return@onUseWith true
            }

            val ingredientAmount = amountInInventory(player, used.id)
            val secondaryAmount = amountInInventory(player, with.id)
            val maxAmount = min(ingredientAmount, secondaryAmount)

            val handler: SkillDialogueHandler = object : SkillDialogueHandler(player, SkillDialogue.ONE_OPTION, Item(recipe.ingredientID)) {
                override fun create(amount: Int, index: Int) {
                    player.pulseManager.run(object : SkillPulse<Item?>(player, Item(recipe.ingredientID)) {
                        private var remaining = amount

                        override fun checkRequirements(): Boolean {
                            return inInventory(player, recipe.ingredientID) && inInventory(player, recipe.secondaryID)
                        }

                        override fun animate() {
                            recipe.animation?.let { animate(player, it) }
                        }

                        override fun reward(): Boolean {
                            val ingredientItem = player.inventory.getItem(used.asItem()) ?: return true
                            player.inventory.replace(Item(recipe.productID, 1), ingredientItem.slot)

                            if (recipe.secondaryID != Items.KNIFE_946) {
                                player.inventory.get(recipe.secondaryID)?.let { secondaryItem ->
                                    recipe.returnsContainer?.let { container ->
                                        player.inventory.replace(Item(container, 1), secondaryItem.slot)
                                    }
                                }
                            } else {
                                recipe.returnsContainer?.let { addItemOrDrop(player, it, 1) }
                            }

                            recipe.xpReward?.let { rewardXP(player, Skills.COOKING, it) }
                            recipe.message?.let { sendMessage(player, it) }

                            delayClock(player, Clocks.SKILLING, 2)
                            remaining--
                            return remaining <= 0
                        }
                    })
                }

                override fun getAll(index: Int): Int {
                    return min(amountInInventory(player, recipe.ingredientID), amountInInventory(player, recipe.secondaryID))
                }
            }

            if (maxAmount == 1) {
                handler.create(1, 1)
            } else {
                handler.open()
            }

            return@onUseWith true
        }



        onUseWith(IntType.ITEM, Items.PITTA_BREAD_1865, Items.KEBAB_MIX_1881) { player, _, _ ->
            handleSpecialRecipe(player)
            return@onUseWith true
        }

        onUseWith(
            IntType.ITEM, Items.CAKE_TIN_1887, Items.EGG_1944, Items.BUCKET_OF_MILK_1927, Items.POT_OF_FLOUR_1933
        ) { player, _, _ ->
            handleCakeRecipe(player)
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, Items.CHEESE_1985, Items.POTATO_1942) { player, _, _ ->
            sendMessage(player, "You must add butter to the baked potato before adding toppings.")
            return@onUseWith true
        }
    }

    /**
     * Handles a special ugthanki kebab recipe.
     *
     * @param player The player.
     */
    private fun handleSpecialRecipe(player: Player) {
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

    /**
     * Handles creating the uncooked cake.
     *
     * @param player The player.
     */
    private fun handleCakeRecipe(player: Player) {
        val ingredients = intArrayOf(
            Items.POT_OF_FLOUR_1933, Items.BUCKET_OF_MILK_1927, Items.EGG_1944, Items.CAKE_TIN_1887
        )
        if (!hasLevelDyn(player, Skills.COOKING, 40)) {
            sendDialogue(player, "You need a Cooking level of at least 40 to make this cake.")
            return
        }

        if (!anyInInventory(player, *ingredients)) return
        if (freeSlots(player) < 1) {
            sendMessage(player, "You don't have enough space in your inventory.")
            return
        }

        ingredients.forEach { id ->
            val item = player.inventory.get(id)
            if (item != null && item.slot > -1) {
                when (id) {
                    Items.POT_OF_FLOUR_1933 -> player.inventory.replace(Item(Items.EMPTY_POT_1931, 1), item.slot)
                    Items.BUCKET_OF_MILK_1927 -> player.inventory.replace(Item(Items.BUCKET_1925, 1), item.slot)
                    Items.EGG_1944, Items.CAKE_TIN_1887 -> player.inventory.remove(Item(id, 1))
                }
            }
        }

        addItem(player, Items.UNCOOKED_CAKE_1889, 1)

        rewardXP(player, Skills.COOKING, 40.0)
        sendMessage(player, "You mix the milk, flour, and egg together to make a raw cake mix.")
        delayClock(player, Clocks.SKILLING, 2)
    }
}
