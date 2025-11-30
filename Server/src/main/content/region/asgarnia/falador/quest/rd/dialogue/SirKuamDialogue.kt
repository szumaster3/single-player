package content.region.asgarnia.falador.quest.rd.dialogue

import content.region.asgarnia.falador.quest.rd.RecruitmentDrive
import content.region.asgarnia.falador.quest.rd.plugin.SirLeyeNPC
import core.api.getAttribute
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

class SirKuamDialogue(private val dialogueNum: Int = 0) : DialogueFile() {

    companion object {
        const val spawnSirLeye = "rd:generatedsirleye"
    }

    override fun handle(componentID: Int, buttonID: Int) {
        val player = player ?: return

        when (dialogueNum) {
            1 -> {
                if(getAttribute(player, RecruitmentDrive.stagePass, false)) {
                    npc(FaceAnim.HAPPY, "Excellent work, ${player.username}!", "Please step through the portal to meet your next", "challenge.")
                    stage = END_DIALOGUE
                }
            }
            else -> when (stage) {
                0 -> npc("Ah, ${player.username}, you're finally here.", "Your task for this room is to defeat Sir Leye.", "He has been blessed by Saradomin to be undefeatable", "by any man, so it should be quite the challenge for you.").also { stage++ }
                1 -> npc("If you are having problems, remember", "A true warrior uses his wits as much as his brawn.", "Fight smarter, not harder.").also { stage++ }
                2 -> {
                    end()
                    SirLeyeNPC.init(player)
                    stage = END_DIALOGUE
                }

            }
        }
    }
}