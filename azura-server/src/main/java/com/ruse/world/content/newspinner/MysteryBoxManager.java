package com.ruse.world.content.newspinner;



import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.World;
import com.ruse.world.content.newspinner.boxes.MysteryBox;
import com.ruse.world.content.newspinner.boxes.RegularMysteryBox;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerHandler;

import java.util.ArrayList;
import java.util.Collections;

public class MysteryBoxManager {

    private final int MAIN_INTERFACE_ID = 47000;
    private final int POSSIBLE_REWARDS_INTERFACE_ID = 47019;
    private final int REWARD_STRIP_INTERFACE_ID = 47022;
    private final int SPINS_LEFT_INTERFACE_ID = 47009;
    private final int REWARD_TEXT_ID = 47028;
    private final int REWARD_ITEM_ID = 47026;
    private final int HEADER_ID = 47007;

    private final int MAGIC_INDEX = 41;

    private boolean isSpinning = false;

    private Player player;

    private MysteryBox mysteryBox;

    public MysteryBoxManager(Player player) {
        this.player = player;
    }

    public void openBox(int boxId) {
        player.sendMessage(":refreshspinner:");

        if (boxId == 6199) {
            mysteryBox = new RegularMysteryBox();
        } else {
            mysteryBox = null;
        }
        loadInformation();
        loadPossibleRewards();
        loadRewardStrip();
        refreshLatest();
        player.getPacketSender().sendInterface(MAIN_INTERFACE_ID);
        player.getPacketSender().sendFrame126("Status: Inspecting box.", 47011);
    }


    private void loadInformation() {
        int boxesInInventory = player.getInventory().getAmount(mysteryBox.getId());

        player.getPacketSender().sendFrame126(mysteryBox.getName(), HEADER_ID);
        player.getPacketSender().sendFrame126(Integer.toString(boxesInInventory), SPINS_LEFT_INTERFACE_ID);
    }

    public void clearMysteryBox() {
        for (int i = 0; i < 100; i++) {
            player.getPacketSender().sendItemOnInterface(POSSIBLE_REWARDS_INTERFACE_ID, -1, i, -1);
        }
    }

    public void loadPossibleRewards() {
        int slot = 0;

        for (Item item : mysteryBox.getSuper_rare_items()) {
            player.getPacketSender().sendItemOnInterface(POSSIBLE_REWARDS_INTERFACE_ID, item.getId(), slot, item.getAmount());
            slot++;
        }

        for (Item item : mysteryBox.getRare_items()) {
            player.getPacketSender().sendItemOnInterface(POSSIBLE_REWARDS_INTERFACE_ID, item.getId(), slot, item.getAmount());
            slot++;
        }

        for (Item item : mysteryBox.getUncommon_items()) {
            player.getPacketSender().sendItemOnInterface(POSSIBLE_REWARDS_INTERFACE_ID, item.getId(), slot, item.getAmount());
            slot++;
        }

        for (Item item : mysteryBox.getCommon_items()) {
            player.getPacketSender().sendItemOnInterface(POSSIBLE_REWARDS_INTERFACE_ID, item.getId(),  slot, item.getAmount());
            slot++;
        }
    }

    public void addLatestLoot(int mysteryPrize) {
        if (mysteryBox.getId() == 6199) {
            for (int i = 17; i > 0; i--) {
                player.mboxLoot[i] = player.mboxLoot[i - 1];
            }
            player.mboxLoot[0] = mysteryPrize;
        }
        refreshLatest();
    }

    public void refreshLatest() {
        if (mysteryBox.getId() == 6199) {
            for (int i = 0; i < 17; i++) {
                player.getPacketSender().sendItemOnInterface(47030, player.mboxLoot[i], i, 1);
            }
        }
    }

