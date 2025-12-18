package content.global.skill.hunter

import core.game.node.entity.npc.NPC
import core.game.node.entity.skill.Skills
import core.game.node.item.Item

/**
 * Represents a single trap node configuration.
 *
 * @property npcIds array of npc ids this trap node can catch
 * @property level required Hunter level to use this trap node
 * @property experience experience gained when catching an NPC
 * @property objectIds array of object ids representing this trap in the world (e.g., trap, trap fail)
 * @property rewards array of items given to the player upon successful catch
 */
open class TrapNode(
    val npcIds: IntArray,
    @JvmField val level: Int,
    val experience: Double,
    val objectIds: IntArray,
    @JvmField val rewards: Array<Item>
) {

    /**
     * Determines whether the given NPC can be caught by this trap node.
     *
     * @param wrapper the trap wrapper instance holding the trap state
     * @param npc the NPC to attempt to catch
     * @return true if the NPC can be caught, false otherwise
     */
    open fun canCatch(wrapper: TrapWrapper, npc: NPC): Boolean {
        val player = wrapper.player

        // NPC cannot be caught if the trap is busy, already caught, or failed
        if (wrapper.isCaught || wrapper.isBusy || wrapper.isFailed) {
            return false
        }

        // Player must have required Hunter level and NPC must be visible
        return player.skills.getStaticLevel(Skills.HUNTER) >= level && !npc.isInvisible
    }

    /**
     * Gets the initial object id representing this trap node in the world.
     */
    val transformId: Int
        get() = objectIds[0]

    /**
     * Gets the final object id for this trap node (e.g., failed trap or reset state).
     */
    val finalId: Int
        get() = objectIds[1]
}