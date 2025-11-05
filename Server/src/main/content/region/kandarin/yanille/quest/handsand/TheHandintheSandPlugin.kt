package content.region.kandarin.yanille.quest.handsand

import core.api.*
import core.game.dialogue.FaceAnim
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction
import shared.consts.*

/**
 * Handles all interactions in The Hand in the Sand quest.
 */
class TheHandintheSandPlugin : InteractionListener {

    companion object {
        val BEER_IDS = intArrayOf(Items.GREENMANS_ALE_1909, Items.DRAGON_BITTER_1911)
        private val SANDY = intArrayOf(3110, 3111, NPCs.SANDY_3112, NPCs.SANDY_3113)

        val fakeContent = arrayOf(
            "Sandy's Sand Corp - Brimhaven",
                    "",
            "    Bert's Rota - Copy   ",
                    "",
            "Week 1 - 6am-10pm - 50gps",
                    "",
            "Week 2 - 6am-10pm - 50gps",
                    "",
            "Week 3 - 6am-10pm - 50gps",
                    "",
            "Week 4 - 6am-10pm - 50gps",
                    "",
            "Week 5 - 6am-10pm - 50gps",
                    "",
            "Week 6 - 6am-10pm - 50gps"
        )

        val originalContent = arrayOf(
            "Sandy's Sand Corp - Brimhaven",
            "",
            "    Bert's Rota - Original   ",
            "",
            "Week 1 - 9am-6pm - 50gps",
            "",
            "Week 2 - 9am-6pm - 50gps",
            "",
            "Week 3 - 9am-6pm - 50gps",
            "",
            "Week 4 - 9am-6pm - 50gps",
            "",
            "Week 5 - 9am-6pm - 50gps",
            "",
            "Week 6 - 9am-10pm - 50gps"
        )
    }

    override fun defineListeners() {

        /*
         * Handles using dif beers on guard captain.
         */

        onUseWith(IntType.NPC, BEER_IDS, NPCs.GUARD_CAPTAIN_3109) { player, _, with ->
            sendNPCDialogue(player, with.id, "Yeeeuuuch! I hatesh that shtuff, jusht bring ush a beer! Mmmmm beer!", FaceAnim.OLD_DRUNK_LEFT)
            return@onUseWith true
        }

        /*
         * Handles read the Bert rota.
         */

        on(Items.BERTS_ROTA_6947, IntType.ITEM, "Read") { player, _ ->
            openInterface(player, Components.BLANK_SCROLL_222)
            sendString(player, fakeContent.joinToString("<br>"), Components.BLANK_SCROLL_222, 1)
            return@on true
        }

        /*
         * Handles read the Sandy rota.
         */

        on(Items.SANDYS_ROTA_6948, IntType.ITEM, "Read") { player, _ ->
            openInterface(player, Components.BLANK_SCROLL_222)
            sendString(player, originalContent.joinToString("<br>"), Components.BLANK_SCROLL_222, 1)
            return@on true
        }

        on(SANDY, IntType.NPC, "Pickpocket") { player, _ ->
            player.animate(Animation(Animations.HUMAN_PICKPOCKETING_881))
            if(freeSlots(player) == 0) {
                sendDialogue(player, "I'd better make room in my inventory first!")
                return@on false
            }

            val random = RandomFunction.random(1,5)
            if(random == 1) {
                sendDialogue(player, "You rummage around in Sandy's pockets.....")
                sendMessage(player, "You find a small amount of sand.")
                addItem(player, Items.SAND_6958)
            } else {
                sendDialogue(player, "You felt something but it slipped through your fingers...")
            }
            return@on true
        }

    }

}