package core.storage

import core.auth.UserAccountInfo

/**
 * An in-memory implementation of [AccountStorageProvider] that stores user account information
 * using a HashMap. This storage is non-persistent and exists only during runtime.
 */
class InMemoryStorageProvider : AccountStorageProvider {
    private val storage = HashMap<String, UserAccountInfo>()

    /**
     * Checks if a username is already taken.
     *
     * @param username The username to check.
     * @return `true` if the username is already in use, `false` otherwise.
     */
    override fun checkUsernameTaken(username: String): Boolean = storage[username] != null

    /**
     * Retrieves the account information for a given username. If the account does not exist,
     * a default user account is created, assigned a UID based on the username hash, and stored.
     *
     * @param username The username whose account information is to be retrieved.
     * @return The [UserAccountInfo] associated with the username.
     */
    override fun getAccountInfo(username: String): UserAccountInfo =
        storage[username] ?: UserAccountInfo
            .createDefault()
            .also {
                it.uid = username.hashCode()
                storage[username] = it
            }

    /**
     * Stores the provided user account information in memory. The UID is set based on the
     * username's hash before storing.
     *
     * @param info The user account information to store.
     */
    override fun store(info: UserAccountInfo) {
        info.uid = info.username.hashCode()
        storage[info.username] = info
    }

    /**
     * Updates the stored user account information for an existing username.
     *
     * @param info The updated user account information.
     */
    override fun update(info: UserAccountInfo) {
        storage[info.username] = info
    }

    /**
     * Removes the specified user account from storage.
     *
     * @param info The user account to remove.
     */
    override fun remove(info: UserAccountInfo) {
        storage.remove(info.username)
    }

    /**
     * Retrieves a list of online friends for a given username.
     * Since this is an in-memory provider, it currently returns an empty list.
     *
     * @param username The username for which to retrieve online friends.
     * @return A list of online friends, which is always empty in this implementation.
     */
    override fun getOnlineFriends(username: String): List<String> = ArrayList()

    /**
     * Retrieves a list of usernames associated with a given IP address.
     * Since this is an in-memory provider, it currently returns an empty list.
     *
     * @param ip The IP address to search for.
     * @return A list of usernames linked to the given IP, which is always empty in this implementation.
     */
    override fun getUsernamesWithIP(ip: String): List<String> = ArrayList()
}
