package ms.system.mysql

/**
 * Represents an SQL table.
 *
 * @author Emperor
 */
class SQLTable(vararg columns: SQLColumn) {
    /**
     * The columns.
     */
    private val columns: Array<SQLColumn>

    /**
     * Constructs a new `SQLTable` `Object`.
     *
     * @param columns The columns.
     */
    init {
        this.columns = columns as Array<SQLColumn>
    }

    /**
     * Gets the column for the given name.
     *
     * @param name The column name.
     * @return The column.
     */
    fun getColumn(name: String): SQLColumn? {
        for (column in columns) {
            if (column.name == name) {
                return column
            }
        }
        return null
    }

    val changed: List<SQLColumn>
        /**
         * Gets the changed columns.
         *
         * @return The columns.
         */
        get() {
            val updated: MutableList<SQLColumn> = ArrayList(20)
            for (i in columns.indices) {
                val column = columns[i]
                if (column.isChanged) {
                    updated.add(column)
                }
            }
            return updated
        }
}