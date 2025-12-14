package content.region.desert.nardah.dialogue

import core.api.decantContainer
import core.api.lock
import core.game.dialogue.DialogueFile

/**
 * Represents the Zahur dialogue.
 */
class ZahurDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        when (stage) {
            0 -> npc("I can combine your potion vials to try and make", "the potions fit into fewer vials. This service is free.", "Would you like to do this?").also { stage++ }
            1 -> options("Yes", "No").also { stage++ }
            2 -> {
                lock(player!!, 3)
                if (buttonID == 1) {
                    val decantResult = decantContainer(player!!.inventory)
                    val toRemove = decantResult.first
                    val toAdd = decantResult.second
                    for (item in toRemove) {
                        player?.inventory?.remove(item)
                    }
                    for (item in toAdd) {
                        player?.inventory?.add(item)
                    }
                    npc("There, all done.")
                } else {
                    end()
                }
            }
        }
    }
}
