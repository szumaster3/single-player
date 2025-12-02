package content.global.skill.thieving

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.combat.DeathTask
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.impl.Animator
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.entity.skill.Skills
import core.game.node.scenery.Scenery
import core.game.world.map.zone.ZoneBorders
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction
import shared.consts.Animations
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Sounds

class ThievingOptionPlugin : InteractionListener {
    override fun defineListeners() {

        /*
         * Handles thieving scenery options.
         */

        on(IntType.SCENERY, "steal-from", "steal from", "steal") { player, node ->
            val scenery = node as? Scenery ?: return@on true
            val stall = ThievingDefinition.Stall.values().firstOrNull { scenery.id in it.fullIDs } ?: return@on true
            ThievingDefinition.Stall.handleSteal(player, scenery, stall)
            lockInteractions(player, 6)
            return@on true
        }

        /*
         * Handles clothes stall interaction in Keldagrim.
         */

        on(shared.consts.Scenery.CLOTHES_STALL_6165, IntType.SCENERY, "steal-from") { player, _ ->
            sendDialogue(player, "You don't really see anything you'd want to steal from this stall.")
            return@on true
        }

        /*
         * Handles opening thieving chests.
         */

        on(ThievingDefinition.Chests.OBJECT_IDS, IntType.SCENERY, "open") { player, node ->
            ThievingDefinition.Chests.forId(node.id)?.open(player, node as Scenery)
            return@on true
        }

        /*
         * Handles searching for traps.
         */

        on(ThievingDefinition.Chests.OBJECT_IDS, IntType.SCENERY, "search for traps") { player, node ->
            ThievingDefinition.Chests.forId(node.id)?.searchTraps(player, node as Scenery)
            return@on true
        }

        /*
         * Handles pickpocket NPCs.
         */

        on(IntType.NPC, "pickpocket", "pick-pocket") { player, node ->
            val pocketData = ThievingDefinition.Pickpocket.forID(node.id) ?: return@on false
            val npc = node.asNpc()
            val npcName = npc.name.lowercase()
            val cabinetKey = hasAnItem(player, Items.DISPLAY_CABINET_KEY_4617).container != null

            if (player.inCombat()) {
                sendMessage(player, "You can't do this while in combat.")
                return@on true
            }

            if (pocketData == null) {
                sendMessage(player, "You cannot pickpocket that NPC.")
                return@on true
            }

            if (getStatLevel(player, Skills.THIEVING) < pocketData.requiredLevel) {
                sendMessage(player, "You need a Thieving level of ${pocketData.requiredLevel} to do that.")
                return@on true
            }

            if (DeathTask.isDead(npc)) {
                sendMessage(player, "Too late, $npcName is already dead.")
                return@on true
            }

            if (npc.id == NPCs.CURATOR_HAIG_HALEN_646 && cabinetKey) {
                sendMessage(player, "You have no reason to do that.")
                return@on true
            }

            if (!pocketData.table.canRoll(player)) {
                sendMessage(player, "You don't have enough inventory space to do that.")
                return@on true
            }

            animate(player, PICKPOCKET_ANIM)
            sendMessage(player, "You attempt to pick the $npcName pocket.")
            val lootTable = ThievingDefinition.pickpocketRoll(player, pocketData.low, pocketData.high, pocketData.table)

            if (lootTable == null) {
                npc.face(player)
                npc.animator.animate(NPC_ANIM)
                npc.sendChat(pocketData.message)
                sendMessage(player, "You fail to pick the $npcName pocket.")

                playHurtAudio(player, 20)
                stun(player, pocketData.stunTime)
                impact(player, RandomFunction.random(pocketData.stunDamageMin, pocketData.stunDamageMax), ImpactHandler.HitsplatType.NORMAL)
                sendMessage(player, "You feel slightly concussed from the blow.")
                npc.face(null)
            } else {
                lock(player, 2)
                playAudio(player, Sounds.PICK_2581)
                lootTable.forEach {
                    player.inventory.add(it)
                }

                if (getStatLevel(player, Skills.THIEVING) >= 40) {
                    when {
                        inBorders(player, ZoneBorders(3201, 3456, 3227, 3468)) && npc.id == NPCs.GUARD_5920 -> {
                            finishDiaryTask(player, DiaryType.VARROCK, 1, 12)
                        }
                        inBorders(player, ZoneBorders(2934, 3399, 3399, 3307)) && npc.id in intArrayOf(NPCs.GUARD_9, NPCs.GUARD_3230, NPCs.GUARD_3228, NPCs.GUARD_3229) -> {
                            finishDiaryTask(player, DiaryType.FALADOR, 1, 6)
                        }
                    }
                }

                sendMessage(player, if (npc.id == NPCs.CURATOR_HAIG_HALEN_646) { "You steal a tiny key." } else { "You pick the $npcName's pocket." })
                rewardXP(player, Skills.THIEVING, pocketData.experience)
            }
            return@on true
        }

        /*
         * Handles opening locked doors.
         */

        on(ThievingDefinition.Doors.DOOR_IDS, IntType.SCENERY, "open", "pick-lock") { player, node ->
            val option = getUsedOption(player)
            val door = ThievingDefinition.Doors.forLocation(node.location)

            when (option) {
                "open" -> {
                    if (door == null) {
                        sendMessage(player, "The door is locked.")
                    } else {
                        door.open(player, node as Scenery)
                    }
                    return@on true
                }
                "pick-lock" -> {
                    if (door == null) {
                        sendMessage(player, "This door cannot be unlocked.")
                    } else {
                        door.pickLock(player, node as Scenery)
                    }
                    return@on true
                }
                else -> return@on false
            }
        }
    }

    companion object {
        val PICKPOCKET_ANIM = Animation(Animations.HUMAN_PICKPOCKETING_881, Animator.Priority.HIGH)
        val NPC_ANIM = Animation(Animations.PUNCH_422)
    }
}
