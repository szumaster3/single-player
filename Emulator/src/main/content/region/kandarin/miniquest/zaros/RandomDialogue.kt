package content.region.kandarin.miniquest.zaros

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import kotlin.random.Random

/**
 * Represents the random dialogue of a mysterious ghost
 * when the player does not have the ghostspeak amulet.
 *
 * Relations:
 * - [Curse of Zaros miniquest][CurseOfZaros]
 */
class RandomDialogue : DialogueFile() {

    private var randomDialogue = -1

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(npc!!.originalId)

        if (randomDialogue == -1) {
            randomDialogue = Random.nextInt(0, 8) * 10
            stage = randomDialogue
        }

        when (stage) {
            0 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            1 -> playerl(FaceAnim.FRIENDLY, "Yeah, I heard about that, ha-ha-ha!").also { stage++ }
            2 -> npcl(FaceAnim.NEUTRAL, "Woo! WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            3 -> playerl(FaceAnim.FRIENDLY, "With a MACKEREL? Ouch!").also { stage++ }
            4 -> npc(FaceAnim.NEUTRAL, "Wooowoowoooooo....", "Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            5 -> playerl(FaceAnim.FRIENDLY, "Well, it was fun. Let's do it again sometime.").also { stage = END_DIALOGUE }

            10 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            11 -> playerl(FaceAnim.FRIENDLY, "Hey, don't think you can talk to me like that!").also { stage++ }
            12 -> npc(FaceAnim.NEUTRAL, "Woo!", "WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            13 -> playerl(FaceAnim.FRIENDLY, "Are you threatening me?").also { stage++ }
            14 -> npc(FaceAnim.NEUTRAL, "Wooowoowoooooo....", "Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            15 -> playerl(FaceAnim.FRIENDLY, "Just because you're already dead, doesn't mean I can't find a way to hurt you ghosty!").also { stage = END_DIALOGUE }

            20 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            21 -> playerl(FaceAnim.FRIENDLY, "Why, thank you very much!").also { stage++ }
            22 -> npc(FaceAnim.NEUTRAL, "Woo!", "WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            23 -> playerl(FaceAnim.FRIENDLY, "I know, but what are you going to do?").also { stage++ }
            24 -> npc(FaceAnim.NEUTRAL, "Wooowoowoooooo....", "Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            25 -> playerl(FaceAnim.FRIENDLY, "Well, I guess that's always true in the long run. See you around, weird invisible dead person!").also { stage = END_DIALOGUE }

            30 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            31 -> playerl(FaceAnim.FRIENDLY, "We get signal!").also { stage++ }
            32 -> npc(FaceAnim.NEUTRAL, "Woo!", "WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            33 -> playerl(FaceAnim.FRIENDLY, "Somebody set up us the bomb!").also { stage++ }
            34 -> npcl(FaceAnim.NEUTRAL, "Wooowoowoooooo.... Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            35 -> playerl(FaceAnim.FRIENDLY, "You have no chance to survive make your time.").also { stage++ }
            36 -> npcl(FaceAnim.NEUTRAL, "Woo?").also { stage++ }
            37 -> playerl(FaceAnim.FRIENDLY, "All your base are belong to us.").also { stage = END_DIALOGUE }

            40 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            41 -> playerl(FaceAnim.FRIENDLY, "No, I've never been there in my life and CERTAINLY didn't steal anything when I was!").also { stage++ }
            42 -> npc(FaceAnim.NEUTRAL, "Woo!", "WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            43 -> playerl(FaceAnim.FRIENDLY, "Are you calling me a liar?!?! I have never stolen a thing... Or cakes. Or fur. Repeatedly.").also { stage++ }
            44 -> npc(FaceAnim.NEUTRAL, "Wooowoowoooooo....", "Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            45 -> playerl(FaceAnim.FRIENDLY, "Well if you are going to take that attitude, then I have nothing further to say.").also { stage = END_DIALOGUE }

            50 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            51 -> playerl(FaceAnim.FRIENDLY, "Yeah, I don't want to brag, but seriously: I am SOOOO rich....").also { stage++ }
            52 -> npc(FaceAnim.NEUTRAL, "Woo!", "WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            53 -> playerl(FaceAnim.FRIENDLY, "Sometimes I just like to open my bank and look at my stuff...").also { stage++ }
            54 -> npc(FaceAnim.NEUTRAL, "Wooowoowoooooo....", "Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            55 -> playerl(FaceAnim.FRIENDLY, "Well, us rich alive people don't want to waste time with you dead ones!").also { stage++ }
            56 -> playerl(FaceAnim.FRIENDLY, "See ya around ghosty!").also { stage = END_DIALOGUE }

            60 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            61 -> playerl(FaceAnim.FRIENDLY, "You don't say?").also { stage++ }
            62 -> npc(FaceAnim.NEUTRAL, "Woo!", "WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            63 -> playerl(FaceAnim.FRIENDLY, "You don't say!").also { stage++ }
            64 -> npc(FaceAnim.NEUTRAL, "Wooowoowoooooo....", "Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            65 -> playerl(FaceAnim.FRIENDLY, "Well, I guess you didn't say.").also { stage = END_DIALOGUE }

            70 -> npc(FaceAnim.NEUTRAL, "Wooo?", "Woooo woo woooooo wooooo woooowooo wooo woooooo woo!").also { stage++ }
            71 -> playerl(FaceAnim.FRIENDLY, "Yeah it's not bad, but I prefer cooked chicken.").also { stage++ }
            72 -> npc(FaceAnim.NEUTRAL, "Woo!", "WOO WOOOOO WOOWOOWOO WOO WOOOOO!").also { stage++ }
            73 -> playerl(FaceAnim.FRIENDLY, "Maybe, but nothing beats a home cooked pie! Man, I love pie!").also { stage++ }
            74 -> npc(FaceAnim.NEUTRAL, "Wooowoowoooooo....", "Woowoowoo? Woooo, wooowoo wooowooowooo!").also { stage++ }
            75 -> playerl(FaceAnim.FRIENDLY, "You don't say? I never knew that. Well, I must be going, see you around.").also { stage = END_DIALOGUE }
        }
    }
}
