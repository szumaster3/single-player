package core.game.system.timer

import com.google.gson.JsonObject
import core.api.getWorldTicks
import core.game.node.entity.Entity
import kotlin.reflect.full.createInstance

/**
 * Abstract timer with support for persistence (saving and loading state).
 */
abstract class PersistTimer(
    runInterval: Int,
    identifier: String,
    isSoft: Boolean = false,
    isAuto: Boolean = false,
    flags: Array<TimerFlag> = arrayOf(),
) : RSTimer(runInterval, identifier, isSoft, isAuto, flags) {

    open fun save(root: JsonObject, entity: Entity) {
        val ticksLeft = nextExecution - getWorldTicks()
        root.addProperty("ticksLeft", ticksLeft)
    }

    open fun parse(root: JsonObject, entity: Entity) {
        val ticksLeft = root.get("ticksLeft")?.asInt ?: 0
        nextExecution = getWorldTicks() + ticksLeft
    }

    override fun retrieveInstance(): RSTimer = this::class.createInstance()
}