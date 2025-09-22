package content.region.karamja.brimhaven.dialogue

import content.global.travel.ship.CharterShip
import core.api.*
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.RandomFunction
import shared.consts.Items
import shared.consts.NPCs
import shared.consts.Quests

@Initializable
class CaptainShanksDialogue(player: Player? = null) : Dialogue(player) {

    private var coins: Item? = null

    companion object {
        private const val HAS_TICKET = 0
        private const val NO_TICKET = 1
        private const val SELECT_DESTINATION = 2
        private const val CANCEL = 3
        private const val SAIL_KHAZARD = 4
        private const val SAIL_SARIM = 5
        private const val CONFIRM_PURCHASE = 6
        private const val PURCHASE_CHECK = 8
        private const val POST_PURCHASE = 9
    }

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC

        if (!hasRequirement(player, Quests.SHILO_VILLAGE, false)) {
            npcl(
                FaceAnim.HALF_GUILTY,
                "Oh dear, this ship is in a terrible state. And I just can't get the items I need to repair it because Shilo village is overrun with zombies."
            )
            return true
        }

        npcl(FaceAnim.HALF_ASKING, "Hello there shipmate! I sail to Khazard Port and to Port Sarim. Where are you bound?")
        stage = if (inInventory(player, Items.SHIP_TICKET_621)) HAS_TICKET else NO_TICKET
        return true
    }

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        when (stage) {

            HAS_TICKET -> {
                setTitle(player, 3)
                showTopics(
                    "Captain Shanks asks, 'Where are you bound?'",
                    Topic("Khazard Port please.", SAIL_KHAZARD),
                    Topic("Port Sarim please.", SAIL_SARIM),
                    Topic("Nowhere just at the moment thanks.", CANCEL)
                )
                stage = SELECT_DESTINATION
            }

            NO_TICKET -> {
                coins = Item(Items.COINS_995, RandomFunction.random(20, 50))
                npcl(FaceAnim.ASKING, "I see you don't have a ticket. Shall we say ${coins!!.amount} gold pieces?")
                stage = CONFIRM_PURCHASE
            }

            CANCEL -> {
                npcl(FaceAnim.HALF_GUILTY, "Very well then me old shipmate, Just let me know if you change your mind.")
                stage = END_DIALOGUE
            }

            SAIL_KHAZARD -> {
                end()
                if (removeItem(player, Items.SHIP_TICKET_621)) {
                    CharterShip.sail(player, CharterShip.CAIRN_ISLAND_TO_PORT_KHAZARD)
                }
            }

            SAIL_SARIM -> {
                end()
                if (removeItem(player, Items.SHIP_TICKET_621)) {
                    CharterShip.sail(player, CharterShip.PORT_SARIM)
                }
            }

            CONFIRM_PURCHASE -> {
                setTitle(player, 2)
                showTopics(
                    "Buy a ticket for ${coins!!.amount} gold pieces?",
                    Topic("Yes, I'll buy a ticket.", PURCHASE_CHECK),
                    Topic("No thanks, not just at the moment.", CANCEL)
                )
            }

            PURCHASE_CHECK -> {
                when {
                    !inInventory(player, coins!!.amount) -> {
                        npcl(FaceAnim.HALF_GUILTY, "Sorry me old ship mate, you seem to be financially challenged. Come back when your coffers are full!")
                        stage = END_DIALOGUE
                    }
                    freeSlots(player) == 0 -> {
                        npcl(FaceAnim.HALF_GUILTY, "Sorry me old ship mate, you don't have enough space for a ticket. Come back later.")
                        stage = END_DIALOGUE
                    }
                    else -> {
                        npcl(FaceAnim.HALF_GUILTY, "It's a good deal and no mistake. Here you go, here's your ticket.")
                        removeItem(player, coins)
                        addItem(player, Items.SHIP_TICKET_621)
                        stage = POST_PURCHASE
                    }
                }
            }

            POST_PURCHASE -> {
                npcl(FaceAnim.HALF_ASKING, "Ok, now you have your ticket, do you want to sail anywhere?")
                stage = HAS_TICKET
            }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue = CaptainShanksDialogue(player)

    override fun getIds(): IntArray = intArrayOf(NPCs.CAPTAIN_SHANKS_518)
}
