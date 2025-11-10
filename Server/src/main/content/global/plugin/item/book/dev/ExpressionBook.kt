package content.global.plugin.item.book.dev

import content.global.plugin.iface.BookInterface
import content.global.plugin.iface.BookLine
import content.global.plugin.iface.Page
import content.global.plugin.iface.PageSet
import core.api.Commands
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.system.command.Privilege

class ExpressionBook : Commands {
    companion object {
        private const val TITLE = "Expression Book"

        private const val MALE_NPC_ID = 1
        private const val FEMALE_NPC_ID = 5

        private val MALE_IMAGE_ENABLE_ID = BookInterface.FANCY_BOOK_3_49_IMAGE_ENABLE_DRAW_IDS[4]
        private val MALE_IMAGE_DRAW_ID = BookInterface.FANCY_BOOK_3_49_IMAGE_DRAW_IDS[4]

        private val FEMALE_IMAGE_ENABLE_ID = BookInterface.FANCY_BOOK_3_49_IMAGE_ENABLE_DRAW_IDS[15]
        private val FEMALE_IMAGE_DRAW_ID = BookInterface.FANCY_BOOK_3_49_IMAGE_DRAW_IDS[15]

        private val EXPRESSIONS = enumValues<FaceAnim>()
            .filterNot { it.name.startsWith("OLD_") }
            .filterNot { it.name.startsWith("CHILD_") }
            .filterNot { it.name.startsWith("NEW_") }
            .toTypedArray()

        private val CONTENTS: Array<PageSet> = EXPRESSIONS.map { anim ->
            PageSet(
                Page(BookLine("${anim.animationId} | ${anim.name.lowercase()}", 65)),
                Page(BookLine("${anim.animationId} | ${anim.name.lowercase()}", 76))
            )
        }.toTypedArray()
    }

    @Suppress("UNUSED_PARAMETER")
    fun display(player: Player, pageNum: Int, buttonID: Int): Boolean {
        BookInterface.pageSetup(player, BookInterface.FANCY_BOOK_3_49, TITLE, CONTENTS)

        val anim = EXPRESSIONS.getOrNull(pageNum)
        if (anim != null) {
            BookInterface.setNpcOnPage(
                player,
                pageNum,
                MALE_NPC_ID,
                BookInterface.FANCY_BOOK_3_49,
                MALE_IMAGE_ENABLE_ID,
                MALE_IMAGE_DRAW_ID,
                anim.animationId,
                1200
            )
            BookInterface.setNpcOnPage(
                player,
                pageNum,
                FEMALE_NPC_ID,
                BookInterface.FANCY_BOOK_3_49,
                FEMALE_IMAGE_ENABLE_ID,
                FEMALE_IMAGE_DRAW_ID,
                anim.animationId,
                1200
            )
        }

        return true
    }

    override fun defineCommands() {
        define("showexpression", Privilege.ADMIN) { player, args ->
            if (args.size > 1) {
                reject(player, "Usage: ::showexpression")
                return@define
            }
            BookInterface.openBook(player, BookInterface.FANCY_BOOK_3_49, ::display)
        }
    }
}