package ms.system.communication

/**
 * Represents the rank of a clan member.
 *
 * @author Emperor
 */
enum class ClanRank
/**
 * Constructs a new `ClanRank` `Object`.
 *
 * @param value The rank value.
 * @param info  The requirement info.
 */(
    /**
     * The value of the rank.
     */
    val value: Int,
    /**
     * The requirement info.
     */
    val info: String
) {
    NONE(-1, "Anyone"),
    FRIEND(0, "Any friends"),
    RECRUIT(1, "Recruit+"),
    CORPORAL(2, "Corporal+"),
    SERGEANT(3, "Sergeant+"),
    LIEUTENANT(4, "Lieutenant+"),
    CAPTAIN(5, "Captain+"),
    GENERAL(6, "General+"),
    OWNER(7, "Only me"),
    ADMINISTRATOR(127, "No-one")

    /**
     * Gets the value.
     *
     * @return The value.
     */

    /**
     * Gets the info.
     *
     * @return The info.
     */
}