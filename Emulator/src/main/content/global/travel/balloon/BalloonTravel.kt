package content.global.travel.balloon

import content.data.GameAttributes
import core.api.*
import core.api.quest.isQuestComplete
import core.api.ui.setMinimapState
import core.cache.def.impl.ItemDefinition
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.interaction.QueueStrength
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.info.Rights
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import org.rs.consts.*

/**
 * Handles the balloon travel system.
 */
class BalloonTravel : InterfaceListener, InteractionListener {

    init {
        assistants.forEach { (npcId, location) ->
            NPC(npcId, location).apply {
                init()
                isWalks = true
            }
        }
    }

    companion object {
        /**
         * Represents the assistant npc.
         */
        private val assistantIds = (5049..5057).toIntArray()

        /**
         * Represents the basket ids.
         */
        private val basketIds = intArrayOf(Scenery.BASKET_19128, Scenery.BASKET_19129)

        /**
         * Represents assistant npcs and their spawn locations.
         */
        private val assistants = mapOf(
            NPCs.AUGUSTE_5050 to Location.create(2938, 3424, 0),
            NPCs.ASSISTANT_SERF_5053 to Location.create(3298, 3484, 0),
            NPCs.ASSISTANT_LE_SMITH_5056 to Location.create(2480, 3458, 0),
            NPCs.ASSISTANT_STAN_5057 to Location.create(2938, 3424, 0)
        )

        /**
         * Opens the interface.
         */
        private fun openBalloonInterface(player: Player, location: Balloons) {
            player.setAttribute(GameAttributes.BALLOON_ORIGIN, location)
            openInterface(player, Components.ZEP_BALLOON_MAP_469)
            setComponentVisibility(player, Components.ZEP_BALLOON_MAP_469, location.componentId, false)
        }

        fun handleFlight(player: Player, destination: Balloons) {
            val origin = player.getAttribute<Balloons>(GameAttributes.BALLOON_ORIGIN)
            if (origin == null) {
                player.debug("null location.")
                return
            }

            lock(player, 7)
            lockInteractions(player, 7)

            val animationId = Balloons.getAnimationId(origin, destination)
            val animationTime = Animation(animationId).duration

            playJingle(player, 118)

            closeInterface(player)
            setMinimapState(player, 2)
            openInterface(player, Components.FADE_TO_BLACK_120)
            sendMessage(player, "You board the balloon and fly to ${destination.areaName}.")
            queueScript(player, 4, QueueStrength.SOFT) { ticks: Int ->
                when (ticks) {
                    0 -> {
                        openInterface(player, Components.ZEP_BALLOON_MAP_469)
                        setComponentVisibility(player, Components.ZEP_BALLOON_MAP_469, 12, false)
                        animateInterface(player, Components.ZEP_BALLOON_MAP_469, 12, animationId)
                        player.teleport(destination.destination)
                        return@queueScript delayScript(player, animationTime)
                    }

                    1 -> {
                        unlock(player)
                        closeInterface(player)
                        setMinimapState(player, 0)
                        openOverlay(player, Components.FADE_FROM_BLACK_170)
                        removeAttribute(player, GameAttributes.BALLOON_ORIGIN)
                        sendDialogue(player, "You arrive safely in ${destination.areaName}.")
                        return@queueScript stopExecuting(player)
                    }

                    else -> return@queueScript stopExecuting(player)
                }
            }
        }
    }

    override fun defineInterfaceListeners() {
        on(Components.ZEP_BALLOON_MAP_469) { player, _, _, buttonID, _, _ ->
            val destination = Balloons.fromButtonId(buttonID) ?: return@on true
            val isAdmin = player.rights == Rights.ADMINISTRATOR

            //TODO: You can open new locations from Entrana.

            if (!hasLevelStat(player, Skills.FIREMAKING, destination.requiredLevel)) {
                sendDialogue(player, "You require a Firemaking level of ${destination.requiredLevel} to travel to ${destination.areaName}.")
                return@on true
            }

            val origin = player.getAttribute<Balloons>(GameAttributes.BALLOON_ORIGIN)
            if (origin == destination) {
                sendDialogue(player, "You can't fly to the same location.")
                return@on true
            }

            if (player.familiarManager.hasFamiliar()) {
                sendMessage(player, "You can't take a follower on a ride.")
                return@on true
            }

            if (destination == Balloons.ENTRANA) {
                if (!isAdmin && !ItemDefinition.canEnterEntrana(player)) {
                    sendDialogue(player, "You can't take flight with weapons and armour to Entrana.")
                    return@on true
                } else {
                    sendMessage(player, "You are quickly searched.")
                }
            }

            if (isAdmin) {
                handleFlight(player, destination)
                return@on true
            }

            if (removeItem(player, Item(destination.logId, 1))) {
                handleFlight(player, destination)
                if (destination == Balloons.VARROCK) {
                    finishDiaryTask(player, DiaryType.VARROCK, 2, 17)
                }
            } else {
                val requiredItem = getItemName(destination.logId).lowercase().removeSuffix("s").trim()
                sendDialogue(player, "You need at least one $requiredItem.")
            }
            return@on true
        }
    }

    override fun defineListeners() {

        /*
         * Handles interaction with basket scenery.
         */

        on(basketIds, IntType.SCENERY, "use") { player, node ->
            val sceneryId = node.asScenery().wrapper.id
            val location = Balloons.fromSceneryId(sceneryId)
            if (location != null) {
                openBalloonInterface(player, location)
            }
            return@on true
        }

        /*
         * Handles interaction with npc.
         */

        on(assistantIds, IntType.NPC, "fly") { player, node ->
            if (!isQuestComplete(player, Quests.ENLIGHTENED_JOURNEY)) {
                sendMessage(player, "You must complete ${Quests.ENLIGHTENED_JOURNEY} before you can use it.")
                return@on true
            }
            val sceneryId = node.asScenery().wrapper.id
            val location = Balloons.fromSceneryId(sceneryId)
            if (location != null) {
                openBalloonInterface(player, location)
            }
            return@on true
        }
    }
}

