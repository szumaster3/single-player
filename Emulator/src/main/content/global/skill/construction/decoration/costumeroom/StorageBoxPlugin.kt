package content.global.skill.construction.decoration.costumeroom

import core.api.*
import core.cache.def.impl.SceneryDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Animations
import org.rs.consts.Scenery as Obj

@Initializable
class StorageBoxPlugin : OptionHandler() {
    private val bookcaseClosed = intArrayOf(Obj.BOOKCASE_13597, Obj.BOOKCASE_13598, Obj.BOOKCASE_13599)
    private val capeRack = intArrayOf(Obj.OAK_CAPE_RACK_18766, Obj.TEAK_CAPE_RACK_18767, Obj.MAHOGANY_CAPE_RACK_18768, Obj.GILDED_CAPE_RACK_18769, Obj.MARBLE_CAPE_RACK_18770, Obj.MAGIC_CAPE_RACK_18771)
    private val fancyDressClosed = intArrayOf(Obj.FANCY_DRESS_BOX_18772, Obj.FANCY_DRESS_BOX_18774, Obj.FANCY_DRESS_BOX_18776)
    private val fancyDressOpen = intArrayOf(Obj.FANCY_DRESS_BOX_18773, Obj.FANCY_DRESS_BOX_18775, Obj.FANCY_DRESS_BOX_18777)
    private val toyBoxClosed = intArrayOf(Obj.TOY_BOX_18798, Obj.TOY_BOX_18800, Obj.TOY_BOX_18802)
    private val toyBoxOpen = intArrayOf(Obj.TOY_BOX_18799, Obj.TOY_BOX_18801, Obj.TOY_BOX_18803)

    override fun newInstance(arg: Any?): Plugin<Any> {
        for (id in bookcaseClosed) {
            SceneryDefinition.forId(id).handlers["option:search"] = this
        }
        for (id in capeRack) {
            SceneryDefinition.forId(id).handlers["option:search"] = this
        }
        for (id in fancyDressClosed) {
            SceneryDefinition.forId(id).handlers["option:open"] = this
        }
        for (id in fancyDressOpen) {
            SceneryDefinition.forId(id).handlers["option:search"] = this
            SceneryDefinition.forId(id).handlers["option:close"] = this
        }
        for (id in toyBoxClosed) {
            SceneryDefinition.forId(id).handlers["option:open"] = this
        }
        for (id in toyBoxOpen) {
            SceneryDefinition.forId(id).handlers["option:search"] = this
            SceneryDefinition.forId(id).handlers["option:close"] = this
        }
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        val obj = node as Scenery

        when (obj.id) {
            in bookcaseClosed -> {
                StorageBoxInterface.openStorage(player, CostumeRoomStorage.Type.BOOK)
            }

            in capeRack -> {
                StorageBoxInterface.openStorage(player, CostumeRoomStorage.Type.CAPE)
            }

            in fancyDressClosed -> {
                if (option == "open") {
                    animate(player, Animations.OPEN_CHEST_536)
                    replaceScenery(obj.asScenery(), obj.id + 1, -1)
                }
            }

            in fancyDressOpen -> {
                when (option) {
                    "close" -> {
                        animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                        replaceScenery(obj.asScenery(), obj.id - 1, -1)
                    }
                    "search" -> StorageBoxInterface.openStorage(player, CostumeRoomStorage.Type.FANCY)
                }
            }

            in toyBoxClosed -> {
                if (option == "open") {
                    animate(player, Animations.OPEN_CHEST_536)
                    replaceScenery(obj.asScenery(), obj.id + 1, -1)
                }
            }

            in toyBoxOpen -> {
                when (option) {
                    "close" -> {
                        animate(player, Animations.HUMAN_CLOSE_CHEST_538)
                        replaceScenery(obj.asScenery(), obj.id - 1, -1)
                    }
                    "search" -> StorageBoxInterface.openStorage(player, CostumeRoomStorage.Type.TOY)
                }
            }
        }

        return true
    }
}