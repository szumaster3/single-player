package content.global.skill.firemaking

import core.api.inInventory
import core.api.submitIndividualPulse
import core.cache.def.impl.ItemDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.item.GroundItem
import core.game.node.item.Item
import core.plugin.Initializable
import core.plugin.Plugin
import shared.consts.Items

/**
 * Represents the plugin used to light a log.
 * @author Vexia
 */
@Initializable
class LightLogOptionPlugin : OptionHandler() {

    override fun handle(player: Player, node: Node, option: String): Boolean {

        val mode = switch(player)
        if (mode == null) {
            player.packetDispatch.sendMessage(
                "You do not have the required items to light this."
            )
            return true
        }

        submitIndividualPulse(
            player,
            FireMakingPlugin(
                player,
                node as Item,
                node as GroundItem,
                mode
            )
        )
        return true
    }

    /**
     * Decides whether this is standard or barbarian fm.
     */
    private fun switch(player: Player): FiremakingMode? {
        return when {
            inInventory(player, Items.TINDERBOX_590, 1) ->
                FiremakingMode.STANDARD

            FireMakingListener.BARB_TOOLS.any { inInventory(player, it, 1) } ->
                FiremakingMode.BARBARIAN

            else -> null
        }
    }

    override fun newInstance(arg: Any?): Plugin<Any> {
        ItemDefinition.setOptionHandler("light", this)
        return this
    }
}
