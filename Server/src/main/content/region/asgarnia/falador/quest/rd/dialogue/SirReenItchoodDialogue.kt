package content.region.asgarnia.falador.quest.rd.dialogue

import content.region.asgarnia.falador.quest.rd.RecruitmentDrive
import content.region.asgarnia.falador.quest.rd.cutscene.FailTestCutscene
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim

class SirReenItchoodDialogue(private val dialogueNum: Int = 0) : DialogueFile() {

    companion object {
        const val ATTRIBUTE_CLUE = "rd:cluenumber"
    }

    override fun handle(componentID: Int, buttonID: Int) {
        when {
            dialogueNum in 0..1 && !getAttribute(player!!, RecruitmentDrive.stageFail, false) -> handleClueDialogue()
            dialogueNum == 2 || getAttribute(player!!, RecruitmentDrive.stageFail, false) -> handleFailDialogue()
        }
    }

    private fun handleClueDialogue() {
        if (stage == 0) {
            if (getAttribute(player!!, ATTRIBUTE_CLUE, 6) == 6) {
                setAttribute(player!!, ATTRIBUTE_CLUE, (0..5).random())
            }
            stage++
        }

        val clueValue = getAttribute(player!!, ATTRIBUTE_CLUE, 0)

        when (stage) {
            1 -> npc(FaceAnim.FRIENDLY, "Greetings friend, and welcome here,", "you'll find my puzzle not so clear.", "Hidden amongst my words, it's true,", "the password for the door as a clue.").also { stage++ }
            2 -> playerl(FaceAnim.FRIENDLY, "Can I have the clue for the door?").also { stage++ }
            3 -> {
                when (clueValue) {
                    0 -> npc(FaceAnim.FRIENDLY, "Better than me, you'll not find", "In rhyming and in puzzles.", "This clue so clear will tax your mind", "Entirely as it confuzzles!")
                    1 -> npc(FaceAnim.FRIENDLY, "Feel the aching of your mind", "In puzzlement, confused.", "See the clue hidden behind", "His words, as you perused.")
                    2 -> npc(FaceAnim.FRIENDLY, "Look closely at the words i speak;", "And study closely every part.", "See for yourself the word you seek", "Trapped for you if you're smart.")
                    3 -> npc(FaceAnim.FRIENDLY, "More than words, i have not for you", "Except the things i say today.", "Aware are you, this is a clue?", "Take note of what i say!")
                    4 -> npc(FaceAnim.FRIENDLY, "Rare it is that you will see", "A puzzle such as this!", "In many ways it tickles me", "Now, watching you hit and miss!")
                    5 -> npc(FaceAnim.FRIENDLY, "This riddle of mine may confuse,", "I am quite sure of that.", "Mayhap you should closely peruse", "Every word i have spat?")
                }
                stage++
            }
            4 -> playerl(FaceAnim.FRIENDLY, "I don't get that riddle... Can I have a different one?").also { stage++ }

            5 -> {
                when (clueValue) {
                    0 -> npc(FaceAnim.FRIENDLY, "Before you hurry through that door", "Inspect the words i spoke.", "There is a simple hidden flaw", "Ere you think my rhyme a joke.")
                    1 -> npc(FaceAnim.FRIENDLY, "First my clue you did not see,", "I really wish you had.", "Such puzzling wordplay devilry", "Has left you kind of mad!")
                    2 -> npc(FaceAnim.FRIENDLY, "Last time my puzzle did not help", "Apparently, so you've bidden.", "Study my speech carefully, whelp", "To find the answer, hidden.")
                    3 -> npc(FaceAnim.FRIENDLY, "Many types have passed through here", "Even such as you amongst their sort.", "And in the end, the puzzles clear;", "The hidden word you sought.")
                    4 -> npc(FaceAnim.FRIENDLY, "Repetition, once again", "Against good sense it goes.", "In my words, the answers plain", "Now that you see rhyme flows.")
                    5 -> npc(FaceAnim.FRIENDLY, "Twice it is now, i have stated", "In a rhyme, what is the pass.", "Maybe my words obfuscated", "Entirely beyond your class.")
                }
                stage = 0
                end()
            }
        }
    }

    private fun handleFailDialogue() {
        when (stage) {
            0 -> {
                setAttribute(player!!, RecruitmentDrive.stageFail, true)
                npc(FaceAnim.SAD, "It's sad to say,", "this test beat you.", "I'll send you to Tiffy,", "what to do?")
                stage++
            }
            1 -> {
                lock(player!!, 10)
                removeAttribute(player!!, ATTRIBUTE_CLUE)
                setAttribute(player!!, RecruitmentDrive.stagePass, false)
                setAttribute(player!!, RecruitmentDrive.stageFail, false)
                runTask(player!!, 3) {
                    FailTestCutscene(player!!).start()
                }
                end()
            }
        }
    }
}
