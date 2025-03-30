package content.region.fremennik.handlers.general_shadows.handlers

import content.region.fremennik.handlers.general_shadows.GeneralShadow
import core.api.*
import core.game.node.entity.Entity
import core.game.node.entity.impl.Projectile
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.plugin.Initializable
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.NPCs

@Initializable
class GhostBouncerNPC(
    id: Int = 0,
    location: Location? = null,
) : AbstractNPC(id, location) {
    override fun construct(
        id: Int,
        location: Location,
        vararg objects: Any,
    ): AbstractNPC = GhostBouncerNPC(id, location)

    override fun getIds(): IntArray = intArrayOf(NPCs.BOUNCER_5564)

    companion object {
        fun spawnGhostBouncer(player: Player) {
            val ghostBouncer = GhostBouncerNPC(NPCs.BOUNCER_5564)
            ghostBouncer.location = Location(1761, 4705, 0)
            ghostBouncer.isWalks = true
            ghostBouncer.isAggressive = true
            ghostBouncer.isActive = false

            if (ghostBouncer.asNpc() != null && ghostBouncer.isActive) {
                ghostBouncer.properties.teleportLocation = ghostBouncer.properties.spawnLocation
            }
            ghostBouncer.isActive = true
            GameWorld.Pulser.submit(
                object : Pulse(14, ghostBouncer) {
                    override fun pulse(): Boolean {
                        ghostBouncer.init()
                        ghostBouncer.attack(player)
                        return true
                    }
                },
            )
        }
    }

    override fun finalizeDeath(killer: Entity?) {
        if (killer is Player) {
            val player = killer.asPlayer()
            lock(player, 100)
            lockInteractions(player, 100)
            sendMessage(player, "You grab the severed leg and throw it to distract the hound.")
            animate(player, Animation(Animations.SEVERED_LEG_ATTACK_5812))
            sendChat(player, "Away, darn spot!")
            spawnProjectile(
                source = Projectile.getLocation(killer),
                dest = Location.getRandomLocation(Projectile.getLocation(player), 5, true),
                projectile = 1024,
                startHeight = 40,
                endHeight = 30,
                delay = 0,
                speed = 250,
                angle = 0,
            )
            if (removeItem(player, Items.SEVERED_LEG_10857)) {
                lock(player, 8)
                runTask(player, 8) {
                    sendItemDialogue(
                        killer.asPlayer(),
                        Items.SHADOW_SWORD_10858,
                        "You receive a shadow sword and 2000 Slayer xp.",
                    )
                    rewardXP(player, Skills.SLAYER, 2000.0)
                    addItemOrDrop(player, Items.SHADOW_SWORD_10858)
                    /*
                     * Finish the general shadows mini-quest.
                     */
                    GeneralShadow.setShadowProgress(player, 5)
                    unlock(player)
                }
            }
        }
        clear()
        super.finalizeDeath(killer)
    }
}
