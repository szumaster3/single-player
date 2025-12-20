package content.global.skill.gather.fishing

import content.region.kandarin.baxtorian.BarbarianTraining
import core.api.*
import core.game.interaction.Clocks
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction
import core.tools.RandomUtils
import shared.consts.Animations
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Scenery

class BarbFishingListener : InteractionListener {

    override fun defineListeners() {

        /*
         * Heavy rod pickup (training start)
         */
        on(Scenery.BARBARIAN_BED_25268, IntType.SCENERY, "search") { player, _ ->
            if (
                getAttribute(player, BarbarianTraining.FISHING_BASE, false) &&
                !inInventory(player, Items.BARBARIAN_ROD_11323) &&
                freeSlots(player) > 0
            ) {
                sendMessage(player, "You find a heavy fishing rod under the bed and take it.")
                addItem(player, Items.BARBARIAN_ROD_11323, 1)
            } else {
                sendMessage(player, "You don't find anything that interests you.")
            }
            return@on true
        }

        /*
         * Barbarian fishing spot
         */
        onUseWith(IntType.ITEM, Items.BARBARIAN_ROD_11323, NPCs.FISHING_SPOT_1176) { player, _, _ ->

            val fishing = getStatLevel(player, Skills.FISHING)
            val agility = getStatLevel(player, Skills.AGILITY)
            val strength = getStatLevel(player, Skills.STRENGTH)

            if (fishing < 48) {
                sendMessage(player, "You need a Fishing level of at least 48 to fish here.")
                return@onUseWith true
            }

            if (agility < 15 || strength < 15) {
                val stat = when {
                    agility < 15 && strength < 15 -> "agility and strength"
                    agility < 15 -> "agility"
                    else -> "strength"
                }
                sendMessage(player, "You need a $stat level of at least 15 to fish here.")
                return@onUseWith true
            }

            if (!anyInInventory(
                    player,
                    Items.FISHING_BAIT_313,
                    Items.FEATHER_314,
                    Items.ROE_11324,
                    Items.FISH_OFFCUTS_11334,
                    Items.CAVIAR_11326
                )
            ) {
                sendMessage(player, "You don't have any bait to fish with.")
                return@onUseWith true
            }

            if (player.inventory.isFull) {
                sendMessage(player, "You don't have enough space in your inventory.")
                return@onUseWith true
            }

            val anim = Animation(Animations.ROD_FISHING_622)

            queueScript(player, 1, QueueStrength.WEAK) {

                if (!clockReady(player, Clocks.SKILLING)) {
                    return@queueScript keepRunning(player)
                }

                if (player.inventory.isFull) {
                    return@queueScript clearScripts(player)
                }

                player.animate(anim)

                val fish = getRandomFish(player)
                val success = rollSuccess(player, fish.id)

                if (success) {
                    removeItem(player, Items.FISH_OFFCUTS_11334) ||
                            removeItem(player, Items.FEATHER_314)

                    addItem(player, fish.id)

                    val fishXP = when (fish.id) {
                        Items.LEAPING_TROUT_11328 -> 50.0
                        Items.LEAPING_SALMON_11330 -> 70.0
                        Items.LEAPING_STURGEON_11332 -> 80.0
                        else -> 0.0
                    }

                    val strAgiXP = when (fish.id) {
                        Items.LEAPING_TROUT_11328 -> 5.0
                        Items.LEAPING_SALMON_11330 -> 6.0
                        Items.LEAPING_STURGEON_11332 -> 7.0
                        else -> 0.0
                    }

                    rewardXP(player, Skills.FISHING, fishXP)
                    rewardXP(player, Skills.AGILITY, strAgiXP)
                    rewardXP(player, Skills.STRENGTH, strAgiXP)

                    sendMessage(player, "You catch a ${fish.name.lowercase()}.")

                    if (!getAttribute(player, BarbarianTraining.FISHING_BASE, false)) {
                        sendDialogueLines(
                            player,
                            "You feel you have learned more of barbarian ways. Otto might wish",
                            "to talk to you more."
                        )
                        setAttribute(player, BarbarianTraining.FISHING_BASE, true)
                    }
                } else {
                    sendMessage(player, "You fail to catch any fish.")
                }

                delayClock(player, Clocks.SKILLING, 2)
                keepRunning(player)
            }

            return@onUseWith true
        }

        /*
         * Handles cutting leaping fish.
         */

        onUseWith(
            IntType.ITEM,
            Items.KNIFE_946,
            Items.LEAPING_TROUT_11328,
            Items.LEAPING_SALMON_11330,
            Items.LEAPING_STURGEON_11332
        ) { player, _, fishItem ->

            val fishId = fishItem.id
            val level = getStatLevel(player, Skills.COOKING)

            if (level < 1) {
                sendDialogue(player, "You need a Cooking level to attempt cutting this fish.")
                return@onUseWith true
            }

            val slots = freeSlots(player)
            val hasOffcuts = inInventory(player, Items.FISH_OFFCUTS_11334)
            if (slots < 2 && (slots < 1 || !hasOffcuts)) {
                sendMessage(player, "You don't have enough space in your pack to attempt cutting open the fish.")
                return@onUseWith true
            }

            val anim = Animation(Animations.OFFCUTS_6702)

            queueScript(player, 1, QueueStrength.WEAK) {

                if (!clockReady(player, Clocks.SKILLING)) {
                    return@queueScript keepRunning(player)
                }

                if (amountInInventory(player, fishId) <= 0) {
                    return@queueScript clearScripts(player)
                }

                player.animate(anim)
                removeItem(player, fishId)

                val success = rollSuccess(fishId, level)
                if (success) {
                    val product = when (fishId) {
                        Items.LEAPING_TROUT_11328,
                        Items.LEAPING_SALMON_11330 -> Items.ROE_11324
                        Items.LEAPING_STURGEON_11332 -> Items.CAVIAR_11326
                        else -> -1
                    }

                    if (product != -1) addItem(player, product)
                    if (rollOffcuts(fishId)) addItem(player, Items.FISH_OFFCUTS_11334)

                    val xp = when (fishId) {
                        Items.LEAPING_TROUT_11328,
                        Items.LEAPING_SALMON_11330 -> 10.0
                        Items.LEAPING_STURGEON_11332 -> 15.0
                        else -> 0.0
                    }

                    rewardXP(player, Skills.COOKING, xp)
                    sendMessage(player, "You cut open the fish and extract some roe, but the rest is discarded.")
                } else {
                    sendMessage(player, "You fail to cut the fish properly and ruin it.")
                }

                delayClock(player, Clocks.SKILLING, 2)
                keepRunning(player)
            }

            return@onUseWith true
        }
    }

