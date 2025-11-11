package core.game.dialogue

import core.game.node.entity.Entity

/**
 * Dialogue topic with animation, text, next stage, and optional player skip.
 *
 * @param T Type of the next stage.
 * @property expr Facial animation expression.
 * @property text Dialogue text.
 * @property toStage Next dialogue stage.
 * @property skipPlayer Whether to skip the player (default false).
 * @property speaker The entity that starts the topic (optional, default is null).
 */
open class Topic<T>
@JvmOverloads
constructor(
    val expr: FaceAnim,
    val text: String,
    val toStage: T,
    val skipPlayer: Boolean = false,
    val speaker: Entity? = null,
) {
    /**
     * Defaults [expr] to [FaceAnim.ASKING].
     */
    @JvmOverloads
    constructor(text: String, toStage: T, skipPlayer: Boolean = false, speaker: Entity? = null) : this(
        FaceAnim.ASKING, text, toStage, skipPlayer, speaker
    )
}

/**
 * Conditional dialogue topic shown based on [showCondition].
 *
 * @param T Type of the next stage.
 * @property expr Facial animation expression.
 * @property text Dialogue text.
 * @property toStage Next dialogue stage.
 * @property showCondition Condition to show this topic.
 * @property skipPlayer Whether to skip the player (default false).
 * @property speaker The entity that starts the topic (optional, default is null).
 */
class IfTopic<T>
@JvmOverloads
constructor(
    expr: FaceAnim,
    text: String,
    toStage: T,
    val showCondition: Boolean,
    skipPlayer: Boolean = false,
    speaker: Entity? = null,
) : Topic<T>(expr, text, toStage, skipPlayer, speaker) {
    /**
     * Defaults [expr] to [FaceAnim.ASKING].
     */
    @JvmOverloads
    constructor(text: String, toStage: T, showCondition: Boolean, skipPlayer: Boolean = false, speaker: Entity? = null) : this(
        FaceAnim.ASKING, text, toStage, showCondition, skipPlayer, speaker
    )
}
