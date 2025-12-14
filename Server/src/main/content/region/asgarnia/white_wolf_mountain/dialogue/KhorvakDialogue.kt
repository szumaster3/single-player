package content.region.asgarnia.white_wolf_mountain.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Khorvak, a dwarven engineer dialogue.
 */
class KhorvakDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            1 -> npcl(FaceAnim.OLD_DRUNK_LEFT, "Ooooh, heeellooo! Anyshing a bright dwarf like me can help you with?").also { stage++ }
            2 -> playerl(FaceAnim.HALF_THINKING, "The only thing you can help me with is finding the nearest place to get a drink, I think...").also { stage++ }
            3 -> npcl(FaceAnim.OLD_DRUNK_LEFT, "That ish shimple! Help yourshelf to the dwarvern shtout! I mean dwarfen! I mean... I'm not shure what I mean.").also { stage = END_DIALOGUE }
        }
    }
}
