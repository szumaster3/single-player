package content.global.skill.cooking

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.system.task.Pulse
import core.game.world.update.flag.context.Animation

import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery
import org.rs.consts.Sounds

class CookingListeners : InteractionListener {

    private val potteryId = intArrayOf(
        Scenery.POTTERY_OVEN_2643,
        Scenery.POTTERY_OVEN_4308,
        Scenery.POTTERY_OVEN_11601,
        Scenery.POTTERY_OVEN_34802
    )

    private val churnId = intArrayOf(
        Scenery.DAIRY_CHURN_10093,
        Scenery.DAIRY_CHURN_10094,
        Scenery.DAIRY_CHURN_25720,
        Scenery.DAIRY_CHURN_34800,
        Scenery.DAIRY_CHURN_35931
    )

    private val kebabId = intArrayOf(
        Items.KEBAB_1971,
        Items.UGTHANKI_KEBAB_1883,
        Items.UGTHANKI_KEBAB_1885
    )

    private val meatId = intArrayOf(
        Items.RAW_CHOMPY_2876,
        Items.RAW_RABBIT_3226,
        Items.RAW_BIRD_MEAT_9978,
        Items.RAW_BEAST_MEAT_9986
    )

    private val churnInputId = intArrayOf(
        Items.BUCKET_OF_MILK_1927,
        Items.POT_OF_CREAM_2130,
        Items.PAT_OF_BUTTER_6697
    )

