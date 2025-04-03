package core.game.node.entity.combat.equipment;

import core.game.component.Component;
import core.game.component.ComponentDefinition;
import core.game.component.InterfaceType;
import core.game.container.impl.EquipmentContainer;
import core.game.node.entity.combat.spell.CombatSpell;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.SpellBookManager;
import core.game.node.item.Item;
import core.game.system.config.ItemConfigParser;
import core.game.world.update.flag.context.Animation;
import core.net.packet.PacketRepository;
import core.net.packet.context.InterfaceConfigContext;
import core.net.packet.context.InterfaceContext;
import core.net.packet.context.StringContext;
import core.net.packet.out.Interface;
import core.net.packet.out.InterfaceConfig;
import core.net.packet.out.StringPacket;
import kotlin.Unit;
import org.rs.consts.Components;

import static core.api.ContentAPIKt.*;

/**
 * Represents the weapon interface component.
 *
 * @author Emperor
 */
public final class WeaponInterface extends Component {

    /**
     * The modern spell ids.
     */
    private static final int[] MODERN_SPELL_IDS = {1, 4, 6, 8, 10, 14, 17, 20, 24, 27, 33, 38, 45, 48, 52, 55};

    /**
     * The ancient spell ids.
     */
    private static final int[] ANCIENT_SPELL_IDS = {8, 12, 4, 0, 10, 14, 6, 2, 9, 13, 5, 1, 11, 15, 7, 3, 16, 17, 18, 19};

    /**
     * The slayer staff spell ids.
     */
    private static final int[] SLAYER_STAFF_SPELL_IDS = {22, 31, 45, 48, 52, 55};

    /**
     * The void staff spell ids.
     */
    private static final int[] VOID_STAFF_SPELL_IDS = {22, 42, 45, 48, 52, 55};

    /**
     * The default attack animations.
     */
    public static final Animation[] DEFAULT_ANIMS = {new Animation(422, Priority.HIGH), new Animation(423, Priority.HIGH), new Animation(422, Priority.HIGH), new Animation(422, Priority.HIGH)};

    /**
     * The stab equipment bonus index.
     */
    public static final int BONUS_STAB = 0;

    /**
     * The slash equipment bonus index.
     */
    public static final int BONUS_SLASH = 1;

    /**
     * The crush equipment bonus index.
     */
    public static final int BONUS_CRUSH = 2;

    /**
     * The magic equipment bonus index.
     */
    public static final int BONUS_MAGIC = 3;

    /**
     * The range equipment bonus index.
     */
    public static final int BONUS_RANGE = 4;

    /**
     * The accurate melee attack style
     */
    public static final int STYLE_ACCURATE = 0;

    /**
     * The aggressive attack style
     */
    public static final int STYLE_AGGRESSIVE = 1;

    /**
     * The controlled attack style
     */
    public static final int STYLE_CONTROLLED = 2;

    /**
     * The defensive attack style
     */
    public static final int STYLE_DEFENSIVE = 3;

    /**
     * The accurate range attack style
     */
    public static final int STYLE_RANGE_ACCURATE = 4;

    /**
     * The rapid range attack style
     */
    public static final int STYLE_RAPID = 5;

    /**
     * The long range attack style
     */
    public static final int STYLE_LONG_RANGE = 6;

    /**
     * The defensive spell cast attack style
     */
    public static final int STYLE_DEFENSIVE_CAST = 7;

    /**
     * The spell cast attack style
     */
    public static final int STYLE_CAST = 8;

    /**
     * The player.
     */
    private final Player player;

    /**
     * The current weapon interface.
     */
    private WeaponInterfaces current;

    /**
     * If the player has the special attack bar shown.
     */
    private boolean specialBar;

    /**
     * The player's attack animations.
     */
    private Animation[] attackAnimations;

    /**
     * Instantiates a new Weapon interface.
     *
     * @param player the player
     */
    public WeaponInterface(Player player) {
        super(92);
        this.player = player;
        player.addExtension(WeaponInterface.class, this);
    }

