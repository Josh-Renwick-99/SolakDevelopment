package com.ruse.world.content.eventboss;

import java.util.ArrayList;
import java.util.List;

import com.ruse.model.Item;
import com.ruse.model.container.ItemContainer;
import com.ruse.model.container.StackType;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.model.input.EnterAmount;
import com.ruse.world.World;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class EventBossManager {

	private Player player;
    private int npcIdentification, npcHealth;
    private List<Item> npcDropRewards;

    public EventBossManager(Player player) {
        this.player = player;
        this.npcIdentification = -1;
        this.npcHealth = -1;
        this.npcDropRewards = new ArrayList<>();
    }

    public void display() {
    	resetInterfaceAttributes();
        updateInterfaceAttributes();
        player.getPacketSender().sendInterfaceSet(31500, 3321);
        player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
    }

    private void resetInterfaceAttributes() {
    	for (int index = 0; index < 35; index++) {
			player.getPacketSender().sendItemOnInterface(31510, -1, index, -1);
		}
    }
    
    private void updateInterfaceAttributes() {
    	player.getPacketSender().sendString(31504, npcIdentification > 0 ? NpcDefinition.forId(npcIdentification).getName() : "N/A");
    	player.getPacketSender().sendString(31506, "Npc ID: " + (npcIdentification > 0 ? Integer.toString(npcIdentification) : "N/A"));
    	player.getPacketSender().sendString(31507, "Health: " + (npcHealth > 0 ? Integer.toString(npcHealth) : "N/A"));
		player.getPacketSender().sendNpcIdToDisplayPacket(npcIdentification > 0 ? npcIdentification : 1, 31505);
    }

    private void updateLootTable() {
    	for (int index = 0; index < npcDropRewards.size(); index++) {
    		Item item = npcDropRewards.get(index);
			player.getPacketSender().sendItemOnInterface(31510, item.getId(), index, item.getAmount());
		}
    }
    
    public void updateNpcIdentification() {
        player.getPacketSender().sendEnterAmountPrompt("Enter NPC identification");
        player.setInputHandling(new EnterAmount() {
            @Override
            public void handleAmount(Player player, int amount) {
                npcIdentification = amount;
                player.getPacketSender().sendMessage("@bla@The npc identification has been set to " + amount + " (" + NpcDefinition.forId(npcIdentification).getName() + ")");
                updateInterfaceAttributes();
            }
        });
    }

    public void updateNpcHealth() {
        player.getPacketSender().sendEnterAmountPrompt("Enter NPC health");
        player.setInputHandling(new EnterAmount() {
            @Override
            public void handleAmount(Player player, int amount) {
                npcHealth = amount;
                player.getPacketSender().sendMessage("@bla@The npc health has been set to " + amount + " (" + NpcDefinition.forId(npcIdentification).getName() + ")");
                updateInterfaceAttributes();
            }
        });
    }
    
    public void addNpcDropReward(int itemId, int amount, int slot) {
        if(slot < 0)
            return;
        if(player == null)
            return;
        player.getPacketSender().sendClientRightClickRemoval();
        if(!player.getInventory().contains(itemId))
            return;
        if(slot >= player.getInventory().capacity() || player.getInventory().getItems()[slot].getId() != itemId || player.getInventory().getItems()[slot].getAmount() <= 0)
            return;
        Item itemToinsert = player.getInventory().getItems()[slot];
        if(itemToinsert.getId() != itemId)
            return;
        if (player.getInventory().getAmount(itemId) < amount) {
            amount = player.getInventory().getAmount(itemId);
            if (amount == 0 || player.getInventory().getAmount(itemId) < amount) {
                return;
            }
        }
        if (!itemToinsert.getDefinition().isStackable()) {
            for (int a = 0; a < amount && a < 28; a++) {
                if (player.getInventory().getAmount(itemId) >= 1) {
                	npcDropRewards.add(new Item(itemId, 1));
                    player.getInventory().delete(itemId, 1);
                }
            }
        } else
        if (itemToinsert.getDefinition().isStackable()) {
            boolean itemInTrade = false;
            for (Item item : npcDropRewards) {
                if (item.getId() == itemId) {
                    itemInTrade = true;
                    item.setAmount(item.getAmount() + amount);
                    player.getInventory().delete(itemId, amount);
                    break;
                }
            }
            if (!itemInTrade) {
            	npcDropRewards.add(new Item(itemId, amount));
                player.getInventory().delete(itemId, amount);
            }
        }
        player.getInventory().refreshItems();
        for(int i = 0; i < npcDropRewards.size(); i++) {
            player.getPacketSender().sendItemOnInterface(31510, npcDropRewards.get(i).getId(), i, npcDropRewards.get(i).getAmount());
        }
        player.getPacketSender().sendInterfaceSet(31500, 3321);
        player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
    }

    public void removeNpcDropReward(int itemId, int amount) {
        ItemDefinition def = ItemDefinition.forId(itemId);
        if (!def.isStackable()) {
            if (amount > 28)
                amount = 28;
            for (int a = 0; a < amount; a++) {
                for (Item item : npcDropRewards) {
                    if (item.getId() == itemId) {
                        if (!item.getDefinition().isStackable()) {
                        	npcDropRewards.remove(item);
                            player.getInventory().add(itemId, 1);
                        }
                        break;
                    }
                }
            }
        } else
            for (Item item : npcDropRewards) {
                if (item.getId() == itemId) {
                    if (item.getDefinition().isStackable()) {
                        if (item.getAmount() > amount) {
                            item.setAmount(item.getAmount() - amount);
                            player.getInventory().add(itemId, amount);
                        } else {
                            amount = item.getAmount();
                            npcDropRewards.remove(item);
                            player.getInventory().add(itemId, amount);
                        }
                    }
                    break;
                }
            }
        player.getInventory().refreshItems();
        resetInterfaceAttributes();
        for(int i = 0; i < npcDropRewards.size(); i++) {
            player.getPacketSender().sendItemOnInterface(31510, npcDropRewards.get(i).getId(), i, npcDropRewards.get(i).getAmount());
        }
        player.getPacketSender().sendInterfaceSet(31500, 3321);
        player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
    }
    
    public void initiateSpawn() {
        if(npcIdentification > -1 && npcHealth > -1) {
            NPC eventBossEntity = new NPC(npcIdentification, player.getPosition());
            eventBossEntity.setConstitution(npcHealth);
			player.getPacketSender().sendInterfaceRemoval();
            eventBossEntity.setDefaultConstitution(npcHealth);
            eventBossEntity.setEventBoss(true);
            eventBossEntity.setEventBossRewards(npcDropRewards);
			player.getPacketSender().sendInterfaceRemoval();
            World.sendMessage("<shad=1>@yel@<img=18>[Event Boss]@gre@ The Event Boss has spawned at ::Eventboss");
            npcDropRewards.forEach(item -> World.sendMessage("@red@Event Boss Drops:@whi@ " + item.getDefinition().getName() + " x " + item.getAmount()));
            World.sendMessage("<shad=1>@yel@<img=18>[Event Boss]@gre@ The Event Boss has spawned at ::Eventboss");
            World.register(eventBossEntity);
            
        } else {
            player.getPacketSender().sendMessage("@red@Something went wrong, please re-check the event boss settings.");
        }
    }
}
