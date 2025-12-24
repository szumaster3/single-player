package content.region.kandarin.plugin

import core.api.MapArea
import core.game.world.map.zone.ZoneBorders

class CombatTrainingCamp : MapArea {

    override fun defineAreaBorders(): Array<ZoneBorders> {
        val cage = ZoneBorders(2521, 3369, 2533, 3377)
        cage.addException(ZoneBorders(2523, 3373, 2533, 3377))
        return arrayOf(cage)
    }
}