package content.region.misthalin.draynor.quest.swept_cat.plugin

import content.global.skill.summoning.pet.Pets
import content.region.misthalin.draynor.quest.swept_cat.WendyPurpleCatDialogueExtension
import content.region.misthalin.draynor.quest.swept_cat.WendyRegularCatDialogueExtension
import content.region.misthalin.draynor.quest.swept_cat.purpleCats
import core.api.isQuestComplete
import core.api.openDialogue
import core.api.sendMessage
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import shared.consts.NPCs
import shared.consts.Quests

/**
 * Handles Wendy transforming a player cat into a purple cat.
 */
class WendyCatMorphPlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles using cats on Wendy NPC (Purple cats mini-quest).
         */

        onUseAnyWith(IntType.NPC, NPCs.WENDY_8201) { player, item, node ->
            val petData = Pets.forId(item.id)
            if (petData == null) {
                sendMessage(player, "I can't do anything with that.")
                return@onUseAnyWith false
            }

            if (player.familiarManager.hasPet()) {
                sendMessage(player, "You need to pick up your pet first to do that.")
                return@onUseAnyWith false
            }

            val isPurpleCat = item.id in purpleCats

            if (!isQuestComplete(player, Quests.SWEPT_AWAY)) {
                sendMessage(player, "You can't use your cat on Wendy yet.")
                return@onUseAnyWith false
            }

            val npc = node.asNpc()

            npc.resetWalk()
            npc.lock(10)
            npc.faceTemporary(player, 10)

            if (isPurpleCat) {
                openDialogue(player, WendyPurpleCatDialogueExtension())
            } else {
                openDialogue(player, WendyRegularCatDialogueExtension())
            }

            return@onUseAnyWith true
        }
    }
}