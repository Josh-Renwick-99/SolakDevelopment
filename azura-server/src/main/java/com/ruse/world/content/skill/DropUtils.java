package com.ruse.world.content.skill;

import com.ruse.world.content.skill.impl.summoning.BossPets;

public final class DropUtils {
	
	
	public static final Object[][] PET_DATA = {
		//id, xp multiplier, damage multiplier, hasXpMultiplier, hasDamageMultiplier
		{6320, 1.5, 1.5, true, true},
		{1801, 1.5, 1.5, true, true},
		{6304, 1.5, 1.4, false, true},
		{189, 2.0, 1.1, true, true},
		{BossPets.BossPet.DEMON_PET.npcId, 1.0, 1.1, false, true},
		{BossPets.BossPet.GOLEM_PET.npcId, 1.0, 1.15, false, true},
		{BossPets.BossPet.CRYSTAL_ORC_PET.npcId, 1.0, 1.25, false, true},
		{BossPets.BossPet.KILJAEDEN_PET.npcId, 1.0, 1.15, false, true},
	};
	
	public static boolean hasXpBonus(int npcId) {
		for(Object[] data : PET_DATA) {
			if((int)data[0] == npcId && data[3].toString().equals("true")) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean hasDamageBonus(int npcId) {
		for(Object[] data : PET_DATA) {
			if((int)data[0] == npcId && data[4].toString().equals("true")) {
				return true;
			}
		}
		
		return false;
	}
	
	public static double getXpBonus(int npcId) {
		double bonus = 1.0;
		if(!hasXpBonus(npcId)) {
		//	// System.out.println("No xp bonus for that npc.");
			return bonus;
		}
		
		for(Object[] data : PET_DATA) {
			if((int)data[0] == npcId) {
				bonus = (double)data[1];
			}
		}
		
		return bonus;
	}
	
	public static double getDamageBonus(int npcId) {
		double bonus = 1.0;
		if(!hasDamageBonus(npcId)) {
		//	// System.out.println("No damage bonus for that npc.");
			return bonus;
		}

		
		for(Object[] data : PET_DATA) {
			if((int)data[0] == npcId) {
				bonus = (double)data[2];
			}
		}
		
		return bonus;
	}
	
}
