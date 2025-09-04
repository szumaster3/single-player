package core.api

import core.game.container.Container
import core.game.container.ContainerEvent

/**
 * Listener for changes in a [Container].
 */
interface ContainerListener {

    /**
     * Called when the [container] is updated with an [event].
     *
     * @param container The container that was updated, may be null.
     * @param event The event describing the update, may be null.
     */
    fun update(
        container: Container?,
        event: ContainerEvent?,
    )

    /**
     * Called when the [container] needs to be refreshed.
     *
     * @param container The container to refresh, may be null.
     */
    fun refresh(container: Container?)
}