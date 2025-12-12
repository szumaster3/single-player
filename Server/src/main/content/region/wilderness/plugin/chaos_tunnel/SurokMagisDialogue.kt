package content.region.wilderness.plugin.chaos_tunnel

import core.api.getVarbit
import core.game.activity.ActivityPlugin
import core.game.activity.CutscenePlugin
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.world.map.Location
import core.game.world.map.build.DynamicRegion
import core.tools.END_DIALOGUE
import shared.consts.Music

/**
 * Represents the Surok Magis dialogue.
 */
class SurokMagisDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        if (player == null) return

        if (stage == 0) {
            npc(FaceAnim.NEUTRAL,"Hello, ${player!!.username}! The meddling adventurer.").also {
                if (getVarbit(player!!, 3524) >= 1) {
                    player!!.musicPlayer.unlock(Music.THE_WRONG_PATH_488)
                }
                stage++
            }
            return
        }

        when (stage) {
            1 -> player(FaceAnim.THINKING,"Surok! What are you doing here?", "How did you-").also { stage++ }
            2 -> npc(FaceAnim.ASKING, "Escape from Varrock Palace Library? That cruel", "imprisonment you left me in?").also { stage++ }
            3 -> player(FaceAnim.THINKING,"Well...er..yes.").also { stage++ }
            4 -> npc(FaceAnim.NEUTRAL, "Bah! A mere trifle for a powerful mage such as myself.", "There were plenty of other foolish people to help with", "my plans, you would do well to stay out of my way.").also { stage++ }
            5 -> player(FaceAnim.NEUTRAL, "Stop, Surok! As a member of the Varrock Palace Secret", "Guard, I arrest you! Again!").also { stage++ }
            6 -> npc(FaceAnim.NEUTRAL, "Ha! I tire of this meaningless drivel. Catch me if you can.").also { stage = END_DIALOGUE }
        }
    }

    class SurokCutscene() : CutscenePlugin("Surok Cutscene") {
        var scene: SurokScene? = null

        constructor(player: Player?) : this() {
            this.player = player
        }

        override fun start(player: Player, login: Boolean, vararg args: Any): Boolean {
            scene = args[0] as SurokScene
            region = DynamicRegion.create(scene!!.regionId)
            setRegionBase()
            registerRegion(region.id)
            return super.start(player, login, *args)
        }

        override fun newInstance(p: Player): ActivityPlugin = SurokCutscene(p)

        override fun getStartLocation(): Location = base.transform(scene!!.startData[0], scene!!.startData[1], 0)

        override fun getSpawnLocation(): Location? = null

        override fun configure() {}

        enum class SurokScene(val regionId: Int, val startData: IntArray) {
            ESCAPE(-1, intArrayOf())
        }
    }

}