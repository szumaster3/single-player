package content.global.activity.ttrail.dialogue

import content.global.activity.ttrail.ClueScroll
import content.global.activity.ttrail.TreasureTrailManager
import content.global.activity.ttrail.scroll.AnagramScroll
import content.global.activity.ttrail.scroll.ChallengeClueScroll
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import shared.consts.Items
import shared.consts.NPCs

class ChallengeDialogue(npc: NPC?, private val clue: ChallengeClueScroll) : DialogueFile() {

    private val facialExpression: FaceAnim = when (npc?.id) {
        NPCs.UGLUG_NAR_2039,
        NPCs.GNOME_COACH_2802,
        NPCs.GNOME_BALL_REFEREE_635,
        NPCs.GNOME_TRAINER_162 -> FaceAnim.OLD_DEFAULT
        else -> FaceAnim.HALF_ASKING
    }

    override fun handle(componentID: Int, buttonID: Int) {
        val p = player ?: return end()
        val n = npc ?: return end()

        val manager = TreasureTrailManager.getInstance(p)
        val anagramClue = AnagramScroll.getClue(p, n)

        when (stage) {
            0 -> {
                npc(facialExpression, "Please enter the answer to the question.")
                stage = 1
            }

            1 -> {
                sendInputDialogue(p, numeric = true, prompt = "Enter your answer") { input ->
                    val userAnswer = input as? Int
                    if (userAnswer == null || userAnswer != clue.answer) {
                        npc(facialExpression, "That isn't right, keep trying.")
                        end()
                        return@sendInputDialogue
                    }

                    if (freeSlots(p) == 0) {
                        npc(facialExpression, "Your inventory is full, make some room first.")
                        end()
                        return@sendInputDialogue
                    }
                    val rand = arrayOf("Here is your reward!", "Spot on!").random()

                    npc(facialExpression, rand)
                    removeItem(p, clue.clueId)
                    anagramClue?.let { removeItem(p, it.clueId) }

                    removeAttributes(p, "anagram_clue_active")
                    clue.reward(p)

                    stage = 2
                }
            }

            2 -> {
                end()
                if (manager.isCompleted) {
                    sendItemDialogue(p, Items.CASKET_405, "You've found a casket!")
                    manager.clearTrail()
                } else {
                    val newClue = clue.level?.let { ClueScroll.getClue(it) }
                    if (newClue != null) {
                        sendItemDialogue(p, newClue, "You receive another clue scroll.")
                        addItem(p, newClue.id, 1)
                    }
                }
            }
        }
    }
}