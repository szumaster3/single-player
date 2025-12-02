package content.global.skill.thieving

import core.api.*
import core.api.utils.WeightBasedTable
import core.api.utils.WeightedItem
import core.game.event.ResourceProducedEvent
import core.game.global.action.DoorActionHandler.handleAutowalkDoor
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.combat.ImpactHandler.HitsplatType
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.game.world.GameWorld.ticks
import core.game.world.map.Direction
import core.game.world.map.Location
import core.game.world.map.RegionManager
import core.game.world.update.flag.context.Animation
import core.tools.RandomFunction
import core.tools.StringUtils.isPlusN
import shared.consts.Animations
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Quests


enum class Stall(full: Array<Int>, empty: Array<Int>, var level: Int, var rewards: Array<Item>, var experience: Double, var delay: Int, var message: String) {
    VEGETABLE_STALL(arrayOf(shared.consts.Scenery.VEG_STALL_4706, shared.consts.Scenery.VEG_STALL_4708), arrayOf(shared.consts.Scenery.MARKET_STALL_634), 2, arrayOf(Item(Items.ONION_1957, 1), Item(Items.CABBAGE_1965, 1), Item(Items.POTATO_1942, 1), Item(Items.TOMATO_1982, 1), Item(Items.GARLIC_1550, 1)), 10.0, 4, "vegetables",),
    BAKER_STALL(arrayOf(shared.consts.Scenery.BAKER_S_STALL_2561, shared.consts.Scenery.BAKERY_STALL_6163, shared.consts.Scenery.BAKER_S_STALL_34384), arrayOf(shared.consts.Scenery.MARKET_STALL_634, shared.consts.Scenery.MARKET_STALL_6984, shared.consts.Scenery.MARKET_STALL_34381), 5, arrayOf(Item(Items.CAKE_1891, 1), Item(Items.BREAD_2309, 1), Item(Items.CHOCOLATE_SLICE_1901, 1)), 16.0, 4, "bread"),
    CRAFTING_STALL(arrayOf(shared.consts.Scenery.CRAFTING_STALL_4874, shared.consts.Scenery.CRAFTING_STALL_6166), arrayOf(shared.consts.Scenery.BAMBOO_DESK_4797, shared.consts.Scenery.MARKET_STALL_6984), 5, arrayOf(Item(Items.RING_MOULD_1592, 1), Item(Items.NECKLACE_MOULD_1597, 1), Item(Items.CHISEL_1755, 1)), 16.0, 12, "tool"),
    TEA_STALL(arrayOf(shared.consts.Scenery.TEA_STALL_635, shared.consts.Scenery.TEA_STALL_6574), arrayOf(shared.consts.Scenery.MARKET_STALL_634, shared.consts.Scenery.MARKET_STALL_6573), 5, arrayOf(Item(Items.CUP_OF_TEA_712, 1)), 16.0, 12, "tea"),
    SILK_STALL(arrayOf(shared.consts.Scenery.SILK_STALL_34383, shared.consts.Scenery.SILK_STALL_2560), arrayOf(shared.consts.Scenery.MARKET_STALL_34381, shared.consts.Scenery.MARKET_STALL_634), 20, arrayOf(Item(Items.SILK_950, 1)), 24.0, 13, "silk"),
    WINE_STALL(arrayOf(shared.consts.Scenery.MARKET_STALL_14011), arrayOf(shared.consts.Scenery.MARKET_STALL_634), 22, arrayOf(Item(Items.JUG_1935, 1), Item(Items.JUG_OF_WATER_1937, 1), Item(Items.JUG_OF_WINE_1993, 1), Item(Items.BOTTLE_OF_WINE_7919, 1)), 27.0, 27, "wine",),
    MARKET_SEED_STALL(arrayOf(shared.consts.Scenery.SEED_STALL_7053), arrayOf(shared.consts.Scenery.MARKET_STALL_634), 27, arrayOf(Item(Items.MARIGOLD_SEED_5096, 1), Item(Items.ROSEMARY_SEED_5097, 1), Item(Items.REDBERRY_SEED_5101, 1), Item(Items.POTATO_SEED_5318, 1), Item(Items.ONION_SEED_5319, 1), Item(Items.CABBAGE_SEED_5324, 1)), 10.0, 19, "seeds",),
    FUR_STALL(arrayOf(shared.consts.Scenery.FUR_STALL_34387, shared.consts.Scenery.FUR_STALL_2563, shared.consts.Scenery.FUR_STALL_4278), arrayOf(shared.consts.Scenery.MARKET_STALL_34381, shared.consts.Scenery.MARKET_STALL_634, shared.consts.Scenery.MARKET_STALL_634), 35, arrayOf(Item(Items.FUR_6814, 1), Item(Items.GREY_WOLF_FUR_958, 1)), 36.0, 25, "fur"),
    FISH_STALL(arrayOf(shared.consts.Scenery.FISH_STALL_4277, shared.consts.Scenery.FISH_STALL_4705, shared.consts.Scenery.FISH_STALL_4707), arrayOf(shared.consts.Scenery.MARKET_STALL_634, shared.consts.Scenery.MARKET_STALL_634, shared.consts.Scenery.MARKET_STALL_634), 42, arrayOf(Item(Items.RAW_SALMON_331, 1), Item(Items.RAW_TUNA_359, 1), Item(Items.RAW_LOBSTER_377, 1)), 42.0, 27, "fish"),
    CROSSBOW_STALL(arrayOf(shared.consts.Scenery.CROSSBOW_STALL_17031), arrayOf(shared.consts.Scenery.MARKET_STALL_6984), 49, arrayOf(Item(Items.BRONZE_BOLTS_877, 3), Item(Items.BRONZE_LIMBS_9420, 1), Item(Items.WOODEN_STOCK_9440, 1)), 52.0, 19, "crossbow parts"),
    SILVER_STALL(arrayOf(shared.consts.Scenery.SILVER_STALL_2565, shared.consts.Scenery.SILVER_STALL_6164, shared.consts.Scenery.SILVER_STALL_34382), arrayOf(shared.consts.Scenery.MARKET_STALL_634, shared.consts.Scenery.MARKET_STALL_6984, shared.consts.Scenery.MARKET_STALL_34381), 50, arrayOf(Item(Items.SILVER_ORE_442, 1)), 54.0, 50, "silver"),
    SPICE_STALL(arrayOf(shared.consts.Scenery.SPICE_STALL_34386, shared.consts.Scenery.CRAFTING_STALL_6166), arrayOf(shared.consts.Scenery.MARKET_STALL_34381, shared.consts.Scenery.MARKET_STALL_6984), 65, arrayOf(Item(Items.SPICE_2007, 1)), 81.0, 134, "spices"),
    GEM_STALL(arrayOf(shared.consts.Scenery.GEM_STALL_2562, shared.consts.Scenery.GEM_STALL_6162, shared.consts.Scenery.GEM_STALL_34385), arrayOf(shared.consts.Scenery.MARKET_STALL_634, shared.consts.Scenery.MARKET_STALL_6984, shared.consts.Scenery.MARKET_STALL_34381), 75, arrayOf(Item(Items.UNCUT_SAPPHIRE_1623, 1), Item(Items.EMERALD_1605, 1), Item(Items.RUBY_1603, 1), Item(Items.DIAMOND_1601, 1)), 160.0, 300, "gems"),
    SCIMITAR_STALL(arrayOf(shared.consts.Scenery.SCIMITAR_STALL_4878), arrayOf(shared.consts.Scenery.BAMBOO_DESK_4797), 65, arrayOf(Item(Items.IRON_SCIMITAR_1323, 1)), 100.0, 134, "equipment"),
    MAGIC_STALL(arrayOf(shared.consts.Scenery.MAGIC_STALL_4877), arrayOf(shared.consts.Scenery.BAMBOO_DESK_4797), 65, arrayOf(Item(Items.AIR_RUNE_556, 1), Item(Items.EARTH_RUNE_557, 1), Item(Items.FIRE_RUNE_554, 1), Item(Items.WATER_RUNE_555, 1), Item(Items.LAW_RUNE_563, 1)), 100.0, 134, "equipment"),
    GENERAL_STALL(arrayOf(shared.consts.Scenery.GENERAL_STALL_4876), arrayOf(shared.consts.Scenery.BAMBOO_DESK_4797), 5, arrayOf(Item(Items.EMPTY_POT_1931, 1), Item(Items.HAMMER_2347, 1), Item(Items.TINDERBOX_590, 1)), 16.0, 12, "goods"),
    FOOD_STALL(arrayOf(shared.consts.Scenery.FOOD_STALL_4875), arrayOf(shared.consts.Scenery.BAMBOO_DESK_4797), 5, arrayOf(Item(Items.BANANA_1963, 1)), 16.0, 12, "food"),
    CANDLES(arrayOf(shared.consts.Scenery.CANDLES_19127), arrayOf(shared.consts.Scenery.CANDLES_19127), 20, arrayOf(Item(Items.CANDLE_36, 1)), 20.0, 0, "candles"),
    COUNTER(arrayOf(shared.consts.Scenery.COUNTER_2793), arrayOf(shared.consts.Scenery.COUNTER_2791), 15, arrayOf(Item(Items.ROCK_CAKE_2379, 1)), 6.5, 12, "rocks"),
    ;

