package content.global.dialogue

import content.region.asgarnia.falador.dialogue.FaladorShopkeeperDialogue
import content.region.asgarnia.rimmington.dialogue.RimmingtonShopkeeperDialogue
import content.region.desert.al_kharid.dialogue.AlKharidShopkeeperDialogue
import content.region.misthalin.edgeville.dialogue.EdgevilleShopKeeperDialogue
import core.api.openDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.NPCs

class ShopkeeperPlugin : InteractionListener {

    override fun defineListeners() {
        on(intArrayOf(NPCs.SHOPKEEPER_524, NPCs.SHOP_ASSISTANT_525), IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AlKharidShopkeeperDialogue(), node.asNpc())
            return@on true
        }

        on(intArrayOf(NPCs.SHOPKEEPER_526, NPCs.SHOP_ASSISTANT_527), IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, FaladorShopkeeperDialogue(), node.asNpc())
            return@on true
        }

        on(intArrayOf(NPCs.SHOPKEEPER_528, NPCs.SHOP_ASSISTANT_529), IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, EdgevilleShopKeeperDialogue(), node.asNpc())
            return@on true
        }

        on(intArrayOf(NPCs.SHOPKEEPER_530, NPCs.SHOP_ASSISTANT_531), IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, RimmingtonShopkeeperDialogue(), node.asNpc())
            return@on true
        }
    }
}