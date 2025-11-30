package content.region.asgarnia.falador.quest.rd.plugin

import content.region.asgarnia.falador.dialogue.KnightNotesDialogue
import content.region.asgarnia.falador.quest.rd.RecruitmentDrive
import content.region.asgarnia.falador.quest.rd.cutscene.FailTestCutscene
import content.region.asgarnia.falador.quest.rd.cutscene.FinishTestCutscene
import content.region.asgarnia.falador.quest.rd.dialogue.*
import core.api.*
import core.game.dialogue.FaceAnim
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.interaction.QueueStrength
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.game.world.update.flag.context.Graphics
import shared.consts.*

class RecruitmentDrivePlugin : InteractionListener, InterfaceListener {

    override fun defineInterfaceListeners() {
        onOpen(Components.RD_COMBOLOCK_285) { player, _ ->
            lockArray.forEach { setAttribute(player, it, INITIAL_VALUE) }
            return@onOpen true
        }
        onClose(Components.RD_COMBOLOCK_285) { player, _ ->
            lockArray.forEach { removeAttribute(player, it) }
            return@onClose true
        }
        on(Components.RD_COMBOLOCK_285) { player, _, _, buttonID, _, _ ->
            when (buttonID) {
                in 10..17 -> adjustLock(player, buttonID)
                18 -> validateAnswer(player)
            }
            return@on true
        }
    }

