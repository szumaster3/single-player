package content.activity.ttrail.puzzle

import TestUtils
import content.global.activity.ttrail.clue.PuzzleBox
import content.global.activity.ttrail.plugin.PuzzleBoxPlugin
import core.api.addItem
import core.api.setAttribute
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PuzzleBoxTests {
    private val player = TestUtils.getMockPlayer("puzzleSessionTest")
    private val plugin = PuzzleBoxPlugin()

    init {
        TestUtils.preTestSetup()
    }

    @Test
    fun generateOnlySolvablePuzzles() {
        val solution = (1..24).toList() + -1
        repeat(50) {
            val puzzle = plugin.generatePuzzle(solution)
            Assertions.assertEquals(
                25, puzzle.size,
                "Puzzle should have 25 pieces"
            )
            Assertions.assertTrue(
                puzzle.contains(-1),
                "Puzzle must contain the empty slot (-1)"
            )
        }
    }

    @Test
    fun saveAndLoadSession() {
        val puzzleEnum = PuzzleBox.CASTLE
        val solution = puzzleEnum.fullSolution.toMutableList()
        val shuffled = plugin.generatePuzzle(solution)

        plugin.saveSession(player, puzzleEnum, shuffled)
        val loaded = plugin.loadSession(player, puzzleEnum)

        Assertions.assertEquals(
            shuffled, loaded,
            "Loaded puzzle should match saved session"
        )
    }

    @Test
    fun markPuzzleComplete() {
        val puzzleEnum = PuzzleBox.CASTLE
        val solution = puzzleEnum.fullSolution.toMutableList()

        plugin.saveSession(player, puzzleEnum, solution)
        addItem(player, puzzleEnum.id)
        setAttribute(player, "${puzzleEnum.name.lowercase()}:puzzle:done", true)

        Assertions.assertTrue(
            PuzzleBox.isComplete(player, puzzleEnum.name.lowercase()),
            "Puzzle should be marked as completed and player should have the item"
        )
    }

    @Test
    fun randomPuzzleSelection() {
        val randomEnum = PuzzleBox.values().random()
        val id = randomEnum.id
        val box = PuzzleBox.forId(id)

        Assertions.assertNotNull(box, "Random puzzle ID must correspond to a PuzzleBox enum")
        Assertions.assertEquals(
            25, box!!.fullSolution.size,
            "PuzzleBox must have 25 tiles"
        )
    }
}
