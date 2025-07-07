package content.global.skill.construction.decoration.costumeroom

import core.api.*
import core.api.allInInventory
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.net.packet.OutgoingContext
import core.net.packet.PacketRepository
import core.net.packet.out.ContainerPacket
import org.rs.consts.Items

class StorageBoxInterface : InterfaceListener {
    private val INTERFACE = 467
    private val CONTAINER_COMPONENT = 164
    private val TOTAL_SLOTS = 30
    private val BUTTON_MORE = Items.MORE_10165
    private val BUTTON_BACK = Items.BACK_10166

    override fun defineInterfaceListeners() {
        on(INTERFACE) { player, _, _, buttonID, _, _ ->
            val typeName = getAttribute(player, "con:storage:type", null) as? String ?: return@on true
            val type = Storable.Type.valueOf(typeName.uppercase())
            handleStorageInteraction(player, buttonID, type)
            return@on true
        }
    }

    private fun getContainer(player: Player, type: Storable.Type) =
        player.getCostumeRoomState().containers.getOrPut(type) { StorageContainer() }

    private fun getRelevantItems(type: Storable.Type): List<Storable> {
        val trailTiers = listOf(
            Storable.Type.LOW_LEVEL_TRAILS,
            Storable.Type.MED_LEVEL_TRAILS,
            Storable.Type.HIGH_LEVEL_TRAILS
        )
        val magicTiers = listOf(
            Storable.Type.ONE_SET_OF_ARMOUR,
            Storable.Type.TWO_SETS_OF_ARMOUR,
            Storable.Type.THREE_SETS_OF_ARMOUR,
            Storable.Type.FOUR_SETS_OF_ARMOUR,
            Storable.Type.FIVE_SETS_OF_ARMOUR,
            Storable.Type.SIX_SETS_OF_ARMOUR
        )
        val armourTiers = listOf(
            Storable.Type.TWO_SETS_ARMOUR_CASE,
            Storable.Type.FOUR_SETS_ARMOUR_CASE,
            Storable.Type.ALL_SETS_ARMOUR_CASE
        )

        return Storable.values().filter {
            when (type) {
                // Treasures
                Storable.Type.LOW_LEVEL_TRAILS -> it.type == type
                Storable.Type.MED_LEVEL_TRAILS -> it.type in trailTiers.take(2)
                Storable.Type.HIGH_LEVEL_TRAILS -> it.type in trailTiers
                // Magic wardrobe
                Storable.Type.ONE_SET_OF_ARMOUR -> it.type == Storable.Type.ONE_SET_OF_ARMOUR
                Storable.Type.TWO_SETS_OF_ARMOUR -> it.type in magicTiers.take(2)
                Storable.Type.THREE_SETS_OF_ARMOUR -> it.type in magicTiers.take(3)
                Storable.Type.FOUR_SETS_OF_ARMOUR -> it.type in magicTiers.take(4)
                Storable.Type.FIVE_SETS_OF_ARMOUR -> it.type in magicTiers.take(5)
                Storable.Type.SIX_SETS_OF_ARMOUR -> it.type in magicTiers.take(6)
                Storable.Type.ALL_SETS_OF_ARMOUR -> it.type in magicTiers
                // Armour case
                Storable.Type.TWO_SETS_ARMOUR_CASE -> it.type in armourTiers.take(1)
                Storable.Type.FOUR_SETS_ARMOUR_CASE -> it.type in armourTiers.take(2)
                Storable.Type.ALL_SETS_ARMOUR_CASE -> it.type in armourTiers
                // Default
                else -> it.type == type
            }
        }
    }

    private fun handleStorageInteraction(player: Player, id: Int, type: Storable.Type) {
        val container = getContainer(player, type)
        val items = getRelevantItems(type)
        val start = container.currentPage * (TOTAL_SLOTS - 1)
        val pageItems = items.drop(start).take(TOTAL_SLOTS - 1).toMutableList<Any>()

        val hasNext = items.size > start + (TOTAL_SLOTS - 1)
        val hasPrev = container.currentPage > 0
        if (hasNext) pageItems.add("MORE")
        if (hasPrev) pageItems.add("BACK")

        if (id in 56..(56 + (TOTAL_SLOTS - 1) * 2) step 2) {
            when (val clicked = pageItems.getOrNull((id - 56) / 2)) {
                "MORE" -> container.nextPage()
                "BACK" -> container.prevPage()
                is Storable -> handleInteraction(player, container, clicked, type)
            }
            openInterface(player, INTERFACE)
            renderPage(player, type)
        }
    }

