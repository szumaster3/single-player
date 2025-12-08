package content.region.misthalin.varrock.quest.dragon.plugin

import content.region.misthalin.varrock.quest.dragon.DragonSlayer
import core.api.playAudio
import core.api.replaceScenery
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.GroundItemManager
import shared.consts.Sounds

class DragonSlayerChestPlugin : InteractionListener {

    override fun defineListeners() {

        // Open chest
        on(2603, IntType.SCENERY, "open") { player, node ->
            player.packetDispatch.sendMessage("You open the chest.")
            replaceScenery(node.asScenery(), 2604, 1)
            playAudio(player, Sounds.CHEST_OPEN_52)
            return@on true
        }

        // Search & close
        on(2604, IntType.SCENERY, "search") { player, _ ->
            if (!player.inventory.containsItem(DragonSlayer.MAZE_PIECE)) {
                if (!player.inventory.add(DragonSlayer.MAZE_PIECE)) GroundItemManager.create(DragonSlayer.MAZE_PIECE, player)
                player.dialogueInterpreter.sendItemMessage(
                    DragonSlayer.MAZE_PIECE.id, "You find a map piece in the chest."
                )
            } else {
                player.packetDispatch.sendMessage("You find nothing in the chest.")
            }
            return@on true
        }

        on(2604, IntType.SCENERY, "close") { player, node ->
            player.packetDispatch.sendMessage("You shut the chest.")
            replaceScenery(node.asScenery(), 2603, -1)
            playAudio(player, Sounds.CHEST_CLOSE_51)
            return@on true
        }
    }
}
