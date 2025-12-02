package content.region.fremennik.miscellania.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import shared.consts.NPCs

class ThorodinDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.THORODIN_3936)
        when (stage) {
            0 -> npc(FaceAnim.OLD_DEFAULT, "Good day, sir.").also { stage++ }
            1 -> options("What are you doing down here?", "Good day.").also { stage++ }
            2 -> when (buttonID) {
                1 -> player(FaceAnim.FRIENDLY, "What are you doing down here?").also { stage++ }
                2 -> player(FaceAnim.NEUTRAL, "Good day.").also { stage = END_DIALOGUE }
            }
            3 -> npc(FaceAnim.OLD_DEFAULT, "We're extending the cave so more people can live in it.", "These Miscellanians aren't so bad.", "They appreciate the benefits of living underground.").also { stage++ }
            4 -> player(FaceAnim.ASKING, "...such as?").also { stage++ }
            5 -> npc(FaceAnim.OLD_DEFAULT, "Not getting rained on, for example.", "Did you do anything about that monster Donal", "was talking about?").also { stage++ }
            6 -> player(FaceAnim.FRIENDLY, "It's been taken care of.").also { stage++ }
            7 -> npc(FaceAnim.OLD_HAPPY, "Glad to hear it.", "Now we can get on with excavating.").also { stage = END_DIALOGUE }
        }
    }
}
