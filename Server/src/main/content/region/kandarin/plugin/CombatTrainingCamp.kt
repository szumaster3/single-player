package content.region.kandarin.plugin

import core.api.*
import core.game.world.map.zone.ZoneBorders
class CombatTrainingCamp : MapArea {

    companion object {
        val CAGE_AREA =
            ZoneBorders(2523, 3373, 2533, 3377)
    }

    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(ZoneBorders(2521, 3369, 2533, 3377))
    }
}