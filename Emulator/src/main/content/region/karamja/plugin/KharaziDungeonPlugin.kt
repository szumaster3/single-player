package content.region.karamja.plugin

import content.data.items.SkillingTool
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.interaction.Clocks
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.TeleportManager
import core.game.node.entity.skill.Skills
import core.game.world.map.Location
import shared.consts.Animations
import shared.consts.Items
import shared.consts.Scenery

class KharaziDungeonPlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles enter to the Kharazi dungeon.
         */

        on(Scenery.MOSSY_ROCK_24300, IntType.SCENERY, "search") { player, _ ->
            sendMessage(player, "You try to crawl through...")
            player.dialogueInterpreter.sendDialogue("You see that there is a small crevice that you may be able to crawl", "through.")
            addDialogueAction(player) { p, button ->
                sendDialogueOptions(p, "Crawl though hole?", "Yes, I'll crawl through. I'm very athletic.", "No, I'm a bit claustrophobic.")
                if (button == 2) {
                    teleport(p, Location.create(2772, 9341, 0), TeleportManager.TeleportType.INSTANT)
                    sendMessage(p, "You contort your body to fit the crevice.")
                    sendMessage(p, "You adroitly squeeze, serpent-like, into the crevice.", 1)
                    sendMessage(p, "You find a small narrow tunnel that goes for some distance.", 1)
                    sendMessage(p, "After some time, you find a small cave opening... and walk through.", 2)
                }
            }
            return@on true
        }

        /*
         * Handles shortcut via bookcase near old gate.
         */

        on(Scenery.BOOKCASE_2911, IntType.SCENERY, "search", "look") { player, n ->
            val option = getUsedOption(player)
            when (option) {
                "look" -> sendDialogue(player, "This bamboo book shelf doesn't house many books, it doesn't look as if the Shaman has much opportunity to read at the moment in any case.")
                else -> openDialogue(player, NorthEasternBookcase())
            }
            return@on true
        }

        on(Scenery.CREVICE_2918, IntType.SCENERY, "look-at") { player, n ->
            val option = getUsedOption(player)
            when (option) {
                "look" -> sendDialogue(player, "This bamboo book shelf doesn't house many books, it doesn't look as if the Shaman has much opportunity to read at the moment in any case.")
                else -> openDialogue(player, NorthEasternBookcase())
            }
            return@on true
        }

        /*
         * Handles mining the boulders to get to the death wing spawn.
         */

        on(intArrayOf(Scenery.BOULDER_2919, Scenery.BOULDER_2920, Scenery.BOULDER_2921), IntType.SCENERY, "smash-to-bits") { player, boulder ->
            val pickaxe = SkillingTool.getPickaxe(player)

            if (pickaxe == null) {
                sendMessage(player, "You do not have a pickaxe to use.")
                return@on true
            }

            if (!finishedMoving(player) || !clockReady(player, Clocks.SKILLING)) return@on true

            animate(player, pickaxe.animation)
            lock(player, 7)

            queueScript(player, 1, QueueStrength.SOFT) { stage: Int ->
                when (stage) {
                    0 -> {
                        sendMessage(player, "You swing your pickaxe at the rock.")
                        return@queueScript delayScript(player, 2)
                    }

                    1 -> {
                        if (!checkReward(player, pickaxe)) {
                            sendMessage(player, "You only scratch the rock.")
                            delayClock(player, Clocks.SKILLING, 3)
                            return@queueScript restartScript(player)
                        }

                        replaceScenery(boulder.asScenery(), 2918, 5)
                        sendMessage(player, "You smash the rock to bits.")
                        addItem(player, Items.ROCK_1480, 1)
                        resetAnimator(player)
                        return@queueScript delayScript(player, 2)
                    }

                    2 -> {
                        val target = if (player.location.y > boulder.location.y)
                            boulder.location.transform(0, -2, 0)
                        else
                            boulder.location.transform(0, 2, 0)

                        forceMove(player, boulder.location, target, 30, 120, null, Animations.WALK_819)
                        sendMessage(player, "Another boulder drops down behind you.", 1)
                        delayClock(player, Clocks.SKILLING, 3)
                        return@queueScript delayScript(player, 2)
                    }

                    else -> return@queueScript stopExecuting(player)
                }
            }

            return@on true
        }

        on(Scenery.CREVICE_2918, IntType.SCENERY, "look-at", "search") { player, _ ->
            teleport(player, Location.create(2794, 9339, 0), TeleportManager.TeleportType.INSTANT)
            return@on true
        }
    }

    override fun defineDestinationOverrides() {
        setDest(IntType.SCENERY, Scenery.CREVICE_2918) { _, _ ->
            return@setDest Location.create(2800, 9340, 0)
        }
    }

    inner class NorthEasternBookcase : DialogueFile() {
        override fun handle(componentID: Int, buttonID: Int) {
            when (stage) {
                0 -> player!!.dialogueInterpreter.sendDialogue("After perusing the empty bookcase for a while, you decide to check", "behind it. There's a crevice in the wall! You might just be able to", "force your way through if you were in any way athletic.")
                1 -> sendDialogueOptions(player!!, "Would you like to squeeze into this crevice?", "Yes please!", "No thanks!")
                2 -> when (buttonID) {
                    1 -> {
                        teleport(player!!, Location.create(2800, 9340, 0), TeleportManager.TeleportType.INSTANT)
                        sendMessage(player!!, "You successfully squeeze through the crevice into a small tunnel.")
                    }
                    2 -> sendMessage(player!!, "You decide not to squeeze yourself into that ridiculously small crevice.")
                }
            }
        }
    }

    private fun checkReward(player: Player, tool: SkillingTool): Boolean {
        val level = 1 + getDynLevel(player, Skills.MINING) + getFamiliarBoost(player, Skills.MINING)
        val hostRatio = Math.random() * (100.0 * 0.1)
        var toolRatio = tool.ratio
        val clientRatio = Math.random() * ((level - 1) * (1.0 + toolRatio))
        return hostRatio < clientRatio
    }

}