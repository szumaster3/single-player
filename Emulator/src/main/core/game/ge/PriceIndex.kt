package core.game.ge

import core.api.item.itemDefinition
import java.sql.ResultSet

/**
 * The PriceIndex object handles database operations
 * related to item pricing in the Grand Exchange.
 */
object PriceIndex {

    /**
     * Checks if an item can be traded in the Grand Exchange.
     *
     * @param id The item ID to check.
     * @return True if the item can be traded, false otherwise.
     */
    @JvmStatic
    fun canTrade(id: Int): Boolean {
        var canTrade = false

        GEDatabase.run { conn ->
            val stmt = conn.prepareStatement(EXISTS_QUERY)
            stmt.setInt(1, id)
            val res = stmt.executeQuery()
            if (res.next()) {
                canTrade = res.getInt(1) == 1
            }
        }

        return canTrade
    }

    /**
     * Bans an item from being traded in the Grand Exchange.
     *
     * @param id The item ID to ban.
     */
    @JvmStatic
    fun banItem(id: Int) {
        GEDatabase.run { conn ->
            val stmt = conn.prepareStatement(REMOVE_QUERY)
            stmt.setInt(1, id)
            stmt.execute()
        }
    }

    /**
     * Allows an item to be traded in the Grand Exchange if it is not already allowed.
     *
     * @param id The item ID to allow.
     */
    @JvmStatic
    fun allowItem(id: Int) {
        if (canTrade(id)) return

        GEDatabase.run { conn ->
            val stmt = conn.prepareStatement(INSERT_QUERY)
            stmt.setInt(1, id)
            stmt.setInt(2, itemDefinition(id).getAlchemyValue(true))
            stmt.setInt(3, itemDefinition(id).getAlchemyValue(true))
            stmt.setInt(4, 0)
            stmt.setInt(5, 0)
            stmt.execute()
        }
    }

    /**
     * Retrieves the current value of an item from the Grand Exchange price index.
     *
     * @param id The item ID to retrieve the value for.
     * @return The current price of the item.
     */
    @JvmStatic
    fun getValue(id: Int): Int {
        var value = itemDefinition(id).getAlchemyValue(true)

        GEDatabase.run { conn ->
            val stmt = conn.prepareStatement(GET_VALUE_QUERY)
            stmt.setInt(1, id)
            val res = stmt.executeQuery()
            if (res.next()) {
                value = res.getInt(1)
            }
        }

        return value
    }

    /**
     * Adds a trade to the price index for an item.
     *
     * @param id The item ID to add the trade for.
     * @param amount The amount of the item traded.
     * @param pricePerUnit The price per unit of the item.
     */
    @JvmStatic
    fun addTrade(
        id: Int,
        amount: Int,
        pricePerUnit: Int,
    ) {
        val oldInfo = getInfo(id) ?: return
        val newInfo = oldInfo.copy()

        val volumeResetThreshold =
            if (pricePerUnit >= 1000000) {
                500
            } else if (pricePerUnit >= 100000) {
                1000
            } else if (pricePerUnit >= 25000) {
                2500
            } else {
                10000
            }

        // Update total value, unique trades, and current value.
        newInfo.totalValue += (amount * pricePerUnit)
        newInfo.uniqueTrades += amount
        newInfo.lastUpdate = System.currentTimeMillis()
        newInfo.currentValue = (newInfo.totalValue / newInfo.uniqueTrades).toInt()

        if (newInfo.uniqueTrades > volumeResetThreshold) {
            val newAmt = (0.1 * volumeResetThreshold).toInt()
            newInfo.uniqueTrades = newAmt
            newInfo.totalValue = newAmt.toLong() * newInfo.currentValue
        }

        // Update the price info in the database.
        updateInfo(newInfo)
    }

    /**
     * Updates the price information for an item in the database.
     *
     * @param newInfo The updated price information.
     */
    @JvmStatic
    private fun updateInfo(newInfo: PriceInfo) {
        GEDatabase.run { conn ->
            val stmt = conn.prepareStatement(UPDATE_QUERY)
            stmt.setInt(1, newInfo.currentValue)
            stmt.setLong(2, newInfo.totalValue)
            stmt.setInt(3, newInfo.uniqueTrades)
            stmt.setLong(4, newInfo.lastUpdate)
            stmt.setInt(5, newInfo.itemId)
            stmt.execute()
        }
    }

    /**
     * Retrieves the price information for an item from the database.
     *
     * @param id The item ID to retrieve the information for.
     * @return The price information for the item, or null if not found.
     */
    @JvmStatic
    private fun getInfo(id: Int): PriceInfo? {
        var priceInfo: PriceInfo? = null

        GEDatabase.run { conn ->
            val stmt = conn.prepareStatement(SELECT_QUERY)
            stmt.setInt(1, id)
            val res = stmt.executeQuery()
            if (res.next()) {
                priceInfo = PriceInfo.fromQuery(res)
            }
        }

        return priceInfo
    }

    const val SELECT_QUERY = "SELECT * FROM price_index WHERE item_id = ?;"
    const val UPDATE_QUERY =
        "UPDATE price_index SET value = ?, total_value = ?, unique_trades = ?, last_update = ? WHERE item_id = ?;"
    const val EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM price_index WHERE item_id = ?);"
    const val REMOVE_QUERY = "DELETE FROM price_index WHERE item_id = ?;"
    const val INSERT_QUERY =
        "INSERT INTO price_index (item_id, value, total_value, unique_trades, last_update) VALUES (?,?,?,?,?);"
    const val GET_VALUE_QUERY = "SELECT value FROM price_index WHERE item_id = ?;"
}

/**
 * A data class that represents the price information for an item in the Grand Exchange.
 *
 * @param itemId The item ID.
 * @param currentValue The current price of the item.
 * @param totalValue The total value of all trades for the item.
 * @param uniqueTrades The number of unique trades for the item.
 * @param lastUpdate The timestamp of the last price update.
 */
data class PriceInfo(
    var itemId: Int,
    var currentValue: Int,
    var totalValue: Long,
    var uniqueTrades: Int,
    var lastUpdate: Long,
) {
    /**
     * Creates a copy of this PriceInfo object.
     *
     * @return A new PriceInfo object with the same values.
     */
    fun copy(): PriceInfo = PriceInfo(itemId, currentValue, totalValue, uniqueTrades, lastUpdate)

    companion object {
        /**
         * Creates a PriceInfo object from the result of a SQL query.
         *
         * @param result The result set from the query.
         * @return A PriceInfo object.
         */
        fun fromQuery(result: ResultSet): PriceInfo =
            PriceInfo(
                result.getInt(1), // item_id
                result.getInt(2), // value
                result.getLong(3), // total_value
                result.getInt(4), // unique_trades
                result.getLong(5), // last_update
            )
    }
}