    private fun handleInteraction(player: Player, container: StorageContainer, item: Storable, type: Storable.Type) {
        val id = item.takeIds.firstOrNull() ?: item.displayId
        val boxName = if (type.name.contains("TRAILS")) "Treasure chest" else if(type.name.contains("SET_OF_ARMOUR")) "Magic wardrobe" else if(type.name.contains("ARMOUR_CASE")) "Armour case" else type.name.lowercase()

        when {
            container.hasItem(item) -> {
                if (freeSlots(player) <= 0) {
                    sendMessage(player, "You don't have enough inventory space.")
                    return
                }
                sendMessage(player, "You take the item from the $boxName box.")
                addItem(player, id, 1)
                container.withdraw(item)
            }

            allInInventory(player, id) -> {
                sendMessage(player, "You put the item into the $boxName box.")
                removeItem(player, Item(id))
                container.store(item)
            }

            else -> sendMessage(player, "That isn't currently stored in the $boxName box.")
        }
    }

    private fun renderPage(player: Player, type: Storable.Type) {
        val container = getContainer(player, type)
        val items = getRelevantItems(type)
        val stored = container.storedItems.toSet()
        val start = container.currentPage * (TOTAL_SLOTS - 1)
        val pageItems = items.drop(start).take(TOTAL_SLOTS - 1).toMutableList<Any>()

        val hasNext = items.size > start + (TOTAL_SLOTS - 1)
        val hasPrev = container.currentPage > 0
        if (hasNext) pageItems.add("MORE") else if (hasPrev) pageItems.add("BACK")

        val title = if (type.name.contains("TRAILS")) "Treasure chest" else if(type.name.contains("SET_OF_ARMOUR")) "Magic wardrobe" else if(type.name.contains("ARMOUR_CASE")) "Armour case" else type.name.lowercase()
            .replaceFirstChar(Char::titlecase) + " box"
        sendString(player, title, INTERFACE, 225)

        val itemsArray = Array<Item?>(TOTAL_SLOTS) { index ->
            when (val obj = pageItems.getOrNull(index)) {
                is Storable -> Item(obj.displayId)
                "MORE" -> Item(BUTTON_MORE)
                "BACK" -> Item(BUTTON_BACK)
                else -> null
            }
        }.filterNotNull().toTypedArray()

        PacketRepository.send(
            ContainerPacket::class.java,
            OutgoingContext.Container(player, INTERFACE, CONTAINER_COMPONENT, TOTAL_SLOTS, itemsArray, false)
        )

        repeat(TOTAL_SLOTS) { index ->
            val nameComponent = 55 + index * 2
            val iconComponent = 56 + index * 2
            val obj = pageItems.getOrNull(index)

            val (name, hidden) = when (obj) {
                is Storable -> getItemName(obj.takeIds.firstOrNull() ?: obj.displayId) to (obj !in stored)
                "MORE" -> "More..." to false
                "BACK" -> "Back..." to false
                else -> "" to true
            }

            sendString(player, name, INTERFACE, nameComponent)
            sendInterfaceConfig(player, INTERFACE, nameComponent, false)
            sendInterfaceConfig(player, INTERFACE, iconComponent, hidden)
        }
    }

    private fun openStorageInterface(player: Player, type: Storable.Type) {
        setAttribute(player, "con:storage:type", type.name)
        openInterface(player, INTERFACE)
        renderPage(player, type)
    }

    companion object {
        lateinit var instance: StorageBoxInterface
            private set

        /**
         * Open the storage box interface.
         *
         * @param player The player.
         * @param type The storage type.
         */
        fun openStorage(player: Player, type: Storable.Type) {
            instance.openStorageInterface(player, type)
        }
    }

    init {
        instance = this
    }
}