    override fun defineListeners() {
        on(Scenery.OLD_BOOKSHELF_7327, IntType.SCENERY, "search") { player, _ ->
            RDUtils.searchingHelper(player, magnet, Items.MAGNET_5604, "You search the bookshelves...", "Hidden amongst the books you find a magnet.")
            return@on true
        }

        on(Scenery.OLD_BOOKSHELF_7328, IntType.SCENERY, "search") { player, _ ->
            if (getAttribute(player, "/save:rd:help", -1) < 3) {
                sendMessage(player, "You search the bookshelves...")
                sendMessage(player, "You find nothing of interest.", 1)
            } else {
                RDUtils.searchingHelper(player, book, Items.ALCHEMICAL_NOTES_5588, "You search the bookshelves...", "You find a book that looks like it might be helpful.")
            }
            return@on true
        }

        on(Scenery.OLD_BOOKSHELF_7329, IntType.SCENERY, "search") { player, _ ->
            RDUtils.searchingHelper(player, knife, Items.KNIFE_5605, "You search the bookshelves...", "Hidden amongst the books you find a knife.")
            return@on true
        }

        on(Scenery.OLD_BOOKSHELF_7330, IntType.SCENERY, "search") { player, _ ->
            RDUtils.searchingHelper(player, "", 0, "You search the bookshelves...", "")
            return@on true
        }

        VIAL_ITEM_IDS.forEachIndexed { index, item ->
            on(SHELVES_SCENERY_IDS[index], IntType.SCENERY, "search") { player, _ ->
                val vialList = mutableListOf<Int>()
                if (!getAttribute(player, Vials.vialMap[item]?.attribute ?: return@on false, false)) {
                    vialList.add(item)
                }
                openDialogue(player, RDUtils.ShelfHelper(vialList.toIntArray()))
                return@on true
            }
        }

        on(Scenery.SHELVES_7340, IntType.SCENERY, "search") { player, _ ->
            val vialCount = getAttribute(player, vials, 3)
            val vialList = List(vialCount) { Items.VIAL_OF_LIQUID_5582 }
            openDialogue(player, RDUtils.ShelfHelper(vialList.toIntArray(), vials))
            return@on true
        }

        on(Scenery.CRATE_7347, IntType.SCENERY, "search") { player, node ->
            if (node.location == RDUtils.getLocationForScenery(node.asScenery())) {
                RDUtils.searchingHelper(player, tin, Items.TIN_5600, "You search the crate...", "Inside the crate you find a ${getItemName(Items.TIN_5600).lowercase()}.")
            } else {
                RDUtils.searchingHelper(player, "", 0, "You search the crate...", "")
            }
            return@on true
        }

        on(Scenery.CRATE_7348, IntType.SCENERY, "search") { player, node ->
            if (node.location == RDUtils.getLocationForScenery(node.asScenery())) {
                RDUtils.searchingHelper(player, chisel, Items.CHISEL_5601, "You search the crate...", "Inside the crate you find a ${getItemName(Items.CHISEL_5601).lowercase()}.")
            } else {
                RDUtils.searchingHelper(player, "", 0, "You search the crate...", "")
            }
            return@on true
        }

        on(Scenery.CRATE_7349, IntType.SCENERY, "search") { player, node ->
            if (node.location == RDUtils.getLocationForScenery(node.asScenery())) {
                RDUtils.searchingHelper(player, wire, Items.BRONZE_WIRE_5602, "You search the crate...", "Inside the crate you find a ${getItemName(Items.BRONZE_WIRE_5602).lowercase()}.")
            } else {
                RDUtils.searchingHelper(player, "", 0, "You search the crate...", "")
            }
            return@on true
        }

        on(Scenery.CLOSED_CHEST_7350, IntType.SCENERY, "open") { _, node ->
            replaceScenery(node.asScenery(), Scenery.OPEN_CHEST_7351, 100)
            return@on true
        }

        on(Scenery.OPEN_CHEST_7351, IntType.SCENERY, "search") { player, _ ->
            RDUtils.searchingHelper(player, shears, Items.SHEARS_5603, "You search the chest...", "Inside the chest you find some shears.")
            return@on true
        }

        on(Scenery.OPEN_CHEST_7351, IntType.SCENERY, "close") { _, node ->
            replaceScenery(node.asScenery(), Scenery.CLOSED_CHEST_7350, -1)
            return@on true
        }

        onUseWith(IntType.ITEM, Items.TIN_5600, Items.GYPSUM_5579) { player, used, with ->
            RDUtils.processItemUsageAndReturn(player, used.asItem(), with.asItem(), Item(Items.TIN_5592))
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, Items.TIN_5592, Items.VIAL_OF_LIQUID_5582) { player, used, with ->
            RDUtils.processItemUsage(player, used.asItem(), with.asItem(), Item(Items.TIN_5593))
            sendMessage(player, "You notice the tin gets quite warm as you do this.")
            sendMessage(player, "A lumpy white mixture is made, that seems to be hardening.", 1)
            return@onUseWith true
        }

        onUseWith(IntType.SCENERY, Items.TIN_5593, Scenery.KEY_7346) { player, used, _ ->
            sendMessage(player, "You make an impression of the key as the white mixture hardens.")
            replaceSlot(player, slot = used.index, Item(Items.TIN_5594))
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, Items.TIN_5594, *potionIDs) { player, used, with ->
            RDUtils.processItemUsageAndReturn(player, used.asItem(), with.asItem(), Item(Items.TIN_5595))
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, Items.TIN_5595, *potionIDs) { player, used, with ->
            RDUtils.processItemUsageAndReturn(player, used.asItem(), with.asItem(), Item(Items.TIN_5596))
            return@onUseWith true
        }

        onUseWith(IntType.SCENERY, Items.TIN_5596, Scenery.BUNSEN_BURNER_7332) { player, used, _ ->
            if (removeItem(player, used.id)) {
                sendMessage(player, "You heat the two powdered ores together in the tin.")
                sendMessage(player, "You make a duplicate of the key in bronze.")
                addItemOrDrop(player, Items.TIN_5597)
            }
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, Items.TIN_5597, *toolIDs) { player, used, _ ->
            if (removeItem(player, used.id)) {
                sendMessage(player, "You prise the duplicate key out of the tin.")
                addItemOrDrop(player, Items.TIN_5600)
                addItemOrDrop(player, Items.BRONZE_KEY_5585)
            }
            return@onUseWith true
        }

        onUseWith(IntType.SCENERY, Items.METAL_SPADE_5586, Scenery.BUNSEN_BURNER_7332) { player, _, _ ->
            lock(player, 3)
            sendMessage(player, "You burn the wooden handle away from the spade...")
            queueScript(player, 1, QueueStrength.WEAK) { stage: Int ->
                when (stage) {
                    0 -> {
                        visualize(player, -1, Graphics(157, 96))
                        playAudio(player, Sounds.FIREWAVE_HIT_163)
                        keepRunning(player)
                    }

                    1 -> {
                        removeItem(player, Items.METAL_SPADE_5586)
                        keepRunning(player)
                    }

                    2 -> {
                        addItem(player, Items.METAL_SPADE_5587)
                        addItem(player, Items.ASHES_592)
                        sendMessage(player, "...and are left with a metal spade with no handle.")
                        stopExecuting(player)
                    }

                    else -> stopExecuting(player)
                }
            }
            return@onUseWith true
        }

        on(Scenery.STONE_DOOR_7343, SCENERY, "study") { player, _ ->
            sendDialogueLines(player, "There is a stone slab here obstructing the door.", "There is a small hole in the slab that looks like it might be for a handle.")
            // sendMessage(player, "It's nearly a perfect fit!")
            return@on true
        }

        onUseWith(IntType.SCENERY, Items.METAL_SPADE_5587, Scenery.STONE_DOOR_7343) { player, used, _ ->
            if (removeItem(player, used.id)) {
                playAudio(player, Sounds.RECRUIT_SPADE_1742)
                sendMessage(player, "You slide the spade into the hole in the stone...")
                sendMessage(player, "It's nearly a perfect fit!", 1)
                setVarbit(player, doorVarbit, 1)
            }
            return@onUseWith true
        }

        onUseWith(IntType.SCENERY, DoorVials.doorVialsArray, Scenery.STONE_DOOR_7344) { player, used, _ ->
            RDUtils.handleVialUsage(player, used.asItem())
            return@onUseWith true
        }

        on(Scenery.STONE_DOOR_7344, SCENERY, "pull-spade") { player, _ ->
            RDUtils.handleSpadePull(player)
            return@on true
        }

        on(Scenery.OPEN_DOOR_7345, SCENERY, "walk-through") { player, _ ->
            RDUtils.handleDoorWalkThrough(player)
            return@on true
        }

        on(Rooms.STATUE_SCENERY_IDS, IntType.SCENERY, "touch") { player, node ->
            val correctStatue = getAttribute(player, "rd:statues", 0)
            if (node.id == Rooms.STATUE_SCENERY_IDS[correctStatue]) {
                setAttribute(player, RecruitmentDrive.stagePass, true)
                playJingle(player, 156)
                sendNPCDialogueLines(player, NPCs.LADY_TABLE_2283, FaceAnim.NEUTRAL, false, "Excellent work, ${player.name}.", "Please step through the portal to meet your next challenge.")
            } else {
                setAttribute(player, RecruitmentDrive.stageFail, true)
                openDialogue(player, LadyTableDialogue(2), NPC(NPCs.LADY_TABLE_2283))
            }
            return@on true
        }

        on(Items.NITROUS_OXIDE_5581, IntType.ITEM, "open") { player, node ->
            sendMessage(player, "You uncork the vial...")
            replaceSlot(player, node.asItem().slot, Item(Items.VIAL_229, 1))
            sendMessage(player, "You smell a strange gas as it escapes from inside the vial.")
            sendChat(player, "Hahahahahahaha!", 1)
            return@on true
        }

        on(Rooms.PORTAL_SCENERY_IDS, IntType.SCENERY, "use") { player, _ ->
            sendOptions(player, "Quit the training grounds?", "YES.", "NO.")
            addDialogueAction(player) { _, buttonID ->
                if (buttonID == 2) {
                    FailTestCutscene(player).start()
                } else {
                    closeChatBox(player)
                }
            }
            return@on true
        }

        onUseWith(IntType.SCENERY, Items.BRONZE_KEY_5585, Scenery.DOOR_7326) { player, _, with ->
            handleDoorInteraction(player, with.asScenery())
            return@onUseWith true
        }

        on(Rooms.DOOR_SCENERY_IDS, IntType.SCENERY, "open") { player, node ->
            handleDoorInteraction(player, node.asScenery())
            return@on true
        }

        onUseWith(IntType.NPC,  Items.KNIGHTS_NOTES_11734, NPCs.SIR_TIFFY_CASHIEN_2290) { player, _, with ->
            openDialogue(player, KnightNotesDialogue(), with)
            return@onUseWith true
        }

        onUseWith(IntType.NPC,  Items.KNIGHTS_NOTES_11735, NPCs.SIR_TIFFY_CASHIEN_2290) { player, _, with ->
            openDialogue(player, KnightNotesDialogue.BrokenKnightNotes(), with)
            return@onUseWith true
        }

        on(BRIDGE_SCENERY, IntType.SCENERY, "cross") { player, node ->
            val equipmentCount = ITEM_VARBITS.keys.count { inEquipment(player, it) }

            when {
                equipmentCount > 1 -> sendDialogue(player, "I shouldn't carry more than 5Kg across the bridge...")
                ITEM_VARBITS.values.count { getVarbit(player, it.first) == 1 } == 2 -> {
                    closeDialogue(player)
                    openDialogue(player, SirTinleyDialogue(2), NPCs.SIR_SPISHYUS_2282)
                }
                else -> {
                    lock(player, 5)
                    sendMessage(player, "You carefully walk across the bridge...")
                    val path = if (node.id == Scenery.PRECARIOUS_BRIDGE_7286) listOf(2476, 4972) else listOf(2484, 4972)
                    player.walkingQueue.reset()
                    player.walkingQueue.addPath(path[0], path[1])
                }
            }
            return@on true
        }

        ITEM_VARBITS.forEach { (item, vars) ->
            val scenery = when (item) {
                Items.GRAIN_5607 -> Scenery.GRAIN_7284
                Items.FOX_5608 -> Scenery.FOX_7277
                Items.CHICKEN_5609 -> Scenery.CHICKEN_7281
                else -> error("Invalid item ID")
            }

            val slot = when (item) {
                Items.GRAIN_5607 -> EquipmentSlot.CAPE.ordinal
                Items.FOX_5608 -> EquipmentSlot.WEAPON.ordinal
                Items.CHICKEN_5609 -> EquipmentSlot.SHIELD.ordinal
                else -> error("Invalid item ID")
            }

            on(scenery, SCENERY, "pick-up") { player, _ ->
                if (getAttribute(player, RecruitmentDrive.stageFail, 0) == 0) {
                    val inLeft = WEST_ZONE_BORDERS.insideBorder(player)
                    val inRight = EAST_ZONE_BORDERS.insideBorder(player)
                    if (inLeft || inRight) {
                        replaceSlot(player, slot, Item(item), null, Container.EQUIPMENT)
                        setVarbit(player, if (inLeft) vars.first else vars.second, if (inLeft) 1 else 0)
                    }
                }
                return@on true
            }

            onUnequip(item) { player, _ ->
                val inLeft = WEST_ZONE_BORDERS.insideBorder(player)
                val inRight = EAST_ZONE_BORDERS.insideBorder(player)
                removeItem(player, item, Container.EQUIPMENT)
                setVarbit(player, if (inLeft) vars.first else vars.second, if (inLeft) 0 else 1)

                if (inRight && ITEM_VARBITS.values.all { getVarbit(player, it.second) == 1 }) {
                    sendMessage(player, "Congratulations! You have solved this room's puzzle!")
                    playJingle(player, 160)
                    setAttribute(player, RecruitmentDrive.stagePass, 1)
                }
                return@onUnequip true
            }
        }
    }

