package content.global.scripts

import core.game.bots.Script

class Idler : Script() {
    override fun tick() {
    }

    override fun newInstance(): Script = this
}
