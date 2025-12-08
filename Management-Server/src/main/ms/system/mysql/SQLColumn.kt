package ms.system.mysql

/**
 * Represents a column used in an SQL table.
 *
 * @author Emperor
 */
class SQLColumn
/**
 * Constructs a new `SQLColumn` `Object`.
 *
 * @param name The column name.
 * @param type The data type.
 */ @JvmOverloads constructor(
    /**
     * The name.
     */
    val name: String,
    /**
     * The data type.
     */
    val type: Class<*>,
    /**
     * If this column should never be updated in the database.
     */
    val isNeverUpdate: Boolean = false
) {
    /**
     * Gets the name.
     *
     * @return The name.
     */

    /**
     * Gets the type.
     *
     * @return The type.
     */

    /**
     * Gets the neverUpdate.
     *
     * @return The neverUpdate.
     */

    /**
     * The value.
     */
    private var value: Any? = null

    /**
     * Gets the changed.
     *
     * @return The changed.
     */
    /**
     * Sets the changed.
     *
     * @param changed The changed to set.
     */
    /**
     * If the column value changed.
     */
    var isChanged: Boolean = false

    /**
     * Constructs a new `SQLColumn` `Object`.
     *
     * @param name        The column name.
     * @param type        The data type.
     * @param isNeverUpdate If this column should never be updated in the database.
     */

    /**
     * Updates the value.
     *
     * @param value The value.
     */
    fun updateValue(value: Any) {
        this.isChanged = value !== this.value
        this.value = value
    }

    /**
     * Gets the value.
     *
     * @return The value.
     */
    fun getValue(): Any? {
        return value
    }

    /**
     * Sets the value.
     *
     * @param value The value to set.
     */
    fun setValue(value: Any?) {
        this.value = value
        this.isChanged = false
    }
}