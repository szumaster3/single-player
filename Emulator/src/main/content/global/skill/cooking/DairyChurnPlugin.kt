package content.global.skill.cooking

import core.api.*
import core.game.dialogue.Dialogue
import core.game.event.ResourceProducedEvent
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.SkillPulse
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.update.flag.context.Animation
import core.plugin.ClassScanner
import org.rs.consts.Animations
import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.Scenery
import java.util.*

class DairyChurnPlugin : InteractionListener {
    private val ingredients = intArrayOf(Items.BUCKET_OF_MILK_1927, Items.POT_OF_CREAM_2130, Items.PAT_OF_BUTTER_6697)
    private val churns = intArrayOf(Scenery.DAIRY_CHURN_10093, Scenery.DAIRY_CHURN_10094, Scenery.DAIRY_CHURN_25720, Scenery.DAIRY_CHURN_34800, Scenery.DAIRY_CHURN_35931)

    companion object {
        private const val DIALOGUE_ID = 984374
    }

    override fun defineListeners() {
        ClassScanner.definePlugin(DairyChurnDialogue())

        on(churns, IntType.SCENERY, "churn") { player, _ ->
            if (!anyInInventory(player, *ingredients)) {
                sendMessage(player, "You need some milk, cream or butter to use in the churn.")
                return@on true
            }
            player.dialogueInterpreter.open(DIALOGUE_ID)
            true
        }

        onUseWith(IntType.SCENERY, ingredients, *churns) { player, _, _ ->
            if (!anyInInventory(player, *ingredients)) {
                sendMessage(player, "You need some milk, cream or butter to use in the churn.")
                return@onUseWith true
            }
            player.dialogueInterpreter.open(DIALOGUE_ID)
            true
        }
    }
}

class DairyChurnDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        val hasMilk = inInventory(player, Items.BUCKET_OF_MILK_1927)
        val hasCream = inInventory(player, Items.POT_OF_CREAM_2130)
        val hasButter = inInventory(player, Items.PAT_OF_BUTTER_6697)

        val interfaceId = when {
            hasMilk -> Components.COOKING_CHURN_OP3_74
            hasCream -> Components.COOKING_CHURN_OP2_73
            hasButter -> Components.COOKING_CHURN_OP3_74
            else -> return false
        }

        openChatbox(player, interfaceId)
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        val inputItemId: Int
        val dairyProduct: DairyProduct
        val amount: Int

        when (interfaceId) {
            Components.COOKING_CHURN_OP3_74 -> {
                when (buttonId) {
                    13 -> { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.CHEESE; amount = 1 }
                    12 -> { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.CHEESE; amount = 5 }
                    11 -> { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.CHEESE; amount = 10 }
                    10 -> { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.PAT_OF_BUTTER; amount = 1 }
                    9 ->  { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.PAT_OF_BUTTER; amount = 5 }
                    8 ->  { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.PAT_OF_BUTTER; amount = 10 }
                    7 ->  { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.POT_OF_CREAM; amount = 1 }
                    6 ->  { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.POT_OF_CREAM; amount = 5 }
                    5 ->  { inputItemId = Items.BUCKET_OF_MILK_1927; dairyProduct = DairyProduct.POT_OF_CREAM; amount = 10 }
                    else -> return false
                }
            }
            Components.COOKING_CHURN_OP2_73 -> {
                when (buttonId) {
                    9 ->  { inputItemId = Items.POT_OF_CREAM_2130; dairyProduct = DairyProduct.CHEESE; amount = 1 }
                    8 ->  { inputItemId = Items.POT_OF_CREAM_2130; dairyProduct = DairyProduct.CHEESE; amount = 5 }
                    7 ->  { inputItemId = Items.POT_OF_CREAM_2130; dairyProduct = DairyProduct.CHEESE; amount = 10 }
                    6 ->  { inputItemId = Items.POT_OF_CREAM_2130; dairyProduct = DairyProduct.PAT_OF_BUTTER; amount = 1 }
                    5 ->  { inputItemId = Items.POT_OF_CREAM_2130; dairyProduct = DairyProduct.PAT_OF_BUTTER; amount = 5 }
                    4 ->  { inputItemId = Items.POT_OF_CREAM_2130; dairyProduct = DairyProduct.PAT_OF_BUTTER; amount = 10 }
                    else -> return false
                }
            }
            else -> {
                when (buttonId) {
                    5 ->  { inputItemId = Items.PAT_OF_BUTTER_6697; dairyProduct = DairyProduct.CHEESE; amount = 1 }
                    4 ->  { inputItemId = Items.PAT_OF_BUTTER_6697; dairyProduct = DairyProduct.CHEESE; amount = 5 }
                    3 ->  { inputItemId = Items.PAT_OF_BUTTER_6697; dairyProduct = DairyProduct.CHEESE; amount = 10 }
                    else -> return false
                }
            }
        }

        player.pulseManager.run(DairyChurnPulse(player, Item(inputItemId), dairyProduct, amount))
        return true
    }

    override fun newInstance(player: Player?): Dialogue = DairyChurnDialogue(player)

    override fun getIds(): IntArray = intArrayOf(984374)
}

