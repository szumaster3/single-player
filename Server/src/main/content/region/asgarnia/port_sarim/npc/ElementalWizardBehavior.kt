package content.region.asgarnia.port_sarim.npc

import core.api.getAttribute
import core.api.setAttribute
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.spell.CombatSpell
import core.game.node.entity.combat.spell.MagicSpell
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager.SpellBook
import core.tools.RandomFunction
import shared.consts.NPCs

class ElementalWizardBehavior : NPCBehavior(
    NPCs.FIRE_WIZARD_2709,
    NPCs.WATER_WIZARD_2710,
    NPCs.EARTH_WIZARD_2711,
    NPCs.AIR_WIZARD_2712
) {
    private val ATTR_SWITCH = "spell-switch"

    override fun onCreation(self: NPC) {
        setBaseSpell(self)
    }

    override fun afterDamageReceived(self: NPC, attacker: Entity, state: BattleState) {
        state.spell?.takeIf { isSpellType(self, it) }?.let {
            state.estimatedHit = 0
            state.maximumHit = 0
            self.sendChat("Gratias tibi ago")
            self.getSkills().heal(self.getSkills().getStaticLevel(core.game.node.entity.skill.Skills.HITPOINTS))

            (state.attacker as? Player)?.let { player ->
                player.achievementDiaryManager
                    .getDiary(core.game.node.entity.player.link.diary.DiaryType.FALADOR)
                    ?.takeIf { !it.isComplete(0, 8) }
                    ?.updateTask(player, 0, 8, true)
            }
        }

        if (getAttribute(self, ATTR_SWITCH, false)) {
            setBaseSpell(self)
            setAttribute(self, ATTR_SWITCH, false)
        }
    }

    override fun tick(self: NPC): Boolean {
        if (RandomFunction.random(6) > 4) {
            setRandomSpell(self)
        }
        return true
    }

    private fun setRandomSpell(self: NPC) {
        SPELLS.getOrNull(spellIndex(self))?.let { spells ->
            self.properties.autocastSpell = spells.random().let { spellId ->
                SpellBook.MODERN.getSpell(spellId) as? CombatSpell
            }
            setAttribute(self, ATTR_SWITCH, true)
        }
    }

    private fun setBaseSpell(self: NPC) {
        SPELLS.getOrNull(spellIndex(self))?.firstOrNull()?.let { spellId ->
            self.properties.autocastSpell = SpellBook.MODERN.getSpell(spellId) as? CombatSpell
        }
    }

    private fun isSpellType(self: NPC, spell: MagicSpell): Boolean {
        val prefixes = arrayOf("Fire", "Water", "Earth", "Air")
        return spell.javaClass.simpleName.startsWith(prefixes.getOrNull(spellIndex(self)) ?: "")
    }

    private fun spellIndex(self: NPC): Int = ids.indexOf(self.id).takeIf { it >= 0 } ?: 0

    companion object {
        private val SPELLS = arrayOf(
            intArrayOf(8, 7), // Fire
            intArrayOf(4, 7), // Water
            intArrayOf(6, 7), // Earth
            intArrayOf(1, 7)  // Air
        )
    }
}
