package content.global.skill.summoning.familiar.dialogue.titan

import content.global.skill.summoning.familiar.Familiar
import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.link.TeleportManager.TeleportType
import core.game.world.map.Location
import core.game.world.map.zone.impl.WildernessZone
import shared.consts.NPCs

/**
 * Handles interaction with Lava titan familiar.
 */
class LavaTitanOptionListener : InteractionListener {

    override fun defineListeners() {
        on(intArrayOf(NPCs.LAVA_TITAN_7341, NPCs.LAVA_TITAN_7342), IntType.NPC, "Interact") { player, node ->
            val familiar = node as? Familiar
            if (familiar == null) {
                return@on false
            }

            if (familiar.owner != player) {
                sendMessage(player, "This is not your follower.")
                return@on true
            }

            sendOptions(player, "Chat", "Teleport to Lava Maze")

            addDialogueAction(player) { p,b ->
                if(b == 2) {
                    closeDialogue(p).also {
                    openDialogue(p, LavaTitanDialogue())}
                    return@addDialogueAction
                }
                if(b == 3){
                    if (!WildernessZone.checkTeleport(p, 20)) {
                        closeDialogue(p)
                    } else {
                        closeDialogue(p)
                        teleport(p, Location(3030, 3842, 0), TeleportType.NORMAL)
                    }
                } else {
                    closeDialogue(p)
                }
            }
            return@on true
        }
    }
}