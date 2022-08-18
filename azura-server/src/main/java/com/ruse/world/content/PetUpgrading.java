package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.Dialogue;
import com.ruse.world.content.dialogue.DialogueExpression;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.DialogueType;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;

public enum PetUpgrading {

    WOLF_PET(459, 460, 100, "100 pet fragments + 1K PVM Tickets to upgrade @red@(100% success)",
            new Item[]{new Item(19000, 100), new Item(5022, 1000)}),

    GORILLA_PET(460, 5508, 90, "250 pet fragments + 10K PVM Tickets to upgrade @red@(90% success)",
            new Item[]{new Item(19000, 250), new Item(5022, 10000)}),

    MARIO_PET(5508, 5506, 80, "500 pet fragments + 50K PVM Tickets to upgrade @red@(80% success)",
            new Item[]{new Item(19000, 500), new Item(5022, 50000)}),

    DONKEY_KONG_PET(5506, 461, 65, "1000 pet fragments + 100K PVM Tickets to upgrade @red@(65% success)",
            new Item[]{new Item(19000, 1000), new Item(5022, 100000)}),

    VEGETA_PET(461, 462, 50, "2000 pet fragments + 500K PVM Tickets to upgrade @red@(50% success)",
            new Item[]{new Item(19000, 2000), new Item(5022, 500000)}),

    YOSHI_PET(462, 11319, 35, "2500 pet fragments + 1M PVM Tickets to upgrade @red@(35% success)",
            new Item[]{new Item(19000, 2500), new Item(5022, 1000000)}),

    ZORBAK(11319, 11318, 25, "5000 pet fragments + 5M PVM Tickets to upgrade @red@(25% success)",
            new Item[]{new Item(19000, 5000), new Item(5022, 5000000)}),

    RAICHU(11318, 22107, 25, "10K pet frags/10M PVM Ticks/10M Upg tokens @red@(25% success)",
                   new Item[]{new Item(19000, 10_000), new Item(5022, 10_000_000), new Item(ItemDefinition.TOKEN_ID, 10_000_000)}),
    ;

    @Getter
    private final int itemId, reward, upgradeChange;
    @Getter
    private final String message;
    @Getter
    private final Item[] required;

    PetUpgrading(int itemId, int reward, int upgradeChange, String message, Item[] required) {
        this.itemId = itemId;
        this.reward = reward;
        this.upgradeChange = upgradeChange;
        this.message = message;
        this.required = required;
    }


    public static boolean upgradeable(Player player, int itemId) {
        for (PetUpgrading petUpgrading : PetUpgrading.values()) {
            if (petUpgrading.itemId == itemId) {
                DialogueManager.start(player, 9923);
                player.setDialogueActionId(9923);
            }
        }
        return false;
    }


    public static void upgrade(Player player) {
        for (PetUpgrading petUpgrading : PetUpgrading.values()) {
            if (petUpgrading.itemId == player.getInteractingItem().getId()) {
                if (player.getInventory().contains(petUpgrading.getRequired())) {
                    player.getInventory().deleteItemSet(petUpgrading.getRequired());
                    if (1 + Misc.getRandom(99) < petUpgrading.getUpgradeChange()) {
                        player.getInventory().delete(petUpgrading.getItemId(), 1);
                        player.getInventory().add(petUpgrading.getReward(), 1);
                        DialogueManager.sendStatement(player, "You successfully upgraded to the next pet.");
                    } else {
                        DialogueManager.sendStatement(player, "You failed the upgrade to the next pet.");
                    }
                } else {
                    DialogueManager.sendStatement(player, "You do not have the required items.");
                }
                return;
            }
        }
    }

    public static PetUpgrading forId(int itemId) {
        for (PetUpgrading petUpgrading : PetUpgrading.values()) {
            if (petUpgrading.itemId == itemId) {
                return petUpgrading;
            }
        }
        return null;
    }

    
    public static Dialogue dialogue(final Player player) {
        return new Dialogue() {
            @Override
            public DialogueType type() {
                return DialogueType.OPTION;
            }

            @Override
            public int npcId() {
                return -1;
            }

            @Override
            public DialogueExpression animation() {
                return null;
            }

            @Override
            public String[] dialogue() {
                return new String[]{"Sell x" + player.getInventory().getAmount(19000) + " Pet fragments for 100 Solak tokens each ("
                        + (player.getInventory().getAmount(19000)  * 100)+" total)",  "Cancel"
                };
            }

            @Override
            public void specialAction() {
                player.setDialogueActionId(6969);
            }
        };
    }

}
