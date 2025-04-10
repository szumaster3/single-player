package content.region.kandarin.quest.phoenix.handlers

import core.api.addItemOrBank
import core.api.sendChat
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs

class PhoenixEgglingDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when(stage) {
            0 -> {
                sendChat(player!!, "*Gasp!* It's...it's hatching!")
                stage++
            }

            1 -> {}

            2 -> {
                this.npc!!.transform(NPCs.PHOENIX_EGGLING_8550)
                sendChat(npc!!, "Cheeeep!")
                stage++
            }
            3 -> npc(FaceAnim.CHILD_FRIENDLY, "Cheep cheep-chirp chirp?").also { stage++ }
            4 -> player(FaceAnim.EXTREMELY_SHOCKED, "It's trying to climb into my backpack!").also { stage++ }
            5 -> player(FaceAnim.THINKING,"Hmmm. Should I take it with me?").also { stage++ }
            6 -> options("Hop in the bag, you!", "I have enough mouths to feed.").also { stage++ }
            7 -> when(buttonID) {
                1 -> {
                    npc!!.clear()
                    addItemOrBank(player!!, Items.PHOENIX_EGGLING_14626)
                    npcl(FaceAnim.CHILD_FRIENDLY, "Cheeeeeeep! Chir, cheepy cheep chirp?")
                }
                2 -> npc(FaceAnim.CHILD_SAD, "Chiiiirp...").also { stage = END_DIALOGUE }
            }
        }
    }
}