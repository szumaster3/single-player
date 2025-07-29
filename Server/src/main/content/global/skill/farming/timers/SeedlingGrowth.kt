package content.global.skill.farming.timers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import content.global.skill.farming.Seedling
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.system.timer.PersistTimer
import java.util.concurrent.TimeUnit

class SeedlingGrowth : PersistTimer(1, "farming:seedling", isSoft = true) {
    val seedlings = ArrayList<Seedling>()
    lateinit var player: Player

    fun addSeedling(seedling: Int) {
        seedlings.add(
            Seedling(
                seedling,
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5),
                seedling + if (seedling > 5400) 8 else 6,
            ),
        )
    }

    override fun onRegister(entity: Entity) {
        player = (entity as? Player)!!
    }

    override fun run(entity: Entity): Boolean {
        val removeList = ArrayList<Seedling>()
        for (seed in seedlings) {
            if (System.currentTimeMillis() > seed.TTL) {
                val inInventory = player.inventory.get(Item(seed.id))
                if (inInventory != null) {
                    player.inventory.replace(Item(seed.sapling), inInventory.slot)
                    removeList.add(seed)
                } else {
                    val inBank = player.bank.get(Item(seed.id))
                    if (inBank == null) {
                        removeList.add(seed)
                    } else {
                        player.bank.remove(Item(inBank.id, 1))
                        player.bank.add(Item(seed.sapling))
                        removeList.add(seed)
                    }
                }
            }
        }
        seedlings.removeAll(removeList)
        return seedlings.isNotEmpty()
    }

    override fun save(
        root: JsonObject,
        entity: Entity,
    ) {
        val seedArray = JsonArray()
        for (s in seedlings) {
            val seed = JsonObject()
            seed.addProperty("id", s.id)
            seed.addProperty("ttl", s.TTL)
            seed.addProperty("sapling", s.sapling)
            seedArray.add(seed)
        }
        root.add("seedlings", seedArray)
    }

    override fun parse(
        root: JsonObject,
        entity: Entity,
    ) {
        val seedlingsArray = root.getAsJsonArray("seedlings") ?: return
        for (element in seedlingsArray) {
            val s = element.asJsonObject
            val id = s.get("id").asInt
            val ttl = s.get("ttl").asLong
            val sapling = s.get("sapling").asInt
            seedlings.add(Seedling(id, ttl, sapling))
        }
    }
}
