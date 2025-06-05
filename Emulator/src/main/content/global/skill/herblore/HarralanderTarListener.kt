package content.global.skill.herblore

import core.api.amountInInventory
import core.api.ui.repositionChild
import core.game.dialogue.SkillDialogueHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import org.rs.consts.Components
import org.rs.consts.Items

class HarralanderTarListener : InteractionListener {
    val tar = TarItem.values().map(TarItem::ingredient).toIntArray()

    override fun defineListeners() {
        onUseWith(IntType.ITEM, tar, Items.SWAMP_TAR_1939) { player, used, _ ->
            var tar = TarItem.forId(used.id)
            val handler: SkillDialogueHandler =
                object : SkillDialogueHandler(player, SkillDialogue.ONE_OPTION, Item(tar!!.product)) {
                    override fun create(
                        amount: Int,
                        index: Int,
                    ) {
                        player.pulseManager.run(
                            tar?.let { HarralanderTarPulse(player, null, it, amount) },
                        )
                    }

                    override fun getAll(index: Int): Int = amountInInventory(player, used.id)
                }
            handler.open()
            repositionChild(player, Components.SKILL_MULTI1_309, 2, 210, 15)
            return@onUseWith true
        }
    }
}
