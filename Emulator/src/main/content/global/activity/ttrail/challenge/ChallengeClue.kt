package content.global.activity.ttrail.challenge

import content.global.activity.ttrail.ClueLevel
import core.plugin.Plugin
import org.rs.consts.Items
import org.rs.consts.NPCs

/**
 * Represents Challenge clue scrolls.
 */
open class ChallengeClue : ChallengeClueScroll {

    /**
     * Default constructor.
     */
    constructor() : super(null, -1, null, null, null, null)

    /**
     * Constructs a challenge clue scroll.
     *
     * @param name      the internal name of the clue
     * @param clueId    the item ID representing the clue scroll
     * @param level     the difficulty level of the clue
     * @param question  the question text the player must answer
     * @param npc       the NPC associated with this clue
     * @param answer    the correct answer to the challenge
     */
    constructor(name: String, clueId: Int, level: ClueLevel, question: String, npc: Int, answer: Int) : super(
        name,
        clueId,
        level,
        question,
        npc,
        answer,
    )

    /**
     * Initializes and registers the challenge clues.
     *
     * @param arg optional argument, not used
     * @return this instance of [ChallengeClue]
     */
    override fun newInstance(arg: Any?): Plugin<Any?>? {
        register(ChallengeClue("zoo_keeper", Items.CLUE_SCROLL_7301, ClueLevel.HARD, "How many animals<br>are in the Ardougne Zoo?", NPCs.ZOO_KEEPER_28, 40))
        register(ChallengeClue("luthas", Items.CLUE_SCROLL_7303, ClueLevel.HARD, "How many banana trees<br>are there in the plantation?", NPCs.LUTHAS_379, 33))
        register(ChallengeClue("king_roald", Items.CLUE_SCROLL_7304, ClueLevel.MEDIUM, "How many bookcases<br>are in the palace library?", NPCs.KING_ROALD_648, 24))
        register(ChallengeClue("gabooty", Items.CLUE_SCROLL_7305, ClueLevel.MEDIUM, "How many buildings<br>in the village?", NPCs.GABOOTY_2520, 11))
        register(ChallengeClue("cook", Items.CLUE_SCROLL_7307, ClueLevel.MEDIUM, "How many cannons does<br>Lumbridge castle have?", NPCs.COOK_278, 9))
        register(ChallengeClue("caroline", Items.CLUE_SCROLL_7309, ClueLevel.MEDIUM, "How many fishermen<br>on the fishing platform?", NPCs.CAROLINE_696, 11))
        register(ChallengeClue("bolkoy", Items.CLUE_SCROLL_7311, ClueLevel.HARD, "How many flowers are<br>in the clearing below<br>this platform?", NPCs.BOLKOY_471, 13))
        register(ChallengeClue("gnome_coach", Items.CLUE_SCROLL_7313, ClueLevel.HARD, "How many gnomes on the<br>gnome ball field<br>have red patches on<br>their uniforms?", NPCs.GNOME_COACH_2802, 6))
        register(ChallengeClue("recruiter", Items.CLUE_SCROLL_7315, ClueLevel.MEDIUM, "How many houses have<br>a cross on the door?", NPCs.RECRUITER_720, 20))
        register(ChallengeClue("brundt_the_chief", Items.CLUE_SCROLL_7317, ClueLevel.MEDIUM, "How many people are<br>waiting for the next<br>bard to perform?", NPCs.BRUNDT_THE_CHIEFTAIN_1294, 4))
        register(ChallengeClue("edmond", Items.CLUE_SCROLL_10180, ClueLevel.MEDIUM, "How many pigeon cages<br>are there around the back<br>of Jerico's house?", NPCs.EDMOND_714, 3))
        register(ChallengeClue("oracle", Items.CLUE_SCROLL_10182, ClueLevel.MEDIUM, "If x is 15 and y is 3,<br>what is 3x + y?", NPCs.ORACLE_746, 48))
        register(ChallengeClue("karim", Items.CLUE_SCROLL_10184, ClueLevel.MEDIUM, "I have 16 kebabs, I eat one myself<br>and share the rest equally between 3 friends.<br>How many do they have each?", NPCs.KARIM_543, 5))
        register(ChallengeClue("hazelmere", Items.CLUE_SCROLL_10186, ClueLevel.MEDIUM, "What is 19 to<br>the power of 3?", NPCs.HAZELMERE_669, 6859))
        register(ChallengeClue("gnome_ball_referee", Items.CLUE_SCROLL_10188, ClueLevel.MEDIUM, "What is 57x89 + 23?", NPCs.GNOME_BALL_REFEREE_635, 5096))
        return this
    }
}
