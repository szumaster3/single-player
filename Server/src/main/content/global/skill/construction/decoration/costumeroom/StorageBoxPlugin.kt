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

    private val configs =
        listOf(
            // Bookcase
            Config.fromIds(Obj.BOOKCASE_13597, Obj.BOOKCASE_13598, Obj.BOOKCASE_13599) { player, _
                ->
                StorageBoxInterface.openStorage(player, StorableType.BOOK)
            },

            // Cape racks
            Config.fromIds(
                Obj.OAK_CAPE_RACK_18766,
                Obj.TEAK_CAPE_RACK_18767,
                Obj.MAHOGANY_CAPE_RACK_18768,
                Obj.GILDED_CAPE_RACK_18769,
                Obj.MARBLE_CAPE_RACK_18770,
                Obj.MAGIC_CAPE_RACK_18771
            ) { player, _ ->
                StorageBoxInterface.openStorage(player, StorableType.CAPE)
            },

            // Fancy Dress Box
            Config.fromIds(
                Obj.FANCY_DRESS_BOX_18772,
                Obj.FANCY_DRESS_BOX_18774,
                Obj.FANCY_DRESS_BOX_18776,
                openable = true
            ),
            Config.fromIds(
                Obj.FANCY_DRESS_BOX_18773,
                Obj.FANCY_DRESS_BOX_18775,
                Obj.FANCY_DRESS_BOX_18777,
                closable = true
            ) { player, _ ->
                StorageBoxInterface.openStorage(player, StorableType.FANCY)
            },

            // Toy Box
            Config.fromIds(
                Obj.TOY_BOX_18798,
                Obj.TOY_BOX_18800,
                Obj.TOY_BOX_18802,
                openable = true
            ),
            Config.fromIds(
                Obj.TOY_BOX_18799,
                Obj.TOY_BOX_18801,
                Obj.TOY_BOX_18803,
                closable = true
            ) { player, _ ->
                StorageBoxInterface.openStorage(player, StorableType.TOY)
            },

            // Treasure Chest
            Config.fromMap(
                mapOf(
                    Obj.TREASURE_CHEST_18805 to StorableType.LOW_LEVEL_TRAILS,
                    Obj.TREASURE_CHEST_18807 to StorableType.MED_LEVEL_TRAILS,
                    Obj.TREASURE_CHEST_18809 to StorableType.HIGH_LEVEL_TRAILS
                ),
                openableIds =
                intArrayOf(
                    Obj.TREASURE_CHEST_18804,
                    Obj.TREASURE_CHEST_18806,
                    Obj.TREASURE_CHEST_18808
                )
            ),

            // Magic Wardrobe
            Config.fromMap(
                mapOf(
                    Obj.MAGIC_WARDROBE_18785 to StorableType.ONE_SET_OF_ARMOUR,
                    Obj.MAGIC_WARDROBE_18787 to StorableType.TWO_SETS_OF_ARMOUR,
                    Obj.MAGIC_WARDROBE_18789 to StorableType.THREE_SETS_OF_ARMOUR,
                    Obj.MAGIC_WARDROBE_18791 to StorableType.FOUR_SETS_OF_ARMOUR,
                    Obj.MAGIC_WARDROBE_18793 to StorableType.FIVE_SETS_OF_ARMOUR,
                    Obj.MAGIC_WARDROBE_18795 to StorableType.SIX_SETS_OF_ARMOUR,
                    Obj.MAGIC_WARDROBE_18797 to StorableType.ALL_SETS_OF_ARMOUR
                ),
                openableIds =
                intArrayOf(
                    Obj.MAGIC_WARDROBE_18784,
                    Obj.MAGIC_WARDROBE_18786,
                    Obj.MAGIC_WARDROBE_18788,
                    Obj.MAGIC_WARDROBE_18790,
                    Obj.MAGIC_WARDROBE_18792,
                    Obj.MAGIC_WARDROBE_18794,
                    Obj.MAGIC_WARDROBE_18796
                )
            ),

            // Armour Case
            Config.fromMap(
                mapOf(
                    Obj.ARMOUR_CASE_18779 to StorableType.TWO_SETS_ARMOUR_CASE,
                    Obj.ARMOUR_CASE_18781 to StorableType.FOUR_SETS_ARMOUR_CASE,
                    Obj.ARMOUR_CASE_18783 to StorableType.ALL_SETS_ARMOUR_CASE
                ),
                openableIds =
                intArrayOf(Obj.ARMOUR_CASE_18778, Obj.ARMOUR_CASE_18780, Obj.ARMOUR_CASE_18782)
            )
        )

    override fun newInstance(arg: Any?): Plugin<Any> {
        configs.forEach { config ->
            config.ids.forEach { id ->
                SceneryDefinition.forId(id)?.let { def -> config.registerHandlers(def, this) }
            }
        }
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        val obj = node as Scenery
        configs.firstOrNull { obj.id in it.ids }?.handle(player, obj, option)
        return true
    }

    private data class Config(
        val ids: IntArray,
        val searchable: ((Player, Int) -> Unit)? = null,
        val openable: Boolean = false,
        val closable: Boolean = false
    ) {

        companion object {
            fun fromIds(
                vararg ids: Int,
                openable: Boolean = false,
                closable: Boolean = false,
                searchable: ((Player, Int) -> Unit)? = null
            ) = Config(ids = ids, searchable = searchable, openable = openable, closable = closable)

            fun fromMap(
                idToType: Map<Int, StorableType>,
                openableIds: IntArray = intArrayOf(),
                closable: Boolean = true
            ) =
                Config(
                    ids = idToType.keys.toIntArray() + openableIds,
                    searchable = { player, id ->
                        idToType[id]?.let { StorageBoxInterface.openStorage(player, it) }
                    },
                    openable = openableIds.isNotEmpty(),
                    closable = closable
                )
        }

        fun registerHandlers(def: SceneryDefinition, handler: OptionHandler) {
            searchable?.let { def.handlers["option:search"] = handler }
            if (openable) def.handlers["option:open"] = handler
            if (closable) def.handlers["option:close"] = handler
        }

        fun handle(player: Player, obj: Scenery, option: String) {
            when (option) {
                "search" -> searchable?.invoke(player, obj.id)
                "open" -> if (openable) open(player, obj)
                "close" -> if (closable) close(player, obj)
            }
        }

        private fun open(player: Player, obj: Scenery) {
            playAudio(player, Sounds.CHEST_OPEN_52)
            animate(player, Animations.HUMAN_OPEN_CHEST_536)
            replaceScenery(obj, obj.id + 1, -1)
        }

        private fun close(player: Player, obj: Scenery) {
            playAudio(player, Sounds.CHEST_CLOSE_51)
            animate(player, Animations.HUMAN_CLOSE_CHEST_538)
            replaceScenery(obj, obj.id - 1, -1)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Config

            if (!ids.contentEquals(other.ids)) return false
            if (searchable != other.searchable) return false
            if (openable != other.openable) return false
            if (closable != other.closable) return false

            return true
        }

        override fun hashCode(): Int {
            var result = ids.contentHashCode()
            result = 31 * result + (searchable?.hashCode() ?: 0)
            result = 31 * result + openable.hashCode()
            result = 31 * result + closable.hashCode()
            return result
        }
    }
}
