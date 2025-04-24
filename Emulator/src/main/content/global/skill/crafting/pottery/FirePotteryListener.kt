package content.global.skill.crafting.pottery

import core.api.amountInInventory
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.Items
import org.rs.consts.Scenery

class FirePotteryListener : InteractionListener {

    val SCENERY_IDS = intArrayOf(
        Scenery.POTTERY_OVEN_2643, Scenery.POTTERY_OVEN_4308,
        Scenery.POTTERY_OVEN_11601, Scenery.POTTERY_OVEN_34802
    )

    override fun defineListeners() {

        /*
         * Handles interaction with fire ovens.
         */

        on(SCENERY_IDS, IntType.NPC, "fire") { player, _ ->
            sendSkillDialogue(player) {
                withItems(
                    Items.UNFIRED_POT_1787,
                    Items.UNFIRED_PIE_DISH_1789,
                    Items.UNFIRED_BOWL_1791,
                    Items.UNFIRED_PLANT_POT_5352,
                    Items.UNFIRED_POT_LID_4438
                )
                create { index, amount ->
                    player.pulseManager.run(
                        FirePotteryPulse(
                            player = player,
                            node = Pottery.values()[index].unfinished,
                            pottery = Pottery.values()[index],
                            amount = amount,
                        ),
                    )
                }

                calculateMaxAmount { amount ->
                    amountInInventory(player, amount)
                }
            }

            return@on true
        }
    }
}