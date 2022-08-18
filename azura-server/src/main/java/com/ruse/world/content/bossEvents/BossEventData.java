package com.ruse.world.content.bossEvents;

import java.util.HashMap;
import java.util.Map;

import com.ruse.model.Position;
import com.ruse.GameSettings;

/**
 * 
 * @author Adam_#6723
 *
 */

public enum BossEventData {
	FROST_DRAGONS(0, 51, "Frost dragon", 25, 19624, 25, "Frost dragon", 699, 20000, "All", 35,
			new Position(2957, 3887, 0)),
	TORMENTED_DEMONS(1, 8349, "Tormented Demon", 15, 19624, 15, "Tormented Demon", 699, 20000, "All", 35,
			new Position(2717, 9805, 0)),
	EXPERIMENTS(2, 1677, "Experiment", 40, 19624, 40, "Experiment", 126, 12000, "All", 26, new Position(2097, 4427, 0)),
	MONEY_GUARDS(3, 1459, "Monkey Guard", 35, 19624, 35, "Monkey Guard", 126, 12000, "All", 26,
			new Position(2925, 3248, 0)),
	JUNGLEWORM(4, 9467, "Jungle strykewyrm", 32, 19624, 32, "Jungle strykewyrm", 126, 12000, "All", 26,
			new Position(2731, 5095, 0)),
	DESERTWORM(5, 9465, "Desert strykewyrm", 30, 19624, 30, "Desert strykewyrm", 126, 12000, "All", 26,
			new Position(2731, 5095, 0)),
	ICESTRKYWORM(6, 9463, "Ice strykewyrm", 25, 19624, 25, "Ice strykewyrm", 126, 12000, "All", 26,
			new Position(2735, 5081, 0)),
	DARK_BEAST(7, 2783, "Dark beast", 25, 19624, 25, "Dark beast", 126, 12000, "All", 26, new Position(3176, 5470, 0)),
	EVENT_BOX(8, 8011, "Event box", 20, 19624, 20, "Event Box", 699, 20000, "All", 35, new Position(3163, 3808, 0)),
	REV_TARRAGON(9, 6692, "Revenant Tarragon", 20, 19624, 20, "Revenant Tarragon", 126, 12000, "All", 26,
			new Position(3658, 3495, 0)),;

	BossEventData(int taskNumber, int npcid, String text, int endamount, int rewards, int amount, String name,
			int combatLevel, int health, String weakness, int maxhit, Position position) {
		this.taskNumber = taskNumber;
		this.npcid = npcid;
		this.text = text;
		this.endamount = endamount;
		this.rewards = rewards;
		this.amount = amount;
		this.name = name;
		this.combatLevel = combatLevel;
		this.health = health;
		this.weakness = weakness;
		this.maxhit = maxhit;
		this.position = position;
	}

	private Position position;

	public Position getPosition() {
		return position;
	}

	private int npcid, endamount, taskNumber, combatLevel, health, maxhit, x, y, z;
	private int rewards, amount;

	private String text, name, weakness;

	public int getMaxhit() {
		return maxhit;
	}

	public int getAmount() {
		return amount;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public void setCombatLevel(int combatLevel) {
		this.combatLevel = combatLevel;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public String getWeakness() {
		return weakness;
	}

	public void setWeakness(String weakness) {
		this.weakness = weakness;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public int getEndamount() {
		return endamount;
	}

	public void setEndamount(int endamount) {
		this.endamount = endamount;
	}

	public String getName() {
		return name;
	}

	public int getNpcid() {
		return npcid;
	}

	public String getText() {
		return text;
	}

	public int getRewards() {
		return rewards;
	}

	static final Map<Integer, BossEventData> byId = new HashMap<Integer, BossEventData>();

	private final static BossEventData data[] = BossEventData.values();

	static {
		for (BossEventData e : data) {
			if (byId.put(e.getTaskNumber(), e) != null) {
				throw new IllegalArgumentException("duplicate id: " + e.getTaskNumber());
			}
		}
	}

	public static BossEventData getById(int id) {
		if (byId.get(id) == null) {
			return byId.get(0);
		}
		return byId.get(id);
	}
}