    var fullIDs: List<Int> = ArrayList(listOf(*full))
    var empty_ids: List<Int> = ArrayList(listOf(*empty))

    fun getEmpty(id: Int): Int {
        val fullIndex = fullIDs.indexOf(id)
        return empty_ids[fullIndex]
    }

    val randomLoot: Item
        get() = rewards[RandomFunction.random(rewards.size)]

    companion object {
        private val idMap =
            HashMap<Int, Stall>().apply {
                Stall.values().forEach { entry ->
                    entry.fullIDs.forEach { id -> putIfAbsent(id, entry) }
                }
            }

        fun handleStall(player: Player, node: Scenery, stall: Stall) {
            if (player.inCombat()) {
                sendMessage(player, "You can't steal from the market stall during combat!")
                return
            }
            if (getStatLevel(player, Skills.THIEVING) < stall.level) {
                sendMessage(player, "You need to be level ${stall.level} to steal from the ${node.name.lowercase()}.")
                return
            }
            if (player.inventory.freeSlots() == 0) {
                sendMessage(player, "You don't have enough inventory space.")
                return
            }

            if (player.location.isInRegion(10553) && !isQuestComplete(player, Quests.THE_FREMENNIK_TRIALS)) {
                if (stall.fullIDs.contains(4278)) {
                    sendDialogue(player, "The fur trader is staring at you suspiciously. You cannot steal from his stall while he distrusts you.")
                    return
                }
                if (stall.fullIDs.contains(4277)) {
                    sendDialogue(player, "The fishmonger is staring at you suspiciously. You cannot steal from his stall while he distrusts you.")
                    return
                }
            }

            player.animate(Animation(Animations.HUMAN_MULTI_USE_832))
            player.locks.lockInteractions(2)

            queueScript(player, 0) {
                delayScript(player, 2)
                val item = stall.randomLoot
                val success = RandomFunction.random(15) >= 4

                if (!success) {
                    for (npc in RegionManager.getLocalNpcs(player.location, 8)) {
                        if (!npc.properties.combatPulse.isAttacking && (npc.id == 32 || npc.id == 2236)) {
                            npc.sendChat("Hey! Get your hands off there!")
                            npc.properties.combatPulse.attack(player)
                        }
                    }
                    if (stall == Stall.CANDLES) {
                        stun(player, 15, false)
                        impact(player, 3, ImpactHandler.HitsplatType.NORMAL)
                        player.sendMessage("A higher power smites you.")
                    }
                    return@queueScript   stopExecuting(player)
                }


                player.inventory.add(item)
                player.getSkills().addExperience(Skills.THIEVING, stall.experience, true)
                if (stall == Stall.SILK_STALL) {
                    player.getSavedData().globalData.setSilkSteal(System.currentTimeMillis() + 1800000)
                }
                if (node.isActive) {
                    SceneryBuilder.replace(node, node.transform(stall.getEmpty(node.id)), stall.delay)
                }

                sendMessage(
                    player,
                    "You steal ${if (isPlusN(item.name)) "an" else "a"} ${item.name.lowercase()} from the ${stall.name.lowercase().replace('_', ' ')}."
                )
                player.dispatch(ResourceProducedEvent(item.id, item.amount, node, 0))
                stopExecuting(player)
            }
        }
    }
}

