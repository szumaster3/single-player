package content.global.skill.construction.decoration.diningroom

import content.global.skill.construction.servants.Servant
import core.cache.def.impl.SceneryDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.plugin.Plugin
import shared.consts.Scenery

/**
 * Handles the bell pull interaction.
 */
@Initializable
class BellPullPlugin : OptionHandler() {

    override fun newInstance(arg: Any?): Plugin<Any> {
        SceneryDefinition.forId(Scenery.ROPE_BELL_PULL_13307).handlers["option:ring"] = this
        SceneryDefinition.forId(Scenery.BELL_PULL_13308).handlers["option:ring"] = this
        SceneryDefinition.forId(Scenery.POSH_BELL_PULL_13309).handlers["option:ring"] = this
        return this
    }

    override fun handle(player: Player?, node: Node?, option: String?): Boolean {
        val manager = player?.houseManager ?: return true
        val servant = manager.servant
        if (servant == null || !manager.hasServant()) {
            player.sendMessage("You have no servant to ring.")
            return true
        }
        servant.init()
        return true
    }
}