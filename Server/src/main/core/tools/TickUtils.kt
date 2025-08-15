package core.tools

/**
 * The duration of a single game tick in milliseconds.
 */
const val tick = 600

/**
 * One second in milliseconds.
 */
const val second = 1000

/**
 * The duration of a single cycle in milliseconds.
 */
const val cycle = 20

/**
 * Converts [seconds] to the equivalent number of game ticks.
 *
 * @param seconds number of seconds
 * @return number of ticks
 */
fun secondsToTicks(seconds: Int): Int {
    val secondsMs = seconds * second
    return secondsMs / tick
}

/**
 * Converts [ticks] to the equivalent number of seconds.
 *
 * @param ticks number of game ticks
 * @return number of seconds
 */
fun ticksToSeconds(ticks: Int): Int {
    val ticksMs = ticks * tick
    return ticksMs / 1000
}

/**
 * Converts [cycles] to the equivalent number of game ticks, rounding up.
 *
 * @param cycles number of cycles
 * @return number of ticks
 */
fun cyclesToTicks(cycles: Int): Int {
    val cyclesPerTick = tick / cycle
    return kotlin.math.ceil(cycles / cyclesPerTick.toDouble()).toInt()
}

/**
 * Converts [ticks] to the equivalent number of cycles.
 *
 * @param ticks number of ticks
 * @return number of cycles
 */
fun ticksToCycles(ticks: Int): Int = ticks * (tick / cycle)

/**
 * Converts [minutes] to the equivalent number of game ticks.
 *
 * @param minutes number of minutes as Int
 * @return number of ticks
 */
fun minutesToTicks(minutes: Int): Int {
    val minutesMs = minutes * 60 * 1000
    return minutesMs / tick
}

/**
 * Converts [minutes] to the equivalent number of game ticks.
 *
 * @param minutes number of minutes as Double
 * @return number of ticks
 */
fun minutesToTicks(minutes: Double): Int {
    val minutesMs = (minutes * 60 * 1000).toInt()
    return minutesMs / tick
}

/**
 * Converts [ticks] to the equivalent number of minutes.
 *
 * @param ticks number of game ticks
 * @return number of minutes
 */
fun ticksToMinutes(ticks: Int): Int {
    val ticksMs = ticks * tick
    return ticksMs / 1000 / 60
}

/**
 * Number of game ticks per second.
 */
const val ticksPerSecond = second / tick

/**
 * Number of game ticks per minute.
 */
const val ticksPerMinute = 60 * ticksPerSecond
