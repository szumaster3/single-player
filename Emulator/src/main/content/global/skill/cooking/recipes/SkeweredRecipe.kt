package content.global.skill.cooking.recipes

import core.api.*
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Sounds
import kotlin.math.min

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

        onUseWith(IntType.ITEM, IRON_SPIT, *rawIngredients) { player, used, base ->
            val itemDetails = mapOf(
                Items.RAW_BIRD_MEAT_9978 to Pair(11, Items.SKEWERED_BIRD_MEAT_9984),
                Items.RAW_RABBIT_3226 to Pair(16, Items.SKEWERED_RABBIT_7224),
                Items.RAW_BEAST_MEAT_9986 to Pair(21, Items.SKEWERED_BEAST_9992),
                Items.RAW_CHOMPY_2876 to Pair(30, Items.SKEWERED_CHOMPY_7230)
            )
            val (requirements, product) = itemDetails[base.id] ?: return@onUseWith false

            if (!hasLevelDyn(player, Skills.COOKING, requirements)) {
                sendMessage(player, "You need a Cooking level of $requirements to make that.")
                return@onUseWith true
            }

            if (amountInInventory(player, used.id) == 1 || amountInInventory(player, base.id) == 1) {
                if (removeItem(player, Item(used.id, 1), Container.INVENTORY) && removeItem(player, Item(base.id, 1), Container.INVENTORY)) {
                    addItem(player, product, 1, Container.INVENTORY)
                    val ingredient = base.name.lowercase()
                    sendMessage(player, "You pierce the $ingredient with the iron spit.")
                }
                return@onUseWith true
            }

            sendSkillDialogue(player) {
                withItems(product)
                create { _, amount ->
                    runTask(player, 2, amount) {
                        if (amount < 1) return@runTask
                        if (removeItem(player, Item(used.id, 1), Container.INVENTORY) && removeItem(
                                player,
                                Item(base.id, 1),
                                Container.INVENTORY
                            )
                        ) {
                            addItem(player, product, 1, Container.INVENTORY)
                        }
                    }
                }

                calculateMaxAmount { _ ->
                    min(amountInInventory(player, used.id), amountInInventory(player, base.id))
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

        onUseWith(IntType.ITEM, SKEWER_STICK, SPIDER_CARCASS) { player, used, with ->
            if (removeItem(player, Item(used.id, 1), Container.INVENTORY) && removeItem(player, Item(with.id, 1), Container.INVENTORY)) {
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

        onUseWith(IntType.ITEM, ARROW_SHAFT, SPIDER_CARCASS) { player, used, with ->
            if (removeItem(player, Item(used.id, 1), Container.INVENTORY) && removeItem(player, Item(with.id, 1), Container.INVENTORY)) {
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