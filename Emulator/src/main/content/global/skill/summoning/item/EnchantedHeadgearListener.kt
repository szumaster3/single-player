package content.global.skill.summoning.item

import core.api.*
import core.api.quest.isQuestComplete
import core.game.dialogue.FaceAnim
import core.game.dialogue.SequenceDialogue.dialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.update.flag.context.Graphics
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests

class EnchantedHeadgearListener : InteractionListener {

    private val allowedScrolls = intArrayOf(
        Items.HOWL_SCROLL_12425, Items.DREADFOWL_STRIKE_SCROLL_12445, Items.SLIME_SPRAY_SCROLL_12459,
        Items.PESTER_SCROLL_12838, Items.ELECTRIC_LASH_SCROLL_12460, Items.FIREBALL_ASSAULT_SCROLL_12839,
        Items.SANDSTORM_SCROLL_12446, Items.VAMPIRE_TOUCH_SCROLL_12447, Items.BRONZE_BULL_RUSH_SCROLL_12461,
        Items.EVIL_FLAMES_SCROLL_12448, Items.PETRIFYING_GAZE_SCROLL_12458, Items.IRON_BULL_RUSH_SCROLL_12462,
        Items.ABYSSAL_DRAIN_SCROLL_12454, Items.DISSOLVE_SCROLL_12453, Items.AMBUSH_SCROLL_12836,
        Items.RENDING_SCROLL_12840, Items.DOOMSPHERE_SCROLL_12455, Items.DUST_CLOUD_SCROLL_12468,
        Items.STEEL_BULL_RUSH_SCROLL_12463, Items.POISONOUS_BLAST_SCROLL_12467, Items.MITH_BULL_RUSH_SCROLL_12464,
        Items.TOAD_BARK_SCROLL_12452, Items.FAMINE_SCROLL_12830, Items.ARCTIC_BLAST_SCROLL_12451,
        Items.RISE_FROM_THE_ASHES_SCROLL_14622, Items.CRUSHING_CLAW_SCROLL_12449, Items.MANTIS_STRIKE_SCROLL_12450,
        Items.INFERNO_SCROLL_12841, Items.ADDY_BULL_RUSH_SCROLL_12465, Items.DEADLY_CLAW_SCROLL_12831,
        Items.ACORN_MISSILE_SCROLL_12457, Items.SPIKE_SHOT_SCROLL_12456, Items.EBON_THUNDER_SCROLL_12837,
        Items.SWAMP_PLAGUE_SCROLL_12832, Items.RUNE_BULL_RUSH_SCROLL_12466, Items.BOIL_SCROLL_12833,
        Items.IRON_WITHIN_SCROLL_12828, Items.STEEL_OF_LEGENDS_SCROLL_12825
    )

    override fun defineListeners() {

        /*
         * Handles Enchant headgear.
         */

        on(NPCs.PIKKUPSTIX_6970, IntType.NPC, "Enchant") { player, _ ->

            if (!isQuestComplete(player, Quests.WOLF_WHISTLE)) {
                sendMessage(player, "You need to complete Wolf Whistle to enchant the headgear.")
                return@on true
            }

            if (!anyInInventory(player, *defaultIDs)) {
                sendNPCDialogue(player, NPCs.PIKKUPSTIX_6970, "You do not have items that can be enchanted.")
                return@on true
            }

            sendNPCDialogue(player, NPCs.PIKKUPSTIX_6970, "What would you like disenchanted or enchanted?")
            addDialogueAction(player) { _, _ ->
                sendItemSelect(player, "Choose an item to enchant:") { slot, _ ->
                    val item = player.inventory[slot] ?: return@sendItemSelect
                    enchant(player, item, 1)
                }
            }
            return@on true
        }

        /*
         * Handles uncharge the headgear.
         */

        on(chargedIDs, IntType.ITEM, "Uncharge") { player, node ->
            val item = node.asItem() ?: return@on true
            val enchManager = player.enchgearManager

            if (!enchManager.hasScrolls(item.id)) {
                sendDialogue(player, "There are no scrolls stored in this headgear.")
                return@on true
            }

            if (freeSlots(player) == 0) {
                sendDialogue(player, "You need at least one free inventory slot to remove the scrolls.")
                return@on true
            }

            enchManager.withdrawAllScrolls(item.id)
            return@on true
        }

        /*
         * Handles check stored scrolls in charged headgear.
         */

        on(chargedIDs, IntType.ITEM, "Commune", "Operate") { player, node ->
            val item = node.asItem() ?: return@on true
            val enchManager = player.enchgearManager

            val chargedHeadgear = enchManager.enchantedGear[item.id]

            if (chargedHeadgear == null || chargedHeadgear.container.toArray().all { it == null }) {
                sendMessage(player, "This headgear has no stored scrolls.")
            } else {
                val scrollsDesc = chargedHeadgear.container.toArray()
                    .filterNotNull()
                    .joinToString(", ") { "${it.amount} x ${it.id}" }
                sendDialogue(player, "Stored scrolls: $scrollsDesc")
            }
            return@on true
        }

        // Obsługa użycia scrolla na enchanted headgear
        onUseWith(IntType.ITEM, enchantedIDs, *allowedScrolls) { player, used, with ->
            val enchantedItem = used.asItem() ?: return@onUseWith true
            val scrollItem = with.asItem() ?: return@onUseWith true

            val headgear = EnchantedHeadgear.forEnchanted(enchantedItem) ?: return@onUseWith true

            if (getStatLevel(player, Skills.SUMMONING) < headgear.requiredLevel) {
                sendMessage(player, "You need Summoning level ${headgear.requiredLevel} to enchant this headgear.")
                return@onUseWith true
            }

            val chargedItem = EnchantedHeadgear.getChargedItem(enchantedItem) ?: return@onUseWith true

            val enchManager = player.enchgearManager
            enchManager.addScrolls(chargedItem.id, scrollItem.id, scrollItem.amount)

            val slot = enchantedItem.index
            if (slot >= 0) {
                replaceSlot(player, slot, chargedItem)
            } else {
                sendMessage(player, "Failed to update your inventory.")
            }

            return@onUseWith true
        }
    }

    companion object {
        private val defaultIDs = EnchantedHeadgear.values().map { it.defaultItem.id }.toIntArray()
        private val enchantedIDs = EnchantedHeadgear.values().map { it.enchantedItem.id }.toIntArray()
        private val chargedIDs = EnchantedHeadgear.values().map { it.chargedItem.id }.toIntArray()

        fun enchant(player: Player, item: Item, option: Int): Boolean {
            val headgear = EnchantedHeadgear.forItem(item) ?: return false

            if (getStatLevel(player, Skills.SUMMONING) < headgear.requiredLevel) return false

            if (option == 1) {
                lock(player, 1)
                animate(findLocalNPC(player, NPCs.PIKKUPSTIX_6970) ?: return false, Animations.CAST_SPELL_711)
                sendGraphics(Graphics(434, 100), player.location)

                if (item.id == headgear.defaultItem.id) {
                    removeItem(player, headgear.defaultItem.id)
                    addItem(player, headgear.enchantedItem.id)

                    val npc = NPC(NPCs.PIKKUPSTIX_6971)
                    dialogue(player) {
                        npc(npc, FaceAnim.NEUTRAL, "Good choice. Here you go, you can now store spells on it.")
                        player(FaceAnim.CALM_TALK, "Excellent. Thank you!")
                    }
                    return true
                }
            }
            return false
        }
    }
}
