package content.region.asgarnia.falador.quest.rd.cutscene

import content.region.asgarnia.falador.quest.rd.plugin.RecruitmentDrivePlugin
import core.api.*
import core.game.activity.Cutscene
import core.game.dialogue.FaceAnim
import core.game.interaction.QueueStrength
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.world.map.Location
import shared.consts.NPCs

class FailTestCutscene(player: Player) : Cutscene(player) {
    override fun setup() {
        setExit(Location(2996, 3375))
    }

    override fun runStage(stage: Int) {
        when (stage) {
            0 -> {
                player.lock()
                fadeToBlack()
                setMinimapState(player, 2)
                timedUpdate(6)
            }

            1 -> {
                var clearBoss = getAttribute(player, RecruitmentDrivePlugin.SirLeyeNPC.init(player).toString(), NPC(0))
                if (clearBoss.id != 0) {
                    clearBoss.clear()
                }
                clearInventory(player)
                queueScript(player, 1, QueueStrength.SOFT) { stage: Int ->
                    when (stage) {
                        0 -> {
                            fadeFromBlack()
                            return@queueScript delayScript(player, 2)
                        }

                        1 -> {
                            sendNPCDialogue(player, NPCs.SIR_TIFFY_CASHIEN_2290, "Oh, jolly bad luck, what? Not quite the brainbox you thought you were, eh?", FaceAnim.SAD)
                                addDialogueAction(player) { _, button ->
                                    if(button > 0)
                                        sendNPCDialogue(player, NPCs.SIR_TIFFY_CASHIEN_2290, "Well, never mind! You have an open invitation to join our organization, so when you're feeling a little smarter, come back and talk to me again.", FaceAnim.HAPPY)
                                }


                            return@queueScript stopExecuting(player)
                        }


                        else -> return@queueScript stopExecuting(player)
                    }
                }

                endWithoutFade {
                    face(player, findLocalNPC(player, NPCs.SIR_TIFFY_CASHIEN_2290)!!)
                    fadeFromBlack()
                    player.unlock()
                    player.interfaceManager.restoreTabs()
                    player.interfaceManager.openDefaultTabs()
                }
            }
        }
    }
}
