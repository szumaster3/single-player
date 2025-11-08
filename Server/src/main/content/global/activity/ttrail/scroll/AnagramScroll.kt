package content.global.activity.ttrail.scroll

import content.global.activity.ttrail.ClueLevel
import content.global.activity.ttrail.ClueScroll
import core.api.getAttribute
import core.api.inInventory
import core.api.removeAttribute
import core.api.sendString
import core.game.interaction.Option
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import shared.consts.Components

/**
 * Represents an anagram clue scroll.
 */
abstract class AnagramScroll(
    name: String?,
    clueId: Int,
    private val anagram: String?,
    val npc: Int,
    level: ClueLevel?,
    val challenge: Int? = null
) : ClueScroll(name, clueId, level, Components.TRAIL_MAP09_345) {

    override fun interact(e: Entity, target: Node, option: Option): Boolean {
        val player = e as? Player ?: return false
        val npc = target as? NPC ?: return false
        return getClue(player, npc) != null
    }

    override fun read(player: Player) {
        repeat(8) { sendString(player, "", interfaceId, it + 1) }
        super.read(player)
        val text = anagram ?: "???"
        sendString(player, "This anagram reveals who to speak to next:\n$text", interfaceId, 1)
    }

    companion object {
        /**
         * Gets the active anagram scroll.
         */
        fun getClue(player: Player, npc: NPC): AnagramScroll? {
            player.inventory.toArray()
                .filterNotNull()
                .mapNotNull { getClueScrolls()[it.id] as? AnagramScroll }
                .firstOrNull { it.npc == npc.id }
                ?.let { return it }

            val clueId = getAttribute(player, "anagram_clue_active", -1)
            val activeClue = getClueScrolls()[clueId] as? AnagramScroll

            return if (activeClue != null && activeClue.npc == npc.id && inInventory(player, activeClue.clueId)) {
                activeClue
            } else {
                removeAttribute(player, "anagram_clue_active")
                null
            }
        }
    }
}