    /**
     * Opens the interface.
     *
     * @param player The player who is opening the interface.
     */
    @Override
    public void open(Player player) {
        current = null;
        updateInterface();
    }

    /**
     * Opens the weapon interface for the player and sets the appropriate attack style.
     */
    private void open() {
        ComponentDefinition definition = ComponentDefinition.forId(Components.WEAPON_FISTS_SEL_92);
        boolean resizable = player.getInterfaceManager().isResizable();
        PacketRepository.send(Interface.class, new InterfaceContext(player, definition.getWindowPaneId(resizable), definition.getChildId(resizable), id, definition.isWalkable()));
        int slot = ensureStyleIndex(player, player.getSettings().getAttackStyleIndex());
        if (slot != player.getSettings().getAttackStyleIndex()) {
            player.getSettings().toggleAttackStyleIndex(slot);
        }
        player.getProperties().setAttackStyle(current.getAttackStyles()[slot]);
        checkStaffConfigs(slot);
    }

    /**
     * Ensures the attack style index is valid for the current weapon.
     *
     * @param player The player whose attack style index is being validated.
     * @param slot   The current attack style index.
     * @return A valid attack style index > 0
     */
    private int ensureStyleIndex(Player player, int slot) {
        AttackStyle style = player.getProperties().getAttackStyle();
        if (slot >= current.getAttackStyles().length) {
            slot = current.getAttackStyles().length - 1;
            if (style != null) {
                for (int i = slot; i >= 0; i--) {
                    if (current.getAttackStyles()[i].style == style.style) {
                        return i;
                    }
                }
            }
            return slot;
        }
        if (style != null && current.getAttackStyles()[slot].style != style.style) {
            for (int i = current.getAttackStyles().length - 1; i >= 0; i--) {
                if (current.getAttackStyles()[i].style == style.style) {
                    return i;
                }
            }
        }
        return slot;
    }

    /**
     * Determines the configuration value based on the weapon type and interface id.
     *
     * @param buttons     The button id clicked.
     * @param interfaceId The interface id of the weapon selection screen.
     * @return The corresponding configuration value.
     */
    private int getConfig(int buttons, int interfaceId) {
        if (interfaceId == Components.WEAPON_STAFF_SEL_90) {
            return 87;//Return Config
        }
        if (interfaceId != Components.WEAPON_WHIP_SEL_93 && interfaceId != Components.WEAPON_WARHAMMER_SEL_76 && interfaceId != Components.WEAPON_XBOW_SEL_79 && interfaceId != Components.WEAPON_HALBERD_SEL_84 && interfaceId != Components.WEAPON_THROWN_SEL_91) {
            switch (buttons) {
                case 3:
                    return 13;
                case 4:
                    return 12;
            }
        } else {
            return 10;
        }
        return 10;
    }

    /**
     * Update interface.
     */
    public void updateInterface() {
        player.getInterfaceManager().getTabs()[0] = this;

        Item weapon = player.getEquipment().get(EquipmentContainer.SLOT_WEAPON);
        WeaponInterfaces inter = getWeaponInterface(weapon);

        if (inter == null) {
            return;
        }

        String name;
        if (weapon != null && weapon.getDefinition() != null) {
            name = weapon.getDefinition().getName();
            specialBar = weapon.getDefinition().getConfiguration(ItemConfigParser.HAS_SPECIAL, false);
            attackAnimations = weapon.getDefinition().getConfiguration(ItemConfigParser.ATTACK_ANIMS, DEFAULT_ANIMS);
        } else {
            name = "Unarmed";
            specialBar = false;
            attackAnimations = DEFAULT_ANIMS;
        }

        if (inter != current) {
            id = inter.interfaceId;
            current = inter;
            open();
            player.getProperties().getCombatPulse().updateStyle();
        }

        int attackIndex = player.getSettings().getAttackStyleIndex();
        if (attackAnimations != null && attackIndex < attackAnimations.length && !player.getAppearance().isNpc()) {
            player.getProperties().setAttackAnimation(attackAnimations[attackIndex]);
        }

        if (current == WeaponInterfaces.STAFF) {
            PacketRepository.send(InterfaceConfig.class, new InterfaceConfigContext(player, id, 87, !specialBar));
        } else {
            selectAutoSpell(-1, false);
            PacketRepository.send(InterfaceConfig.class, new InterfaceConfigContext(player, id, getConfig(current.getAttackStyles().length, current.getInterfaceId()), !specialBar));
        }
        if (!canAutocast(false)) {
            if (current == WeaponInterfaces.STAFF && player.getAttribute("autocast_select", false)) {
                open();
            }
            selectAutoSpell(-1, true);
        }
        PacketRepository.send(StringPacket.class, new StringContext(player, name, id, 0));
        if (player.getSettings().isSpecialToggled()) {
            player.getSettings().toggleSpecialBar();
        }
    }

