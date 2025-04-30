package content.global.skill.cooking.recipes

import core.api.*
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import org.rs.consts.Items

class PieRecipe : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles creating a pie shell from pastry dough and a pie dish.
         */

        onUseWith(IntType.ITEM, Items.PASTRY_DOUGH_1953, Items.PIE_DISH_2313) { player, used, with ->
            if (removeItem(player, Item(used.id, 1))) {
                addItem(player, Items.PIE_SHELL_2315, 1)
                sendMessage(player, "You put the pastry dough into the pie dish to make a pie shell.")
            }
            return@onUseWith true
        }

        /*
         * Handles combining a pie shell with ingredients to create first part pies.
         *
         * Requirements:
         * - Redberry pie:     Level 10 Cooking (using Items.REDBERRIES_1951)
         * - Meat pie:         Level 20 Cooking (using Items.COOKED_MEAT_2142 or Items.COOKED_CHICKEN_2140)
         * - Part mud pie      Level 29 Cooking (using Items.COMPOST_6032)
         * - Apple pie:        Level 30 Cooking (using Items.COOKING_APPLE_1955)
         * - Part garden pie:  Level 34 Cooking (using Items.TOMATO_1982)
         * - Part fish pie:    Level 47 Cooking (using Items.TROUT_333)
         * - Part admiral pie: Level 70 Cooking (using Items.SALMON_329)
         * - Part wild pie:    Level 85 Cooking (using Items.RAW_BEAR_MEAT_2136)
         * - Part summer pie   Level 95 Cooking (using Items.STRAWBERRY_5504)
         *
         * Ticks: 2 (1.2 seconds)
         */

        val pieRecipes = mapOf(
            Items.REDBERRIES_1951 to Pair(10, Items.UNCOOKED_BERRY_PIE_2321),
            Items.COOKED_MEAT_2142 to Pair(20, Items.UNCOOKED_MEAT_PIE_2319),
            Items.COOKED_CHICKEN_2140 to Pair(20, Items.UNCOOKED_MEAT_PIE_2319),
            Items.COMPOST_6032 to Pair(29, Items.PART_MUD_PIE_7164),
            Items.COOKING_APPLE_1955 to Pair(30, Items.UNCOOKED_APPLE_PIE_2317),
            Items.TOMATO_1982 to Pair(34, Items.PART_GARDEN_PIE_7172),
            Items.TROUT_333 to Pair(47, Items.PART_FISH_PIE_7182),
            Items.SALMON_329 to Pair(70, Items.PART_ADMIRAL_PIE_7192),
            Items.RAW_BEAR_MEAT_2136 to Pair(85, Items.PART_WILD_PIE_7202),
            Items.STRAWBERRY_5504 to Pair(95, Items.PART_SUMMER_PIE_7212)
        )

        onUseWith(IntType.ITEM, firstPieIngredients, Items.PIE_SHELL_2315) { player, used, with ->
            val (requiredLevel, productID) = pieRecipes[used.id] ?: return@onUseWith false

            if (!hasLevelDyn(player, Skills.COOKING, requiredLevel)) {
                sendDialogue(player, "You need an Cooking level of at least $requiredLevel to make that.")
                return@onUseWith true
            }

            val ingredientName = used.name.lowercase()
            val amountUsed = amountInInventory(player, used.id)
            val amountWith = amountInInventory(player, with.id)
            val maxAmount = minOf(amountUsed, amountWith)

            fun process(): Boolean {
                val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)
                if (success) {
                    if(used.id == Items.COMPOST_6032) {
                        addItemOrDrop(player, Items.BUCKET_1925)
                    }
                    addItem(player, productID, 1, Container.INVENTORY)
                    sendMessage(player, "You fill the pie with $ingredientName.")
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
         * Handles combining a part pies with ingredients to create second part pies.
         *
         * Requirements:
         * - Part mud pie      Level 29 Cooking (using Items.BUCKET_OF_WATER_1929)
         * - Part garden pie:  Level 34 Cooking (using Items.ONION_1957)
         * - Part fish pie:    Level 47 Cooking (using Items.COD_339)
         * - Part admiral pie: Level 70 Cooking (using Items.TUNA_361)
         * - Part wild pie:    Level 85 Cooking (using Items.RAW_CHOMPY_2876)
         * - Part summer pie   Level 95 Cooking (using Items.WATERMELON_5982)
         *
         * Ticks: 2 (1.2 seconds)
         */

        val secondPieRecipes = mapOf(
            Pair(Items.PART_MUD_PIE_7164, Items.BUCKET_OF_WATER_1929) to Triple(29, Items.PART_MUD_PIE_7166, true),
            Pair(Items.PART_GARDEN_PIE_7172, Items.ONION_1957) to Triple(34, Items.PART_GARDEN_PIE_7174, false),
            Pair(Items.PART_FISH_PIE_7182, Items.COD_339) to Triple(47, Items.PART_FISH_PIE_7184, false),
            Pair(Items.PART_ADMIRAL_PIE_7192, Items.TUNA_361) to Triple(70, Items.PART_ADMIRAL_PIE_7194, false),
            Pair(Items.PART_WILD_PIE_7202, Items.RAW_CHOMPY_2876) to Triple(85, Items.PART_WILD_PIE_7204, false),
            Pair(Items.PART_SUMMER_PIE_7212, Items.WATERMELON_5982) to Triple(95, Items.PART_SUMMER_PIE_7214, false)
        )

        onUseWith(IntType.ITEM, secondPieIngredients, *firstPartPies) { player, used, with ->
            val recipe = secondPieRecipes[with.id to used.id] ?: return@onUseWith true
            val (requiredLevel, productID, returnsBucket) = recipe

            if (!hasLevelDyn(player, Skills.COOKING, requiredLevel)) {
                sendDialogue(player, "You need an Cooking level of at least $requiredLevel to make that.")
                return@onUseWith true
            }

            val ingredientName = used.name.lowercase()
            val maxAmount = minOf(amountInInventory(player, used.id), amountInInventory(player, with.id))

            fun process(): Boolean {
                val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)
                if (success) {
                    if (returnsBucket) addItemOrDrop(player, Items.BUCKET_1925, 1)
                    addItem(player, productID, 1, Container.INVENTORY)
                    sendMessage(player, "You fill the pie with the $ingredientName.")
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
         * Handles combining a second part pies with ingredients to create raw pies.
         *
         * Requirements:
         * - Raw mud pie      Level 29 Cooking (using Items.CLAY_434)
         * - Raw garden pie:  Level 34 Cooking (using Items.CABBAGE_1965 or Items.CABBAGE_1967)
         * - Raw wild pie:    Level 85 Cooking (using Items.RAW_RABBIT_3226)
         * - Raw summer pie   Level 95 Cooking (using Items.COOKING_APPLE_1955)
         *
         * Ticks: 2 (1.2 seconds)
         */

        val rawPieRecipes = mapOf(
            Pair(Items.PART_MUD_PIE_7166, Items.CLAY_434) to Pair(29, Items.RAW_MUD_PIE_7168),
            Pair(Items.PART_GARDEN_PIE_7174, Items.CABBAGE_1965) to Pair(34, Items.RAW_GARDEN_PIE_7176),
            Pair(Items.PART_GARDEN_PIE_7174, Items.CABBAGE_1967) to Pair(34, Items.RAW_GARDEN_PIE_7176),
            Pair(Items.PART_WILD_PIE_7204, Items.RAW_RABBIT_3226) to Pair(85, Items.RAW_WILD_PIE_7206),
            Pair(Items.PART_SUMMER_PIE_7214, Items.COOKING_APPLE_1955) to Pair(95, Items.RAW_SUMMER_PIE_7216)
        )

        onUseWith(IntType.ITEM, rawPieIngredients, *secondPartPie) { player, used, with ->
            val recipe = rawPieRecipes[with.id to used.id] ?: return@onUseWith true
            val (requiredLevel, productID) = recipe

            if (!hasLevelDyn(player, Skills.COOKING, requiredLevel)) {
                sendDialogue(player, "You need an Cooking level of at least $requiredLevel to make that.")
                return@onUseWith true
            }

            val productName = getItemName(productID).lowercase()
            val maxAmount = minOf(amountInInventory(player, used.id), amountInInventory(player, with.id))

            fun prepareRawPie(): Boolean {
                val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)
                if (success) {
                    addItem(player, productID, 1, Container.INVENTORY)
                    sendMessage(player, "You prepare an $productName.")
                    return true
                }
                return false
            }

            if (maxAmount <= 1) {
                prepareRawPie()
            } else {
                sendSkillDialogue(player) {
                    withItems(productID)
                    create { _, amount ->
                        runTask(player, 2, amount) {
                            prepareRawPie()
                        }
                    }
                    calculateMaxAmount { maxAmount }
                }
            }

            return@onUseWith true
        }

        /*
         * Handles creating raw admiral pie.
         */

        onUseWith(IntType.ITEM, Items.PART_ADMIRAL_PIE_7194, Items.POTATO_1942) { player, used, with ->
            if (!hasLevelDyn(player, Skills.COOKING, 70)) {
                sendDialogue(player, "You need an Cooking level of at least 70 to make that.")
                return@onUseWith true
            }

            if (amountInInventory(player, used.id) == 1 || amountInInventory(player, with.id) == 1) {
                val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(
                    player, with.asItem(), Container.INVENTORY
                )
                if (success) {
                    addItem(player, Items.RAW_ADMIRAL_PIE_7196, 1, Container.INVENTORY)
                    sendMessage(player, "You prepare a admiral pie.")
                }
                return@onUseWith true
            }

            sendSkillDialogue(player) {
                withItems(Items.RAW_ADMIRAL_PIE_7196)
                create { _, amount ->
                    runTask(player, 2, amount) {
                        if (amount < 1) return@runTask
                        val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(
                            player, with.asItem(), Container.INVENTORY
                        )
                        if (success) {
                            addItem(player, Items.RAW_ADMIRAL_PIE_7196, 1, Container.INVENTORY)
                            sendMessage(player, "You prepare a admiral pie.")
                        }
                    }
                }

                calculateMaxAmount { _ ->
                    minOf(amountInInventory(player, with.id), amountInInventory(player, used.id))
                }
            }

            return@onUseWith true
        }

        /*
         * Handles creating a raw fish pie.
         */

        onUseWith(IntType.ITEM, Items.PART_FISH_PIE_7184, Items.POTATO_1942) { player, used, with ->
            if (!hasLevelDyn(player, Skills.COOKING, 47)) {
                sendDialogue(player, "You need an Cooking level of at least 47 to make that.")
                return@onUseWith true
            }

            if (amountInInventory(player, used.id) == 1 || amountInInventory(player, with.id) == 1) {
                val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(
                    player, with.asItem(), Container.INVENTORY
                )
                if (success) {
                    addItem(player, Items.RAW_FISH_PIE_7186, 1, Container.INVENTORY)
                    sendMessage(player, "You prepare a fish pie.")
                }
                return@onUseWith true
            }

            sendSkillDialogue(player) {
                withItems(Items.RAW_FISH_PIE_7186)
                create { _, amount ->
                    runTask(player, 2, amount) {
                        if (amount < 1) return@runTask
                        val success = removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(
                            player, with.asItem(), Container.INVENTORY
                        )
                        if (success) {
                            addItem(player, Items.RAW_FISH_PIE_7186, 1, Container.INVENTORY)
                            sendMessage(player, "You prepare a fish pie.")
                        }
                    }
                }

                calculateMaxAmount { _ ->
                    minOf(amountInInventory(player, with.id), amountInInventory(player, used.id))
                }
            }

            return@onUseWith true
        }
    }

    companion object {
        private val firstPieIngredients = intArrayOf(
            Items.REDBERRIES_1951,
            Items.COOKED_MEAT_2142,
            Items.COOKED_CHICKEN_2140,
            Items.COMPOST_6032,
            Items.COOKING_APPLE_1955,
            Items.TOMATO_1982,
            Items.TROUT_333,
            Items.SALMON_329,
            Items.RAW_BEAR_MEAT_2136,
            Items.STRAWBERRY_5504
        )
        private val firstPartPies = intArrayOf(
            Items.PART_MUD_PIE_7164,
            Items.PART_GARDEN_PIE_7172,
            Items.PART_FISH_PIE_7182,
            Items.PART_ADMIRAL_PIE_7192,
            Items.PART_WILD_PIE_7202,
            Items.PART_SUMMER_PIE_7212
        )
        private val secondPieIngredients = intArrayOf(
            Items.BUCKET_OF_WATER_1929,
            Items.ONION_1957,
            Items.COD_339,
            Items.TUNA_361,
            Items.RAW_CHOMPY_2876,
            Items.WATERMELON_5982
        )
        private val secondPartPie = intArrayOf(
            Items.PART_MUD_PIE_7166, Items.PART_GARDEN_PIE_7174, Items.PART_WILD_PIE_7204, Items.PART_SUMMER_PIE_7214
        )
        private val rawPieIngredients = intArrayOf(
            Items.CLAY_434, Items.CABBAGE_1965 or Items.CABBAGE_1967, Items.RAW_RABBIT_3226, Items.COOKING_APPLE_1955
        )
    }
}