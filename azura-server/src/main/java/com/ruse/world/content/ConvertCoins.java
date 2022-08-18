package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

public class ConvertCoins {

	public static void convertMillCoins(Player player) {
		if (player.getInventory().getFreeSlots() < 1) {
			player.sendMessage("You need atleast "+1+" inventory slots to do this.");
			return;
		}
		if (player.getInventory().getAmount(ItemDefinition.COIN_ID) >= 1_000) {
			int amount = player.getInventory().getAmount(ItemDefinition.COIN_ID) / 1_000;
			player.getInventory().delete(new Item(ItemDefinition.COIN_ID, amount * 1_000));
			player.getInventory().add(new Item(ItemDefinition.TOKEN_ID, amount));
			player.sendMessage("You have successfully converted coins to Solak tokens");
		} else {
			player.sendMessage("You don't have enough coins to convert to Solak tokens");
		}
	}
	/*
	public static void convertMillCoins(Player player) {
		if (player.getInventory().getAmount(ItemDefinition.COIN_ID) >= 1_000_000) {
			int amount = player.getInventory().getAmount(ItemDefinition.COIN_ID) / 1_000_000;
			player.getInventory().delete(new Item(ItemDefinition.COIN_ID, player.getInventory().getAmount(995)));
			player.getInventory().add(new Item(5021, amount));
			player.sendMessage("You have successfully converted your Solak tokens to 1m bag");
		} else {
			player.sendMessage("You don't have enough coins to convert to 1m bag");
		}
	}
*/
}
