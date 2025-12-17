package content.global.skill.slayer.location.waterbirth_dungeon

import core.api.playAudio
import core.api.sendGraphics
import core.api.stopWalk
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.world.GameWorld
import core.game.world.map.RegionManager
import core.game.world.map.path.ClipMaskSupplier
import shared.consts.Graphics
import shared.consts.NPCs
import shared.consts.Sounds

class SuspiciousWaterBehavior : NPCBehavior(
    NPCs.SUSPICIOUS_WATER_2895
) {

    companion object {
        private const val ATTR_TRIGGERED = "suspicious_water:triggered"
        private const val ATTR_NEXT_ACTION = "suspicious_water:next_action"

        private val SPINOLYPS = intArrayOf(
            NPCs.SPINOLYP_2894,
            NPCs.SPINOLYP_2896
        )
    }

    override fun onCreation(self: NPC) {
        self.attributes.remove(ATTR_TRIGGERED)
        self.attributes[ATTR_NEXT_ACTION] = 0L

        self.isAggressive = true
        self.isNeverWalks = false
    }

    override fun tick(self: NPC): Boolean {
        val tick = GameWorld.ticks.toLong()
        val players = RegionManager.getLocalPlayers(self.location, 4)
        if (players.isEmpty()) {
            return false
        }

        val triggered = self.attributes[ATTR_TRIGGERED] as? Boolean ?: false
        val nextAction = self.attributes[ATTR_NEXT_ACTION] as? Long ?: 0L

        if (triggered) {
            return false
        }

        if (tick < nextAction) {
            return true
        }

        self.walkingQueue.reset()
        sendGraphics(Graphics.WATER_SPLASH_68, self.location)
        playAudio(players.first(), Sounds.WATERSPLASH_2496)

        self.transform(SPINOLYPS.random())
        self.attributes[ATTR_TRIGGERED] = true
        self.attributes[ATTR_NEXT_ACTION] = tick + 2

        return true
    }

    override fun onRespawn(self: NPC) {
        self.attributes.remove(ATTR_TRIGGERED)
        self.attributes[ATTR_NEXT_ACTION] = 0L
        self.transform(NPCs.SUSPICIOUS_WATER_2895)
    }

    override fun getClippingSupplier(self: NPC): ClipMaskSupplier = WaterClipping
}

private object WaterClipping : ClipMaskSupplier {
    override fun getClippingFlag(z: Int, x: Int, y: Int): Int =
        RegionManager.getWaterClipFlag(z, x, y)
}