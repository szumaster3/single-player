package content.global.skill.farming

import core.api.inInventory
import core.api.note
import core.api.removeItem
import core.cache.def.impl.DataMap
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.item.Item
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs

/**
 * Represents the Farmer protection dialogue.
 */
class FarmerPayOptionDialogue(val patch: Patch, val quickPay: Boolean = false) : DialogueFile() {
    var item: Item? = null

    private val protectionMap = DataMap.get(2024)

    override fun handle(componentID: Int, buttonID: Int) {
        val faceAnim = if (npc!!.id in intArrayOf(NPCs.PRISSY_SCILLA_1037, NPCs.BOLONGO_2343))
            FaceAnim.OLD_NORMAL else FaceAnim.HALF_GUILTY

        when (stage) {
            START_DIALOGUE -> {
                if (patch.patch.type == PatchType.TREE_PATCH && patch.plantable != null && patch.isGrown()) {
                    showTopics(
                        Topic("Yes, get rid of the tree.", 300, true),
                        Topic("No thanks.", END_DIALOGUE, true),
                        title = "Pay 200 gp to have the tree chopped down?"
                    )
                } else if (patch.protectionPaid) {
                    npc(faceAnim, "I don't know what you're talking about - I'm already", "looking after that patch for you.")
                    stage = 100
                } else if (patch.isDead) {
                    npc(faceAnim, "That patch is dead - it's too late for me to do", "anything about it now.")
                    stage = END_DIALOGUE
                } else if (patch.isDiseased) {
                    npc(faceAnim, "That patch is diseased - I can't look after it", "until it has been cured.")
                    stage = END_DIALOGUE
                } else if (patch.isWeedy() || patch.isEmptyAndWeeded()) {
                    npc(faceAnim, "You don't have anything planted in that patch. Plant", "something and I might agree to look after it for you.")
                    stage = END_DIALOGUE
                } else if (patch.isGrown()) {
                    npc(faceAnim, "That patch is already fully grown!", "I don't know what you want me to do with it!")
                    stage = END_DIALOGUE
                } else {
                    item = patch.plantable?.protectionItem
                    val resourceId = getProtectionText(item)

                    if (item == null) {
                        if (patch.plantable?.harvestItem == Items.POISON_IVY_BERRIES_6018) {
                            npc(faceAnim, "There is no need for me to look after that poison ivy.")
                            stage = 400
                        } else {
                            npc(faceAnim, "Sorry, I won't protect that.")
                            stage = END_DIALOGUE
                        }
                    } else if (quickPay && !(inInventory(player!!, item!!.id, item!!.amount) || inInventory(player!!, note(item!!).id, note(item!!).amount))) {
                        val amount = if (item!!.amount == 1) "one" else item!!.amount.toString()
                        npc(faceAnim, "I want $amount $resourceId for that.")
                        stage = 200
                    } else if (quickPay) {
                        val amount = if (item!!.amount == 1) "one" else item!!.amount.toString()
                        showTopics(
                            Topic("Yes", 20, true),
                            Topic("No", END_DIALOGUE, true),
                            title = "Pay $amount $resourceId?"
                        )
                    } else {
                        val amountText = if (item!!.amount == 1) "one" else item!!.amount.toString()
                        npc(faceAnim, "If you like, but I want $amountText $resourceId for that.")
                        stage++
                    }
                }
            }

            1 -> {
                if (!(inInventory(player!!, item!!.id, item!!.amount) || inInventory(player!!, note(item!!).id, note(item!!).amount))) {
                    player("I'm afraid I don't have any of those at the moment.")
                    stage = 10
                } else {
                    showTopics(
                        Topic(FaceAnim.NEUTRAL, "Okay, it's a deal.", 20),
                        Topic(FaceAnim.NEUTRAL, "No, that's too much.", 10)
                    )
                }
            }

            10 -> {
                npc(faceAnim, "Well, I'm not wasting my time for free.")
                stage = END_DIALOGUE
            }

            20 -> {
                if (removeItem(player!!, item) || removeItem(player!!, note(item!!))) {
                    patch.protectionPaid = true
                    npc(faceAnim, "That'll do nicely, ${if (player!!.isMale) "sir" else "madam"}. Leave it with me - I'll make sure", "those crops grow for you.")
                    stage = END_DIALOGUE
                } else {
                    npc(faceAnim, "This shouldn't be happening. Please report this.")
                    stage = END_DIALOGUE
                }
            }

            100 -> {
                player("Oh sorry, I forgot.")
                stage = END_DIALOGUE
            }

            200 -> {
                player(FaceAnim.NEUTRAL, "Thanks, maybe another time.")
                stage = END_DIALOGUE
            }

            300 -> {
                if (removeItem(player!!, Item(Items.COINS_995, 200))) {
                    patch.clear()
                    dialogue("The gardener obligingly removes your tree.")
                    stage = END_DIALOGUE
                } else {
                    dialogue("You need 200 gp to pay for that.")
                    stage = END_DIALOGUE
                }
            }

            400 -> {
                player("Really?")
                stage++
            }

            401 -> {
                npc(
                    "Yes, poison ivy is pretty hardy stuff, and most animals",
                    "will avoid eating it."
                )
                stage++
            }

            402 -> {
                npc("Hence, there is no reason to worry about it.")
                stage++
            }

            403 -> {
                player("Great.")
                stage = END_DIALOGUE
            }
        }
    }

    private fun getProtectionText(item: Item?): String? {
        if (item == null) return null
        val baseText = protectionMap.getString(item.id) ?: return item.name.lowercase()
        return if ("(s)" in baseText) {
            if (item.amount == 1) baseText.replace("(s)", "") else baseText.replace("(s)", "s")
        } else {
            baseText
        }
    }
}
