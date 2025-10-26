package content.global.skill.runecrafting

import core.api.hasRequirement
import core.api.isQuestComplete
import core.api.sendMessage
import core.cache.def.impl.ItemDefinition
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import shared.consts.Quests

/**
 * Represents an altar used in the runecrafting skill.
 */
enum class Altar(
    val scenery: Int,
    val exit: Int,
    val rift: Int,
    val ruin: MysteriousRuins?,
    val rune: Rune?
) {
    AIR(shared.consts.Scenery.AIR_ALTAR_2478, shared.consts.Scenery.AIR_ALTAR_EXIT_2465, shared.consts.Scenery.AIR_RIFT_7139, MysteriousRuins.AIR, Rune.AIR),
    MIND(shared.consts.Scenery.MIND_ALTAR_2479, shared.consts.Scenery.MIND_ALTAR_EXIT_2466, shared.consts.Scenery.MIND_RIFT_7140, MysteriousRuins.MIND, Rune.MIND),
    WATER(shared.consts.Scenery.WATER_ALTAR_2480, shared.consts.Scenery.WATER_ALTAR_EXIT_2467, shared.consts.Scenery.WATER_RIFT_7137, MysteriousRuins.WATER, Rune.WATER),
    EARTH(shared.consts.Scenery.EARTH_ALTAR_2481, shared.consts.Scenery.EARTH_ALTAR_EXIT_2468, shared.consts.Scenery.EARTH_RIFT_7130, MysteriousRuins.EARTH, Rune.EARTH),
    FIRE(shared.consts.Scenery.FIRE_ALTAR_2482, shared.consts.Scenery.FIRE_ALTAR_EXIT_2469, shared.consts.Scenery.FIRE_RIFT_7129, MysteriousRuins.FIRE, Rune.FIRE),
    BODY(shared.consts.Scenery.BODY_ALTAR_2483, shared.consts.Scenery.BODY_ALTAR_EXIT_2470, shared.consts.Scenery.BODY_RIFT_7131, MysteriousRuins.BODY, Rune.BODY),
    COSMIC(shared.consts.Scenery.COSMIC_ALTAR_2484, shared.consts.Scenery.COSMIC_ALTAR_EXIT_2471, shared.consts.Scenery.COSMIC_RIFT_7132, MysteriousRuins.COSMIC, Rune.COSMIC),
    CHAOS(shared.consts.Scenery.CHAOS_ALTAR_2487, shared.consts.Scenery.CHAOS_ALTAR_EXIT_2474, shared.consts.Scenery.CHAOS_RIFT_7134, MysteriousRuins.CHAOS, Rune.CHAOS),
    ASTRAL(shared.consts.Scenery.ALTAR_17010, 0, 0, null, Rune.ASTRAL),
    NATURE(shared.consts.Scenery.NATURE_ALTAR_2486, shared.consts.Scenery.NATURE_ALTAR_EXIT_2473, shared.consts.Scenery.NATURE_RIFT_7133, MysteriousRuins.NATURE, Rune.NATURE),
    LAW(shared.consts.Scenery.LAW_ALTAR_2485, shared.consts.Scenery.LAW_PORTAL_EXIT_2472, shared.consts.Scenery.LAW_RIFT_7135, MysteriousRuins.LAW, Rune.LAW),
    DEATH(shared.consts.Scenery.DEATH_ALTAR_2488, shared.consts.Scenery.DEATH_ALTAR_EXIT_2475, shared.consts.Scenery.DEATH_RIFT_7136, MysteriousRuins.DEATH, Rune.DEATH),
    BLOOD(shared.consts.Scenery.BLOOD_ALTAR_30624, shared.consts.Scenery.BLOOD_ALTAR_EXIT_2477, shared.consts.Scenery.BLOOD_RIFT_7141, MysteriousRuins.BLOOD, Rune.BLOOD),
    OURANIA(shared.consts.Scenery.OURANIA_ALTAR_26847, 0, 0, null, null);

    companion object {
        private val altarByScenery = values().associateBy { it.scenery }
        private val altarByExit = values().associateBy { it.exit }
        private val altarByRiftId = values().associateBy { it.rift }

        /**
         * Retrieves the corresponding altar based on the given scenery object.
         */
        fun forScenery(scenery: Scenery): Altar? {
            return altarByScenery[scenery.id]
                ?: altarByExit[scenery.id]
                ?: altarByRiftId[scenery.id]
        }
    }

    /**
     * Makes the player enter the rift for the current altar.
     */
    fun enterRift(player: Player) {
        when (this) {
            ASTRAL -> if (!hasRequirement(player, Quests.LUNAR_DIPLOMACY)) return
            DEATH -> if (!hasRequirement(player, Quests.MOURNINGS_END_PART_II)) return
            BLOOD -> if (!hasRequirement(player, Quests.LEGACY_OF_SEERGAZE)) return
            LAW -> if (!ItemDefinition.canEnterEntrana(player)) {
                sendMessage(player, "You can't take weapons and armour into the law rift.", null)
                return
            }
            COSMIC -> if (!isQuestComplete(player, Quests.LOST_CITY)) {
                sendMessage(player, "You need to have completed the Lost City quest in order to do that.", null)
                return
            }
            else -> {}
        }
        ruin?.let { player.properties.teleportLocation = it.end }
    }

    /**
     * Determines if the current altar is the Ourania altar.
     */
    fun isOurania(): Boolean = rune == null

    /**
     * Retrieves the talisman associated with the current altar.
     */
    fun getTalisman(): Talisman? = Talisman.values().find { it.name.equals(this.name, ignoreCase = true) }

    /**
     * Retrieves the tiara associated with the current altar.
     */
    fun getTiara(): Tiara? = Tiara.values().find { it.name.equals(this.name, ignoreCase = true) }
}
