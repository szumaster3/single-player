package content.region.asgarnia.falador.quest.rd.dialogue

import content.data.GameAttributes
import content.region.asgarnia.falador.quest.rd.RecruitmentDrive
import content.region.asgarnia.falador.quest.rd.cutscene.FailTestCutscene
import core.api.getAttribute
import core.api.removeAttribute
import core.api.runTask
import core.api.setAttribute
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

class SirSpishyusDialogue(private val dialogueNum: Int = 0) : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when {
            getAttribute(player!!, RecruitmentDrive.stagePass, false) -> {
                npc(FaceAnim.HAPPY, "Excellent work, ${player?.username}.", "Please step through the portal to your next challenge.")
            }
            dialogueNum == 2 || getAttribute(player!!, RecruitmentDrive.stageFail, false) -> {
                setAttribute(player!!, RecruitmentDrive.stageFail, true)
                npc(FaceAnim.SAD, "No... I am very sorry.", "You are not up to the challenge.", "Better luck in the future.")
                removeAttribute(player!!, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE)
                setAttribute(player!!, RecruitmentDrive.stagePass, false)
                setAttribute(player!!, RecruitmentDrive.stageFail, false)
                runTask(player!!, 3) { FailTestCutscene(player!!).start() }
            }

            else -> when (stage) {
                0 -> npcl(FaceAnim.FRIENDLY, "Ah, welcome ${player?.username}.").also { stage++ }
                1 -> playerl(FaceAnim.FRIENDLY, "Hello. What am I supposed to do here?").also { stage++ }
                2 -> npcl(FaceAnim.FRIENDLY, "Take the fox, chicken, and grain across the bridge, but be careful!").also { stage++ }
                3 -> npcl(FaceAnim.FRIENDLY, "You can only carry one at a time, and leaving the wrong pair alone will result in failure.").also { stage++ }
                4 -> playerl(FaceAnim.FRIENDLY, "Got it. I'll see what I can do.").also { stage = END_DIALOGUE }
            }
        }
    }
}