private class DairyChurnPulse(
    player: Player?,
    item: Item?,
    private val dairy: DairyProduct,
    private var amount: Int
) : SkillPulse<Item?>(player, item) {

    companion object {
        private val ANIMATION = Animation(Animations.CHURN_BUTTER_2793)
        private val BUCKET_OF_MILK = Item(Items.BUCKET_OF_MILK_1927)
        private val BUCKET = Item(Items.BUCKET_1925)
    }

    init {
        delay = 8
    }

    override fun checkRequirements(): Boolean {
        closeChatBox(player)
        val input = dairy.inputsItems.firstOrNull { inInventory(player, it.id) } ?: run {
            sendMessage(player, "You need a bucket of milk.")
            return false
        }

        if (player.getSkills().getLevel(Skills.COOKING) < dairy.level) {
            sendMessage(player, "You need a cooking level of ${dairy.level} to cook this.")
            return false
        }

        val availableAmount = player.inventory.getAmount(input.id)
        if (amount > availableAmount) amount = availableAmount
        if (amount < 1) return false

        animate()
        node = input
        return true
    }

    override fun animate() {
        player.animate(ANIMATION)
    }

    override fun reward(): Boolean {
        amount--
        for (input in dairy.inputsItems) {
            if (removeItem(player, input)) {
                player.inventory.add(dairy.product)
                if (input.id == BUCKET_OF_MILK.id) addItemOrDrop(player, BUCKET.id)

                val productName = getItemName(dairy.product.id).lowercase(Locale.getDefault())
                sendMessage(player, "You make " + (if (productName.firstOrNull()?.isVowel() == true) "an" else "a") + " $productName.")

                player.dispatch(ResourceProducedEvent(dairy.product.id, amount, input, BUCKET_OF_MILK.id))
                player.getSkills().addExperience(Skills.COOKING, dairy.experience, true)
                break
            }
        }
        return amount < 1
    }

    private fun Char.isVowel(): Boolean = this.lowercaseChar() in "aeiou"
}

enum class DairyProduct(val level: Int, val experience: Double, val product: Item, inputs: List<Int>) {
    POT_OF_CREAM(21, 18.0, Item(Items.POT_OF_CREAM_2130), listOf(Items.BUCKET_OF_MILK_1927)),
    PAT_OF_BUTTER(38, 40.5, Item(Items.PAT_OF_BUTTER_6697), listOf(Items.BUCKET_OF_MILK_1927, Items.POT_OF_CREAM_2130)),
    CHEESE(48, 64.0, Item(Items.CHEESE_1985), listOf(Items.BUCKET_OF_MILK_1927, Items.POT_OF_CREAM_2130, Items.PAT_OF_BUTTER_6697));

    val inputsItems: List<Item> = inputs.map { Item(it) }
}