package content.global.skill.cooking.recipes

import core.api.*
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Sounds

class SkeweredRecipe : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles creating skewered food with a spit.
         *
         * Products:
         *  - Skewered Bird Meat    (Required: Level 11 Cooking)
         *  - Skewered Rabbit:      (Required: Level 16 Cooking)
         *  - Skewered Beast Meat:  (Required: Level 21 Cooking)
         *  - Skewered Chompy:      (Required: Level 30 Cooking)
         *
         * Ticks: 2 (1.2 seconds)
         */

        val spitRecipes = mapOf(
            Items.RAW_BIRD_MEAT_9978 to Pair(11, Items.SKEWERED_BIRD_MEAT_9984),
            Items.RAW_RABBIT_3226 to Pair(16, Items.SKEWERED_RABBIT_7224),
            Items.RAW_BEAST_MEAT_9986 to Pair(21, Items.SKEWERED_BEAST_9992),
            Items.RAW_CHOMPY_2876 to Pair(30, Items.SKEWERED_CHOMPY_7230)
        )

        onUseWith(IntType.ITEM, rawIngredients, IRON_SPIT) { player, used, with ->
            val (requiredLevel, productID) = spitRecipes[used.id] ?: return@onUseWith false

            if (!hasLevelDyn(player, Skills.COOKING, requiredLevel)) {
                sendDialogue(player, "You need an Cooking level of at least $requiredLevel to make that.")
                return@onUseWith true
            }

            val ingredientName = used.name.lowercase()
            val amountUsed = amountInInventory(player, used.id)
            val amountWith = amountInInventory(player, with.id)
            val maxAmount = minOf(amountUsed, amountWith)

            fun process(): Boolean {
                val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(
                    player, with.asItem(), Container.INVENTORY
                )
                if (success) {
                    addItem(player, productID, 1, Container.INVENTORY)
                    sendMessage(player, "You pierce the $ingredientName with the iron spit.")
                    return true
                }
                return false
            }

            if (maxAmount <= 1) {
                process()
            } else {
                sendSkillDialogue(player) {
                    withItems(productID)
                    create { _, amount ->
                        runTask(player, 2, amount) {
                            process()
                        }
                    }
                    calculateMaxAmount { maxAmount }
                }
            }

            return@onUseWith true
        }

        /*
         * Handles creating a spider on a stick using a skewer stick and a spider carcass.
         *
         * Product:
         *  - Spider on a Stick.
         *
         */

        onUseWith(IntType.ITEM, SPIDER_CARCASS, SKEWER_STICK) { player, used, with ->
            val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)
            if (success) {
                animate(player, PIERCE_ANIMATION)
                playAudio(player, Sounds.TBCU_SPIDER_STICK_1280)
                addItem(player, SPIDER_ON_STICK, 1, Container.INVENTORY)
                sendMessage(player, "You pierce the spider carcass with the skewer stick.")
            }
            return@onUseWith true
        }

        /*
         * Handles creating a spider on a shaft using an arrow shaft and a spider carcass.
         *
         * Product:
         *  - Spider on a Shaft.
         *
         */

        onUseWith(IntType.ITEM, SPIDER_CARCASS, ARROW_SHAFT) { player, used, with ->
            val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)
            if (success) {
                animate(player, PIERCE_ANIMATION)
                playAudio(player, Sounds.TBCU_SPIDER_1279)
                addItem(player, SPIDER_ON_SHAFT, 1, Container.INVENTORY)
                sendMessage(player, "You pierce the spider carcass with the arrow shaft.")
            }
            return@onUseWith true
        }
    }

    companion object {
        private const val ARROW_SHAFT = Items.ARROW_SHAFT_52
        private const val SPIDER_CARCASS = Items.SPIDER_CARCASS_6291
        private const val PIERCE_ANIMATION = Animations.CRAFT_ITEM_1309
        private const val SKEWER_STICK = Items.SKEWER_STICK_6305
        private const val SPIDER_ON_SHAFT = Items.SPIDER_ON_SHAFT_6295
        private const val SPIDER_ON_STICK = Items.SPIDER_ON_STICK_6293
        private const val IRON_SPIT = Items.IRON_SPIT_7225
        private val rawIngredients = intArrayOf(Items.RAW_CHOMPY_2876, Items.RAW_RABBIT_3226, Items.RAW_BIRD_MEAT_9978, Items.RAW_BEAST_MEAT_9986)
    }
}