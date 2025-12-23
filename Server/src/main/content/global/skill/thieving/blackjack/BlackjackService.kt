package content.global.skill.thieving.blackjack

import core.api.getStatLevel
import core.api.sendMessage
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction
import shared.consts.Animations
import core.api.stun
import core.api.animate
import core.api.impact
import core.api.rewardXP
import core.game.world.GameWorld

object BlackjackService {
    private val ticks: Int
        get() = GameWorld.ticks

    /**
     * Attempt to lure the NPC.
     */
    fun lure(player: Player, npc: NPC) {
        sendMessage(player, "You attempt to lure the ${npc.name}.")
        npc.face(player)
    }

    /**
     * Attempt to knock out the NPC with a blackjack.
     */
    fun knockOut(player: Player, npc: NPC) {
        val blackjack = getEquippedBlackjack(player) ?: run {
            sendMessage(player, "You need a blackjack to do this.")
            return
        }

        val npcData = BlackjackNPC.forId(npc.id) ?: run {
            sendMessage(player, "This NPC cannot be knocked out with a blackjack.")
            return
        }

        if (getStatLevel(player, Skills.THIEVING) < npcData.thievingLevel) {
            sendMessage(player, "You need a Thieving level of ${npcData.thievingLevel} to knock out this NPC.")
            return
        }

        if (RandomFunction.random(100) < failChance(player, npc)) {
            fail(player, npc)
        } else {
            success(player, npc, npcData, blackjack)
        }
    }

    /**
     * Handle a successful knockout.
     */
    private fun success(player: Player, npc: NPC, data: BlackjackNPC, blackjack: BlackjackType) {
        val state = BlackjackState(
            unconsciousUntil = ticks + 1,
            pickpocketsLeft = data.maxPickpockets
        )
        npc.attributes["blackjack"] = state
        player.animate(Animation(827))
        npc.animator.animate(Animation(837))
        npc.sendChat("Arghh, my head.")
        npc.sendChat("Zzzzzzz")
        stun(npc, getStunTicks(blackjack))
        rewardXP(player, Skills.THIEVING, data.xp)
        sendMessage(player, "You knock the ${npc.name} out.")
    }

    /**
     * Handle a failed knockout attempt.
     */
    private fun fail(player: Player, npc: NPC) {
        animate(npc, Animations.PUNCH_422)
        impact(player, 2)
        stun(player, 3)
        sendMessage(player, "You fail to knock them out.")
    }

    /**
     * Returns whether the NPC can currently be pickpocketed.
     */
    fun canPickpocket(npc: NPC): Boolean {
        val state = npc.attributes["blackjack"] as? BlackjackState ?: return false
        return state.isUnconscious(ticks) && state.pickpocketsLeft > 0
    }

    /**
     * Called when a player successfully pickpockets the NPC.
     */
    fun onPickpocket(npc: NPC) {
        val state = npc.attributes["blackjack"] as? BlackjackState ?: return
        state.pickpocketsLeft--
    }

    /**
     * Returns the blackjack equipped by the player, if any.
     */
    private fun getEquippedBlackjack(player: Player): BlackjackType? =
        BlackjackType.fromItem(player.equipment.getId(3))

    /**
     * Determines fail chance for knockout attempt.
     */
    private fun failChance(player: Player, npc: NPC): Int {
        val levelDiff = getStatLevel(player, Skills.THIEVING) - (BlackjackNPC.forId(npc.id)?.thievingLevel ?: 0)
        return (50 - levelDiff).coerceIn(5, 95)
    }

    /**
     * Get the number of stun ticks for a blackjack.
     */
    private fun getStunTicks(blackjack: BlackjackType): Int = when (blackjack) {
        BlackjackType.ORDINARY -> 1
        BlackjackType.OFFENSIVE -> 1
        BlackjackType.DEFENSIVE -> 1
    }
}