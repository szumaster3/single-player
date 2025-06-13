package content.global.skill.construction.storage

import core.api.*
import core.api.item.allInInventory
import core.api.ui.sendInterfaceConfig
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import org.rs.consts.Items

private data class StorageInterface(
    val attribute: String,
    val validIds: List<Int>,
    val handler: (Int) -> Unit
)

