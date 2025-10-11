package content.global.skill.magic

import core.api.*
import core.game.event.SpellCastEvent
import core.game.interaction.MovementPulse
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager
import core.game.world.map.path.Pathfinder
import core.tools.Log
import shared.consts.Items

/**
 * Manages magic spell casting listeners.
 */
object SpellListeners {
    /**
     * Maps spell ids to their casting method.
     */
    val castMap = HashMap<String, (Player, Node?) -> Unit>()

    /**
     * Maps spell ids to their max casting distance.
     */
    val spellRanges = HashMap<String, Int>()

    /**
     * Registers a spell with a single identifier.
     *
     * @param spellID the id of the spell.
     * @param type the target type.
     * @param book the spellbook name.
     * @param distance the maximum casting distance.
     * @param method the function to execute when the spell is cast.
     */
    fun add(spellID: Int, type: Int, book: String, distance: Int, method: (Player, Node?) -> Unit) {
        castMap["$book:$spellID:$type"] = method
        spellRanges["$book:$spellID:$type"] = distance
    }

    /**
     * Registers a spell for multiple target ids.
     *
     * @param spellID the id of the spell.
     * @param type the target type.
     * @param ids an array of target ids.
     * @param book the spellbook name.
     * @param distance the max casting distance.
     * @param method the function to execute when the spell is cast.
     */
    fun add(spellID: Int, type: Int, ids: IntArray, book: String, distance: Int, method: (Player, Node?) -> Unit) {
        for (id in ids) {
            castMap["$book:$spellID:$type:$id"] = method
            spellRanges["$book:$spellID:$type:$id"] = distance
        }
    }

    /**
     * Gets a spells casting range and method by spell id and type.
     *
     * @param spellID the id of the spell
     * @param type the target type
     * @param book the spellbook name
     * @return a pair of the spells range and method
     */
    fun get(spellID: Int, type: Int, book: String): Pair<Int, ((Player, Node?) -> Unit)?> {
        log(this::class.java, Log.FINE, "Getting $book:$spellID:$type")
        return Pair(spellRanges["$book:$spellID:$type"] ?: 10, castMap["$book:$spellID:$type"])
    }

    /**
     * Gets a spells casting range and method by spell id, type, and target.
     *
     * @param spellID the id of the spell.
     * @param type the target type.
     * @param id the targets id.
     * @param book the spellbook name.
     * @return a pair of the spells range and method.
     */
    fun get(spellID: Int, type: Int, id: Int, book: String): Pair<Int, ((Player, Node?) -> Unit)?> {
        log(this::class.java, Log.FINE, "Getting $book:$spellID:$type:$id")
        return Pair(spellRanges["$book:$spellID:$type:$id"] ?: 10, castMap["$book:$spellID:$type:$id"])
    }

    /**
     * Executes a spell cast by a player on a target node or directly.
     *
     * @param button the spell button id.
     * @param type the target type.
     * @param book the spellbook name.
     * @param player the player casting the spell.
     * @param node (optional) the target node.
     */
    @JvmStatic
    fun run(button: Int, type: Int, book: String, player: Player, node: Node? = null) {
        if (inEquipment(player, Items.SLED_4084)) {
            sendMessage(player, "You can't do that right now.")
            return
        }

        var (range, method) = get(button, type, node?.id ?: 0, book)

        if (method == null) {
            var next = get(button, type, book)
            range = next.first
            method = next.second ?: return
        }

        if (type in intArrayOf(
                SpellListener.NPC,
                SpellListener.OBJECT,
                SpellListener.PLAYER,
                SpellListener.GROUND_ITEM,
            )
        ) {
            player.pulseManager.run(
                object : MovementPulse(player, node, Pathfinder.SMART) {
                    override fun pulse(): Boolean {
                        try {
                            method.invoke(player, node)
                        } catch (e: IllegalStateException) {
                            player.removeAttribute("spell:runes")
                            return true
                        }
                        return true
                    }

                    override fun update(): Boolean {
                        if (player.location.withinMaxnormDistance(
                                node!!.centerLocation,
                                range,
                            ) && hasLineOfSight(player, node)
                        ) {
                            player.faceLocation(node.getFaceLocation(player.location))
                            player.walkingQueue.reset()
                            pulse()
                            stop()
                            return true
                        }
                        return super.update()
                    }
                },
            )
        } else {
            try {
                method.invoke(player, node)
                player.dispatch(SpellCastEvent(SpellBookManager.SpellBook.valueOf(book.uppercase()), button, node))
            } catch (e: IllegalStateException) {
                removeAttribute(player, "spell:runes")
                return
            }
        }
    }
}
