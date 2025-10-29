package content.global.skill.crafting.glass

import content.global.skill.crafting.CraftingObjects
import core.api.*
import core.game.event.ResourceProducedEvent
import core.game.interaction.Clocks
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.system.task.Pulse
import shared.consts.Animations
import shared.consts.Items
import kotlin.math.min

class MoltenGlassMakePlugin : InteractionListener {

    companion object {
        const val SODA_ASH = Items.SODA_ASH_1781
        const val BUCKET_OF_SAND = Items.BUCKET_OF_SAND_1783
        const val SANDBAG = Items.SANDBAG_9943
        const val MOLTEN_GLASS = Items.MOLTEN_GLASS_1775
        val INPUTS = intArrayOf(SODA_ASH, BUCKET_OF_SAND, SANDBAG)
        val SAND_SOURCES = intArrayOf(BUCKET_OF_SAND, SANDBAG)
    }

    override fun defineListeners() {
        onUseWith(IntType.SCENERY, INPUTS, *CraftingObjects.FURNACES) { player, _, _ ->
            if (!clockReady(player, Clocks.SKILLING)) return@onUseWith true

            if (!inInventory(player, SODA_ASH, 1)) {
                sendMessage(player, "You need at least one heap of soda ash to do this.")
                return@onUseWith true
            }

            var hasSand = false
            for (sandId in SAND_SOURCES) {
                if (inInventory(player, sandId)) {
                    hasSand = true
                    break
                }
            }

            if (!hasSand) {
                sendMessage(player, "You need at least one bucket of sand or sandbag to do this.")
                return@onUseWith true
            }

            sendSkillDialogue(player) {
                withItems(MOLTEN_GLASS)
                create { id, amount ->
                    delayClock(player, Clocks.SKILLING, 3)
                    submitIndividualPulse(player, GlassMakePulse(player, id, amount))
                }
                calculateMaxAmount { _ ->
                    min(amountInInventory(player, SODA_ASH), SAND_SOURCES.sumOf { amountInInventory(player, it) })
                }
            }

            return@onUseWith true
        }
    }
}

/**
 * Handles crafting molten glass.
 */
private class GlassMakePulse(private val player: Player, val product: Int, private var amount: Int) : Pulse() {

    private val SAND_SOURCES = MoltenGlassMakePlugin.SAND_SOURCES

    override fun pulse(): Boolean {
        if (amount < 1) return true

        if (!inInventory(player, MoltenGlassMakePlugin.SODA_ASH) || !anyInInventory(player, *SAND_SOURCES)) {
            return true
        }

        lock(player, 3)
        animate(player, Animations.HUMAN_FURNACE_SMELT_3243)
        sendMessage(player, "You heat the sand and soda ash in the furnace to make glass.")

        if (removeItem(player, MoltenGlassMakePlugin.SODA_ASH) && removeSand(player)) {
            addItem(player, MoltenGlassMakePlugin.MOLTEN_GLASS)
            rewardXP(player, Skills.CRAFTING, 20.0)
            player.dispatch(ResourceProducedEvent(product, amount, player))
        } else {
            return true
        }

        amount--
        delay = 3
        return false
    }

    /**
     * Removes one sand source from the inventory.
     */
    private fun removeSand(player: Player): Boolean {
        return when {
            inInventory(player, MoltenGlassMakePlugin.BUCKET_OF_SAND) -> {
                if (removeItem(player, MoltenGlassMakePlugin.BUCKET_OF_SAND)) {
                    addItem(player, Items.BUCKET_1925)
                    true
                } else false
            }
            inInventory(player, MoltenGlassMakePlugin.SANDBAG) -> {
                if (removeItem(player, MoltenGlassMakePlugin.SANDBAG)) {
                    addItem(player, Items.EMPTY_SACK_5418)
                    true
                } else false
            }
            else -> false
        }
    }
}