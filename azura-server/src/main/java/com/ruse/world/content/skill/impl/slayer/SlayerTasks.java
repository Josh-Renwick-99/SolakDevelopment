package com.ruse.world.content.skill.impl.slayer;

import com.ruse.model.Position;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;

import java.util.*;

/**
 * @author Gabriel Hannason
 */

public enum SlayerTasks {

	NO_TASK(null, -1, null, -1, null),


	//boss slayer tasks ///////////
	// Old bosses
	/*FRIEZA(SlayerMaster.BOSS_SLAYER, 252, "Frieza can be found by using the Teleport interface", 500,
			new Position(2516, 3042, 0)),
	PERFECT_CELL(SlayerMaster.BOSS_SLAYER, 449, "Perfect cell can be found by using the Teleport interface.", 500,
			new Position(3000, 2511, 0)),
	SUPER_BUU(SlayerMaster.BOSS_SLAYER, 452, "Super Buu can be found by using the Teleport interface.", 500,
			new Position(3342, 3022, 0)),
	HERBAL_ROGUE(SlayerMaster.BOSS_SLAYER, 2342, "Herbal Rogue can be found by using the Teleport interface.", 500,
			new Position(3044, 2969, 0)),
	AZURE_BEAST(SlayerMaster.BOSS_SLAYER, 3831, "Azure Beast can be found by using the Teleport interface.", 500,
			new Position(2924, 2842, 0)),
	JOKER(SlayerMaster.BOSS_SLAYER, 185, "Joker can be found by using the Teleport interface.", 500,
			new Position(1807, 3211, 0)),

	SUPREME_BOSS(SlayerMaster.BOSS_SLAYER, 440, "Supreme Boss can be found by using the Teleport interface.", 600,
			new Position(2403, 5082, 1000)),
	VASA_NISTIRIO(SlayerMaster.BOSS_SLAYER, 1120, "Vasa Nistirio can be found by using the Teleport interface.", 600,
			new Position(2910, 2593, 0)),*/

	// Boss slayer tasks 375-750k * 2 = 750-1500k / task
	// Easy bosses
	ELITE_DRAGON(SlayerMaster.BOSS_SLAYER, 8015, "Elite Dragon can be found by using the Teleport interface.", 375_000,
			new Position(2911, 3991, 0)),
	ETERNAL_DRAGON(SlayerMaster.BOSS_SLAYER, 4972, "Eternal Dragon can be found by using the Teleport interface.", 400_000,
			new Position(2975, 4000, 0)),
	SCARLET_FALCON(SlayerMaster.BOSS_SLAYER, 2949, "Scarlet Falcon can be found by using the Teleport interface.", 375_000,
			new Position(3036, 4003, 0)),
	//Med bosses
	CRYSTAL_QUEEN(SlayerMaster.BOSS_SLAYER, 6430, "Crystal Queen can be found by using the Teleport interface.", 500_000,
			new Position(1887, 5468, 0)),
	LUCIFER(SlayerMaster.BOSS_SLAYER, 9012, "Lucifer can be found by using the Teleport interface.", 666_666,
			new Position(2335, 3998, 0)),
	MEGA_AVATAR(SlayerMaster.BOSS_SLAYER, 4540, "Mega Avatar can be found by using the Teleport interface.", 550_000,
			new Position(1884, 5334, 0)),

	// Hard bosses
	CRAZY_WITCH(SlayerMaster.BOSS_SLAYER, 1234, "Crazy witches can be found by using the Teleport interface.", 700_000,
			new Position(2784, 4445, 0)),
	DARK_SUPREME(SlayerMaster.BOSS_SLAYER, 438, "Dark supremes can be found by using the Teleport interface.", 750_000,
			new Position(2721, 4450, 0)),
	LIGHT_SUPREME(SlayerMaster.BOSS_SLAYER, 440, "Light supremes can be found by using the Teleport interface.", 650_000,
			new Position(2761, 4575, 0)),
	FRACTITE_DEMON(SlayerMaster.BOSS_SLAYER, 12843, "Fractite demons can be found by using the Teleport interface.", 650_000,
			new Position(2785, 4525, 0)),
	INFERNAL_DEMON(SlayerMaster.BOSS_SLAYER, 12810, "Infernal demons can be found by using the Teleport interface.", 750_000,
			new Position(2712, 4508, 0)),


