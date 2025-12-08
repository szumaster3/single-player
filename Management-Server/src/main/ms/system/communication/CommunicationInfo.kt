package ms.system.communication

import ms.net.packet.WorldPacketRepository.notifyLogout
import ms.net.packet.WorldPacketRepository.notifyPlayers
import ms.net.packet.WorldPacketRepository.sendContactUpdate
import ms.net.packet.WorldPacketRepository.sendMessage
import ms.net.packet.WorldPacketRepository.sendPlayerMessage
import ms.system.util.ByteBufferUtils
import ms.system.util.StringUtils
import ms.world.PlayerSession
import ms.world.WorldDatabase
import java.nio.ByteBuffer
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * Holds communication information.
 *
 * @author Emperor
 */
class CommunicationInfo(player: PlayerSession?) {
    /**
     * The clan ranks.
     */
    val contacts: MutableMap<String, ClanRank> = HashMap()

    /**
     * The list of blocked players.
     */
    val blocked: MutableList<String> = ArrayList(20)

    /**
     * The player's clan name.
     */
    var clanName: String = ""

    /**
     * The current clan this player is in.
     */
    var currentClan: String? = "SinglePlayer"

    /**
     * The rank required for joining.
     */
    var joinRequirement: ClanRank = ClanRank.FRIEND

    /**
     * The rank required for messaging.
     */
    var messageRequirement: ClanRank = ClanRank.NONE

    /**
     * The rank required for kicking members.
     */
    var kickRequirement: ClanRank = ClanRank.OWNER

    /**
     * The rank required for loot-share.
     */
    var lootRequirement: ClanRank = ClanRank.ADMINISTRATOR

    /**
     * The public chat setting.
     */
    var publicChatSetting: Int = 0

    /**
     * The private chat setting.
     */
    var privateChatSetting: Int = 0

    /**
     * The trade setting.
     */
    var tradeSetting: Int = 0

    /**
     * The player session.
     */
    var player: PlayerSession? = null

    /**
     * Constructs a new `CommunicationInfo` `Object`.
     *
     * @param player The player.
     */
    init {
        this.player = player
    }

    /**
     * Called when the player logs in.
     */
    fun sync() {
        if (privateChatSetting != 2) {
            for (server in WorldDatabase.worlds) {
                if (server != null && server.isActive) {
                    val names: MutableList<String> = ArrayList(20)
                    for (p in server.players.values) {
                        if (p.isActive && p.communication.contacts.containsKey(player!!.username)) {
                            if (privateChatSetting == 0 || contacts.containsKey(p.username)) {
                                names.add(p.username)
                            }
                        }
                    }
                    notifyPlayers(server, player!!, names)
                }
            }
        }
    }

    /**
     * Called when the player logs out.
     */
    fun clear() {
        for (server in WorldDatabase.worlds) {
            if (server != null && server.isActive) {
                notifyLogout(server, player!!)
            }
        }
        if (player!!.clan != null) {
            player!!.clan?.leave(player!!, true)
        }
    }

    /**
     * Saves the communication info.
     *
     * @param statement The buffer.
     * @throws SQLException The exception if thrown.
     */
    @Throws(SQLException::class)
    fun save(statement: PreparedStatement) {
        var contacts = ""
        var blocked = ""
        val blockedBuilder = StringBuilder()
        for (i in this.blocked.indices) {
            val blockline = (if (i == 0) "" else ",") + this.blocked[i]
            blockedBuilder.append(blockline)
        }
        blocked = blockedBuilder.toString()
        var count = 0
        val contactBuilder = StringBuilder()
        for ((key, value) in this.contacts) {
            val contactLine = "{" + key + "," + value.ordinal + "}" + (if (count == this.contacts.size - 1) "" else "~")
            contactBuilder.append(contactLine)
            count++
        }
        contacts = contactBuilder.toString()
        statement.setString(3, contacts)
        statement.setString(4, blocked)
        statement.setString(5, clanName)
        statement.setString(6, currentClan)
        statement.setString(
            7,
            joinRequirement.ordinal.toString() + "," + messageRequirement.ordinal + "," + kickRequirement.ordinal + "," + lootRequirement.ordinal
        )
        statement.setString(8, "$publicChatSetting,$privateChatSetting,$tradeSetting")
    }

