package content.region.wilderness.npc.revenants

import content.region.wilderness.npc.revenants.RevenantController.Companion.registerRevenant
import content.region.wilderness.npc.revenants.RevenantController.Companion.unregisterRevenant
import core.api.playAudio
import core.api.playGlobalAudio
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.CombatSwingHandler
import core.game.node.entity.combat.DeathTask
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.audio.Audio
import core.game.node.entity.skill.Skills
import core.game.system.config.NPCConfigParser
import core.game.world.GameWorld.ticks
import core.game.world.map.Location
import core.game.world.map.RegionManager.getLocalPlayers
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.impl.WildernessZone
import core.game.world.update.flag.context.Animation
import core.plugin.Initializable
import core.tools.RandomFunction
import shared.consts.NPCs
import shared.consts.Sounds
import kotlin.math.abs

@Initializable
class RevenantNPC @JvmOverloads constructor(id: Int = -1, location: Location? = null, val routes: Array<Array<Location>>? = null) : AbstractNPC(id, location) {

    private var swingHandler: CombatSwingHandler? = null
    val type: RevenantsType? = RevenantsType.forId(id)

    override fun configure() {
        isWalks = true
        isRespawn = false
        isAggressive = true
        isRenderable = true
        setDefaultBehavior()
        getAggressiveHandler().radius = 64 * 2
        getAggressiveHandler().chanceRatio = 9
        getAggressiveHandler().isAllowTolerance = false
        properties.combatTimeOut = 120
        configureBonuses()
        super.configure()
        this.swingHandler =
            RevenantCombatHandler(properties.attackAnimation, properties.magicAnimation, properties.rangeAnimation)
        setAttribute("food-items", 20)
    }

    override fun init() {
        super.init()
        registerRevenant(this)
        val spawnAnim = definition.getConfiguration(NPCConfigParser.SPAWN_ANIMATION, -1)
        if (spawnAnim != -1) {
            animate(Animation(spawnAnim))
        }
    }

    override fun clear() {
        super.clear()
        unregisterRevenant(this, true)
    }

    override fun finalizeDeath(killer: Entity) {
        super.finalizeDeath(killer)
        playGlobalAudio(killer.location, 4063)
    }

    override fun tick() {
        skills.pulse()
        walkingQueue.update()
        if (viewport.region!!.isActive) {
            updateMasks.prepare(this)
        }
        if (!DeathTask.isDead(this)) {
            val curhp = getSkills().lifepoints
            val maxhp = getSkills().getStaticLevel(Skills.HITPOINTS)
            val fooditems = getAttribute("food-items", 0)
            if (curhp < maxhp / 2 && fooditems > 0 && getAttribute("eat-delay", 0) < ticks) {
                lock(3)
                properties.combatPulse.delayNextAttack(3)
                getSkills().heal(maxhp / 6)
                for (p in getLocalPlayers(this)) {
                    playAudio(p, Sounds.EAT_2393)
                }
                setAttribute("eat-delay", ticks + 6)
                setAttribute("food-items", fooditems - 1)
            }
        }
        behavior.tick(this)
        if (aggressiveHandler != null && aggressiveHandler.selectTarget()) {
            return
        }
    }

    override fun sendImpact(state: BattleState) {
        if (state.estimatedHit > type!!.maxHit) {
            state.estimatedHit = RandomFunction.random(type.maxHit - 5, type.maxHit)
        }
    }

    override fun getAudio(index: Int): Audio? = null

    override fun construct(id: Int, location: Location, vararg objects: Any): AbstractNPC = RevenantNPC(id, location, null)

    override fun setNextWalk() {
        nextWalk = ticks + RandomFunction.random(7, 15)
    }

    override fun getSwingHandler(swing: Boolean): CombatSwingHandler = swingHandler!!

    override fun canMove(location: Location): Boolean {
        for (zone in SAFE_ZONES) {
            if (zone.insideBorder(location)) {
                return false
            }
        }
        return true
    }

    override fun getMovementDestination(): Location {
        if (!pathBoundMovement || movementPath == null || movementPath.size < 1) {
            return getLocation().transform(
                -5 + RandomFunction.random(getWalkRadius()),
                -5 + RandomFunction.random(getWalkRadius()),
                0,
            )
        }
        val l = movementPath[movementIndex++]
        if (movementIndex == movementPath.size) {
            movementIndex = 0
        }
        return l
    }

    override fun getWalkRadius(): Int = 20

    override fun continueAttack(target: Entity, style: CombatStyle, message: Boolean): Boolean = if (target is Player) hasAcceptableCombatLevel(target.asPlayer()) else true