    override fun defineListeners() {

        /*
         * Handles creating an uncooked cake.
         */

        onUseWith(IntType.ITEM, Items.CAKE_TIN_1887, Items.POT_OF_FLOUR_1933) { player, used, _ ->
            val itemSlot = used.asItem().slot

            if (!anyInInventory(player, Items.POT_OF_FLOUR_1933, Items.BUCKET_OF_MILK_1927, Items.EGG_1944)) {
                return@onUseWith false
            }

            if (player.inventory.remove(
                    Item(Items.BUCKET_OF_MILK_1927, 1),
                    Item(Items.EGG_1944, 1),
                    Item(Items.CAKE_TIN_1887, 1),
                    Item(Items.POT_OF_FLOUR_1933, 1),
                )
            ) {
                replaceSlot(player, itemSlot, Item(Items.UNCOOKED_CAKE_1889, 1))
                sendMessage(player, "You mix the milk, flour, and egg together to make a raw cake mix.")
                return@onUseWith true
            }

            return@onUseWith false
        }

        /*
         * Handles creating a super kebab from a kebab and red-hot sauce.
         */

        onUseWith(IntType.ITEM, Items.RED_HOT_SAUCE_4610, *kebabId) { player, used, with ->
            if (removeItem(player, used.asItem()) && removeItem(player, with.asItem())) {
                addItemOrDrop(player, Items.SUPER_KEBAB_4608)
            }
            return@onUseWith true
        }

        /*
         * Handles creating a pie shell from pastry dough and a pie dish.
         */

        onUseWith(IntType.ITEM, Items.PASTRY_DOUGH_1953, Items.PIE_DISH_2313) { player, used, with ->
            if (removeItem(player, used.asItem()) && removeItem(player, with.asItem())) {
                addItem(player, Items.PIE_SHELL_2315)
                sendMessage(player, "You put the pastry dough into the pie dish to make a pie shell.")
            }
            return@onUseWith true
        }

        /*
         * Handles putting nettles into a bowl of water.
         */

        onUseWith(IntType.ITEM, Items.NETTLES_4241, Items.BOWL_OF_WATER_1921) { player, used, with ->
            if (removeItem(player, used.id)) {
                replaceSlot(player, with.asItem().slot, Item(Items.NETTLE_WATER_4237))
                sendMessage(player, "You place the nettles into the bowl of water.")
            }
            return@onUseWith true
        }

        /*
         * Handles creating nettle tea (Milk).
         */

        onUseWith(IntType.ITEM, Items.BUCKET_OF_MILK_1927, Items.NETTLE_TEA_4239) { player, used, with ->
            replaceSlot(player, used.asItem().slot, Item(Items.BUCKET_1925))
            replaceSlot(player, with.asItem().slot, Item(Items.NETTLE_TEA_4240))
            return@onUseWith true
        }

        /*
         * Handles adding milk to cup of tea.
         */

        onUseWith(IntType.ITEM, Items.BUCKET_OF_MILK_1927, Items.CUP_OF_TEA_4245) { player, used, with ->
            replaceSlot(player, used.asItem().slot, Item(Items.BUCKET_1925))
            replaceSlot(player, with.asItem().slot, Item(Items.CUP_OF_TEA_4246))
            return@onUseWith true
        }

        /*
         * Handles filling an empty cup with nettle tea.
         */

        onUseWith(IntType.ITEM, Items.NETTLE_TEA_4239, Items.EMPTY_CUP_1980) { player, used, with ->
            replaceSlot(player, used.asItem().slot, Item(Items.BOWL_1923))
            replaceSlot(player, with.asItem().slot, Item(Items.CUP_OF_TEA_4242))
            return@onUseWith true
        }

        /*
         * Handles filling an empty porcelain cup with nettle tea.
         */

        onUseWith(IntType.ITEM, Items.NETTLE_TEA_4240, Items.EMPTY_CUP_1980) { player, used, with ->
            replaceSlot(player, used.asItem().slot, Item(Items.BOWL_1923))
            replaceSlot(player, with.asItem().slot, Item(Items.CUP_OF_TEA_4243))
            return@onUseWith true
        }

        /*
         * Handles filling a porcelain cup with nettle tea.
         */

        onUseWith(IntType.ITEM, Items.NETTLE_TEA_4239, Items.PORCELAIN_CUP_4244) { player, used, with ->
            replaceSlot(player, used.asItem().slot, Item(Items.BOWL_1923))
            replaceSlot(player, with.asItem().slot, Item(Items.CUP_OF_TEA_4245))
            return@onUseWith true
        }

        /*
         * Handles filling a porcelain cup with nettle tea.
         */

        onUseWith(IntType.ITEM, Items.NETTLE_TEA_4240, Items.PORCELAIN_CUP_4244) { player, used, with ->
            replaceSlot(player, used.asItem().slot, Item(Items.BOWL_1923))
            replaceSlot(player, with.asItem().slot, Item(Items.CUP_OF_TEA_4246))
            return@onUseWith true
        }

        /*
         * Handles cutting a calquat fruit with a knife.
         */

        onUseWith(IntType.ITEM, Items.KNIFE_946, Items.CALQUAT_FRUIT_5980) { player, used, with ->
            val anim = Animation(Animations.HUMAN_FRUIT_CUTTING_1192)
            if (used.id == Items.KNIFE_946) {
                animate(player, anim)
                queueScript(player, animationDuration(anim), QueueStrength.NORMAL) {
                    replaceSlot(player, with.asItem().slot, Item(Items.CALQUAT_KEG_5769))
                    return@queueScript stopExecuting(player)
                }
            }
            return@onUseWith true
        }

        /*
         * Handles cutting a chocolate bar with a knife.
         */

        onUseWith(IntType.ITEM, Items.KNIFE_946, Items.CHOCOLATE_BAR_1973) { player, _, _ ->
            player.pulseManager.run(
                object : Pulse(1) {
                    val cut_animation = Animations.CUTTING_CHOCOLATE_BAR_1989

                    override fun pulse(): Boolean {
                        super.setDelay(4)
                        val amount = amountInInventory(player, Items.CHOCOLATE_BAR_1973)
                        if (amount > 0) {
                            removeItem(player, Items.CHOCOLATE_BAR_1973).also {
                                animate(player, cut_animation)
                                addItem(player, Items.CHOCOLATE_DUST_1975)
                            }
                        }
                        return amount <= 0
                    }
                },
            )
            return@onUseWith true
        }

        /*
         * Handles cutting a tuna with a knife to creating chopped tuna.
         */

        onUseWith(IntType.ITEM, Items.TUNA_361, Items.KNIFE_946) { player, used, with ->
            if (!inInventory(player, with.id, 1)) {
                sendDialogue(player, "You need a knife to slice up the tuna.")
                return@onUseWith false
            }
            if (!inInventory(player, Items.BOWL_1923, 1)) {
                sendDialogue(player, "You need a bowl in order to make it.")
                return@onUseWith false
            }

            if (removeItem(player, Item(Items.TUNA_361, 1), Container.INVENTORY)) {
                replaceSlot(player, used.asItem().slot, Item(Items.CHOPPED_TUNA_7086, 1))
                sendMessage(player, "You chop the tuna into the bowl.")
            }
            return@onUseWith true
        }

        /*
         * Handles churn interaction to process milk, cream, or butter.
         */

        on(churnId, IntType.SCENERY, "churn") { player, _ ->
            if (!anyInInventory(player, *churnInputId)) {
                sendMessage(player, "You need some milk, cream or butter to use in the churn.")
                return@on true
            }
            player.dialogueInterpreter.open(984374)
            return@on true
        }

        /*
         * Handles using the item on churn to process milk, cream, or butter.
         */

        onUseWith(IntType.SCENERY, churnInputId, *churnId) { player, _, _ ->
            if (!anyInInventory(player, *churnInputId)) {
                sendMessage(player, "You need some milk, cream or butter to use in the churn.")
                return@onUseWith true
            }
            player.dialogueInterpreter.open(984374)
            return@onUseWith true
        }

        /*
         * Handles cooking meat on an iron spit.
         */

        onUseWith(IntType.ITEM, Items.IRON_SPIT_7225, *meatId) { player, used, with ->
            if (getStatLevel(player, Skills.FIREMAKING) < 20) {
                sendDialogue(player, "You need a firemaking level of at least 20 in order to do this.")
                return@onUseWith false
            }

            val productId =
                when (used.id) {
                    Items.RAW_CHOMPY_2876 -> Items.SKEWERED_CHOMPY_7230
                    Items.RAW_RABBIT_3226 -> Items.SKEWERED_RABBIT_7224
                    Items.RAW_BIRD_MEAT_9978 -> Items.SKEWERED_BIRD_MEAT_9984
                    Items.RAW_BEAST_MEAT_9986 -> Items.SKEWERED_BEAST_9992
                    else -> null
                }

            if (productId != null) {
                if (removeItem(player, used.asItem()) && removeItem(player, with.asItem())) {
                    addItem(player, productId)
                }
            } else {
                sendDialogue(player, "This item cannot be cooked on the spit.")
            }

            return@onUseWith true
        }

        /*
         * Handles creating a spider on a stick.
         */

        onUseWith(IntType.ITEM, Items.SKEWER_STICK_6305, Items.SPIDER_CARCASS_6291) { player, used, with ->
            val itemSlot = used.asItem().slot
            if (removeItem(player, used.asItem()) && removeItem(player, with.asItem())) {
                animate(player, Animations.CRAFT_ITEM_1309)
                playAudio(player, Sounds.TBCU_SPIDER_STICK_1280)
                replaceSlot(player, itemSlot, Item(Items.SPIDER_ON_STICK_6293, 1))
                sendMessage(player, "You pierce the spider carcass with the skewer stick.")
            }
            return@onUseWith true
        }

        /*
         * Handles interacting with the pottery oven.
         */

        on(potteryId, IntType.SCENERY, "fire") { player, node ->
            player.faceLocation(node.location)
            player.dialogueInterpreter.open(99843, true, true)
            return@on true
        }

        /*
         * Handles using a jug of water with grapes to create unfermented wine.
         */

        onUseWith(IntType.ITEM, Items.JUG_OF_WATER_1937, Items.GRAPES_1987) { player, used, with ->
            val itemSlot = used.asItem().slot
            if (getStatLevel(player, Skills.COOKING) < 35) {
                sendDialogue(player, "You need a cooking level of 35 to do this.")
                return@onUseWith false
            }
            if (removeItem(player, with.asItem())) {
                replaceSlot(player, itemSlot, Item(Items.UNFERMENTED_WINE_1995, 1))
                submitIndividualPulse(player, WineFermentingPulse(1, player))
            }
            return@onUseWith true
        }

        /*
         * Handles attempting to add cheese to a baked potato without butter.
         */

        onUseWith(IntType.ITEM, Items.POTATO_1943, Items.CHEESE_1985) { player, _, _ ->
            sendMessage(player, "You must add butter to the baked potato before adding toppings.")
            return@onUseWith true
        }

    }

}
