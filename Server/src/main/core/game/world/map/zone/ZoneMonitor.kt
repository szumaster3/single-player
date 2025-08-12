package core.game.world.map.zone

import core.game.interaction.Option
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.music.MusicZone
import core.game.node.entity.player.link.request.RequestType
import core.game.node.item.Item
import core.game.world.map.Location
import shared.consts.Items

/**
 * Handles the zones for an entity.
 *
 * @author Emperor
 */
class ZoneMonitor(private val entity: Entity) {
    internal val zones: MutableList<RegionZone> = ArrayList(20)
    private val musicZones: MutableList<MusicZone> = ArrayList(20)
    val type: Int
        /**
         * Gets type.
         *
         * @return the type
         */
        get() {
            for (zone in zones) {
                if (zone.zone.zoneType != 0) {
                    return zone.zone.zoneType
                }
            }
            return 0
        }

    /**
     * Checks if the player can logout.
     *
     * @return `True` if so.
     */
    fun canLogout(): Boolean {
        for (z in zones) {
            if (!z.zone.canLogout(entity as Player)) {
                return false
            }
        }
        return true
    }

    /**
     * Is restricted boolean.
     *
     * @param restriction the restriction
     * @return the boolean
     */
    fun isRestricted(restriction: ZoneRestriction): Boolean {
        return isRestricted(restriction.flag)
    }

    /**
     * Checks if the restriction was flagged.
     *
     * @param flag The restriction flag.
     * @return `True` if so.
     */
    fun isRestricted(flag: Int): Boolean {
        for (z in zones) {
            if (z.zone.isRestricted(flag)) {
                return true
            }
        }
        return false
    }

    /**
     * Handles a death.
     *
     * @param killer The killer.
     * @return `True` if the death got handled.
     */
    fun handleDeath(killer: Entity?): Boolean {
        for (z in zones) {
            if (z.zone.death(entity, killer)) {
                return true
            }
        }
        return false
    }

    /**
     * Checks if the entity is able to continue attacking the target.
     *
     * @param target The target.
     * @param style  The combat style used.
     * @return `True` if so.
     */
    fun continueAttack(target: Node?, style: CombatStyle?, message: Boolean): Boolean {
        if (target is Entity) {
            if (!entity.continueAttack(target, style, message)) {
                return false
            }
        }
        for (z in zones) {
            if (!z.zone.continueAttack(entity, target, style, message)) {
                return false
            }
        }
        if (entity is Player && target is Player) {
            if (!entity.skullManager.isWilderness || !target.skullManager.isWilderness) {
                if (message) {
                    entity.packetDispatch.sendMessage("You can only attack other players in the wilderness.")
                }
                return false
            }
        }
        if (target is Entity && !MapZone.checkMulti(entity, target, message)) {
            return false
        }
        return true
    }

    /**
     * Interact boolean.
     *
     * @param target the target
     * @param option the option
     * @return the boolean
     */
    fun interact(target: Node?, option: Option?): Boolean {
        for (z in zones) {
            if (z.zone.interact(entity, target, option)) {
                return true
            }
        }
        return false
    }

    /**
     * Use with boolean.
     *
     * @param used the used
     * @param with the with
     * @return the boolean
     */
    fun useWith(used: Item?, with: Node?): Boolean {
        for (z in zones) {
            if (z.zone.handleUseWith(entity.asPlayer(), used, with)) {
                return true
            }
        }
        return false
    }

    /**
     * Checks if the player handled the reward button using a map zone.
     *
     * @param interfaceId The interface id.
     * @param buttonId    The button id.
     * @param slot        The slot.
     * @param itemId      The item id.
     * @param opcode      The packet opcode.
     * @return `True` if the button got handled.
     */
    fun clickButton(interfaceId: Int, buttonId: Int, slot: Int, itemId: Int, opcode: Int): Boolean {
        for (z in zones) {
            if (z.zone.actionButton(entity as Player, interfaceId, buttonId, slot, itemId, opcode)) {
                return true
            }
        }
        return false
    }

    /**
     * Checks if multiway combat zone rules should be ignored.
     *
     * @param victim The victim.
     * @return `True` if this entity can attack regardless of multiway
     * combat zone.
     */
    fun isIgnoreMultiBoundaries(victim: Entity?): Boolean {
        for (z in zones) {
            if (z.zone.ignoreMultiBoundaries(entity, victim)) {
                return true
            }
        }
        return false
    }

    /**
     * Teleport boolean.
     *
     * @param type the type
     * @param node the node
     * @return the boolean
     */
    fun teleport(type: Int, node: Node): Boolean {
        if (type != -1 && entity.isTeleBlocked && !canTeleportByJewellery(type, node)) {
            if (entity.isPlayer) {
                entity.asPlayer().sendMessage("A magical force has stopped you from teleporting.")
            }
            return false
        }
        for (z in zones) {
            if (!z.zone.teleport(entity, type, node)) {
                return false
            }
        }
        return true
    }

