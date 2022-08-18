package com.ruse.world.content.globalBoss;

import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.util.Stopwatch;
import com.ruse.world.World;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class TheGeneral {

	private static final int TIME = 4000000; // 40 minutes? not sure lol

	private static Stopwatch timer = new Stopwatch().reset();

	public static int[] COMMONLOOT = { 15501, 19138, 19139, 19140, 18835, 18834, 18795, 18796, 18797, 18798, 18799, };
	public static int[] RARELOOT = { 14010, 14009, 14008, 6933, 6935, 6934, 6932, 6931, 6930, 8842, 9364 };
	public static int[] SUPERRARELOOT = { 3324, 19749, 8810, 19115, 10942, 20555, };
	public static int[] LEGENDARYLOOT = { 22083, 17678, 17656, 17652, 17690 };

	public static Position position = new Position(2334, 9820, 0);

	public static void sequence() {
		NPC npc = new NPC(7553, position);
		World.register(npc);
		World.sendMessage("<img=5>@red@The General has spawned in his cave. do ::general");
		World.sendMessage("<img=5>@red@The General is a global boss. Everyone can contribute!");
	}

	public static void giveLoot(Player player, NPC npc) {
		// int chance = Misc.getRandom(100);
		int common = COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)];
		int rare = RARELOOT[Misc.getRandom(RARELOOT.length - 1)];
		int superrare = SUPERRARELOOT[Misc.getRandom(SUPERRARELOOT.length - 1)];
		int legendaryrare = LEGENDARYLOOT[Misc.getRandom(LEGENDARYLOOT.length - 1)];

		if (Misc.exclusiveRandom(1, 1250) <= 2) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(legendaryrare),
							new Position(position.getX() + Misc.getRandom(7), position.getY() + Misc.getRandom(4), 0),
							"", true, 200, true, 200, true));
			String itemName = (new Item(legendaryrare).getDefinition().getName());
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			World.sendMessage("<img=5><col=FF0000>[OMG LEGENDARY!] The General dropped " + itemMessage + "!");
		}

		if (Misc.exclusiveRandom(1, 750) <= 3) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(superrare),
							new Position(position.getX() + Misc.getRandom(2), position.getY() + Misc.getRandom(3), 0),
							"", true, 200, true, 200, true));
			String itemName = (new Item(superrare).getDefinition().getName());
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			World.sendMessage("<img=5><col=FF0000>[SUPER RARE] The General dropped " + itemMessage + "!");
		}
		// 1 Super rare
		if (Misc.exclusiveRandom(1, 750) <= 3) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(superrare),
							new Position(position.getX() + Misc.getRandom(5), position.getY() + Misc.getRandom(3), 0),
							"", true, 200, true, 200, true));
			String itemName = (new Item(superrare).getDefinition().getName());
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			World.sendMessage("<img=5><col=FF0000>[SUPER RARE] The General dropped " + itemMessage + "!");
		}
		// 2 rares
		if (Misc.exclusiveRandom(1, 10) == 1) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(RARELOOT[Misc.getRandom(RARELOOT.length - 1)]),
							new Position(position.getX() + Misc.getRandom(9), position.getY() + Misc.getRandom(7), 0),
							"", true, 200, true, 200, true));
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(RARELOOT[Misc.getRandom(RARELOOT.length - 1)]),
							new Position(position.getX() + Misc.getRandom(4), position.getY() + Misc.getRandom(4), 0),
							"", true, 200, true, 200, true));
			String itemName = (new Item(rare).getDefinition().getName());
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			World.sendMessage("<img=5><col=FF0000>[RARE] The General dropped " + itemMessage + "!");
		}

		// Commons
		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() + Misc.getRandom(10), position.getY() + Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() - Misc.getRandom(8), position.getY() - Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() - Misc.getRandom(6), position.getY() - Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() + Misc.getRandom(12), position.getY() + Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() + Misc.getRandom(8), position.getY() + Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() - Misc.getRandom(6), position.getY() - Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() - Misc.getRandom(12), position.getY() - Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)]),
						new Position(position.getX() + Misc.getRandom(8), position.getY() + Misc.getRandom(5), 0), "",
						true, 200, true, 200, true));

	}

}
