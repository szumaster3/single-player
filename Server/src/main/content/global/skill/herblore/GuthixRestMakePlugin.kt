package content.global.skill.herblore

import core.api.getStatLevel
import core.api.hasRequirement
import core.api.rewardXP
import core.api.sendMessage
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import shared.consts.Items
import shared.consts.Quests

class GuthixRestMakePlugin : InteractionListener {

    override fun defineListeners() {
        onUseWith(IntType.ITEM, herbsArray, *teaMixes) { player, used, base ->
            handleMix(player, used.asItem(), base.asItem())
            return@onUseWith true
        }
    }

    private fun handleMix(player: Player, from: Item, to: Item): Boolean {
        if (!hasRequirement(player, Quests.DRUIDIC_RITUAL) || !hasRequirement(player, Quests.ONE_SMALL_FAVOUR)) return false
        if (getStatLevel(player, Skills.HERBLORE) < 18) {
            sendMessage(player, "You need a Herblore level of at least 18 to mix a Guthix Rest Tea.")
            return false
        }

        val (herb, mix) = if (from.id in herbIds) from to to else to to from
        val existingIngredients = PartialTea.byTeaId[mix.id]?.ingredients ?: emptySet()
        val newIngredients = existingIngredients + herb.id

        val upgradedTea = PartialTea.byIngredients[newIngredients]
            ?: return player.sendMessage("Nothing interesting happens.").let { false }

        val mixSlot = mix.slot
        player.inventory.replace(Item(upgradedTea.product), mixSlot, true)

        val herbSlot = herb.slot
        player.inventory.remove(herb, herbSlot, true)

        sendMessage(player, "You place the ${herb.name.lowercase().replace(" leaf", "")} into the steamy mixture" +
                if (upgradedTea == PartialTea.COMPLETE_MIX) " and make Guthix Rest Tea." else ".")

        rewardXP(player, Skills.HERBLORE, 13.5 + newIngredients.size * 0.5)
        return true
    }

    companion object {
        private val herbIds = setOf(Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251, Items.CLEAN_HARRALANDER_255)
        val herbsArray = herbIds.toIntArray()
        val teaMixes = PartialTea.values().map { it.product }.toIntArray() + Items.CUP_OF_HOT_WATER_4460
    }

    private enum class PartialTea(val ingredients: Set<Int>, val product: Int) {
        HERB_TEA_MIX_1(setOf(Items.CLEAN_HARRALANDER_255), Items.HERB_TEA_MIX_4464),
        HERB_TEA_MIX_2(setOf(Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4466),
        HERB_TEA_MIX_3(setOf(Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4468),
        HERB_TEA_MIX_4(setOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4470),
        HERB_TEA_MIX_5(setOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4472),
        HERB_TEA_MIX_6(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4474),
        HERB_TEA_MIX_7(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4476),
        HERB_TEA_MIX_8(setOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_MARRENTILL_251, Items.CLEAN_GUAM_249), Items.HERB_TEA_MIX_4478),
        HERB_TEA_MIX_9(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251), Items.HERB_TEA_MIX_4480),
        HERB_TEA_MIX_10(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_HARRALANDER_255), Items.HERB_TEA_MIX_4482),
        COMPLETE_MIX(setOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251, Items.CLEAN_HARRALANDER_255), Items.GUTHIX_REST3_4419);

        companion object {
            val byIngredients: Map<Set<Int>, PartialTea> = values().associateBy { it.ingredients }
            val byTeaId: Map<Int, PartialTea> = values().associateBy { it.product }
        }
    }
}