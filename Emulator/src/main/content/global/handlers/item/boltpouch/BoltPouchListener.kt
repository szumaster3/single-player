package content.global.handlers.item.boltpouch

import content.global.skill.fletching.items.bolts.Bolt
import core.api.openInterface
import core.api.sendMessage
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.Components
import org.rs.consts.Items

class BoltPouchListener : InteractionListener {

    val bolts = Bolt.product.map { it.key }.toIntArray()

    override fun defineListeners() {

        /*
         * Handles opening bolt pouch interface.
         */

        on(Items.BOLT_POUCH_9433, IntType.ITEM, "open") { player, _ ->
            openInterface(player, Components.XBOWS_POUCH_433)
            return@on true
        }

        /*
         * Handles adding bolts to the pouch.
         */

        onUseWith(IntType.ITEM, bolts, Items.BOLT_POUCH_9433) { player, item, otherItem ->
            val pouchItem = if (item.id == Items.BOLT_POUCH_9433) item else otherItem
            val boltItem = if (pouchItem == item) otherItem else item

            val pouch = player.getAttribute("bolt_pouch", BoltPouch)

            if (!pouch.storeBolt(player, boltItem.asItem())) {
                sendMessage(player, "You don't have space to store that type of bolt.")
            }

            return@onUseWith true
        }
    }
}