    /**
     * Sets the player's attack style based on the selected button.
     *
     * @param button The button id corresponding to an attack style selection.
     * @return {@code true} if the attack style was successfully set, otherwise {@code false}.
     */
    public boolean setAttackStyle(int button) {
        if (current == null) {
            return false;
        }
        int slot = button - 1;
        if (current != WeaponInterfaces.STAFF) {
            slot--;
        }
        boolean isMaul = current == WeaponInterfaces.WARHAMMER_MAUL;
        boolean isRangedThirdStyle = (current.attackStyles.length > 2 && current.attackStyles[2].bonusType == BONUS_RANGE);
        boolean isNotThrown = current.getInterfaceId() != Components.WEAPON_THROWN_SEL_91;

        if (isMaul || (isRangedThirdStyle && isNotThrown)) {
            slot = (button == 4) ? 1 : (button == 3) ? 2 : 0;
        } else if (current == WeaponInterfaces.CLAWS) {
            slot = (button == 5) ? 1 : (button == 3) ? 3 : slot;
        } else if (current == WeaponInterfaces.AXE) {
            if (button == 5) {
                slot = 1;
            } else if (button == 3) {
                slot = 3;
            }
        }
        if (slot < 0 || slot >= current.getAttackStyles().length) {
            return false;
        }
        AttackStyle style = current.getAttackStyles()[slot];
        player.getProperties().setAttackStyle(style);
        player.getSettings().toggleAttackStyleIndex(slot);
        if (slot < attackAnimations.length && !player.getAppearance().isNpc()) {
            player.getProperties().setAttackAnimation(attackAnimations[slot]);
        }
        checkStaffConfigs(button - 1);
        return true;
    }

    /**
     * Updates staff-related configurations when changing attack styles.
     *
     * @param slot The selected attack style slot.
     */
    private void checkStaffConfigs(int slot) {
        if (current != WeaponInterfaces.STAFF) {
            selectAutoSpell(-1, false);
            return;
        }

        String autoCast = "autocast_component";
        registerLogoutListener(player, autoCast, player -> {
            removeAttribute(player, autoCast);
            player.getSpellBookManager().update(player);
            return Unit.INSTANCE;
        });

        int spellBook = player.getSpellBookManager().getSpellBook();
        int config = spellBook == SpellBookManager.SpellBook.LUNAR.getInterfaceId() ? 2 : spellBook == SpellBookManager.SpellBook.ANCIENT.getInterfaceId() ? (slot == 3 ? -3 : 1) : (slot == 3 ? -4 : 0);

        setVarp(player, 439, slot == 3 ? -4: config);
        if (slot > 2) setVarp(player, 43, slot == 3 ? -1 : 3);
    }

