package com.ruse.world.content.casketopening;

import com.ruse.util.Misc;

public class BoxLoot {
	public static Box getLoot(Box[] loot) {

		Box[] possibleDrops = new Box[loot.length];
		int possibleDropsCount = 0;
		for (Box drop : loot) {
			double rate = drop.getRate() * 100;
			if (Misc.getRandom(10000) <= rate) {
				possibleDrops[possibleDropsCount++] = drop;
			}
		}

		if (possibleDropsCount > 0) {
			return possibleDrops[Misc.getRandom((possibleDropsCount - 1))];
		} else {
			return loot[Misc.getRandom((possibleDropsCount - 1))];
		}
	}

}
