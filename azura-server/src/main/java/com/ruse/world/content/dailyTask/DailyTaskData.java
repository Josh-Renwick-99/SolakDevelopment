package com.ruse.world.content.dailyTask;

import java.util.HashMap;
import java.util.Map;

import com.ruse.model.Position;

/**
 * @author Adam_#6723 Enum which holds data for Daily Task System.
 */

public enum DailyTaskData {

	KILL_COWS(0, -26165, 6199, "Ghoul Gobbler", "[EASY] Kill 200 Ghouls before the next day!",
			"Ghouls can\\nbe found at\\nTeleports", 200, new Position(3420, 3510, 0), DailyTaskDifficulty.EASY, 1218,
			"Ghoul", 15000),

	KILL_CHAOS(1, -26164, 6199, "Chaos Crash", "[EASY] Kill 200 Chaos Druids before the next day!",
			"Chaos Druids\\ncan be found\\nat Teleports", 200, new Position(2931, 9846, 0), DailyTaskDifficulty.EASY,
			181, "Chaos druid", 15000),

	KILL_CHAOS1(2, -26163, 6199, "Crab Smasher", "[EASY] Kill 200 Rock Crab before the next day!",
			"Rock Crabs\\ncan be found\\nat Teleports", 200, new Position(2524, 2527, 0), DailyTaskDifficulty.EASY,
			1267, "Rock Crab", 15000),

	MONKEY_GUARD(3, -26162, 6199, "Monkey Craze", "[EASY] Kill 200 Monkey Guard before the next day!",
			"Monkey Guard\\ncan be found\\nat Teleports", 200, new Position(2925, 3248, 0), DailyTaskDifficulty.EASY,
			1459, "Monkey Guard", 15000),

	DEVILS(4, -26161, 6199, "Devilspawns", "[EASY] Kill 200 Devilspawn before the next day!",
			"Devilspawn\\ncan be found\\nat Teleports", 200, new Position(2977, 9521, 1), DailyTaskDifficulty.EASY,
			1614, "Devilspawn", 15000),

	LORDS(5, -26160, 6199, "Elite Warriors", "[EASY] Kill 200 Inferior Elite Warrior before the next day!",
			"Inferior Elite Warrior\\ncan be found\\nat Teleports", 200, new Position(3362, 9641, 0),
			DailyTaskDifficulty.EASY, 603, "Inferior Elite Warrior", 15000),

	DEMON(6, -26165, 19116, "Fractite Demons", "[EASY] Kill 250 Fractite Demon before the next day!",
			"Fractite Demon\\ncan be found\\nat Teleports", 250, new Position(3362, 9641, 0),
			DailyTaskDifficulty.MEDIUM, 12843, "Fractite Demon", 15000),

	BEASTS(7, -26164, 19116, "MageBeast", "[MEDUIM] Kill 250 MageBeast before the next day!",
			"MageBeast can\\nbe found at\\nTeleports", 250, new Position(1701, 5600, 0), DailyTaskDifficulty.MEDIUM,
			8018, "MageBeast", 15000),

	GLOWY_DRAGONS(8, -26163, 19116, "Joyx Dragon", "[MEDUIM] Kill 250 Joyx dragon before the next day!",
			"Joyx dragon can\\nbe found at\\nTeleports", 250, new Position(3420, 3510, 0), DailyTaskDifficulty.MEDIUM,
			53, "Joyx dragon", 15000),

	KILL_COWS_MED(9, -26162, 19116, "Predator", "[MEDUIM] Kill 250 Predator before the next day!",
			"Predator can\\nbe found at\\nTeleports", 250, new Position(2901, 3615, 0), DailyTaskDifficulty.MEDIUM,
			1218, "Predator", 15000),

	KILL_CHAOS_MED(10, -26161, 19116, "Chaos Crash", "[MEDUIM] Kill 250 Chaos Druids before the next day!",
			"Chaos Druids\\ncan be found\\nat Teleports", 250, new Position(2931, 9846, 0), DailyTaskDifficulty.MEDIUM,
			181, "Chaos druid", 15000),

