package content.global.bots

import core.api.item.produceGroundItem
import core.game.bots.Script
import core.game.bots.SkillingBotAssembler
import core.game.interaction.IntType
import core.game.interaction.InteractionListeners
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import org.rs.consts.Items
import org.rs.consts.NPCs

class BarbarianSalmon : Script() {
    private var ticks = 0L

    override fun tick() {
        val now = System.currentTimeMillis()
        if (now < ticks) return

        val fishingSpot = scriptAPI.getNearestNode(NPCs.FISHING_SPOT_309, false)
        if (fishingSpot != null && !bot.inventory.isFull) {
            InteractionListeners.run(fishingSpot.id, IntType.NPC, "lure", bot, fishingSpot)
        }
        if (bot.inventory.contains(Items.RAW_SALMON_331, 5)) {
            produceGroundItem(bot, Items.RAW_SALMON_331, 5, bot.location)
            bot.inventory.remove(Item(Items.RAW_SALMON_331, 5))
        }
        if (bot.inventory.contains(Items.RAW_TROUT_335, 5)) {
            produceGroundItem(bot, Items.RAW_TROUT_335, 5, bot.location)
            bot.inventory.remove(Item(Items.RAW_TROUT_335, 5))
        }
        if (bot.inventory.isFull &&
            (
                    !bot.inventory.containsAtLeastOneItem(Items.RAW_SALMON_331) ||
                            !bot.inventory.containsAtLeastOneItem(Items.RAW_TROUT_335)
                    )
        ) {
            bot.inventory.clear()
            bot.inventory.add(Item(Items.FLY_FISHING_ROD_309))
            bot.inventory.add(Item(Items.FEATHER_314, 10000))
        }

        ticks = now + 3000
    }

    override fun newInstance(): Script {
        val script = BarbarianSalmon()
        script.bot = SkillingBotAssembler().produce(SkillingBotAssembler.Wealth.values().random(), bot.startLocation)
        return script
    }

    init {
        skills[Skills.FISHING] = 55
        inventory.add(Item(Items.FLY_FISHING_ROD_309))
        inventory.add(Item(Items.FEATHER_314, 10000))
    }
}
