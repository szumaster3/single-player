package content.global.skill.crafting.items.armour.snakeskin

import content.global.skill.crafting.items.leather.ThreadUtils
import core.api.*
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.SkillPulse
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import org.rs.consts.Animations
import org.rs.consts.Components
import org.rs.consts.Items

class SnakeskinCraftingPlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles crafting the snakeskin.
         */

        onUseWith(IntType.ITEM, Items.NEEDLE_1733, Items.SNAKESKIN_6289) { player, used, _ ->
            sendString(player, "Which snakeskin item would you like to make?", Components.SKILL_MAKE_306, 27)
            sendSkillDialogue(player) {
                withItems(Items.SNAKESKIN_BODY_6322, Items.SNAKESKIN_CHAPS_6324, Items.SNAKESKIN_VBRACE_6330, Items.SNAKESKIN_BANDANA_6326, Items.SNAKESKIN_BOOTS_6328)
                create { id, amount ->
                    val item = Snakeskin.forId(id)
                    item?.let {
                        submitIndividualPulse(
                            entity = player,
                            pulse = SnakeskinCraftingPulse(player, null, amount, it),
                        )
                    } ?: sendMessage(player, "Invalid snakeskin item selected.")
                }
                calculateMaxAmount {
                    amountInInventory(player, used.id)
                }
            }
            return@onUseWith true
        }
    }
}

private class SnakeskinCraftingPulse(
    player: Player?,
    node: Item?,
    var amount: Int,
    val skin: Snakeskin,
) : SkillPulse<Item?>(player, node) {
    private var ticks = 0

    override fun checkRequirements(): Boolean {
        if (getStatLevel(player, Skills.CRAFTING) < skin.level) {
            sendDialogue(player, "You need a crafting level of ${skin.level} to make this.")
            return false
        }

        if (!inInventory(player, Items.NEEDLE_1733, 1)) {
            sendDialogue(player, "You need a needle to make this.")
            return false
        }
        if (!inInventory(player, Items.THREAD_1734, 1)) {
            sendDialogue(player, "You need thread to make this.")
            return false
        }
        if (!inInventory(player, Items.SNAKESKIN_6289, skin.amount)) {
            sendDialogue(player, "You need ${skin.amount} snakeskins to do this.")
            return false
        }

        closeInterface(player)
        return true
    }

    override fun animate() {
        if (ticks % 5 == 0) {
            animate(player, ANIMATION)
        }
    }

    override fun reward(): Boolean {
        if (++ticks % 5 != 0) {
            return false
        }

        if (removeItem(player, Item(Items.SNAKESKIN_6289, skin.amount))) {
            val craftedItem = Item(skin.product, amount)
            player.inventory.add(craftedItem)
            rewardXP(player, Skills.CRAFTING, skin.experience)

            ThreadUtils.decayThread(player)
            if (ThreadUtils.isLastThread(player)) {
                ThreadUtils.removeThread(player)
            }
        }

        amount--
        return amount < 1
    }

    companion object {
        private const val ANIMATION = Animations.CRAFT_LEATHER_1249
    }
}