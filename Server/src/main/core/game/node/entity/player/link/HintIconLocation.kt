package core.game.node.entity.player.link

/**
 * The enum Hint icon location.
 */
enum class HintIconLocation(
    /**
     * Gets location.
     *
     * @return the location
     */
    val location: Int
) {
    /**
     * Entity hint icon location.
     */
    ENTITY(1),

    /**
     * Center hint icon location.
     */
    CENTER(2),

    /**
     * West hint icon location.
     */
    WEST(3),

    /**
     * East hint icon location.
     */
    EAST(4),

    /**
     * South hint icon location.
     */
    SOUTH(5),

    /**
     * North hint icon location.
     */
    NORTH(6)
}