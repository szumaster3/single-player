package content.global.bots

import core.game.bots.Script
import core.game.bots.SkillingBotAssembler
import core.game.interaction.IntType
import core.game.interaction.InteractionListeners
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import shared.consts.Items
import shared.consts.Scenery

class CakeBandit : Script() {

    private var state = State.INIT
    private val stealZone = ZoneBorders(2652, 3295, 2669, 3316)
    private val bankZone = ZoneBorders(2640, 3277, 2661, 3293)
    private val bankId = Scenery.BANK_BOOTH_34752
    private val stallId = Scenery.BAKER_S_STALL_34384

    private val foodItems =
        listOf(Items.CAKE_1891, Items.BREAD_2309, Items.CHOCOLATE_SLICE_1901).map { Item(it) }

    private var nextStealTick: Long = 0
    private var lastWalkTick: Long = 0
    private val walkDelay = 1000L
    private var targetStallLoc: Location? = null
    private var targetBankLoc: Location? = null
    private var currentDestination: Location? = null

    override fun tick() {
        val now = System.currentTimeMillis()

        val hpThreshold = bot.skills.lifepoints * 0.1
        val low = bot.skills.lifepoints <= hpThreshold

        if (low || foodItems.any { bot.inventory.containsItem(it) }) {
            foodItems.firstOrNull { bot.inventory.containsItem(it) }?.let { scriptAPI.eat(it.id) }
        }

        when (state) {
            State.INIT -> handleStealState(now)
            State.TO_BANK -> handleToBankState(now)
            State.BANKING -> handleBanking()
            State.RETURN -> handleReturnState(now)
        }
    }

    private fun handleStealState(now: Long) {
        if (!stealZone.insideBorder(bot)) {
            walkToRandomLocation(stealZone, now)
            return
        }

        if (targetStallLoc == null) {
            val stallNode = scriptAPI.getNearestNode(stallId, true) ?: return
            targetStallLoc = stallNode.location
        }

        if (now < nextStealTick) return

        if (!bot.location.withinDistance(targetStallLoc!!, 1)) {
            walkTo(targetStallLoc!!, now)
        } else {
            val cakeStall = scriptAPI.getNearestNode(stallId, true) ?: return
            InteractionListeners.run(stallId, IntType.SCENERY, "steal-from", bot, cakeStall)
            nextStealTick = now + 2500
        }

        if (bot.inventory.isFull) state = State.TO_BANK
    }

    private fun handleToBankState(now: Long) {
        if (!bankZone.insideBorder(bot)) {
            walkToRandomLocation(bankZone, now)
            return
        }

        if (targetBankLoc == null) {
            val bankNode = scriptAPI.getNearestNode(bankId, true) ?: return
            targetBankLoc = bankNode.location
        }

        if (!bot.location.withinDistance(targetBankLoc!!, 1)) {
            walkTo(targetBankLoc!!, now)
        } else {
            state = State.BANKING
        }
    }

    private fun handleBanking() {
        if (bot.inventory.isEmpty()) {
            state = State.RETURN
            return
        }

        bot.inventory
            .toArray()
            .filterNotNull()
            .filter { it.id in foodItems.map { f -> f.id } }
            .forEach { bot.bank.add(it) }

        bot.inventory.clear()
        bot.fullRestore()
        state = State.RETURN
    }

    private fun handleReturnState(now: Long) {
        if (!stealZone.insideBorder(bot)) {
            walkToRandomLocation(stealZone, now)
        } else {
            currentDestination = null
            state = State.INIT
        }
    }

    private fun walkToRandomLocation(zone: ZoneBorders, now: Long) {
        walkTo(zone.randomLoc, now)
    }

    private fun walkTo(loc: Location, now: Long) {
        if (currentDestination == loc) return
        if (now - lastWalkTick < walkDelay) return
        scriptAPI.walkTo(loc)
        currentDestination = loc
        lastWalkTick = now
    }

    override fun newInstance(): Script {
        val script = CakeBandit()
        script.bot =
            SkillingBotAssembler().produce(SkillingBotAssembler.Wealth.AVERAGE, bot.startLocation)
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
