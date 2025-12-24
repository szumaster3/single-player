package core.game.system.timer.impl

import com.google.gson.JsonObject
import core.api.sendMessage
import core.game.node.entity.Entity
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.player.Player
import core.game.system.timer.PersistTimer
import core.game.system.timer.RSTimer
import core.game.system.timer.TimerFlag
import core.game.world.repository.Repository

class Poison : PersistTimer(30, "poison", flags = arrayOf(TimerFlag.ClearOnDeath)) {
    lateinit var damageSource: Entity

    var severity = 0

    override fun save(root: JsonObject, entity: Entity) {
        root.addProperty("source-uid", (damageSource as? Player)?.details?.uid ?: -1)
        root.addProperty("severity", severity)
    }

    override fun parse(root: JsonObject, entity: Entity) {
        val uid = root.get("source-uid")?.asInt ?: -1
        damageSource = Repository.getPlayerByUid(uid) ?: entity
        severity = root.get("severity")?.asInt ?: 0
    }

    override fun onRegister(entity: Entity) {
        if (entity is Player) {
            sendMessage(entity, "You have been poisoned.")
            entity.debug("[Poison] -> Received for $severity severity.")
        }
        if (damageSource is Player)
            (damageSource as? Player)?.debug("[Poison] -> Applied for $severity severity.")
    }

    override fun run(entity: Entity): Boolean {
        entity.impactHandler.manualHit(
            damageSource,
            getDamageFromSeverity(severity--),
            ImpactHandler.HitsplatType.POISON
        )

        if (severity == 0 && entity is Player)
            sendMessage(entity, "The poison has worn off.")
        return severity > 0
    }

    override fun getTimer(vararg args: Any): RSTimer {
        val timer = Poison()
        timer.damageSource = args[0] as? Entity ?: return timer
        timer.severity = args[1] as? Int ?: return timer
        return timer
    }

    private fun getDamageFromSeverity(severity: Int): Int {
        return (severity + 4) / 5
    }
}