    private fun handleDoorInteraction(player: Player, door: core.game.node.scenery.Scenery) {
        val doorId = door.id
        val stagePass = getAttribute(player, RecruitmentDrive.stagePass, false)

        if (doorId == Scenery.DOOR_7326 && inInventory(player, Items.BRONZE_KEY_5585)) {
            DoorActionHandler.handleAutowalkDoor(player, door)
            sendMessage(player, "You use the duplicate key you made to unlock the door.")
            setAttribute(player, RecruitmentDrive.stagePass, true)
        }

        if (stagePass) {
            setAttribute(player, RecruitmentDrive.stagePass, false)
            val newStage = getAttribute(player, RecruitmentDrive.stage, 0) + 1
            setAttribute(player, RecruitmentDrive.stage, newStage)

            if (newStage >= 5) {
                DoorActionHandler.handleAutowalkDoor(player, door)
                face(player, door)
                FinishTestCutscene(player).start()
                return
            }

            val currentStageEnum = Rooms.index[getAttribute(player, RecruitmentDrive.stageArray[newStage], 0)]!!
            player.interfaceManager.close()
            clearInventory(player)

            queueScript(player, 1, QueueStrength.SOFT) { stage: Int ->
                when (stage) {
                    0 -> delayScript(player, 4).also {
                        DoorActionHandler.handleAutowalkDoor(player, door)
                        face(player, door)
                    }
                    1 -> delayScript(player, 2).also { teleport(player, currentStageEnum.location) }
                    2 -> delayScript(player, 1).also { forceWalk(player, currentStageEnum.destination, "DUMB") }
                    3 -> {
                        initRoomStage(player, currentStageEnum.npc)
                        return@queueScript stopExecuting(player)
                    }
                    else -> stopExecuting(player)
                }
            }
            return
        }

        when (doorId) {
            Scenery.DOOR_7323 -> openInterface(player, Components.RD_COMBOLOCK_285)
            else -> sendMessage(player, "You have not completed this room's puzzle yet.")
        }
    }

