package com.ruse.world.content.combat.strategy.impl;

import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;


public class IronBossCombatScript implements CombatStrategy {
	private final int[] MELEEANIMS = { 903,920,410,848,869 };
	private final int[] GFX = { 710, 2929, 2923, 2910,984 }; 

	private int getRandomAnimationMelee() {
		return MELEEANIMS[RandomUtility.exclusiveRandom(0, MELEEANIMS.length)];
	}
	private int getRandomGfx() {
		return GFX[RandomUtility.exclusiveRandom(0, GFX.length)];
	}


	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC Boss = (NPC) entity;
		Animation animationsmelee = new Animation(getRandomAnimationMelee());
		Graphic graphics = new Graphic(getRandomGfx());
		if (Locations.goodDistance(Boss.getPosition().copy(), victim.getPosition().copy(), 1)
				&& Misc.getRandom(5) <= 3) {
			Boss.performAnimation(animationsmelee);
					new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
			Boss.getCombatBuilder().setContainer(new CombatContainer(Boss, victim, 1, 1, CombatType.MAGIC, true));
		} else {
			Boss.performAnimation(animationsmelee);
			new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
			Boss.getCombatBuilder().setContainer(new CombatContainer(Boss, victim, 1, 2, CombatType.MELEE,
					Misc.getRandom(10) > 2));
		}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 4;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}

}
