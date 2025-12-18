package content.global.skill.hunter

import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.world.update.flag.context.Animation

class MagicBoxSetting : TrapSetting(
    /* nodeId = */              10025,
    /* objectIds = */           intArrayOf(19223),
    /* baitIds = */             intArrayOf(1470, 1472, 1476, 1474),
    /* option = */              "activate",
    /* failId = */              19224,
    /* setupAnimation = */      Animation.create(5208),
    /* dismantleAnimation = */  Animation.create(9726),
    /* level = */               27
) {
    override fun handleCatch(counter: Int, wrapper: TrapWrapper, node: TrapNode, npc: NPC, success: Boolean) {
        when (counter) {
            2 -> if (success) {
                wrapper.player.packetDispatch.sendPositionedGraphic(932, 0, 0, npc.location)
            }

            3 -> npc.moveStep()
        }
    }

    override fun addTool(player: Player, wrapper: TrapWrapper, type: Int) {
        if (!wrapper.isCaught) {
            super.addTool(player, wrapper, type)
        }
    }
}
