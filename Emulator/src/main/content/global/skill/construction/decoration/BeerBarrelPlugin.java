package content.global.skill.construction.decoration;

import core.game.interaction.NodeUsageEvent;
import core.game.interaction.UseWithHandler;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.world.update.flag.context.Animation;
import core.plugin.Initializable;
import core.plugin.Plugin;
import org.rs.consts.Animations;
import org.rs.consts.Items;

/**
 * The type Beer barrel plugin.
 */
@Initializable
public class BeerBarrelPlugin extends UseWithHandler {

    private static final int[] OBJECTS = new int[]{
            org.rs.consts.Scenery.BEER_BARREL_13568,
            org.rs.consts.Scenery.CIDER_BARREL_13569,
            org.rs.consts.Scenery.ASGARNIAN_ALE_13570,
            org.rs.consts.Scenery.GREENMAN_S_ALE_13571,
            org.rs.consts.Scenery.DRAGON_BITTER_13572,
            org.rs.consts.Scenery.CHEF_S_DELIGHT_13573
    };

    /**
     * Instantiates a new Beer barrel plugin.
     */
    public BeerBarrelPlugin() {
        super(Items.BEER_GLASS_1919);
    }

    @Override
    public Plugin<java.lang.Object> newInstance(java.lang.Object arg) throws Throwable {

        for (int id : OBJECTS) {
            addHandler(id, OBJECT_TYPE, this);
        }
        return this;
    }

    @Override
    public boolean handle(NodeUsageEvent event) {
        Player player = event.getPlayer();
        final Scenery scenery = (Scenery) event.getUsedWith();

        if (player.getInventory().remove(new Item(Items.BEER_GLASS_1919))) {
            player.animate(Animation.create(Animations.HUMAN_WITHDRAW_833));
            player.sendMessage("You fill up your glass.");
            player.getInventory().add(new Item(getReward(scenery.getId()), 1));
        }
        return true;
    }

    /**
     * Gets reward.
     *
     * @param barrelId the barrel id
     * @return the reward
     */
    public int getReward(int barrelId) {
        switch (barrelId) {
            case org.rs.consts.Scenery.BEER_BARREL_13568:
                return Items.BEER_1917;
            case org.rs.consts.Scenery.CIDER_BARREL_13569:
                return Items.CIDER_5763;
            case org.rs.consts.Scenery.ASGARNIAN_ALE_13570:
                return Items.ASGARNIAN_ALE_1905;
            case org.rs.consts.Scenery.GREENMAN_S_ALE_13571:
                return Items.GREENMANS_ALE_1909;
            case org.rs.consts.Scenery.DRAGON_BITTER_13572:
                return Items.DRAGON_BITTER_1911;
            case org.rs.consts.Scenery.CHEF_S_DELIGHT_13573:
                return Items.CHEFS_DELIGHT_5755;
            default:
                return Items.BEER_1917;
        }
    }
}
