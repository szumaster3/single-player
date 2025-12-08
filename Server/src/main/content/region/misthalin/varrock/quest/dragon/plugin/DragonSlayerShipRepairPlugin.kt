package content.region.misthalin.varrock.quest.dragon.plugin

import content.region.misthalin.varrock.quest.dragon.DragonSlayer
import core.api.sendDialogue
import core.api.setVarp
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.update.flag.context.Animation
import shared.consts.Animations

class DragonSlayerShipRepairPlugin : InteractionListener {

    override fun defineListeners() {

        listOf(25036, 2589).forEach { id ->

            on(id, IntType.SCENERY, "repair", "fix", "use") { player, _ ->

                val memorized = player.getSavedData().questData.getDragonSlayerAttribute("memorized")
                if (memorized) {
                    player.dialogueInterpreter.sendDialogue(
                        "You don't need to mess about with broken ships now that you have",
                        "found the secret passage from Karamja."
                    )
                    return@on true
                }

                when {
                    !player.inventory.containsItem(DragonSlayer.NAILS) -> sendDialogue(
                        player,
                        "You need 30 steel nails to attach the plank with."
                    )

                    !player.inventory.containsItem(DragonSlayer.PLANK) -> sendDialogue(
                        player,
                        "You'll need to use wooden planks on this hole to patch it up."
                    )

                    !player.inventory.containsItem(DragonSlayer.HAMMER) -> sendDialogue(
                        player,
                        "You need a hammer to force the nails in with."
                    )

                    else -> {
                        player.inventory.remove(DragonSlayer.NAILS)
                        player.inventory.remove(DragonSlayer.PLANK)
                        player.animate(Animation(Animations.BUILD_WITH_HAMMER_3676))
                        player.getSavedData().questData.dragonSlayerPlanks++

                        if (player.getSavedData().questData.dragonSlayerPlanks < 3) {
                            player.dialogueInterpreter.sendDialogue(
                                "You nail a plank over the hole, but you still need more planks to",
                                "close the hole completely."
                            )
                        } else {
                            player.getSavedData().questData.setDragonSlayerAttribute("repaired", true)
                            setVarp(player, 177, 1967876)
                            player.dialogueInterpreter.sendDialogue(
                                "You nail a final plank over the hole. You have successfully patched",
                                "the hole in the ship."
                            )
                        }
                    }
                }
                return@on true
            }
        }
    }
}
