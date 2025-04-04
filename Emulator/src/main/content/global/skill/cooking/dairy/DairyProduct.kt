package content.global.skill.cooking.dairy

import core.game.node.item.Item
import org.rs.consts.Items
import java.util.*

/**
 * Represents dairy products.
 */
enum class DairyProduct(
    val level: Int, val experience: Double, val product: Item, inputs: Array<Int>
) {
    POT_OF_CREAM(
        21,
        18.0,
        Item(Items.POT_OF_CREAM_2130, 1),
        arrayOf(Items.BUCKET_OF_MILK_1927)
    ),
    PAT_OF_BUTTER(
        38,
        40.5,
        Item(Items.PAT_OF_BUTTER_6697, 1),
        arrayOf(Items.BUCKET_OF_MILK_1927, Items.POT_OF_CREAM_2130)
    ),
    CHEESE(
        48,
        64.0,
        Item(Items.CHEESE_1985, 1),
        arrayOf(Items.BUCKET_OF_MILK_1927, Items.POT_OF_CREAM_2130, Items.PAT_OF_BUTTER_6697)
    );

    /**
     * The possible inputs for making this dairy product.
     */
    val inputs: Array<Item> = Arrays.stream(inputs).map { id: Int? ->
        Item(
            id!!, 1
        )
    }.toArray { len: Int -> arrayOfNulls(len) }
}