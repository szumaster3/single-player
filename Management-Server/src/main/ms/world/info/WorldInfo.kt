package ms.world.info

/**
 * Holds the info of a world server.
 *
 * @author Emperor
 */
class WorldInfo(
    /**
     * The world id.
     */
    val worldId: Int,
    /**
     * The IP-address.
     */
    val address: String,
    /**
     * The revision of the world.
     */
    val revision: Int,
    /**
     * The country this world is located in.
     */
    val country: CountryFlag,
    /**
     * The world activity.
     */
    val activity: String,
    /**
     * If the world is members only.
     */
    val isMembers: Boolean,
    /**
     * If the world is a PvP world.
     */
    val isPvp: Boolean,
    /**
     * If the world is a quick chat only world.
     */
    val isQuickChat: Boolean,
    /**
     * If the world has lootshare option enabled.
     */
    val isLootshare: Boolean
) {

    val settings: Int
        /**
         * Gets the settings hash.
         *
         * @return The settings hash.
         */
        get() {
            var settings = 0
            if (isMembers) {
                settings = settings or 0x1
            }
            if (isQuickChat) {
                settings = settings or 0x2
            }
            if (isPvp) {
                settings = settings or 0x4
            }
            if (isLootshare) {
                settings = settings or 0x8
            }
            return settings
        }
}