    override fun defineDestinationOverrides() {
        setDest(IntType.SCENERY, intArrayOf(Scenery.OPEN_DOOR_7345), "walk-through") { player, _ ->
            when {
                inBorders(player, 2476, 4941, 2477, 4939) -> Location(2476, 4940, 0)
                inBorders(player, 2477, 4941, 2478, 4939) -> Location(2478, 4940, 0)
                else -> Location(2478, 4940, 0)
            }
        }

        setDest(IntType.NPC, intArrayOf(NPCs.SIR_TIFFY_CASHIEN_2290), "talk-to") { _, _ ->
            Location(2997, 3374, 0)
        }
    }

    private fun adjustLock(player: Player, buttonID: Int) {
        val pos = (buttonID - 10) / 2
        val dir = if ((buttonID - 10) % 2 == 0) -1 else 1
        val newValue = ((getAttribute(player, lockArray[pos], INITIAL_VALUE) + dir - LOWER_BOUND) % (UPPER_BOUND - LOWER_BOUND + 1) + LOWER_BOUND)
        setAttribute(player, lockArray[pos], newValue)
        sendString(player, newValue.toChar().toString(), Components.RD_COMBOLOCK_285, pos + 6)
    }

    private fun validateAnswer(player: Player) {
        val answer = lockArray.joinToString("") { getAttribute(player, it, INITIAL_VALUE).toChar().toString() }
        closeInterface(player)
        if (answers[getAttribute(player, SirReenItchoodDialogue.ATTRIBUTE_CLUE, 0)] == answer) {
            if (!getAttribute(player, RecruitmentDrive.stageFail, false)) {
                setAttribute(player, RecruitmentDrive.stagePass, true)
            }
            playJingle(player, 159)
            sendNPCDialogue(player, NPCs.SIR_REN_ITCHOOD_2287, "Your wit is sharp, your brains quite clear; You solved my puzzle with no fear. At puzzles I rank you quite the best, now enter the portal for your next test.")
        } else {
            setAttribute(player, RecruitmentDrive.stageFail, true)
            openDialogue(player, SirReenItchoodDialogue(2), NPC(NPCs.SIR_REN_ITCHOOD_2287))
        }
    }

