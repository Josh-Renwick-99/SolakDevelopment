package com.ruse.world.content;

import com.ruse.world.entity.impl.player.Player;

import java.util.Random;

public class GoodieBag {

    private Player player;

    public GoodieBag(Player player) {
        this.player = player;
    }
    private boolean claimed = false;

    public int boxId = -1;

    //  public static final int[] REWARDS = { 1050, 14484, 4151, 20054, 4565, 1053, 1055, 1057, 1042, 1044, 1046, 1048,
    //  4777, 11694, 19055, 6199, 15373, 5206, 455, 10835 };

    public int[] rewards = {1050, 14484, 4151, 20054, 4565, 1053, 1055, 1057, 1042, 1044, 1046, 1048,
            4777, 11694, 19055, 6199, 15373, 5206, 455};

    public void setRewards(int[] rewards) {
        this.rewards = rewards;
    }

    public void open() {
        player.getPacketSender().sendInterface(49200);
        player.getPacketSender().resetItemsOnInterface(49270, 20);
        shuffle(rewards);
        claimed = false;
        player.selectedGoodieBag = -1;
       
        for (int i = 1; i <= 20; i++) {
            player.getPacketSender().sendString(49232 + i, String.valueOf(i));
        }

    }

    private void shuffle(int[] array) {
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    private void showRewards() {

        for (int i = 1; i <= 20; i++) {
            player.getPacketSender().sendString(49232 + i, "");
        }

        for (int i = 0; i < rewards.length; i++) {
            player.getPacketSender().sendItemOnInterface(49270, rewards[i], i, 1);
        }
    }

    public boolean handleClick(int buttonId) {
        if (!(buttonId >= -16325 && buttonId <= -16306)) {
            return false;
        }
        
        if(claimed) {
        	return false;
        }
        
        int index = -1;

        if (buttonId >= -16325) {
            index = 16325 + buttonId;
        }
        player.getPacketSender().sendString(49232 + player.selectedGoodieBag + 1,
                String.valueOf(player.selectedGoodieBag + 1));
        player.selectedGoodieBag = index;
        player.getPacketSender().sendString(49232 + index + 1, "Pick");

        return true;
    }
    // btw this code is also easy to use, u see how clean it is

    public void claim() {
        if (player.selectedGoodieBag == -1) {
            player.sendMessage("@red@You haven't picked a number yet");
            return;
        }

        if (boxId == -1) {
            player.sendMessage("You already opened this box"); // basically for it to refresh rewards(this is the best way) u gotta reopen each box
            // because otherwise, for it to reset back, u'd need another button etc.
            return;
        }
        if (!claimed) {
            if (player.getInventory().contains(boxId)) { // gg this is guaranteed to work
                showRewards();
                player.getInventory().delete(boxId, 1);
                player.getInventory().add(rewards[player.selectedGoodieBag], 1);
                claimed = true;
                boxId = -1;
            } else {
                player.sendMessage("@red@You need a goodiebag box to claim the reward");
            }
        } else {
            player.sendMessage("@red@You've already claimed the reward for this box");
        }
    }
}
