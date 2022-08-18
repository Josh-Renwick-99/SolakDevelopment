package com.ruse.world.content.minigames.impl;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.content.casketopening.Box;
import com.ruse.world.content.casketopening.BoxLoot;
import com.ruse.world.content.dailytasks_new.DailyTask;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.HashMap;

public class TreasureHunter {

    public static final Position TELEPORT_AREA = new Position(2015, 5022, 0);
    public static final int CHEST_ID = 41207;

    public static int NPC_1 = 9815, NPC_2 = 9816, NPC_3 = 9817, NPC_4 = 9818;
    public static int KEY_1 = 23103;
    public static int KEY_2 = 23104;
    public static int KEY_3 = 23105;
    public static int KEY_4 = 23106;
    public static Item MASTER_KEY = new Item(23107);
    private HashMap<Item, Integer> auraDrops = new HashMap<>();

    public static void addKey(Player player, NPC npc) {
       /* int chanceInt = Misc.getRandom(3);
        if (chanceInt == 1) {
            if (npc.getId() == NPC_1) {
                player.getInventory().add(new Item(KEY_1));
            } else if (npc.getId() == NPC_2) {
                player.getInventory().add(new Item(KEY_2));
            } else if (npc.getId() == NPC_3) {
                player.getInventory().add(new Item(KEY_3));
            } else if (npc.getId() == NPC_4) {
                player.getInventory().add(new Item(KEY_4));
            }
            System.out.println("Congratulations, you've received a key from killing a " + npc.getDefinition().getName() + "!");
        }*/
    }

    public static boolean combineKeys(Player player, Item used, Item usedWith) {
        if (used.getId() >= KEY_1 && used.getId() <= KEY_4 && usedWith.getId() >= KEY_1 && usedWith.getId() <= KEY_4) {
            if (player.getInventory().contains(new Item[]{new Item(KEY_1), new Item(KEY_2), new Item(KEY_3), new Item(KEY_4)})) {
                player.getInventory().deleteItemSet(new Item[]{new Item(KEY_1), new Item(KEY_2), new Item(KEY_3), new Item(KEY_4)});
                if (Misc.getRandom(100) <= 75){
                    player.getInventory().add(MASTER_KEY);
                    player.sendMessage("You've successfully created the Master Key.");
                }else{
                    player.sendMessage("You've failed the Master Key creation.");
                }
                return true;
            }
        }
        return false;
    }

    public static void useKeyOnChest(Player player, Item item) {
        if (item.getId() == MASTER_KEY.getId() && player.getInventory().contains(MASTER_KEY.getId())) {
            player.getInventory().delete(MASTER_KEY);

            handleRewards(player);
        }
    }

    /**
     * Returns the Chest Rewards
     *
     * @param player
     */
    public static void handleRewards(Player player) {
        //Guaranteed Cash Drop
        player.getInventory().add(ItemDefinition.COIN_ID, 50000);

        Box box = BoxLoot.getLoot(loot);
        player.getInventory().add(box.getId(), box.getAmount());

        Achievements.doProgress(player, Achievements.Achievement.OPEN_5_CHESTS_IN_TREASURE_HUNTER);
        Achievements.doProgress(player, Achievements.Achievement.OPEN_25_CHESTS_IN_TREASURE_HUNTER);
        Achievements.doProgress(player, Achievements.Achievement.OPEN_200_CHESTS_IN_TREASURE_HUNTER);
        DailyTask.TREASURE_HUNTER.tryProgress(player);

    }


    public static Box[] loot = { //
            new Box(995, 0, 95000, 100D),
            new Box(23126, 1, 0.4D, true),
            new Box(23123, 1, 0.4D, true),
            new Box(23120, 1, 0.4D, true),
            new Box(23048, 1, 0.285D, true),
            new Box(23049, 1, 0.2D, true),
    };

}
