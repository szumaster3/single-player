package content.global.skill.crafting

import core.api.*
import core.game.dialogue.SkillDialogueHandler
import core.game.interaction.Clocks
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.map.Location
import shared.consts.Animations
import shared.consts.Components
import shared.consts.Items
import shared.consts.Sounds

class WeavingPlugin : InteractionListener {

    /**
     * Represents weaving items.
     */
    enum class Weaving(val product: Item, val required: Item, val level: Int, val experience: Double) {
        SACK(Item(Items.EMPTY_SACK_5418), Item(Items.JUTE_FIBRE_5931, 4), 21, 38.0),
        BASKET(Item(Items.BASKET_5376), Item(Items.WILLOW_BRANCH_5933, 6), 36, 56.0),
        CLOTH(Item(Items.STRIP_OF_CLOTH_3224), Item(Items.BALL_OF_WOOL_1759, 4), 10, 12.0),
    }

    override fun defineListeners() {

        /*
         * Handles interaction with weaves.
         */

        on(IntType.SCENERY, "weave") { player, node ->
            object : SkillDialogueHandler(
                player,
                SkillDialogue.THREE_OPTION,
                Weaving.SACK.product,
                Weaving.BASKET.product,
                Weaving.CLOTH.product
            ) {
                override fun create(amount: Int, index: Int) {

                    val type = Weaving.values()[index]
                    val required = type.required
                    val product = type.product

                    queueScript(player, 0, QueueStrength.WEAK) {

                        var remaining = amount

                        if (getStatLevel(player, Skills.CRAFTING) < type.level) {
                            sendMessage(player, "You need a crafting level of at least ${type.level} to do this.")
                            return@queueScript stopExecuting(player)
                        }

                        if (!inInventory(player, required.id, required.amount)) {
                            val reqName = type.required.name.lowercase()
                            val suffix = when (type) {
                                Weaving.SACK -> "s"
                                Weaving.CLOTH -> ""
                                else -> "es"
                            }
                            val reqLower = reqName.replace("ball", "balls")
                            val article = if (reqLower.startsWith("a") || reqLower.startsWith("e") ||
                                reqLower.startsWith("i") || reqLower.startsWith("o") ||
                                reqLower.startsWith("u")
                            ) "an" else "a"

                            sendMessage(player,
                                "You need ${required.amount} ${reqLower}${suffix} to weave $article ${product.name.lowercase()}."
                            )
                            return@queueScript stopExecuting(player)
                        }


                        if (remaining <= 0 || !clockReady(player, Clocks.SKILLING))
                            return@queueScript stopExecuting(player)

                        animate(player, Animations.PULLING_ROPE_2270)
                        playAudio(player, Sounds.LOOM_WEAVE_2587)
                        delayClock(player, Clocks.SKILLING, 5)

                        if (removeItem(player, required)) {
                            addItem(player, product.id)
                            rewardXP(player, Skills.CRAFTING, type.experience)

                            val reqName = required.name.lowercase().replace("ball", "balls")
                            val suffix = when (type) {
                                Weaving.SACK -> "s"
                                Weaving.CLOTH -> ""
                                else -> "es"
                            }
                            val a = if (product.name.lowercase().matches(Regex("^[aeiou].*"))) "an" else "a"

                            sendMessage(
                                player,
                                "You weave the ${reqName}${suffix} into $a ${product.name.lowercase()}."
                            )

                            // diary
                            if (type == Weaving.BASKET &&
                                node.id == 8717 &&
                                withinDistance(player, Location(3039, 3287, 0))
                                && !hasDiaryTaskComplete(player, DiaryType.FALADOR, 1, 0)
                            ) {
                                finishDiaryTask(player, DiaryType.FALADOR, 1, 0)
                            }
                        }

                        remaining--

                        if (remaining <= 0 ||
                            !inInventory(player, required.id, required.amount)
                        ) {
                            return@queueScript stopExecuting(player)
                        }

                        delayScript(player, 5)
                    }
                }
            }.open()

            repositionChild(player, Components.SKILL_MAKE_304, 2, 56, 32)
            repositionChild(player, Components.SKILL_MAKE_304, 3, 207, 32)

            return@on true
        }
    }
}
