package content.global.skill.cooking.dairy

import core.api.*
import core.game.event.ResourceProducedEvent
import core.game.node.entity.player.Player
import core.game.node.entity.skill.SkillPulse
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.update.flag.context.Animation
import core.tools.StringUtils
import org.rs.consts.Animations
import org.rs.consts.Items
import java.util.*

/**
 * Represents a skill pulse used to create a dairy products.
 */
class DairyChurnPulse(player: Player?, item: Item?, product: DairyProduct, amount: Int) :
    SkillPulse<Item?>(player, item) {

    /**
     * The dairy product that the player is making.
     */
    private val dairy: DairyProduct

    /**
     * The amount of dairy product to be created.
     */
    private var amount: Int

    init {
        super.setDelay(8)
        this.amount = amount
        this.dairy = product
    }

    /**
     * Checks the requirements for making the dairy product.
     */
    override fun checkRequirements(): Boolean {
        closeChatBox(player)
        var hasAnyInput = false
        for (input in dairy.inputs) {
            if (inInventory(player, input!!.id)) {
                hasAnyInput = true
                node = input
                break
            }
        }
        if (!hasAnyInput) {
            sendMessage(player, "You need a bucket of milk.")
            return false
        }
        if (getStatLevel(player, Skills.COOKING) < dairy.level) {
            sendMessage(player, "You need a cooking level of ${dairy.level} to cook this.")
            return false
        }
        if (amount > player.inventory.getAmount(node)) {
            amount = player.inventory.getAmount(node)
        }
        if (amount < 1) {
            return false
        }
        animate()
        return true
    }

    /**
     * Triggers the animation.
     */
    override fun animate() {/*
         * TODO: Send Graphics (457-464)
         */
        player.animate(ANIMATION)
    }

    /**
     * Processes the reward for successfully making the dairy product.
     */
    override fun reward(): Boolean {
        amount--
        for (input in dairy.inputs) {
            if (removeItem(player, input)) {
                player.inventory.add(dairy.product)
                if (input.id == BUCKET_OF_MILK.id) {
                    addItemOrDrop(player, BUCKET.id)
                }
                val productName = getItemName(dairy.product.id).lowercase(Locale.getDefault())

                sendMessage(
                    player,
                    "You make " + (if (StringUtils.isPlusN(productName)) "an" else "a") + " " + productName + "."
                )

                player.dispatch(node?.let {
                    ResourceProducedEvent(
                        dairy.product.id, amount, it, BUCKET_OF_MILK.id
                    )
                })

                player.getSkills().addExperience(Skills.COOKING, dairy.experience, true)
                break
            }
        }

        return amount < 1
    }

    companion object {
        /**
         * The animation used for the churn action.
         */
        private val ANIMATION = Animation(Animations.CHURN_BUTTER_2793)

        /**
         * The item representing a bucket of milk.
         */
        private val BUCKET_OF_MILK = Item(Items.BUCKET_OF_MILK_1927, 1)

        /**
         * The item representing a regular bucket.
         */
        private val BUCKET = Item(Items.BUCKET_1925, 1)
    }
}