    public void loadRewardStrip() {
        ArrayList<Item> all_rewards = new ArrayList<>();
        all_rewards.addAll(mysteryBox.getCommon_items());
        all_rewards.addAll(mysteryBox.getUncommon_items());
        all_rewards.addAll(mysteryBox.getRare_items());
        all_rewards.addAll(mysteryBox.getSuper_rare_items());
        Collections.shuffle(all_rewards);

        int slot = 0;

        for (Item item : all_rewards) {
            player.getPacketSender().sendItemOnInterface(REWARD_STRIP_INTERFACE_ID,item.getId(),  slot, item.getAmount());
            slot++;
        }
    }

    public void findReward() {
        if (player.getInventory().getAmount(mysteryBox.getId()) <= 0) {
            player.sendMessage("You must have a <col=0A6A30>"+(mysteryBox.getId() == 13302 ? "wilderness chest key" : "mystery box")+"</col> in your inventory.");
            return;
        }

        if (isSpinning) {
            player.sendMessage("You must wait until the current spin is finished.");
            return;
        }

        isSpinning = true;
        player.canMysteryBox = false;
        player.getPacketSender().sendFrame126("Status: Rolling...", 47011);

        player.getInventory().delete(mysteryBox.getId(), 1);

        int random = Helper.random(1, 100);
        ArrayList<Item> reward_list = random < 59 ? mysteryBox.getCommon_items() : random >= 59 && random <= 92 ? mysteryBox.getUncommon_items() : random > 90 && random <= 98 ? mysteryBox.getRare_items() : mysteryBox.getSuper_rare_items();
        Item reward = Helper.random(reward_list);

        ArrayList<Item> all_rewards = new ArrayList<>();
        all_rewards.addAll(mysteryBox.getCommon_items());
        all_rewards.addAll(mysteryBox.getUncommon_items());
        all_rewards.addAll(mysteryBox.getRare_items());
        all_rewards.addAll(mysteryBox.getSuper_rare_items());
        Collections.shuffle(all_rewards);

        all_rewards.set(MAGIC_INDEX, reward);

        int slot = 0;

        player.sendMessage(":spinner:");

        for (Item item : all_rewards) {
            player.getPacketSender().sendItemOnInterface(REWARD_STRIP_INTERFACE_ID,item.getId(), slot,  item.getAmount());
            slot++;
        }

        TaskManager.submit(new Task(9) {
            @Override
            protected void execute() {
                player.getInventory().add(reward.getId(), reward.getAmount());
                String messageToPlayer = "You open the "+(mysteryBox.getId() == 13302 ? "chest" : "box")+" and find" + " " + Helper.getAorAn(ItemDefinition.forId(reward.getId()).getName()) + " " + "<col=0A6A30>" + ItemDefinition.forId(reward.getId()).getName() + "</col> in your inventory.";
                if (random >= 59) {
                    messageToPlayer = "You open the "+(mysteryBox.getId() == 13302 ? "chest" : "box")+" and find" + " " + Helper.getAorAn(ItemDefinition.forId(reward.getId()).getName()) + " " + "<col=FF6100>" + ItemDefinition.forId(reward.getId()).getName() + "</col> in your inventory.";
                    if (random > 92) {
                        messageToPlayer = "You open the "+(mysteryBox.getId() == 13302 ? "chest" : "box")+" and find" + " " + Helper.getAorAn(ItemDefinition.forId(reward.getId()).getName()) + " " + "<col=FF0000>" + ItemDefinition.forId(reward.getId()).getName() + "</col> in your inventory.";
                     World.sendMessage(""+player.getUsername()+" has just found: <col=006600>" +reward.getAmount()+"x " + ItemDefinition.forId(reward.getId()).getName() + "</col> in a mystery box!");
                    }
                }

                player.getPacketSender().sendFrame126("Status: Reward found - @red@"+ItemDefinition.forId(reward.getId()).getName(), 47011);
                player.sendMessage(messageToPlayer);
                player.canMysteryBox = true;
                isSpinning = false;
                loadInformation();
                addLatestLoot(reward.getId());
                this.stop();
            }
        });
    }
}
