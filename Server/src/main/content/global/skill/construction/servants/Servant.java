package content.global.skill.construction.servants;

import com.google.gson.JsonObject;
import core.game.node.entity.npc.NPC;
import core.game.node.item.Item;

import java.nio.ByteBuffer;

public final class Servant extends NPC {

    private final ServantType type;
    private Item item;
    private int uses;
    private boolean greet;

    public Servant(ServantType type) {
        super(type.getId());
        this.type = type;
    }

    public void save(ByteBuffer buffer) {
        buffer.put((byte) type.ordinal());
        buffer.putShort((short) uses);
        if (item == null) {
            buffer.putShort((short) -1);
        } else {
            buffer.putShort((short) item.getId());
            buffer.putInt(item.getAmount());
        }
        buffer.put((byte) (greet ? 1 : 0));
    }

    /**
     * Parse servant from Gson JsonObject.
     */
    public static Servant parse(JsonObject data) {
        int typeIndex = data.get("type").getAsInt();
        Servant servant = new Servant(ServantType.values()[typeIndex]);
        servant.uses = data.get("uses").getAsInt();

        if (data.has("item") && !data.get("item").isJsonNull()) {
            JsonObject itemObj = data.getAsJsonObject("item");
            int id = itemObj.get("id").getAsInt();
            int amount = itemObj.get("amount").getAsInt();
            servant.item = new Item(id, amount);
        }

        servant.greet = data.get("greet").getAsBoolean();

        return servant;
    }

    public static Servant parse(ByteBuffer buffer) {
        int type = buffer.get();
        if (type == -1) {
            return null;
        }
        Servant servant = new Servant(ServantType.values()[type]);
        servant.uses = buffer.getShort() & 0xFFFF;
        int itemId = buffer.getShort() & 0xFFFF;
        if ((short) itemId != -1) {
            servant.item = new Item(itemId, buffer.getInt());
        }
        servant.greet = buffer.get() == 1;
        return servant;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public boolean isGreet() {
        return greet;
    }

    public void setGreet(boolean greet) {
        this.greet = greet;
    }

    public ServantType getType() {
        return type;
    }
}
