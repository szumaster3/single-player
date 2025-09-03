package content.global.dialogue

import core.ServerConstants
import core.api.openInterface
import core.api.sendDialogue
import core.api.sendItemDialogue
import core.api.sendString
import core.game.dialogue.DialogueFile
import core.game.node.item.Item
import core.tools.START_DIALOGUE
import shared.consts.Components
import shared.consts.Items

/**
 * Represents the dialogue shown when the user presses the "?" button on the Bank Interface.
 *
 * @author vddCore
 */
class BankHelpDialogue : DialogueFile() {

    companion object {
        private val COIN_TIERS = mapOf(
            2 to Items.COINS_8890,
            3 to Items.COINS_8891,
            4 to Items.COINS_8892,
            5 to Items.COINS_8893,
            25 to Items.COINS_8894,
            100 to Items.COINS_8895,
            250 to Items.COINS_8896,
            1000 to Items.COINS_8897,
            10000 to Items.COINS_8898,
            Int.MAX_VALUE to Items.COINS_8899,
        )
    }

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            START_DIALOGUE -> {
                options("Check Bank Value", "Banking Assistance", "Close")
                stage++
            }

            1 -> when (buttonID) {
                1 -> showBankValue()
                2 -> showBankHelp()
                3 -> end()
            }
        }
    }

    private fun showBankValue() {
        val p = player ?: return
        end()

        val wealth = p.bank.wealth
        if (wealth <= 0) {
            sendDialogue(p, "You have no valuables in your bank.")
            return
        }

        val word = if (wealth == 1) "coin" else "coins"
        val coinItemId = COIN_TIERS.entries.first { wealth < it.key }.value

        sendItemDialogue(
            p,
            Item(coinItemId, wealth),
            "Your bank is worth <col=a52929>$wealth</col> $word."
        )
    }

    private fun showBankHelp() {
        val p = player ?: return
        end()
        p.bank.close()

        openInterface(p, Components.BANK_V2_HELP_767)

        if (p.interfaceManager.isOpened()) {
            sendString(
                p,
                "Bank of ${ServerConstants.SERVER_NAME} - Help",
                Components.BANK_V2_HELP_767,
                40
            )
        }
    }
}