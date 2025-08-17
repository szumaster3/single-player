package content.region.kandarin.ardougne.west.plugin

import core.api.*
import core.cache.def.impl.SceneryDefinition
import core.game.global.action.DoorActionHandler
import core.game.interaction.OptionHandler
import core.game.interaction.QueueStrength
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.game.world.map.Direction
import core.plugin.Initializable
import core.plugin.Plugin
import shared.consts.NPCs
import shared.consts.Quests
import shared.consts.Sounds

/**
 * Handles interactions with the West Ardougne doors.
 */
@Initializable
class WestArdougneDoorPlugin : OptionHandler() {

    override fun newInstance(arg: Any?): Plugin<Any>? {
        SceneryDefinition.forId(9738).handlers["option:open"] = this
        SceneryDefinition.forId(9330).handlers["option:open"] = this
        return this
    }

    override fun handle(p: Player, n: Node, option: String): Boolean {
        val d1 = n as? Scenery ?: return false
        val d2 = DoorActionHandler.getSecondDoor(d1)
        if (!isQuestComplete(p, Quests.BIOHAZARD)) {
            sendMessage(p, "You try to open the large wooden doors...")
            sendMessage(p, "...But they will not open.", 1)
            if (p.location.x > 2557) sendNPCDialogue(p, NPCs.MOURNER_2349, "Oi! What are you doing? Get away from there!")
            return true
        }
        sendMessage(p, "You pull on the large wooden doors...")
        fun open(s: Scenery?) = s?.let { Scenery(if (it.id == 9738) it.id + 2 else 9330, it.location.transform(-1, 0, 0), 10, if (it.id == 9738) 5 else 3) }
        SceneryBuilder.replace(d1, open(d1)!!, 4)
        open(d2)?.let { SceneryBuilder.replace(d2, it, 4) }
        queueScript(p, 1, QueueStrength.SOFT) {
            Direction.getLogicalDirection(p.location, d1.location)?.let { dir ->
                playAudio(p, Sounds.BIG_WOODEN_DOOR_OPEN_44)
                forceMove(p, p.location, p.location.transform(dir, 2), 0, 60, dir, 0x333)
                playAudio(p, Sounds.BIG_WOODEN_DOOR_CLOSE_43, 2)
            }
            sendMessage(p, "...You open them and walk through.")
            return@queueScript stopExecuting(p)
        }
        return true
    }
}