	KILL_CHAOS1_MED(11, -26160, 19116, "Crab Smasher", "[MEDUIM] Kill 250 Rock Crab before the next day!",
			"Rock Crabs\\ncan be found\\nat Teleports", 250, new Position(2524, 2527, 0), DailyTaskDifficulty.MEDIUM,
			1267, "Rock Crab", 15000),

	MONKEY_GUARD_MED(12, -26159, 19116, "Monkey Craze", "[MEDUIM] Kill 250 Monkey Guard before the next day!",
			"Monkey Guard\\ncan be found\\nat Teleports", 250, new Position(2925, 3248, 0), DailyTaskDifficulty.MEDIUM,
			1459, "Monkey Guard", 15000),

	REV_TARRAGON(13, -26165, 19114, "Revenant Tarragon", "[HARD] Kill 375 Revenant Tarragon before the next day!",
			"Monkey Guard\\ncan be found\\nat Teleports", 375, new Position(3658, 3495, 0), DailyTaskDifficulty.HARD,
			6692, "Revenant Tarragon", 15000),

	NEX(14, -26164, 19114, "Nex", "[HARD] Kill 125 Nex Guard before the next day!", "Nex\\ncan be found\\nat Teleports",
			125, new Position(3658, 3495, 0), DailyTaskDifficulty.HARD, 13447, "Nex", 15000),

	ETERNAL_DRAGON(15, -26163, 19114, "Eternal Dragon", "[HARD] Kill 125 Eternal dragon before the next day!",
			"Eternal dragon\\ncan be found\\nat Teleports", 125, new Position(2079, 3229, 0), DailyTaskDifficulty.HARD,
			4972, "Eternal dragon", 15000),

	KINGS(16, -26162, 19114, "Troll King", "[HARD] Kill 200 Troll King before the next day!",
			"Troll King\\ncan be found\\nat Teleports", 200, new Position(1625, 5601, 0), DailyTaskDifficulty.HARD,
			13635, "Troll King", 15000),;

	DailyTaskData(int index, int clickingId, int Rewards, String TaskName, String TaskName2, String TaskInformation,
			int KillingRequirement, Position position, DailyTaskDifficulty difficulty, int npcId, String npcName,
			int depositAmount) {
		this.index = index;
		this.clickingId = clickingId;
		this.Rewards = Rewards;
		this.TaskName = TaskName;
		this.TaskName2 = TaskName2;
		this.TaskInformation = TaskInformation;
		this.KillingRequirement = KillingRequirement;
		this.position = position;
		this.difficulty = difficulty;
		this.npcId = npcId;
		this.npcName = npcName;
		this.depositAmount = depositAmount;
	}

	private int index;
	private String TaskName;
	private int Rewards;
	private int amount;
	private String TaskInformation;
	private int DisplayNpcId;
	private int KillingRequirement;
	private Position position;
	private DailyTaskDifficulty difficulty;
	private int clickingId, npcId, depositAmount;

	public int getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(int depositAmount) {
		this.depositAmount = depositAmount;
	}

	public int getNpcId() {
		return npcId;
	}

	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	public String getNpcName() {
		return npcName;
	}

	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}

	private String TaskName2, npcName;

	public String getTaskTimer() {
		return TaskName2;
	}

	public int getClickingId() {
		return clickingId;
	}

	public DailyTaskDifficulty getDifficulty() {
		return difficulty;
	}

	public int getKillingRequirement() {
		return KillingRequirement;
	}

	public int getDisplayNpcId() {
		return DisplayNpcId;
	}

	public String getTaskInformation() {
		return TaskInformation;
	}

	public int getTaskNumber() {
		return index;
	}

	public int getRewards() {
		return Rewards;
	}

	public String getTaskName() {
		return TaskName;
	}

	public int getAmount() {
		return amount;
	}

	public Position getPosition() {
		return position;
	}

	static final Map<Integer, DailyTaskData> byId = new HashMap<Integer, DailyTaskData>();

	private final static DailyTaskData data[] = DailyTaskData.values();

	static {
		for (DailyTaskData e : data) {
			if (byId.put(e.getTaskNumber(), e) != null) {
				throw new IllegalArgumentException("duplicate id: " + e.getTaskNumber());
			}
		}
	}

	public static DailyTaskData getById(int id) {
		if (byId.get(id) == null) {
			return byId.get(0);
		}
		return byId.get(id);
	}
}
