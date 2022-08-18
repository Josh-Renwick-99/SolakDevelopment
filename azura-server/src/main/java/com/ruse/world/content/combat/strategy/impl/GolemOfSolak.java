package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;


public class GolemOfSolak implements CombatStrategy {
	private final int[] MELEEANIMS = { 903,920,410,848,869 };
	private final int[] GFX = { 710, 2929, 2923, 2910,984 }; 
	private boolean[] specialAttacks = {false, false, false};
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
		NPC golem = (NPC) entity;
		if(golem.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		double total = (double) golem.getConstitution() / (double) golem.getDefaultConstitution() * 100;
		if(total <= 75 && !specialAttacks[0]) {
			specialAttack(victim, golem);
			specialAttacks[0] = true;
		} else if(total <= 50 && !specialAttacks[1]) {
			specialAttack(victim, golem);
			specialAttacks[1] = true;
		} else if(total <= 25 && !specialAttacks[2]) {
			specialAttack(victim, golem);
			specialAttacks[2] = true;
		}

		if (Locations.goodDistance(golem.getPosition().copy(), victim.getPosition().copy(), 1)
				&& Misc.getRandom(5) <= 3) {
			meleeAttack(victim, golem);
		} else {
			mageAttack(victim, golem);
		}
		return true;
	}

	public void specialAttack(Character victim, NPC golem) {
		//golem.setChargingAttack(true);
		golem.performAnimation(new Animation(MELEEANIMS[2]));
		golem.forceChat("Gwador, Gwathel, Ada");
		TaskManager.submit(new Task(1, golem, false) {
			Player target = (Player) victim;
			int tick = 0;

			@Override
			public void execute() {// every 2 ticks for 5 seconds
				if (tick < 10 && tick % 2 != 0) {
					// try using golem.getPossibleTargets()
					for (Player t : Misc.getCombinedPlayerList(target)) {
						if (t == null || !Locations.goodDistance(t, golem, 50))
							continue;

						t.performGraphic(new Graphic(481));
						t.dealDamage(new Hit(500, Hitmask.RED, CombatIcon.MAGIC));

					}

				} else if (tick == 11) {
					//golem.setChargingAttack(false);
					stop();
				}
				tick++;
			}
		});
	}

	public void mageAttack(Character victim, NPC boss) {
		Animation animationsmelee = new Animation(getRandomAnimationMelee());
		Graphic graphics = new Graphic(getRandomGfx());
		boss.performAnimation(animationsmelee);
		new Projectile(boss, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
		boss.getCombatBuilder().setContainer(new CombatContainer(boss, victim, 1, 1, CombatType.MAGIC, true));
	}
	public void meleeAttack(Character victim, NPC boss) {
		Animation animationsmelee = new Animation(getRandomAnimationMelee());
		Graphic graphics = new Graphic(getRandomGfx());
		boss.performAnimation(animationsmelee);
		new Projectile(boss, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
		boss.getCombatBuilder().setContainer(new CombatContainer(boss, victim, 1, 2, CombatType.MELEE,
				Misc.getRandom(10) > 2));
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 50;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}

}
