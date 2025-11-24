package content.global.skill.herblore

import content.global.skill.herblore.potions.FinishedPotion
import content.global.skill.herblore.potions.GenericPotion
import content.global.skill.herblore.potions.UnfinishedPotion
import core.api.amountInInventory
import core.api.asItem
import core.game.dialogue.SkillDialogueHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import shared.consts.Items

class PotionMakeListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles creating unfinished potions
         * by combining a herb with a base vial.
         */

        onUseWith(IntType.ITEM, UNF_INGREDIENTS, *POTION_BASE) { player, used, with ->
            val unf = UnfinishedPotion.forID(used.id, with.id) ?: return@onUseWith false
            val potion = GenericPotion.transform(unf)
            val base = potion.base
            val product = potion.product ?: return@onUseWith false

            val amount = amountInInventory(player, base)

            val handler = object : SkillDialogueHandler(player, SkillDialogue.ONE_OPTION, Item(product)) {
                override fun create(amount: Int, index: Int) {
                    player.pulseManager.run(HerblorePulse(player, base.asItem(), amount, potion))
                }

                override fun getAll(index: Int): Int = amountInInventory(player, base)
            }

            if (amount == 1) {
                handler.create(0, 1)
            } else {
                handler.open()
            }

            return@onUseWith true
        }

        /*
         * Handles creating finished potions
         * by combining an unfinished potion with its secondary ingredient.
         */

        onUseWith(IntType.ITEM, INGREDIENTS, *UNF_PRODUCT) { player, used, with ->
            val (unfId, ingId) = if (used.name.contains("(unf)")) used.id to with.id else with.id to used.id

            val finished = FinishedPotion.getPotion(unfId, ingId) ?: return@onUseWith false
            val potion = GenericPotion.transform(finished)
            val base = potion.base
            val product = potion.product ?: return@onUseWith false

            val amount = amountInInventory(player, base)

            val handler = object : SkillDialogueHandler(player, SkillDialogue.ONE_OPTION, Item(product)) {
                override fun create(amount: Int, index: Int) {
                    player.pulseManager.run(HerblorePulse(player, base.asItem(), amount, potion))
                }

                override fun getAll(index: Int): Int = amountInInventory(player, base)
            }

            if (amount == 1) {
                handler.create(0, 1)
            } else {
                handler.open()
            }

            return@onUseWith true
        }
    }

    companion object {
        val UNF_PRODUCT = UnfinishedPotion.values().map { it.product }.distinct().toIntArray()
        val INGREDIENTS = FinishedPotion.values().map { it.ingredient }.distinct().toIntArray()
        val POTION_BASE = intArrayOf(Items.VIAL_OF_WATER_227, Items.COCONUT_MILK_5935)
        val UNF_INGREDIENTS = UnfinishedPotion.values().map { it.ingredient }.distinct().toIntArray()
    }
}