package content.global.skill.cooking.recipes

import core.api.*
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.system.task.Pulse
import org.rs.consts.Animations
import org.rs.consts.Items
import kotlin.math.min

class ChoppingRecipe : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles cutting ingredients with a knife.
         *
         * Products:
         *  - Calquat Keg
         *  - Chocolate Dust
         *
         * Ticks: 4 (2.4 seconds)
         */

        onUseWith(IntType.ITEM, KNIFE, *cuttingIngredients) { player, _, items ->
            val (base, product, animation) = when (items.id) {
                CALQUAT_FRUIT -> Triple(CALQUAT_FRUIT, CALQUAT_KEG, CALQUAT_CARVED_ANIMATION)
                CHOCOLATE_BAR -> Triple(CALQUAT_FRUIT, CHOCOLATE_DUST, CHOCOLATE_CUT_ANIMATION)
                else -> return@onUseWith true
            }

            player.pulseManager.run(object : Pulse(1) {
                override fun pulse(): Boolean {
                    super.setDelay(4)
                    val amount = amountInInventory(player, base)
                    if (amount > 0) {
                        if (removeItem(player, Item(base, 1), Container.INVENTORY)) {
                            animate(player, animation)
                            addItem(player, product, 1, Container.INVENTORY)
                        }
                    }
                    return amount <= 0
                }
            })

            return@onUseWith true
        }

        /*
         * Handles chopping ingredients into a bowl using a knife.
         *
         * Products:
         *  - Chopped Tuna
         *  - Chopped Onion
         *  - Chopped Garlic
         *  - Chopped Tomato
         *  - Chopped Ugthanki
         *  - Sliced Mushrooms
         *  - Minced Meat
         *
         * Ticks: 2 (1.2 seconds)
         */

        onUseWith(IntType.ITEM, EMPTY_BOWL, *choppingIngredients) { player, used, ingredients ->
            if (!inInventory(player, Items.KNIFE_946)) {
                sendMessage(player, "You need a knife to slice up the ${ingredients.name.lowercase()}.")
                return@onUseWith true
            }

            val (product, message) = when (ingredients.id) {
                Items.TUNA_361 -> Items.CHOPPED_TUNA_7086 to "You chop the tuna into the bowl."
                Items.ONION_1957 -> Items.CHOPPED_ONION_1871 to "You chop the onion into small pieces."
                Items.GARLIC_1550 -> Items.CHOPPED_GARLIC_7074 to "You chop the garlic into the bowl."
                Items.TOMATO_1982 -> Items.CHOPPED_TOMATO_1869 to "You chop the tomato into the bowl."
                Items.UGTHANKI_MEAT_1861 -> Items.CHOPPED_UGTHANKI_1873 to "You chop the meat into the bowl."
                Items.MUSHROOM_6004 -> Items.SLICED_MUSHROOMS_7080 to "You slice the mushrooms."
                Items.COOKED_MEAT_2142 -> Items.MINCED_MEAT_7070 to "You chop the meat into the bowl."
                else -> return@onUseWith true
            }

            player.pulseManager.run(object : Pulse(1) {
                override fun pulse(): Boolean {
                    super.setDelay(2)
                    val amount = amountInInventory(player, ingredients.id)
                    if (amount > 0) {
                        if (removeItem(player, Item(ingredients.id, 1), Container.INVENTORY) && removeItem(player, Item(used.id, 1), Container.INVENTORY)) {
                            animate(player, Animations.CUT_THING_WITH_KNIFE_IN_HAND_5756)
                            addItem(player, product, 1, Container.INVENTORY)
                            rewardXP(player, Skills.COOKING, 1.0)
                            sendMessage(player, message)
                        }
                    }
                    return amount <= 0
                }
            })
            return@onUseWith true
        }

        /*
         * Handles creating spicy sauce from a chopped garlic and gnome spice.
         *
         * Product: Uncooked Egg.
         */

        onUseWith(IntType.ITEM, KNIFE, EGG) { player, used, with ->
            if (removeItem(player, Item(used.id, 1), Container.INVENTORY) && removeItem(player, Item(with.id, 1), Container.INVENTORY)) {
                addItem(player, Items.UNCOOKED_EGG_7076)
                sendMessage(player, "You prepare an uncooked egg.")
            }
            return@onUseWith true
        }

        /*
         * Handles creating spicy sauce from a chopped garlic and gnome spice.
         */

        onUseWith(IntType.ITEM, CHOPPED_GARLIC, GNOME_SPICE) { player, used, with ->
            if (!hasLevelDyn(player, Skills.COOKING, 9)) {
                sendMessage(player, "You need a Cooking level of 9 to make that.")
                return@onUseWith true
            }

            fun makeDish(): Boolean {
                if (removeItem(player, Item(used.id, 1), Container.INVENTORY) &&
                    removeItem(player, Item(with.id, 1), Container.INVENTORY)
                ) {
                    addItem(player, SPICY_SAUCE, 1, Container.INVENTORY)
                    rewardXP(player, Skills.COOKING, 25.0)
                    sendMessage(player, "You mix the ingredients to make spicy sauce.")
                    return true
                }
                return false
            }

            val amountUsed = amountInInventory(player, used.id)
            val amountWith = amountInInventory(player, with.id)

            if (amountUsed == 1 || amountWith == 1) {
                return@onUseWith makeDish()
            }

            sendSkillDialogue(player) {
                withItems(SPICY_SAUCE)
                create { _, amount ->
                    runTask(player, 2, amount) {
                        if (amount > 0) makeDish()
                    }
                }
                calculateMaxAmount {
                    min(amountWith, amountUsed)
                }
            }

            return@onUseWith true
        }
    }

    companion object {
        private const val CALQUAT_FRUIT = Items.CALQUAT_FRUIT_5980
        private const val CALQUAT_KEG = Items.CALQUAT_KEG_5769
        private const val CALQUAT_CARVED_ANIMATION = Animations.CARVE_CALQUAT_KEG_2290

        private const val CHOCOLATE_BAR = Items.CHOCOLATE_BAR_1973
        private const val CHOCOLATE_DUST = Items.CHOCOLATE_DUST_1975
        private const val CHOCOLATE_CUT_ANIMATION = Animations.CUTTING_CHOCOLATE_BAR_1989

        private const val CHOPPED_GARLIC = Items.CHOPPED_GARLIC_7074
        private const val GNOME_SPICE = Items.GNOME_SPICE_2169
        private const val SPICY_SAUCE = Items.SPICY_SAUCE_7072
        private const val EMPTY_BOWL = Items.BOWL_1923
        private const val EGG = Items.EGG_1944
        private const val KNIFE = Items.KNIFE_946
        private val choppingIngredients = intArrayOf(Items.TUNA_361, Items.ONION_1957, Items.GARLIC_1550, Items.TOMATO_1982, Items.UGTHANKI_MEAT_1861, Items.MUSHROOM_6004, Items.COOKED_MEAT_2142)
        private val cuttingIngredients = intArrayOf(Items.CALQUAT_FRUIT_5980, Items.CHOCOLATE_BAR_1973)
    }
}