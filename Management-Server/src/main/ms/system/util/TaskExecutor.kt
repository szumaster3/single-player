package ms.system.util

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * A class holding methods to execute tasks.
 *
 * @author Emperor
 */
object TaskExecutor {

    /**
     * The executor to use.
     */
    val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    /**
     * The SQL task executor.
     */
    private val SQL_EXECUTOR: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    /**
     * Executes an SQL handling task.
     *
     * @param task The task.
     */
    fun executeSQL(task: Runnable?) {
        SQL_EXECUTOR.execute(task)
    }

    /**
     * Executes the task.
     *
     * @param task The task to execute.
     */
    fun execute(task: Runnable?) {
        executor.execute(task)
    }
}