package content.region.kandarin.plugin

import core.api.*
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders
import shared.consts.Items
import shared.consts.NPCs

class OgreNPCBehavior : NPCBehavior(NPCs.OGRE_2801) {

    companion object {
        const val ATTACKED_BY_ATTRIBUTE = "attackedByPlayers"
        const val AVA_WARNING_SHOWN = "ava_warning_shown"
        private val AVA_EXCEPTION_AREA = ZoneBorders(2523, 3373, 2533, 3377)
    }

    override fun canBeAttackedBy(self: NPC, attacker: Entity, style: CombatStyle, shouldSendMessage: Boolean): Boolean {
        if (attacker !is Player) return false

        val allowed =
            style == CombatStyle.MAGIC || style == CombatStyle.RANGE || (style == CombatStyle.MELEE && attacker.location.getDistance(
                self.location
            ) > 1)

        if (!allowed && shouldSendMessage) {
            sendMessage(attacker, "These ogres are for ranged combat only.")
        }

        return allowed
    }

    override fun afterDamageReceived(self: NPC, attacker: Entity, state: BattleState) {
        if (attacker !is Player) return

        getAttacker(self).add(attacker)

        val hasAva =
            inEquipment(attacker, Items.AVAS_ATTRACTOR_10498) ||
                    inEquipment(attacker, Items.AVAS_ACCUMULATOR_10499)

        if (!hasAva) return

        val inAvaException = inBorders(attacker, AVA_EXCEPTION_AREA)
        if (inAvaException) return

        if (getAttribute(attacker, AVA_WARNING_SHOWN, false)) return

        setAttribute(attacker, AVA_WARNING_SHOWN, true)
        sendMessage(attacker, "Your Ava's device does not work here.")
    }

    override fun shouldIgnoreMultiRestrictions(self: NPC, victim: Entity): Boolean {
        return victim is Player && getAttacker(self).contains(victim)
    }

    private fun getAttacker(self: NPC): MutableSet<Player> {
        val set = getAttribute(self, ATTACKED_BY_ATTRIBUTE, mutableSetOf<Player>())
        if (set.isEmpty()) {
            setAttribute(self, ATTACKED_BY_ATTRIBUTE, set)
        }
        return set
    }
}
