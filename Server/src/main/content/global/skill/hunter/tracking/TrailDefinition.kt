package content.global.skill.hunter.tracking

import core.game.world.map.Location

/**
 * Represents a hunting trail that can be tracked by the player.
 *
 * @property varbit the variable bit associated with this trail
 * @property type the type of the trail (LINKING, INITIAL, TUNNEL)
 * @property inverted whether the trail is inverted
 * @property startLocation the starting location of the trail
 * @property endLocation the ending location of the trail
 * @property triggerObjectLocation the location of the object that triggers this trail; defaults to [endLocation]
 */
class TrailDefinition(
    val varbit: Int,
    val type: TrailType,
    var inverted: Boolean,
    val startLocation: Location,
    val endLocation: Location,
    val triggerObjectLocation: Location = endLocation,
) {
    override fun toString(): String =
        "$startLocation $endLocation [varbit: $varbit] [${type.name}] [inverted: $inverted]"
}

/**
 * Enum representing the type of hunting trail.
 */
enum class TrailType {
    /** A trail that links other trails. */
    LINKING,

    /** The initial trail that starts a tracking sequence. */
    INITIAL,

    /** A trail that represents a tunnel, such as for certain creatures. */
    TUNNEL,
}
