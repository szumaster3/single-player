package content.global.random.event.pinball

import core.api.getVarbit
import core.api.sendPlainDialogue
import core.api.unlock
import core.game.component.Component
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.BLUE

/**
 * Represents the Flippa and Tilt dialogue for Pinball random event.
 */
class PinballGuardDialogue : DialogueFile() {
    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> {
                if (getVarbit(player!!, PinballUtils.VARBIT_PINBALL_SCORE) >= 10) {
                    Component.setUnclosable(
                        player!!,
                        interpreter!!.sendDialogues(player, FaceAnim.HALF_ASKING, "So... I'm free to go now right?")
                    )
                    stage++
                } else {
                    player!!.lock()
                    Component.setUnclosable(
                        player!!,
                        interpreter!!.sendDialogues(
                            npc,
                            FaceAnim.OLD_NORMAL,
                            "You poke 10 flashing pillars, right? You NOT poke other pillars,",
                            "right? Okay, you go play now."
                        )
                    )
                    stage += 3
                }
            }
            1 -> {
                Component.setUnclosable(
                    player!!,
                    interpreter!!.sendDialogues(
                        npc,
                        FaceAnim.OLD_NORMAL,
                        "Yer, get going. We get break now."
                    )
                )
                stage++
            }
            2 -> end().also {
                sendPlainDialogue(player!!, true, "", "Congratulations - you can now leave the arena.")
            }
            3 -> {
                end()
                unlock(player!!)
                PinballUtils.getTag(player!!)
                Component.setUnclosable(
                    player!!,
                    player!!.dialogueInterpreter!!.sendPlainMessage(
                        true,
                        "",
                        "Tag the post with the " + BLUE + "flashing rings</col>."
                    )
                )
            }
        }
    }
}
