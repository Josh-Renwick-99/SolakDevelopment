package com.ruse.world.content.instanceMananger;

import com.ruse.model.Position;
import com.ruse.model.RegionInstance.RegionInstanceType;

/**
 * 
 * @author Adam_#6723
 *
 */

public enum InstanceData {

	ROCK_CRABS(-30465, 1265, "Solak Lions", 35, new int[] { 19624 }, "Solak Lions",
			new Position(2758, 4743), RegionInstanceType.CORPOREAL_BEAST),
	CHARMANDER(-30464, 1023, "Dark Bloodveld", 35, new int[] { 19624 }, "Dark Bloodveld",
			new Position(2768, 4744), RegionInstanceType.KRIL_TSUTSAROTH),
	BULBASAUR(-30463, 1233, "Rusted Gargoyle", 35, new int[] { 19624 }, "Rusted Gargoyle",
			new Position(2778, 4744), RegionInstanceType.GENERAL_GRAARDOR),
	SQUIRTLE(-30462, 1234, "Crazy Witch", 35, new int[] { 19624 }, "Crazy Witch",
			new Position(2788, 4744), RegionInstanceType.COMMANDER_ZILYANA),
	THE_MUMMY(-30461, 13747, "Ice Demon", 35, new int[] { 19624 }, "Ice Demon",new Position(2798, 4744),
			RegionInstanceType.KREE_ARRA),
	PREDATOR(-30460, 12343, "Predator", 35, new int[] { 19624 }, "Predator", new Position(2808, 4744),
			RegionInstanceType.ETERNAL),
	CYANTRIX(-30459, 12886, "Cyantrix", 35, new int[] { 19624 }, "Cyantrix",
			new Position(2808, 4760), RegionInstanceType.TORMENTED_DEMON),
	BULWARK(-30458, 10103, "Bulwark", 35, new int[] { 19624 }, "Bulwark",
			new Position(2797, 4759), RegionInstanceType.KING_BLACK_DRAGON),
	SHADOW_WARRIOR(-30457, 606, "Shadow Warrior", 35, new int[] { 19624 }, "Shadow Warrior",
			new Position(2786, 4759),RegionInstanceType.CHAOS_ELEMENTAL),
	COL_WARRIOR(-30456, 607, "Col Warrior", 35, new int[] { 19624 }, "Col Warrior", new Position(2774, 4759),
			RegionInstanceType.SLASH_BASH),
	LORD(-30455, 603, "Lord", 35, new int[] { 19624 }, "Lord", new Position(2762, 4759),
			RegionInstanceType.BANDOS_AVATAR),
	FRACTITE_DEMON(-30454, 12843, "Fractite Demon", 35, new int[] { 19624 }, "Fractite Demon", new Position(2762, 4777),
			RegionInstanceType.DAG_PRIME),
	JOYX_DRAGON(-30453, 53, "Joyx Dragon", 35, new int[] { 19624 }, "Joyx Dragon",
			new Position(2774, 4777), RegionInstanceType.DAG_SUPREME),
	MAGE_BEAST(-30452, 8018, "Mage Beast", 35, new int[] { 19624 }, "Mage Beast",new Position(2786, 4777),
			RegionInstanceType.DAG_REX),
	TROLL_KING(-30451, 13635, "Troll King", 35, new int[] { 19624 }, "Troll King",new Position(2798, 4777),
			RegionInstanceType.CALLISTO),
	AVATAR_TITAN(-30450, 8008, "Avatar Titan", 35, new int[] { 19624 }, "Avatar Titan", new Position(2810, 4777),
			RegionInstanceType.VETION),
	ANGEL_LUGIA(-30449, 3308, "Angel Lugia", 35, new int[] { 19624 }, "Angel Lugia",new Position(2806, 4793),
			RegionInstanceType.VENENATIS),
	LUCIEN(-30448, 3117, "Lucien", 35, new int[] { 19624 }, "Lucien", new Position(2774, 4793),
			RegionInstanceType.GALVEK),
	HERCULES(-30447, 201, "Hercules", 35, new int[] { 19624 }, "Hercules",
			new Position(2789, 4793), RegionInstanceType.REV_TARRAGON),

	// NEX(-30445, 13447, "Nex", 35, new int[] {19624}, "Nex", new Position(2874,
	// 5210, 0), RegionInstanceType.NEX),
	SATAN(-30446, 202, "Satan", 35, new int[] { 19624 }, "Satan", new Position(2761, 4793),
			RegionInstanceType.KINGS),
	ZEUS(-30445, 203, "Zeus", 35, new int[] { 19624 }, "Zeus", new Position(2632, 4777),
			RegionInstanceType.KINGS),
	GROUDON(-30444, 8010, "Groudon", 35, new int[] { 19624 }, "Groudon", new Position(2644, 4777),
			RegionInstanceType.KINGS),
	FRIEZA(-30443, 252, "Frieza", 35, new int[] { 19624 }, "Frieza",new Position(2655, 4777),
			RegionInstanceType.KINGS),
	PERFECT_CELL(-30442, 449, "Perfect Cell", 35, new int[] { 19624 }, "Perfect Cell", new Position(2668, 4777),
			RegionInstanceType.KINGS),
	SUPER_BUU(-30441, 452, "Super Buu", 35, new int[] { 19624 }, "Super Buu",new Position(2679, 4777),
			RegionInstanceType.KINGS),
	SCARLET_FALCON(-30440, 2949, "Scarlet Falcon", 35, new int[] { 19624 }, "Scarlet Falcon", new Position(2678, 4761),
			RegionInstanceType.KINGS),
	HERBAL_ROGUE(-30439, 2342, "Herbal Rogue", 35, new int[] { 19624 }, "Herbal Rogue", new Position(2667, 4761),
			RegionInstanceType.KINGS),
	AZURE_BEAST(-30438, 3831, "Azure Beast", 35, new int[] { 19624 }, "Azure Beast",new Position(2656, 4761),
			RegionInstanceType.KINGS),
	JOKER(-30437, 185, "Joker", 35, new int[] { 19624 }, "Joker",new Position(2644, 4760),
			RegionInstanceType.KINGS),
	CRYSTAL_QUEEN(-30436, 6430, "Crystal Queen", 35, new int[] { 19624 }, "Crystal Queen", new Position(2632, 4760),
			RegionInstanceType.KINGS),
	;

	InstanceData(int buttonid, int npcid, String text, int endamount, int rewards[], String name, Position position,
			RegionInstanceType region) {
		this.buttonid = buttonid;
		this.npcid = npcid;
		this.text = text;
		this.endamount = endamount;
		this.rewards = rewards;
		this.name = name;
		this.position = position;
		this.region = region;
	}

	private Position position;
	private RegionInstanceType region;

	public RegionInstanceType getRegion() {
		return region;
	}

	public void setRegion(RegionInstanceType region) {
		this.region = region;
	}

	public Position getPosition() {
		return position;
	}

	private int npcid, endamount, taskNumber, buttonid;

	public int getButtonid() {
		return buttonid;
	}

	private int amount;
	private int rewards[];

	public int[] getRewards() {
		return rewards;
	}

	private String text, name, weakness;

	public int getAmount() {
		return amount;
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
}
