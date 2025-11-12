package content.region.kandarin.seers_village.dialogue

import com.google.gson.JsonObject
import core.ServerStore
import core.api.hasSpaceFor
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.Diary
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

/**
 * Handles the Geoffrey dialogue in Seers' Village.
 */
@Initializable
class GeoffreyDialogue(player: Player? = null) : Dialogue(player) {

    override fun open(vararg args: Any?): Boolean {
        var gotoStage = when {
            Diary.hasClaimedLevelRewards(player, DiaryType.SEERS_VILLAGE, 2) -> 13
            Diary.hasClaimedLevelRewards(player, DiaryType.SEERS_VILLAGE, 1) -> 12
            Diary.hasClaimedLevelRewards(player, DiaryType.SEERS_VILLAGE, 0) -> 11
            else -> 0
        }

        val store = getStoreFile()
        val username = player?.username?.lowercase() ?: ""
        val alreadyClaimed = store[username]?.asBoolean ?: false
        val hasSpace = hasSpaceFor(player, Item(Items.FLAX_1780, 1)) ?: false

        if (gotoStage != 0) {
            player("Hello there. Are you Geoff-erm-Flax? I've been told that", "you'll give me some flax.")
            stage = when {
                alreadyClaimed -> 9
                !hasSpace -> 10
                else -> gotoStage
            }
        } else {
            player("Hello there. You look busy.")
            stage = 0
        }

        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> npc("Yes, I am very busy. Picking GLORIOUS flax.", "The GLORIOUS flax won't pick itself. So I pick it.", "I pick it all day long.").also { stage++ }
            1 -> player("Wow, all that flax must really mount up.", "What do you do with it all?").also { stage++ }
            2 -> npc("I give it away! I love picking the GLORIOUS flax,", "but, if I let it all mount up, I wouldn't have any", "room for more GLORIOUS flax.").also { stage++ }
            3 -> player("So, you're just picking the flax for fun? You must", "really like flax.").also { stage++ }
            4 -> npc("'Like' the flax? I don't just 'like' flax. The", "GLORIOUS flax is my calling, my reason to live.", "I just love the feeling of it in my hands!").also { stage++ }
            5 -> player("Erm, okay. Maybe I can have some of your spare flax?").also { stage++ }
            6 -> npc("No. I don't trust outsiders. Who knows what depraved", "things you would do with the GLORIOUS flax? Only", "locals know how to treat it right.").also { stage++ }
            7 -> player("I know this area! It's, erm, Seers' Village. There's", "a pub and, er, a bank.").also { stage++ }
            8 -> npc("Pah! You call that local knowledge? Perhaps if you", "were wearing some kind of item from one of the", "seers, I might trust you.").also { stage = END_DIALOGUE }
            9 -> npc("Don't be greedy. Other people want GLORIOUS flax too.", "You can have some more tomorrow.").also { stage = END_DIALOGUE }
            10 -> npc("Yes, but your inventory is full.", "Come back when you have some space for GLORIOUS flax.").also { stage = END_DIALOGUE }
            11 -> rewardFlax(30, "Yes. The seers have instructed me to give you an", "allowance of 30 GLORIOUS flax a day. I'm not going", "to argue with them, so here you go.")
            12 -> rewardFlax(60, "Yes. Stankers has instructed me to give you an", "allowance of 60 GLORIOUS flax a day. I'm not going", "to argue with a dwarf, so here you go.")
            13 -> rewardFlax(120, "Yes. Sir Kay has instructed me to give you an", "allowance of 120 GLORIOUS flax a day. I'm not going", "to argue with a knight, so here you go.")
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = GeoffreyDialogue(player)

    private fun rewardFlax(amount: Int, vararg messages: String) {
        npc(*messages)
        player?.inventory?.add(Item(Items.FLAX_1780, amount))
        val store = getStoreFile()
        val username = player?.username?.lowercase() ?: return
        store.addProperty(username, true)
        stage = END_DIALOGUE
    }

    override fun getIds(): IntArray = intArrayOf(NPCs.GEOFFREY_8590)

    private fun getStoreFile(): JsonObject = ServerStore.getArchive("daily-seers-flax")
}