    override fun isAttackable(entity: Entity, style: CombatStyle, message: Boolean): Boolean {
        if (entity is Player) {
            if (!hasAcceptableCombatLevel(entity.asPlayer()) && !entity.asPlayer().isAdmin) {
                if (message) {
                    entity.asPlayer().sendMessage(
                        "The level difference between you and your opponent is too great.",
                    )
                }
                return false
            }
        }
        if (entity is content.global.skill.summoning.familiar.Familiar) {
            val owner = entity.owner ?: return false
            if (!hasAcceptableCombatLevel(owner)) {
                return false
            }
        }
        return super.isAttackable(entity, style, message)
    }

    override fun canSelectTarget(target: Entity): Boolean {
        if (target !is Player) {
            return false
        }
        return hasAcceptableCombatLevel(target.asPlayer())
    }

    override fun getIds(): IntArray = intArrayOf(
        // Imps.
        NPCs.REVENANT_IMP_6604, NPCs.REVENANT_IMP_6635, NPCs.REVENANT_IMP_6655, NPCs.REVENANT_IMP_6666, NPCs.REVENANT_IMP_6677, NPCs.REVENANT_IMP_6697, NPCs.REVENANT_IMP_6703, NPCs.REVENANT_IMP_6715,
        // Goblins.
        NPCs.REVENANT_GOBLIN_6605, NPCs.REVENANT_GOBLIN_6612, NPCs.REVENANT_GOBLIN_6616, NPCs.REVENANT_GOBLIN_6620, NPCs.REVENANT_GOBLIN_6636, NPCs.REVENANT_GOBLIN_6637, NPCs.REVENANT_GOBLIN_6638, NPCs.REVENANT_GOBLIN_6639, NPCs.REVENANT_GOBLIN_6651, NPCs.REVENANT_GOBLIN_6656, NPCs.REVENANT_GOBLIN_6657, NPCs.REVENANT_GOBLIN_6658, NPCs.REVENANT_GOBLIN_6667, NPCs.REVENANT_GOBLIN_6678, NPCs.REVENANT_GOBLIN_6679, NPCs.REVENANT_GOBLIN_6680, NPCs.REVENANT_GOBLIN_6681, NPCs.REVENANT_GOBLIN_6693, NPCs.REVENANT_GOBLIN_6698, NPCs.REVENANT_GOBLIN_6699, NPCs.REVENANT_GOBLIN_6704, NPCs.REVENANT_GOBLIN_6705, NPCs.REVENANT_GOBLIN_6706, NPCs.REVENANT_GOBLIN_6707, NPCs.REVENANT_GOBLIN_6716, NPCs.REVENANT_GOBLIN_6717, NPCs.REVENANT_GOBLIN_6718, NPCs.REVENANT_GOBLIN_6719,
        // Icefiends.
        NPCs.REVENANT_ICEFIEND_6606, NPCs.REVENANT_ICEFIEND_6621, NPCs.REVENANT_ICEFIEND_6628, NPCs.REVENANT_ICEFIEND_6640, NPCs.REVENANT_ICEFIEND_6659, NPCs.REVENANT_ICEFIEND_6682, NPCs.REVENANT_ICEFIEND_6694, NPCs.REVENANT_ICEFIEND_6708, NPCs.REVENANT_ICEFIEND_6720,
        // Pyrefiends.
        NPCs.REVENANT_PYREFIEND_6622, NPCs.REVENANT_PYREFIEND_6631, NPCs.REVENANT_PYREFIEND_6641, NPCs.REVENANT_PYREFIEND_6660, NPCs.REVENANT_PYREFIEND_6668, NPCs.REVENANT_PYREFIEND_6683, NPCs.REVENANT_PYREFIEND_6709, NPCs.REVENANT_PYREFIEND_6721,
        // Hobgoblins.
        NPCs.REVENANT_HOBGOBLIN_6608, NPCs.REVENANT_HOBGOBLIN_6642, NPCs.REVENANT_HOBGOBLIN_6661, NPCs.REVENANT_HOBGOBLIN_6684, NPCs.REVENANT_HOBGOBLIN_6710, NPCs.REVENANT_HOBGOBLIN_6722, NPCs.REVENANT_HOBGOBLIN_6727,
        // Vampires.
        NPCs.REVENANT_VAMPIRE_6613, NPCs.REVENANT_VAMPIRE_6623, NPCs.REVENANT_VAMPIRE_6643, NPCs.REVENANT_VAMPIRE_6652, NPCs.REVENANT_VAMPIRE_6662, NPCs.REVENANT_VAMPIRE_6669, NPCs.REVENANT_VAMPIRE_6671, NPCs.REVENANT_VAMPIRE_6674, NPCs.REVENANT_VAMPIRE_6685, NPCs.REVENANT_VAMPIRE_6695, NPCs.REVENANT_VAMPIRE_6700, NPCs.REVENANT_VAMPIRE_6711, NPCs.REVENANT_VAMPIRE_6723,
        // Werewolfs.
        NPCs.REVENANT_WEREWOLF_6607, NPCs.REVENANT_WEREWOLF_6609, NPCs.REVENANT_WEREWOLF_6614, NPCs.REVENANT_WEREWOLF_6617, NPCs.REVENANT_WEREWOLF_6625, NPCs.REVENANT_WEREWOLF_6632, NPCs.REVENANT_WEREWOLF_6644, NPCs.REVENANT_WEREWOLF_6663, NPCs.REVENANT_WEREWOLF_6675, NPCs.REVENANT_WEREWOLF_6686, NPCs.REVENANT_WEREWOLF_6701, NPCs.REVENANT_WEREWOLF_6712, NPCs.REVENANT_WEREWOLF_6724, NPCs.REVENANT_WEREWOLF_6728,
        // Cyclops.
        NPCs.REVENANT_CYCLOPS_6645, NPCs.REVENANT_CYCLOPS_6687,
        // Hellhounds.
        NPCs.REVENANT_HELLHOUND_6646, NPCs.REVENANT_HELLHOUND_6688,
        // Demons.
        NPCs.REVENANT_DEMON_6647, NPCs.REVENANT_DEMON_6689,
        // Orks.
        NPCs.REVENANT_ORK_6610, NPCs.REVENANT_ORK_6615, NPCs.REVENANT_ORK_6618, NPCs.REVENANT_ORK_6624, NPCs.REVENANT_ORK_6626, NPCs.REVENANT_ORK_6629, NPCs.REVENANT_ORK_6633, NPCs.REVENANT_ORK_6648, NPCs.REVENANT_ORK_6653, NPCs.REVENANT_ORK_6664, NPCs.REVENANT_ORK_6670, NPCs.REVENANT_ORK_6672, NPCs.REVENANT_ORK_6690, NPCs.REVENANT_ORK_6696, NPCs.REVENANT_ORK_6702, NPCs.REVENANT_ORK_6713, NPCs.REVENANT_ORK_6725, NPCs.REVENANT_ORK_6729,
        // Dark beasts.
        NPCs.REVENANT_DARK_BEAST_6649, NPCs.REVENANT_DARK_BEAST_6691,
        // Knights.
        NPCs.REVENANT_KNIGHT_6611, NPCs.REVENANT_KNIGHT_6619, NPCs.REVENANT_KNIGHT_6627, NPCs.REVENANT_KNIGHT_6630, NPCs.REVENANT_KNIGHT_6634, NPCs.REVENANT_KNIGHT_6650, NPCs.REVENANT_KNIGHT_6654, NPCs.REVENANT_KNIGHT_6665, NPCs.REVENANT_KNIGHT_6673, NPCs.REVENANT_KNIGHT_6676, NPCs.REVENANT_KNIGHT_6692, NPCs.REVENANT_KNIGHT_6714, NPCs.REVENANT_KNIGHT_6726, NPCs.REVENANT_KNIGHT_6730, NPCs.REVENANT_DRAGON_6998, NPCs.REVENANT_DRAGON_6999,
    )

    private fun configureBonuses() {
        for (i in properties.bonuses.indices) {
            properties.bonuses[i] = 40 + (4 * (properties.combatLevel / 2))
        }
    }

    private fun configureRoute() {
        if (routes.isNullOrEmpty()) {
            return
        }
        configureMovementPath(*RandomFunction.getRandomElement(routes))
    }

    private fun hasAcceptableCombatLevel(player: Player): Boolean {
        var level = WildernessZone.getWilderness(this)
        if (player.skullManager.level < level) {
            level = player.skullManager.level
        }
        val combat = properties.currentCombatLevel
        val targetCombat = player.properties.currentCombatLevel
        return abs((combat - targetCombat).toDouble()) <= level
    }

    companion object {
        private val SAFE_ZONES = arrayOf(
            ZoneBorders(3074, 3651, 3193, 3774),
            ZoneBorders(3264, 3672, 3279, 3695),
            ZoneBorders(3081, 3909, 3129, 3954),
            ZoneBorders(3350, 3869, 3391, 3900),
        )
    }
}
