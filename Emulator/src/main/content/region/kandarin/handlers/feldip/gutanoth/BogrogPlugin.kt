package content.region.kandarin.handlers.feldip.gutanoth

import content.region.kandarin.dialogue.gutanoth.BogrogDialogue
import content.region.kandarin.handlers.feldip.gutanoth.BogrogPouchSwapper.handle
import core.api.getStatLevel
import core.api.sendItemSelect
import core.api.sendMessage
import core.cache.def.impl.NPCDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.plugin.ClassScanner.definePlugin
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.NPCs

/**
 * Handles bogrog NPC for summoning pouch swapping.
 */
@Initializable
class BogrogPlugin : OptionHandler() {

    @Throws(Throwable::class)
    override fun newInstance(arg: Any?): Plugin<Any> {
        registerPlugin()
        return this
    }

    private fun registerPlugin() {
        NPCDefinition.forId(NPCs.BOGROG_4472).handlers["option:swap"] = this
        definePlugin(BogrogDialogue())
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        if ("swap" == option) {
            openSwap(player)
        }
        return true
    }

    companion object {
        /**
         * Opens the pouch swapping interface.
         *
         * @param player the player.
         */
        fun openSwap(player: Player) {
            if (getStatLevel(player, Skills.SUMMONING) < 21) {
                sendMessage(player, "You need a Summoning level of at least 21 in order to do that.")
            } else {
                sendItemSelect(
                    player,
                    "Value",
                    "Swap 1",
                    "Swap 5",
                    "Swap 10",
                    "Swap X",
                    keepAlive = true
                ) { slot, index ->
                    handle(player, index, slot)
                }
            }
        }
    }
}