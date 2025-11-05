package content.region.kandarin.yanille.quest.handsand

import core.api.*
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Quests
import shared.consts.Vars

/**
 * Handles the Sandy dialogue in The Hand in the Sand quest.
 */
@Initializable
class SandyDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        if (getQuestStage(player, Quests.THE_HAND_IN_THE_SAND) == 4) {
            player(FaceAnim.HALF_ASKING, "Hello Sir, do you run the Sand Corp?")
        } else if (inInventory(player, Items.SANDYS_ROTA_6948)) {
            sendDialogue(player, "You already have the rota, perhaps you should take both rotas back to Bert in Yanille.").also { stage = END_DIALOGUE }
        } else {
            npcl(FaceAnim.NEUTRAL, "Sand is yellow,").also { stage = 4 }
        }
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> npcl(FaceAnim.HALF_ASKING, "Who wants to know?").also { stage++ }
            1 -> player(FaceAnim.HALF_ASKING, "I'm [player name]. I'm here investigating the possible murder of a wizard.").also { stage++ }
            2 -> npcl(FaceAnim.HALF_ASKING, "I don't care about that, I have far too much work to do. Let the authorities take care of things like murder and stop snooping around my office!").also { stage++ }
            3 -> sendDialogue(player, "Sand seems very keen to get you out of the office, perhaps you should take a look around.").also {
                setQuestStage(player, Quests.THE_HAND_IN_THE_SAND, 5)
                setVarbit(player, Vars.VARBIT_QUEST_THE_HAND_IN_THE_SAND_PROGRESS_1527, 2, true)
                stage = END_DIALOGUE
            }

            4 -> npcl(FaceAnim.NEUTRAL, "Sand is grand,").also { stage++ }
            5 -> npcl(FaceAnim.NEUTRAL, "Sand puts money,").also { stage++ }
            6 -> npcl(FaceAnim.NEUTRAL, "In my hand!").also { stage = END_DIALOGUE }

        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = SandyDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.SANDY_3112)
}
