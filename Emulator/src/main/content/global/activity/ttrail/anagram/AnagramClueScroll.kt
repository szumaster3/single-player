package content.global.activity.ttrail.anagram

import content.global.activity.ttrail.ClueLevel
import content.global.activity.ttrail.ClueScrollPlugin
import core.api.ui.setInterfaceText
import core.game.interaction.Option
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import org.rs.consts.Components

abstract class AnagramClueScroll(
    name: String?,
    clueId: Int,
    val anagram: String?,
    val npcId: Int,
    level: ClueLevel?,
    val challenge: Int? = null
) : ClueScrollPlugin(name, clueId, level, Components.TRAIL_MAP09_345) {

    override fun read(player: Player) {
        repeat(8) { index ->
            setInterfaceText(player, "", interfaceId, index + 1)
        }

        super.read(player)
        setInterfaceText(player, "<br><br><br><br>This anagram reveals<br>who to speak to next:<br><br><br>$anagram", interfaceId, 1)
    }

    override fun interact(
        e: Entity,
        target: Node,
        option: Option,
    ): Boolean {
        return super.interact(e, target, option)
    }
}
