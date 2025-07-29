package core.game.node.entity.player.link;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.game.node.item.Item;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The type Quest data.
 */
public final class QuestData {

    private final boolean[] cooksAssistant = new boolean[4];

    private final boolean[] demonSlayer = new boolean[2];

    private final boolean[] draynorLever = new boolean[6];

    private final boolean[] dragonSlayer = new boolean[9];

    private final Item[] desertTreasure = new Item[7];

    private int dragonSlayerPlanks;

    private boolean gardenerAttack;

    private boolean talkedDrezel;

    private int witchsExperimentStage;

    private boolean witchsExperimentKilled;

    /**
     * Instantiates a new Quest data.
     */
    public QuestData() {
        Arrays.fill(draynorLever, true);
        populateDesertTreasureNode();
    }

    /**
     * Parse.
     *
     * @param data the data
     */
    public void parse(JsonObject data) {
        JsonArray dl = data.getAsJsonArray("draynorLever");
        for (int i = 0; i < dl.size(); i++) {
            draynorLever[i] = dl.get(i).getAsBoolean();
        }

        JsonArray drs = data.getAsJsonArray("dragonSlayer");
        for (int i = 0; i < drs.size(); i++) {
            dragonSlayer[i] = drs.get(i).getAsBoolean();
        }

        dragonSlayerPlanks = data.get("dragonSlayerPlanks").getAsInt();

        JsonArray des = data.getAsJsonArray("demonSlayer");
        for (int i = 0; i < des.size(); i++) {
            demonSlayer[i] = des.get(i).getAsBoolean();
        }

        JsonArray ca = data.getAsJsonArray("cooksAssistant");
        for (int i = 0; i < ca.size(); i++) {
            cooksAssistant[i] = ca.get(i).getAsBoolean();
        }

        gardenerAttack = data.get("gardenerAttack").getAsBoolean();
        talkedDrezel = data.get("talkedDrezel").getAsBoolean();

        JsonArray dtn = data.getAsJsonArray("desertTreasureNode");
        for (int i = 0; i < dtn.size(); i++) {
            JsonObject itemObj = dtn.get(i).getAsJsonObject();
            desertTreasure[i] = new Item(
                    itemObj.get("id").getAsInt(),
                    itemObj.get("amount").getAsInt()
            );
        }

        witchsExperimentKilled = data.get("witchsExperimentKilled").getAsBoolean();
        witchsExperimentStage = data.get("witchsExperimentStage").getAsInt();
    }

    private final void saveDesertTreasureNode(ByteBuffer buffer) {
        buffer.put((byte) 8);
        for (int i = 0; i < desertTreasure.length; i++) {
            Item item = desertTreasure[i];
            buffer.putShort((short) item.getId());
            buffer.put((byte) item.getAmount());
        }
    }

    /**
     * Get draynor levers boolean [ ].
     *
     * @return the boolean [ ]
     */
    public boolean[] getDraynorLevers() {
        return draynorLever;
    }

    /**
     * Get dragon slayer items boolean [ ].
     *
     * @return the boolean [ ]
     */
    public boolean[] getDragonSlayerItems() {
        return dragonSlayer;
    }

    /**
     * Gets dragon slayer item.
     *
     * @param name the name
     * @return the dragon slayer item
     */
    public boolean getDragonSlayerItem(String name) {
        return name == "lobster" ? dragonSlayer[0] : name == "wizard" ? dragonSlayer[3] : name == "silk" ? dragonSlayer[2] : name == "bowl" ? dragonSlayer[1] : dragonSlayer[0];
    }

    /**
     * Gets dragon slayer attribute.
     *
     * @param name the name
     * @return the dragon slayer attribute
     */
    public boolean getDragonSlayerAttribute(String name) {
        return name == "ship" ? dragonSlayer[4] : name == "memorized" ? dragonSlayer[5] : name == "repaired" ? dragonSlayer[6] : name == "ned" ? dragonSlayer[7] : name == "poured" ? dragonSlayer[8] : dragonSlayer[8];
    }

    /**
     * Sets dragon slayer attribute.
     *
     * @param name  the name
     * @param value the value
     */
    public void setDragonSlayerAttribute(String name, boolean value) {
        dragonSlayer[(name == "ship" ? 4 : name == "memorized" ? 5 : name == "repaired" ? 6 : name == "ned" ? 7 : name == "poured" ? 8 : 8)] = value;
    }

    /**
     * Gets cook assist.
     *
     * @param name the name
     * @return the cook assist
     */
    public boolean getCookAssist(String name) {
        return name == "milk" ? cooksAssistant[0] : name == "egg" ? cooksAssistant[1] : name == "flour" ? cooksAssistant[2] : name == "gave" ? cooksAssistant[3] : cooksAssistant[3];
    }

