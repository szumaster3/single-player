package content.global.ame.maze

import content.data.GameAttributes
import content.data.RandomEvent
import content.global.ame.RandomEventNPC
import core.api.*
import core.api.utils.WeightBasedTable
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.link.TeleportManager
import org.rs.consts.Components
import org.rs.consts.NPCs

class MazeNPC(override var loot: WeightBasedTable? = null) : RandomEventNPC(NPCs.MYSTERIOUS_OLD_MAN_410) {

    override fun init() {
        super.init()
        face(player)
        sendChat("Aha, you'll do ${player.username}!")
        runTask(player, 3) {
            registerLogoutListener(player, RandomEvent.logout()) { p ->
                p.location = getAttribute(p, RandomEvent.save(), player.location)
            }
            sendMessage(player, "You need to reach the maze center, then you'll be returned to where you were.")
            teleport(player, MazeInterface.STARTING_POINTS.random(), TeleportManager.TeleportType.NORMAL)
            runTask(player, 6) {
                openOverlay(player, Components.MAZETIMER_209)
                setAttribute(player, GameAttributes.MAZE_ATTRIBUTE_TICKS_LEFT, 300)
                setVarp(player, MazeInterface.MAZE_TIMER_VARP, (getAttribute(player, GameAttributes.MAZE_ATTRIBUTE_TICKS_LEFT, 0) / 3), false)
                sendNPCDialogue(player, NPCs.MYSTERIOUS_OLD_MAN_410, "You need to reach the maze center, then you'll be returned to where you were.")
                sendMessage(player, "Head for the center of the maze.")
            }
        }
    }

    override fun talkTo(npc: NPC) {
    }
}
