package com.ruse.world.content.skeletalhorror;

import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatBuilder.CombatDamageCache;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

///import com.ruse.tools.discord.DiscordConstant;
//import com.ruse.tools.discord.DiscordManager;

public class SkeletalHorror extends NPC {

	private static long massMessageTimer = 0;

	public static int[] COMMONLOOT = { 15501, 19138, 19139, 19140, 18835, 18834, 18795, 18796, 18797, 18798, 18799,14010, 14009, 14008, 6933, 6935, 6934, 6932, 6931, 6930, 8842, 9364,
			22077,6927,6928,6929,19136,6930,6931,6932,6936,6933,6934,6935,19116,12634,17291,22079,6937,15418,3324,3905 };
	public static int[] RARELOOT = { 14010, 14009, 14008, 6933, 6935, 6934, 6932, 6931, 6930, 8842, 9364,
			22077,6927,6928,6929,19136,6930,6931,6932,6936,6933,6934,6935,19116,12634,17291,22079,6937,15418,3324,3905};
	public static int[] SUPERRARELOOT = { 3324, 19749, 8810, 19115, 10942, 20555,14176,14172,14174 };
	public static int[] LEGENDARYLOOT = { 22083, 17678, 17656, 17652, 17690, 3324, 19749, 8810, 19115, 10942, 20555,14176,14172,14174  };
	/**
	 *
	 */
	public static boolean wyrmAlive = false;
	public static int rng = 0,
			// testINTERVAL = 1*1000,
			INTERVAL = 30 * 60 * 3000, NPC_ID = 3830;
	/**
	 *
	 *
	 *
	 */

	public static final SkeletalHorrorLocation[] LOCATIONS = {//2466, 10156
			new SkeletalHorrorLocation(2464, 10146, 0, "::Prime", "::Prime"),
			new SkeletalHorrorLocation(2464, 10146, 0, "::Prime", "::Prime"),
			new SkeletalHorrorLocation(2464, 10146, 0, "::Prime", "::Prime"),
			new SkeletalHorrorLocation(2464, 10146, 0, "::Prime", "::Prime") };

	/**
	 *
	 */
	private static SkeletalHorror current;
	public static String playerPanelFrame;

	/**
	 *
	 * @param position
	 */
	public SkeletalHorror(Position position) {

		super(NPC_ID, position);

		// setConstitution(96500/3); //7,650
		setDefaultConstitution(16500000);

	}

