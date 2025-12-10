package core.game.dialogue;

import core.game.node.entity.player.Player;
import core.plugin.Initializable;
import kotlin.Unit;

import static core.api.ContentAPIKt.sendInputDialogue;

/**
 * Represents the dialogue plugin used to automatically handle skill dialogues with creation amounts.
 * @author Vexia
 */
@Initializable
public class SkillDialoguePlugin extends Dialogue {

    private SkillDialogueHandler handler;

    public SkillDialoguePlugin() {
        /*
         * empty.
         */
    }

    public SkillDialoguePlugin(final Player player) {
        super(player);
    }

    @Override
    public Dialogue newInstance(Player player) {
        return new SkillDialoguePlugin(player);
    }

    @Override
    public boolean open(Object... args) {
        handler = (SkillDialogueHandler) args[0];
        handler.display();
        player.getInterfaceManager().openChatbox(handler.getType().getInterfaceId());
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        final int amount = handler.getType().getAmount(handler, buttonId);
        final int index = handler.getType().getIndex(handler, buttonId);
        end();
        if (amount != -1) {
            handler.create(amount, index);
        } else {
            sendInputDialogue(player, true, "Enter the amount:", (value) -> {
                if (value instanceof String) {
                    handler.create(Integer.parseInt((String) value), index);
                } else {
                    handler.create((int) value, index);
                }
                return Unit.INSTANCE;
            });
            return true;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{SkillDialogueHandler.SKILL_DIALOGUE};
    }

}