    /**
     * Parses the communication info from the database.
     *
     * @param set The result set.
     * @throws SQLException The exception if thrown.
     */
    @Throws(SQLException::class)
    fun parse(set: ResultSet) {
        val contacts = set.getString("contacts")
        var tokens: Array<String>
        if (contacts != null && !contacts.isEmpty()) {
            val datas = contacts.split("~".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (d in datas) {
                tokens = d.replace("{", "").replace("}", "").split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                if (tokens.size < 2) {
                    continue
                }
                this.contacts[tokens[0]] = ClanRank.values()[tokens[1].toInt()]
            }
        }
        val bl = set.getString("blocked")
        if (bl != null && !bl.isEmpty()) {
            tokens = bl.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            blocked.addAll(Arrays.asList(*tokens))
        }
        clanName = set.getString("clanName")
        currentClan = set.getString("currentClan")
        val clanReqs = set.getString("clanReqs")
        if (!clanReqs.isEmpty()) {
            tokens = clanReqs.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var rank: ClanRank? = null
            var ordinal = 0
            for (i in tokens.indices) {
                ordinal = tokens[i].toInt()
                if (ordinal < 0 || ordinal > ClanRank.values().size - 1) {
                    continue
                }
                rank = ClanRank.values()[ordinal]
                when (i) {
                    0 -> joinRequirement = rank
                    1 -> messageRequirement = rank
                    2 -> {
                        if (ordinal < 3 || ordinal > 8) {
                            break
                        }
                        kickRequirement = rank
                    }

                    3 -> lootRequirement = rank
                    else -> {}
                }
            }
        }
        val chatSettings = set.getString("chatSettings")
        if (!chatSettings.isEmpty()) {
            tokens = chatSettings.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in tokens.indices) {
                when (i) {
                    0 -> publicChatSetting = tokens[0].toInt()
                    1 -> privateChatSetting = tokens[1].toInt()
                    2 -> tradeSetting = tokens[2].toInt()
                    else -> println("Illegal arg count in chatsetting string: $chatSettings")
                }
            }
        }
    }

    /**
     * Saves the communication info.
     *
     * @param buffer The buffer.
     */
    fun save(buffer: ByteBuffer) {
        buffer.put(contacts.size.toByte())
        for ((key, value) in contacts) {
            ByteBufferUtils.putString(key, buffer)
            buffer.put(value.ordinal.toByte())
        }
        buffer.put(blocked.size.toByte())
        for (name in blocked) {
            ByteBufferUtils.putString(name, buffer)
        }
        ByteBufferUtils.putString(clanName, buffer)
        if (currentClan != null) {
            ByteBufferUtils.putString(currentClan, buffer.put(1.toByte()))
        } else {
            buffer.put(0.toByte())
        }
        buffer.put(joinRequirement.ordinal.toByte())
        buffer.put(messageRequirement.ordinal.toByte())
        buffer.put(kickRequirement.ordinal.toByte())
        buffer.put(lootRequirement.ordinal.toByte())
        buffer.put(publicChatSetting.toByte())
        buffer.put(privateChatSetting.toByte())
        buffer.put(tradeSetting.toByte())
    }

    /**
     * Parses the communication info from the buffer.
     *
     * @param buffer The buffer.
     */
    fun parse(buffer: ByteBuffer) {
        var length = buffer.get().toInt() and 0xFF
        for (i in 0 until length) {
            contacts[ByteBufferUtils.getString(buffer)] = ClanRank.values()[buffer.get().toInt() and 0xFF]
        }
        length = buffer.get().toInt() and 0xFF
        for (i in 0 until length) {
            blocked.add(ByteBufferUtils.getString(buffer))
        }
        clanName = ByteBufferUtils.getString(buffer)
        if (buffer.get().toInt() == 1) {
            currentClan = ByteBufferUtils.getString(buffer)
        }
        joinRequirement = ClanRank.values()[buffer.get().toInt()]
        messageRequirement = ClanRank.values()[buffer.get().toInt()]
        kickRequirement = ClanRank.values()[buffer.get().toInt()]
        lootRequirement = ClanRank.values()[buffer.get().toInt()]
        publicChatSetting = buffer.get().toInt()
        privateChatSetting = buffer.get().toInt()
        tradeSetting = buffer.get().toInt()
    }

    /**
     * Sends a message to the target.
     *
     * @param target  The target.
     * @param message The message to send.
     */
    fun sendMessage(target: String?, message: String?) {
        val receiver = WorldDatabase.getPlayer(target)
        if (receiver == null || !receiver.isActive) {
            sendPlayerMessage(player, "That player is currently offline.")
            return
        }
        sendMessage(player!!, receiver, 0, message!!)
        sendMessage(receiver, player!!, 1, message)
    }

    /**
     * Adds a contact.
     *
     * @param contact The contact to add.
     */
    fun add(contact: String) {
        if (contacts.size >= MAX_LIST_SIZE) {
            sendPlayerMessage(player, "Your friend list is full.")
            return
        }
        if (blocked.contains(contact)) {
            sendPlayerMessage(
                player, "Please remove " + StringUtils.formatDisplayName(contact) + " from your ignored list first."
            )
            return
        }
        if (contacts.containsKey(contact)) {
            sendPlayerMessage(player, StringUtils.formatDisplayName(contact) + " is already on your friend list.")
            return
        }
        contacts[contact] = ClanRank.FRIEND
        sendContactUpdate(player!!, contact, false, false, getWorldId(player, contact), null)
        val clan: ClanRepository = ClanRepository.clans[player!!.username]!!
        if (clan != null) {
            clan.update()
        }
        if (privateChatSetting == 1) {
            val other = WorldDatabase.getPlayer(contact)
            if (other != null && other.isActive && other.communication.contacts.containsKey(player!!.username)) {
                sendContactUpdate(other, player!!.username, false, false, getWorldId(other, player!!.username), null)
            }
        }
    }

