package content.region.kandarin.seers_village.dialogue

import content.region.kandarin.seers_village.npc.IgnatiusVulcanNPC
import core.api.runTask
import core.api.sendChat
import core.game.dialogue.Dialogue
import core.game.global.Skillcape.isMaster
import core.game.global.Skillcape.purchase
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.NPCs

/**
 * Represents the Ignatius Vulcan dialogue.
 *
 * # Relations
 * - [Ignatius Vulcan NPC][content.region.kandarin.seers_village.npc.IgnatiusVulcanNPC]
 */
@Initializable
class IgnatiusVulcanDialogue(player: Player? = null) : Dialogue(player) {
    
    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npc("Can I help you at all?")
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> if (isMaster(player, Skills.FIREMAKING)) {
                options("Who are you?", "Could I buy a Skillcape of Firemaking?", "No, thanks.").also { stage = 2 }
            } else {
                options("Who are you?", "What is that cape you're wearing?", "No, thanks.").also { stage++ }
            }
            1 -> when (buttonId) {
                1 -> player("Who are you?").also { stage = 7 }
                2 -> player("What is that cape you're wearing?").also { stage = 11 }
                3 -> player("No, thanks.").also { stage = END_DIALOGUE }
            }
            2 -> when (buttonId) {
                1 -> player("Who are you?").also { stage = 7 }
                2 -> player("Could I buy a Skillcape of Firemaking?").also { stage++ }
                3 -> player("No, thanks.").also { stage = END_DIALOGUE }
            }
            3 -> npc("Certainly! Right when you give me 99000 coins.").also { stage++ }
            4 -> options("Okay, here you go.", "No, thanks.").also { stage++ }
            5 -> when (buttonId) {
                1 -> player("Okay, here you go.").also { stage++ }
                2 -> end()
            }
            6 -> {
                if (purchase(player, Skills.FIREMAKING)) {
                    npc("There you go! Enjoy.")
                }
                stage = END_DIALOGUE
            }
            7 -> npc("My name is Ignatius Vulcan. Once I was - like you -", "an adventurer, but that was before I realised the", "beauty and power of flame! Just look at this...").also { stage++ }
            8 -> {
                IgnatiusVulcanNPC.createFire(npc, player.location)
                runTask(player, 1) {
                    player.moveStep()
                    sendChat(player,"Yeeouch!")
                    npc("Stare into the flame and witness the purity and power", "of fire! As my attraction to flame grew, so did my skills", "at firelighting. I began to neglect my combat skills, my", "Mining skills and my questing. Who needs such")
                    stage++
                }
            }
            9 -> npc("mundane skills when one can harness the power of fire?", "After years of practice I am now the acknowledged", "master of Flame! Everything must be purified by fire!").also { stage++ }
            10 -> player("Okaaay! err, I'll be going now. Umm, get better soon.").also { stage = END_DIALOGUE }
            11 -> npc("This is a Skillcape of Firemaking. I was given it in", "recognition of my skill as the greatest firemaker in the", "lands! I AM the Master of Flame!").also { stage++ }
            12 -> player("Hmm, I'll be going now. Keep a sharp look out for", "those men with their white jackets!").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = IgnatiusVulcanDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.IGNATIUS_VULCAN_4946)
}