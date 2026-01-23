package content.global.skill.construction.decoration.costumeroom

import core.api.*
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
class StorageBoxHandler : OptionHandler() {

    private data class StorageBoxInfo(
        val objectIds: IntArray,
        val storableType: StorableType? = null,
        val tier: Int = 0
    )

    private val allBoxes = listOf(
        // BOOKCASE
        StorageBoxInfo(intArrayOf(Obj.BOOKCASE_13597), StorableType.BOOK, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.BOOKCASE_13598), StorableType.BOOK, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.BOOKCASE_13599), StorableType.BOOK, tier = 0),
        // CAPE RACK
        StorageBoxInfo(intArrayOf(Obj.OAK_CAPE_RACK_18766),      StorableType.CAPE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.TEAK_CAPE_RACK_18767),     StorableType.CAPE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAHOGANY_CAPE_RACK_18768), StorableType.CAPE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.GILDED_CAPE_RACK_18769),   StorableType.CAPE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MARBLE_CAPE_RACK_18770),   StorableType.CAPE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_CAPE_RACK_18771),    StorableType.CAPE, tier = 0),
        // FANCY BOX
        StorageBoxInfo(intArrayOf(Obj.FANCY_DRESS_BOX_18772), StorableType.FANCY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.FANCY_DRESS_BOX_18773), StorableType.FANCY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.FANCY_DRESS_BOX_18774), StorableType.FANCY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.FANCY_DRESS_BOX_18775), StorableType.FANCY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.FANCY_DRESS_BOX_18776), StorableType.FANCY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.FANCY_DRESS_BOX_18777), StorableType.FANCY, tier = 0),
        // TOY BOX

        StorageBoxInfo(intArrayOf(Obj.TOY_BOX_18798), StorableType.TOY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.TOY_BOX_18799), StorableType.TOY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.TOY_BOX_18800), StorableType.TOY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.TOY_BOX_18801), StorableType.TOY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.TOY_BOX_18802), StorableType.TOY, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.TOY_BOX_18803), StorableType.TOY, tier = 0),
        // TREASURE CHEST
        StorageBoxInfo(
            intArrayOf(
                Obj.TREASURE_CHEST_18804,
                Obj.TREASURE_CHEST_18805,
                Obj.TREASURE_CHEST_18806,
                Obj.TREASURE_CHEST_18807,
                Obj.TREASURE_CHEST_18808,
                Obj.TREASURE_CHEST_18809
            ),
            StorableType.TRAILS
        ),
        // MAGIC WARDROBE
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18784), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18785), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18786), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18787), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18788), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18789), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18790), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18791), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18792), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18793), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18794), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18795), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18796), StorableType.ARMOUR, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.MAGIC_WARDROBE_18797), StorableType.ARMOUR, tier = 0),
        // ARMOUR CASE
        StorageBoxInfo(intArrayOf(Obj.ARMOUR_CASE_18778), StorableType.ARMOUR_CASE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.ARMOUR_CASE_18779), StorableType.ARMOUR_CASE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.ARMOUR_CASE_18780), StorableType.ARMOUR_CASE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.ARMOUR_CASE_18781), StorableType.ARMOUR_CASE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.ARMOUR_CASE_18782), StorableType.ARMOUR_CASE, tier = 0),
        StorageBoxInfo(intArrayOf(Obj.ARMOUR_CASE_18783), StorableType.ARMOUR_CASE, tier = 0)
    )

    override fun newInstance(arg: Any?): Plugin<Any> {
        allBoxes.forEach { box ->
            box.objectIds.forEach { id ->
                SceneryDefinition.forId(id)?.let { def ->
                    if (box.storableType != null) def.handlers["option:search"] = this
                    def.handlers["option:open"] = this
                    def.handlers["option:close"] = this
                }
            }
        }
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        val obj = node as Scenery
        val box = allBoxes.firstOrNull { obj.id in it.objectIds } ?: return true

        when (option) {
            "search" -> {
                if (box.objectIds.all { it in arrayOf(
                        Obj.TREASURE_CHEST_18804, Obj.TREASURE_CHEST_18805, Obj.TREASURE_CHEST_18806,
                        Obj.TREASURE_CHEST_18807, Obj.TREASURE_CHEST_18808, Obj.TREASURE_CHEST_18809
                    )
                    }) {
                    handleTreasureChest(player, obj.id)
                } else {
                    box.storableType?.let { type ->
                        val container = player.getCostumeRoomState().getContainer(type)
                        container.setTier(type, box.tier)
                        StorageBoxInterface.openStorage(player, type)
                    }
                }
            }
            "open"  -> openBox(player, obj)
            "close" -> closeBox(player, obj)
        }
        return true
    }

    private fun handleTreasureChest(player: Player, objId: Int) {
        when (objId) {
            Obj.TREASURE_CHEST_18804, Obj.TREASURE_CHEST_18805, Obj.TREASURE_CHEST_18806 -> {
                setTitle(player, 2)
                sendOptions(player, "Take which level of Treasure Trail reward?", "Level 1", "Level 2")
                addDialogueAction(player) { p, button ->
                    val tier = when (button) {
                        2 -> 0
                        3 -> 1
                        else -> null
                    }
                    tier?.let {
                        val container = p.getCostumeRoomState().getContainer(StorableType.TRAILS)
                        container.setTier(StorableType.TRAILS, it)
                        StorageBoxInterface.openStorage(p, StorableType.TRAILS)
                    }
                }
            }
            Obj.TREASURE_CHEST_18807, Obj.TREASURE_CHEST_18808, Obj.TREASURE_CHEST_18809 -> {
                setTitle(player, 3)
                sendOptions(player, "Take which level of Treasure Trail reward?", "Level 1", "Level 2", "Level 3")
                addDialogueAction(player) { p, button ->
                    val tier = when (button) {
                        2 -> 0
                        3 -> 1
                        4 -> 2
                        else -> null
                    }
                    tier?.let {
                        val container = p.getCostumeRoomState().getContainer(StorableType.TRAILS)
                        container.setTier(StorableType.TRAILS, it)
                        StorageBoxInterface.openStorage(p, StorableType.TRAILS)
                    }
                }
            }
        }
    }

    private fun openBox(player: Player, obj: Scenery) {
        playAudio(player, Sounds.CHEST_OPEN_52)
        animate(player, Animations.HUMAN_OPEN_CHEST_536)
        replaceScenery(obj, obj.id + 1, -1)
    }

    private fun closeBox(player: Player, obj: Scenery) {
        playAudio(player, Sounds.CHEST_CLOSE_51)
        animate(player, Animations.HUMAN_CLOSE_CHEST_538)
        replaceScenery(obj, obj.id - 1, -1)
    }
}
