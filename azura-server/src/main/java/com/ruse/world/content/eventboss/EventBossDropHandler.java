package com.ruse.world.content.eventboss;

import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.List;

public class EventBossDropHandler {

	public static void death(Player killer, NPC npc) {
		List<Item> rewards = npc.getEventBossRewards();
		for (Item reward : rewards) {
			Position random = new Position(npc.getPosition().getX() + Misc.random(9), npc.getPosition().getY() + Misc.random(9), npc.getPosition().getZ());
			GroundItemManager.add(new GroundItem(reward, random, npc.getDefinition().getName(), true, 110, true, 100, true), true);
		}
		resetEventBoss(npc);
    }
	
	private static void resetEventBoss(NPC npc) {
		npc.setEventBoss(false);
		npc.setEventBossRewards(null);
		World.sendMessage("<shad=1>@yel@<img=18>[Event Boss]@red@ The Event Boss has been slayed at ::Eventboss!");
	}

	 
}
