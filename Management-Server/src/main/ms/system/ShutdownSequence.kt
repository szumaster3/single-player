package ms.system

import ms.Management

/**
 * The shutdown sequence used for safely turning off the Management server.
 *
 * @author Emperor
 */
class ShutdownSequence : Thread() {
    override fun run() {
        if (Management.active) {
            shutdown()
        }
    }

    companion object {
        /**
         * Safely shuts down the Management server.
         */
        fun shutdown() {
            println("Management server successfully shut down!")
            Management.active = false
        }
    }
}