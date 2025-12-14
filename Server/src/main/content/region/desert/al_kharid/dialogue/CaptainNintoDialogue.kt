package content.region.desert.al_kharid.dialogue

import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE

/**
 * Represents the Captain Ninto dialogue.
 */
class CaptainNintoDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npcl(FaceAnim.OLD_DEFAULT, "Hello, what are you doing here, so far from home?").also { stage++ }
            1 -> playerl(FaceAnim.DRUNK, "I'm enjoyn' the local hoschpitalieee. hee hee.").also { stage++ }
            2 -> npcl(FaceAnim.OLD_DEFAULT, "Looks like you've enjoyed more than your fair share of hospitality.").also { stage++ }
            3 -> playerl(FaceAnim.DRUNK, "tee hee, I probblie schoodn' 'av anuva drinkie right now. But dis dwarven beer ish kind of moreish after the fiff pint.").also { stage++ }
            4 -> npcl(FaceAnim.OLD_DEFAULT, "I'd go easy on the dwarven stout if I were you.").also { stage++ }
            5 -> playerl(FaceAnim.DRUNK, "I yousht to be a tesht pilot yoo know. I reeel hero.").also { stage++ }
            6 -> npcl(FaceAnim.OLD_DEFAULT, "But I loscht my bottle. Scho I now ffind scholiss in the bottle.").also { stage++ }
            7 -> playerl(FaceAnim.DRUNK, "What happened? Did you have a glider crash or get attacked by huge flying birds or something?").also { stage++ }
            8 -> npcl(FaceAnim.OLD_DEFAULT, "Naaah, I realished I woz scared ov heightsh.").also { stage++ }
            9 -> playerl(FaceAnim.DRUNK, "Ah, I can see that would be a problem ... You should keep an eye on your drinking, though.").also { stage++ }
            10 -> npcl(FaceAnim.OLD_DEFAULT, "I'll try. Bofe eyez...").also { stage = END_DIALOGUE }
        }
    }
}
