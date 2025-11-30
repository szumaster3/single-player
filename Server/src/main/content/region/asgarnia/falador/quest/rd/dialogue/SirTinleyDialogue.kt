package content.region.asgarnia.falador.quest.rd.dialogue

import content.data.GameAttributes
import content.region.asgarnia.falador.quest.rd.RecruitmentDrive
import content.region.asgarnia.falador.quest.rd.cutscene.FailTestCutscene
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.system.task.Pulse
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.tools.END_DIALOGUE
import shared.consts.NPCs

class SirTinleyDialogue(private val dialogueNum: Int = 0) : DialogueFile(), MapArea {

    override fun handle(componentID: Int, buttonID: Int) {
        val player = player ?: return
        when (dialogueNum) {
            0 -> when {
                isInitialDialogue(player) -> {
                    npc("Ah, welcome ${player.username}", "I have but one clue for you to pass this room's puzzle:", "'Patience'.")
                    stage = 10
                }
                isFailDialogue(player) -> {
                    setAttribute(player, RecruitmentDrive.stageFail, true)
                    npc(FaceAnim.SAD, "No... I am very sorry.", "Apparently you are not up to the challenge.", "I will return you where you came from, better luck in the", "future.")
                    stage = 20
                }
            }
            1 -> {
                npc("Ah, ${player.username}, you have arrived.", "Speak to me to begin your task.")
                setAttribute(player, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE, false)
                stage = END_DIALOGUE
            }
            2 -> {
                npc(FaceAnim.SAD, "No... I am very sorry.", "Apparently you are not up to the challenge.", "I will return you where you came from, better luck in the", "future.")
                handleFailStage(player)
                stage = END_DIALOGUE
            }
        }

        when (stage) {
            10 -> handlePatienceDialogue(player)
            20 -> handleFailStage(player)
        }
    }

    private fun isInitialDialogue(player: Player): Boolean =
        dialogueNum == 0 &&
                !getAttribute(player, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE, false) &&
                !getAttribute(player, RecruitmentDrive.stageFail, false)

    private fun isFailDialogue(player: Player): Boolean =
        dialogueNum == 0 &&
                (getAttribute(player, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE, false) ||
                        getAttribute(player, RecruitmentDrive.stageFail, false) ||
                        dialogueNum == 2)

    private fun handlePatienceDialogue(player: Player) {
        setAttribute(player, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE, true)
        submitWorldPulse(object : Pulse() {
            private var counter = 0
            override fun pulse(): Boolean {
                if (counter++ == 15) {
                    if (!getAttribute(player, RecruitmentDrive.stageFail, false)) {
                        setAttribute(player, RecruitmentDrive.stagePass, true)
                        setAttribute(player, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE, false)
                        npc(FaceAnim.HAPPY, "Excellent work, ${player.username}", "Please step through the portal to meet your next", "challenge.")
                    }
                }
                return false
            }
        })
    }

    private fun handleFailStage(player: Player) {
        removeAttribute(player, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE)
        setAttribute(player, RecruitmentDrive.stagePass, false)
        setAttribute(player, RecruitmentDrive.stageFail, false)
        runTask(player, 3) { FailTestCutscene(player).start() }
    }

    override fun defineAreaBorders(): Array<ZoneBorders> = arrayOf(ZoneBorders(2474, 4959, 2478, 4957))

    override fun entityStep(entity: Entity, location: Location, lastLocation: Location) {
        if (entity is Player && getAttribute(entity, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE, false)) {
            setAttribute(entity, GameAttributes.RECRUITMENT_DRIVE_TEST_OF_PATIENCE, false)
            setAttribute(entity, RecruitmentDrive.stageFail, true)
            openDialogue(entity, SirTinleyDialogue(2), NPC(NPCs.SIR_TINLEY_2286))
        }
    }
}
