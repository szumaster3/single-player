package content.global.skill.construction.decoration

import core.api.*
import core.api.item.allInInventory
import core.api.ui.sendInterfaceConfig
import core.cache.def.impl.ItemDefinition
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.net.packet.PacketRepository
import core.net.packet.context.ContainerContext
import core.net.packet.out.ContainerPacket
import core.tools.YELLOW
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery

class ItemStorageListener : InterfaceListener, InteractionListener {

    override fun defineInterfaceListeners() {

        /*
         * Handles storage boxes.
         */

        on(INTERFACE) { player, _, _, buttonID, _, _ ->
            when {
                // Bookcase
                getAttribute(player, "con:bookcase", false) -> {
                    val setIndex = (buttonID - 56) / 2
                    if (setIndex in BOOKCASE_CONTENT.indices) {
                        addItem(player, BOOKCASE_CONTENT[setIndex])
                    } else {
                        player.debug("Invalid button / book index: [$buttonID]")
                    }
                }
                // Dress box
                getAttribute(player, "con:fancy-dress-box", false) -> {
                    val DRESS_BOX_COMPONENT_MAP = DRESS_BOX_COMPONENT_PAIRS.flatMapIndexed { index, (labelId, iconId) ->
                        listOf(
                            labelId to index,
                            iconId to index
                        )
                    }.toMap()
                    val setIndex = DRESS_BOX_COMPONENT_MAP[buttonID]

                    if (setIndex != null) {
                        handleFancyDressBox(player, setIndex)
                    } else {
                        player.debug("Invalid button / costume set: [$buttonID]")
                    }
                }
                // Cape rack
                getAttribute(player, "con:cape-rack", false) -> {
                    val CAPE_RACK_COMPONENT_MAP = CAPE_RACK_COMPONENT_PAIRS.flatMapIndexed { index, (label, icon) ->
                        listOf(
                            label to index,
                            icon to index
                        )
                    }.toMap()
                    val setIndex = CAPE_RACK_COMPONENT_MAP[buttonID]

                    if (setIndex != null) {
                        handleCapeRack(player, setIndex)
                    } else {
                        player.debug("Invalid button / cape display: [$buttonID]")
                    }
                }
            }

            return@on true
        }

        /*
         * Remove previous interaction attribute.
         */

        onClose(INTERFACE) { player, _ ->
            removeAttributes(
                player,
                "con:fancy-dress-box",
                "con:bookcase",
                "con:cape-rack"
            )
            return@onClose true
        }
    }

    override fun defineListeners() {

        /*
         * Handles opening of fancy dress box.
         */

        on(DRESS_BOX_CLOSE, IntType.SCENERY, "open") { player, node ->
            animate(player, Animations.OPEN_CHEST_536)
            replaceScenery(node.asScenery(), node.id + 1, -1)
            return@on true
        }

        /*
         * Handles interaction with fancy dress box.
         */

        on(DRESS_BOX_OPEN, IntType.SCENERY, "search", "close") { player, node ->
            when (getUsedOption(player)) {
                "close" -> {
                    animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                    replaceScenery(node.asScenery(), node.id - 1, -1)
                }

                else -> {
                    setAttribute(player, "con:fancy-dress-box", true)

                    openInterface(player, INTERFACE).also {
                        PacketRepository.send(
                            ContainerPacket::class.java,
                            ContainerContext(player, INTERFACE, 164, 30, DRESS_BOX_DISPLAY, false)
                        )

                        DRESS_BOX_COMPONENT_PAIRS.forEachIndexed { index, (label, icon) ->
                            val key = "set:$index"
                            val hidden = getAttribute(player, key, false)
                            sendInterfaceConfig(player, INTERFACE, label, hidden)
                            sendInterfaceConfig(player, INTERFACE, icon + 1, hidden)
                        }

                        listOf(
                            225 to "Fancy dress box",
                            55 to "Mime costume",
                            57 to "Royal frog costume",
                            59 to "Frog mask",
                            61 to "Zombie outfit",
                            63 to "Camo outfit",
                            65 to "Lederhosen outfit",
                            67 to "Shade robes"
                        ).forEach { (id, text) ->
                            sendString(player, text, INTERFACE, id)
                        }
                    }
                }
            }
            return@on true
        }

        /*
         * Handles interaction with bookcase.
         */

        on(BOOKCASE, IntType.SCENERY, "search") { player, _ ->
            animate(player, Animations.USE_OBJECT_POH_3659)
            setAttribute(player, "con:bookcase", true)
            openInterface(player, INTERFACE).also {
                PacketRepository.send(
                    ContainerPacket::class.java, ContainerContext(
                        player, INTERFACE, 164, 30, BOOKCASE_CONTENT.map { Item(it) }.toTypedArray(), false
                    )
                )
                BOOKCASE_CONTENT.forEachIndexed { index, itemId ->
                    val itemName = getItemName(itemId)
                    val itemExamine = ItemDefinition.forId(itemId).examine
                    sendString(player, "$YELLOW$itemName</col> <br>$itemExamine", INTERFACE, 55 + index * 2)
                }
                sendString(player, "Bookcase", INTERFACE, 225)
            }
            return@on true
        }

        /*
         * Handles interaction with cape rack.
         */

        on(CAPE_RACK, IntType.SCENERY, "search") { player, _ ->
            setAttribute(player, "con:cape-rack", true)
            openInterface(player, INTERFACE).also {
                sendString(player, "Cape rack", INTERFACE, 225)
                PacketRepository.send(
                    ContainerPacket::class.java, ContainerContext(player, INTERFACE, 164, 30, CAPE_RACK_CONTENT, false)
                )
                CAPE_RACK_COMPONENT_PAIRS.forEachIndexed { index, (label, icon) ->
                    val item = CAPE_RACK_CONTENT.getOrNull(index)
                    val itemName = item?.let { getItemName(it.id) } ?: "Please report this."
                    sendString(player, itemName, INTERFACE, 55 + index * 2)

                    val key = "cape:$index"
                    val hidden = getAttribute(player, key, false)

                    sendInterfaceConfig(player, INTERFACE, label, hidden)
                    sendInterfaceConfig(player, INTERFACE, icon + 1, hidden)
                }
            }
            return@on true
        }

        /*
         * Handles use the restricted book on bookcase.
         */

        onUseWith(IntType.SCENERY, BOOKCASE_RESTRICTED_CONTENT, *BOOKCASE) { player, _, _ ->
            sendMessage(player, "There doesn't seem to be space for that on the bookcase.")
            return@onUseWith true
        }
    }

