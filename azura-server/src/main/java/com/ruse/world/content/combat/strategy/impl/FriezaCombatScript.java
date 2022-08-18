package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
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


public class FriezaCombatScript implements CombatStrategy {
	public static final int FRIEZA = 252;
	
	private final int[] MELEEANIMS = { 2876,402,1658,3170,2400 };
	private final int[] MAGICANIMS = { 791,729,716,725,744,1125,1060,3007,1877,1914 };
	private final int[] RANGEANIMS = { 0 };
	private final int[] GFX = { 2264, 499, 360, 720,332 }; 

	private int getRandomAnimationMelee() {
		return MELEEANIMS[RandomUtility.exclusiveRandom(0, MELEEANIMS.length)];
	}
	private int getRandomAnimationMagic() {
		return MELEEANIMS[RandomUtility.exclusiveRandom(0, MAGICANIMS.length)];
	}
	private int getRandomAnimationRange() {
		return RANGEANIMS[RandomUtility.exclusiveRandom(0, RANGEANIMS.length)];
	}
	private int getRandomGfx() {
		return GFX[RandomUtility.exclusiveRandom(0, GFX.length)];
	}

	/**
	 * Attacking melee
	 */
	private Animation animationsmelee = new Animation(getRandomAnimationMelee());
//	private Animation animationsmagic = new Animation(getRandomAnimationMagic());
	//private Animation animationsrange = new Animation(getRandomAnimationRange());
	private Graphic graphics = new Graphic(getRandomGfx());
	

	@Override
	public boolean canAttack(Character entity, Character victim) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean customContainerAttack(Character entity, Character victim) {
		NPC freiza = (NPC) entity;
		animationsmelee = new Animation(getRandomAnimationMelee());
	//	animationsmagic = new Animation(getRandomAnimationMagic());
	//	animationsrange = new Animation(getRandomAnimationRange());
		graphics = new Graphic(getRandomGfx());
		if (Locations.goodDistance(freiza.getPosition().copy(), victim.getPosition().copy(), 1)
				&& Misc.getRandom(5) <= 3) {
			freiza.performAnimation(animationsmelee);
			//freiza.performGraphic(graphics);
			new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();

			freiza.getCombatBuilder().setContainer(new CombatContainer(freiza, victim, 1, 1, CombatType.MAGIC, true));
		//	new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();

		} else {
			
			freiza.performAnimation(animationsmelee);
			
			new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
			freiza.getCombatBuilder().setContainer(new CombatContainer(freiza, victim, 1, 2, CombatType.MELEE,
					Misc.getRandom(10) <= 2 ? false : true));
		}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		// TODO Auto-generated method stub
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public CombatType getCombatType() {
		// TODO Auto-generated method stub
		return CombatType.MELEE;
	}

}
