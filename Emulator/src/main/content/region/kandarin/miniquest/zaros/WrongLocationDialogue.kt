package content.region.kandarin.miniquest.zaros

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import kotlin.random.Random

/**
 * Represents the mysterious ghost dialogue
 * when we talk to npc in the wrong location.
 *
 * Relations:
 * - [Curse of Zaros miniquest][CurseOfZaros]
 */
class WrongLocationDialogue : DialogueFile() {

    private var randomDialogue = -1

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(npc!!.originalId)

        if (randomDialogue == -1) {
            randomDialogue = Random.nextInt(0, 8) * 10
            stage = randomDialogue
        }

        when (stage) {
            0 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            1 -> npcl(FaceAnim.NEUTRAL, "Mortal... Take heed of my example, and waste not your life, lest you may suffer the same fate as myself...").also { stage++ }
            2 -> playerl(FaceAnim.FRIENDLY, "Huh? You mean someone did this to you?").also { stage++ }
            3 -> npcl(FaceAnim.NEUTRAL, "You have ascertained the truth in my words...").also { stage++ }
            4 -> playerl(FaceAnim.FRIENDLY, "So who did it to you? And why?").also { stage++ }
            5 -> npcl(FaceAnim.NEUTRAL, "Events of moons past, I remember not clearly...").also { stage++ }
            6 -> playerl(FaceAnim.FRIENDLY, "Fine help you are then.").also { stage = END_DIALOGUE }

            10 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            11 -> npcl(FaceAnim.NEUTRAL, "Hello.").also { stage++ }
            12 -> playerl(FaceAnim.FRIENDLY, "So you're a ghost, huh?").also { stage++ }
            13 -> npcl(FaceAnim.NEUTRAL, "Apparently.").also { stage++ }
            14 -> playerl(FaceAnim.FRIENDLY, "In which case I only have one thing to say to you:").also { stage++ }
            15 -> player(FaceAnim.FRIENDLY, "Woooo! Woowooooowoo,", "wooo WOOOOOOOOOOO woowoo woowoowoo woo!").also { stage++ }
            16 -> npcl(FaceAnim.NEUTRAL, "...what?").also { stage++ }
            17 -> playerl(FaceAnim.FRIENDLY, "Guess you don't have the amulet of humanspeak, huh?").also { stage++ }
            18 -> npcl(FaceAnim.NEUTRAL, "...huh?").also { stage++ }
            19 -> player(FaceAnim.FRIENDLY, "Yeah, that's right.", "WOO! WOOOWOOO WOO WOOOOWOO!", "Got no comeback for that, have you?").also { stage = 7 }
            7  -> npcl(FaceAnim.NEUTRAL, "You are very strange...").also { stage = END_DIALOGUE }


            20 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            21 -> npcl(FaceAnim.NEUTRAL, "Please... Leave me to my fate. I have been here so long that I scarce remember aught else...").also { stage++ }
            22 -> playerl(FaceAnim.FRIENDLY, "Uh.... huh.").also { stage = END_DIALOGUE }

            30 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            31 -> npcl(FaceAnim.NEUTRAL, "You can see me?").also { stage++ }
            32 -> playerl(FaceAnim.FRIENDLY, "Uh... yes?").also { stage++ }
            33 -> npcl(FaceAnim.NEUTRAL, "And you understand my words?").also { stage++ }
            34 -> playerl(FaceAnim.FRIENDLY, "Well, most of them...").also { stage++ }
            35 -> npcl(FaceAnim.NEUTRAL, "This is incredible! How can such a thing have come to pass?").also { stage++ }
            36 -> playerl(FaceAnim.FRIENDLY, "What can I say? I'm a professional.").also { stage = END_DIALOGUE }

            40 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            41 -> npcl(FaceAnim.NEUTRAL, "Hello back at you.").also { stage++ }
            42 -> playerl(FaceAnim.FRIENDLY, "So what's a nice ghost like you doing in a place like this?").also { stage++ }
            43 -> npcl(FaceAnim.NEUTRAL, "I suppose you think that's funny?").also { stage++ }
            44 -> playerl(FaceAnim.FRIENDLY, "Well... Mildly amusing I guess.").also { stage++ }
            45 -> npcl(FaceAnim.NEUTRAL, "I don't think I want to talk to you anymore.").also { stage = END_DIALOGUE }

            50 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            51 -> npcl(FaceAnim.NEUTRAL, "Hello stranger.").also { stage++ }
            52 -> player(FaceAnim.FRIENDLY, "Soooo....", "Invisible ghost haunting the same place for thousands of years, huh?").also { stage++ }
            53 -> playerl(FaceAnim.FRIENDLY, "That's got to be a pretty rubbish way to spend your life. Er, death.").also { stage++ }
            54 -> npcl(FaceAnim.NEUTRAL, "You have no idea...").also { stage++ }
            55 -> playerl(FaceAnim.FRIENDLY, "Well, bad luck and all that. See ya around!").also { stage = END_DIALOGUE }

            60 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            61 -> npcl(FaceAnim.NEUTRAL, "Hello stranger. It is rare indeed that I meet one who can see my presence, let alone one who can understand my words.").also { stage++ }
            62 -> player(FaceAnim.FRIENDLY, "Soooo....", "Is it fun being a ghost?").also { stage++ }
            63 -> npcl(FaceAnim.NEUTRAL, "Does it look like fun to you?").also { stage++ }
            64 -> playerl(FaceAnim.FRIENDLY, "Erm... Well, yes actually.").also { stage++ }
            65 -> npcl(FaceAnim.NEUTRAL, "Then you are a fool, and I will waste no more words upon you.").also { stage++ }
            66 -> playerl(FaceAnim.FRIENDLY, "What, not even 'goodbye'?").also { stage++ }
            67 -> npcl(FaceAnim.NEUTRAL, "").also { stage++ }
            68 -> playerl(FaceAnim.FRIENDLY, "Sheesh, what a grouch. You'd think you'd have more of a sense of humour being dead and all.").also { stage = END_DIALOGUE }

            70 -> playerl(FaceAnim.FRIENDLY, "Hello there.").also { stage++ }
            71 -> npcl(FaceAnim.NEUTRAL, "The endless tragedy of fate... Why must you torment me so?").also { stage++ }
            72 -> playerl(FaceAnim.FRIENDLY, "Alright, alright, calm down, calm down. All I said was 'hello'!").also { stage = END_DIALOGUE }
        }
    }
}
