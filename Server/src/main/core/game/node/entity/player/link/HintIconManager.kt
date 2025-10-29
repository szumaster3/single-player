package core.game.node.entity.player.link

import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.net.packet.OutgoingContext
import core.net.packet.PacketRepository.send
import core.net.packet.out.HintIcon

/**
 * Manages the hint icons displayed for a player.
 *
 * @author Emperor
 */
class HintIconManager {

    /**
     * Stores currently active hint icons.
     * Each slot may contain one [OutgoingContext.HintIcon] or be `null` if unused.
     */
    private val hintIcons: Array<OutgoingContext.HintIcon?> = arrayOfNulls(MAXIMUM_SIZE)

    /**
     * Cached index of the next free slot, to reduce search time when many icons are removed/added.
     */
    private var cachedFreeSlot: Int = 0

    /**
     * Removes all hint icons currently registered for this player.
     */
    fun clear() {
        hintIcons.indices.filter { hintIcons[it] != null }.forEach { index ->
                hintIcons[index]?.let { removeHintIcon(it.player, index) }
            }
        cachedFreeSlot = 0
    }

    /**
     * Finds the first available (free) hint icon slot.
     *
     * @return The first free slot index, or -1 if all slots are occupied.
     */
    fun freeSlot(): Int {
        for (i in cachedFreeSlot until MAXIMUM_SIZE) {
            if (hintIcons[i] == null) {
                cachedFreeSlot = i
                return i
            }
        }
        // Reset search in case lower slots were freed.
        for (i in 0 until cachedFreeSlot) {
            if (hintIcons[i] == null) {
                cachedFreeSlot = i
                return i
            }
        }
        return -1
    }

    /**
     * Gets the hint icon currently assigned to the given slot.
     *
     * @param slot The slot index.
     * @return The [OutgoingContext.HintIcon] at that slot, or `null` if empty or invalid.
     */
    fun getIcon(slot: Int): OutgoingContext.HintIcon? = hintIcons.getOrNull(slot)

    companion object {
        /**
         * Maximum number of hint icons a player can have active simultaneously.
         */
        const val MAXIMUM_SIZE: Int = 8

        /**
         * Default arrow sprite id for a hint icon.
         */
        const val DEFAULT_ARROW: Int = 1

        /**
         * Default model id for hint icons without a custom model.
         */
        const val DEFAULT_MODEL: Int = -1

        /**
         * Model id for an arrow enclosed within a circle.
         */
        const val ARROW_CIRCLE_MODEL: Int = 40497

        /**
         * Registers a height-based hint icon.
         *
         * @param player The player receiving the icon.
         * @param target The target node (entity or object).
         * @param height The vertical height offset.
         * @return The slot number assigned to the icon, or -1 if no slot was available.
         */
        fun registerHeightHintIcon(player: Player, target: Node, height: Int): Int =
            registerHintIcon(player, target, DEFAULT_ARROW, DEFAULT_MODEL, player.hintIconManager.freeSlot(), height)

        /**
         * Registers a new hint icon with a specific arrow type.
         *
         * @param player The player receiving the icon.
         * @param target The target node.
         * @param arrowId The arrow id to display.
         * @return The slot index assigned, or -1 if none available.
         */
        fun registerHintIcon(player: Player, target: Node?, arrowId: Int): Int =
            registerHintIcon(player, target, arrowId, DEFAULT_MODEL, player.hintIconManager.freeSlot())

        /**
         * Registers a new hint icon with a custom arrow and model.
         *
         * @param player The player receiving the icon.
         * @param target The target node.
         * @param arrowId The arrow sprite id.
         * @param modelId The model id to use.
         * @return The slot index assigned, or -1 if none available.
         */
        fun registerHintIcon(player: Player, target: Node?, arrowId: Int, modelId: Int): Int =
            registerHintIcon(player, target, arrowId, modelId, player.hintIconManager.freeSlot())

        /**
         * Registers a generic hint icon.
         *
         * @param player The player receiving the icon.
         * @param target The target node.
         * @param arrowId The arrow sprite id.
         * @param modelId The model id to use.
         * @param slot The slot index to occupy.
         * @return The assigned slot index, or -1 if invalid.
         */
        @JvmOverloads
        fun registerHintIcon(
            player: Player,
            target: Node?,
            arrowId: Int = DEFAULT_ARROW,
            modelId: Int = DEFAULT_MODEL,
            slot: Int = player.hintIconManager.freeSlot()
        ): Int {
            if (slot < 0 || target == null) return -1

            val manager = player.hintIconManager
            val icon = OutgoingContext.HintIcon(player, slot, arrowId, target, modelId)
            send(HintIcon::class.java, icon)
            manager.hintIcons[slot] = icon
            return slot
        }

        /**
         * Registers a hint icon with a specific height offset.
         *
         * @param player The player.
         * @param target The node to target.
         * @param arrowId The arrow sprite id.
         * @param modelId The model id.
         * @param slot The icon slot.
         * @param height The vertical height offset.
         * @return The slot index assigned, or -1 if invalid.
         */
        fun registerHintIcon(
            player: Player, target: Node, arrowId: Int, modelId: Int, slot: Int, height: Int
        ): Int {
            val type = when (target) {
                is Player -> 10
                is Entity -> 1
                else -> 2
            }
            return registerHintIcon(player, target, arrowId, modelId, slot, height, type)
        }

        /**
         * Registers a fully customized hint icon.
         *
         * @param player The player.
         * @param target The target node.
         * @param arrowId The arrow sprite id.
         * @param modelId The model id.
         * @param slot The slot to occupy.
         * @param height The height offset.
         * @param targetType The target type id.
         * @return The assigned slot index, or -1 if invalid.
         */
        fun registerHintIcon(
            player: Player, target: Node, arrowId: Int, modelId: Int, slot: Int, height: Int, targetType: Int
        ): Int {
            if (slot < 0) return -1

            val manager = player.hintIconManager
            val icon = OutgoingContext.HintIcon(player, slot, arrowId, targetType, target, modelId, height)
            send(HintIcon::class.java, icon)
            manager.hintIcons[slot] = icon
            return slot
        }

        /**
         * Removes a hint icon at the specified slot for a player.
         *
         * @param player The player.
         * @param slot The slot index to clear.
         */
        fun removeHintIcon(player: Player, slot: Int) {
            if (slot < 0) return

            val manager = player.hintIconManager
            val icon = manager.hintIcons.getOrNull(slot) ?: return

            icon.targetType = 0
            send(HintIcon::class.java, icon)
            manager.hintIcons[slot] = null
        }
    }
}
