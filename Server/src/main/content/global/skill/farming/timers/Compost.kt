package content.global.skill.farming.timers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import content.global.skill.farming.CompostBin
import content.global.skill.farming.CompostBins
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.system.timer.PersistTimer

class Compost : PersistTimer(500, "farming:compost", isSoft = true) {
    private val binMap = HashMap<CompostBins, CompostBin>()
    lateinit var player: Player

    override fun onRegister(entity: Entity) {
        player = (entity as? Player)!!
    }

    override fun getInitialRunDelay(): Int = 1

    override fun run(entity: Entity): Boolean {
        val removeList = ArrayList<CompostBins>()
        for ((cBin, bin) in binMap) {
            if (bin.isReady() && !bin.isFinished) {
                bin.finish()
            } else if (bin.isDefaultState()) {
                removeList.add(cBin)
            }
        }
        removeList.forEach { binMap.remove(it) }
        removeList.clear()

        return binMap.isNotEmpty()
    }

    fun getBin(bin: CompostBins): CompostBin = binMap[bin] ?: (CompostBin(player, bin).also { binMap[bin] = it })

    fun getBins(): MutableCollection<CompostBin> = binMap.values

    override fun save(
        root: JsonObject,
        entity: Entity,
    ) {
        val bins = JsonArray()
        for ((key, bin) in binMap) {
            val b = JsonObject()
            b.addProperty("bin-ordinal", key.ordinal)
            bin.save(b)
            bins.add(b)
        }
        root.add("bins", bins)
    }

    override fun parse(
        root: JsonObject,
        entity: Entity,
    ) {
        val binsArray = root.getAsJsonArray("bins") ?: return
        for (element in binsArray) {
            val bin = element.asJsonObject
            val binOrdinal = bin.get("bin-ordinal").asInt
            val cBin = CompostBins.values()[binOrdinal]
            val b = CompostBin((entity as? Player)!!, cBin).also { binMap[cBin] = it }
            b.parse(bin.getAsJsonObject("binData"))
        }
    }

}