    private fun rollSuccess(fish: Int, level: Int): Boolean =
        when (fish) {
            Items.LEAPING_TROUT_11328 ->
                RandomUtils.randomDouble() < (level.coerceAtMost(99) / 150.0)

            Items.LEAPING_SALMON_11330,
            Items.LEAPING_STURGEON_11332 ->
                RandomUtils.randomDouble() < (level.coerceAtMost(80) / 80.0)

            else -> true
        }

    private fun rollOffcuts(fish: Int): Boolean =
        when (fish) {
            Items.LEAPING_TROUT_11328 -> RandomUtils.randomDouble() < 0.5
            Items.LEAPING_SALMON_11330 -> RandomUtils.randomDouble() < 0.75
            Items.LEAPING_STURGEON_11332 -> RandomUtils.randomDouble() < (5.0 / 6.0)
            else -> false
        }

    private fun rollSuccess(player: Player, fish: Int): Boolean {
        val level = 1 + player.skills.getLevel(Skills.FISHING) +
                player.familiarManager.getBoost(Skills.FISHING)
        val host = Math.random() * fish
        val client = Math.random() * (level * 3.0 - fish)
        return host < client
    }

    private fun getRandomFish(player: Player): Item {
        val fish = arrayOf(
            Items.LEAPING_TROUT_11328,
            Items.LEAPING_SALMON_11330,
            Items.LEAPING_STURGEON_11332
        )

        var max = 0
        if (player.skills.getLevel(Skills.FISHING) >= 58 &&
            player.skills.getLevel(Skills.STRENGTH) >= 30 &&
            player.skills.getLevel(Skills.AGILITY) >= 30
        ) max++

        if (player.skills.getLevel(Skills.FISHING) >= 70 &&
            player.skills.getLevel(Skills.STRENGTH) >= 45 &&
            player.skills.getLevel(Skills.AGILITY) >= 45
        ) max++

        return Item(fish[RandomFunction.random(max + 1)])
    }
}