enum class Pickpocket(val ids: IntArray, val requiredLevel: Int, val low: Double, val high: Double, val experience: Double, val stunDamageMin: Int, val stunDamageMax: Int, val stunTime: Int, val message: String?, val table: WeightBasedTable) {
    MAN(intArrayOf(NPCs.MAN_1, NPCs.MAN_2, NPCs.MAN_3, NPCs.WOMAN_4, NPCs.WOMAN_5, NPCs.WOMAN_6, NPCs.MAN_16, NPCs.MAN_24, NPCs.WOMAN_25, NPCs.MAN_170, NPCs.MAN_1086, NPCs.MAN_2675, NPCs.MAN_3224, NPCs.MAN_3225, NPCs.WOMAN_3227, NPCs.MAN_5923, NPCs.WOMAN_5924, NPCs.MAN_7873, NPCs.MAN_7874, NPCs.MAN_7875, NPCs.MAN_7876, NPCs.MAN_7877, NPCs.MAN_7878, NPCs.MAN_7879, NPCs.WOMAN_7880, NPCs.WOMAN_7881, NPCs.WOMAN_7882, NPCs.WOMAN_7883, NPCs.WOMAN_7884, NPCs.WOMAN_7925, NPCs.HENGEL_2683, NPCs.ANJA_2684), 1, 180.0, 240.0, 8.0, 1, 1, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 3, 3, 1.0, true)),),
    CURATOR_HAIG_HELEN(intArrayOf(NPCs.CURATOR_HAIG_HALEN_646), 1, 180.0, 240.0, 8.0, 1, 1, 5, null, WeightBasedTable.create(WeightedItem(Items.DISPLAY_CABINET_KEY_4617, 1, 1, 1.0, true))),
    GANG_OF_THIEVES(intArrayOf(NPCs.CUFFS_3237, NPCs.NARF_3238, NPCs.RUSTY_3239, NPCs.JEFF_3240), 1, 180.0, 240.0, 8.0, 1, 1, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 3, 3, 1.0, true))),
    FARMER(intArrayOf(NPCs.FARMER_7, NPCs.FARMER_1757, NPCs.FARMER_1758), 10, 180.0, 240.0, 14.5, 1, 1, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 9, 9, 1.0, true), WeightedItem(Items.POTATO_SEED_5318, 1, 1, 1.0, true)),),
    MALE_HAM_MEMBER(intArrayOf(NPCs.HAM_MEMBER_1714), 20, 117.0, 240.0, 22.5, 1, 3, 4, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 1, 21, 5.5), WeightedItem(Items.TINDERBOX_590, 1, 1, 5.0), WeightedItem(Items.LOGS_1511, 1, 1, 7.0), WeightedItem(Items.UNCUT_JADE_1627, 1, 1, 2.5), WeightedItem(Items.UNCUT_OPAL_1625, 1, 1, 2.5), WeightedItem(Items.RAW_ANCHOVIES_321, 1, 1, 7.0), WeightedItem(Items.RAW_CHICKEN_2138, 1, 1, 3.5), WeightedItem(Items.HAM_CLOAK_4304, 1, 1, 0.25), WeightedItem(Items.HAM_HOOD_4302, 1, 1, 0.25), WeightedItem(Items.HAM_LOGO_4306, 1, 1, 0.25), WeightedItem(Items.HAM_ROBE_4300, 1, 1, 0.25), WeightedItem(Items.HAM_SHIRT_4298, 1, 1, 0.25), WeightedItem(Items.BOOTS_4310, 1, 1, 1.0), WeightedItem(Items.GLOVES_4308, 1, 1, 1.0), WeightedItem(Items.BRONZE_PICKAXE_1265, 1, 1, 5.0), WeightedItem(Items.IRON_PICKAXE_1267, 1, 1, 5.0), WeightedItem(Items.STEEL_PICKAXE_1269, 1, 1, 2.5), WeightedItem(Items.GRIMY_GUAM_199, 1, 1, 2.0), WeightedItem(Items.GRIMY_HARRALANDER_205, 1, 1, 2.0), WeightedItem(Items.GRIMY_KWUARM_213, 1, 1, 2.0), WeightedItem(Items.GRIMY_MARRENTILL_201, 1, 1, 1.5), WeightedItem(Items.RUSTY_SWORD_686, 1, 1, 3.5), WeightedItem(Items.BROKEN_ARMOUR_698, 1, 1, 3.5), WeightedItem(Items.BROKEN_STAFF_689, 1, 1, 3.2), WeightedItem(Items.BROKEN_ARROW_687, 1, 1, 3.1), WeightedItem(Items.BUTTONS_688, 1, 1, 3.0)).insertEasyClue(1.0),),
    FEMALE_HAM_MEMBER(intArrayOf(NPCs.HAM_MEMBER_1715), 15, 135.0, 240.0, 18.5, 1, 3, 4, "Stop! @name is a thief!", WeightBasedTable.create(WeightedItem(Items.COINS_995, 1, 21, 5.5), WeightedItem(Items.TINDERBOX_590, 1, 1, 5.0), WeightedItem(Items.LOGS_1511, 1, 1, 7.0), WeightedItem(Items.UNCUT_JADE_1627, 1, 1, 2.5), WeightedItem(Items.UNCUT_OPAL_1625, 1, 1, 2.5), WeightedItem(Items.RAW_ANCHOVIES_321, 1, 1, 7.0), WeightedItem(Items.RAW_CHICKEN_2138, 1, 1, 3.5), WeightedItem(Items.HAM_CLOAK_4304, 1, 1, 0.25), WeightedItem(Items.HAM_HOOD_4302, 1, 1, 0.25), WeightedItem(Items.HAM_LOGO_4306, 1, 1, 0.25), WeightedItem(Items.HAM_SHIRT_4298, 1, 1, 0.25), WeightedItem(Items.HAM_ROBE_4300, 1, 1, 0.25), WeightedItem(Items.BOOTS_4310, 1, 1, 1.0), WeightedItem(Items.GLOVES_4308, 1, 1, 1.0), WeightedItem(Items.BRONZE_PICKAXE_1265, 1, 1, 5.0), WeightedItem(Items.IRON_PICKAXE_1267, 1, 1, 5.0), WeightedItem(Items.STEEL_PICKAXE_1269, 1, 1, 2.5), WeightedItem(Items.GRIMY_GUAM_199, 1, 1, 2.0), WeightedItem(Items.GRIMY_HARRALANDER_205, 1, 1, 2.0), WeightedItem(Items.GRIMY_KWUARM_213, 1, 1, 2.0), WeightedItem(Items.GRIMY_MARRENTILL_201, 1, 1, 1.5), WeightedItem(Items.RUSTY_SWORD_686, 1, 1, 3.5), WeightedItem(Items.BROKEN_ARMOUR_698, 1, 1, 3.5), WeightedItem(Items.BROKEN_STAFF_689, 1, 1, 3.2), WeightedItem(Items.BROKEN_ARROW_687, 1, 1, 3.1), WeightedItem(Items.BUTTONS_688, 1, 1, 3.0)).insertEasyClue(1.0),),
    WARRIOR(intArrayOf(NPCs.WARRIOR_WOMAN_15, NPCs.AL_KHARID_WARRIOR_18), 25, 84.0, 240.0, 26.0, 2, 2, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 18, 18, 1.0, true))),
    VILLAGER(intArrayOf(1887, NPCs.VILLAGER_1888, NPCs.VILLAGER_1889, NPCs.VILLAGER_1890, NPCs.VILLAGER_1891, NPCs.VILLAGER_1892, NPCs.VILLAGER_1893, NPCs.VILLAGER_1894, NPCs.VILLAGER_1895, NPCs.VILLAGER_1896, NPCs.VILLAGER_1897, NPCs.VILLAGER_1898, NPCs.VILLAGER_1899, NPCs.VILLAGER_1900), 30, 74.0, 240.0, 8.0, 2, 2, 5, "Thief! Thief! Get away from me.", WeightBasedTable.create(WeightedItem(Items.COINS_995, 5, 5, 1.0, true)),),
    ROGUE(intArrayOf(NPCs.ROGUE_187, NPCs.ROGUE_GUARD_2267, NPCs.ROGUE_GUARD_2268, NPCs.ROGUE_GUARD_2269, NPCs.ROGUE_8122), 32, 74.0, 240.0, 35.5, 2, 2, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 25, 40, 5.0, true), WeightedItem(Items.JUG_OF_WINE_1993, 1, 1, 6.0), WeightedItem(Items.AIR_RUNE_556, 8, 8, 8.0), WeightedItem(Items.LOCKPICK_1523, 1, 1, 5.0), WeightedItem(Items.IRON_DAGGERP_1219, 1, 1, 1.0)),),
    CAVE_GOBLIN(intArrayOf(NPCs.CAVE_GOBLIN_5752, NPCs.CAVE_GOBLIN_5768), 36, 72.0, 240.0, 40.0, 1, 1, 5, null, WeightBasedTable.create(WeightedItem(Items.BAT_SHISH_10964, 1, 1, 2.5), WeightedItem(Items.FINGERS_10965, 1, 1, 2.5), WeightedItem(Items.COATED_FROGS_LEGS_10963, 1, 1, 2.5), WeightedItem(Items.COINS_995, 30, 30, 6.5), WeightedItem(Items.OIL_LAMP_4522, 1, 1, 0.5), WeightedItem(Items.BULLSEYE_LANTERN_4544, 1, 1, 0.5), WeightedItem(Items.UNLIT_TORCH_596, 1, 1, 0.5), WeightedItem(Items.TINDERBOX_590, 1, 1, 0.5), WeightedItem(Items.SWAMP_TAR_1939, 1, 1, 0.5), WeightedItem(Items.IRON_ORE_441, 1, 4, 0.25)),),
    MASTER_FARMER(intArrayOf(NPCs.MASTER_FARMER_2234, NPCs.MASTER_FARMER_2235, NPCs.MARTIN_THE_MASTER_GARDENER_3299), 38, 90.0, 240.0, 43.0, 3, 3, 5, "Cor blimey, mate! What are ye doing in me pockets?", WeightBasedTable.create(WeightedItem(Items.POTATO_SEED_5318, 1, 3, 50.0), WeightedItem(Items.ONION_SEED_5319, 1, 3, 50.0), WeightedItem(Items.CABBAGE_SEED_5324, 1, 3, 50.0), WeightedItem(Items.TOMATO_SEED_5322, 1, 2, 50.0), WeightedItem(Items.SWEETCORN_SEED_5320, 1, 2, 50.0), WeightedItem(Items.STRAWBERRY_SEED_5323, 1, 1, 25.0), WeightedItem(Items.WATERMELON_SEED_5321, 1, 1, 8.0), WeightedItem(Items.BARLEY_SEED_5305, 1, 4, 50.0), WeightedItem(Items.HAMMERSTONE_SEED_5307, 1, 3, 50.0), WeightedItem(Items.ASGARNIAN_SEED_5308, 1, 3, 50.0), WeightedItem(Items.JUTE_SEED_5306, 1, 3, 50.0), WeightedItem(Items.YANILLIAN_SEED_5309, 1, 2, 25.0), WeightedItem(Items.KRANDORIAN_SEED_5310, 1, 2, 25.0), WeightedItem(Items.WILDBLOOD_SEED_5311, 1, 1, 8.0), WeightedItem(Items.MARIGOLD_SEED_5096, 1, 1, 50.0), WeightedItem(Items.NASTURTIUM_SEED_5098, 1, 1, 50.0), WeightedItem(Items.ROSEMARY_SEED_5097, 1, 1, 50.0), WeightedItem(Items.WOAD_SEED_5099, 1, 1, 50.0), WeightedItem(Items.LIMPWURT_SEED_5100, 1, 1, 25.0), WeightedItem(Items.REDBERRY_SEED_5101, 1, 1, 50.0), WeightedItem(Items.CADAVABERRY_SEED_5102, 1, 1, 50.0), WeightedItem(Items.DWELLBERRY_SEED_5103, 1, 1, 25.0), WeightedItem(Items.JANGERBERRY_SEED_5104, 1, 1, 25.0), WeightedItem(Items.WHITEBERRY_SEED_5105, 1, 1, 25.0), WeightedItem(Items.GUAM_SEED_5291, 1, 1, 50.0), WeightedItem(Items.MARRENTILL_SEED_5292, 1, 1, 50.0), WeightedItem(Items.TARROMIN_SEED_5293, 1, 1, 50.0), WeightedItem(Items.HARRALANDER_SEED_5294, 1, 1, 25.0), WeightedItem(Items.RANARR_SEED_5295, 1, 1, 8.0), WeightedItem(Items.TOADFLAX_SEED_5296, 1, 1, 8.0), WeightedItem(Items.IRIT_SEED_5297, 1, 1, 8.0), WeightedItem(Items.AVANTOE_SEED_5298, 1, 1, 8.0), WeightedItem(Items.KWUARM_SEED_5299, 1, 1, 8.0), WeightedItem(Items.SNAPDRAGON_SEED_5300, 1, 1, 5.0), WeightedItem(Items.CADANTINE_SEED_5301, 1, 1, 8.0), WeightedItem(Items.LANTADYME_SEED_5302, 1, 1, 5.0), WeightedItem(Items.DWARF_WEED_SEED_5303, 1, 1, 5.0), WeightedItem(Items.TORSTOL_SEED_5304, 1, 1, 5.0)),),
    GUARD(intArrayOf(NPCs.GUARD_9, NPCs.GUARD_32, NPCs.GUARD_206, NPCs.GUARD_296, NPCs.GUARD_297, NPCs.GUARD_298, NPCs.GUARD_299, NPCs.GUARD_344, NPCs.GUARD_345, NPCs.GUARD_346, NPCs.GUARD_368, NPCs.GUARD_678, NPCs.GUARD_812, NPCs.GUARD_9, NPCs.GUARD_32, NPCs.GUARD_296, NPCs.GUARD_297, NPCs.GUARD_298, NPCs.GUARD_299, NPCs.GUARD_2699, NPCs.GUARD_2700, NPCs.GUARD_2701, NPCs.GUARD_2702, NPCs.GUARD_2703, NPCs.GUARD_3228, NPCs.GUARD_3229, NPCs.GUARD_3230, NPCs.GUARD_3231, NPCs.GUARD_3232, NPCs.GUARD_3233, NPCs.GUARD_3241, NPCs.GUARD_3407, NPCs.GUARD_3408, NPCs.GUARD_4307, NPCs.GUARD_4308, NPCs.GUARD_4309, NPCs.GUARD_4310, NPCs.GUARD_4311, NPCs.GUARD_5919, NPCs.GUARD_5920), 40, 50.0, 240.0, 46.5, 2, 2, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 30, 30, 1.0, true)),),
    FREMENNIK_CITIZEN(intArrayOf(NPCs.AGNAR_1305, NPCs.FREIDIR_1306, NPCs.BORROKAR_1307, NPCs.LANZIG_1308, NPCs.PONTAK_1309, NPCs.FREYGERD_1310, NPCs.LENSA_1311, NPCs.JENNELLA_1312, NPCs.SASSILIK_1313, NPCs.INGA_1314), 45, 65.0, 240.0, 65.0, 2, 2, 5, "You stay away from me Outlander!", WeightBasedTable.create(WeightedItem(Items.COINS_995, 40, 40, 1.0, true)),),
    BEARDED_BANDIT(intArrayOf(NPCs.BANDIT_1880, NPCs.BANDIT_1881, NPCs.BANDIT_6174, NPCs.BANDIT_6388), 45, 50.0, 240.0, 65.0, 5, 5, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.ANTIPOISON4_2446, 1, 1, 1.0), WeightedItem(Items.LOCKPICK_1523, 1, 1, 2.0), WeightedItem(Items.COINS_995, 1, 1, 4.0)),),
    DESERT_BANDIT(intArrayOf(NPCs.BANDIT_1926, NPCs.BARTENDER_1921), 53, 50.0, 240.0, 79.5, 3, 3, 5, "I'll kill you for that!", WeightBasedTable.create(WeightedItem(Items.COINS_995, 50, 1, 3.0), WeightedItem(Items.ANTIPOISON4_2446, 1, 1, 1.0), WeightedItem(Items.LOCKPICK_1523, 1, 1, 1.0)),),
    KNIGHT_OF_ADROUGNE(intArrayOf(NPCs.KNIGHT_OF_ARDOUGNE_23, NPCs.KNIGHT_OF_ARDOUGNE_26), 55, 50.0, 240.0, 84.3, 3, 3, 6, null, WeightBasedTable.create(WeightedItem(Items.COINS_995, 50, 50, 1.0, true))),
    YANILLE_WATCHMAN(intArrayOf(NPCs.WATCHMAN_34), 65, 50.0, 240.0, 137.5, 3, 3, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 60, 60, 1.0, true), WeightedItem(Items.BREAD_2309, 1, 1, 1.0, true)),),
    MENAPHITE_THUG(intArrayOf(NPCs.MENAPHITE_THUG_1905), 65, 50.0, 240.0, 137.5, 5, 5, 5, "I'll kill you for that!", WeightBasedTable.create(WeightedItem(Items.COINS_995, 60, 60, 1.0, true))),
    PALADIN(intArrayOf(NPCs.PALADIN_20, NPCs.PALADIN_2256), 70, 50.0, 150.0, 151.75, 3, 3, 5, "Hey! Get your hands off there!", WeightBasedTable.create(WeightedItem(Items.COINS_995, 80, 80, 1.0, true), WeightedItem(Items.CHAOS_RUNE_562, 2, 2, 1.0, true)),),
    GNOME(intArrayOf(NPCs.GNOME_66, NPCs.GNOME_67, NPCs.GNOME_68, NPCs.GNOME_WOMAN_168, NPCs.GNOME_WOMAN_169, NPCs.GNOME_2249, NPCs.GNOME_2250, NPCs.GNOME_2251, NPCs.GNOME_2371, NPCs.GNOME_2649, NPCs.GNOME_2650, NPCs.GNOME_6002, NPCs.GNOME_6004), 75, 8.0, 120.0, 198.5, 1, 1, 5, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 300, 300, 2.5), WeightedItem(Items.EARTH_RUNE_557, 1, 1, 3.5), WeightedItem(Items.GOLD_ORE_445, 1, 1, 1.0), WeightedItem(Items.FIRE_ORB_569, 1, 1, 5.0), WeightedItem(Items.SWAMP_TOAD_2150, 1, 1, 8.0), WeightedItem(Items.KING_WORM_2162, 1, 1, 9.0)),),
    HERO(intArrayOf(NPCs.HERO_21), 80, 6.0, 100.0, 273.3, 6, 6, 6, "What do you think you're doing?", WeightBasedTable.create(WeightedItem(Items.COINS_995, 200, 300, 1.5), WeightedItem(Items.DEATH_RUNE_560, 2, 2, 1.0), WeightedItem(Items.BLOOD_RUNE_565, 1, 1, 0.5), WeightedItem(Items.FIRE_ORB_569, 1, 1, 2.5), WeightedItem(Items.DIAMOND_1601, 1, 1, 2.0), WeightedItem(Items.GOLD_ORE_444, 1, 1, 1.5), WeightedItem(Items.JUG_OF_WINE_1993, 1, 1, 3.0)),),
    ;

    companion object {
        /**
         * A map that links object IDs to their corresponding [Pickpocket] enum entry.
         */
        val idMap: MutableMap<Int, Pickpocket> = HashMap(Pickpocket.values().size * 5)

        init {
            Pickpocket.values().forEach { pickpocket ->
                pickpocket.ids.forEach { id ->
                    idMap[id] = pickpocket
                }
            }
        }

        /**
         * Gets the [Pickpocket] instance associated with a specific object id.
         *
         * @param id The object ID used for lookup.
         * @return The corresponding [Pickpocket] instance, or `null` if not found.
         */
        @JvmStatic
        fun forID(id: Int): Pickpocket? = idMap[id]
    }

    /**
     * Computes the chance of successfully pickpocketing based on the thieving level.
     *
     * @param player The [Player] attempting to pickpocket.
     * @return A [Double] representing the success probability.
     */
    fun getSuccessChance(player: Player): Double =
        RandomFunction.getSkillSuccessChance(low, high, player.skills.getLevel(Skills.THIEVING))
}

