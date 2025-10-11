package content.global.skill.crafting.pottery

import content.global.skill.crafting.CraftingObjects
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.Items

class FireOptionPlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles interaction with fire ovens.
         */

        on(CraftingObjects.POTTERY_OVENS, IntType.SCENERY, "fire") { player, _ ->
            sendSkillDialogue(player) {
                val potteryMap = mapOf(
                    Items.UNFIRED_POT_1787 to Pottery.POT,
                    Items.UNFIRED_PIE_DISH_1789 to Pottery.DISH,
                    Items.UNFIRED_BOWL_1791 to Pottery.BOWL,
                    Items.UNFIRED_PLANT_POT_5352 to Pottery.PLANT,
                    Items.UNFIRED_POT_LID_4438 to Pottery.LID
                )

                withItems(*potteryMap.keys.toIntArray())

                create { selectedItemId, amount ->
                    val pottery = potteryMap[selectedItemId]
                    if (pottery != null) {
                        player.pulseManager.run(FirePotteryPulse(player = player, node = pottery.unfinished, pottery = pottery, amount = amount))
                    }
                }

                calculateMaxAmount { selectedItemId ->
                    amountInInventory(player, selectedItemId)
                }
            }
            return@on true
        }

        /*
         * Handles interacting with the cooking range.
         */

        on(CraftingObjects.RANGE, IntType.SCENERY, "fire") { player, node ->
            if (inInventory(player, Items.UNCOOKED_STEW_2001, 1)) {
                faceLocation(player, node.location)
                openDialogue(player, 43989, Items.UNCOOKED_STEW_2001, "stew")
            }
            return@on true
        }
    }
}