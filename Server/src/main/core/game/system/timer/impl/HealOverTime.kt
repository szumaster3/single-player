package core.game.system.timer.impl

import com.google.gson.JsonObject
import core.api.hasTimerActive
import core.api.removeTimer
import core.game.node.entity.Entity
import core.game.system.timer.PersistTimer
import core.game.system.timer.RSTimer
import core.game.system.timer.TimerFlag
import kotlin.math.min

class HealOverTime :
    PersistTimer(
        runInterval = 1,
        identifier = "healovertime",
        flags = arrayOf(TimerFlag.ClearOnDeath),
    ) {
    var healRemaining = 0
    var healPerTick = 0

    override fun run(entity: Entity): Boolean {
        val amt = min(healRemaining, healPerTick)
        healRemaining -= amt
        entity.skills.heal(amt)
        return healRemaining > 0
    }

    override fun save(
        root: JsonObject,
        entity: Entity,
    ) {
        super.save(root, entity)
        root.addProperty("healRemaining", healRemaining)
        root.addProperty("healPerTick", healPerTick)
    }

    override fun parse(
        root: JsonObject,
        entity: Entity,
    ) {
        super.parse(root, entity)
        healRemaining = root.get("healRemaining")?.asInt ?: 0
        healPerTick = root.get("healPerTick")?.asInt ?: 0
    }

    override fun onRegister(entity: Entity) {
        if (hasTimerActive<MiasmicImmunity>(entity)) {
            removeTimer(entity, this)
        }
        if (hasTimerActive<Miasmic>(entity)) {
            removeTimer(entity, this)
        }
    }

    override fun getTimer(vararg args: Any): RSTimer {
        val t = HealOverTime()
        t.runInterval = args.getOrNull(0) as? Int ?: 100
        t.healRemaining = args.getOrNull(1) as? Int ?: 10
        t.healPerTick = args.getOrNull(2) as? Int ?: 2
        return t
    }
}
