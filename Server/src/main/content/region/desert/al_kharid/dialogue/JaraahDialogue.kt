package content.region.desert.al_kharid.dialogue

import core.api.getStatLevel
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.skill.Skills
import core.tools.END_DIALOGUE

/**
 * Represents the Jaraah dialogue.
 */
class JaraahDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> player(FaceAnim.FRIENDLY, "Hi!").also { stage++ }
            1 -> npcl(FaceAnim.ANNOYED, "What? Can't you see I'm busy?!").also { stage++ }
            2 -> showTopics(
                Topic("Can you heal me?", 101),
                Topic("You must see some gruesome things?", 201),
                Topic("Why do they call you 'The Butcher'?", 301),
            )
            101 -> {
                end()
                if (player!!.skills.lifepoints < getStatLevel(player!!, Skills.HITPOINTS)) {
                    player!!.skills.heal(21)
                    npcl(FaceAnim.FRIENDLY, "There you go!")
                    stage = END_DIALOGUE
                } else {
                    npcl(FaceAnim.FRIENDLY, "Okay, this will hurt you more than it will me.")
                    stage = END_DIALOGUE
                }
            }
            201 -> npcl(FaceAnim.FRIENDLY, "It's a gruesome business and with the tools they give me it gets mroe gruesome before it gets better!").also { stage = END_DIALOGUE }
            301 -> npcl(FaceAnim.HALF_THINKING, "'The Butcher'?").also { stage++ }
            302 -> npcl(FaceAnim.LAUGH, "Ha!").also { stage++ }
            303 -> npcl(FaceAnim.HALF_ASKING, "Would you like me to demonstrate?").also { stage++ }
            304 -> player(FaceAnim.AFRAID, "Er...I'll give it a miss, thanks.").also { stage = END_DIALOGUE }
        }
    }
}