    /**
     * Retrieves the index of a spell for autocasting.
     *
     * @param spellId The id of the spell being checked.
     * @return The index of the spell in the corresponding spell array, or -1 if not found.
     */
    public int getAutospellId(int spellId) {
        boolean modern = player.getSpellBookManager().getSpellBook() == Components.MAGIC_192;
        int[] data = modern ? MODERN_SPELL_IDS : ANCIENT_SPELL_IDS;
        String weaponName = player.getEquipment().getNew(3).getName();
        if (modern && weaponName.equalsIgnoreCase("Slayer's staff")) {
            data = SLAYER_STAFF_SPELL_IDS;
        }
        if (modern && weaponName.equalsIgnoreCase("Void knight mace")) {
            data = VOID_STAFF_SPELL_IDS;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] == spellId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Select auto spell.
     *
     * @param buttonId          the button id
     * @param adjustAttackStyle the adjust attack style
     */
    public void selectAutoSpell(int buttonId, boolean adjustAttackStyle) {
        boolean modern = player.getSpellBookManager().getSpellBook() == Components.MAGIC_192;
        int[] data = modern ? MODERN_SPELL_IDS : ANCIENT_SPELL_IDS;

        String weaponName = player.getEquipment().getNew(3).getName();
        if (modern) {
            if (weaponName.equalsIgnoreCase("Slayer's staff")) {
                data = SLAYER_STAFF_SPELL_IDS;
            } else if (weaponName.equalsIgnoreCase("Void knight mace")) {
                data = VOID_STAFF_SPELL_IDS;
            }
        }
        CombatSpell current = player.getProperties().getAutocastSpell();
        if (buttonId >= data.length) {
            return;
        }

        int configStart = modern ? 45 : 13;
        if (current != null) {
            for (int index = 0; index < data.length; index++) {
                if (data[index] == current.spellId) {
                    int previousConfigId = configStart + (2 * index);
                    player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, previousConfigId, true);
                    player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, 100 + previousConfigId, true);
                } else {
                    boolean defensive = player.getSettings().getAttackStyleIndex() == 3;
                    player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, 183, defensive);
                    player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, 83, !defensive);
                }
            }
        }
        if (buttonId < 0) {
            player.getProperties().setAutocastSpell(null);
            if (adjustAttackStyle && current != null) {
                setAttackStyle(3);
                player.getProperties().getCombatPulse().updateStyle();
            }
            return;
        }
        boolean defensive = player.getSettings().getAttackStyleIndex() == 3;
        player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, 183, defensive);
        player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, 83, !defensive);
        current = (CombatSpell) (modern ? SpellBookManager.SpellBook.MODERN.getSpell(data[buttonId]) : SpellBookManager.SpellBook.ANCIENT.getSpell(data[buttonId]));
        player.getProperties().setAutocastSpell(current);
        int configId = configStart + (2 * buttonId);
        if (modern && player.getEquipment().getNew(3).getName().equalsIgnoreCase("Slayer's staff") || modern && player.getEquipment().getNew(3).getName().equalsIgnoreCase("Void knight mace")) {
            boolean slayer = player.getEquipment().getNew(3).getName().equalsIgnoreCase("Slayer's staff");
            switch (buttonId) {
                case 0:
                    configId = 77;
                    break;
                case 1:
                    configId = slayer ? 79 : 81;
                    break;
                case 2:
                    configId = 69;
                    break;
                case 3:
                    configId = 71;
                    break;
                case 4:
                    configId = 73;
                    break;
                case 5:
                    configId = 75;
                    break;

            }
            if (configId >= 85 || configId <= 65) {
                return;
            } else {
                player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, (defensive ? 100 : 0) + configId, false);
            }
        } else {
            configId += defensive ? 100 : 0;
            switch (buttonId) {
                case 16:
                    configId = defensive ? 265 : 253;
                    break;
                case 17:
                    configId = defensive ? 268 : 256;
                    break;
                case 18:
                    configId = defensive ? 271 : 259;
                    break;
                case 19:
                    configId = defensive ? 274 : 262;
                    break;
            }
            player.getPacketDispatch().sendInterfaceConfig(Components.WEAPON_STAFF_SEL_90, configId, false);
        }

    }

    /**
     * Handles the opening of the autocast interface for the player.
     */
    public void openAutocastSelect() {
        if (current != WeaponInterfaces.STAFF) {
            return;
        }
        if (!canAutocast(true)) {
            setAttackStyle(3);
            return;
        }
        player.setAttribute("autocast_select", true);
        int id = player.getSpellBookManager().getSpellBook() == 193 ? 797 : 319;
        boolean slayer = player.getEquipment().getNew(3).getName().equalsIgnoreCase("Slayer's staff");
        boolean mace = player.getEquipment().getNew(3).getName().equalsIgnoreCase("Void knight mace");
        if (slayer) {
            id = 310;
        }
        if (mace) {
            id = 406;
        }
        Component component = new Component(id);
        assert component.definition != null;
        component.definition.tabIndex = 0;
        component.definition.type = InterfaceType.TAB;
        player.setAttribute("autocast_component", component);
        player.getInterfaceManager().openTab(component);
    }

    /**
     * Checks if the player can autocast spells based on their equipped weapon and spellbook.
     *
     * @param message If true, sends a message to the player when they cannot autocast.
     * @return true if the player can autocast, false otherwise.
     */
    public boolean canAutocast(boolean message) {
        if (current != WeaponInterfaces.STAFF) {
            return false;
        }

        int spellBook = player.getSpellBookManager().getSpellBook();
        if (spellBook == SpellBookManager.SpellBook.LUNAR.getInterfaceId()) {
            if (message) {
                player.getPacketDispatch().sendMessage("You can't autocast spells from the Lunar spellbook.");
            }
            return false;
        }

        String weaponName = player.getEquipment().getNew(3).getName();
        boolean hasAncientStaff = weaponName.contains("ncient staff");
        boolean hasWildernessStaff = weaponName.contains("uriel's staff");

        if (spellBook == SpellBookManager.SpellBook.ANCIENT.getInterfaceId() && !(hasAncientStaff || hasWildernessStaff)) {
            if (message) {
                player.getPacketDispatch().sendMessage("You can only autocast ancient magicks with an Ancient or Zuriel's staff.");
            }
            return false;
        }

        return true;
    }

    /**
     * Gets weapon interface.
     *
     * @param weapon the weapon
     * @return the weapon interface
     */
    public static WeaponInterfaces getWeaponInterface(Item weapon) {
        if (weapon == null) {
            return WeaponInterfaces.values()[0];
        }
        int slot = weapon.getDefinition().getConfiguration(ItemConfigParser.WEAPON_INTERFACE, 0);
        return WeaponInterfaces.values()[slot];
    }

    /**
     * Represents an attack style.
     *
     * @author Emperor
     */
    public static class AttackStyle {

        /**
         * The style type.
         */
        private final int style;

        /**
         * The bonus type.
         */
        private final int bonusType;

        /**
         * Instantiates a new Attack style.
         *
         * @param style     the style
         * @param bonusType the bonus type
         */
        public AttackStyle(int style, int bonusType) {
            this.style = style;
            this.bonusType = bonusType;
        }

        /**
         * Gets style.
         *
         * @return the style
         */
        public int getStyle() {
            return style;
        }

        /**
         * Gets bonus type.
         *
         * @return the bonus type
         */
        public int getBonusType() {
            return bonusType;
        }
    }

    /**
     * The enum Weapon interfaces.
     */
    public enum WeaponInterfaces {

        /**
         * The Unarmed.
         */
        UNARMED(Components.WEAPON_FISTS_SEL_92, new AttackStyle(STYLE_ACCURATE, BONUS_CRUSH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_CRUSH)),

        /**
         * The Staff.
         */
        STAFF(Components.WEAPON_STAFF_SEL_90, new AttackStyle(STYLE_ACCURATE, BONUS_CRUSH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_CRUSH), new AttackStyle(STYLE_CAST, BONUS_MAGIC), new AttackStyle(STYLE_DEFENSIVE_CAST, BONUS_MAGIC)),

        /**
         * The Axe.
         */
        AXE(Components.WEAPON_BAXE_SEL_75, new AttackStyle(STYLE_ACCURATE, BONUS_SLASH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_SLASH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_SLASH)),

        /**
         * The Scepter.
         */
        SCEPTER(Components.WEAPON_SCEPTER_SEL_85, new AttackStyle(STYLE_ACCURATE, BONUS_CRUSH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_CRUSH)),

        /**
         * The Pickaxe.
         */
        PICKAXE(Components.WEAPON_PICKAXE_SEL_83, new AttackStyle(STYLE_ACCURATE, BONUS_STAB), new AttackStyle(STYLE_AGGRESSIVE, BONUS_STAB), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_STAB)),

        /**
         * The Sword dagger.
         */
        SWORD_DAGGER(Components.WEAPON_DAGGER_SEL_89, new AttackStyle(STYLE_ACCURATE, BONUS_STAB), new AttackStyle(STYLE_AGGRESSIVE, BONUS_STAB), new AttackStyle(STYLE_AGGRESSIVE, BONUS_SLASH), new AttackStyle(STYLE_DEFENSIVE, BONUS_STAB)),

        /**
         * The Scimitar.
         */
        SCIMITAR(Components.WEAPON_SCIMITAR_SEL_81, new AttackStyle(STYLE_ACCURATE, BONUS_SLASH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_SLASH), new AttackStyle(STYLE_CONTROLLED, BONUS_STAB), new AttackStyle(STYLE_DEFENSIVE, BONUS_SLASH)),

        /**
         * The 2H sword.
         */
        TWO_H_SWORD(Components.WEAPON_2H_SWORD_SEL_82, new AttackStyle(STYLE_ACCURATE, BONUS_SLASH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_SLASH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_SLASH)),

        /**
         * The Mace.
         */
        MACE(Components.WEAPON_MACE_SEL_88, new AttackStyle(STYLE_ACCURATE, BONUS_CRUSH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_CONTROLLED, BONUS_STAB), new AttackStyle(STYLE_DEFENSIVE, BONUS_CRUSH)),

        /**
         * The Claws.
         */
        CLAWS(Components.WEAPON_CLAWS_SEL_78, new AttackStyle(STYLE_ACCURATE, BONUS_SLASH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_SLASH), new AttackStyle(STYLE_CONTROLLED, BONUS_STAB), new AttackStyle(STYLE_DEFENSIVE, BONUS_SLASH)),

        /**
         * The Warhammer / maul.
         */
        WARHAMMER_MAUL(Components.WEAPON_WARHAMMER_SEL_76, new AttackStyle(STYLE_ACCURATE, BONUS_CRUSH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_CRUSH)),

        /**
         * The Abyssal whip.
         */
        WHIP(Components.WEAPON_WHIP_SEL_93, new AttackStyle(STYLE_ACCURATE, BONUS_SLASH), new AttackStyle(STYLE_CONTROLLED, BONUS_SLASH), new AttackStyle(STYLE_DEFENSIVE, BONUS_SLASH)),

        /**
         * The Flowers.
         */
        FLOWERS(Components.WEAPON_WARHAMMER_SEL_76, new AttackStyle(STYLE_ACCURATE, BONUS_CRUSH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_CRUSH)),

        /**
         * The Mud pie.
         */
        MUD_PIE(Components.WEAPON_THROWN_SEL_91, new AttackStyle(STYLE_RANGE_ACCURATE, BONUS_RANGE), new AttackStyle(STYLE_RAPID, BONUS_RANGE), new AttackStyle(STYLE_LONG_RANGE, BONUS_RANGE)),

        /**
         * The Spear.
         */
        SPEAR(Components.WEAPON_SPEAR_SEL_87, new AttackStyle(STYLE_CONTROLLED, BONUS_STAB), new AttackStyle(STYLE_CONTROLLED, BONUS_SLASH), new AttackStyle(STYLE_CONTROLLED, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_STAB)),

        /**
         * The Halberd.
         */
        HALBERD(Components.WEAPON_HALBERD_SEL_84, new AttackStyle(STYLE_CONTROLLED, BONUS_STAB), new AttackStyle(STYLE_AGGRESSIVE, BONUS_SLASH), new AttackStyle(STYLE_DEFENSIVE, BONUS_STAB)),

        /**
         * The Bow.
         */
        BOW(Components.WEAPON_BOW_SEL_77, new AttackStyle(STYLE_RANGE_ACCURATE, BONUS_RANGE), new AttackStyle(STYLE_RAPID, BONUS_RANGE), new AttackStyle(STYLE_LONG_RANGE, BONUS_RANGE)),

        /**
         * The Crossbow.
         */
        CROSSBOW(Components.WEAPON_XBOW_SEL_79, new AttackStyle(STYLE_RANGE_ACCURATE, BONUS_RANGE), new AttackStyle(STYLE_RAPID, BONUS_RANGE), new AttackStyle(STYLE_LONG_RANGE, BONUS_RANGE)),

        /**
         * The Thrown weapons.
         */
        THROWN_WEAPONS(Components.WEAPON_THROWN_SEL_91, new AttackStyle(STYLE_RANGE_ACCURATE, BONUS_RANGE), new AttackStyle(STYLE_RAPID, BONUS_RANGE), new AttackStyle(STYLE_LONG_RANGE, BONUS_RANGE)),

        /**
         * The Chinchompa.
         */
        CHINCHOMPA(Components.WEAPON_CHINCHOMPA_SEL_473, new AttackStyle(STYLE_RANGE_ACCURATE, BONUS_RANGE), new AttackStyle(STYLE_RAPID, BONUS_RANGE), new AttackStyle(STYLE_LONG_RANGE, BONUS_RANGE)),

        /**
         * The Fixed device.
         */
        FIXED_DEVICE(Components.WEAPON_FIXED_DEVICE_SEL_80, new AttackStyle(STYLE_RANGE_ACCURATE, BONUS_RANGE), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH)),

        /**
         * The Salamander.
         */
        SALAMANDER(Components.WEAPON_SALAMANDER_SEL_474, new AttackStyle(STYLE_AGGRESSIVE, BONUS_SLASH), new AttackStyle(STYLE_RANGE_ACCURATE, BONUS_RANGE), new AttackStyle(STYLE_DEFENSIVE_CAST, BONUS_MAGIC)),

        /**
         * The Scythe.
         */
        SCYTHE(Components.WEAPON_SCYTHE_SEL_86, new AttackStyle(STYLE_ACCURATE, BONUS_SLASH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_STAB), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_SLASH)),

        /**
         * The Ivandis flail.
         */
        IVANDIS_FLAIL(Components.WEAPON_SCEPTER_SEL_85, new AttackStyle(STYLE_ACCURATE, BONUS_CRUSH), new AttackStyle(STYLE_AGGRESSIVE, BONUS_CRUSH), new AttackStyle(STYLE_DEFENSIVE, BONUS_CRUSH));

        /**
         * The interface id.
         */
        private final int interfaceId;

        /**
         * The attack styles.
         */
        private final AttackStyle[] attackStyles;

        WeaponInterfaces(int interfaceId, AttackStyle... attackStyles) {
            this.interfaceId = interfaceId;
            this.attackStyles = attackStyles;
        }

        /**
         * Gets interface id.
         *
         * @return the interface id
         */
        public int getInterfaceId() {
            return interfaceId;
        }

        /**
         * Get attack styles attack style.
         *
         * @return the attack style
         */
        public AttackStyle[] getAttackStyles() {
            return attackStyles;
        }
    }

    /**
     * Gets weapon interface.
     *
     * @return the weapon interface
     */
    public WeaponInterfaces getWeaponInterface() {
        return current;
    }

    /**
     * Is special bar boolean.
     *
     * @return the boolean
     */
    public boolean isSpecialBar() {
        return specialBar;
    }

}
