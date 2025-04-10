package content.global.skill.herblore.herbs

import org.rs.consts.Items

/**
 * Enum representing different types of Tars used in Herblore.
 *
 * Each type of Tar has a corresponding ingredient, required level, experience gained when used,
 * and the resulting product when used in the Herblore skill.
 *
 * @property ingredient The id of the ingredient used to create the Tar.
 * @property level The Herblore level required to use the Tar.
 * @property experience The amount of experience gained for creating the Tar.
 * @property product The ID of the product produced when the Tar is created.
 */
enum class Tars(
    val ingredient: Int,
    val level: Int,
    val experience: Double,
    val product: Int,
) {
    GUAM_TAR(Herbs.GUAM.product.id, 19, 30.0, Items.GUAM_TAR_10142),
    GROUND_GUAM_TAR(Items.GROUND_GUAM_6681, 19, 30.0, Items.GUAM_TAR_10142),
    MARRENTILL_TAR(Herbs.MARRENTILL.product.id, 31, 42.5, Items.MARRENTILL_TAR_10143),
    TARROMIN_TAR(Herbs.TARROMIN.product.id, 39, 55.0, Items.TARROMIN_TAR_10144),
    HARRALANDER_TAR(Herbs.HARRALANDER.product.id, 44, 72.5, Items.HARRALANDER_TAR_10145),
    ;

    companion object {
        /**
         * Finds the [Tars] enum value for the given ingredient ids.
         *
         * @param id The id of the ingredient.
         * @return The corresponding [Tars] enum value or null if no match is found.
         */
        fun forId(id: Int): Tars? {
            for (tar in values()) {
                if (tar.ingredient == id) {
                    return tar
                }
            }
            return null
        }
    }
}
