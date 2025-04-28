package content.minigame.pestcontrol.handlers.bots

import content.minigame.pestcontrol.handlers.PCHelper
import content.minigame.pestcontrol.handlers.PestControlActivityPlugin
import core.game.bots.CombatBotAssembler
import core.game.bots.PvMBots
import core.game.interaction.IntType
import core.game.interaction.InteractionListeners
import core.game.world.map.Location
import core.tools.RandomFunction
import kotlin.random.Random

/**
 * A novice-level Pest Control bot that participates in the Pest Control minigame.
 */
class PestControlNoviceBot(
    l: Location,
) : PvMBots(legitimizeLocation(l)) {

    var tick = 0
    var combatMoveTimer = 0
    var justStartedGame = true
    var movetimer = 0
    var openedGate = false
    var myCounter = 0
    private val num = Random.nextInt(4)
    private val myBoat = PCHelper.BoatInfo.NOVICE
    private val combathandler = CombatState(this)
    var time = 0
    private var switch = false
    private var insideBoatWalks = 3

    /**
     * Represents the different states the bot can be in during Pest Control gameplay.
     */
    enum class State {
        REFRESH, OUTSIDE_GANGPLANK, WAITING_IN_BOAT, PLAY_GAME, GET_TO_PC, HANDLE_GATE,
    }

    init {
        if (Random.nextInt(100) < 30) {
            setAttribute("pc_role", "defend_squire")
        } else {
            setAttribute("pc_role", "attack_portals")
            this.customState = "Fighting NPCs"
        }
        if (num <= 2) {
            CombatBotAssembler().gearPCnMeleeBot(this)
        } else {
            CombatBotAssembler().gearPCnRangedBot(this, Random.nextBoolean())
        }
    }

    /**
     * Called every game tick. Handles moving between states and performing actions based on current state.
     */
    override fun tick() {
        super.tick()
        tick++
        time++
        movetimer--
        if (movetimer <= 0) {
            movetimer = 0
            customState = state.toString()
            when (state) {
                State.GET_TO_PC, State.REFRESH -> handlePestControl()
                State.OUTSIDE_GANGPLANK -> handleBoat()
                State.WAITING_IN_BOAT -> handleBoatIdle()
                State.PLAY_GAME -> handleAttack()
                State.HANDLE_GATE -> handleGate()
            }
        }
    }

    /**
     * Returns the current state of the bot based on its location and internal timers.
     */
    val state: State
        get() {
            return when {
                PCHelper.isInPestControlInstance(this) && openedGate -> State.HANDLE_GATE
                PCHelper.landerContainsLoc(this.location) -> State.WAITING_IN_BOAT
                PCHelper.isInPestControlInstance(this) -> State.PLAY_GAME
                PCHelper.outsideGangplankContainsLoc(this.location) -> State.OUTSIDE_GANGPLANK
                else -> State.GET_TO_PC
            }
        }

    /**
     * Handles combat behavior when the bot is inside the Pest Control game instance.
     */
    private fun handleAttack() {
        if (PCHelper.outsideGangplankContainsLoc(location)) {
            PestControlActivityPlugin().leave(this, false)
            getClosestNodeWithEntry(50, myBoat.ladderId)?.let { test ->
                test.interaction.handle(this, test.interaction[0])
            } ?: println("PC: Gangplank Null")
        }

        walkingQueue.isRunning = true

        if (getAttribute("pc_role", "E") == "attack_portals") {
            combathandler.handlePortal()
        } else {
            movetimer = RandomFunction.random(2, 10)
            randomWalkAroundPoint(PCHelper.getMyPestControlSession1(this)?.squire?.location ?: location, 5)
            combathandler.handleAttack()
        }
    }

    /**
     * Handles idle behavior while waiting inside the Pest Control boat.
     */
    private fun handleBoatIdle() {
        justStartedGame = true
        openedGate = false
        time = 0

        if (prayer.active.isNotEmpty()) {
            prayer.reset()
        }

        if (PCHelper.outsideGangplankContainsLoc(location)) {
            getClosestNodeWithEntry(15, myBoat.ladderId)?.let { test ->
                test.interaction.handle(this, test.interaction[0])
            }
            handleBoat()
            return
        }

        if (Random.nextInt(100) < 40) {
            if (Random.nextInt(insideBoatWalks) <= 1) {
                insideBoatWalks = (insideBoatWalks * 1.5).toInt()
                if (Random.nextInt(4) == 1) {
                    walkingQueue.isRunning = !walkingQueue.isRunning
                }
                if (Random.nextInt(7) >= 4) {
                    walkToPosSmart(myBoat.boatBorder.randomLoc!!)
                }
            }
            if (Random.nextInt(3) == 1) {
                insideBoatWalks += 2
            }
        }
    }

    /**
     * Handles behavior when the bot is outside the boat but not yet inside the game.
     */
    private fun handleBoat() {
        if (PCHelper.outsideGangplankContainsLoc(location)) {
            movetimer = Random.nextInt(10)
            combathandler.randomWalkTo(PCHelper.PestControlLanderNovice, 1)
        }

        if (prayer.active.isNotEmpty()) {
            prayer.reset()
        }

        if (Random.nextInt(8) >= 4) {
            combathandler.randomWalkTo(Location(2658, 2659, 0), 12)
            movetimer = Random.nextInt(300) + 30
        }

        if (Random.nextInt(8) >= 2) {
            randomWalk(3, 3)
            movetimer = Random.nextInt(10)
        }

        if (Random.nextInt(100) > 50 && Random.nextInt(10) <= 5) {
            walkToPosSmart(myBoat.outsideBoatBorder.randomLoc!!)
            movetimer += RandomFunction.normalPlusWeightRandDist(400, 200)
        }

        movetimer = RandomFunction.normalPlusWeightRandDist(100, 50)

        getClosestNodeWithEntry(15, myBoat.ladderId)?.let { test ->
            test.interaction.handle(this, test.interaction[0])
        } ?: randomWalk(1, 1)

        insideBoatWalks = 3
    }

    /**
     * Handles teleportation and boarding when the bot needs to return to the Pest Control area.
     */
    private fun handlePestControl() {
        time = 0
        if (!switch) {
            teleport(PCHelper.PestControlLanderNovice)
            switch = true
        } else {
            getClosestNodeWithEntry(30, myBoat.ladderId)?.let { test ->
                switch = false
                test.interaction.handle(this, test.interaction[0])
            } ?: run {
                switch = false
                teleport(PCHelper.PestControlLanderNovice)
            }
        }
    }

    /**
     * Handles the opening of gates if needed.
     */
    private fun handleGate() {
        if (PCHelper.isInPestControlInstance(this)) {
            return
        }

        val gate = getClosestNodeWithEntry(75, PCHelper.GATE_ENTRIES)

        if (gate != null) {
            customState = "OPENING_GATE ID=${gate.id}"
            InteractionListeners.run(gate.id, IntType.SCENERY, "open", this, gate)
            openedGate = true

            movetimer = Random.nextInt(2) + 1
            customState = "Gate Opened, Transitioning to Play Game"
        }

        if (openedGate) {
            State.PLAY_GAME
        }
    }

    companion object {
        /**
         * Ensures that the given location is valid for a Pest Control novice bot.
         *
         * @param l The original location.
         * @return A corrected location inside the Pest Control novice lander if needed.
         */
        fun legitimizeLocation(l: Location): Location =
            if (PCHelper.landerContainsLoc(l)) Location(2660, 2648, 0) else l
    }
}
