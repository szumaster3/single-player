package core.game.node.entity.skill

import core.api.getStatLevel
import core.game.node.entity.player.Player

/**
 * Represents a skill jingle with optional levels
 * that trigger the jingle, and ids for low and high jingles.
 */
private data class SkillJingle(
    val levels: Set<Int>? = null,
    val low: Int,
    val high: Int
)

/**
 * Map of skill identifiers to their [SkillJingle].
 */
private val skillJingles: Map<Int, SkillJingle> = mapOf(
    0 to  SkillJingle(setOf(5,10,15,20,30,40,50,60,70,75,78,99),29,30),//Attack
    1 to  SkillJingle(setOf(5,10,20,25,30,40,45,50,55,60,65,70,75,78,80,99),37,38),//Defence
    2 to  SkillJingle(low=65,high=66),//Strength
    3 to  SkillJingle(low=47,high=48),//Hitpoints
    4 to  SkillJingle(setOf(5,10,16,19,20,21,25,26,28,31,30,33,35,36,37,39,40,42,45,46,50,55,60,61,65,70,78,80,99),57,58),//Ranged
    5 to  SkillJingle(setOf(2,4,7,8,9,10,13,16,19,22,25,26,28,31,34,35,37,40,43,44,45,46,49,52,60,70,85,90,99),55,56),//Prayer
    6 to  SkillJingle(setOf(4,5,7,9,11,13,14,15,17,19,20,21,23,24,25,27,29,30,31,32,33,35,37,39,40,43,45,47,49,50,51,52,53,54,55,56,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,90,91,92,93,94,95,96,97,99),51,52),//Magic
    7 to  SkillJingle(setOf(4,5,6,7,8,10,11,12,14,15,16,17,18,19,20,21,22,23,24,25,28,29,30,31,32,33,34,35,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,53,54,55,57,58,59,60,62,64,65,67,68,70,80,82,85,91,95,99),33,34),//Cooking
    8 to  SkillJingle(setOf(6,10,12,15,20,21,27,30,31,35,37,40,41,42,44,45,50,54,56,57,58,60,61,75,80,82,99),69,70),//Woodcutting
    9 to  SkillJingle(setOf(5,7,9,10,11,15,18,20,22,24,25,26,30,32,33,35,37,38,39,40,41,42,43,45,46,48,49,50,51,52,53,54,55,55,56,58,59,60,61,62,63,65,67,69,70,71,73,75,77,80,81,85,90,95,99),43,44),//Fletching
    10 to SkillJingle(setOf(5,10,15,16,20,23,25,28,30,33,35,38,40,46,48,50,53,55,58,62,65,68,70,76,79,81,90,96,99),41,42),//Fishing
    11 to SkillJingle(setOf(4,5,11,12,15,20,21,25,26,30,33,35,40,42,43,45,47,48,49,50,52,53,55,58,59,60,62,63,65,68,70,72,75,76,78,79,80,83,85,87,89,92,95,99),39,40),//Firemaking
    12 to SkillJingle(setOf(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,60,62,63,64,66,67,68,70,71,73,74,75,77,79,80,82,84,85,87,90,99),35,36),//Crafting
    13 to SkillJingle(setOf(2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,46,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,68,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99),63,64),//Smithing
    14 to SkillJingle(setOf(6,10,15,20,21,30,31,35,40,41,45,46,50,55,60,70,80,85,90,99),53,54),//Mining
    15 to SkillJingle(setOf(3,5,8,11,12,19,20,22,25,26,30,31,34,35,36,38,39,40,44,45,48,50,52,53,54,55,57,59,60,63,65,66,67,68,69,70,72,73,75,76,78,80,81,82,99),45,46),//Herblore
    16 to SkillJingle(low=28,high=28),//Agility
    17 to SkillJingle(setOf(2,5,10,13,15,20,22,25,27,28,30,32,35,36,38,40,42,43,44,45,47,49,50,52,53,55,59,63,65,70,72,75,78,80,82,85,99),67,68),//Thieving
    18 to SkillJingle(setOf(5,7,10,15,17,20,22,25,30,32,33,35,37,39,40,42,45,47,50,51,52,55,56,57,58,59,60,63,65,68,70,72,75,80,83,85,90,99),61,62),//Slayer
    19 to SkillJingle(setOf(2,3,4,5,7,8,9,10,11,12,13,14,15,16,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,36,38,39,42,44,45,47,48,50,51,52,53,55,56,57,59,60,62,63,67,68,70,72,73,75,79,83,85,99),10,11),//Farming
    20 to SkillJingle(setOf(2,5,6,9,10,11,13,14,15,19,20,22,23,26,27,28,33,35,38,40,42,44,46,52,54,55,56,57,59,65,66,70,74,76,77,78,82,84,91,92,95,98,99),59,60),//Runecrafting
    21 to SkillJingle(low=50,high=49),//Hunter
    22 to SkillJingle(low=32,high=31),//Construction
    23 to SkillJingle(low=300,high=301)//Summoning
)

/**
 * Gets the jingle for skill slot.
 *
 * @param player The player.
 * @param slot The skill slot.
 * @return The jingle to play.
 */
fun getSkillJingle(player: Player, slot: Int): Int {
    val level = getStatLevel(player, slot)
    val jingle = skillJingles[slot] ?: return 40

    return when (slot) {
        2, 3, 23 -> if (level < 50) jingle.low else jingle.high
        21 -> if (level % 2 == 0) jingle.high else jingle.low
        22 -> if (level % 10 == 0) jingle.high else jingle.low
        else -> if (jingle.levels?.contains(level) == true) jingle.high else jingle.low
    }
}