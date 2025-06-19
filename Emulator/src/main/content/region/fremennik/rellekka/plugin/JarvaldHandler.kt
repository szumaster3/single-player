package content.region.fremennik.rellekka.plugin

import core.cache.def.impl.NPCDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.plugin.Plugin

/**
 * Handles Jarvald NPC options.
 */
@Initializable
class JarvaldHandler : OptionHandler() {
    override fun newInstance(arg: Any?): Plugin<Any> {
        NPCDefinition.forId(2435).handlers["option:travel"] = this
        NPCDefinition.forId(2436).handlers["option:travel"] = this
        NPCDefinition.forId(2437).handlers["option:travel"] = this
        NPCDefinition.forId(2438).handlers["option:travel"] = this
        return this
    }

    override fun handle(player: Player?, node: Node?, option: String?): Boolean {
        val target = node as? NPC ?: return false
        player?.dialogueInterpreter?.open(target.id, target, true)
        return true
    }
}