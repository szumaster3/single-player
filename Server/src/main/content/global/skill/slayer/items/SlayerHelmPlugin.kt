package content.global.skill.slayer.items

import content.global.skill.slayer.SlayerManager
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import shared.consts.Items

class SlayerHelmPlugin : InteractionListener {

    companion object {
        private const val SLAYER_HELM = Items.SLAYER_HELMET_13263
        private const val SPINY_HELM = Items.SPINY_HELMET_4551
        private val INGREDIENTS = intArrayOf(Items.NOSE_PEG_4168, Items.EARMUFFS_4166, Items.FACE_MASK_4164, Items.BLACK_MASK_8921)
    }

    override fun defineListeners() {
        onUseWith(IntType.ITEM, SPINY_HELM, *INGREDIENTS) { player, _, _ ->
            craftHelm(player)
            return@onUseWith true
        }

        on(SLAYER_HELM, IntType.ITEM, "disassemble") { player, _ ->
            disassembleHelm(player)
            return@on true
        }
    }

    private fun craftHelm(player: Player) {
        val sm = SlayerManager.getInstance(player)
        if (getStatLevel(player, Skills.CRAFTING) < 55) {
            sendMessage(player, "You need Crafting level 55 to do this.")
            return
        }
        if (!sm.flags.isHelmUnlocked()) {
            sendMessage(player, "You need to unlock this ability first.")
            return
        }
        if (!anyInInventory(player, *INGREDIENTS)) {
            sendMessage(player, "You need all required ingredients to craft a Slayer helm.")
            return
        }
        player.lock(1)
        if (removeItem(player, SPINY_HELM)) {
            for (id in INGREDIENTS) removeItem(player, id)
            addItemOrDrop(player, SLAYER_HELM)
            sendMessage(player, "You combine the items into a Slayer helm.")
        }
    }

    private fun disassembleHelm(player: Player) {
        if (freeSlots(player) < 4) {
            sendMessage(player, "You don't have enough inventory space.")
            return
        }
        player.lock(1)
        if (removeItem(player, SLAYER_HELM)) {
            for (id in INGREDIENTS) addItemOrDrop(player, id)
            addItemOrDrop(player, SPINY_HELM)
            sendMessage(player, "You disassemble your Slayer helm.")
        }
    }
}
