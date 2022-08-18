package com.ruse.world.content;

import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Doom {

	private static int[] Monsterswave1 = { 3112 };
	private static int[] Monsterswave2 = { 667, 1613, 1614, 934, 998, 2919 };

	public static void spawnWave1(Player player) {
		for (int i = 0; i < Monsterswave1.length; i++) {
			Position DoomSpawn = new Position(2585 + Misc.getRandom(3), 2609 + Misc.getRandom(3));
			NPC monster = new NPC(Monsterswave1[i], DoomSpawn);
			World.register(monster);
			monster.getCombatBuilder().attack(player);
			// System.out.println("Spawned: " + Monsterswave1[i]);
		}
	//	World.sendMessage("Registered");
	}

}
