package com.ruse.world.content.globalBoss;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Position;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.world.World;
import com.ruse.world.content.achievement.Achievements;
import com.ruse.world.content.combat.CombatBuilder;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.dailytasks_new.DailyTask;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stan van der Bend on 16/12/2017. project: runeworld-game-server
 * package: runeworld.world.entity.combat.strategy.global
 */
public abstract class GlobalBossHandler extends NPC {

	private final static List<GlobalBoss> GLOBAL_BOSSES = new ArrayList<>();

	public static void init() {
		register(new ShadowLord());
	}

	public GlobalBossHandler(int id) {
		super(id, new Position(324, 324, 0));
	}

	static void register(GlobalBoss globalBoss) {
		GLOBAL_BOSSES.add(globalBoss);

		if (globalBoss.getRespawn()) {
			final long millisTillRespawn = TimeUnit.MINUTES.toMillis(globalBoss.minutesTillRespawn());
			final int cyclesTillRespawn = Math.toIntExact(millisTillRespawn / GameSettings.GAME_PROCESSING_CYCLE_RATE);

			// System.out.println(
				//	"A " + globalBoss.getDefinition().getName() + " will spawn in " + cyclesTillRespawn + " cycles.");

			TaskManager.submit(new Task(cyclesTillRespawn, false) {
				@Override
				protected void execute() {
					globalBoss.spawn();
					globalBoss.setRespawn(false);
					stop();
				}
			});
		}
	}

	static void deRegister(GlobalBoss globalBoss) {
		// System.out.println("Deregistered global " + globalBoss.getDefinition().getName());
		GLOBAL_BOSSES.remove(globalBoss);
		World.deregister(globalBoss);
	}

	public static void onDeath(GlobalBoss npc) {
		handleDrop(npc);
		deRegister(npc);
		register(npc.reincarnate());
	}

	private static void handleDrop(GlobalBoss npc) {
		final int damageMapSize = npc.getCombatBuilder().getDamageMap().size();

		if (npc.getCombatBuilder().getDamageMap().size() == 0)
			return;

		final Map<Player, Integer> killers = new HashMap<>();

		for (Map.Entry<Player, CombatBuilder.CombatDamageCache> entry : npc.getCombatBuilder().getDamageMap()
				.entrySet()) {

			if (entry == null)
				continue;

			final long timeout = entry.getValue().getStopwatch().elapsed();

			if (timeout > CombatFactory.DAMAGE_CACHE_TIMEOUT)
				continue;

			final Player player = entry.getKey();

			if (player.getConstitution() <= 0 || !player.isRegistered())
				continue;

			killers.put(player, entry.getValue().getDamage());
		}

		npc.getCombatBuilder().getDamageMap().clear();

		List<Map.Entry<Player, Integer>> result = sortEntries(killers);
		int count = 0;

		for (Map.Entry<Player, Integer> entry : result) {

			final Player killer = entry.getKey();

			Achievements.doProgress(killer, Achievements.Achievement.KILL_45_GLOBAL_BOSSES);
			DailyTask.GLOBAL_BOSSES.tryProgress(killer);

			NPCDrops.handleDrops(killer, npc);

			if (++count >= npc.maximumDrops())
				break;
		}
	}

	private static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortEntries(Map<K, V> map) {
		final List<Map.Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());
		sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
		return sortedEntries;
	}

	public static List<GlobalBoss> getBosses() {
		return GLOBAL_BOSSES;
	}
}
