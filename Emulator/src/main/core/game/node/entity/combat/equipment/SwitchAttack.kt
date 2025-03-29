package core.game.node.entity.combat.equipment

import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.CombatSwingHandler
import core.game.node.entity.impl.Projectile
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics

/**
 * Represents a switch attack, which allows modifying attack properties such as animations, graphics, and projectiles.
 *
 * @property handler The combat swing handler used for the attack.
 * @property animation The attack animation.
 * @property startGraphics The initial graphics effect when executing the attack.
 * @property endGraphics The final graphics effect after the attack.
 * @property projectile The projectile used in the attack, if applicable.
 */
open class SwitchAttack
@JvmOverloads
constructor(
    handler: CombatSwingHandler?,
    animation: Animation?,
    startGraphics: Graphics? = null,
    endGraphics: Graphics? = null,
    projectile: Projectile? = null,
) {
    /**
     * The combat swing handler associated with this attack.
     */
    var handler: CombatSwingHandler? = null

    /**
     * The initial graphics effect when executing the attack.
     */
    val startGraphics: Graphics?

    /**
     * The attack animation.
     */
    val animation: Animation?

    /**
     * The final graphics effect after the attack.
     */
    val endGraphics: Graphics?

    /**
     * The projectile used in the attack, if applicable.
     */
    val projectile: Projectile?

    /**
     * Determines whether to use the handler for processing attack mechanics.
     */
    @JvmField
    var isUseHandler: Boolean = false

    /**
     * The maximum possible damage for this attack.
     */
    @JvmField
    var maximumHit: Int = -1

    /**
     * Secondary constructor for attacks that involve a projectile but no start or end graphics.
     *
     * @param handler The combat swing handler for the attack.
     * @param animation The attack animation.
     * @param projectile The projectile used in the attack.
     */
    constructor(handler: CombatSwingHandler?, animation: Animation?, projectile: Projectile?) : this(
        handler = handler,
        animation = animation,
        startGraphics = null,
        endGraphics = null,
        projectile = projectile,
    )

    /**
     * Secondary constructor using a [CombatStyle].
     *
     * @param style The combat style associated with this attack.
     */
    constructor(style: CombatStyle) : this(
        handler = style.swingHandler,
        animation = null,
        startGraphics = null,
        endGraphics = null,
    )

    init {
        this.handler = handler
        this.animation = animation
        this.startGraphics = startGraphics
        this.endGraphics = endGraphics
        this.projectile = projectile
    }

    /**
     * Determines whether this attack style can be selected based on the entity, victim, and battle state.
     *
     * @param entity The attacking entity.
     * @param victim The entity being attacked.
     * @param state The battle state containing relevant combat details.
     * @return `true` if the attack can be selected, otherwise `false`.
     */
    open fun canSelect(
        entity: Entity?,
        victim: Entity?,
        state: BattleState?,
    ): Boolean = true

    /**
     * Retrieves the combat style associated with this attack.
     */
    val style: CombatStyle? get() = handler!!.type

    /**
     * Sets whether the attack should use the handler for combat calculations.
     *
     * @param useHandler `true` to use the handler, `false` otherwise.
     * @return The modified [SwitchAttack] instance.
     */
    fun setUseHandler(useHandler: Boolean): SwitchAttack {
        this.isUseHandler = useHandler
        return this
    }

    /**
     * Sets the maximum possible hit for this attack.
     *
     * @param maximumHit The maximum hit value.
     * @return The modified [SwitchAttack] instance.
     */
    fun setMaximumHit(maximumHit: Int): SwitchAttack {
        this.maximumHit = maximumHit
        return this
    }
}