    companion object {
        private const val INTERFACE = 467

        // Fancy dress box.
        private val DRESS_BOX_CLOSE = intArrayOf(Scenery.FANCY_DRESS_BOX_18772, Scenery.FANCY_DRESS_BOX_18774, Scenery.FANCY_DRESS_BOX_18776)
        private val DRESS_BOX_OPEN = intArrayOf(Scenery.FANCY_DRESS_BOX_18773, Scenery.FANCY_DRESS_BOX_18775, Scenery.FANCY_DRESS_BOX_18777)
        private val DRESS_BOX_DISPLAY = arrayOf(Item(Items.MIME_MASK_10629), Item(Items.PRINCESS_BLOUSE_10630), Item(Items.FROG_MASK_10721), Item(Items.ZOMBIE_SHIRT_10631), Item(Items.CAMO_TOP_10632), Item(Items.LEDERHOSEN_TOP_10633), Item(Items.SHADE_ROBE_10634))

        // Bookcase.
        private val BOOKCASE = intArrayOf(Scenery.BOOKCASE_13597, Scenery.BOOKCASE_13598, Scenery.BOOKCASE_13599)
        private val BOOKCASE_CONTENT = intArrayOf(Items.ARENA_BOOK_6891, Items.MY_NOTES_11339, Items.CRUMBLING_TOME_4707, Items.PIE_RECIPE_BOOK_7162, Items.GIANNES_COOK_BOOK_2167, Items.GAME_BOOK_7681, Items.STRONGHOLD_NOTES_9004, Items.COCKTAIL_GUIDE_2023, Items.TARNS_DIARY_10587, Items.CONSTRUCTION_GUIDE_8463, Items.GLASSBLOWING_BOOK_11656, Items.BREWIN_GUIDE_8989, Items.SECURITY_BOOK_9003, Items.QUEEN_HELP_BOOK_10562, Items.ABYSSAL_BOOK_5520, Items.EXPLORERS_NOTES_11677, Items.GOBLIN_BOOK_10999, Items.DWARVEN_LORE_4568, Items.BOOK_O_PIRACY_7144, Items.CLOCKWORK_BOOK_10594, Items.SCABARITE_NOTES_11975, Items.TRANSLATION_4655, Items.BOOK_ON_CHEMICALS_711, Items.INSTRUCTION_MANUAL_5, Items.BIRD_BOOK_10173, Items.FEATHERED_JOURNAL_10179, Items.BATTERED_BOOK_2886, Items.BEATEN_BOOK_9717, Items.A_HANDWRITTEN_BOOK_9627, Items.VARMENS_NOTES_4616)
        private val BOOKCASE_RESTRICTED_CONTENT = intArrayOf(Items.HOLY_BOOK_3840, Items.DAMAGED_BOOK_3839, Items.BOOK_OF_BALANCE_3844, Items.DAMAGED_BOOK_3843, Items.UNHOLY_BOOK_3842, Items.DAMAGED_BOOK_3841, Items.STRANGE_BOOK_5507, Items.BOOK_ON_CHICKENS_7464, Items.BOOK_OF_FOLKLORE_5508, Items.PVP_WORLDS_MANUAL_14056)

        // Cape rack.
        private val CAPE_RACK = intArrayOf(Scenery.OAK_CAPE_RACK_18766, Scenery.TEAK_CAPE_RACK_18767, Scenery.MAHOGANY_CAPE_RACK_18768, Scenery.GILDED_CAPE_RACK_18769, Scenery.MARBLE_CAPE_RACK_18770, Scenery.MAGIC_CAPE_RACK_18771)
        private val CAPE_RACK_DISPLAY = arrayOf(Item(Items.CAPE_OF_LEGENDS_10635), Item(Items.OBSIDIAN_CAPE_10636), Item(Items.FIRE_CAPE_10637), Item(Items.TEAM_1_CAPE_10638), Item(Items.ATTACK_CAPE_10639), Item(Items.STRENGTH_CAPE_10640), Item(Items.DEFENCE_CAPE_10641), Item(Items.RANGING_CAPE_10642), Item(Items.PRAYER_CAPE_10643), Item(Items.MAGIC_CAPE_10644), Item(Items.RUNECRAFT_CAPE_10645), Item(Items.HUNTER_CAPE_10646), Item(Items.HITPOINTS_CAPE_10647), Item(Items.AGILITY_CAPE_10648), Item(Items.HERBLORE_CAPE_10649), Item(Items.THIEVING_CAPE_10650), Item(Items.CRAFTING_CAPE_10651), Item(Items.FLETCHING_CAPE_10652), Item(Items.SLAYER_CAPE_10653), Item(Items.CONSTRUCT_CAPE_10654), Item(Items.MINING_CAPE_10655), Item(Items.SMITHING_CAPE_10656), Item(Items.FISHING_CAPE_10657), Item(Items.COOKING_CAPE_10658), Item(Items.FIREMAKING_CAPE_10659), Item(Items.WOODCUTTING_CAPE_10660), Item(Items.FARMING_CAPE_10661), Item(Items.QUEST_POINT_CAPE_10662), Item(Items.SPOTTED_CAPE_10663), Item(Items.SPOTTIER_CAPE_10664))
        private val CAPE_RACK_CONTENT = arrayOf(Item(Items.CAPE_OF_LEGENDS_1052), Item(Items.OBSIDIAN_CAPE_6568), Item(Items.FIRE_CAPE_6570), Item(Items.TEAM_10_CAPE_4333), Item(Items.ATTACK_CAPE_9747), Item(Items.STRENGTH_CAPE_9750), Item(Items.DEFENCE_CAPE_9753), Item(Items.RANGING_CAPE_9756), Item(Items.PRAYER_CAPE_9759), Item(Items.MAGIC_CAPE_9762), Item(Items.RUNECRAFT_CAPE_9765), Item(Items.HUNTER_CAPE_9948), Item(Items.HITPOINTS_CAPE_9768), Item(Items.AGILITY_CAPE_9771), Item(Items.HERBLORE_CAPE_9774), Item(Items.THIEVING_CAPE_9777), Item(Items.CRAFTING_CAPE_9780), Item(Items.FLETCHING_CAPE_9783), Item(Items.SLAYER_CAPE_9786), Item(Items.CONSTRUCT_CAPE_9789), Item(Items.MINING_CAPE_9792), Item(Items.SMITHING_CAPE_9795), Item(Items.FISHING_CAPE_9798), Item(Items.COOKING_CAPE_9801), Item(Items.FIREMAKING_CAPE_9804), Item(Items.WOODCUTTING_CAPE_9807), Item(Items.FARMING_CAPE_9810), Item(Items.QUEST_POINT_CAPE_9813), Item(Items.SPOTTED_CAPE_10663), Item(Items.SPOTTIER_CAPE_10664))

        // These components are responsible for buttons. Right side id +1 darkens the component area.
        // Buttons at this point must be dependent because the options for them display together.
        val DRESS_BOX_COMPONENT_PAIRS = listOf(56 to 165, 58 to 167, 60 to 169, 62 to 171, 64 to 173, 66 to 175, 68 to 177)
        val CAPE_RACK_COMPONENT_PAIRS = listOf(56 to 165, 58 to 167, 60 to 169, 62 to 171, 64 to 173, 66 to 175, 68 to 177, 70 to 179, 72 to 181, 74 to 183, 76 to 185, 78 to 187, 80 to 189, 82 to 191, 84 to 193, 86 to 195, 88 to 197, 90 to 199, 92 to 201, 94 to 203, 96 to 205, 98 to 207, 100 to 209, 102 to 211, 104 to 213, 106 to 215, 108 to 217, 110 to 219, 112 to 221, 114 to 223)

        private fun handleFancyDressBox(player: Player, setIndex: Int) {
            val item = DRESS_BOX_DISPLAY.getOrNull(setIndex)
            if (item == null) {
                player.debug("Invalid outfit selection.")
                return
            }

            val set = when (item.id) {
                Items.MIME_MASK_10629 -> { intArrayOf(Items.MIME_MASK_3057, Items.MIME_TOP_3058, Items.MIME_LEGS_3059, Items.MIME_BOOTS_3061, Items.MIME_GLOVES_3060)
                }

                Items.PRINCESS_BLOUSE_10630 -> {
                    if (player.isMale) {
                        intArrayOf(Items.PRINCE_TUNIC_6184, Items.PRINCE_LEGGINGS_6185)
                    } else {
                        intArrayOf(Items.PRINCESS_BLOUSE_6186, Items.PRINCESS_SKIRT_6187)
                    }
                }

                Items.FROG_MASK_10721 -> {
                    intArrayOf(Items.FROG_MASK_6188)
                }

                Items.ZOMBIE_SHIRT_10631 -> {
                    intArrayOf(Items.ZOMBIE_MASK_7594, Items.ZOMBIE_SHIRT_7592, Items.ZOMBIE_TROUSERS_7593, Items.ZOMBIE_GLOVES_7595, Items.ZOMBIE_BOOTS_7596)
                }

                Items.CAMO_TOP_10632 -> {
                    intArrayOf(Items.CAMO_HELMET_6656, Items.CAMO_TOP_6654, Items.CAMO_BOTTOMS_6655)
                }

                Items.LEDERHOSEN_TOP_10633 -> {
                    intArrayOf(Items.LEDERHOSEN_HAT_6182, Items.LEDERHOSEN_TOP_6180, Items.LEDERHOSEN_SHORTS_6181)
                }

                Items.SHADE_ROBE_10634 -> {
                    intArrayOf(Items.SHADE_ROBE_546, Items.SHADE_ROBE_548)
                }

                else -> return
            }

            val key = "set:$setIndex"
            val (labelId, iconId) = DRESS_BOX_COMPONENT_PAIRS.getOrNull(setIndex) ?: return

            if (getAttribute(player, key, false)) {
                if (freeSlots(player) >= set.size) {
                    set.forEach { addItem(player, it, 1) }
                    removeAttribute(player, key)
                    sendMessage(player, "You take the outfit from the wardrobe.")
                    sendInterfaceConfig(player, INTERFACE, labelId, false)
                    sendInterfaceConfig(player, INTERFACE, iconId + 1, false)
                } else {
                    sendMessage(player, "You don't have enough inventory space.")
                }
            } else {
                if (allInInventory(player, *set)) {
                    set.forEach { removeItem(player, Item(it, 1)) }
                    setAttribute(player, "/save:$key", true)
                    sendMessage(player, "You put your outfit into the wardrobe.")
                    sendInterfaceConfig(player, INTERFACE, labelId, true)
                    sendInterfaceConfig(player, INTERFACE, iconId + 1, true)
                } else {
                    sendMessage(player, "That isn't currently stored in the fancy dress box.")
                }
            }
        }

        private fun handleCapeRack(player: Player, setIndex: Int) {
            val item = CAPE_RACK_CONTENT.getOrNull(setIndex)
            val displayItem = CAPE_RACK_DISPLAY.getOrNull(setIndex)
            val (labelId, iconId) = CAPE_RACK_COMPONENT_PAIRS.getOrNull(setIndex)
                ?: return player.debug("Invalid cape component pair at index [$setIndex]")
            val key = "cape:$setIndex"

            if (item == null || displayItem == null) {
                player.debug("Invalid cape index: [$setIndex]")
                return
            }

            if (getAttribute(player, key, false)) {
                if (freeSlots(player) >= 1) {
                    addItem(player, item.id, 1)
                    removeAttribute(player, key)
                    sendMessage(player, "You take the cape from the rack.")
                    sendInterfaceConfig(player, INTERFACE, labelId, false)
                    sendInterfaceConfig(player, INTERFACE, iconId + 1, false)
                } else {
                    sendMessage(player, "You don't have enough inventory space.")
                }
            } else {
                if (removeItem(player, item)) {
                    setAttribute(player, "/save:$key", true)
                    sendMessage(player, "You put the cape into the rack.")
                    sendInterfaceConfig(player, INTERFACE, labelId, true)
                    sendInterfaceConfig(player, INTERFACE, iconId + 1, true)
                } else {
                    sendMessage(player, "That isn't currently stored in the cape rack.")
                }
            }
        }
    }
}
