package content.global.bots

import core.game.bots.Script
import core.game.bots.SkillingBotAssembler
import core.game.interaction.DestinationFlag
import core.game.interaction.IntType
import core.game.interaction.InteractionListeners
import core.game.interaction.MovementPulse
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.system.task.Pulse
import core.game.world.map.zone.ZoneBorders
import shared.consts.Items
import shared.consts.Scenery

class CakeBandit : Script() {

    private var state = State.INIT
    private val stealZone = ZoneBorders(2652, 3295, 2669, 3316)
    private val bankZone = ZoneBorders(2640, 3277, 2661, 3293)
    private val bankId = Scenery.BANK_BOOTH_34752
    private val stallId = Scenery.BAKER_S_STALL_34384
    private val foodIds = listOf(Items.CAKE_1891, Items.BREAD_2309, Items.CHOCOLATE_SLICE_1901)
    private var nextStealTick: Long = 0

    override fun tick() {
        for (id in foodIds) {
            if (bot.inventory.containsItem(Item(id))) {
                scriptAPI.eat(id)
                break
            }
        }

        when (state) {
            State.INIT -> handleStealState()
            State.TO_BANK -> goToBank()
            State.BANKING -> handleBanking()
            State.RETURN -> returnToStealZone()
        }
    }

    private fun handleStealState() {
        if (!stealZone.insideBorder(bot)) {
            scriptAPI.walkTo(stealZone.randomLoc)
            return
        }

        val cakeStall = scriptAPI.getNearestNode(stallId, true) ?: return
        val now = System.currentTimeMillis()

        val destinationReached =
            bot.destinationFlag.getDestination(bot, cakeStall)
                ?.withinDistance(cakeStall.location) == true

        if (now < nextStealTick) return

        if (!destinationReached) {
            bot.pulseManager.run(
                object : MovementPulse(bot, cakeStall, DestinationFlag.OBJECT) {
                    override fun pulse(): Boolean {
                        steal(cakeStall)
                        return true
                    }
                }
            )
        } else {
            steal(cakeStall)
        }

        if (bot.inventory.isFull) state = State.TO_BANK
    }

    private fun steal(cakeStall: core.game.node.Node) {
        InteractionListeners.run(stallId, IntType.SCENERY, "steal-from", bot, cakeStall)
        nextStealTick = System.currentTimeMillis() + (3 * 600) // 3 ticki
    }

    private fun goToBank() {
        if (!bankZone.insideBorder(bot)) {
            scriptAPI.walkTo(bankZone.randomLoc)
        } else {
            val bank = scriptAPI.getNearestNode(bankId, true) ?: return
            bot.pulseManager.run(
                object : MovementPulse(bot, bank, DestinationFlag.OBJECT) {
                    override fun pulse(): Boolean {
                        state = State.BANKING
                        return true
                    }
                }
            )
        }
    }

    private fun handleBanking() {
        val bank = scriptAPI.getNearestNode(bankId, true) ?: return
        bot.pulseManager.run(object : Pulse(1) {
            private val itemsToBank = bot.inventory.toArray().filterNotNull().filter { it.id in foodIds }.toMutableList()

            override fun pulse(): Boolean {
                if (itemsToBank.isEmpty()) {
                    bot.fullRestore()
                    state = State.RETURN
                    return true
                }

                val item = itemsToBank.removeFirst()
                bot.bank.add(item)
                bot.inventory.remove(item)
                return false
            }
        })
    }

    private fun returnToStealZone() {
        if (!stealZone.insideBorder(bot)) {
            scriptAPI.walkTo(stealZone.randomLoc)
        } else {
            state = State.INIT
        }
    }

    override fun newInstance(): Script {
        val script = CakeBandit()
        script.bot =
            SkillingBotAssembler()
                .produce(SkillingBotAssembler.Wealth.AVERAGE, bot.startLocation)
        return script
    }

    init {
        skills[Skills.ATTACK] = 25
        skills[Skills.STRENGTH] = 45
        skills[Skills.DEFENCE] = 35
        skills[Skills.THIEVING] = 25
    }

    enum class State {
        INIT,
        TO_BANK,
        BANKING,
        RETURN
    }
}