enum class PickableDoor(val locations: Array<Location>, val level: Int, val experience: Double, val isLockpick: Boolean = false, val flipped: Boolean = false) {
    DOOR1(arrayOf(Location.create(3014, 3182)), 1, 0.0),
    DOOR2(arrayOf(Location.create(2672, 3308)), 1, 3.8),
    DOOR3(arrayOf(Location.create(2672, 3301)), 14, 15.0),
    DOOR4(arrayOf(Location.create(2610, 3316)), 15, 15.0),
    DOOR5(arrayOf(Location.create(3190, 3957)), 32, 25.0, isLockpick = true),
    DOOR6(arrayOf(Location.create(2565, 3356)), 46, 37.5),
    DOOR7(arrayOf(Location.create(2579, 3286, 1)), 61, 50.0),
    DOOR8(arrayOf(Location.create(2579, 3307, 1)), 61, 50.0),
    DOOR9(arrayOf(Location.create(3018, 3187)), 1, 0.0),
    DOOR10(arrayOf(Location.create(2601, 9482)), 82, 0.0, isLockpick = true),
    DOOR11(arrayOf(Location.create(3044, 3956)), 39, 35.0, isLockpick = true, flipped = true),
    DOOR12(arrayOf(Location.create(3041, 3959)), 39, 35.0, isLockpick = true, flipped = true),
    DOOR13(arrayOf(Location.create(3038, 3956)), 39, 35.0, isLockpick = true, flipped = true);

