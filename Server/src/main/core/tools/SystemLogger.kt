package core.tools

import com.displee.cache.ProgressListener
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import core.ServerConstants
import core.api.log
import core.game.world.GameWorld
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Handles server log printing.
 * @author Ceikry
 */
object SystemLogger {
    private val t = Terminal()
    private val errT = t.forStdErr()
    private val formatter = SimpleDateFormat("HH:mm:ss")

    private fun getTime(): String = "[" + formatter.format(Date()) + "]"

    private val classColors: List<(String) -> String> = listOf(
        { TextColors.cyan(it) },
        { TextColors.magenta(it) },
        { TextColors.green(it) },
        { TextColors.yellow(it) },
        { TextColors.blue(it) },
        { TextColors.white(it) },
        { TextColors.brightRed(it) },
        { TextColors.brightBlue(it) },
        { TextColors.brightMagenta(it) }
    )

    private fun getClassColor(clazz: Class<*>): (String) -> String {
        val index = abs(clazz.name.hashCode()) % classColors.size
        return classColors[index]
    }

    @JvmStatic
    fun processLogEntry(clazz: Class<*>, log: Log, message: String) {
        val classColor = getClassColor(clazz)
        val prefix = classColor("[${clazz.simpleName}]")

        when (log) {
            Log.DEBUG -> {
                if (GameWorld.settings?.isDevMode != true) return
                t.println(TextColors.cyan("${getTime()} [DEBUG] $prefix $message"))
            }

            Log.FINE -> {
                if (ServerConstants.LOG_LEVEL < LogLevel.VERBOSE) return
                t.println(TextColors.gray("${getTime()} [FINE] $prefix $message"))
            }

            Log.INFO -> {
                if (ServerConstants.LOG_LEVEL < LogLevel.DETAILED) return
                t.println(TextColors.white("${getTime()} [INFO] $prefix $message"))
            }

            Log.WARN -> {
                if (ServerConstants.LOG_LEVEL < LogLevel.CAUTIOUS) return
                t.println(TextColors.yellow("${getTime()} [WARN] $prefix $message"))
            }

            Log.ERR -> {
                errT.println(TextColors.red("${getTime()} [ERROR] $prefix $message"))
            }
        }
    }

    @JvmStatic
    fun logGE(message: String) = log(this::class.java, Log.FINE, TextColors.blue("[GE] $message"))

    @JvmStatic
    fun logStartup(message: String) = log(this::class.java, Log.INFO, TextColors.green("[STARTUP] $message"))

    @JvmStatic
    fun logShutdown(message: String) = log(this::class.java, Log.INFO, TextColors.green("[SHUTDOWN] $message"))

    fun logMS(s: String) = log(this::class.java, Log.FINE, TextColors.magenta("[MS] $s"))

    @JvmStatic
    fun logCache(message: String) {
        if (message.isNotBlank()) {
            t.println(TextColors.gray("${getTime()} [Cache] $message"))
        }
    }

    class CreateProgressListener : ProgressListener {
        override fun notify(progress: Double, message: String?) {
            logCache(message ?: "")
        }
    }
}

enum class LogLevel {
    SILENT,
    CAUTIOUS,
    DETAILED,
    VERBOSE,
}

enum class Log {
    FINE,
    INFO,
    WARN,
    ERR,
    DEBUG,
}
