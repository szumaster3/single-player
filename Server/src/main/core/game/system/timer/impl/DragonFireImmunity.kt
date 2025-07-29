package core.game.system.timer.impl

import com.google.gson.JsonObject
import core.api.playAudio
import core.api.sendMessage
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.system.timer.PersistTimer
import core.game.system.timer.RSTimer
import core.game.system.timer.TimerFlag
import core.tools.colorize
import core.tools.secondsToTicks
import org.rs.consts.Sounds

class DragonFireImmunity : PersistTimer(1, "dragonfire:immunity", isSoft = true, flags = arrayOf(TimerFlag.ClearOnDeath)) {
    var ticksRemaining = 0

    override fun save(
        root: JsonObject,
        entity: Entity,
    ) {
        root.addProperty("ticksRemaining", ticksRemaining)
    }

    override fun parse(
        root: JsonObject,
        entity: Entity,
    ) {
        ticksRemaining = root.get("ticksRemaining")?.asInt ?: 0
    }

    override fun run(entity: Entity): Boolean {
        ticksRemaining--

        if (entity is Player && ticksRemaining == secondsToTicks(30)) {
            sendMessage(entity, colorize("%RYou have 30 seconds remaining on your antifire potion."))
            playAudio(entity, Sounds.CLOCK_TICK_1_3120, 0, 3)
        } else if (entity is Player && ticksRemaining == 0) {
            sendMessage(entity, colorize("%RYour antifire potion has expired."))
            playAudio(entity, Sounds.DRAGON_POTION_FINISHED_2607)
        }

        return ticksRemaining > 0
    }

    override fun getTimer(vararg args: Any): RSTimer {
        val t = DragonFireImmunity()
        t.ticksRemaining = args.getOrNull(0) as? Int ?: 100
        return t
    }
}