    fun open(player: Player, door: Scenery) {
        if (!correctSide(player, door)) {
            sendMessage(player, "The door is locked.")
            return
        }
        handleAutowalkDoor(player, door)
        sendMessage(player, "You go through the door.")
    }

    fun pick(player: Player, door: Scenery) {
        val inside = correctSide(player, door)
        val failTrap = RandomFunction.random(12) < 4
        val wrongSide = inside

        if (!wrongSide) {
            sendMessage(player, "The door is already unlocked.")
            return
        }

        if (getStatLevel(player, Skills.THIEVING) < level) {
            sendMessage(player, "You attempt to pick the lock.")
            if (RandomFunction.random(10) < 5) {
                impact(player, RandomFunction.random(1, 3), ImpactHandler.HitsplatType.NORMAL)
                sendMessage(player, "You have activated a trap on the lock.", 1)
            }
            return
        }

        if (isLockpick && !inInventory(player, Items.LOCKPICK_1523)) {
            sendMessage(player, "You need a lockpick in order to pick this lock.")
            return
        }

        val success = !failTrap
        rewardXP(player, Skills.THIEVING, experience)

        sendMessage(player, "You attempt to pick the lock.")
        sendMessage(player, "You ${if (success) "manage" else "fail"} to pick the lock.", 1)

        if (success) {
            handleAutowalkDoor(player, door)
            escape(player)
        }
    }

