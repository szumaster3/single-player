package content.region.asgarnia.falador.quest.rd.dialogue

import content.region.asgarnia.falador.quest.rd.RecruitmentDrive
import content.region.asgarnia.falador.quest.rd.cutscene.FailTestCutscene
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import shared.consts.NPCs

class HynnTerprettDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        val player = player ?: return
        val riddle = getAttribute(player, ATTRIBUTE_RANDOM_RIDDLE, -1)
        if (riddle == -1) setAttribute(player, ATTRIBUTE_RANDOM_RIDDLE, (0..4).random())
        when (stage) {
            0 -> {
                npcl(FaceAnim.HAPPY, "Greetings, ${player.username}. I am here to test your wits with a simple riddle.")
                stage = getAttribute(player, ATTRIBUTE_RANDOM_RIDDLE, 0) + 10
            }
            10 -> npcl(FaceAnim.FRIENDLY, "I estimate there to be one million inhabitants in the world of Gielinor; creatures and people both. What number would you get if you multiply the number of fingers on everything's left hand, to the nearest million?").also { stage++ }
            11 -> sendInputDialogue(player, true, "Enter the amount:") { value: Any ->
                if (value == 5000000) setAttribute(player, ATTRIBUTE_CORRECT_ANSWER, true)
                else setAttribute(player, RecruitmentDrive.stageFail, true)
                stage = if (getAttribute(player, ATTRIBUTE_CORRECT_ANSWER, false)) 100 else 200
                openDialogue(player, this, NPC(NPCs.MS_HYNN_TERPRETT_2289))
            }
            12 -> npcl(FaceAnim.FRIENDLY, "Which of the following statements is true?").also { stage++ }
            13 -> showTopics(
                Topic("The number of false statements here is one.", 120),
                Topic("The number of false statements here is two.", 120),
                Topic("The number of false statements here is three.", 100),
                Topic("The number of false statements here is four.", 120)
            )
            14 -> npcl(FaceAnim.FRIENDLY, "I have both a husband and daughter. My husband is four times older than my daughter. In twenty years time, he will be twice as old as my daughter. How old is my daughter now?").also { stage++ }
            15 -> sendInputDialogue(player, true, "Enter the amount:") { value: Any ->
                if (value == 10) setAttribute(player, ATTRIBUTE_CORRECT_ANSWER, true)
                else setAttribute(player, RecruitmentDrive.stageFail, true)
                stage = if (getAttribute(player, ATTRIBUTE_CORRECT_ANSWER, false)) 100 else 200
                openDialogue(player, this, NPC(NPCs.MS_HYNN_TERPRETT_2289))
            }
            16 -> npcl(FaceAnim.FRIENDLY, "Imagine that you have been captured by an enemy. You are to be killed, but in a moment of mercy, the enemy has allowed you to pick your own demise. Your first choice is to be drowned in a lake of acid. Your second choice is to be burned on a fire.").also { stage++ }
            17 -> npcl(FaceAnim.FRIENDLY, "Your third choice is to be thrown to a pack of wolves that have not been fed in over a month. Your final choice of fate is to be thrown from the walls of a castle, many hundreds of feet high. Which fate would you be wise to choose?").also { stage++ }
            18 -> showTopics(
                Topic("The lake of acid.", 120),
                Topic("The large fire.", 120),
                Topic("The wolves.", 100),
                Topic("The castle walls.", 120)
            )
            19 -> npcl(FaceAnim.FRIENDLY, "I dropped four identical stones, into four identical buckets, each containing an identical amount of water. The first bucket's water was at 32 degrees Fahrenheit, the second was at 33 degrees, the third at 34 and the fourth was at 35 degrees.").also { stage++ }
            20 -> showTopics(
                    Topic("Bucket A (32 degrees)", 100),
                    Topic("Bucket B (33 degrees)", 120),
                    Topic("Bucket C (34 degrees)", 120),
                    Topic("Bucket D (35 degrees)", 120)
            )
            100, 120 -> {
                removeAttribute(player, ATTRIBUTE_CORRECT_ANSWER)
                if (!getAttribute(player, RecruitmentDrive.stageFail, false)) {
                    setAttribute(player, RecruitmentDrive.stagePass, true)
                    removeAttribute(player, ATTRIBUTE_RANDOM_RIDDLE)
                }
                npc(FaceAnim.HAPPY, "Excellent work, ${player.username}", "Please step through the portal to meet your next challenge.")
                stage = END_DIALOGUE
            }
            200, 220 -> {
                removeAttribute(player, ATTRIBUTE_RANDOM_RIDDLE)
                setAttribute(player, RecruitmentDrive.stagePass, false)
                setAttribute(player, RecruitmentDrive.stageFail, false)
                npc("No... I am very sorry.", "Apparently you are not up to the challenge.", "I will return you where you came from, better luck in the future.")
                runTask(player, 3) { FailTestCutscene(player).start() }
                stage = END_DIALOGUE
            }
            300 -> {
                npc(FaceAnim.NEUTRAL, "You certainly have the wits to be a Temple Knight.", "Pass on through the portal to find your next challenge.")
                stage = END_DIALOGUE
            }
        }
    }

    companion object {
        const val ATTRIBUTE_RANDOM_RIDDLE = "rd:randomriddle"
        const val ATTRIBUTE_CORRECT_ANSWER = "rd:recentlycorrect"
    }
}
