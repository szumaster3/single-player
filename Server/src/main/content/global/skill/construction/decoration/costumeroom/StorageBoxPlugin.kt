package content.global.skill.construction.decoration.costumeroom

import core.api.animate
import core.api.playAudio
import core.api.replaceScenery
import core.cache.def.impl.SceneryDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.plugin.Initializable
import core.plugin.Plugin
import shared.consts.Animations
import shared.consts.Sounds
import shared.consts.Scenery as Obj

@Initializable
class StorageBoxPlugin : OptionHandler() {

    private val mappings =
        listOf(
            // Bookcase
            Config(intArrayOf(Obj.BOOKCASE_13597, Obj.BOOKCASE_13598, Obj.BOOKCASE_13599), searchable = { player, _ -> StorageBoxInterface.openStorage(player, StorableType.BOOK) }),
            Config(intArrayOf(Obj.OAK_CAPE_RACK_18766, Obj.TEAK_CAPE_RACK_18767, Obj.MAHOGANY_CAPE_RACK_18768, Obj.GILDED_CAPE_RACK_18769, Obj.MARBLE_CAPE_RACK_18770, Obj.MAGIC_CAPE_RACK_18771), searchable = { player, _ -> StorageBoxInterface.openStorage(player, StorableType.CAPE) }),

            // Fancy Dress
            Config(intArrayOf(Obj.FANCY_DRESS_BOX_18772, Obj.FANCY_DRESS_BOX_18774, Obj.FANCY_DRESS_BOX_18776), openable = true),
            Config(intArrayOf(Obj.FANCY_DRESS_BOX_18773, Obj.FANCY_DRESS_BOX_18775, Obj.FANCY_DRESS_BOX_18777), searchable = { player, _ -> StorageBoxInterface.openStorage(player, StorableType.FANCY) }, closable = true),

            // Toy Box
            Config(intArrayOf(Obj.TOY_BOX_18798, Obj.TOY_BOX_18800, Obj.TOY_BOX_18802), openable = true),
            Config(intArrayOf(Obj.TOY_BOX_18799, Obj.TOY_BOX_18801, Obj.TOY_BOX_18803), searchable = { player, _ -> StorageBoxInterface.openStorage(player, StorableType.TOY) }, closable = true),

            // Treasure Chest
            Config(intArrayOf(Obj.TREASURE_CHEST_18804, Obj.TREASURE_CHEST_18806, Obj.TREASURE_CHEST_18808), openable = true),
            Config(mapOf(Obj.TREASURE_CHEST_18805 to StorableType.LOW_LEVEL_TRAILS, Obj.TREASURE_CHEST_18807 to StorableType.MED_LEVEL_TRAILS, Obj.TREASURE_CHEST_18809 to StorableType.HIGH_LEVEL_TRAILS), closable = true),

            // Magic Wardrobe
            Config(intArrayOf(Obj.MAGIC_WARDROBE_18784, Obj.MAGIC_WARDROBE_18786, Obj.MAGIC_WARDROBE_18788, Obj.MAGIC_WARDROBE_18790, Obj.MAGIC_WARDROBE_18792, Obj.MAGIC_WARDROBE_18794, Obj.MAGIC_WARDROBE_18796), openable = true),
            Config(mapOf(Obj.MAGIC_WARDROBE_18785 to StorableType.ONE_SET_OF_ARMOUR, Obj.MAGIC_WARDROBE_18787 to StorableType.TWO_SETS_OF_ARMOUR, Obj.MAGIC_WARDROBE_18789 to StorableType.THREE_SETS_OF_ARMOUR, Obj.MAGIC_WARDROBE_18791 to StorableType.FOUR_SETS_OF_ARMOUR, Obj.MAGIC_WARDROBE_18793 to StorableType.FIVE_SETS_OF_ARMOUR, Obj.MAGIC_WARDROBE_18795 to StorableType.SIX_SETS_OF_ARMOUR, Obj.MAGIC_WARDROBE_18797 to StorableType.ALL_SETS_OF_ARMOUR), closable = true),

            // Armour Case
            Config(intArrayOf(Obj.ARMOUR_CASE_18778, Obj.ARMOUR_CASE_18780, Obj.ARMOUR_CASE_18782), openable = true),
            Config(mapOf(Obj.ARMOUR_CASE_18779 to StorableType.TWO_SETS_ARMOUR_CASE, Obj.ARMOUR_CASE_18781 to StorableType.FOUR_SETS_ARMOUR_CASE, Obj.ARMOUR_CASE_18783 to StorableType.ALL_SETS_ARMOUR_CASE), closable = true)
        )

    override fun newInstance(arg: Any?): Plugin<Any> {
        mappings.forEach { cfg ->
            cfg.ids.forEach { id ->
                val def = SceneryDefinition.forId(id)
                cfg.registerHandlers(def, this)
            }
        }
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        val obj = node as Scenery
        mappings.firstOrNull { obj.id in it.ids }?.handle(player, obj, option)
        return true
    }

    private data class Config(val ids: IntArray = intArrayOf(), val searchable: ((Player, Int) -> Unit)? = null, val openable: Boolean = false, val closable: Boolean = false) {

        constructor(idToType: Map<Int, StorableType>, closable: Boolean = false) : this(
            ids = idToType.keys.toIntArray(),
            searchable = { player, id -> StorageBoxInterface.openStorage(player, idToType.getValue(id)) },
            closable = closable
        )

        fun registerHandlers(def: SceneryDefinition, handler: OptionHandler) {
            searchable?.let { def.handlers["option:search"] = handler }
            if (openable) def.handlers["option:open"] = handler
            if (closable) def.handlers["option:close"] = handler
        }

        fun handle(player: Player, obj: Scenery, option: String) {
            when (option) {
                "search" -> searchable?.invoke(player, obj.id)
                "open" ->
                    if (openable) {
                        playAudio(player, Sounds.CHEST_OPEN_52)
                        animate(player, Animations.OPEN_CHEST_536)
                        replaceScenery(obj, obj.id + 1, -1)
                    }
                "close" ->
                    if (closable) {
                        playAudio(player, Sounds.CHEST_CLOSE_51)
                        animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                        replaceScenery(obj, obj.id - 1, -1)
                    }
            }
        }
    }
}
