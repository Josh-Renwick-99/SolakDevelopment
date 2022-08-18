package com.ruse.world.content.pos;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.net.packet.Packet.PacketType;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a single player owned shop. In this we hold and manage
 * all the items that are added or sold using an instance of this class. A
 * single instance of this class shows a single player owned shop in the manager
 * class.
 *
 * @author Nick Hartskeerl <apachenick@hotmail.com>
 */
public class PlayerOwnedShop {

    /**
     * The total capacity of items a shop can contain.
     */
    public static final int SHOP_CAPACITY = 32;
    /**
     * The name of the player owning this player owned shop.
     */
    String username;
    /**
     * A collection of all the items in this player owned shop. If an item slot is
     * empty this is represented as {@code null}, else as an {@link ShopItem}
     * instance.
     */
    private ShopItem[] items = new ShopItem[SHOP_CAPACITY];
    /**
     * A collection of all the items in this player owned shop. If an item slot is
     * empty this is represented as {@code null}, else as an {@link ShopItem}
     * instance.
     */
    private ArrayList<HistoryItem> history = new ArrayList<HistoryItem>();
    /**
     * A reference to the player owning this shop. We use this reference to notify
     * the shop owner of certain events.
     */
    private Player owner;
    private long earnings;

    public static void resetItems(Player player) {

        for (int i = 0; i < SHOP_CAPACITY; i++) {

            PacketBuilder out = new PacketBuilder(34, PacketType.SHORT);

            out.putShort(32621);
            out.put(i);
            out.putShort(0);
            out.put(0);

            player.getSession().queueMessage(out);

        }

    }

    public void open(Player player) {
        player.getPacketSender().sendString(32610, "Player Owned Shop - " + username);
        resetItems(player);
        refresh(player, false);
        player.getPacketSender().sendInterface(32600);
    }

    public void refresh(Player player, boolean myShop) {

        for (int i = 0; i < items.length; i++) {

            ShopItem item = items[i];

            PacketBuilder out = new PacketBuilder(34, PacketType.SHORT);

            out.putShort(myShop ? 33621 : 32621);
            out.put(i);
            out.putShort(item == null ? 0 : item.getId() + 1);

            if (item != null && item.getAmount() > 254) {
                out.put(255);
                out.putInt(item.getAmount());
            } else {
                out.put(item == null ? 0 : item.getAmount());
            }

            player.getSession().queueMessage(out);

        }

    }

    public void add(int id, int amount) {

        ItemDefinition definition = ItemDefinition.forId(id);
        long price = 0;

        if (definition != null) {
            price = definition.getValue();
        }

        add(id, amount, price);

    }

    public void add(int id, int amount, long price) {
        add(new ShopItem(id, amount, price, amount));
        refreshAll();
        save();
    }

