package content.data

import core.api.ui.sendInterfaceConfig
import core.game.node.entity.player.Player
import org.rs.consts.Components

object RandomEvent {
    /**
     * Returns the event path for logging out.
     */
    @JvmStatic
    fun logout(): String = "/save:${javaClass.simpleName}-logout"

    /**
     * Returns the event path for saving the original location.
     */
    @JvmStatic
    fun save(): String = "/save:original-loc"

    /**
     * Returns the event path for saving the score.
     */
    @JvmStatic
    fun score(): String = "/save:${javaClass.simpleName}:score"


    /**
     * Hide logout button (both SD/HD variant).
     */
    @JvmStatic
    fun hideLogout(player: Player, hide: Boolean) {
        sendInterfaceConfig(player, Components.TOPLEVEL_548, 69, hide)
        sendInterfaceConfig(player, Components.TOPLEVEL_FULLSCREEN_746, 12, hide)
    }
}
