package content.region.kandarin.quest.phoenix.handlers

import core.game.activity.Cutscene
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.world.map.Direction
import org.rs.consts.NPCs

class PhoenixEgglingCutscene(player: Player) : Cutscene(player) {

    override fun setup() {
        val x = if (player.location.regionX == 46) 46 else player.location.regionX
        val y = if (player.location.regionY == 40) 40 else player.location.regionY
        setExit(player.location.transform(x, y, 0))
        loadRegion(player.location.regionId)
        addNPC(LARGE_EGG, 47, 46, Direction.SOUTH)
    }

    override fun runStage(stage: Int) {
        when (stage) {
            0 -> playerDialogueUpdate(FaceAnim.THINKING, "Ack! This isn't the way out. I'm lost! Hmm... what's that?")

            2 -> {
                teleport(player, 46, 40)
                timedUpdate(1)
            }

            3 -> {
                rotateCamera(
                    player.location.regionX,
                    player.location.regionY,
                    300
                )
                moveCamera(
                    player.location.regionX,
                    player.location.regionY,
                    300, 3
                )
                timedUpdate(3)
            }

            4 -> {
                resetCamera()

                moveCamera(
                    getNPC(LARGE_EGG)!!.location.regionX,
                    getNPC(LARGE_EGG)!!.location.regionY
                )
                rotateCamera(
                    player.location.regionX,
                    player.location.regionY,
                    100, 3
                )
                timedUpdate(6)
            }

            5 -> {
                rotateCameraBy(
                    getNPC(LARGE_EGG)!!.location.regionX - player.location.regionX,
                    getNPC(LARGE_EGG)!!.location.regionY - player.location.regionY,
                    300,
                    3
                )
                timedUpdate(6)
            }

            6 -> endWithoutFade()
        }
    }

    companion object {
        private const val LARGE_EGG = NPCs.LARGE_EGG_8552
    }
}
