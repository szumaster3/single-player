package content.global.activity.ttrail.scroll

import content.global.activity.ttrail.ClueLevel
import content.global.activity.ttrail.ClueScroll
import core.api.sendString
import core.game.interaction.Option
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders
import shared.consts.Components

/**
 * Represents a challenge clue scroll.
 */
abstract class ChallengeClueScroll(
    name: String?,
    clueId: Int,
    level: ClueLevel?,
    val question: String?,
    val npc: Int?,
    val answer: Int?,
    vararg borders: ZoneBorders
) : ClueScroll(name, clueId, level, Components.TRAIL_MAP09_345, borders) {

    override fun read(player: Player) {
        repeat(8) { sendString(player, "", interfaceId, it + 1) }
        super.read(player)
        sendString(player, "<br><br><br><br><br>" + question?.replace("<br>", "<br><br>"), interfaceId, 1)
    }

    override fun interact(e: Entity, target: Node, option: Option): Boolean {
        val player = e as? Player ?: return false
        val npc = target as? NPC ?: return false
        return getClue(player, npc) != null
    }

    companion object {
        /**
         * Gets the challenge clue scroll item in inventory for the given NPC.
         */
        fun getClue(player: Player, npc: NPC): ChallengeClueScroll? =
            player.inventory.toArray()
                .filterNotNull()
                .mapNotNull { ClueScroll.getClueScrolls()[it.id] }
                .filterIsInstance<ChallengeClueScroll>()
                .firstOrNull { it.npc == npc.id }
    }
}
