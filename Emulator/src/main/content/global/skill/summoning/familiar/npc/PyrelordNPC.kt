package content.global.skill.summoning.familiar.npc

import content.global.skill.crafting.casting.gold.Jewellery
import content.global.skill.firemaking.FiremakingPulse
import content.global.skill.firemaking.Log
import content.global.skill.firemaking.logs
import content.global.skill.summoning.familiar.Familiar
import content.global.skill.summoning.familiar.FamiliarSpecial
import core.api.finishDiaryTask
import core.api.lock
import core.api.sendMessage
import core.game.interaction.NodeUsageEvent
import core.game.interaction.UseWithHandler
import core.game.node.entity.combat.equipment.WeaponInterface
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.entity.skill.SkillBonus
import core.game.node.entity.skill.Skills
import core.game.node.item.GroundItemManager
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.map.RegionManager.getObject
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.plugin.ClassScanner.definePlugin
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Items
import org.rs.consts.NPCs

@Initializable
class PyrelordNPC @JvmOverloads constructor(owner: Player? = null, id: Int = NPCs.PYRELORD_7377) :
    Familiar(owner, id, 3200, Items.PYRELORD_POUCH_12816, 6, WeaponInterface.STYLE_AGGRESSIVE) {

    init {
        boosts.add(SkillBonus(Skills.FIREMAKING, 3.0))
    }

    override fun construct(owner: Player, id: Int): Familiar {
        return PyrelordNPC(owner, id)
    }

    public override fun configureFamiliar() {
        definePlugin(PyrelordAbility())
    }


    override fun specialMove(special: FamiliarSpecial): Boolean {
        val item = special.node as Item
        if (item.id != Items.GOLD_BAR_2357) {
            sendMessage(owner, "You can only use this special on gold bars.")
            return false
        }
        lock(owner, 1)
        visualize(Animation.create(8081), Graphics.create(org.rs.consts.Graphics.RAINING_FLAMES_1463))
        Jewellery.open(owner)
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.PYRELORD_7377, NPCs.PYRELORD_7378)
    }


    inner class PyrelordAbility : UseWithHandler(*logs) {
        @Throws(Throwable::class)
        override fun newInstance(arg: Any?): Plugin<Any> {
            for (id in ids) {
                addHandler(id, NPC_TYPE, this)
            }
            return this
        }

        override fun handle(event: NodeUsageEvent): Boolean {
            val player = event.player
            val log = Log.forId(event.usedItem.id)
            val familiar = event.usedWith as Familiar
            val ticks = FIREMAKING_ANIMATION.definition.getDurationTicks()
            if (!player.familiarManager.isOwner(familiar)) {
                return true
            }
            if (getObject(familiar.location) != null || familiar.zoneMonitor.isInZone("bank")) {
                sendMessage(player, "You can't light a fire here.")
                return false
            }
            familiar.lock(ticks)
            familiar.animate(FIREMAKING_ANIMATION)
            if (player.inventory.remove(event.usedItem)) {
                val ground = GroundItemManager.create(event.usedItem, familiar.location, player)
                Pulser.submit(object : Pulse(ticks, player, familiar) {
                    override fun pulse(): Boolean {
                        if (!ground.isActive) {
                            return true
                        }
                        val `object` = Scenery(log!!.fireId, familiar.location)
                        familiar.moveStep()
                        GroundItemManager.destroy(ground)
                        player.getSkills().addExperience(Skills.FIREMAKING, log.xp + 10)
                        familiar.faceLocation(`object`.getFaceLocation(familiar.location))
                        SceneryBuilder.add(`object`, log.life, FiremakingPulse.getAsh(player, log, `object`))
                        if (player.viewport.region.id == 10806) {
                            finishDiaryTask(player, DiaryType.SEERS_VILLAGE, 1, 9)
                        }
                        return true
                    }
                })
            }
            return true
        }
    }

    companion object {
        private val FIREMAKING_ANIMATION: Animation = Animation.create(8085)
    }
}
