package content.region.asgarnia.handlers

import content.data.GameAttributes
import content.region.misc.handlers.MinecartTravel
import core.api.*
import core.api.quest.isQuestComplete
import core.api.ui.closeDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.map.Location
import org.rs.consts.Quests
import org.rs.consts.Scenery

class MinecartListener : InteractionListener {

    private val MINECART = intArrayOf(Scenery.TRAIN_CART_7028, Scenery.TRAIN_CART_7029, Scenery.TRAIN_CART_7030)
    private val GRAND_EXCHANGE = Location.create(3140, 3507, 0)
    private val DWARVEN_MINE = Location.create(2997, 9837, 0)
    private val SINDARPOS = Location.create(2875, 9871, 0)

    override fun defineListeners() {
        on(MINECART, IntType.SCENERY, "ride") { player, node ->
            if (!player.getAttribute(GameAttributes.MINECART_TRAVEL_UNLOCK, false)) {
                sendDialogue(player, "You must visit Keldagrim to use this shortcut.")
                return@on true
            }

            val isCartFromKeldagrim = node.id == Scenery.TRAIN_CART_7028
            val hasFishingContest = isQuestComplete(player, Quests.FISHING_CONTEST)

            if (isCartFromKeldagrim) {
                val options = if (hasFishingContest) {
                    arrayOf("To the Grand Exchange.", "To Ice Mountain.", "To White Wolf Mountain.", "Stay here.")
                } else {
                    arrayOf("To the Grand Exchange.", "To Ice Mountain.", "Stay here.")
                }

                sendDialogueOptions(player, "Select an Option", *options)

                addDialogueAction(player) { _, option ->
                    when (option) {
                        2 -> MinecartTravel.leaveKeldagrimTo(player, GRAND_EXCHANGE)
                        3 -> MinecartTravel.leaveKeldagrimTo(player, DWARVEN_MINE)
                        4 -> if (hasFishingContest) {
                            MinecartTravel.leaveKeldagrimTo(player, SINDARPOS)
                        } else {
                            closeDialogue(player)
                        }
                        else -> closeDialogue(player)
                    }
                }
            } else {
                sendDialogueOptions(player, "Select an option", "Travel to Keldagrim.", "Stay here.")
                addDialogueAction(player) { _, option ->
                    when (option) {
                        2 -> MinecartTravel.goToKeldagrim(player)
                        else -> closeDialogue(player)
                    }
                }
            }

            return@on true
        }
    }
}