	// Easy slayer tasks 12.5k-50k * 2 = 25-100k / task
	GRANITE_CRAB(SlayerMaster.EASY_SLAYER, 1700, "Easy Slayer Dungeon", 12500, new Position(1866, 4865)),

	FLAMING_BUTTERFLY(SlayerMaster.EASY_SLAYER, 1711, "Easy Slayer Dungeon", 22000, new Position(1882, 4823)),
	ANT_WORKER(SlayerMaster.EASY_SLAYER, 1724, "Easy Slayer Dungeon 2", 17500, new Position(2001, 4779)),

	LAVANNOTH(SlayerMaster.EASY_SLAYER, 1702, "Easy Slayer Dungeon", 27500, new Position(1828, 4864)),
	DARK_BLOODVELD(SlayerMaster.EASY_SLAYER, 1710, "Easy Slayer Dungeon", 42500, new Position(1810, 4832)),
	BLAST_CLOUD(SlayerMaster.EASY_SLAYER, 1739, "Easy Slayer Dungeon", 33000, new Position(1845, 4820)),

	RUSTED_GARGOYLE(SlayerMaster.EASY_SLAYER, 1712, "Easy Slayer Dungeon", 30000, new Position(1903, 4850)),
	BUNYIP(SlayerMaster.EASY_SLAYER, 1705, "Easy Slayer Dungeon", 25000, new Position(1901, 4878)),

	JELLO(SlayerMaster.EASY_SLAYER, 1729, "Easy Slayer Dungeon", 15000, new Position(1878, 4912)),
	JELLYFISH(SlayerMaster.EASY_SLAYER, 1721, "Easy Slayer Dungeon", 18000, new Position(1847, 4899)),
	VAMPYRE_HAND(SlayerMaster.EASY_SLAYER, 1703, "Easy Slayer Dungeon", 15000, new Position(1813, 4908)),


	MOSQUITO(SlayerMaster.EASY_SLAYER, 1713, "Easy Slayer Dungeon 2", 20000, new Position(2032, 4778)),
	WAR_PLANT(SlayerMaster.EASY_SLAYER, 1737, "Easy Slayer Dungeon 2", 30000, new Position(2032, 4751)),
	TYCOONS_BIRD(SlayerMaster.EASY_SLAYER, 1730, "Easy Slayer Dungeon 2", 50000, new Position(1998, 4751)),

	// Med slayer tasks 50k-125k * 2 = 100-250k / task
	ZAMORAK_BIRD(SlayerMaster.MEDIUM_SLAYER, 1725, "Medium Slayer Dungeon", 70000, new Position(2215, 4962)),
	PELICANBIRD(SlayerMaster.MEDIUM_SLAYER, 1709, "Medium Slayer Dungeon", 60000, new Position(2149, 4950)),
	SABERTOOTH(SlayerMaster.MEDIUM_SLAYER, 1731, "Medium Slayer Dungeon", 75000, new Position(2129, 4978)),

	GROOTER(SlayerMaster.MEDIUM_SLAYER, 1744, "Medium Slayer Dungeon", 50000, new Position(2164, 4946)),
	SYMBIOTE(SlayerMaster.MEDIUM_SLAYER, 1727, "Medium Slayer Dungeon", 85000, new Position(2221, 4979)),
	GHOULORD(SlayerMaster.MEDIUM_SLAYER, 1708, "Medium Slayer Dungeon", 100_000, new Position(2196, 4976)),

	NATURE_UNICORN(SlayerMaster.MEDIUM_SLAYER, 1742, "Medium Slayer Dungeon", 80000, new Position(2212, 4940)),
	ELEMENTAL_MOSS(SlayerMaster.MEDIUM_SLAYER, 1740, "Medium Slayer Dungeon", 110_000, new Position(2164, 4946)),
	ELEMENTAL_FIRE(SlayerMaster.MEDIUM_SLAYER, 1741, "Medium Slayer Dungeon", 115_000, new Position(2161, 4971)),

	RUNITE_TURTLE(SlayerMaster.MEDIUM_SLAYER, 1745, "Medium Slayer Dungeon", 90000, new Position(2129, 4978)),
	ARMORED_MINOTAUR(SlayerMaster.MEDIUM_SLAYER, 1719, "Medium Slayer Dungeon", 125_000, new Position(2126, 4952)),
	BABY_BRONZE_DRAGON(SlayerMaster.MEDIUM_SLAYER, 1706, "Medium Slayer Dungeon", 65000, new Position(2190, 4943)),


