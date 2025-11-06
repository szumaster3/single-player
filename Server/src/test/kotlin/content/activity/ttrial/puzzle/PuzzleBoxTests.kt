package content.activity.ttrial.puzzle

import TestUtils
import content.global.activity.ttrail.plugin.Puzzle
import content.global.activity.ttrail.plugin.PuzzleBoxPlugin
import core.api.addItem
import core.api.setAttribute
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PuzzleBoxTests {
    private val p = TestUtils.getMockPlayer("puzzleSessionTest")
    private val plugin = PuzzleBoxPlugin()

    init {
        TestUtils.preTestSetup()
    }

    @Test
    fun generateOnlySolvablePuzzles() {
        val solution = (1..24).toList() + -1
        repeat(50) {
            val puzzle = plugin.generatePuzzle(solution)
            Assertions.assertEquals(25, puzzle.size, "Puzzle should have 25 pieces")
            Assertions.assertTrue(puzzle.contains(-1), "Puzzle must contain the empty slot (-1)")
        }
    }

    @Test
    fun saveAndLoadSession() {
        val type = "castle"
        val solution = Puzzle.forType(type)!!.fullSolution.toMutableList()
        val shuffled = plugin.generatePuzzle(solution)

        plugin.saveSession(p, type, shuffled)
        val loaded = plugin.loadSession(p, type)

        Assertions.assertEquals(shuffled, loaded, "Loaded puzzle should match saved session")
    }

    @Test
    fun markPuzzleComplete() {
        val type = "castle"
        val solution = Puzzle.forType(type)!!.fullSolution

        plugin.saveSession(p, type, solution.toMutableList())
        addItem(p, Puzzle.forType(type)!!.id)
        setAttribute(p, "$type:puzzle:done", true)

        Assertions.assertTrue(
            Puzzle.isComplete(p, type),
            "Puzzle should be marked as completed and player should have the item"
        )
    }

    @Test
    fun randomPuzzleSelection() {
        val id = Puzzle.random()
        val box = Puzzle.forId(id)
        Assertions.assertNotNull(box, "Random puzzle ID must correspond to a Puzzle")
        Assertions.assertEquals(25, box!!.fullSolution.size, "Puzzle must have 25 tiles")
    }
}
