package cache;

import java.io.File;

public final class Constants {

    public static final int INDICES = 29;

    public static final String CACHE_PATH = "resources/cache";
    public static final String XTEAS_PATH = "resources/keys.json";
    public static final String FLAT_FILES = "resources/flat_files";
    public static final String NEW_CACHE = "resources/repacked";
    public static final String MERGED_CACHE_PATH = "resources/merged_cache";

    /*
     * Unpack
     */

    public static final String DUMP_PATH = "resources/dump";
    public static final String SPRITE_PATH = DUMP_PATH + File.separator + "sprites";
    public static final String MODEL_PATH = DUMP_PATH + File.separator + "models";
    public static final String MAP_PATH = DUMP_PATH + File.separator + "maps";
    public static final String VERSION_TABLE_PATH = DUMP_PATH + File.separator + "version_table";

    /*
     * Configurations
     */

    public static final String ITEMS_DEFINITION_PATH = DUMP_PATH + File.separator + "items";
    public static final String OBJECTS_DEFINITION_PATH = DUMP_PATH + File.separator + "objects";
    public static final String NPCS_DEFINITION_PATH = DUMP_PATH + File.separator + "npcs";
    public static final String INTERFACE_DEFINITION_PATH = DUMP_PATH + File.separator + "interfaces";


    private Constants() {
    }
}