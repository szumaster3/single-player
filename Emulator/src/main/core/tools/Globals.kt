package core.tools

/**
 * Predefined color codes for game text.
 */
const val BLACK = "<col=000000>"
const val RED = "<col=ff0000>"
const val ORANGE = "<col=ff6600>"
const val YELLOW = "<col=ffff00>"
const val GREEN = "<col=32CD32>"
const val BLUE = "<col=0000ff>"
const val PURPLE = "<col=cc00ff>"
const val WHITE = "<col=ffffff>"
const val BROWN = "<col=8B4513>"
const val DARK_RED = "<col=8A0808>"
const val DARK_ORANGE = "<col=ab7000>"
const val DARK_YELLOW = "<col=a6a300>"
const val DARK_GREEN = "<col=007d0c>"
const val DARK_BLUE = "<col=08088A>"
const val DARK_PURPLE = "<col=734a75>"

/**
 * Regex pattern for matching arbitrary hex color codes in the format `%RRGGBB`.
 */
private val pattern = Regex("%[0-9a-fA-F]{6}")

/**
 * Test data for color replacement.
 */
private val testData = arrayOf(
    "This is a string with no colors.",
    "This %R is a string with one color.",
    "This %R %G %B is a string with multiple colors.",
    "This %ffffff is an arbitrary hex string.",
)

/**
 * Replaces color placeholders in [line] with actual color codes.
 * Supports both predefined codes (e.g., `%R`, `%G`) and arbitrary hex codes (`%ffffff`).
 *
 * @param line the input string containing color placeholders
 * @return the string with color codes replaced and closed with `</col>`
 */
fun colorize(line: String): String =
    line
        .replace("%BK", BLACK)
        .replace("%R", RED)
        .replace("%O", ORANGE)
        .replace("%Y", YELLOW)
        .replace("%G", GREEN)
        .replace("%B", BLUE)
        .replace("%P", PURPLE)
        .replace("%W", WHITE)
        .replace("%DR", DARK_RED)
        .replace("%DO", DARK_ORANGE)
        .replace("%DY", DARK_YELLOW)
        .replace("%DG", DARK_GREEN)
        .replace("%DB", DARK_BLUE)
        .replace("%DP", DARK_PURPLE)
        .replace(pattern) { matchResult -> "<col=${matchResult.value.substring(1)}>" }
        .append("</col>") + " "

/**
 * Wraps [line] in a color defined by [hexColor].
 *
 * @param line the string to colorize
 * @param hexColor the hex color code (e.g., `"ff0000"`)
 * @return the colorized string
 */
fun colorize(line: String, hexColor: String): String = line.prepend("<col=$hexColor>").append("</col>")

/**
 * Appends [line] to the end of the current string.
 */
fun String.append(line: String): String = this + line

/**
 * Prepends [line] to the beginning of the current string.
 */
fun String.prepend(line: String): String = line + this

/**
 * Returns a shuffled version of the string with characters randomly reordered.
 *
 * @return shuffled string
 */
fun String.shuffle(): String {
    var new = ""
    val old = this.split("").toMutableList()
    for (i in this.indices) {
        val c = old.random()
        new += c.toString()
        old.remove(c)
    }
    return new
}

/**
 * Prepends the correct English article ("a" or "an") to [noun].
 * Handles exceptions for certain words.
 *
 * @param noun the word to prepend the article to
 * @return the word prefixed with the appropriate article
 */
fun prependArticle(noun: String): String {
    val exceptions = hashMapOf("unicorn" to "a", "herb" to "an", "hour" to "an")
    if (exceptions.contains(noun.lowercase())) {
        return "${exceptions[noun.lowercase()]} $noun"
    }
    return when (noun[0].lowercaseChar()) {
        'a', 'e', 'i', 'o', 'u' -> "an $noun"
        else -> "a $noun"
    }
}
