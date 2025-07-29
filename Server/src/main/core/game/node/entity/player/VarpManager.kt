package core.game.node.entity.player

import com.google.gson.JsonArray
import com.google.gson.JsonObject

class VarpManager(val player: Player) {

    fun save(root: JsonObject) {
    }

    fun parse(data: JsonArray) {
        for (varpElem in data) {
            val vobj = varpElem.asJsonObject
            val index = vobj.get("index").asInt
            val bits = vobj.getAsJsonArray("bitArray")
            var total = 0
            for (vbitElem in bits) {
                val varbit = vbitElem.asJsonObject
                val offset = varbit.get("offset").asInt
                val value = varbit.get("value").asInt
                total = total or (value shl offset)
            }
            player.varpMap[index] = total
            player.saveVarp[index] = true
        }
    }
}
