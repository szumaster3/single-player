package content.global.skill.agility.shortcuts

import content.global.skill.agility.AgilityHandler
import content.global.skill.agility.AgilityShortcut
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.world.update.flag.context.Animation
import core.plugin.Initializable
import org.rs.consts.Animations

/**
 * Represents the Oo'glog rock passage shortcut.
 */
@Initializable
class OoglogRockPassageShortcut : AgilityShortcut(intArrayOf(29099), 29, 0.0, "squeeze-through") {

    override fun run(player: Player, scenery: Scenery, option: String, failed: Boolean)
    {
        val destination = player.location.transform(0, if (player.location.y == 2871) -2 else 2, 0)
        val animation = Animation(Animations.DUCK_UNDER_2240)

        AgilityHandler.forceWalk(
            player,
            -1,
            player.location,
            destination,
            animation,
            5,
            0.0,
            null,
            1
        )
    }
}