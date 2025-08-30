package content.region.misthalin.draynor.quest.swept_cat

import content.global.skill.summoning.pet.Pets
import core.api.openDialogue
import core.api.isQuestComplete
import core.api.sendMessage
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.npc.NPC
import core.game.node.item.Item
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Quests

val purpleCats = intArrayOf(
    Items.PET_CAT_14090,
    Items.LAZY_CAT_14091,
    Items.WILY_CAT_14093,
)

class CatOnWendyPlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles using cats on Wendy NPC (Purple cats mini-quest).
         */

        onUseWith(IntType.NPC, NPCs.WENDY_8201) { player, item, _ ->
            val petData = Pets.forId(item.id)
            if (petData == null) {
                sendMessage(player, "I can't do anything with that.")
                return@onUseWith false
            }

            if (!isQuestComplete(player, Quests.SWEPT_AWAY)) {
                sendMessage(player, "You can't use your cat on Wendy yet.")
                return@onUseWith false
            }

            openDialogue(player, WendyRegularCatDialogueExtension())
            return@onUseWith true
        }

        /*
         * Handles using purple cats on Wendy NPC.
         */

        onUseWith(IntType.NPC, NPCs.WENDY_8201, *purpleCats) { player, _, _ ->
            sendMessage(player, "You show your purple cat to Wendy.")
            openDialogue(player, WendyPurpleCatDialogueExtension())
            return@onUseWith true
        }
    }
}

class WendyRegularCatDialogueExtension : DialogueFile() {
    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.WENDY_8201)

        when (stage) {
            0 -> player("Could you make this cat purple for me?").also { stage++ }
            1 -> npcl(FaceAnim.FRIENDLY, "I can, but I should warn you: I can make cats purple, but I don't know how to change them back. It's a permanent change! Is that okay?").also { stage++ }
            2 -> options("Yes, please make my cat permanently purple.", "No, I think I'll hold off for the time being.").also { stage++ }
            3 -> when (buttonID) {
                1 -> player("Yes, please make my cat permanently purple.").also { stage = 5 }
                2 -> player(FaceAnim.NEUTRAL, "No, I think I'll hold off for the time being.").also { stage = 7 }
            }
            5 -> {
                val familiar = player?.familiarManager?.familiar
                val inv = player?.inventory?.toArray() ?: arrayOfNulls<Item>(0)
                val petID = inv.firstOrNull { it != null && Pets.forId(it.id) != null }

                if (familiar == null && petID == null) {
                    npcl(null, "Of course. Just bring your cat to me and let me hold her for a moment. I'll sort her out in no time.")
                    stage = 7
                    return
                }

                val petItemID = familiar?.id ?: petID?.id
                val petData = Pets.forId(petItemID!!) ?: run {
                    npcl(null, "Hmm, I can't figure out this cat.")
                    stage = 7
                    return
                }

                val purpleCatID = when (petItemID) {
                    petData.babyItemId -> Pets.CAT_6.babyItemId
                    petData.grownItemId -> Pets.CAT_6.grownItemId
                    petData.overgrownItemId -> Pets.CAT_6.overgrownItemId
                    petData.lazyItemId -> Pets.CAT_6.lazyItemId
                    petData.wilyItemId -> Pets.CAT_6.wilyItemId
                    else -> run {
                        end()
                        return
                    }
                }

                player?.familiarManager?.morphPet(Item(purpleCatID), false, familiar?.location ?: player?.location)
                npcl(null, "There you go! Oh, such a beautiful colour.")
                stage = 6
            }
            6 -> player("Thank you.").also { stage++ }
            7 -> end()
        }
    }
}

class WendyPurpleCatDialogueExtension : DialogueFile() {
    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.WENDY_8201)
        when (stage) {
            0 -> npc("Oh, what a lovely cat you have there. Hello, kitty!").also { stage++ }
            1 -> options("Can you change this cat back to its normal colour?", "Thank you.").also { stage++ }
            2 -> when (buttonID) {
                1 -> player("Can you change this cat back to its normal colour?").also { stage++ }
                2 -> player("Thank you.").also { stage = 5 }
            }
            3 -> npcl(FaceAnim.STRUGGLE, "Um, did I forget to mention...once the spell is cast, it's cast for good. I can't undo the purple.").also { stage++ }
            4 -> player(FaceAnim.HALF_CRYING, "Ah, right. Hmm.").also { stage = 5 }
            5 -> end()
        }
    }
}
