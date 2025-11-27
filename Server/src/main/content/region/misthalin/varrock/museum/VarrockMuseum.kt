package content.region.misthalin.varrock.museum

import core.api.MapArea
import core.api.closeOverlay
import core.api.openOverlay
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders
import shared.consts.Components

class VarrockMuseum : MapArea {

    override fun areaEnter(entity: Entity) {
        if (entity is Player) {
            val player = entity.asPlayer()
            openOverlay(player, Components.VM_KUDOS_532)
        }
    }

    override fun areaLeave(entity: Entity, logout: Boolean) {
        if (entity is Player) {
            val player = entity.asPlayer()
            closeOverlay(player)
        }
    }

    override fun defineAreaBorders(): Array<ZoneBorders> {
        return VARROCK_MUSEUM
    }

    companion object {
        private val VARROCK_MUSEUM = arrayOf(ZoneBorders(3253, 3442, 3267, 3455), ZoneBorders(1730, 4932, 1788, 4988))
    }
}