    /**
     * Removes a contact.
     *
     * @param contact The contact to remove.
     * @param block   If the contact should be removed from the block list.
     */
    fun remove(contact: String, block: Boolean) {
        val other = WorldDatabase.getPlayer(contact)
        if (block) {
            blocked.remove(contact)
            if (other != null && other.isActive && other.communication.contacts.containsKey(player!!.username)) {
                sendContactUpdate(other, player!!.username, false, false, getWorldId(other, player!!.username), null)
            }
        } else {
            contacts.remove(contact)
            val clan: ClanRepository = ClanRepository.clans[player!!.username]!!
            if (clan != null) {
                clan.update()
            }
            if (privateChatSetting == 1 && other != null && other.isActive && other.communication.contacts.containsKey(
                    player!!.username
                )
            ) {
                sendContactUpdate(other, player!!.username, false, false, getWorldId(other, player!!.username), null)
            }
        }
        sendContactUpdate(player!!, contact, block, true, 0, null)
    }


    /**
     * Adds a blocked contact.
     *
     * @param contact The contact to block.
     */
    fun block(contact: String) {
        if (blocked.size >= MAX_LIST_SIZE) {
            sendPlayerMessage(player, "Your ignore list is full.")
            return
        }
        if (contacts.containsKey(contact)) {
            sendPlayerMessage(
                player, "Please remove " + StringUtils.formatDisplayName(contact) + " from your friends list first."
            )
            return
        }
        if (blocked.contains(contact)) {
            sendPlayerMessage(player, StringUtils.formatDisplayName(contact) + " is already on your friend list.")
            return
        }
        blocked.add(contact)
        sendContactUpdate(player!!, contact, true, false, 0, null)
        val other = WorldDatabase.getPlayer(contact)
        if (other != null && other.isActive && other.communication.contacts.containsKey(player!!.username)) {
            sendContactUpdate(other, player!!.username, false, false, 0, null)
        }
    }

    /**
     * Updates the clan rank of a certain contact.
     *
     * @param contact  The contact.
     * @param clanRank The clan rank to set.
     */
    fun updateClanRank(contact: String, clanRank: ClanRank) {
        if (!contacts.containsKey(contact)) {
            System.err.println("Could not find contact $contact to update clan rank!")
            return
        }
        contacts[contact] = clanRank
        val clan: ClanRepository = ClanRepository.clans[player!!.username]!!
        if (clan != null) {
            clan.update()
        }

        sendContactUpdate(player!!, contact, false, false, 0, clanRank)
    }

    /**
     * Updates the settings.
     *
     * @param publicSetting  The public chat setting.
     * @param privateSetting The private chat setting.
     * @param tradeSetting   The trade setting.
     */
    fun updateSettings(publicSetting: Int, privateSetting: Int, tradeSetting: Int) {
        this.publicChatSetting = publicSetting
        this.tradeSetting = tradeSetting
        if (this.privateChatSetting != privateSetting) {
            updatePrivateSetting(privateSetting)
        }
    }

    /**
     * Updates the private chat setting.
     *
     * @param privateSetting The private chat setting.
     */
    private fun updatePrivateSetting(privateSetting: Int) {
        this.privateChatSetting = privateSetting
        for (server in WorldDatabase.worlds) {
            if (server != null && server.isActive) {
                if (privateSetting == 2) {
                    notifyLogout(server, player!!)
                    continue
                }
                for (p in server.players.values) {
                    if (p.isActive && p.communication.contacts.containsKey(player!!.username)) {
                        sendContactUpdate(p, player!!.username, false, false, getWorldId(p, player!!.username), null)
                    }
                }
            }
        }
    }

    /**
     * Gets the contacts value.
     *
     * @return The contacts.
     */
    fun getContacts(): Map<String, ClanRank> {
        return contacts
    }

    /**
     * Gets the clan rank for the given contact.
     *
     * @param contact The contact.
     * @return The rank.
     */
    fun getRank(contact: String): ClanRank? {
        return contacts[contact]
    }

    /**
     * Gets the blocked value.
     *
     * @return The blocked.
     */
    fun getBlocked(): List<String> {
        return blocked
    }

    companion object {
        /**
         * The maximum list size.
         */
        const val MAX_LIST_SIZE: Int = 200

        /**
         * Gets the world id for the given contact.
         *
         * @param player  The player.
         * @param contact The contact.
         * @return The world id to display.
         */
        fun getWorldId(player: PlayerSession?, contact: String?): Int {
            val p = WorldDatabase.getPlayer(contact)
            if (p == null || !p.isActive || p.communication.privateChatSetting == 2) {
                return 0
            }
            if (p.communication.blocked.contains(player!!.username)) {
                return 0
            }
            if (p.communication.privateChatSetting == 1) {
                if (p.communication.contacts.containsKey(player.username)) {
                    return p.worldId
                }
                return 0
            }
            return p.worldId
        }
    }
}