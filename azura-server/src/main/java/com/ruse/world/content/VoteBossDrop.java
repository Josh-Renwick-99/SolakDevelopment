package com.ruse.world.content;

import com.ruse.model.Position;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.motivote3.doMotivote;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatBuilder.CombatDamageCache;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.*;
import java.util.Map.Entry;

public class VoteBossDrop {

	public static NPC currentSpawn;


	public static void handleSpawn() {
		if (currentSpawn != null){
			System.out.println("Already spawned.");
			return;
		}
		
		doMotivote.setVoteCount(doMotivote.getVoteCount() - 50);
		currentSpawn = new NPC(8013, new Position(2980, 2778, 0));

		World.register(currentSpawn);
		World.sendMessage(
				"<img=28><shad=f9f6f6>Vote boss has spawned at ::voteboss kill it now for amazing rewards!<shad=-1>");
	}

	public static void handleForcedSpawn() {
		if (currentSpawn != null){
			System.out.println("Already spawned.");
			return;
		}

		//doMotivote.setVoteCount(doMotivote.getVoteCount() - 50);
		currentSpawn = new NPC(8013, new Position(2980, 2778, 0));

		World.register(currentSpawn);
		World.sendMessage(
				"<img=28><shad=f9f6f6>Vote boss has spawned at ::voteboss kill it now for amazing rewards!<shad=-1>");
	}
	
	public static void handleDrop(NPC npc) {
		if (npc.getCombatBuilder().getDamageMap().size() == 0) {
			return;
		}
		Map<Player, Integer> killers = new HashMap<>();

		for (Entry<Player, CombatDamageCache> entry : npc.getCombatBuilder().getDamageMap().entrySet()) {
			if (entry == null) {
				continue;
			}

			long timeout = entry.getValue().getStopwatch().elapsed();
			if (timeout > CombatFactory.DAMAGE_CACHE_TIMEOUT) {
				continue;
			}

			Player player = entry.getKey();
			if (player.getConstitution() <= 0 || !player.isRegistered()) {
				continue;
			}

			killers.put(player, entry.getValue().getDamage());
		}

		npc.getCombatBuilder().getDamageMap().clear();

		List<Entry<Player, Integer>> result = sortEntries(killers);
		for (Iterator<Entry<Player, Integer>> iterator = result.iterator(); iterator.hasNext(); ) {
			Entry<Player, Integer> entry = iterator.next();
			Player killer = entry.getKey();
			NPCDrops.handleDrops(killer, npc);
			iterator.remove();
		}
		currentSpawn = null;
	}


	/**
	 * 
	 * @param map
	 * @return
	 */
	static <K, V extends Comparable<? super V>> List<Entry<K, V>> sortEntries(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

		return sortedEntries;

	}
}
