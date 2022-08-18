package com.ruse.world.content.skill.impl.prayer;

public enum BonesData {
	BONES(526, 50), BAT_BONES(530, 60), WOLF_BONES(2859, 10), BIG_BONES(532, 75), FEMUR_BONES(15182, 300),
	BABYDRAGON_BONES(534, 80), JOGRE_BONE(3125, 95), ZOGRE_BONES(4812, 105), LONG_BONES(10976, 350),
	CURVED_BONE(10977, 350), SHAIKAHAN_BONES(3123, 100), DRAGON_BONES(536, 175), FAYRG_BONES(4830, 184),
	RAURG_BONES(4832, 203), DAGANNOTH_BONES(6729, 320), OURG_BONES(14793, 300), FROSTDRAGON_BONES(18830, 1000),
	GUABT_SNAKE_SPINE(22047, 750),EARTHQUAKE(2023, 1000),DBZ(2025, 2500),VOTE(2026, 1000);

	BonesData(int boneId, int buryXP) {
		this.boneId = boneId;
		this.buryXP = buryXP;
	}

	private int boneId;
	private int buryXP;

	public int getBoneID() {
		return this.boneId;
	}

	public int getBuryingXP() {
		return this.buryXP;
	}

	public static BonesData forId(int bone) {
		for (BonesData prayerData : BonesData.values()) {
			if (prayerData.getBoneID() == bone) {
				return prayerData;
			}
		}
		return null;
	}

}
