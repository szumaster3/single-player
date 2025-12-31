package core.cache.def;

import core.cache.Cache;
import core.cache.type.CacheIndex;
import core.util.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that loads item/model information from the cache.
 *
 * @author Graham
 * @author `Discardedx2
 */
public class ItemDefinition {

    private String name;

    private int inventoryModelId;
    private int modelZoom;
    private int modelRotation1;
    private int modelRotation2;
    private int modelOffset1;
    private int modelOffset2;

    private int stackable;
    private int value;
    private boolean membersOnly;

    private int maleWearModel1;
    private int maleWearModel2;
    private int femaleWearModel1;
    private int femaleWearModel2;

    private String[] groundOptions;
    private String[] inventoryOptions;

    private short[] originalModelColors;
    private short[] modifiedModelColors;
    private short[] textureColour1;
    private short[] textureColour2;
    private boolean unnoted;

    private int colourEquip1;
    private int colourEquip2;
    private int notedId;
    private int notedTemplateId;
    private int[] stackableIds;
    private int[] stackableAmounts;
    private int teamId;
    private int lendId;
    private int lendTemplateId;
    private Map<Integer, Object> params;

    /**
     * Decodes an {@link ItemDefinition} from the provided {@link ByteBuffer}.
     *
     * @param buffer the {@link ByteBuffer} containing the encoded item definition data.
     *               The buffer's position should be at the start of the definition.
     * @return a new {@link ItemDefinition} instance populated with the decoded values.
     * @throws IllegalArgumentException if an unknown or invalid opcode is encountered.
     */
    public static ItemDefinition decode(ByteBuffer buffer) {
        ItemDefinition def = new ItemDefinition();
        def.groundOptions = new String[]{null, null, null, null, null};
        def.inventoryOptions = new String[]{null, null, null, null, null,};
        while (true) {
            int opcode = buffer.get() & 0xFF;
            if (opcode == 0) break;
            if (opcode == 1) def.inventoryModelId = buffer.getShort() & 0xFFFFF;
            else if (opcode == 2) def.name = ByteBufferUtils.getJagexString(buffer);
            else if (opcode == 4) def.modelZoom = buffer.getShort() & 0xFFFFF;
            else if (opcode == 5) def.modelRotation1 = buffer.getShort() & 0xFFFFF;
            else if (opcode == 6) def.modelRotation2 = buffer.getShort() & 0xFFFFF;
            else if (opcode == 7) {
                def.modelOffset1 = buffer.getShort() & 0xFFFFF;
                if (def.modelOffset1 > 32767) def.modelOffset1 -= 65536;
                def.modelOffset1 <<= 0;
            } else if (opcode == 8) {
                def.modelOffset2 = buffer.getShort() & 0xFFFFF;
                if (def.modelOffset2 > 32767) def.modelOffset2 -= 65536;
                def.modelOffset2 <<= 0;
            } else if (opcode == 11) def.stackable = 1;
            else if (opcode == 12) def.value = buffer.getInt();
            else if (opcode == 16) def.membersOnly = true;
            else if (opcode == 23) def.maleWearModel1 = buffer.getShort() & 0xFFFFF;
            else if (opcode == 24) def.femaleWearModel1 = buffer.getShort() & 0xFFFFF;
            else if (opcode == 25) def.maleWearModel2 = buffer.getShort() & 0xFFFFF;
            else if (opcode == 26) def.femaleWearModel2 = buffer.getShort() & 0xFFFFF;
            else if (opcode >= 30 && opcode < 35)
                def.groundOptions[opcode - 30] = ByteBufferUtils.getJagexString(buffer);
            else if (opcode >= 35 && opcode < 40)
                def.inventoryOptions[opcode - 35] = ByteBufferUtils.getJagexString(buffer);
            else if (opcode == 40) {
                int length = buffer.get() & 0xFF;
                def.originalModelColors = new short[length];
                def.modifiedModelColors = new short[length];
                for (int index = 0; index < length; index++) {
                    def.originalModelColors[index] = (short) (buffer.getShort() & 0xFFFFF);
                    def.modifiedModelColors[index] = (short) (buffer.getShort() & 0xFFFFF);
                }
            } else if (opcode == 41) {
                int length = buffer.get() & 0xFF;
                def.textureColour1 = new short[length];
                def.textureColour2 = new short[length];
                for (int index = 0; index < length; index++) {
                    def.textureColour1[index] = (short) (buffer.getShort() & 0xFFFFF);
                    def.textureColour2[index] = (short) (buffer.getShort() & 0xFFFFF);
                }
            } else if (opcode == 42) {
                int length = buffer.get() & 0xFF;
                for (int index = 0; index < length; index++) {
                    int i = buffer.get();
                }
            } else if (opcode == 65) {
                def.unnoted = true;
            } else if (opcode == 78) {
                def.colourEquip1 = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 79) {
                def.colourEquip2 = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 90) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 91) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 92) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 93) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 95) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 96) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 97) {
                def.notedId = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 98) {
                def.notedTemplateId = buffer.getShort() & 0xFFFFF;
            } else if (opcode >= 100 && opcode < 110) {
                if (def.stackableIds == null) {
                    def.stackableIds = new int[10];
                    def.stackableAmounts = new int[10];
                }
                def.stackableIds[opcode - 100] = buffer.getShort() & 0xFFFFF;
                def.stackableAmounts[opcode - 100] = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 110) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 111) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 112) {
                int i = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 113) {
                int i = buffer.get();
            } else if (opcode == 114) {
                int i = buffer.get() * 5;
            } else if (opcode == 115) {
                def.teamId = buffer.get() & 0xFF;
            } else if (opcode == 121) {
                def.lendId = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 122) {
                def.lendTemplateId = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 125) {
                int i = buffer.get() << 0;
                int i2 = buffer.get() << 0;
                int i3 = buffer.get() << 0;
            } else if (opcode == 126) {
                int i = buffer.get() << 0;
                int i2 = buffer.get() << 0;
                int i3 = buffer.get() << 0;
            } else if (opcode == 127) {
                int i = buffer.get() & 0xFF;
                int i2 = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 128) {
                int i = buffer.get() & 0xFF;
                int i2 = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 129) {
                int i = buffer.get() & 0xFF;
                int i2 = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 130) {
                int i = buffer.get() & 0xFF;
                int i2 = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 132) {
                int len = buffer.get() & 0xFF;
                for (int index = 0; index < len; index++) {
                    int anInt = buffer.getShort() & 0xFFFFF;
                }
            } else if (opcode == 249) {
                int length = buffer.get() & 0xFF;
                def.params = new HashMap<>();
                for (int index = 0; index < length; index++) {
                    boolean isString = buffer.get() == 1;
                    int key = ByteBufferUtils.getTriByte(buffer);
                    Object value = isString ? ByteBufferUtils.getJagexString(buffer) : buffer.getInt();
                    def.params.put(key, value);
                }
            }
        }
        return def;
    }

    /**
     * Calculates the total number of items stored in the cache.
     *
     * @param cache the cache instance.
     * @return the total number of items.
     */
    public static int getItemDefinitionsSize(Cache cache) {
        int lastArchiveId = cache.getLastArchiveId(CacheIndex.ITEM_CONFIGURATION.getID());
        return lastArchiveId * 256 + cache.getValidFilesCount(CacheIndex.ITEM_CONFIGURATION.getID(), lastArchiveId);
    }

    /**
     * Encodes this {@link ItemDefinition} into a {@link ByteBuffer}.
     *
     * @return a {@link ByteBuffer} containing the encoded item definition data.
     */
    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // opcode 1 - inventoryModelId (short)
        if (inventoryModelId != 0) {
            buffer.put((byte) 1);
            buffer.putShort((short) inventoryModelId);
        }

        // opcode 2 - name (string)
        if (name != null && !name.isEmpty()) {
            buffer.put((byte) 2);
            ByteBufferUtils.putJagexString(buffer, name);
        }

        // opcode 4 - modelZoom (short)
        if (modelZoom != 0) {
            buffer.put((byte) 4);
            buffer.putShort((short) modelZoom);
        }

        // opcode 5 - modelRotation1 (short)
        if (modelRotation1 != 0) {
            buffer.put((byte) 5);
            buffer.putShort((short) modelRotation1);
        }

        // opcode 6 - modelRotation2 (short)
        if (modelRotation2 != 0) {
            buffer.put((byte) 6);
            buffer.putShort((short) modelRotation2);
        }

        // opcode 7 - modelOffset1 (short, signed)
        if (modelOffset1 != 0) {
            buffer.put((byte) 7);
            buffer.putShort((short) modelOffset1);
        }

        // opcode 8 - modelOffset2 (short, signed)
        if (modelOffset2 != 0) {
            buffer.put((byte) 8);
            buffer.putShort((short) modelOffset2);
        }

        // opcode 11 - stackable flag (1 = stackable)
        if (stackable == 1) {
            buffer.put((byte) 11);
        }

        // opcode 12 - value (int)
        if (value != 0) {
            buffer.put((byte) 12);
            buffer.putInt(value);
        }

        // opcode 16 - membersOnly (flag)
        if (membersOnly) {
            buffer.put((byte) 16);
        }

        // opcode 23 - maleWearModel1 (short)
        if (maleWearModel1 != 0) {
            buffer.put((byte) 23);
            buffer.putShort((short) maleWearModel1);
        }

        // opcode 24 - femaleWearModel1 (short)
        if (femaleWearModel1 != 0) {
            buffer.put((byte) 24);
            buffer.putShort((short) femaleWearModel1);
        }

        // opcode 25 - maleWearModel2 (short)
        if (maleWearModel2 != 0) {
            buffer.put((byte) 25);
            buffer.putShort((short) maleWearModel2);
        }

        // opcode 26 - femaleWearModel2 (short)
        if (femaleWearModel2 != 0) {
            buffer.put((byte) 26);
            buffer.putShort((short) femaleWearModel2);
        }

        // opcode 30-34 - groundOptions (strings)
        if (groundOptions != null) {
            for (int i = 0; i < 5; i++) {
                if (groundOptions[i] != null) {
                    buffer.put((byte) (30 + i));
                    ByteBufferUtils.putJagexString(buffer, groundOptions[i]);
                }
            }
        }

        // opcode 35-39 - inventoryOptions (strings)
        if (inventoryOptions != null) {
            for (int i = 0; i < 5; i++) {
                if (inventoryOptions[i] != null) {
                    buffer.put((byte) (35 + i));
                    ByteBufferUtils.putJagexString(buffer, inventoryOptions[i]);
                }
            }
        }

        // opcode 40 - original and modified model colors (short arrays)
        if (originalModelColors != null && modifiedModelColors != null) {
            buffer.put((byte) 40);
            buffer.put((byte) originalModelColors.length);
            for (int i = 0; i < originalModelColors.length; i++) {
                buffer.putShort(originalModelColors[i]);
                buffer.putShort(modifiedModelColors[i]);
            }
        }

        // opcode 41 - textureColour1 and textureColour2 (short arrays)
        if (textureColour1 != null && textureColour2 != null) {
            buffer.put((byte) 41);
            buffer.put((byte) textureColour1.length);
            for (int i = 0; i < textureColour1.length; i++) {
                buffer.putShort(textureColour1[i]);
                buffer.putShort(textureColour2[i]);
            }
        }

        // opcode 65 - unnoted flag
        if (unnoted) {
            buffer.put((byte) 65);
        }

        // opcode 78 - colourEquip1
        if (colourEquip1 != 0) {
            buffer.put((byte) 78);
            buffer.putShort((short) colourEquip1);
        }

        // opcode 79 - colourEquip2
        if (colourEquip2 != 0) {
            buffer.put((byte) 79);
            buffer.putShort((short) colourEquip2);
        }

        // opcode 97 - notedId
        if (notedId != 0) {
            buffer.put((byte) 97);
            buffer.putShort((short) notedId);
        }

        // opcode 98 - notedTemplateId
        if (notedTemplateId != 0) {
            buffer.put((byte) 98);
            buffer.putShort((short) notedTemplateId);
        }

        // opcode 100-109 - stackableIds and stackableAmounts arrays
        if (stackableIds != null && stackableAmounts != null) {
            for (int i = 0; i < 10; i++) {
                if (stackableIds[i] != 0 || stackableAmounts[i] != 0) {
                    buffer.put((byte) (100 + i));
                    buffer.putShort((short) stackableIds[i]);
                    buffer.putShort((short) stackableAmounts[i]);
                }
            }
        }

        // opcode 115 - teamId
        if (teamId != 0) {
            buffer.put((byte) 115);
            buffer.put((byte) teamId);
        }

        // opcode 121 - lendId
        if (lendId != 0) {
            buffer.put((byte) 121);
            buffer.putShort((short) lendId);
        }

        // opcode 122 - lendTemplateId
        if (lendTemplateId != 0) {
            buffer.put((byte) 122);
            buffer.putShort((short) lendTemplateId);
        }

        // opcode 249 - params map
        if (params != null && !params.isEmpty()) {
            buffer.put((byte) 249);
            buffer.put((byte) params.size());
            for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                Object val = entry.getValue();
                if (val instanceof String) {
                    buffer.put((byte) 1);
                    ByteBufferUtils.putTriByte(buffer, entry.getKey());
                    ByteBufferUtils.putJagexString(buffer, (String) val);
                } else if (val instanceof Integer) {
                    buffer.put((byte) 0);
                    ByteBufferUtils.putTriByte(buffer, entry.getKey());
                    buffer.putInt((Integer) val);
                }
            }
        }
        buffer.put((byte) 0);

        buffer.flip();
        return buffer;
    }

    public String getName() {
        return name;
    }

    public int getInventoryModelId() {
        return inventoryModelId;
    }

    public int getModelZoom() {
        return modelZoom;
    }

    public int getModelRotation1() {
        return modelRotation1;
    }

    public int getModelRotation2() {
        return modelRotation2;
    }

    public int getModelOffset1() {
        return modelOffset1;
    }

    public int getModelOffset2() {
        return modelOffset2;
    }

    public int getStackable() {
        return stackable;
    }

    public int getValue() {
        return value;
    }

    public boolean isMembersOnly() {
        return membersOnly;
    }

    public int getMaleWearModel1() {
        return maleWearModel1;
    }

    public int getMaleWearModel2() {
        return maleWearModel2;
    }

    public int getFemaleWearModel1() {
        return femaleWearModel1;
    }

    public int getFemaleWearModel2() {
        return femaleWearModel2;
    }

    public String[] getGroundOptions() {
        return groundOptions;
    }

    public String[] getInventoryOptions() {
        return inventoryOptions;
    }

    public short[] getOriginalModelColors() {
        return originalModelColors;
    }

    public short[] getModifiedModelColors() {
        return modifiedModelColors;
    }

    public short[] getTextureColour1() {
        return textureColour1;
    }

    public short[] getTextureColour2() {
        return textureColour2;
    }

    public boolean isUnnoted() {
        return unnoted;
    }

    public int getColourEquip1() {
        return colourEquip1;
    }

    public int getColourEquip2() {
        return colourEquip2;
    }

    public int getNotedId() {
        return notedId;
    }

    public int getNotedTemplateId() {
        return notedTemplateId;
    }

    public int[] getStackableIds() {
        return stackableIds;
    }

    public int[] getStackableAmounts() {
        return stackableAmounts;
    }

    public int getTeamId() {
        return teamId;
    }

    public int getLendId() {
        return lendId;
    }

    public int getLendTemplateId() {
        return lendTemplateId;
    }

    public Map<Integer, Object> getParams() {
        return params;
    }

    /**
     * Gets the integer parameter associated with the given key.
     *
     * @param key          the key of the parameter
     * @param defaultValue the value to return if the key is not found or is not an Integer
     * @return the integer value of the parameter or the default
     */
    public int getIntParam(int key, int defaultValue) {
        if (params == null) return defaultValue;
        Object value = params.get(key);
        return value instanceof Integer ? (Integer) value : defaultValue;
    }

    /**
     * Gets the string parameter associated with the given key.
     *
     * @param key          the key of the parameter
     * @param defaultValue the value to return if the key is not found or is not a String
     * @return the string value of the parameter or the default
     */
    public String getStringParam(int key, String defaultValue) {
        if (params == null) return defaultValue;
        Object value = params.get(key);
        return value instanceof String ? (String) value : defaultValue;
    }
}