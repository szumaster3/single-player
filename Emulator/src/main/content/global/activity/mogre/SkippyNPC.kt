package content.global.activity.mogre

import core.api.*
import core.game.interaction.QueueStrength
import core.game.node.entity.npc.AbstractNPC
import core.game.world.map.Direction
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.RandomFunction
import org.rs.consts.Animations
import org.rs.consts.Graphics
import org.rs.consts.NPCs
import org.rs.consts.Sounds

/**
 * Represents Skippy NPC for Mogre lore activity.
 * @author szu
 */
@Initializable
class SkippyNPC(
    id: Int = 0,
    location: Location? = null
) : AbstractNPC(id, location) {

    override fun construct(id: Int, location: Location, vararg objects: Any): AbstractNPC =
        SkippyNPC(id, location)

    override fun getIds(): IntArray = intArrayOf(NPCs.SKIPPY_2795)

    override fun handleTickActions() {
        if (RandomFunction.roll(8)) {
            throwVial()
        }
    }

    private fun throwVial() {
        walkingQueue.reset()
        queueScript(this, 1, QueueStrength.SOFT) {
            sendChat("Take this")
            animate(this, Animations.THROW_385)
            faceLocation(location.transform(Direction.SOUTH))
            playGlobalAudio(location, Sounds.SKIPPY_THROWGLASS_1398)
            spawnProjectile(
                this.location,
                location.transform(Direction.SOUTH, 4),
                Graphics.THROWING_VIAL_49,
                30,
                0,
                10,
                100,
                0
            )
            return@queueScript stopExecuting(this)
        }
    }
}