    public void add(ShopItem item) {

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getId() == item.getId()) {
                items[i].setAmount(items[i].getAmount() + item.getAmount());
                items[i].setMaxAmount(items[i].getMaxAmount() + item.getMaxAmount());
                return;
            }
        }

        int index = freeSlot();

        if (index != -1) {
            if (items[index] == null) {
                items[index] = item;
                String itemName = item.getDefinition().getName();
                owner.sendMessage("You have set <col=FF0000>" + (itemName == null ? "the merchandise" : itemName)
                        + "</col> to cost <col=FF0000>" + Misc.sendCashToString(item.price)
                        + "</col> Solak tokens in your shop.");

                PlayerOwnedShopManager.addItem(item, owner.getUsername());
            }
        }

    }

    public int remove(int slot, int amount) {

        ShopItem item = getItem(slot);
        int removed = -1;

        if (item != null) {
            if (amount >= item.getAmount()) {
                PlayerOwnedShopManager.removeItem(item, getUsername());
                items[slot] = null;
                shift();
                removed = item.getAmount();
            } else {
                item.setAmount(item.getAmount() - amount);
                removed = amount;
            }
            save();
            refreshAll();
        }

        return removed;

    }

    public void shift() {

        List<ShopItem> temp = new ArrayList<>();

        for (ShopItem item : items) {
            if (item != null) {
                temp.add(item);
            }
        }

        items = temp.toArray(new ShopItem[SHOP_CAPACITY]);

    }

    public int freeSlot() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public ShopItem getItem(int slot) {
        return items[slot];
    }

    public int getAmount(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getId() == id) {
                return items[i].getAmount();
            }
        }
        return 0;
    }


    public long getValueFromName(String name) {
        long lowest = Long.MAX_VALUE;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getDefinition() != null && items[i].getDefinition().getName() != null
                    && items[i].getDefinition().getName().toLowerCase().contains(name) && items[i].price < lowest) {
                lowest = items[i].price;
            }
        }
        return lowest;
    }

    public boolean contains(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getId() == id) {
                return true;
            }
        }
        return false;
    }

    public int forSlot(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(String name) {

        if (name == null) {
            return false;
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getDefinition() != null && items[i].getDefinition().getName() != null
                    && items[i].getDefinition().getName().toLowerCase().contains(name)) {
                return true;
            }
        }
        return false;

    }
    public ArrayList<ShopItem> forName(String name) {

        if (name == null) {
            return null;
        }
        ArrayList<ShopItem> foundItems = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getDefinition() != null && items[i].getDefinition().getName() != null
                    && items[i].getDefinition().getName().toLowerCase().contains(name)) {
                foundItems.add(items[i]);
            }
        }
        return foundItems;

    }

    public int size() {
        int size = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                size++;
            }
        }
        return size;
    }

    public void refreshAll() {
        for (Player player : World.getPlayers()) {
            if (player != null && player.getPlayerOwnedShopManager().getCurrent() == this) {
                refresh(player, player.getPlayerOwnedShopManager().getMyShop() == this);
            }
        }
    }

    /**
     * Access the online or offline player instance of the owner of this player
     * owned shop. If the owner is indeed offline his/her details will be loaded
     * from the saved serialized character file and reinterpreted to actual OOP
     * objects to access the instance within the JVM.
     *
     * @param force If the instance is forced to be online.
     * @return The player instance to the owner.
     */
    /*
     * public Player accessOwner(boolean force) {
     *
     * if(owner == null && !force) {
     *
     * owner = World.getPlayerByName(username);
     *
     * Path path = Paths.get(GameSettings.getDataLocation()+"saves/characters/",
     * username + ".json"); File file = path.toFile();
     *
     * if(owner == null && file.exists()) {
     *
     * Player player = new Player(null);
     *
     * player.setUsername(username);
     * player.setLongUsername(NameUtils.stringToLong(username));
     *
     * PlayerLoading.getResult(player, true);
     *
     * return player;
     *
     * }
     *
     * }
     *
     * return owner;
     *
     * }
     */

    /*
     * public void addMoney(long amount) {
     *
     * Player owner = accessOwner(false);
     *
     * if(owner != null) {
     *
     * owner.getPlayerOwnedShopManager().addEarnings(amount);
     *
     * //owner.getItems().sendItemToAnyTabOffline(995, amount, owner.isActive());
     *
     * if(!owner.isActive()) { owner.setShopUpdated(true); } else { String
     * formatPrice = Misc.sendCashToString(amount);
     * owner.sendMessage("<col=FF0000>You have earned "
     * +formatPrice+" coins from your shop. Make sure to claim it.</col>"); }
     *
     * PlayerSaving.save(owner);
     *
     * }
     *
     * }
     */
    public void saveHistory() {

        Path path = Paths.get("./data/saves/", "posHistory.txt");

        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (HistoryItem item : PlayerOwnedShopManager.ITEMS) {
                if (item != null) {
                    writer.write(item.getId() + " - " + item.getAmount() + " - " + item.getPrice() + " - " + item.getBuyer());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void save() {
        saveHistory();
        Path path = Paths.get(PlayerOwnedShopManager.DIRECTORY + File.separator, getUsername() + ".txt");

        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(Long.toString(this.earnings));
            writer.newLine();
            for (ShopItem item : getItems()) {
                if (item != null) {
                    writer.write(item.getId() + " - " + item.getAmount() + " - " + item.getPrice() + " - " + item.getMaxAmount());
                    writer.newLine();
                }
            }

            writer.write("History:");
            writer.newLine();
            for (HistoryItem item : getHistoryItems()) {
                if (item != null) {
                    writer.write(item.getId() + " _ " + item.getAmount() + " _ " + item.getPrice() + " _ " + item.getBuyer());
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get a reference to a collection of all the items in this player owned shop.
     * If an item slot is empty it will be shown as {@code null}, else as a
     * {@link ShopItem} instance.
     *
     * @return The array of items in this player owned shop.
     */
    public ShopItem[] getItems() {
        return items;
    }
    public ArrayList<HistoryItem> getHistoryItems() {
        return history;
    }

    /**
     * Set a new array of items to represent the collection of all items in this
     * player owned shop. If an item slot is empty it must be shown as {@code null},
     * else as a {@link ShopItem} instance.
     *
     * @param items The new array of items for this shop.
     */
    public void setItems(ShopItem[] items) {
        this.items = items;
    }

    public long getEarnings() {
        return this.earnings;
    }

    public void setEarnings(long earnings) {
        this.earnings = earnings;
    }

    public void addEarnings(long toAdd) {
        this.earnings += toAdd;
    }

    /**
     * Get the reference to the player instance of the owner of this shop. It is
     * important to notice that with this reference the player instance can refer to
     * an offline player. If you would like to gain access to the player owning this
     * shop while this player is online or offline use the
     * {@link #accessOwner(boolean)} method instead.
     *
     * @return A reference to the player owning this shop.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * @param owner
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static class ShopItem {

        private int id;

        private int amount;

        @Getter
        @Setter
        private int maxAmount;

        private long price;

        public ShopItem(int id) {
            this(id, 1, 1);
        }

        public ShopItem(int id, int amount, int maxAmount) {
            this.id = id;
            this.amount = amount;
            this.maxAmount = maxAmount;
        }

        public ShopItem(int id, int amount, long price, int maxAmount) {
            this(id, amount, maxAmount);
            this.price = price;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public ItemDefinition getDefinition() {
            return ItemDefinition.forId(id);
        }

    }


    public static class HistoryItem {

        @Getter
        @Setter
        private int id, amount;
        @Getter
        @Setter
        private String buyer;
        @Getter
        @Setter
        private long price;


        public HistoryItem(int id, int amount, long price, String buyer) {
            this.id = id;
            this.amount = amount;
            this.price = price;
            this.buyer = buyer;
        }

        public ItemDefinition getDefinition() {
            return ItemDefinition.forId(id);
        }

    }
}
