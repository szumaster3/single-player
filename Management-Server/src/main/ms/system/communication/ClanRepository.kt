package ms.system.communication

import ms.net.packet.WorldPacketRepository.sendClanInformation
import ms.net.packet.WorldPacketRepository.sendLeaveClan
import ms.net.packet.WorldPacketRepository.sendMessage
import ms.net.packet.WorldPacketRepository.sendPlayerMessage
import ms.system.util.StringUtils
import ms.world.GameServer
import ms.world.PlayerSession
import ms.world.WorldDatabase
import ms.world.info.UIDInfo

/**
 * Holds clan related information.
 *
 * @author Emperor
 */
class ClanRepository private constructor() {
    /**
     * The list of players currently in the chat.
     */
    val players: MutableList<PlayerSession> = ArrayList(MAX_MEMBERS)

    /**
     * The banned players.
     */
    private val banned: MutableMap<String, Long> = HashMap()

    /**
     * The owner's details.
     */
    var owner: PlayerSession? = null

    /**
     * Enters the clan chat.
     *
     * @param player The player.
     */
    fun enter(player: PlayerSession) {
        if (players.size >= MAX_MEMBERS && owner!!.username != "2009scape") {
            sendPlayerMessage(player, "The channel you tried to join is full.:clan:")
            return
        }
        if (player != owner && player.rights != 2) {
            if (isBanned(player.username) || owner!!.communication.blocked.contains(player.username)) {
                sendPlayerMessage(player, "You are temporarily banned from this clan channel.:clan:")
                return
            }
            val rank = getRank(player)
            if (rank.ordinal < joinRequirement.ordinal) {
                sendPlayerMessage(player, "You do not have a high enough rank to join this clan channel.:clan:")
                return
            }
        }
        if (!players.contains(player)) {
            players.add(player)
        }
        sendPlayerMessage(player, "Now talking in clan channel " + owner!!.communication.clanName + ".:clan:")
        sendPlayerMessage(player, "To talk, start each line of chat with the / symbol.:clan:")
        player.communication.currentClan = owner!!.username
        player.clan = this
        update()
    }

    /**
     * Leaves the clan chat.
     *
     * @param player The player to leave.
     * @param remove If the player should be removed from the list.
     */
    fun leave(player: PlayerSession, remove: Boolean) {
        if (remove) {
            players.remove(player)
            update()
            if (players.size < 1) {
                banned.clear()
            }
        }
        sendPlayerMessage(player, "You have left the channel.:clan:")
        player.clan = null
        player.communication.currentClan = null
        if (player.isActive) {
            sendLeaveClan(player)
        }
    }

    /**
     * Sends a message to all players in the chat.
     *
     * @param player  The player sending the message.
     * @param message The message to send.
     */
    fun message(player: PlayerSession, message: String?) {
        if (player != owner && player.rights != 2) {
            val rank = getRank(player)
            if (rank.ordinal < messageRequirement.ordinal) {
                sendPlayerMessage(player, "You do not have a high enough rank to talk in this clan channel.:clan:")
                return
            }
        }
        val it: Iterator<PlayerSession> = players.iterator()
        while (it.hasNext()) {
            val p = it.next()
            if (p != null) {
                sendMessage(p, player, 2, message!!)
            }
        }
    }

    /**
     * Updates the clan chat.
     */
    fun update() {
        for (server in WorldDatabase.worlds) {
            if (server != null && server.isActive) {
                sendClanInformation(server, this)
            }
        }
    }

    /**
     * Kicks a player from the clan chat.
     *
     * @param player The player.
     */
    fun kick(player: PlayerSession, target: PlayerSession) {
        val rank = getRank(target)
        if (target.rights == 2) {
            sendPlayerMessage(player, "You can't kick an administrator.:clan:")
            return
        }
        println(rank.toString() + ", " + player.username)
        if (player.rights < 1 /*!= 2 && rank.ordinal() < getKickRequirement().ordinal()*/) {
            sendPlayerMessage(player, "You do not have a high enough rank to kick in this clan channel.:clan:")
            return
        }
        if (target == owner) {
            sendPlayerMessage(player, "You can't kick the owner of this clan channel.:clan:")
            return
        }
        if (target == player) {
            sendPlayerMessage(player, "You can't kick yourself.:clan:")
            return
        }
        for (p in players) {
            sendMessage(
                p,
                player,
                2,
                "[Attempting to kick/ban " + StringUtils.formatDisplayName(target.username) + " from this Clan Chat.]"
            )
        }
        leave(target, true)
        banned[target.username] = System.currentTimeMillis() + (3600000)
        sendPlayerMessage(target, "You have been kicked from the channel.:clan:")
    }

