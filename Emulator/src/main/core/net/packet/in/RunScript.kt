package core.net.packet.`in`

import core.api.*
import core.game.dialogue.InputType
import core.game.node.entity.player.Player

/**
 * Handles script-based input processing for a player.
 * This function validates and processes the input based on the player's attributes
 * and invokes the provided script function with the processed input.
 */
object RunScript {
    /**
     * Processes input provided by the player, validating and converting it as necessary.
     *
     * @param player The player providing input.
     * @param value The raw input value, which can be an [Int] or a [String].
     * @param script A function to execute with the processed input, returning a [Boolean].
     */
    fun processInput(
        player: Player,
        value: Any,
        script: ((Any) -> Boolean),
    ) {
        if (value is Int && value <= 0) return

        val type = player.getAttribute("input-type", InputType.NUMERIC)

        var input = value

        if (player.getAttribute("parseamount", false)) {
            input = value.toString().lowercase()

            if (!input.matches(Regex("^(\\d+)(k+|m+)?$"))) {
                sendDialogue(player, "That doesn't look right. Please try again.")
                return
            }

            input = input.replace("k", "000").replace("m", "000000")
        }

        if (type == InputType.NUMERIC || type == InputType.AMOUNT) {
            input = input.toString().toIntOrNull() ?: input
        }

        try {
            script(input)
        } catch (_: NumberFormatException) {
            sendDialogue(player, "That number's a bit large, don't you think?")
        } catch (_: ClassCastException) {
            sendDialogue(player, "Something went wrong here. Try again.")
        } finally {
            removeAttribute(player, "runscript")
            removeAttribute(player, "input-type")
        }
    }
}