	// Hard slayer tasks 125-250k * 2 = 250-500k / task
	NATIVE_DEMON(SlayerMaster.HARD_SLAYER, 1715, "Hard Slayer Dungeon", 150_000, new Position(1632, 4844)),
	WILD_GRAAHK(SlayerMaster.HARD_SLAYER, 1734, "Hard Slayer Dungeon", 125_000, new Position(1667, 4845)),
	LEOPARD(SlayerMaster.HARD_SLAYER, 1733, "Hard Slayer Dungeon", 175_000, new Position(1688, 4841)),
	SEA_CREATURE(SlayerMaster.HARD_SLAYER, 1735, "Hard Slayer Dungeon", 180_000, new Position(1704, 4839)),
	KREE_DEVIL(SlayerMaster.HARD_SLAYER, 1736, "Hard Slayer Dungeon", 200_000, new Position(1708, 4816)),
	HYNDRA(SlayerMaster.HARD_SLAYER, 1743, "Hard Slayer Dungeon", 225_000, new Position(1670, 4815)),
	CHINESE_DRAGON(SlayerMaster.HARD_SLAYER, 1716, "Hard Slayer Dungeon", 250_000, new Position(1641, 4820)),
	EVIL_CHINCHOMPA(SlayerMaster.HARD_SLAYER, 1723, "Hard Slayer Dungeon", 200_000, new Position(1617, 4823)),
	;


	/*
	 * @param taskMaster
	 * 
	 * @param npcId
	 * 
	 * @param npcLocation
	 * 
	 * @param XP
	 * 
	 * @param taskPosition
	 */

	// 1 last thing left the dILY REWARD huh
	SlayerTasks(SlayerMaster taskMaster, int npcId, String npcLocation, int XP, Position taskPosition) {
		this.taskMaster = taskMaster;
		this.npcId = npcId;
		this.npcLocation = npcLocation;
		this.XP = XP;
		this.taskPosition = taskPosition;
	}

	public static Map<SlayerMaster, ArrayList<SlayerTasks>> tasks = new HashMap<>();
	static {


		for (SlayerMaster master : SlayerMaster.values()) {
			ArrayList<SlayerTasks> slayerMasterSet = new ArrayList<>();
			for(SlayerTasks t : SlayerTasks.values()) {
				if(t.taskMaster == master) {
					slayerMasterSet.add(t);
				}
			}
			tasks.put(master, slayerMasterSet);
		}
	}
	private SlayerMaster taskMaster;
	private int npcId;
	private String npcLocation;
	private int XP;
	private Position taskPosition;

	public SlayerMaster getTaskMaster() {
		return this.taskMaster;
	}

	public int getNpcId() {
		return this.npcId;
	}

	public String getNpcLocation() {
		return this.npcLocation;
	}

	public int getXP() {
		return this.XP;
	}

	public Position getTaskPosition() {
		return this.taskPosition;
	}


	public static SlayerTaskData getNewTaskData(SlayerMaster master) {
		int slayerTaskAmount = 20;
		ArrayList<SlayerTasks> possibleTasks = tasks.get(master);
		SlayerTasks task = possibleTasks.get(Misc.getRandom(possibleTasks.size() - 1));

		/*
		 * Getting a task
		 */
		if (master == SlayerMaster.BOSS_SLAYER) {
			slayerTaskAmount = 25 + Misc.getRandom(25);
		} else if (master == SlayerMaster.EASY_SLAYER) {
			slayerTaskAmount = 25 + Misc.random(25);
		} else if (master == SlayerMaster.MEDIUM_SLAYER) {
			slayerTaskAmount = 50 + Misc.random(25);
		} else if (master == SlayerMaster.HARD_SLAYER) {
			slayerTaskAmount = 60 + Misc.random(40);
		}
		return new SlayerTaskData(task, slayerTaskAmount);
	}//

	public String getName() {
		NpcDefinition def = NpcDefinition.forId(npcId);
		return def == null ? Misc.formatText(this.toString().toLowerCase().replaceAll("_", " ")) : def.getName();
	}
}