    companion object {
        private const val INITIAL_VALUE = 65
        private const val LOWER_BOUND   = 65
        private const val UPPER_BOUND   = 90
        private val lockArray = arrayOf("rd:lock1", "rd:lock2", "rd:lock3", "rd:lock4")
        private val answers = arrayOf("BITE", "FISH", "LAST", "MEAT", "RAIN", "TIME")

        private val ITEM_VARBITS      = mapOf(Items.FOX_5608 to Pair(680, 681), Items.CHICKEN_5609 to Pair(682, 683), Items.GRAIN_5607 to Pair(684, 685))
        private val WEST_ZONE_BORDERS = ZoneBorders(2479, 4967, 2490, 4977)
        private val EAST_ZONE_BORDERS = ZoneBorders(2471, 4967, 2478, 4977)
        private val BRIDGE_SCENERY    = intArrayOf(Scenery.PRECARIOUS_BRIDGE_7286, Scenery.PRECARIOUS_BRIDGE_7287)

        enum class Rooms(val npc: Int, val location: Location, val destination: Location, val portal: Int, val door: Int) {
            I(NPCs.SIR_SPISHYUS_2282, Location(2490, 4972), Location(2489, 4972), Scenery.PORTAL_7272, Scenery.DOOR_7274),
            II(NPCs.LADY_TABLE_2283, Location(2460, 4979), Location(2459, 4979), Scenery.PORTAL_7288, Scenery.DOOR_7302),
            III(NPCs.SIR_KUAM_FERENTSE_2284, Location(2455, 4964), Location(2456, 4964), Scenery.PORTAL_7315, Scenery.DOOR_7317),
            IV(NPCs.SIR_TINLEY_2286, Location(2471, 4956), Location(2472, 4956), Scenery.PORTAL_7318, Scenery.DOOR_7320),
            V(NPCs.SIR_REN_ITCHOOD_2287, Location(2439, 4956), Location(2440, 4956), Scenery.PORTAL_7321, Scenery.DOOR_7323),
            VI(NPCs.MISS_CHEEVERS_2288, Location(2467, 4940), Location(2468, 4940), Scenery.PORTAL_7324, Scenery.DOOR_7326),
            VII(NPCs.MS_HYNN_TERPRETT_2289, Location(2451, 4935), Location(2451, 4936), Scenery.PORTAL_7352, Scenery.DOOR_7354),
            ;

            companion object {
                val index = values().associateBy { it.ordinal }
                val PORTAL_SCENERY_IDS = values().map { it.portal }.toIntArray()
                val DOOR_SCENERY_IDS = values().map { it.door }.toIntArray()

                val STATUE_SCENERY_IDS = intArrayOf(0, 7308, 7307, 7306, 7305, 7304, 7303, 7312, 7313, 7314, 7311, 7310, 7309)
            }
        }

        fun initRoomStage(player: Player, npc: Int) {
            when (npc) {
                NPCs.SIR_SPISHYUS_2282 -> openDialogue(player, SirSpishyusDialogue(1), NPC(npc))
                NPCs.LADY_TABLE_2283 -> openDialogue(player, LadyTableDialogue(1), NPC(npc))
                NPCs.SIR_KUAM_FERENTSE_2284 -> openDialogue(player, SirKuamDialogue(1), NPC(npc))
                NPCs.SIR_TINLEY_2286 -> openDialogue(player, SirTinleyDialogue(1), NPC(npc))
                NPCs.SIR_REN_ITCHOOD_2287 -> openDialogue(player, SirReenItchoodDialogue(1), NPC(npc))
                NPCs.MISS_CHEEVERS_2288 -> openDialogue(player, MissCheeversDialogue(1), NPC(npc))
                NPCs.MS_HYNN_TERPRETT_2289 -> openDialogue(player, HynnTerprettDialogue(1), NPC(npc))
            }
        }

        const val doorVarbit = 686
        const val book = "rd:book"
        const val magnet = "rd:magnet"
        const val tin = "rd:tin"
        const val chisel = "rd:chisel"
        const val wire = "rd:wire"
        const val knife = "rd:knife"
        const val shears = "rd:shears"
        const val vials = "rd:3vialsofliquid"

        val toolIDs = intArrayOf(Items.BRONZE_WIRE_5602, Items.CHISEL_5601, Items.KNIFE_5605)
        val potionIDs = intArrayOf(Items.TIN_ORE_POWDER_5583, Items.CUPRIC_ORE_POWDER_5584)

        enum class Vials(val itemId: Int, val attribute: String) {
            CUPRIC_SULPHATE_5577(Items.CUPRIC_SULPHATE_5577, "rd:cupricsulphate"),
            ACETIC_ACID_5578(Items.ACETIC_ACID_5578, "rd:aceticacid"),
            GYPSUM_5579(Items.GYPSUM_5579, "rd:gypsum"),
            SODIUM_CHLORIDE_5580(Items.SODIUM_CHLORIDE_5580, "rd:sodiumchloride"),
            NITROUS_OXIDE_5581(Items.NITROUS_OXIDE_5581, "rd:nitrousoxide"),
            VIAL_OF_LIQUID_5582(Items.VIAL_OF_LIQUID_5582, "rd:vialofliquid"),
            TIN_ORE_POWDER_5583(Items.TIN_ORE_POWDER_5583, "rd:tinorepowder"),
            CUPRIC_ORE_POWDER_5584(Items.CUPRIC_ORE_POWDER_5584, "rd:cupricorepowder");

            companion object {
                val vialMap = values().associateBy { it.itemId }
            }
        }

        enum class DoorVials(val itemId: Int, val attribute: String) {
            CUPRIC_SULPHATE_5577(Items.CUPRIC_SULPHATE_5577, "rd:doorcupricsulphate"),
            ACETIC_ACID_5578(Items.ACETIC_ACID_5578, ""),
            SODIUM_CHLORIDE_5580(Items.SODIUM_CHLORIDE_5580, ""),
            VIAL_OF_LIQUID_5582(Items.VIAL_OF_LIQUID_5582, "rd:doorvialofliquid");

            companion object {
                val doorVialsArray = values().map { it.itemId }.toIntArray()
                val doorVialsMap = values().associateBy { it.itemId }
                val doorVialsRequiredMap = values().associateBy { it.itemId }.filter { it.value.attribute != "" }
            }
        }

        val VIAL_ITEM_IDS = listOf(Items.ACETIC_ACID_5578, Items.CUPRIC_SULPHATE_5577, Items.GYPSUM_5579, Items.SODIUM_CHLORIDE_5580, Items.NITROUS_OXIDE_5581, Items.TIN_ORE_POWDER_5583, Items.CUPRIC_ORE_POWDER_5584)
        val SHELVES_SCENERY_IDS = (Scenery.SHELVES_7333..Scenery.SHELVES_7339).toList()
    }