/**
 * Enum class representing balloon travel destinations and their associated metadata.
 */
enum class Balloons(val areaName: String, val destination: Location, val logId: Int, val requiredLevel: Int, val varbitId: Int, val componentId: Int, val button: Int, val wrapperId: List<Int> = emptyList()) {
    ENTRANA("Entrana", Location(2809, 3356), Items.LOGS_1511, 20, Vars.VARBIT_QUEST_ENLIGHTENED_JOURNEY_ENTRANA_BALLOON_2867, 25, 17, listOf(19133)),
    TAVERLEY("Taverley", Location(2940, 3420), Items.LOGS_1511, 20, Vars.VARBIT_QUEST_ENLIGHTENED_JOURNEY_TAVERLEY_BALLOON_2868, 22, 18, listOf(19135)),
    CRAFT_GUILD("Crafting Guild", Location(2924, 3303), Items.OAK_LOGS_1521, 30, Vars.VARBIT_QUEST_ENLIGHTENED_JOURNEY_CRAFTING_GUILD_BALLOON_2871, 20, 16, listOf(19141)),
    VARROCK("Varrock", Location(3298, 3481), Items.WILLOW_LOGS_1519, 40, Vars.VARBIT_QUEST_ENLIGHTENED_JOURNEY_VARROCK_BALLOON_2872, 21, 19, listOf(19143)),
    CASTLE_WARS("Castle Wars", Location(2462, 3108), Items.YEW_LOGS_1515, 50, Vars.VARBIT_QUEST_ENLIGHTENED_JOURNEY_CASTLE_WARS_BALLOON_2869, 24, 14, listOf(19137)),
    GRAND_TREE("Grand Tree", Location(2480, 3458), Items.MAGIC_LOGS_1513, 60, Vars.VARBIT_QUEST_ENLIGHTENED_JOURNEY_GRAND_TREE_BALLOON_2870, 23, 15, listOf(19139));

    companion object {
        /**
         * A button map id for balloon locations.
         */
        private val buttonToBalloon: Map<Int, Balloons> by lazy {
            values().associateBy { it.button }
        }

        /**
         * Returns the balloon location to the given interface button id.
         *
         * @param buttonId the ID of the interface button
         * @return the matching [Balloons] instance or null if no match is found
         */
        fun fromButtonId(buttonId: Int): Balloons? = buttonToBalloon[buttonId]

        /**
         * A map that associates scenery object id with their balloon locations.
         */
        private val sceneryToBalloon: Map<Int, Balloons> by lazy {
            values().flatMap { balloon -> balloon.wrapperId.map { it to balloon } }.toMap()
        }

        /**
         * Returns the balloon location to the given scenery id.
         *
         * @param id the ID of the scenery object
         * @return the matching [Balloons] instance or null if no match is found
         */
        fun fromSceneryId(id: Int): Balloons? = sceneryToBalloon[id]

        /**
         * Animation map.
         */
        private val animationMap: Map<Pair<Balloons, Balloons>, Int> = mapOf(
            Pair(ENTRANA, TAVERLEY) to 5110,
            Pair(TAVERLEY, ENTRANA) to 5111,
            Pair(ENTRANA, CRAFT_GUILD) to 5112,
            Pair(CRAFT_GUILD, ENTRANA) to 5113,
            Pair(ENTRANA, VARROCK) to 5114,
            Pair(VARROCK, ENTRANA) to 5115,
            Pair(ENTRANA, GRAND_TREE) to 5116,
            Pair(GRAND_TREE, ENTRANA) to 5117,
            Pair(ENTRANA, CASTLE_WARS) to 5118,
            Pair(CASTLE_WARS, ENTRANA) to 5119,
            Pair(VARROCK, CRAFT_GUILD) to 5120,
            Pair(CRAFT_GUILD, VARROCK) to 5121,
            Pair(VARROCK, TAVERLEY) to 5122,
            Pair(TAVERLEY, VARROCK) to 5123,
            Pair(TAVERLEY, CRAFT_GUILD) to 5124,
            Pair(CRAFT_GUILD, TAVERLEY) to 5125,
            Pair(TAVERLEY, CASTLE_WARS) to 5126,
            Pair(CASTLE_WARS, TAVERLEY) to 5127,
            Pair(CRAFT_GUILD, CASTLE_WARS) to 5128,
            Pair(CASTLE_WARS, CRAFT_GUILD) to 5129,
            Pair(VARROCK, CASTLE_WARS) to 5130,
            Pair(CASTLE_WARS, VARROCK) to 5131,
            Pair(GRAND_TREE, CASTLE_WARS) to 5132,
            Pair(CASTLE_WARS, GRAND_TREE) to 5133,
            Pair(GRAND_TREE, CRAFT_GUILD) to 5134,
            Pair(CRAFT_GUILD, GRAND_TREE) to 5135,
            Pair(TAVERLEY, GRAND_TREE) to 5136,
            Pair(GRAND_TREE, TAVERLEY) to 5137,
            Pair(VARROCK, GRAND_TREE) to 5138,
            Pair(GRAND_TREE, VARROCK) to 5139,
        )

        /**
         * Gets the animation id.
         *
         * @param from The origin balloon location.
         * @param to The destination balloon location.
         * @return The animation id.
         */
        fun getAnimationId(from: Balloons, to: Balloons): Int {
            return animationMap[Pair(from, to)] ?: error("No animation for route [$from] -> [$to]")
        }
    }
}