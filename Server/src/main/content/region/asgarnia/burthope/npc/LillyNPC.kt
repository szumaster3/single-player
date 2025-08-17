package content.region.asgarnia.burthope.npc

import core.game.node.entity.npc.AbstractNPC
import core.game.world.map.Location
import core.plugin.Initializable
import core.tools.RandomFunction
import shared.consts.NPCs

/**
 * Handles the LillyNPC.
 */
@Initializable
class LillyNPC(id: Int = 0, location: Location? = null) : AbstractNPC(id, location) {
    private var timer = 0L
    private val forceChat: Array<String> = arrayOf(
        "It's not an optical illusion, it just looks like one.",
        "Madness takes its toll. Please have exact change.",
        "If you don't care where you are, then you ain't lost.",
        "Don't look back, they might be gaining on you.",
        "Demons are a Ghouls best friend.",
        "Pink bananas... mmmmm.",
    )

    override fun construct(id: Int, location: Location, vararg objects: Any): AbstractNPC = AntonNPC(id, location)

    override fun getIds(): IntArray = intArrayOf(NPCs.LILLY_4294)

    override fun tick() {
        val now = System.currentTimeMillis()
        if (now < timer) return
        if (RandomFunction.random(100) < 3) {
            sendChat(forceChat.random())
            timer = now + 3000
        }
    }


    override fun init() {
        super.init()
        isWalks = true
        isNeverWalks = false
    }

    override fun getWalkRadius(): Int = 3
}
