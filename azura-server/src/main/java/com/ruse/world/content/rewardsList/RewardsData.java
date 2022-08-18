package com.ruse.world.content.rewardsList;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Adam_#6723 Enum which holds attributes for the reward interface.
 *
 */

public enum RewardsData {

	MBOX(0, new int[] { 6739, 11728, 6585, 15259, 15332, 2579, 6920, 7630, 6922, 13879, 13883, 15241, 15243, 3321, 3391,
			3322, 3320, 3318, 18360, 18686, 13996, 13913, 13919, 18799, 18834, 18801, 18800, 5095, 19140, 19139, 19138,
			4411, 19887, 22078, 19123, 11617, 3909, 3318, 15501, 11133, 15126, 16043, 6500, 10828, 3751, 3753, 10589,
			10564, 6809, 4587, 1249, 3204, 1305, 1377, 1434, 6528, 7158, 4153, 6, 8, 10, 12, 4675, 6914, 6889 },
			"Mystery Box"),
	ASSASIN_BOX(1,
			new int[] { 1563, 1564, 1562, 12514, 12512, 12522, 12518, 1563, 1564, 1562, 12514, 12512, 12522, 12518,
					1563, 1564, 1562, 12514, 12512, 12522, 12518 },
			"Drop Rate Box"),
	BaphometBox(2,
			new int[] { 4882, 4894, 4900, 4888, 20460, 20456, 18747, 11852, 11854, 11856, 11846, 11848, 6199, 23020,
					18740, 23020, 4153, 1215, 4151, 18684, 18686, 3140, 15332 },
			"PVM Box"),
	DonatorBox(3,
			new int[] { 11235, 17273, 14484, 18685, 11696, 11698, 11700, 13262, 15486, 19336, 18358, 19337, 19338,
					19339, 19340, 14009, 14010, 14008, 22034, 18799, 6570, 15018, 15019, 15020, 15220, 11730, 18349,
					18353, 13896, 18357, 13899, 10551, 3322, 4151, 2577 },
			"Vote Box"),
	DonkeyBox(4,
			new int[] { 11235, 17273, 14484, 18685, 3324, 11696, 11698, 11700, 13262, 15486, 19336, 19337, 19338, 19339,
					19340, 14009, 14010, 14008, 6769, 6570, 15018, 15019, 15020, 15220, 11730, 18349, 18353, 13896,
					18357, 13899, 10551, 3322, 4151, 2577 },
			"Super MBox"),
	GodzillaBOX(5,
			new int[] { 1055, 1053, 1507, 1050, 10284, 962, 1042, 1046, 1044, 1040, 1038, 11858, 11860, 11862, 19580,
					10935, 6769, 11235, 17273, 14484, 11696, 11698, 11700, 13262, 15486, 19336, 19337, 19338, 19339,
					19340, 14009, 14010, 14008, 6769, 6739, 13744, 13738, 13742, 13740, 6570, 19111, 11702, 13752,
					13746, 13750, 19135, 14484 },
			"Extreme MBox"),
	Infernal_Groudon(6,
			new int[] { 19886, 5023, 5497, 19812, 19843, 14769, 6199, 6199, 4178, 20534, 19116, 19115, 19114, 19119,
					6769, 22078, 4409, 19753, 18349, 15501, 1055, 1053, 1507, 1050, 10284, 962, 1042, 1046, 1044, 1040,
					1038, 11858, 11860, 11862, 19580, 10935, 6769 },
			"Grand MBox"),
	MYSTERY_BOX(7,
			new int[] { 18799, 18358, 12603, 12285, 20555, 20554, 19812, 13913, 13996, 11914, 18335, 3909, 19116,
					19115, 20555, 18686, 18684, 6769, 22078, 4409, 11283, 18349, 15501, 20110, 18797, 18795, 15830,
					18787, 18799, 18792, 18791, 18358, 18790, 18834, 18801, 15900, 18796, 18798, 18835, 15845, 15805,
					18800, 13949, 13937, 13934, 13999, 16133, 15920, 17017, 19887, 18685, 962, 6769 },
			"Raids Box"),;

	private int index;
	private String RewardName;
	private int item[];
	private int amount;

	public int getIndex() {
		return index;
	}

	public int[] getItemID() {
		return item;
	}

	public String getText() {
		return RewardName;
	}

	public int getAmount() {
		return amount;
	}

	RewardsData(int index, int[] item, String RewardName) {
		this.index = (index);
		this.item = (item);
		this.RewardName = (RewardName);
	}

	static final Map<Integer, RewardsData> byId = new HashMap<Integer, RewardsData>();

	static {
		for (RewardsData e : RewardsData.values()) {
			if (byId.put(e.getIndex(), e) != null) {
				throw new IllegalArgumentException("duplicate id: " + e.getIndex());
			}
		}
	}

	public static RewardsData getById(int id) {
		if (byId.get(id) == null) {
			return byId.get(0);
		}
		return byId.get(id);
	}

}