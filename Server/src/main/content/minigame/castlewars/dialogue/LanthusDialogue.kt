package content.minigame.castlewars.dialogue

import core.api.openNpcShop
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import shared.consts.Items
import shared.consts.NPCs

/**
 * Represents the Lanthus dialogue.
 */
@Initializable
class LanthusDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {
            0 -> {
                options(
                    "What is this place?",
                    "What do you have for trade?",
                    "Do you have a manual? I'd like to learn how to play!"
                )
                stage++
            }

            1 -> when (buttonId) {
                1 -> {
                    player("What is this place?")
                    stage++
                }

                2 -> {
                    openNpcShop(player, NPCs.LANTHUS_1526)
                    stage = END_DIALOGUE
                }

                3 -> {
                    player("Do you have a manual? I'd like to learn how to play!")
                    stage = 50
                }
            }

            2 -> {
                npcl(FaceAnim.FRIENDLY, "This is the great Castle Wars arena! Here you can fight for the glory of Saradomin or Zamorak.")
                stage++
            }

            3 -> {
                options("Really, how do I do that?", "Are there any rules?", "What can I win?")
                stage++
            }

            4 -> when (buttonId) {
                1 -> {
                    player("Really, how do I do that?")
                    stage = 5
                }

                2 -> {
                    player("Are there any rules?")
                    stage = 80
                }

                3 -> {
                    player("What can I win?")
                    stage = 90
                }
            }

            5 -> {
                npcl(
                    FaceAnim.FRIENDLY,
                    "Easy, you just step through one of the three portals. To join Zamorak, pass through the red portal. To join Saradomin, pass through the blue portal. If you don't mind then pass through the green portal."
                )
                stage++
            }

            6 -> {
                options(
                    "Are there any rules?",
                    "What can I win?",
                    "What do you have for trade?",
                    "Do you have a manual? I'd like to learn how to play!"
                )
                stage++
            }

            7 -> when (buttonId) {
                1 -> {
                    player("Are there any rules?")
                    stage = 80
                }

                2 -> {
                    player("What can I win?")
                    stage = 90
                }

                3 -> {
                    openNpcShop(player, NPCs.LANTHUS_1526)
                    stage = END_DIALOGUE
                }

                4 -> {
                    player("Do you have a manual? I'd like to learn how to play!")
                    stage = 50
                }
            }

            50 -> {
                npcl(FaceAnim.FRIENDLY, "Sure, here you go.")
                player.inventory.add(Item(Items.CASTLEWARS_MANUAL_4055))
                stage = END_DIALOGUE
            }

            80 -> {
                npcl(
                    FaceAnim.FRIENDLY,
                    "Of course, there are always rules. Firstly you can't wear a cape as you enter the portal, you'll be given your team colours to wear while in the arena. You're also prohibited from taking non-combat related items in."
                )
                stage++
            }

            81 -> {
                npcl(
                    FaceAnim.FRIENDLY,
                    "with you. So you should only have equipment, potions, and runes on you. Secondly, attacking your own team or your team's defences isn't allowed. You don't want to be angering"
                )
                stage++
            }

            82 -> {
                npcl(FaceAnim.FRIENDLY, "your patron god, do you? Other than that, just have fun and enjoy it!")
                stage++
            }

            83 -> {
                player("Great! Oh, how do I win the game?")
                stage++
            }

            84 -> {
                npcl(
                    FaceAnim.FRIENDLY,
                    "The aim is to get into your opponents' castle and take their team standard. Then bring that back and capture it on your team's standard."
                )
                stage++
            }

            85 -> {
                options(
                    "What can I win?",
                    "What do you have to trade?",
                    "Do you have a manual? I'd like to learn how to play!"
                )
                stage++
            }

            86 -> when (buttonId) {
                1 -> {
                    player("What can I win?")
                    stage = 90
                }

                2 -> {
                    openNpcShop(player, NPCs.LANTHUS_1526)
                    stage = END_DIALOGUE
                }

                3 -> {
                    player("Do you have a manual? I'd like to learn how to play!")
                    stage = 50
                }
            }

            90 -> {
                npcl(
                    FaceAnim.FRIENDLY,
                    "Players on the winning team will receive 2 Castle Wars Tickets which you can trade back to me for other items. In the event of a draw every player will get 1 ticket."
                )
                stage++
            }

            91 -> {
                options("Are there any rules?", "What do you have to trade?", "Do you have a manual? I'd like to learn how to play!")
                stage++
            }

            92 -> when (buttonId) {
                1 -> {
                    player("Are there any rules?")
                    stage = 80
                }

                2 -> {
                    openNpcShop(player, NPCs.LANTHUS_1526)
                    stage = END_DIALOGUE
                }

                3 -> {
                    player("Do you have a manual? I'd like to learn how to play!")
                    stage = 50
                }
            }

            else -> {
                stage = END_DIALOGUE
            }
        }
        return true
    }

    override fun newInstance(player: Player): Dialogue {
        return LanthusDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.LANTHUS_1526)
    }
}
