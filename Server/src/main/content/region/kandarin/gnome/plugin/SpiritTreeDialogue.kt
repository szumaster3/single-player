package content.region.kandarin.gnome.plugin

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.player.Player
import core.game.system.task.Pulse
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.game.node.entity.player.link.diary.DiaryType
import core.tools.END_DIALOGUE
import shared.consts.Animations
import shared.consts.Graphics as Gfx

/**
 * Spirit Tree Dialogue (Travel System)
 */
class SpiritTreeDialogue(private val showIntro: Boolean) : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> {
                if (showIntro) {
                    returnAtStage(1)
                } else {
                    npc(
                        FaceAnim.CHILD_NEUTRAL,
                        "If you are a friend of the gnome people, you are a friend of mine.",
                        "Do you wish to travel?"
                    )
                    stage++
                }
            }

            1 -> {
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "Tree Gnome Village", 2, true),
                    Topic(FaceAnim.NEUTRAL, "Tree Gnome Stronghold", 3, true),
                    Topic(FaceAnim.NEUTRAL, "Battlefield of Khazard", 4, true),
                    Topic(FaceAnim.NEUTRAL, "Grand Exchange", 5, true),
                    Topic(FaceAnim.NEUTRAL, "Nevermind.", END_DIALOGUE, true),
                )
            }

            2 -> { end(); sendTeleport(player!!, Location(2542, 3170, 0)) }
            3 -> { end(); sendTeleport(player!!, Location(2461, 3444, 0)) }
            4 -> { end(); sendTeleport(player!!, Location(2556, 3259, 0)) }
            5 -> { end(); sendTeleport(player!!, Location(3184, 3508, 0)) }
        }
    }

    private fun sendTeleport(player: Player, location: Location) {
        submitWorldPulse(object : Pulse(1, player) {
            var count = 0
            override fun pulse(): Boolean {
                when (count) {
                    0 -> visualize(
                        player,
                        Animation(Animations.HUMAN_SPIRIT_TREE_TELEPORT_7082),
                        Graphics(Gfx.GRAND_TREE_TP_A_1228)
                    )
                    3 -> teleport(player, location)
                    5 -> {
                        visualize(
                            player,
                            Animation(Animations.HUMAN_SPIRIT_TREE_TELEPORT_FROM_7084),
                            Graphics(Gfx.GRAND_TREE_TP_B_1229)
                        )
                        player.face(null)
                        if (withinDistance(player, Location.create(3184, 3508, 0))) {
                            finishDiaryTask(player, DiaryType.VARROCK, 1, 5)
                        }
                        return true
                    }
                }
                count++
                return false
            }
        })
    }
}
