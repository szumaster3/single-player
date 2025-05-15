package content.global.activity.ttrail.anagram;

import content.global.activity.ttrail.ClueScrollPlugin;
import content.global.activity.ttrail.TreasureTrailManager;
import content.global.activity.ttrail.puzzle.PuzzleBox;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.world.map.zone.ZoneBorders;
import core.plugin.Plugin;
import org.rs.consts.Items;

import java.util.List;

import static core.api.ContentAPIKt.*;

/**
 * Represents a Anagram clue scroll.
 */
public class AnagramClueScroll extends ClueScrollPlugin {

    /** The clue data associated with this scroll. */
    private final AnagramClue clue;

    /**
     * Constructs a new AnagramClueScroll for the given clue.
     *
     * @param clue the associated {@link AnagramClue}
     */
    public AnagramClueScroll(AnagramClue clue) {
        super(clue.name(), clue.ordinal(), clue.getLevel(), 345, (ZoneBorders[]) null);
        this.clue = clue;
    }

    /**
     * Returns a new instance of the plugin for the clue.
     *
     * @param arg the argument
     * @return a new plugin instance
     */
    @Override
    public Plugin<Object> newInstance(Object arg) {
        return new AnagramClueScroll(clue);
    }

    /**
     * Configures the plugin. This clue type does not require any special configuration.
     */
    @Override
    public void configure() {
    }

    /**
     * Handles reading the clue scroll by updating the interface text with the anagram hint.
     *
     * @param player the player reading the scroll
     */
    @Override
    public void read(Player player) {
        for (int i = 1; i <= 8; i++) {
            sendString(player, "", getInterfaceId(), i);
        }
        String text = "<br><br>This anagram reveals<br>who to speak to next:<br><br><br>" +
                clue.getAnagram().replace("<br>", "<br><br>");
        sendString(player, text, getInterfaceId(), 1);
    }

    /**
     * Handles interaction with an NPC related to this anagram clue.
     * This may involve giving the player a puzzle box or rewarding them for solving it.
     *
     * @param player the player interacting with the NPC
     * @param npc    the NPC being interacted with
     */
    public void handleInteraction(Player player, NPC npc) {
        AnagramClue clue = getMatchingClue(npc);
        if (clue == null) return;

        boolean hasClue = player.getInventory().contains(clue.getClueId(), 1);
        PuzzleBox puzzle = clue.getChallenge() != null ? PuzzleBox.fromKey(clue.getChallenge().toString()) : null;

        if (puzzle != null && hasClue) {
            boolean hasPuzzle = player.getInventory().contains(puzzle.getItem().getId(), 1);
            boolean puzzleComplete = PuzzleBox.hasCompletePuzzleBox(player, puzzle.getKey());

            if (!puzzleComplete) {
                if (!hasPuzzle) {
                    List<String> messages = List.of(
                            "Oh, I have a puzzle for you to solve.",
                            "Oh, I've been expecting you.",
                            "The solving of this puzzle could be the key to your treasure."
                    );
                    sendNPCDialogue(player, clue.getNpcId(), getRandomMessage(messages), FaceAnim.HALF_GUILTY);
                    player.getInventory().add(puzzle.getItem());
                    addDialogueAction(player, (p, button) -> {
                        if (button > 0) {
                            sendItemDialogue(player, puzzle.getItem(), npc.getName() + " has given you a puzzle box!");
                        }
                    });
                }
                return;
            }

            if (player.getInventory().remove(new Item(puzzle.getItem().getId()))) {
                sendNPCDialogue(player, clue.getNpcId(), "Well done, traveller.", FaceAnim.HALF_GUILTY);
                TreasureTrailManager manager = TreasureTrailManager.getInstance(player);
                ClueScrollPlugin clueScroll = ClueScrollPlugin.getClueScrolls().get(clue.getClueId());

                if (clueScroll != null) {
                    clueScroll.reward(player);

                    if (manager.isCompleted()) {
                        sendItemDialogue(player, Items.CASKET_405, "You've found a casket!");
                        manager.clearTrail();
                        Item newClue = ClueScrollPlugin.getClue(clueScroll.getLevel());
                        if (newClue != null) {
                            player.getInventory().add(newClue);
                            sendItemDialogue(player, newClue, "You receive another clue scroll.");
                        }
                    } else {
                        Item newClue = ClueScrollPlugin.getClue(clueScroll.getLevel());
                        if (newClue != null) {
                            player.getInventory().add(newClue);
                            sendItemDialogue(player, newClue, "You receive another clue scroll.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Finds the clue that corresponds to the given NPC, if any.
     *
     * @param npc the NPC being interacted with
     * @return the matching {@link AnagramClue} or {@code null} if none match
     */
    private AnagramClue getMatchingClue(NPC npc) {
        for (AnagramClue c : AnagramClue.values()) {
            if (c.getNpcId() == npc.getId()) return c;
        }
        return null;
    }

    /**
     * Selects a random message from the provided list.
     *
     * @param messages the list of messages to choose from
     * @return a random message
     */
    private String getRandomMessage(List<String> messages) {
        return messages.get((int) (Math.random() * messages.size()));
    }

    /**
     * Gets the associated anagram clue.
     *
     * @return the {@link AnagramClue} object
     */
    public AnagramClue getClue() {
        return clue;
    }
}
