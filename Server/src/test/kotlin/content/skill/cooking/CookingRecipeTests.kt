package kotlin.content.skill.cooking

import content.global.skill.cooking.CookingRecipePlugin
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import shared.consts.Items

class CookingRecipeTests {

    private lateinit var testPlayer: Player

    @BeforeEach
    fun setup() {
        testPlayer.skills.setLevel(Skills.COOKING, 50)
        testPlayer.inventory.clear()
    }

    init {
        TestUtils.preTestSetup()
        testPlayer = TestUtils.getMockPlayer("test")
    }

    @Test
    fun `should cook recipe successfully when requirements are met`() {
        val recipe = CookingRecipePlugin.CookingRecipe.UNCOOKED_EGG
        testPlayer.inventory.add(Item(Items.EGG_1944, 1))
        testPlayer.inventory.add(Item(Items.BOWL_1923, 1))

        CookingRecipePlugin.handle(testPlayer, recipe, Items.EGG_1944, Items.BOWL_1923)

        Assertions.assertEquals(1, testPlayer.inventory.getAmount(recipe.productId!!))
    }

    @Test
    fun `should not cook if player level too low`() {
        val recipe = CookingRecipePlugin.CookingRecipe.UNCOOKED_EGG
        testPlayer.skills.setLevel(Skills.COOKING, 1)
        testPlayer.inventory.add(Item(Items.EGG_1944, 1))
        testPlayer.inventory.add(Item(Items.BOWL_1923, 1))

        CookingRecipePlugin.handle(testPlayer, recipe, Items.EGG_1944, Items.BOWL_1923)

        Assertions.assertEquals(0, testPlayer.inventory.getAmount(recipe.productId!!))
    }

    @Test
    fun `should award XP after cooking`() {
        val recipe = CookingRecipePlugin.CookingRecipe.UNCOOKED_EGG
        testPlayer.inventory.add(Item(Items.EGG_1944, 1))
        testPlayer.inventory.add(Item(Items.BOWL_1923, 1))

        val initialXP = testPlayer.skills.getExperience(Skills.COOKING)
        CookingRecipePlugin.handle(testPlayer, recipe, Items.EGG_1944, Items.BOWL_1923)
        val xpAfter = testPlayer.skills.getExperience(Skills.COOKING)

        Assertions.assertTrue(xpAfter > initialXP)
    }

    @Test
    fun `should fail cooking when missing ingredients`() {
        val recipe = CookingRecipePlugin.CookingRecipe.UNCOOKED_EGG

        CookingRecipePlugin.handle(testPlayer, recipe, Items.EGG_1944, Items.BOWL_1923)

        Assertions.assertEquals(0, testPlayer.inventory.getAmount(recipe.productId!!))
    }

    @Test
    fun `should handle multi-step recipe like cake`() {
        val recipe = CookingRecipePlugin.CookingRecipe.UNCOOKED_CAKE
        testPlayer.inventory.add(Item(Items.POT_OF_FLOUR_1933, 1))
        testPlayer.inventory.add(Item(Items.BUCKET_OF_MILK_1927, 1))
        testPlayer.inventory.add(Item(Items.EGG_1944, 1))
        testPlayer.inventory.add(Item(Items.CAKE_TIN_1887, 1))

        CookingRecipePlugin.handle(testPlayer, recipe, Items.POT_OF_FLOUR_1933, Items.CAKE_TIN_1887)

        Assertions.assertEquals(1, testPlayer.inventory.getAmount(recipe.productId!!))
    }

    @Test
    fun `should not cook when inventory full`() {
        val recipe = CookingRecipePlugin.CookingRecipe.UNCOOKED_EGG
        testPlayer.inventory.add(Item(Items.EGG_1944, 1))
        testPlayer.inventory.add(Item(Items.BOWL_1923, 1))
        val free = testPlayer.inventory.freeSlots()
        for (i in 0 until free) {
            testPlayer.inventory.add(Item(Items.POT_OF_FLOUR_1933, 1))
        }

        CookingRecipePlugin.handle(testPlayer, recipe, Items.EGG_1944, Items.BOWL_1923)

        Assertions.assertEquals(0, testPlayer.inventory.getAmount(recipe.productId!!))
    }
}
