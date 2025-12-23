package content.global.skill.thieving.blackjack

import shared.consts.Items

enum class BlackjackType(
    val itemIds: IntArray,
    val thievingLevel: Int,
    val attackLevel: Int = 1,
    val defenceLevel: Int = 1,
    val damageBonus: Int
) {
    ORDINARY(
        itemIds = intArrayOf(
            Items.OAK_BLACKJACK_4599,
            Items.WILLOW_BLACKJACK_4600,
            Items.MAPLE_BLACKJACK_6416
        ),
        thievingLevel = 30,
        damageBonus = 1
    ),

    OFFENSIVE(
        itemIds = intArrayOf(
            Items.OAK_BLACKJACKO_6408,
            Items.WILLOW_BLACKJACKO_6412,
            Items.MAPLE_BLACKJACKO_6418
        ),
        thievingLevel = 30,
        attackLevel = 30,
        damageBonus = 6
    ),

    DEFENSIVE(
        itemIds = intArrayOf(
            Items.OAK_BLACKJACKD_6410,
            Items.WILLOW_BLACKJACKD_6414,
            Items.MAPLE_BLACKJACKD_6420
        ),
        thievingLevel = 30,
        defenceLevel = 30,
        damageBonus = 0
    );

    companion object {
        fun fromItem(id: Int) =
            values().firstOrNull { id in it.itemIds }
    }
}