    /**
     * Sets cooks assistant.
     *
     * @param name  the name
     * @param value the value
     */
    public void setCooksAssistant(String name, boolean value) {
        cooksAssistant[(name == "milk" ? 0 : name == "egg" ? 1 : name == "flour" ? 2 : name == "gave" ? 3 : 3)] = value;
    }

    /**
     * Gets dragon slayer planks.
     *
     * @return the dragon slayer planks
     */
    public int getDragonSlayerPlanks() {
        return dragonSlayerPlanks;
    }

    /**
     * Sets dragon slayer planks.
     *
     * @param i the
     */
    public void setDragonSlayerPlanks(int i) {
        this.dragonSlayerPlanks = i;
    }

    /**
     * Get demon slayer boolean [ ].
     *
     * @return the boolean [ ]
     */
    public boolean[] getDemonSlayer() {
        return demonSlayer;
    }

    /**
     * Get cooks assistant boolean [ ].
     *
     * @return the boolean [ ]
     */
    public boolean[] getCooksAssistant() {
        return cooksAssistant;
    }

    /**
     * Is gardener attack boolean.
     *
     * @return the boolean
     */
    public boolean isGardenerAttack() {
        return gardenerAttack;
    }

    /**
     * Sets gardener attack.
     *
     * @param gardenerAttack the gardener attack
     */
    public void setGardenerAttack(boolean gardenerAttack) {
        this.gardenerAttack = gardenerAttack;
    }

    /**
     * Is talked drezel boolean.
     *
     * @return the boolean
     */
    public boolean isTalkedDrezel() {
        return talkedDrezel;
    }

    /**
     * Sets talked drezel.
     *
     * @param talkedDrezel the talked drezel
     */
    public void setTalkedDrezel(boolean talkedDrezel) {
        this.talkedDrezel = talkedDrezel;
    }

    private final void populateDesertTreasureNode() {
        desertTreasure[0] = new Item(1513, 12);
        desertTreasure[1] = new Item(592, 10);
        desertTreasure[2] = new Item(1775, 6);
        desertTreasure[3] = new Item(2353, 6);
        desertTreasure[4] = new Item(526, 2);
        desertTreasure[5] = new Item(973, 2);
        desertTreasure[6] = new Item(565, 1);
    }

    /**
     * Gets desert treasure item.
     *
     * @param index the index
     * @return the desert treasure item
     */
    public Item getDesertTreasureItem(int index) {
        if (index < 0 || index > desertTreasure.length) {
            throw new IndexOutOfBoundsException("Index out of bounds, index can only span from 0 - 6.");
        }
        return desertTreasure[index];
    }

    /**
     * Sets desert treasure item.
     *
     * @param index the index
     * @param item  the item
     */
    public void setDesertTreasureItem(int index, Item item) {
        if (index < 0 || index > desertTreasure.length) {
            throw new IndexOutOfBoundsException("Index out of bounds, index can only span from 0 - 6.");
        }
        desertTreasure[index] = item;
    }

    /**
     * Gets witchs experiment stage.
     *
     * @return the witchs experiment stage
     */
    public int getWitchsExperimentStage() {
        return witchsExperimentStage;
    }

    /**
     * Sets witchs experiment stage.
     *
     * @param witchsExperimentStage the witchs experiment stage
     */
    public void setWitchsExperimentStage(int witchsExperimentStage) {
        this.witchsExperimentStage = witchsExperimentStage;
    }

    /**
     * Is witchs experiment killed boolean.
     *
     * @return the boolean
     */
    public boolean isWitchsExperimentKilled() {
        return witchsExperimentKilled;
    }

    /**
     * Sets witchs experiment killed.
     *
     * @param witchsExperimentKilled the witchs experiment killed
     */
    public void setWitchsExperimentKilled(boolean witchsExperimentKilled) {
        this.witchsExperimentKilled = witchsExperimentKilled;
    }

    /**
     * Get draynor lever boolean [ ].
     *
     * @return the boolean [ ]
     */
    public boolean[] getDraynorLever() {
        return draynorLever;
    }

    /**
     * Get dragon slayer boolean [ ].
     *
     * @return the boolean [ ]
     */
    public boolean[] getDragonSlayer() {
        return dragonSlayer;
    }

    /**
     * Get desert treasure item [ ].
     *
     * @return the item [ ]
     */
    public Item[] getDesertTreasure() {
        return desertTreasure;
    }
}