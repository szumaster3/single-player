package core.storage

import core.auth.UserAccountInfo

/**
 * Defines the contract for a user account storage provider.
 * Implementations of this interface handle storing, retrieving, and managing user accounts.
 */
interface AccountStorageProvider {
    /**
     * Checks if a username is already taken.
     *
     * @param username The username to check.
     * @return `true` if the username is already in use, `false` otherwise.
     */
    fun checkUsernameTaken(username: String): Boolean

    /**
     * Retrieves the account information associated with a given username.
     *
     * @param username The username whose account information is to be retrieved.
     * @return The [UserAccountInfo] object for the given username.
     */
    fun getAccountInfo(username: String): UserAccountInfo

    /**
     * Retrieves a list of usernames associated with a specific IP address.
     *
     * @param ip The IP address to search for.
     * @return A list of usernames linked to the given IP address.
     */
    fun getUsernamesWithIP(ip: String): List<String>

    /**
     * Stores the provided user account information.
     *
     * @param info The user account information to store.
     */
    fun store(info: UserAccountInfo)

    /**
     * Updates the stored user account information for an existing username.
     *
     * @param info The updated user account information.
     */
    fun update(info: UserAccountInfo)

    /**
     * Removes the specified user account from storage.
     *
     * @param info The user account to remove.
     */
    fun remove(info: UserAccountInfo)

    /**
     * Retrieves a list of online friends for a given username.
     *
     * @param username The username for which to retrieve online friends.
     * @return A list of online friends.
     */
    fun getOnlineFriends(username: String): List<String>
}
