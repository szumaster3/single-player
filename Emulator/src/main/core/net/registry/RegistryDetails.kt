package core.net.registry

import java.sql.Date

/**
 * Holds registration details.
 */
class RegistryDetails
/**
 * Instantiates a new Registry details.
 *
 * @param username the username
 * @param password the password
 * @param birth    the birth
 * @param country  the country
 */(
    /**
     * Gets username.
     *
     * @return the username
     */
    val username: String,
    /**
     * Gets password.
     *
     * @return the password
     */
    val password: String,
    /**
     * Gets birth.
     *
     * @return the birth
     */
    val birth: Date,
    /**
     * Gets country.
     *
     * @return the country
     */
    val country: Int
)