    private fun correctSide(player: Player, door: Scenery): Boolean {
        val dir = Direction.getLogicalDirection(player.location, door.location)
        return when (door.direction) {
            Direction.SOUTH -> dir == Direction.WEST
            Direction.EAST  -> dir == Direction.SOUTH
            Direction.NORTH -> dir == Direction.EAST
            else -> false
        }.xor(flipped)
    }

    companion object {
        fun forLocation(loc: Location): PickableDoor? =
            values().firstOrNull { door -> door.locations.any { it == loc } }
    }

    private fun escape(player: Player) {
        if (getAttribute(player, "shantay-jail", false)) {
            removeAttribute(player, "shantay-jail")
            sendNPCDialogueWithDelay(
                player, 2, NPCs.SHANTAY_836,
                "You should be in jail! But if you get into more trouble, you'll be back."
            )
        }
    }
}

enum class ChestsDefinition(val objectIds: IntArray, val level: Int, val xp: Double, val rewards: Array<Item>, val respawnTicks: Int) {
    TEN_COIN(intArrayOf(shared.consts.Scenery.CHEST_2566), 13, 7.8, arrayOf(Item(Items.COINS_995, 10)), 7),
    NATURE_RUNE(intArrayOf(shared.consts.Scenery.CHEST_2567), 28, 25.0, arrayOf(Item(Items.COINS_995, 3), Item(Items.NATURE_RUNE_561, 1)), 8),
    FIFTY_COIN(intArrayOf(shared.consts.Scenery.CHEST_2568), 43, 125.0, arrayOf(Item(Items.COINS_995, 50)), 55),
    STEEL_ARROWHEADS(intArrayOf(shared.consts.Scenery.CHEST_2573), 47, 150.0, arrayOf(Item(41, 5)), 210),
    BLOOD_RUNES(intArrayOf(shared.consts.Scenery.CHEST_2569), 59, 250.0, arrayOf(Item(Items.COINS_995, 500), Item(Items.BLOOD_RUNE_565, 2)), 135),
    PALADIN(intArrayOf(shared.consts.Scenery.CHEST_2570), 72, 500.0, arrayOf(Item(Items.COINS_995, 1000), Item(Items.RAW_SHARK_383, 1), Item(Items.ADAMANTITE_ORE_449, 1), Item(Items.UNCUT_SAPPHIRE_1623, 1)), 120);

