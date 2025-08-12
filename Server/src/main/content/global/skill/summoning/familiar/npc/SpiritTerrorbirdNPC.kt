package content.global.skill.summoning.familiar.npc

import content.global.skill.summoning.familiar.BurdenBeast
import content.global.skill.summoning.familiar.Familiar
import content.global.skill.summoning.familiar.FamiliarSpecial
import core.game.node.entity.combat.equipment.WeaponInterface
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.plugin.Initializable
import shared.consts.Animations
import shared.consts.Items
import shared.consts.NPCs

@Initializable
class SpiritTerrorbirdNPC @JvmOverloads constructor(owner: Player? = null, id: Int = NPCs.SPIRIT_TERRORBIRD_6794) :
    BurdenBeast(owner, id, 3600, Items.SPIRIT_TERRORBIRD_POUCH_12007, 8, 12, WeaponInterface.STYLE_CONTROLLED) {
    override fun construct(owner: Player, id: Int): Familiar {
        return SpiritTerrorbirdNPC(owner, id)
    }

    override fun specialMove(special: FamiliarSpecial): Boolean {
        visualize(Animation.create(1009), Graphics.create(shared.consts.Graphics.WHITE_SEAGALS_1521))
        owner.getSkills().updateLevel(Skills.AGILITY, 2)
        owner.settings.updateRunEnergy(-owner.getSkills().getStaticLevel(Skills.AGILITY) / 2.0)
        return true
    }

    override fun visualizeSpecialMove() {
        owner.visualize(
            Animation(Animations.CAST_FAMILIAR_SCROLL_7660),
            Graphics(shared.consts.Graphics.SPIRIT_TERRORBIRD_SPECIAL_MOVE_1295)
        )
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SPIRIT_TERRORBIRD_6794, NPCs.SPIRIT_TERRORBIRD_6795)
    }
}
