package com.ruse.world.content.junkstore;

import com.ruse.GameServer;
import com.ruse.model.Item;
import com.ruse.model.container.impl.Shop;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeneralStoreSelling {

    private static final ArrayList<Item> ITEMS = new ArrayList<>();

    public static void addItem(Player player, int rewardId, int price) {

        GameServer.getLoader().getEngine().submit(() -> {
            try {
                FileWriter fw = new FileWriter("./azura-server/data/def/sellableitems.txt", true);
                if (fw != null) {
                    fw.write(rewardId + " : " + price);
                    fw.write(System.lineSeparator());
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ITEMS.add(new Item(rewardId, price));
        player.sendMessage(
                "Added Sellable id \"" + rewardId + "\" for price: " + price + "\" name: " + ItemDefinition.forId(rewardId).getName());
    }

    public static void loadItems() {
        ITEMS.clear();
        try {
            BufferedReader r = new BufferedReader(new FileReader("./azura-server/data/def/sellableitems.txt"));
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                } else {
                    line = line.trim();
                }
                String[] code = line.split(" : ");

                ITEMS.add(new Item(Integer.parseInt(code[0]), Integer.parseInt(code[1])));
            }
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getPrice(int itemID) {
        for (Item item : ITEMS) {
            if (item.getId() == itemID) {
                return item.getAmount() * 2;
            }
        }
        return -1;
    }

    public static void init() {
        loadItems();

        Item[] items = getItems();
        if (Shop.ShopManager.getShops().containsKey(100))
            Shop.ShopManager.getShops().remove(100);

        Shop.ShopManager.getShops().put(100, new Shop(null, 100, "@lre@Sell Items (Solak Tokens)", new Item(10835), items));
    }

    public static Item[] getItems() {
        Item[] items = new Item[ITEMS.size()];
        int index = 0;
        for (Item i : ITEMS) {
            items[index++] = new Item(i.getId(), 0);
        }
        return items;
    }
}