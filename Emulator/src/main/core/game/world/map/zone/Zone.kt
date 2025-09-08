package core.game.world.map.zone

import core.game.node.entity.Entity

/**
 * Represents a zone.
 *
 * @author Emperor
 */
interface Zone {
    /**
     * Checks if the entity can enter this map zone.
     *
     * @param e The entity.
     * @return `True` if so.
     */
    fun enter(e: Entity): Boolean

    /**
     * Called when the entity leaves this map zone.
     *
     * @param e      The entity.
     * @param logout If the entity is logging out.
     * @return `True` if the entity can leave.
     */
    fun leave(e: Entity, logout: Boolean): Boolean
}