	public static String getTimeLeft() {
		long ms = (INTERVAL - (System.currentTimeMillis() - massMessageTimer));
		return String.format("%d hrs, %d mins, %d secs", TimeUnit.MILLISECONDS.toHours(ms),
				TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
				TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
	}
	/**
	 *
	 */
	public static void initialize() {

		if (massMessageTimer == 0 || (System.currentTimeMillis() - massMessageTimer >= INTERVAL)) {
			// // System.out.println("massMessageTimer = "+massMessageTimer);
			// // System.out.println("currentTimeMs = "+System.currentTimeMillis());
			// // System.out.println("spawn wyrm called");
			massMessageTimer = System.currentTimeMillis();
			spawn();

		}

	}

	public static void sendHint(Player player) {
		// player.getPacketSender().sendMessage("<col=1e56ad><img=5> [Skeletal
		// Horror]@bla@ The Skeletal Horror is roaming
		// "+LOCATIONS[rng].getLocation()+"!");
	}

	public static String getPlayerPanelHint() {
		return LOCATIONS[rng].getPlayerPanelHint();
	}

	/**
	 *
	 */
	
	public static void spawncommand() {
		rng = Misc.randomMinusOne(LOCATIONS.length);
		SkeletalHorrorLocation location = LOCATIONS[rng];
		SkeletalHorror instance = new SkeletalHorror(location.copy());
		World.register(instance);
		setCurrent(instance);
			World.sendMessage("<col=1e56ad><img=5> [Optimus Prime Event]@bla@ The Prime is roaming "
					+ LOCATIONS[rng].getLocation() + "!");
			return;
		}
	public static void spawn() {
		if (wyrmAlive == true) { // checks if its already alive to avoid duplicates
			// // System.out.println("spawn failed - wyrm is already alive");
			World.sendMessage("<col=1e56ad><img=5> [Optimus Prime]@bla@ The Prime is roaming "
					+ LOCATIONS[rng].getLocation() + "!");
			return;
		}
		rng = Misc.randomMinusOne(LOCATIONS.length);
		SkeletalHorrorLocation location = LOCATIONS[rng];
		SkeletalHorror instance = new SkeletalHorror(location.copy());

		World.register(instance);
		setCurrent(instance);
		wyrmAlive = true;
		World.sendMessage("<col=1e56ad><img=5> [Transformers]@bla@ Optimus Prime has appeared "
				+ location.getLocation() + "!");

	}

	/**
	 * 
	 * @param npc
	 */
	public static void handleDrop(NPC npc) {

		setCurrent(null);

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
		int count = 0;

		for (Entry<Player, Integer> entry : result) {

			Player killer = entry.getKey();
			int damage = entry.getValue();

			handleDrop(npc, killer, damage);

			if (++count >= 25) {
				break;
			}

		}

	}

	public static void giveLoot(Player player, NPC npc, Position pos) {
		int chance = Misc.exclusiveRandom(1, 1000);
		int common = COMMONLOOT[Misc.getRandom(COMMONLOOT.length - 1)];
		int rare = RARELOOT[Misc.getRandom(RARELOOT.length - 1)];
		int superrare = SUPERRARELOOT[Misc.getRandom(SUPERRARELOOT.length - 1)];
		int legendaryrare = LEGENDARYLOOT[Misc.getRandom(LEGENDARYLOOT.length - 1)];

		GroundItemManager.spawnGroundItem(player,
				new GroundItem(new Item(5022, 1000), pos, player.getUsername(), false, 150, true, 200));		// legendaryrare
		if (chance <= 2) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(legendaryrare), pos, player.getUsername(), false, 150, true, 200));
			String itemName = (new Item(legendaryrare).getDefinition().getName());
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			World.sendMessage("<img=5><col=FF0000>[LEGENDARY]" + player.getUsername() + " received " + itemMessage
					+ " from Optimus prime!");
			return;
		}
		// superrare
		if (chance <= 5) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(superrare), pos, player.getUsername(), false, 150, true, 200));
			String itemName = (new Item(superrare).getDefinition().getName());
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			World.sendMessage("<img=5><col=FF0000>[Super Rare]" + player.getUsername() + " received " + itemMessage
					+ " from Optimus prime!");
			return;
		}
		// rare
		if (chance <= 10) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(rare), pos, player.getUsername(), false, 150, true, 200));
			String itemName = (new Item(rare).getDefinition().getName());
			String itemMessage = Misc.anOrA(itemName) + " " + itemName;
			World.sendMessage("<img=5><col=FF0000>[Rare]" + player.getUsername() + " received " + itemMessage
					+ " from Optimus prime!");
			return;
		}
		// common
		if (chance >= 0) {
			GroundItemManager.spawnGroundItem(player,
					new GroundItem(new Item(common), pos, player.getUsername(), false, 150, true, 200));
			String itemName = (new Item(common).getDefinition().getName());
			World.sendMessage("<img=5><col=FF0000>" + player.getUsername() + " received " + itemName
					+ " from Optimus prime!");
			return;
		}

	}

	/**
	 * 
	 * @param npc
	 * @param player
	 * @param damage
	 */
	private static void handleDrop(NPC npc, Player player, int damage) {
		Position pos = npc.getPosition();
		giveLoot(player, npc, pos);
	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	static <K, V extends Comparable<? super V>> List<Entry<K, V>> sortEntries(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {

			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}

		});

		return sortedEntries;

	}

	/**
	 *
	 * @return
	 */
	public static SkeletalHorror getCurrent() {
		return current;
	}

	/**
	 *
	 * @param current
	 */
	public static void setCurrent(SkeletalHorror current) {
		SkeletalHorror.current = current;
	}

	/**
	 *
	 * @author Nick Hartskeerl <apachenick@hotmail.com>
	 *
	 */
	public static class SkeletalHorrorLocation extends Position {

		/**
		 *
		 */
		private String location, hint;

		/**
		 *
		 * @param x
		 * @param y
		 * @param z
		 * @param location
		 */
		public SkeletalHorrorLocation(int x, int y, int z, String location, String hint) {
			super(x, y, z);
			setLocation(location);
			setHint(hint);
		}

		/**
		 *
		 * @return
		 */

		String getLocation() {
			return location;
		}

		String getPlayerPanelHint() {
			return hint;
		}

		/**
		 *
		 * @param location
		 */
		public void setLocation(String location) {
			this.location = location;
		}

		public void setHint(String hint) {
			this.hint = hint;
		}

	}

}