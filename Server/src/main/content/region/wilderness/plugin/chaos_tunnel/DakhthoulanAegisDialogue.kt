package content.region.wilderness.plugin.chaos_tunnel

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Dakhthoulan Aegis dialogue.
 */
class DakhthoulanAegisDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player(FaceAnim.FRIENDLY, "Hi there.").also { stage++ }
            1 -> npc(FaceAnim.HALF_ASKING, "Hello, you one. What brings you to our humble", "dwelling?").also { stage++ }
            2 -> player(FaceAnim.HALF_THINKING,"I was wondering what this place was?").also { stage++ }
            3 -> npc(FaceAnim.FRIENDLY,"These are the Tunnels of Chaos. They radiate", "with the energy of chaos magic. At the far end of the", "tunnel, you will find a portal to the Chaos Altar itself", "where chaos runes are crafted!").also { stage++ }
            4 -> player(FaceAnim.HAPPY,"Thanks for your time.").also { stage = END_DIALOGUE }
        }
    }
}
