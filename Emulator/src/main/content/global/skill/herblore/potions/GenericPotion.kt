package content.global.skill.herblore.potions

import core.game.node.item.Item

/**
 * Represents a generic potion.
 */
class GenericPotion(val base: Item?, val ingredient: Item?, val level: Int, val experience: Double, val product: Item?) {
    companion object {
        /**
         * Transforms an unfinished potion into a generic potion.
         *
         * @param potion The unfinished potion to transform.
         * @return A [GenericPotion] representing the unfinished potion.
         */
        fun transform(potion: UnfinishedPotion): GenericPotion =
            GenericPotion(
                base = potion.base,
                ingredient = potion.ingredient,
                level = potion.level,
                experience = 0.0,
                product = potion.potion,
            )

        /**
         * Transforms a finished potion into a generic potion.
         *
         * @param potion The finished potion to transform.
         * @return A [GenericPotion] representing the finished potion.
         */
        fun transform(potion: FinishedPotion): GenericPotion =
            GenericPotion(
                base = potion.unfinished.potion,
                ingredient = potion.ingredient,
                level = potion.level,
                experience = potion.experience,
                product = potion.potion,
            )
    }
}
