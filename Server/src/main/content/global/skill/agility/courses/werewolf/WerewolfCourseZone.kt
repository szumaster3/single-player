package content.global.skill.agility.courses.werewolf

import core.api.MapArea
import core.api.anyInInventory
import core.api.removeAll
import core.api.sendMessage
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders
import shared.consts.Items

class WerewolfCourseZone : MapArea {

    override fun defineAreaBorders(): Array<ZoneBorders> = arrayOf(ZoneBorders(3510, 9851, 3592, 9920))

    override fun areaEnter(entity: Entity) {}

    override fun areaLeave(entity: Entity, logout: Boolean) {
        if (entity is Player) {
            val p = entity.asPlayer()
            if (anyInInventory(p, Items.STICK_4179)) {
                removeAll(p, Items.STICK_4179)
                sendMessage(entity, "The werewolf trainer removes your stick as you leave.")
            }
        }
    }
}
