package content.global.skill.summoning.items

import core.api.*
import core.game.node.entity.player.Player
import core.game.node.item.Item
import org.rs.consts.Items

/**
 * Handles the storage and retrieval of Summoning scrolls inside enchanted headgear items.
 */
object EnchantedHeadgearScrolls {
    private const val MAX_AMOUNT = 0xFFFF

    val allowedScrollIDs = intArrayOf(
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

    private fun isAllowedScroll(scrollId: Int): Boolean = scrollId in allowedScrollIDs

    private fun encodeCharge(scrollId: Int, amount: Int): Int {
        val safeAmount = amount.coerceIn(0, MAX_AMOUNT)
        return (scrollId shl 16) or safeAmount
    }

    fun decodeCharge(charge: Int): Pair<Int?, Int> {
        if (charge == 0) return null to 0
        val scrollId = (charge ushr 16) and 0xFFFF
        val amount = charge and 0xFFFF
        return if (isAllowedScroll(scrollId)) scrollId to amount else null to 0
    }

    fun addScroll(player: Player, item: Item, scrollId: Int, amount: Int): Boolean {
        val gear = EnchantedHeadgear.forItem(item) ?: return false

        if (!isAllowedScroll(scrollId)) return false

        val (storedScrollId, storedAmount) = decodeCharge(item.charge)
        if (storedScrollId != null && storedScrollId != scrollId) return false

        val newAmount = (storedAmount + amount).coerceAtMost(gear.scrollCapacity)
        item.charge = encodeCharge(scrollId, newAmount)

        player.inventory.remove(Item(scrollId, amount))
        return true
    }

    fun checkScrolls(player: Player, item: Item) {
        val (scrollId, amount) = decodeCharge(item.charge)
        if (scrollId == null || amount <= 0) {
            sendDialogue(player, "There are no scrolls stored in this headgear.")
        } else {
            val scrollName = getItemName(item.id)
            sendDialogue(player, "The headgear contains: $amount $scrollName${if (amount > 1) "s" else ""}.")
        }
    }

    fun uncharge(player: Player, item: Item) {
        val (scrollId, amount) = decodeCharge(item.charge)
        if (scrollId != null && amount > 0) {
            player.inventory.add(Item(scrollId, amount))
            item.charge = 0
            sendMessages(
                player,
                "You remove the scrolls. You will need to use a Summoning scroll on it to charge the",
                "headgear up once more."
            )
        } else {
            sendMessages(player, "There are no scrolls to remove.")
        }
    }
}
