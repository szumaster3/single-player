package content.global.skill.cooking.recipes

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.Items

class NettleTeaRecipePlugin : InteractionListener {

    override fun defineListeners() {

        /*
         * Handles putting nettles into a bowl of water.
         */

        onUseWith(IntType.ITEM, NETTLES, BOWL_OF_WATER) { player, used, with ->
            if (removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)) {
                addItem(player, NETTLE_WATER, 1)
                sendMessage(player, "You place the nettles into the bowl of water.")
            }
            return@onUseWith true
        }

        /*
         * Handles creating nettle tea (Milk).
         */

        onUseWith(IntType.ITEM, BUCKET_OF_MILK, BOWL_OF_NETTLE_TEA) { player, used, with ->
            if (removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)) {
                addItemOrDrop(player, EMPTY_BUCKET, 1)
                addItem(player, BOWL_OF_NETTLE_TEA_MILKY, 1)
            }
            return@onUseWith true
        }

        /*
         * Handles adding milk to cup of tea.
         */

        onUseWith(IntType.ITEM, BUCKET_OF_MILK, PORCELAIN_CUP_OF_NETTLE_TEA) { player, used, with ->
            if (removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)) {
                addItemOrDrop(player, EMPTY_BUCKET, 1)
                addItem(player, PORCELAIN_CUP_OF_NETTLE_TEA_MILKY, 1)
            }
            return@onUseWith true
        }

        /*
         * Handles filling an empty cup with nettle tea.
         */

        onUseWith(IntType.ITEM, BOWL_OF_NETTLE_TEA, EMPTY_CUP) { player, used, with ->
            if (removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)) {
                addItemOrDrop(player, EMPTY_BOWL, 1)
                addItem(player, CUP_OF_NETTLE_TEA, 1)
            }
            return@onUseWith true
        }

        /*
         * Handles filling an empty porcelain cup with nettle tea.
         */

        onUseWith(IntType.ITEM, BOWL_OF_NETTLE_TEA_MILKY, EMPTY_CUP) { player, used, with ->
            if (removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)) {
                addItemOrDrop(player, EMPTY_BOWL, 1)
                addItem(player, CUP_OF_NETTLE_TEA_MILKY, 1)
            }
            return@onUseWith true
        }

        /*
         * Handles filling a porcelain cup with nettle tea.
         */

        onUseWith(IntType.ITEM, BOWL_OF_NETTLE_TEA, EMPTY_PORCELAIN_CUP) { player, used, with ->
            if (removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)) {
                addItemOrDrop(player, EMPTY_BOWL, 1)
                addItem(player, PORCELAIN_CUP_OF_NETTLE_TEA, 1)
            }
            return@onUseWith true
        }

        /*
         * Handles filling a porcelain cup with nettle tea.
         */

        onUseWith(IntType.ITEM, BOWL_OF_NETTLE_TEA_MILKY, EMPTY_PORCELAIN_CUP) { player, used, with ->
            if (removeItem(player, used.asItem(), Container.INVENTORY) && removeItem(player, with.asItem(), Container.INVENTORY)) {
                addItemOrDrop(player, EMPTY_BOWL, 1)
                addItem(player, PORCELAIN_CUP_OF_NETTLE_TEA_MILKY, 1)
            }
            return@onUseWith true
        }
    }

    companion object {
        private const val EMPTY_BUCKET = Items.BUCKET_1925
        private const val BUCKET_OF_MILK = Items.BUCKET_OF_MILK_1927

        private const val NETTLES = Items.NETTLES_4241
        private const val NETTLE_WATER = Items.NETTLE_WATER_4237
        private const val BOWL_OF_WATER = Items.BOWL_OF_WATER_1921

        private const val EMPTY_BOWL = Items.BOWL_1923
        private const val BOWL_OF_NETTLE_TEA = Items.NETTLE_TEA_4239
        private const val BOWL_OF_NETTLE_TEA_MILKY = Items.NETTLE_TEA_4240

        private const val EMPTY_CUP = Items.EMPTY_CUP_1980
        private const val CUP_OF_NETTLE_TEA = Items.CUP_OF_TEA_4242
        private const val CUP_OF_NETTLE_TEA_MILKY = Items.CUP_OF_TEA_4243

        private const val EMPTY_PORCELAIN_CUP = Items.PORCELAIN_CUP_4244
        private const val PORCELAIN_CUP_OF_NETTLE_TEA = Items.CUP_OF_TEA_4245
        private const val PORCELAIN_CUP_OF_NETTLE_TEA_MILKY = Items.CUP_OF_TEA_4246
    }
}