package content.global.skill.crafting.gem

import core.api.*
import core.game.dialogue.SkillDialogueHandler
import core.game.interaction.Clocks
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.entity.skill.SkillPulse
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.tools.RandomFunction.random
import shared.consts.Animations
import shared.consts.Items
import shared.consts.Sounds

class GemCutPlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles cutting gems using chisel.
         */

        onUseWith(IntType.ITEM, Items.CHISEL_1755, *UNCUT_GEMS) { player, used, with ->
            if (!clockReady(player, Clocks.SKILLING)) return@onUseWith true
            val gem = Gems.forId(if (used.id == Items.CHISEL_1755) with.asItem() else used.asItem()) ?: return@onUseWith true

            val handler = object : SkillDialogueHandler(player, SkillDialogue.ONE_OPTION, gem.gem) {
                override fun create(amount: Int, index: Int) {
                    val count = if (amount <= 0) 1 else amount
                    player.pulseManager.run(GemCutPulse(player, gem.uncut, count, gem))
                }

                override fun getAll(index: Int): Int = amountInInventory(player, gem.uncut.id)
            }

            val invAmount = amountInInventory(player, gem.uncut.id)
            if (invAmount == 1) handler.create(1, 0) else handler.open()

            return@onUseWith true
        }

        /*
         * Handles crushing semi-precious gems using a hammer.
         * Patch: 27 January 2009
         */

        onUseWith(IntType.ITEM, Items.HAMMER_2347, *SEMIPRECIOUS_GEMS) { player, used, with ->
            if (!clockReady(player, Clocks.SKILLING)) return@onUseWith true
            val gemId = if (used.id == Items.HAMMER_2347) with.id else used.id

            val handler = object : SkillDialogueHandler(player, SkillDialogue.ONE_OPTION, Item(gemId)) {
                override fun create(amount: Int, index: Int) {
                    val count = if (amount <= 0) 1 else amount
                    player.pulseManager.run(object : SkillPulse<Item?>(player, Item(gemId)) {
                        private var remaining = count

                        override fun checkRequirements(): Boolean {
                            if (!inInventory(player, gemId)) return false
                            /*
                            if (!hasSpaceFor(player, Item(Items.CRUSHED_GEM_1633))) {
                                sendDialogue(player, "You do not have enough inventory space.")
                                return false
                            }
                            */
                            return true
                        }

                        override fun animate() {
                            animate(player, Animations.USE_HAMMER_CHISEL_11041)
                        }

                        override fun reward(): Boolean {
                            val removed = removeItem(player, Item(gemId))
                            if (removed) {
                                addItem(player, Items.CRUSHED_GEM_1633)
                                sendMessage(player, "You deliberately crush the gem with the hammer.")
                            }
                            remaining--
                            return remaining <= 0
                        }
                    })
                }

                override fun getAll(index: Int): Int = amountInInventory(player, gemId)
            }

            val invAmount = amountInInventory(player, gemId)
            if (invAmount == 1) handler.create(1, 0) else handler.open()

            return@onUseWith true
        }
    }

    companion object {
        private val UNCUT_GEMS = intArrayOf(
            Gems.OPAL.uncut.id,
            Gems.JADE.uncut.id,
            Gems.RED_TOPAZ.uncut.id,
            Gems.SAPPHIRE.uncut.id,
            Gems.EMERALD.uncut.id,
            Gems.RUBY.uncut.id,
            Gems.DIAMOND.uncut.id,
            Gems.DRAGONSTONE.uncut.id,
            Gems.ONYX.uncut.id,
        )
        private val SEMIPRECIOUS_GEMS = intArrayOf(
            Gems.OPAL.uncut.id,
            Gems.JADE.uncut.id,
            Gems.RED_TOPAZ.uncut.id
        )
    }
}

/**
 * Handles pulse used to cut a gem.
 */
private class GemCutPulse(player: Player?, item: Item?, var amount: Int, val gem: Gems) : SkillPulse<Item?>(player, item) {
    private var ticks = 0

    init {
        resetAnimation = false
    }

    override fun checkRequirements(): Boolean {
        val craftingLevel = getStatLevel(player, Skills.CRAFTING)
        if (craftingLevel < gem.level) {
            sendMessage(player, "You need a Crafting level of ${gem.level} to craft this gem.")
            return false
        }

        if (!inInventory(player, CHISEL)) return false
        if (!inInventory(player, gem.uncut.id)) return false
        /*
        if (!hasSpaceFor(player, gem.gem)) {
            sendDialogue(player, "You do not have enough inventory space.")
            return false
        }
        */
        return true
    }

    override fun animate() {
        ticks++
        if (ticks % 5 == 0 || ticks < 2) {
            animate(player, gem.animation)
            playAudio(player, Sounds.CHISEL_2586)
        }
    }

    override fun reward(): Boolean {
        val removed = removeItem(player, gem.uncut)
        if (!removed) return false

        val craftingLevel = getStatLevel(player, Skills.CRAFTING)
        val crushedGem: Item? = when (gem.uncut.id) {
            Items.UNCUT_OPAL_1625 -> if (random(100) < getGemCrushChance(7.42, 0.0, craftingLevel)) Item(Items.CRUSHED_GEM_1633) else null
            Items.UNCUT_JADE_1627 -> if (random(100) < getGemCrushChance(9.66, 0.0, craftingLevel)) Item(Items.CRUSHED_GEM_1633) else null
            Items.UNCUT_RED_TOPAZ_1629 -> if (random(100) < getGemCrushChance(9.2, 0.0, craftingLevel)) Item(Items.CRUSHED_GEM_1633) else null
            else -> null
        }

        if (crushedGem != null) {
            addItem(player, crushedGem.id)
            rewardXP(player, Skills.CRAFTING, when (gem.uncut.id) {
                Items.UNCUT_OPAL_1625 -> 3.8
                Items.UNCUT_RED_TOPAZ_1629 -> 6.3
                else -> 5.0
            })
            sendMessage(player, "You mis-hit the chisel and smash the ${getItemName(gem.gem.id)} to pieces!")
        } else {
            addItem(player, gem.gem.id)
            rewardXP(player, Skills.CRAFTING, gem.exp)
            sendMessage(player, "You cut the ${getItemName(gem.gem.id)}.")
        }

        amount--
        return amount < 1
    }

    companion object {
        private const val CHISEL = Items.CHISEL_1755
    }

    private fun getGemCrushChance(low: Double, high: Double, level: Int): Double {
        if (level >= 50) return 0.0
        val clamped = level.coerceIn(1, 49)
        val chance = low * ((50 - clamped) / 49.0) + high * ((clamped - 1) / 49.0)
        return chance.coerceIn(0.0, 100.0)
    }
}
