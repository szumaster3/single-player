package content.region.desert.quest.deserttreasure

import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.dialogue.DialogueBuilder
import core.game.dialogue.DialogueBuilderFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.plugin.Initializable
import shared.consts.NPCs

@Initializable
class IceTrollDialogue(player: Player? = null) : Dialogue(player) {
    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        openDialogue(player!!, IceTrollDialogueFile(), npc)
        return false
    }

    override fun newInstance(player: Player?): Dialogue {
        return IceTrollDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.ICE_TROLL_1935)
    }

}

class IceTrollDialogueFile : DialogueBuilderFile() {
    override fun create(b: DialogueBuilder) {

        b.onPredicate { _ -> true }
            .npc(FaceAnim.OLD_LAUGH1, "Hur hur hur!", "Well look here, a puny fleshy human!")
            .npc(
                FaceAnim.OLD_LAUGH1,
                "You should beware of the icy wind that runs through",
                "this valley, it will bring a fleshy like you to a cold end",
                "indeed!"
            )
            .end()
    }

}