    private var respawnUntil = 0

    val isRespawning: Boolean
        get() = ticks < respawnUntil

    fun markRespawn() {
        respawnUntil = ticks + (respawnTicks / 0.6).toInt()
    }

    /**
     * Opening without searching = trap damage.
     */
    fun open(player: Player, obj: Scenery) {
        if (isRespawning) {
            sendMessage(player, "It looks like this chest has already been looted.")
        } else {
            lock(player, 2)
            sendMessage(player, "You have activated a trap on the chest.")
            impact(player, hitDamage(player), HitsplatType.NORMAL)
        }
    }

    /**
     * Searching for traps
     */
    fun searchTraps(player: Player, scenery: Scenery) {
        player.faceLocation(scenery.location)

        if (isRespawning) {
            sendMessage(player, "It looks like this chest has already been looted.")
            return
        }

        if (getStatLevel(player, Skills.THIEVING) < level) {
            animate(player, Animations.HUMAN_OPEN_CHEST_536, false)
            lock(player, 2)
            sendMessage(player, "You search the chest for traps.")
            sendMessage(player, "You find nothing.", 1)
            return
        }

        if (freeSlots(player) == 0) {
            sendMessage(player, "Not enough inventory space.")
            return
        }

        lock(player, 6)
        animate(player, Animations.HUMAN_OPEN_CHEST_536, false)
        sendMessage(player, "You find a trap on the chest...")
        player.impactHandler.disabledTicks = 6

        queueScript(player, 0) {
            delayScript(player, 2)
            sendMessage(player, "You disable the trap.")

            delayScript(player, 2)
            animate(player, Animation.create(Animations.HUMAN_OPEN_CHEST_536))
            player.faceLocation(scenery.location)
            sendMessage(player, "You open the chest.")

            delayScript(player, 2)
            rewards.forEach { player.inventory.add(it, player) }
            sendMessage(player, "You find treasure inside!")
            rewardXP(player, Skills.THIEVING, xp)

            if (scenery.isActive) {
                replaceScenery(scenery, 2574, 3)
            }

            markRespawn()
            stopExecuting(player)
        }
    }

    companion object
    {
        fun forId(id: Int): ChestsDefinition? = values().firstOrNull { id in it.objectIds }
        val allObjectIds: IntArray = values().flatMap { it.objectIds.asList() }.toIntArray()
        fun hitDamage(player: Player): Int = maxOf(2, player.skills.lifepoints / 12)
    }
}