    private fun canTeleportByJewellery(type: Int, node: Node): Boolean {
        if (type != 1 || !WILDERNESS_LEVEL_30_TELEPORT_ITEMS.contains(node.asItem().id)) {
            return false
        }
        if (entity.timers.getTimer("teleblock") != null) return false

        if (entity.zoneMonitor.isRestricted(ZoneRestriction.TELEPORT)) {
            return false
        }

        if (entity.locks.isTeleportLocked()) {
            if (entity.isPlayer) {
                val p = entity.asPlayer()
                if (p.skullManager.level >= 1 && p.skullManager.level <= 30) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Start death boolean.
     *
     * @param entity the entity
     * @param killer the killer
     * @return the boolean
     */
    fun startDeath(entity: Entity?, killer: Entity?): Boolean {
        for (z in zones) {
            if (!z.zone.startDeath(entity, killer)) {
                return false
            }
        }
        return true
    }

    /**
     * Can fire random event boolean.
     *
     * @return the boolean
     */
    fun canFireRandomEvent(): Boolean {
        for (z in zones) {
            if (!z.zone.isFireRandoms) {
                return false
            }
        }
        return true
    }

    /**
     * Clear boolean.
     *
     * @return the boolean
     */
    fun clear(): Boolean {
        for (z in zones) {
            if (!z.zone.leave(entity, true)) {
                return false
            }
        }
        for (z in musicZones) {
            z.leave(entity, true)
        }
        zones.clear()
        musicZones.clear()
        return true
    }

    /**
     * Move boolean.
     *
     * @param location    the location
     * @param destination the destination
     * @return the boolean
     */
    fun move(location: Location?, destination: Location?): Boolean {
        for (z in zones) {
            if (!z.zone.move(entity, location, destination)) {
                return false
            }
        }
        return true
    }

    /**
     * Update location boolean.
     *
     * @param last the last
     * @return the boolean
     */
    fun updateLocation(last: Location?): Boolean {
        if (entity is Player && !entity.asPlayer().isArtificial) {
            checkMusicZones()
        }
        entity.updateLocation(last)
        val it = zones.iterator()
        while (it.hasNext()) {
            val zone = it.next()
            if (!zone.borders.insideBorder(entity)) {
                if (zone.zone.isDynamicZone) {
                    continue
                }
                if (!zone.zone.leave(entity, false)) {
                    return false
                }
                it.remove()
            }
        }
        for (zone in entity.viewport.region!!.regionZones) {
            if (!zone.borders.insideBorder(entity)) {
                continue
            }
            var alreadyEntered = false
            for (z in zones) {
                if (z.zone === zone.zone) {
                    alreadyEntered = true
                    break
                }
            }
            if (alreadyEntered) {
                zone.zone.locationUpdate(entity, last)
                continue
            }
            if (!zone.zone.enter(entity)) {
                return false
            }
            zones.add(zone)
            zone.zone.locationUpdate(entity, last)
        }
        return true
    }

    /**
     * Check music zones.
     */
    fun checkMusicZones() {
        if (entity !is Player) {
            return
        }
        val player = entity
        val l = player.location
        val it = musicZones.iterator()
        while (it.hasNext()) {
            val zone = it.next()
            if (!zone.borders.insideBorder(l.x, l.y)) {
                zone.leave(player, false)
                it.remove()
            }
        }
        for (zone in player.viewport.region!!.musicZones) {
            if (!zone.borders.insideBorder(l.x, l.y)) {
                continue
            }
            if (!musicZones.contains(zone)) {
                zone.enter(player)
                musicZones.add(zone)
            }
        }
        if (musicZones.isEmpty() && !player.musicPlayer.isPlaying) {
            player.musicPlayer.playDefault()
        }
    }

    /**
     * Parse command boolean.
     *
     * @param player    the player
     * @param name      the name
     * @param arguments the arguments
     * @return the boolean
     */
    fun parseCommand(player: Player?, name: String?, arguments: Array<String?>?): Boolean {
        for (zone in zones) {
            if (zone.zone.parseCommand(player, name, arguments)) {
                return true
            }
        }
        return false
    }

    /**
     * Can request boolean.
     *
     * @param type   the type
     * @param target the target
     * @return the boolean
     */
    fun canRequest(type: RequestType?, target: Player?): Boolean {
        for (zone in zones) {
            if (!zone.zone.canRequest(type, entity.asPlayer(), target)) {
                return false
            }
        }
        return true
    }

    /**
     * Is in zone boolean.
     *
     * @param name the name
     * @return the boolean
     */
    fun isInZone(name: String): Boolean {
        val uid = name.hashCode()
        for (zone in zones) {
            if (zone.zone.uid == uid) {
                return true
            }
        }
        return false
    }

    /**
     * Remove.
     *
     * @param zone the zone
     */
    fun remove(zone: MapZone) {
        val it = zones.iterator()
        while (it.hasNext()) {
            if (it.next().zone === zone) {
                it.remove()
                break
            }
        }
    }

    /**
     * Gets zones.
     *
     * @return the zones
     */
    fun getZones(): List<RegionZone> {
        return zones
    }

    /**
     * Gets music zones.
     *
     * @return the music zones
     */
    fun getMusicZones(): List<MusicZone> {
        return musicZones
    }

    companion object {
        /**
         * Represents the jewellery that allows teleporting out from the Wilderness at level 30 or below.
         */
        val WILDERNESS_LEVEL_30_TELEPORT_ITEMS: Set<Int> = java.util.Set.of(
            Items.AMULET_OF_GLORY_1704,
            Items.AMULET_OF_GLORY1_1706,
            Items.AMULET_OF_GLORY2_1708,
            Items.AMULET_OF_GLORY3_1710,
            Items.AMULET_OF_GLORYT_10362,
            Items.AMULET_OF_GLORYT1_10360,
            Items.AMULET_OF_GLORYT2_10358,
            Items.AMULET_OF_GLORYT3_10356,
            Items.SKILLS_NECKLACE_11113,
            Items.SKILLS_NECKLACE1_11111,
            Items.SKILLS_NECKLACE2_11109,
            Items.SKILLS_NECKLACE3_11107,
            Items.COMBAT_BRACELET_11126,
            Items.COMBAT_BRACELET1_11124,
            Items.COMBAT_BRACELET2_11122,
            Items.COMBAT_BRACELET3_11120,
            Items.PHARAOHS_SCEPTRE_9044,
            Items.PHARAOHS_SCEPTRE_9046,
            Items.PHARAOHS_SCEPTRE_9048
        )
    }
}
