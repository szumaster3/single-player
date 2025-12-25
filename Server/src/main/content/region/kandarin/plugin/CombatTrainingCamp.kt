package content.region.kandarin.plugin

import core.api.MapArea
import core.api.removeAttribute
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders

class CombatTrainingCamp : MapArea {

    override fun defineAreaBorders(): Array<ZoneBorders> {
        val cage = ZoneBorders(2521, 3369, 2533, 3377)
        cage.addException(ZoneBorders(2523, 3373, 2533, 3377))
        return arrayOf(cage)
    }

    override fun areaLeave(entity: Entity, logout: Boolean) {
        super.areaLeave(entity, logout)
        if(entity !is Player) return
        removeAttribute(entity, OgreNPCBehavior.Companion.AVA_WARNING_SHOWN)
    }
}