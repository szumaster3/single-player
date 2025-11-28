package content.region.other.entrana.quest.zep.cutscene

import core.game.activity.Cutscene
import core.game.node.entity.player.Player

class SeaCutscene(player: Player) : Cutscene(player) {
    override fun setup() {
        setExit(player.location.transform(0, 0, 0))
        if (player.settings.isRunToggled) {
            player.settings.toggleRun()
        }
        loadRegion(7244)
    }

    override fun runStage(stage: Int) {
        when (stage) {
            0 -> {
                end()
                timedUpdate(1)
            }
        }
    }
}
