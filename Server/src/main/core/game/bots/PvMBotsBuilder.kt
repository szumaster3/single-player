package core.game.bots

import content.minigame.pestcontrol.bots.PestControlScript
import content.minigame.pestcontrol.plugin.PCUtils
import core.game.world.map.Location

class PvMBotsBuilder {
    companion object {
        private var botsSpawned = 0

        fun create(l: Location?): PvMBots {
            botsSpawned++
            return PvMBots(l)
        }

        @JvmStatic
        fun createNovicePCBots(l: Location?): PestControlScript {
            botsSpawned++
            return PestControlScript(l!!, PCUtils.LanderZone.NOVICE)
        }

        @JvmStatic
        fun createIntermediatePCBots(l: Location?): PestControlScript {
            botsSpawned++
            return PestControlScript(l!!, PCUtils.LanderZone.INTERMEDIATE)
        }

        @JvmStatic
        fun createVeteranPCBots(l: Location?): PestControlScript {
            botsSpawned++
            return PestControlScript(l!!, PCUtils.LanderZone.VETERAN)
        }
    }
}
