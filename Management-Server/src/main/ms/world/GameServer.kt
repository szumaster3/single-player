package ms.world

import ms.net.IoSession
import ms.net.packet.WorldPacketRepository.sendRegistryResponse
import ms.system.mysql.SQLEntryHandler
import ms.system.mysql.WorldListSQLHandler
import ms.system.util.TaskExecutor
import ms.world.info.Response
import ms.world.info.WorldInfo
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Represents a game world.
 *
 * @author Emperor
 */
class GameServer(val info: WorldInfo) {
    /**
     * The players active on the game server.
     */
    val players: MutableMap<String, PlayerSession> = HashMap()

    /**
     * The I/O session.
     */
    var session: IoSession? = null

    /**
     * The scheduled future for the updating task.
     */
    private var future: ScheduledFuture<*>? = null

    /**
     * Configures the game server.
     *
     * @param session The I/O session.
     */
    fun configure(session: IoSession) {
        this.session = session
        session.gameServer = this
        future = TaskExecutor.executor.scheduleAtFixedRate({
            SQLEntryHandler.write(WorldListSQLHandler(this@GameServer))
            if (!isActive) {
                future!!.cancel(false)
                future = null
            }
        }, 0, 10, TimeUnit.SECONDS)
    }

    /**
     * Registers a player.
     *
     * @param player The player.
     */
    fun register(player: PlayerSession) {
        players[player.username] = player
        player.world = this
        player.isActive = true
        player.worldId = info.worldId
        player.configure()
        sendRegistryResponse(this, player, Response.SUCCESSFUL)
        player.communication.sync()
    }

    val isActive: Boolean
        /**
         * Checks if the game server is active.
         *
         * @return `True` if so.
         */
        get() = session!!.isActive

    val playerAmount: Int
        /**
         * @return the playerAmount
         */
        get() = players.size
}