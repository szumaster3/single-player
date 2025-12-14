package content.region.kandarin.seers_village.hemenster.quest.fishingcompo.dialogue

import core.game.dialogue.Dialogue
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Joshua dialogue.
 *
 * # Relations
 * - [Fishing Contest][content.region.kandarin.seers_village.hemenster.quest.fishingcompo.FishingContest]
 */
@Initializable
class JoshuaDialogue(player: Player? = null) : Dialogue(player) {
    
    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc("Yeah? What do you want?")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> showTopics(
                Topic("Um... nothing really...", 1),
                Topic("Can I fish here instead of you?", 2),
                Topic("Do you have any tips for me?", 3),
            )
            1 -> npc("Quit bugging me then, dude!", "I got me some fish to catch!").also { stage = END_DIALOGUE }
            2 -> npc("nuh uh dude. Less talk, more fishing!").also { stage = END_DIALOGUE }
            3 -> npc("Dude! Why should I help you?", "You like, might beat me!", "I'm not giving away my secrets like that", "dude Grandpa Jack does!").also { stage++ }
            4 -> player("Who's Grandpa Jack?").also { stage++ }
            5 -> npc("Who's Grandpa Jack you say!", "He won this competition four years in a row!", "He lives in the house just outside the gate.").also { stage = END_DIALOGUE }

        }
        return true
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.JOSHUA_229)
}
