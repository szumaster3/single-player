package ms.system.util

/**
 * Represents a command.
 *
 * @author Emperor
 */
abstract class Command(val name: String, val info: String) {

    /**
     * Runs the command.
     *
     * @param args The arguments.
     */
    abstract fun run(vararg args: String?)
}