    /**
     * Gets the rank for the given player.
     *
     * @param player The player.
     * @return The rank.
     */
    fun getRank(player: PlayerSession): ClanRank {
        val rank = owner!!.communication.contacts[player.username]
        if (player.rights == 2 && player != owner) {
            return ClanRank.ADMINISTRATOR
        }
        if (rank == null) {
            if (player == owner) {
                return ClanRank.OWNER
            }
            return ClanRank.NONE
        }
        return rank
    }

    /**
     * Checks if the player is banned.
     *
     * @param username The username of the player.
     * @return `True` if so.
     */
    private fun isBanned(username: String): Boolean {
        val time = banned[username] ?: return false
        if (time < System.currentTimeMillis()) {
            banned.remove(username)
            return false
        }
        return true
    }

    /**
     * Clears the clan chat.
     *
     * @param disable If the clan chat is getting disabled.
     */
    fun clean(disable: Boolean) {
        val it = players.iterator()
        while (it.hasNext()) {
            val player = it.next()
            var remove = disable
            if (!remove) {
                remove = getRank(player).ordinal < joinRequirement.value
            }
            if (remove) {
                leave(player, false)
                it.remove()
            }
        }
        if (players.isEmpty()) {
            banned.clear()
        }
        update()
    }

    /**
     * Renames the clan chat.
     *
     * @param name The new clan name.
     */
    fun rename(name: String?) {
        if (name != null) {
            owner!!.communication.clanName = name
        }
        update()
    }

    val name: String
        /**
         * Gets the clan name.
         *
         * @return The clan name.
         */
        get() = owner!!.communication.clanName

    /**
     * Gets the owner value.
     *
     * @return The owner.
     */
    fun getOwner(): PlayerSession? {
        return owner
    }

    /**
     * Sets the owner value.
     *
     * @param owner The owner to set.
     */
    fun setOwner(owner: PlayerSession?) {
        this.owner = owner
    }

    /**
     * Gets the players value.
     *
     * @return The players.
     */
    fun getPlayers(): List<PlayerSession> {
        return players
    }

    val joinRequirement: ClanRank
        /**
         * Gets the joinRequirement value.
         *
         * @return The joinRequirement.
         */
        get() = owner!!.communication.joinRequirement

    val messageRequirement: ClanRank
        /**
         * Gets the messageRequirement value.
         *
         * @return The messageRequirement.
         */
        get() = owner!!.communication.messageRequirement

    val kickRequirement: ClanRank
        /**
         * Gets the kickRequirement value.
         *
         * @return The kickRequirement.
         */
        get() = owner!!.communication.kickRequirement

    val lootRequirement: ClanRank
        /**
         * Gets the lootRequirement value.
         *
         * @return The lootRequirement.
         */
        get() = owner!!.communication.lootRequirement

    /**
     * Gets the banned value.
     *
     * @return The banned.
     */
    fun getBanned(): Map<String, Long> {
        return banned
    }

    companion object {
        /**
         * The maximum amount of members to be in a clan chat.
         */
        const val MAX_MEMBERS: Int = 100

        /**
         * The mapping of active clans.
         */
        private val CLANS: MutableMap<String, ClanRepository> = HashMap()

        /**
         * Gets the clan repository for the given username.
         *
         * @param ownerName The clan owner's name.
         * @return The clan repository.
         */
        fun get(server: GameServer?, ownerName: String): ClanRepository? {
            var clan = CLANS[ownerName]
            if (clan != null) {
                return clan
            }
            var owner = WorldDatabase.getPlayer(ownerName)
            if (owner == null) {
                owner = PlayerSession(ownerName, ownerName, UIDInfo())
                owner.parse()
            }
            if (owner.communication.clanName == "") {
                return null
            }
            clan = ClanRepository()
            clan.owner = owner
            CLANS[ownerName] = clan
            return clan
        }

        val clans: Map<String, ClanRepository>
            /**
             * Gets the clans value.
             *
             * @return The clans.
             */
            get() = CLANS
    }
}