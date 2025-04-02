package content.global.travel.charter

import core.api.*
import core.api.quest.isQuestComplete
import core.game.global.action.ClimbActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.interaction.QueueStrength
import core.game.node.entity.impl.ForceMovement
import core.game.node.entity.player.Player
import core.game.world.map.Location
import core.game.world.repository.Repository.findNPC
import core.game.world.update.flag.context.Animation
import org.rs.consts.*

class CharterListener :
    InteractionListener,
    InterfaceListener {
    override fun defineListeners() {
        on(SAILORS, IntType.NPC, "charter") { player, _ ->
            CharterUtils.open(player)
            return@on true
        }

        /*
         * Handles ladder on Dragon slayer ship.
         */

        on(Scenery.LADDER_2592, IntType.SCENERY, "climb-up") { player, _ ->
            ClimbActionHandler.climb(player, Animation(Animations.USE_LADDER_828), Location(3048, 3208, 1))
            return@on true
        }

        /*
         * Handles ladder on Dragon slayer ship.
         */

        on(Scenery.LADDER_2590, IntType.SCENERY, "climb-down") { player, _ ->
            ClimbActionHandler.climb(player, Animation(Animations.MULTI_BEND_OVER_827), Location(3048, 9640, 1))
            return@on true
        }

        on(Scenery.GANGPLANK_69, IntType.SCENERY, "cross") { player, _ ->
            player.dialogueInterpreter.open(NPCs.ARHEIN_563, findNPC(NPCs.ARHEIN_563), true)
            return@on true
        }

        on(Scenery.GANGPLANK_2593, IntType.SCENERY, "cross") { player, _ ->
            if (isQuestComplete(player, Quests.DRAGON_SLAYER)) {
                player.dialogueInterpreter.open(NPCs.KLARENSE_744, findNPC(NPCs.KLARENSE_744), true)
                return@on true
            }
            if (!player.getSavedData().questData.getDragonSlayerAttribute("ship")) {
                openDialogue(player, NPCs.KLARENSE_744, findNPC(NPCs.KLARENSE_744)!!, true)
            } else {
                sendMessage(player, "You board the ship.")
                cross(player, Location(3047, 3207, 1))
            }
            return@on true
        }

        on(Scenery.GANGPLANK_11209, IntType.SCENERY, "cross") { player, _ ->
            sendDialogueLines(
                player,
                "I don't think that whoever owns this ship will be happy",
                "with me wandering all over it.",
            )
            return@on true
        }

        on(GANGPLANKS, IntType.SCENERY, "cross") { player, node ->
            lock(player, 1)
            ForceMovement.run(
                player,
                player.location,
                node.location,
                ForceMovement.WALK_ANIMATION,
                ForceMovement.WALKING_SPEED,
            )
            if (getUsedOption(player) == "cross") {
                when (node.id) {
                    // Karamja A
                    Scenery.GANGPLANK_2081 -> cross(player, Location(2956, 3143, 1))
                    // Karamja B
                    Scenery.GANGPLANK_2082 -> cross(player, Location(2956, 3146, 0))
                    // Port Sarim A
                    Scenery.GANGPLANK_2083 -> cross(player, Location(3032, 3217, 1))
                    // Port Sarim B
                    Scenery.GANGPLANK_2084 -> cross(player, Location(3029, 3217, 0))
                    // Ardougne A
                    Scenery.GANGPLANK_2085 ->
                        cross(player, Location(2683, 3268, 1)).also {
                            sendMessage(
                                player,
                                "You must speak to Captain Barnaby before it will set sail.",
                            )
                        }
                    // Ardougne B
                    Scenery.GANGPLANK_2086 -> cross(player, Location(2683, 3271, 0))
                    // Brimhaven A
                    Scenery.GANGPLANK_2087 ->
                        cross(player, Location(2775, 3234, 1)).also {
                            sendMessage(
                                player,
                                "You must speak to the Customs Officer before it will set sail.",
                            )
                        }
                    // Brimhaven B
                    Scenery.GANGPLANK_2088 -> cross(player, Location(2772, 3234, 0))
                    // Port Sarim A
                    Scenery.GANGPLANK_2412 -> cross(player, Location(3048, 3231, 1))
                    // Port Sarim B
                    Scenery.GANGPLANK_2413 -> cross(player, Location(3048, 3234, 0))
                    // Entrana A
                    Scenery.GANGPLANK_2414 -> cross(player, Location(2834, 3331, 1))
                    // Entrana B
                    Scenery.GANGPLANK_2415 -> cross(player, Location(2834, 3335, 0))
                    // Port Sarim (Dragon Slayer)
                    Scenery.GANGPLANK_2594 -> cross(player, Location(3047, 3204, 0))
                    // Moss A
                    Scenery.GANGPLANK_11211 -> cross(player, Location(3684, 2950, 1))
                    // Moss B
                    Scenery.GANGPLANK_11212 -> cross(player, Location(3684, 2953, 0))
                    // Port Sarim A
                    Scenery.GANGPLANK_14304 ->
                        cross(player, Location(3041, 3199, 1)).also {
                            sendMessage(player, "You board the ship.")
                        }
                    // Port Sarim B
                    Scenery.GANGPLANK_14305 ->
                        cross(player, Location(3041, 3202, 0)).also {
                            sendMessage(player, "You disembark the ship.")
                        }
                    // Pest control A
                    Scenery.GANGPLANK_14306 ->
                        cross(player, Location(2662, 2676, 1)).also {
                            sendMessage(player, "You board the ship.")
                        }
                    // Pest control B
                    Scenery.GANGPLANK_14307 ->
                        cross(player, Location(2659, 2676, 0)).also {
                            sendMessage(player, "You disembark the ship.")
                        }
                    // Port Phasmatys A
                    Scenery.GANGPLANK_17392 -> cross(player, Location(3705, 3503, 1))
                    // Port Phasmatys B
                    Scenery.GANGPLANK_17393 -> cross(player, Location(3702, 3503, 0))
                    // Catherby A
                    Scenery.GANGPLANK_17394 -> cross(player, Location(2792, 3417, 1))
                    // Catherby B
                    Scenery.GANGPLANK_17395 -> cross(player, Location(2792, 3414, 0))
                    // Shipyard A
                    Scenery.GANGPLANK_17396 -> cross(player,Location(2998, 3032, 1))
                    // Shipyard B
                    Scenery.GANGPLANK_17397 -> cross(player, Location(3000, 3032, 0))
                    // Karamja A
                    Scenery.GANGPLANK_17398 ->
                        cross(player, Location(2957, 3158, 1)).also {
                            sendMessage(player, "You must speak to the Customs Officer before it will set sail.")
                        }
                    // Karamja B
                    Scenery.GANGPLANK_17399 ->
                        cross(player, Location(2954, 3158, 0)).also {
                            sendMessage(player, "You must speak to the Customs Officer before it will set sail.")
                        }
                    // Brimhaven A
                    Scenery.GANGPLANK_17400 -> cross(player, Location(2763, 3238, 1))
                    // Brimhaven B
                    Scenery.GANGPLANK_17401 -> cross(player, Location(2760, 3238, 0))
                    // Port Khazard A
                    Scenery.GANGPLANK_17402 -> cross(player, Location(2674, 3141, 1))
                    // Port Khazard B
                    Scenery.GANGPLANK_17403 -> cross(player, Location(2674, 3144, 0))
                    // Port Sarim A
                    Scenery.GANGPLANK_17404 -> cross(player, Location(3038, 3189, 1))
                    // Port Sarim B
                    Scenery.GANGPLANK_17405 -> cross(player, Location(3038, 3192, 0))
                    // Mos le harmless A
                    Scenery.GANGPLANK_17406 -> cross(player, Location(3668, 2931, 1))
                    // Mos le harmless B
                    Scenery.GANGPLANK_17407 -> cross(player, Location(3671, 2931, 0))
                    // Tyras A
                    Scenery.GANGPLANK_17408 -> cross(player, Location(2142, 3125, 1))
                    // Tyras B
                    Scenery.GANGPLANK_17409 -> cross(player, Location(2142, 3122, 0))
                    // Oo-Glog A
                    Scenery.GANGPLANK_29168 -> cross(player, Location.create(2626, 2857, 1))
                    // Oo-Glog B
                    Scenery.GANGPLANK_29169 -> cross(player, Location.create(2623, 2857, 0))
                    else ->
                        sendDialogueLines(
                            player,
                            "I don't think that whoever owns this ship will be happy",
                            "with me wandering all over it.",
                        )
                }
            }
            return@on true
        }
    }

    companion object {
        private val GANGPLANKS = intArrayOf(Scenery.GANGPLANK_2081, Scenery.GANGPLANK_2082, Scenery.GANGPLANK_2083, Scenery.GANGPLANK_2084, Scenery.GANGPLANK_2085, Scenery.GANGPLANK_2086, Scenery.GANGPLANK_2087, Scenery.GANGPLANK_2088, Scenery.GANGPLANK_2412, Scenery.GANGPLANK_2413, Scenery.GANGPLANK_2414, Scenery.GANGPLANK_2415, Scenery.GANGPLANK_2594, Scenery.GANGPLANK_11211, Scenery.GANGPLANK_11212, Scenery.GANGPLANK_14304, Scenery.GANGPLANK_14305, Scenery.GANGPLANK_14306, Scenery.GANGPLANK_14307, Scenery.GANGPLANK_17392, Scenery.GANGPLANK_17393, Scenery.GANGPLANK_17394, Scenery.GANGPLANK_17395, Scenery.GANGPLANK_17398, Scenery.GANGPLANK_17399, Scenery.GANGPLANK_17400, Scenery.GANGPLANK_17401, Scenery.GANGPLANK_17402, Scenery.GANGPLANK_17403, Scenery.GANGPLANK_17404, Scenery.GANGPLANK_17405, Scenery.GANGPLANK_17406, Scenery.GANGPLANK_17407, Scenery.GANGPLANK_17408, Scenery.GANGPLANK_17409, Scenery.GANGPLANK_29168, Scenery.GANGPLANK_29169)

        private val SAILORS =
            intArrayOf(
                NPCs.TRADER_STAN_4650,
                NPCs.TRADER_CREWMEMBER_4651,
                NPCs.TRADER_CREWMEMBER_4652,
                NPCs.TRADER_CREWMEMBER_4653,
                NPCs.TRADER_CREWMEMBER_4654,
                NPCs.TRADER_CREWMEMBER_4655,
                NPCs.TRADER_CREWMEMBER_4656,
            )

        fun cross(
            player: Player,
            location: Location?,
        ) {
            queueScript(player, 2, QueueStrength.STRONG) {
                player.properties.teleportLocation = location
                return@queueScript stopExecuting(player)
            }
        }
    }

    override fun defineInterfaceListeners() {
        on(Components.SAILING_TRANSPORT_WORLD_MAP_95) { player, _, _, buttonID, _, _ ->
            CharterUtils.handle(player, buttonID)
            return@on true
        }
    }
}