    class SirLeyeNPC : NPCBehavior(NPCs.SIR_LEYE_2285) {
        private var clearTime = 0

        override fun tick(self: NPC): Boolean {
            if (++clearTime > 288) {
                clearTime = 0
                poofClear(self)
            }
            return true
        }

        override fun beforeDamageReceived(self: NPC, attacker: Entity, state: BattleState) {
            val lifepoints = self.skills.lifepoints
            if (attacker is Player) {
                if (attacker.isMale) {
                    if (state.estimatedHit + Integer.max(state.secondaryHit, 0) > lifepoints - 1) {
                        self.skills.lifepoints = self.getSkills().getStaticLevel(Skills.HITPOINTS)
                    }
                }
            }
        }

        override fun onDeathFinished(self: NPC, killer: Entity) {
            if (killer is Player) {
                clearHintIcon(killer)
                setAttribute(killer, RecruitmentDrive.stagePass, true)
                openDialogue(killer, SirKuamDialogue(1), NPC(NPCs.SIR_KUAM_FERENTSE_2284))
                removeAttribute(killer, SirKuamDialogue.spawnSirLeye)
            }
        }

        override fun getXpMultiplier(self: NPC, attacker: Entity): Double = 0.0

        companion object {
            @JvmStatic
            fun init(player: Player) {
                val boss = NPC.create(NPCs.SIR_LEYE_2285, Location.create(2457, 4966, 0))
                boss.isWalks = true
                boss.isAggressive = true
                boss.isActive = false

                if (boss.asNpc() != null && boss.isActive) {
                    boss.properties.teleportLocation = boss.properties.spawnLocation
                }
                boss.isActive = true
                GameWorld.Pulser.submit(
                    object : Pulse(0, boss) {
                        override fun pulse(): Boolean {
                            boss.init()
                            sendChat(boss, "No man may defeat me!")
                            registerHintIcon(player, boss)
                            boss.attack(player)
                            setAttribute(player, RDUtils.ATTRIBUTE_NPC_SPAWN, true)
                            return true
                        }
                    },
                )
            }
        }
    }
}
