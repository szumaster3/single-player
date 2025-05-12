package content.global.activity.ttrail.anagram

import content.global.activity.ttrail.ClueLevel
import content.global.activity.ttrail.puzzle.PuzzleBox
import org.rs.consts.Items
import org.rs.consts.NPCs

/**
 * Represents the Anagram clues.
 */
enum class AnagramClue(
    val clueId: Int,
    val anagram: String,
    val npcId: Int,
    val level: ClueLevel,
    val challenge: Int?
) {
    A_BAS(Items.CLUE_SCROLL_7236, "A Bas", NPCs.SABA_1070, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    A_ZEN_SHE(Items.CLUE_SCROLL_7238, "A Zen She", NPCs.ZENESHA_589, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ACE_MATCH_ELM(Items.CLUE_SCROLL_7239, "Ace Match Elm", NPCs.CAM_THE_CAMEL_2812, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    AHA_JAR(Items.CLUE_SCROLL_7241,"Aha Jar", NPCs.JARAAH_962, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    AN_PAINT_TONIC(Items.CLUE_SCROLL_7243,"An Paint Tonic", NPCs.CAPTAIN_NINTO_4594, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ARC_O_LINE(Items.CLUE_SCROLL_7245, "Arc O Line", NPCs.CAROLINE_696, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ARE_COL(Items.CLUE_SCROLL_7247,"Are Col", NPCs.ORACLE_746, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ARR_SO_I_AM_A_CRUST(Items.CLUE_SCROLL_7248,"Arr! So I am a crust, and?", NPCs.RAMARA_DU_CROISSANT_3827, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    BAIL_TRIMS(Items.CLUE_SCROLL_7249,"Bail Trims", NPCs.BRIMSTAIL_171, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    BY_LOOK(Items.CLUE_SCROLL_7250,"By Look", NPCs.BOLKOY_471, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    C_ON_GAME_HOC(Items.CLUE_SCROLL_7251,"C on game hoc", NPCs.GNOME_COACH_2802, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    DT_RUN_B(Items.CLUE_SCROLL_7252,"Dt Run B", NPCs.BRUNDT_THE_CHIEFTAIN_1294, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    EEK_ZERO_OP(Items.CLUE_SCROLL_7253,"Eek Zero Op", NPCs.ZOO_KEEPER_28, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    EL_OW(Items.CLUE_SCROLL_7254,"El Ow", NPCs.LOWE_550, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ERR_CURE_IT(Items.CLUE_SCROLL_7255,"Err Cure It", NPCs.RECRUITER_720, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    GOBLIN_KERN(Items.CLUE_SCROLL_7256,"Goblin Kern", NPCs.KING_BOLREN_469, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    GOT_A_BOY(Items.CLUE_SCROLL_7258,"Got A Boy", NPCs.GABOOTY_2520, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    GULAG_RUN(Items.CLUE_SCROLL_7260,"Gulag Run", NPCs.UGLUG_NAR_2039, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    HALT_US(Items.CLUE_SCROLL_7262,"Halt Us", NPCs.LUTHAS_379, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    HE_DO_POSE(Items.CLUE_SCROLL_7264,"He Do Pose. It Is Cultrrl, Mk?", NPCs.RIKI_THE_SCULPTORS_MODEL_2143, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ICY_FE(Items.CLUE_SCROLL_7266,"Icy Fe", NPCs.FYCIE_1011, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    I_EAT_ITS_CHART_HINTS_DO_U(Items.CLUE_SCROLL_7268,"I Eat Its Chart Hints Do U", NPCs.SHIRATTI_THE_CUSTODIAN_3044, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    I_FAFFY_RUN(Items.CLUE_SCROLL_7270,"I Faffy Run", NPCs.FAIRY_NUFF_3303, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    LAND_DOOMD(Items.CLUE_SCROLL_7272,"Land Doomd", NPCs.ODD_OLD_MAN_3670, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    LARK_IN_DOG(Items.CLUE_SCROLL_7274,"Lark In Dog", NPCs.KING_ROALD_648, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ME_AM_THE_CALC(Items.CLUE_SCROLL_7276,"Me Am The Calc", NPCs.CAM_THE_CAMEL_2813, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    ME_IF(Items.CLUE_SCROLL_7278,"Me if", NPCs.FEMI_676, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    NOD_MED(Items.CLUE_SCROLL_7280,"Nod med", NPCs.EDMOND_714, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    O_BIRDZ_A_ZANY_EN_PC(Items.CLUE_SCROLL_7282,"O Birdz A Zany En Pc", NPCs.CAPN_IZZY_NO_BEARD_437, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    OK_CO(Items.CLUE_SCROLL_7284,"Ok Co", NPCs.COOK_278, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    OR_ZINC_FUMES_WARD(Items.CLUE_SCROLL_7286,"Or Zinc Fumes Ward", NPCs.WIZARD_CROMPERTY_2328, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    PEATY_PERT(Items.CLUE_SCROLL_7288,"Peaty Pert", NPCs.PARTY_PETE_659, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    PROFS_LOSE_WRONG_PIE(Items.CLUE_SCROLL_7290,"Profs Lose Wrong Pie", NPCs.PROFESSOR_ODDENSTEIN_286, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    R_AK_MI(Items.CLUE_SCROLL_7292,"R Ak Mi", NPCs.KARIM_543, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    RED_ART_TANS(Items.CLUE_SCROLL_7294,"Red Art Tans", NPCs.TRADER_STAN_4650, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    SEQUIN_DIRGE(Items.CLUE_SCROLL_7296,"Sequin Dirge", NPCs.QUEEN_SIGRID_1359, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    SNAH(Items.CLUE_SCROLL_7298,"Snah", NPCs.HANS_0, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
    THEM_CALL_CAME(Items.CLUE_SCROLL_7300,"Them Call Came", NPCs.CAM_THE_CAMEL_2813, ClueLevel.HARD, PuzzleBox.getRandomPuzzleBox()),
}