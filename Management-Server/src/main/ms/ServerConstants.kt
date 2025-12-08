package ms

import ms.system.OperatingSystem
import java.util.*

/**
 * Holds constants for the management server.
 *
 * @author v4rg
 */
object ServerConstants {
    /**
     * The port to be used for communications.
     */
    const val PORT: Int = 5555

    /**
     * The maximum amount of worlds.
     */
    const val WORLD_LIMIT: Int = 10

    /**
     * The world switching delay in milliseconds.
     */
    const val WORLD_SWITCH_DELAY: Long = 20000L

    /**
     * The store path.
     */
    const val STORE_PATH: String = "./store/"

    /**
     * The maximum amount of players per world.
     */
    const val MAX_PLAYERS: Int = (1 shl 11) - 1

    /**
     * The address of the Management server.
     */
    const val HOST_ADDRESS: String = "127.0.0.1"

    /**
     * The operating system of the management server
     */
    val OS: OperatingSystem = if (System.getProperty("os.name").uppercase(Locale.getDefault())
            .contains("WIN")
    ) OperatingSystem.WINDOWS else OperatingSystem.UNIX

    /**
     * Fixes a path to a specified operating system
     *
     * @param operatingSystem The os type.
     * @param path            The path.
     * @return The fixed path.
     */
    fun fixPath(operatingSystem: OperatingSystem?, path: String): String {
        var operatingSystem = operatingSystem
        if (operatingSystem == null) operatingSystem = OS
        return if (operatingSystem == OperatingSystem.WINDOWS) path.replace("/", "\\") else path.replace("\\", "/")
    }
}