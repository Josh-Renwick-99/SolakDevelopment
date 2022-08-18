package com.ruse.world.content.scratchcards;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.RandomUtility;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

public class ScratchCard {

	private Player player;

	public boolean scratching = false;
	final int[] COMMON_ITEMS = new int[] {1050,6769,13736, 1053, 1057,1055 };
	final int[] RARE_ITEMS = new int[] { 1038,1040,1042,1044,1046,1048 };

	public ScratchCard(Player player) {
		this.player = player;
	}

	private int item1, item2, item3;

	public void scratch() {
		if (scratching) {
			player.sendMessage("@red@Please wait till current game is finished");
			return;
		}

		if (player.getInventory().contains(455)) {
			player.getInventory().delete(455, 1);
		} else {
			player.sendMessage("U don't have a scratchcard :/");
			return;
		}

		int random = RandomUtility.exclusiveRandom(10);
		if (random != 7) {
			item1 = COMMON_ITEMS[RandomUtility.inclusiveRandom(0, COMMON_ITEMS.length - 1)];
			item2 = COMMON_ITEMS[RandomUtility.inclusiveRandom(0, COMMON_ITEMS.length - 1)];
			item3 = COMMON_ITEMS[RandomUtility.inclusiveRandom(0, COMMON_ITEMS.length - 1)];
		} else {
			item1 = RARE_ITEMS[RandomUtility.inclusiveRandom(0, RARE_ITEMS.length - 1)];
			item2 = RARE_ITEMS[RandomUtility.inclusiveRandom(0, RARE_ITEMS.length - 1)];
			item3 = RARE_ITEMS[RandomUtility.inclusiveRandom(0, RARE_ITEMS.length - 1)];
		}

		player.getPacketSender().sendScratchcardItems(item1, item2, item3);
		scratching = true;
	}

	public void open() {
		player.getPacketSender().sendInterface(23630);
		for (int i = 0; i < COMMON_ITEMS.length; i++)
			player.getPacketSender().sendItemOnInterface(23642, COMMON_ITEMS[i], i, 1);
		for (int x = 0; x < RARE_ITEMS.length; x++)
			player.getPacketSender().sendItemOnInterface(23645, RARE_ITEMS[x], x, 1);
	}

	public void getWinnings() {
		int count = 0;

		if (item1 == item2 && item1 == item3) {
			// System.out.println("A TRIPLE!");
			count = 2;
		} else if (item1 == item2 || item1 == item3) {
			count = 1;
		} else if (item2 == item3) {
			count = 1;
		}

		if (count == 0) {
			player.sendMessage("None of the same ones so u don't win anything :(");
		} else if (count == 1) {
			player.sendMessage("Congrats, there were 2 of the same matches so you won a Free Scratch Card");
			player.getInventory().add(455,1);
		} else if (count == 2) {
			player.sendMessage("WINNER! there were 3 of the same matches!");
			player.getInventory().add(item1, 1);
			String name = ItemDefinition.forId(item1).getName();
			World.sendMessage("@blu@<img=5>[SCRATCHCARD]<img=5> @red@" + player.getUsername() + " has got an " + name
					+ " From scratchcards!");
		}

	}
}
