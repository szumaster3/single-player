package content.region.asgarnia.handlers

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

    override fun defineListeners() {
        on(MINECART, IntType.SCENERY, "ride") { player, node ->
            if (!player.getAttribute("keldagrim-visited", false)) {
                sendDialogue(player, "You must visit Keldagrim to use this shortcut.")
                return@on true
            }

            val isCartFromKeldagrim = node.id == Scenery.TRAIN_CART_7028
            val hasFishingContest = isQuestComplete(player, Quests.FISHING_CONTEST)

            if (isCartFromKeldagrim) {
                val options = mutableListOf(
                    "To the Grand Exchange.",
                    "To Ice Mountain."
                )
                if (hasFishingContest) options.add("To White Wolf Mountain.")
                options.add("Stay here.")

                sendDialogueOptions(player, "Select an option", *options.toTypedArray())

                addDialogueAction(player) { _, option ->
                    when (option) {
                        2 -> MinecartTravel.leaveKeldagrimTo(player, Location.create(3140, 3507, 0)) // Ice Mountain
                        3 -> MinecartTravel.leaveKeldagrimTo(player, Location.create(2997, 9837, 0)) // GE
                        4 -> if (hasFishingContest) {
                            MinecartTravel.leaveKeldagrimTo(player, Location.create(2875, 9871, 0)) // White Wolf
                        }
                    }
                    closeDialogue(player)
                }
            } else {
                sendDialogueOptions(player, "Select an option", "Travel to Keldagrim.", "Stay here.")
                addDialogueAction(player) { _, option ->
                    if (option == 2) {
                        MinecartTravel.goToKeldagrim(player)
                    }
                    closeDialogue(player)
                }
            }

            return@on true
        }
    }
}