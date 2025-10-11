package core.game.global.presets

import core.api.sendMessage
import core.game.node.entity.player.Player
import java.util.*

class PresetManager {
    private val player: Player? = null

    private val currentPresets: MutableMap<String, Preset> = HashMap()

    fun storeSet(nameKey: String): PresetManager {
        var name = nameKey
        val set = currentPresets[name]
        if (set != null) {
            player!!.sendMessage("You were unable to store the set $name as it already exists.")
        }
        name = name.lowercase(Locale.getDefault())
        val equipment = ArrayList(listOf(*player!!.equipment.event.items))
        val inventory = ArrayList(listOf(*player.inventory.event.items))
        currentPresets[name] = Preset(equipment, inventory)
        return this
    }

    fun printAvailableSetups() {
        val size = currentPresets.size
        player!!.sendMessage("You have used " + size + "/" + maxSize() + " available setups.")
        if (size > 0) {
            sendMessage(player, "<col=ff0000>Your available setups are:")
            for (key in currentPresets.keys) {
                sendMessage(player, key)
            }
        }
    }

    fun maxSize(): Int = 6
}
