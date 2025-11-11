package content.global.activity.champion.dialogue

import content.data.GameAttributes
import content.global.activity.champion.plugin.ChampionDefinition
import content.global.activity.champion.plugin.ChampionScrollsDropHandler
import core.api.*
import core.game.dialogue.*
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Scenery

/**
 * Represents the dialogue plugin used for the Larxus NPC.
 */
@Initializable
class LarxusDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        npc = NPC(NPCs.LARXUS_3050)
        val defeatAll = getAttribute(player, GameAttributes.ACTIVITY_CHAMPIONS_CHALLENGE_DEFEAT_ALL, false)
        val activityComplete = getAttribute(player, GameAttributes.ACTIVITY_CHAMPIONS_COMPLETE, false)
        if(defeatAll && !activityComplete) {
            npc(FaceAnim.NEUTRAL, "Leon D'Cour has issued you a challenge, he has stated", "there will be no items allowed expect those you're", "wearing. Do you want to accept the challenge?")
            stage = 4
            return true
        }
        when (stage) {
            START_DIALOGUE -> npcl(FaceAnim.NEUTRAL, "Is there something I can help you with?").also { stage++ }
            1 -> showTopics(
                IfTopic(FaceAnim.HALF_ASKING,"I've defeated all the champions, what now?", 5, activityComplete),
                IfTopic(FaceAnim.HALF_ASKING,"I was given a challenge, what now?", 2, hasScroll(player) && !activityComplete),
                Topic(FaceAnim.HALF_ASKING,"What is this place?", 3),
                Topic(FaceAnim.NEUTRAL,"Nothing thanks.",END_DIALOGUE)
            )
            2 -> npcl(FaceAnim.NEUTRAL, "Well pass it here and we'll get you started.").also { stage = END_DIALOGUE }
            3 -> npcl(FaceAnim.NEUTRAL, "This is the champions' arena, the champions of various races use it to duel those they deem worthy of the honour.").also { stage = END_DIALOGUE }
            4 -> {
                end()
                openDialogue(player, LarxusDialogueFile(false))
            }
            5 -> npc(FaceAnim.NEUTRAL, "Well keep a watch out, more champions may rise to test", "your mettle in the future.").also { stage = END_DIALOGUE }
        }
        return true
    }

    private fun hasScroll(player: Player?) = ChampionScrollsDropHandler.SCROLLS.any { player?.inventory?.getItem(it.asItem()) != null }

    override fun getIds(): IntArray = intArrayOf(NPCs.LARXUS_3050)
}

/**
 * Handles dialogue for starting the champions challenge.
 */
class LarxusDialogueFile(private val challengeStart: Boolean = false, private val scrollItem: Item? = null) : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.LARXUS_3050)
        if (!challengeStart || scrollItem == null) return

        val scrollId = scrollItem.id
        val entry = ChampionDefinition.fromScroll(scrollId)

        when (stage) {
            0 -> {
                face(findNPC(NPCs.LARXUS_3050)!!, player!!, 2)
                if (entry?.varbitId != null && getVarbit(player!!, entry.varbitId) == 1) {
                    removeItem(player!!, scrollId)
                    npc(FaceAnim.NEUTRAL, "You've already defeated this Champion, the challenge is", "void.")
                    stage = END_DIALOGUE
                    return
                }

                val prefix = "So you want to accept the challenge huh? Well there are some specific rules for these Champion fights. For"
                val scrollMessage = when (scrollId) {
                    // Earth Warrior.
                    Items.CHAMPION_SCROLL_6798 -> "$prefix this fight you're not allowed to use any Prayer's. Do you still want to proceed?"
                    // Ghoul.
                    Items.CHAMPION_SCROLL_6799 -> "$prefix this fight you're only allowed to take Weapons, no other items are allowed. Do you still want to proceed?"
                    // Giant.
                    Items.CHAMPION_SCROLL_6800 -> "$prefix this fight you're only allowed to use Melee attacks, no Ranged or Magic. Do you still want to proceed?"
                    // Goblin.
                    Items.CHAMPION_SCROLL_6801 -> "$prefix this fight you're only allowed to use Magic attacks, no Melee or Ranged. Do you still want to proceed?"
                    // Hobgoblin.
                    Items.CHAMPION_SCROLL_6802 -> "$prefix this fight you're not allowed to use any Melee attacks. Do you still want to proceed?"
                    // Imp.
                    Items.CHAMPION_SCROLL_6803 -> "$prefix this fight you're not allowed to use any Special Attacks. Do you still want to proceed?"
                    // Jogre.
                    Items.CHAMPION_SCROLL_6804 -> "$prefix this fight you're not allowed to use any Ranged attacks. Do you still want to proceed?"
                    // Lesser Demon.
                    Items.CHAMPION_SCROLL_6805 -> "$prefix this fight you're allowed to use any Weapons or Armour. Do you still want to proceed?"
                    // Skeleton.
                    Items.CHAMPION_SCROLL_6806 -> "$prefix this fight you're only allowed to use Ranged attacks, no Melee or Magic. Do you still want to proceed?"
                    // Zombie.
                    Items.CHAMPION_SCROLL_6807 -> "$prefix this fight you're not allowed to use any Magic attacks. Do you still want to proceed?"

                    else -> null
                }

                scrollMessage?.let { npcl(FaceAnim.NEUTRAL, it) }
                stage = 1
            }
            1 -> showTopics(
                Topic(FaceAnim.FRIENDLY,"Yes, let me at him!", 2),
                Topic(FaceAnim.NEUTRAL,"No, thanks I'll pass.", END_DIALOGUE)
            )
            2 -> {
                npcl(FaceAnim.NEUTRAL, "Your challenger is ready, please go down through the trapdoor when you're ready.")
                val trapdoorLoc = getScenery(Location.create(3184, 9758, 0))
                replaceScenery(trapdoorLoc!!.asScenery(), Scenery.CHAMPION_STATUE_10557, 100)
                scrollItem.let { item ->
                    val usedScroll = player!!.inventory.getItem(item)
                    usedScroll?.let { setCharge(it, it.id) }
                }
                stage = END_DIALOGUE
            }
        }
    }
}