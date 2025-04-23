package content.global.handlers.item.boltpouch

import core.api.*
import core.api.ui.restoreTabs
import core.game.interaction.InterfaceListener
import org.rs.consts.Components

class BoltPouchInterface : InterfaceListener {

    override fun defineInterfaceListeners() {

        /*
         * Handles the visual layer of the pouch itself.
         */

        onOpen(Components.XBOWS_POUCH_433) { player, _ ->
            // Handles showing amount of bolts on each slot.
            val amountSlots = intArrayOf(20, 21, 22, 23, 24)
            for (i in amountSlots.indices) {
                val amount = BoltPouch.getAmount(player, i)
                sendString(player, amount.toString(), Components.XBOWS_POUCH_433, amountSlots[i])
            }
            // Handles showing name of bolts on each slot.
            val boltNameSlots = intArrayOf(25, 26, 27, 28, 29)
            for (i in boltNameSlots.indices) {
                val boltId = BoltPouch.getBolt(player, i)
                val boltName = if (boltId != -1) getItemName(boltId) else "Nothing"
                sendString(player, boltName, Components.XBOWS_POUCH_433, boltNameSlots[i])
            }
            // Handles showing item sprite of bolts on each slot.
            val spriteSlots = intArrayOf(2, 6, 10, 14, 18)
            for (i in spriteSlots.indices) {
                val boltId = BoltPouch.getBolt(player, i)
                sendItemOnInterface(player, Components.XBOWS_POUCH_433, spriteSlots[i], item = boltId)
            }
            return@onOpen true
        }

        /*
         * Handles interaction with pouch.
         */

        on(Components.XBOWS_POUCH_433) { player, _, _, _, slot, _ ->
            val wieldBoltSlots = intArrayOf(3, 7, 11, 15)
            val removeBoltSlots = intArrayOf(4, 8, 12, 16)
            val unwieldBoltSlot = 19

            when (slot) {
                in wieldBoltSlots -> {
                    val pouchSlot = wieldBoltSlots.indexOf(slot)
                    if (!BoltPouch.hasBolts(player, pouchSlot)) {
                        sendMessage(player, "There's nothing in that slot to wield.")
                        return@on true
                    }
                    if (freeSlots(player) == 0) {
                        sendMessage(player, "You don't have enough space in your inventory to do that.")
                        return@on true
                    }
                    BoltPouch.wield(player, pouchSlot)
                    sendMessage(player, "You wield some bolts from your bolt pouch.")
                }

                in removeBoltSlots -> {
                    val pouchSlot = removeBoltSlots.indexOf(slot)
                    if (!BoltPouch.hasBolts(player, pouchSlot)) {
                        sendMessage(player, "There's nothing to remove from that slot.")
                        return@on true
                    }
                    if (freeSlots(player) == 0) {
                        sendMessage(player, "You don't have enough space in your inventory to do that.")
                        return@on true
                    }
                    BoltPouch.removeToInventory(player, pouchSlot)
                    val ordinal = arrayOf("first", "second", "third", "fourth")[pouchSlot]
                    sendMessage(player, "You remove all the bolts from the $ordinal slot of your bolt pouch.")
                }

                unwieldBoltSlot -> {
                    if (player.equipment.getNew(EquipmentSlot.AMMO.ordinal) == null) {
                        sendMessage(player, "You're not wielding anything.")
                        return@on true
                    }
                    if (freeSlots(player) == 0) {
                        sendMessage(player, "You don't have enough space in your inventory to do that.")
                        return@on true
                    }
                    BoltPouch.unwield(player)
                    sendMessage(player, "You place the items you were wielding into your pack.")
                }
            }

            return@on true
        }

        onClose(Components.XBOWS_POUCH_433) { player, _ ->
            restoreTabs(player)
            return@onClose true
        }
    }
}
