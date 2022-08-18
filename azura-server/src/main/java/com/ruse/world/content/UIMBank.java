package com.ruse.world.content;

import java.util.Map;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

public class UIMBank {

	private Player player;

	public UIMBank(Player player) {
		this.player = player;
	}

	public void open() {
		player.getPacketSender().resetItemsOnInterface(48721, 70);
		player.getPacketSender().sendInterfaceSet(48700, 3321);
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		update();
	}

	public void deposit(int id, int amount) {
		if(player.getInterfaceId() == 48700) {
		if (player.getUimBankItems().size() >= 70) {
			player.sendMessage("@red@Your Ultimate ironman bank can only hold 70 items.");
			return;
		}

		if (!player.getInventory().contains(id)) {
			return;
		}

		player.getInventory().delete(id, amount);

		player.getUimBankItems().merge(id, amount, Integer::sum);

		update();
	}
	}

	public void withdraw(int id, int amount) {
		ItemDefinition def = new Item(id).getDefinition();
		boolean stackable = def.isNoted() || def.isStackable();
		if (player.getInventory().getFreeSlots() < amount) { // Done, gn.
			if (!stackable ) {
				player.sendMessage("Get some more free inventory space");
				return;
			} else {

				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("Get some more free inventory space");
					return;
				}
			}
		}

		if (player.getUimBankItems().get(id) != null) {
			if (amount >= player.getUimBankItems().get(id)) {
				player.getUimBankItems().remove(id);
				player.getInventory().add(id, amount);
			} else {
				player.getUimBankItems().replace(id, player.getUimBankItems().get(id) - amount);
				player.getInventory().add(id, amount);
			}
		}

		update();

	}

	private void update() {
		player.getInventory().refreshItems();
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		player.getPacketSender().resetItemsOnInterface(48721, 70);
		int index = 0;
		for (Map.Entry<Integer, Integer> entry : player.getUimBankItems().entrySet()) {
			if (entry.getValue() > 0) {
				player.getPacketSender().sendItemOnInterface(48721, entry.getKey(), index, entry.getValue());
			}
			index++;
		}
	}

}
