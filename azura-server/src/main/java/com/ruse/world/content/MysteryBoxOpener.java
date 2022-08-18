package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.RandomUtility;
import com.ruse.world.World;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.player.Player;

import java.util.HashMap;
import java.util.Map;

public class MysteryBoxOpener {

	private Player player;

	public MysteryBoxOpener(Player player) {
		this.player = player;
	}

	private int openBox = -1;

	public int getOpenBox() {
		return openBox;
	}

	private int[] common, uncommon, rare;

	public void display(int boxId, String name, int[] common, int[] uncommon, int[] rare) {
		player.getPacketSender().sendInterface(48030);
		openBox = boxId;
		this.common = common;
		this.uncommon = uncommon;
		this.rare = rare;
		updateInterface(boxId, name, common, uncommon, rare);
	}

	private void updateInterface(int boxId, String name, int[] common, int[] uncommon, int[] rare) {

		for(int i = 0; i < 3; i++) {
			player.getPacketSender().resetItemsOnInterface(48051 + i * 10,150);
		}
		player.getPacketSender().sendString(48035, name);
		player.getPacketSender().sendItemOnInterface(48045, boxId, 0, 1);

		for (int i = 0; i < common.length; i++) {
			player.getPacketSender().sendItemOnInterface(48051, common[i], i, 1);
		}

		for (int i = 0; i < uncommon.length; i++) {
			player.getPacketSender().sendItemOnInterface(48061, uncommon[i], i, 1);
		}

		for (int i = 0; i < rare.length; i++) {
			player.getPacketSender().sendItemOnInterface(48071, rare[i], i, 1);
		}

	}

	public void open(int boxId) {
		int reward = -1;
		int chance = RandomUtility.inclusiveRandom(0, 100);
		String name = ItemDefinition.forId(boxId).getName();
		if (chance > 95) { // OH LOL oh lol
			reward = rare[RandomUtility.exclusiveRandom(0, rare.length)];
			if (boxId != 6199 && boxId != 989 && boxId != 7956 && boxId != 10025 && boxId != 19116&& boxId != 19115) {
				World.sendMessage("<img=16><shad=1>@cya@[" + player.getUsername() + "] @or2@Just obtained a Very Rare reward from a " + name);
			}
		} else if (chance > 65) {
			reward = uncommon[RandomUtility.exclusiveRandom(0, uncommon.length)];
		} else if (chance >= 0) {
			reward = common[RandomUtility.exclusiveRandom(0, common.length)];
		}
		player.getInventory().delete(boxId, 1);
		player.getInventory().add(reward, 1);
		if (boxId == 11795) {
			TeleportHandler.teleportPlayer(player, new Position(2579, 2566),
					player.getSpellbook().getTeleportType());
		}
	}

	public void openAll(int boxId) {
		int amount = player.getInventory().getAmount(boxId);
		Map<Integer, Integer> rewards = new HashMap<>();
		for (int i = 0; i < amount; i++) {
			int reward = -1;
			int chance = RandomUtility.inclusiveRandom(0, 100);
			String name = ItemDefinition.forId(boxId).getName();
			if (chance > 97) { //90% of 100% = 10%
				reward = rare[RandomUtility.exclusiveRandom(0, rare.length)];
				if (boxId != 6199 && boxId != 989 && boxId != 7956) {
					World.sendMessage("<img=16><shad=1>@cya@[" + player.getUsername() + "] @or2@Just obtained a Very Rare reward from a " + name);
				}
			} else if (chance > 65) { //65% of 100% = 35% (remainder)
				reward = uncommon[RandomUtility.exclusiveRandom(0, uncommon.length)];
			} else if (chance >= 0) { //0% of 100% = 55% (remainder)
				reward = common[RandomUtility.exclusiveRandom(0, common.length)]; // ye its correct.
			}

			rewards.merge(reward, 1, Integer::sum);

		}
		player.getInventory().delete(boxId, amount);
		boolean bank = amount <= player.getInventory().getFreeSlots();
		rewards.forEach((key, value) -> {
			if (bank) {
				player.getInventory().add(new Item(key, value), false);
			} else {
				Item item = new Item(key, value);
				player.depositItemBank(item, false);
			}
		});
		player.getInventory().refreshItems();

		if(!bank) {
			player.sendMessage("@blu@Your rewards have been added to your bank.");
		}
		if (boxId == 11795) {
			TeleportHandler.teleportPlayer(player, new Position(2579, 2566),
					player.getSpellbook().getTeleportType());
		}
	}

}
