package core.cache.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.cache.Cache;
import cache.Constants;
import core.cache.FileStore;
import core.cache.def.ItemDefinition;
import core.cache.type.CacheIndex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.*;

/**
 * Dumps all ItemDefinitions from the cache into a single JSON file.
 */
public class ItemDefinitionDumper {

    private static final Logger logger = Logger.getLogger(ItemDefinitionDumper.class.getName());

    static {
        // Configure logger output to be clean and readable
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord record) {
                return String.format(FORMAT,
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage());
            }
        });
        handler.setLevel(Level.INFO);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        try {
            new ItemDefinitionDumper().dump(Constants.CACHE_PATH, Constants.ITEMS_DEFINITION_PATH);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to dump item definitions", e);
        }
    }

    /**
     * Dumps all item definitions from cache into a JSON file.
     *
     * @param path The path to the cache directory.
     * @param output The directory for the output file.
     */
    public void dump(String path, String output) {
        try (FileStore store = FileStore.open(path)) {
            Cache cache = new Cache(store);

            File outDir = new File(output);
            if (!outDir.exists() && !outDir.mkdirs()) {
                logger.log(Level.SEVERE, "Failed to create output directory: " + outDir.getAbsolutePath());
                return;
            }

            int totalItems = ItemDefinition.getItemDefinitionsSize(cache);
            logger.info(String.format("Starting dump of %d item definitions...", totalItems));

            List<Map<String, Object>> allItems = new ArrayList<>();

            for (int itemId = 0; itemId < totalItems; itemId++) {
                try {
                    int archiveId = itemId >>> 8;
                    int memberId = itemId & 0xFF;

                    ByteBuffer data = cache.read(CacheIndex.ITEM_CONFIGURATION.getID(), archiveId, memberId);
                    if (data == null || data.remaining() == 0) {
                        continue;
                    }

                    ItemDefinition item;
                    try {
                        item = ItemDefinition.decode(data);
                    } catch (Exception ex) {
                        logger.log(Level.WARNING, "Skipping invalid item definition (ID: " + itemId + ")");
                        continue;
                    }

                    if (item == null) continue;

                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("id", itemId);
                    entry.put("name", item.getName());
                    entry.put("value", item.getValue());
                    entry.put("membersOnly", item.isMembersOnly());
                    entry.put("stackable", item.getStackable());
                    entry.put("inventoryModelId", item.getInventoryModelId());
                    entry.put("modelZoom", item.getModelZoom());
                    entry.put("modelRotation1", item.getModelRotation1());
                    entry.put("modelRotation2", item.getModelRotation2());
                    entry.put("modelOffset1", item.getModelOffset1());
                    entry.put("modelOffset2", item.getModelOffset2());
                    entry.put("maleWearModel1", item.getMaleWearModel1());
                    entry.put("maleWearModel2", item.getMaleWearModel2());
                    entry.put("femaleWearModel1", item.getFemaleWearModel1());
                    entry.put("femaleWearModel2", item.getFemaleWearModel2());
                    entry.put("teamId", item.getTeamId());
                    entry.put("lendId", item.getLendId());
                    entry.put("lendTemplateId", item.getLendTemplateId());
                    entry.put("groundOptions", item.getGroundOptions());
                    entry.put("inventoryOptions", item.getInventoryOptions());
                    entry.put("originalModelColors", item.getOriginalModelColors());
                    entry.put("modifiedModelColors", item.getModifiedModelColors());
                    entry.put("textureColour1", item.getTextureColour1());
                    entry.put("textureColour2", item.getTextureColour2());
                    entry.put("unnoted", item.isUnnoted());
                    entry.put("colourEquip1", item.getColourEquip1());
                    entry.put("colourEquip2", item.getColourEquip2());
                    entry.put("notedId", item.getNotedId());
                    entry.put("notedTemplateId", item.getNotedTemplateId());
                    entry.put("stackableIds", item.getStackableIds());
                    entry.put("stackableAmounts", item.getStackableAmounts());
                    entry.put("params", item.getParams());

                    allItems.add(entry);

                    if ((itemId + 1) % 500 == 0) {
                        logger.info(String.format("Processed %d / %d item definitions", itemId + 1, totalItems));
                    }

                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error processing item ID " + itemId, e);
                }
            }

            File outFile = new File(outDir, "items.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try (FileWriter writer = new FileWriter(outFile)) {
                gson.toJson(allItems, writer);
            }

            logger.info(String.format(
                    "Successfully dumped %d item definitions to %s",
                    allItems.size(), outFile.getAbsolutePath())
            );

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize cache or write JSON file", e);
        }
    }
}
