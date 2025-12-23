package content.global.skill.thieving.blackjack

data class BlackjackState(
    var unconsciousUntil: Int = 0,
    var pickpocketsLeft: Int = 0
) {
    fun isUnconscious(ticks: Int) = ticks < unconsciousUntil
}