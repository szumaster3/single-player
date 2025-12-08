package ms

import ms.net.NioReactor
import ms.net.packet.IoBuffer
import ms.net.packet.PacketHeader
import ms.net.packet.WorldPacketRepository.sendConfigReload
import ms.net.packet.WorldPacketRepository.sendUpdate
import ms.store.ServerStore
import ms.system.ShutdownSequence
import ms.system.mysql.SQLManager.init
import ms.system.util.Command
import ms.world.PlayerSession
import ms.world.WorldDatabase
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

/**
 * The main class.
 *
 * @author Emperor
 */
object Management {
    /**
     * If the shutdown hook is active.
     */
    var active: Boolean = true

    /**
     * The main method.
     *
     * @param args The arguments cast on runtime.
     * @throws Throwable When an exception occurs.
     */
    @Throws(Throwable::class)
    @JvmStatic
    fun main(args: Array<String>) {
        if (!isLocallyHosted(ServerConstants.HOST_ADDRESS)) {
            System.err.println("WARNING: Configure host address in server constants!")
        }
        println("Management server launched.")
        ServerStore.init(ServerConstants.STORE_PATH)
        init()
        NioReactor.configure(ServerConstants.PORT).start()
        NioReactor.configure(43595).start()
        Runtime.getRuntime().addShutdownHook(ShutdownSequence())
        println("Status: ready.")
        println("Use -commands for a list of commands!")
        val s = Scanner(System.`in`)
        while (s.hasNext()) {
            try {
                var command = s.nextLine()
                if (!command.startsWith("-")) {
                    continue
                }
                val arguments = command.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                command = arguments[0]
                for (c in COMMANDS) {
                    if (c.name == command) {
                        println("Handling command \"$command\"!")
                        c.run(*arguments)
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        s.close()
    }

    /**
     * The commands.
     */
    private val COMMANDS: List<Command> by lazy {
        listOf(
            object : Command("-commands", "Print a list of all commands.") {
                override fun run(vararg args: String?) {
                    for (c in Management.COMMANDS) {
                        println("Command ${c.name}: ${c.info}")
                    }
                }
            },

            object : Command("-s", "Safely shuts down the server.") {
                override fun run(vararg args: String?) {
                    println("Shutting down Management server...")
                    ShutdownSequence.shutdown()
                }
            },

            object : Command("-debug", "Debug world info.") {
                override fun run(vararg args: String?) {
                    for (server in WorldDatabase.worlds) {
                        if (server != null) {
                            println(
                                "World [id=${server.info.worldId}, IP=${server.info.address}, country=${server.info.country}, " +
                                        "members=${server.info.isMembers}, players=${server.players.size}, active=${server.isActive}]."
                            )
                        }
                    }
                }
            },

            object : Command("-pinfo", "Debugs player information (usage: -pinfo emperor).") {
                override fun run(vararg args: String?) {
                    val name = args.getOrNull(1)
                    if (name == null) {
                        println("Usage: -pinfo <name>")
                        return
                    }
                    val player = WorldDatabase.getPlayer(name)
                    if (player == null) {
                        println("Player $name was not registered!")
                        return
                    }
                    println("Player [name=$name, world=${player.worldId}, active=${player.isActive}].")
                }
            },

            object : Command("-update", "Calls an update on all the game servers (-update -1 to cancel).") {
                override fun run(vararg args: String?) {
                    val ticks = args.getOrNull(1)?.toIntOrNull() ?: return
                    for (server in WorldDatabase.worlds) {
                        if (server != null && server.isActive) {
                            sendUpdate(server, ticks)
                        }
                    }
                }
            },

            object : Command("-reloadconfig", "Reloads the configurations of all worlds.") {
                override fun run(vararg args: String?) {
                    for (server in WorldDatabase.worlds) {
                        if (server == null) continue
                        sendConfigReload(server)
                    }
                }
            },

            object : Command("-rlcache", "Reloads launcher/client resource cache") {
                override fun run(vararg args: String?) {
                    println("Reloaded resource cache!")
                }
            },

            object : Command("-kick", "Kicks a player from the MS (not ingame).") {
                override fun run(vararg args: String?) {
                    val name = args.getOrNull(1)
                    if (name == null) {
                        println("Usage: -kick <name>")
                        return
                    }
                    val player = WorldDatabase.getPlayer(name)
                    if (player == null) {
                        println("Player $name was not registered!")
                        return
                    }
                    player.world?.players?.remove(name)
                    player.worldId = 0
                    println("Kicked player $name!")
                }
            },

            object : Command("-say", "Send a message to all worlds") {
                override fun run(vararg args: String?) {
                    if (args.size <= 1) {
                        println("Usage: -say <message>")
                        return
                    }

                    var message = args.joinToString(" ")
                    message = message.substring(4) // remove "-say"

                    for (server in WorldDatabase.worlds) {
                        if (server == null) continue
                        val finalMessage = message
                        server.players.forEach { (_, p: PlayerSession) ->
                            val buffer = IoBuffer(5, PacketHeader.BYTE)
                            buffer.putString(p.username)
                            buffer.putString("Server")
                            buffer.put(2)
                            buffer.put(2)
                            buffer.putString(finalMessage)
                            p.world?.session?.write(buffer)
                        }
                    }
                }
            }
        )
    }

    /**
     * Checks if the Management server is locally hosted.
     *
     * @return `True` if so.
     * @throws IOException When an I/O exception occurs.
     */
    @Throws(IOException::class)
    private fun isLocallyHosted(ip: String): Boolean {
        val address = InetAddress.getByName(ip)
        if (address.isAnyLocalAddress || address.isLoopbackAddress) {
            return true
        }
        return NetworkInterface.getByInetAddress(address) != null
    }
}