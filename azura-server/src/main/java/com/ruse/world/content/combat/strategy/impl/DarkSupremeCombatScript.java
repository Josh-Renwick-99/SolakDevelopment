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
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DarkSupremeCombatScript implements CombatStrategy {
	private final int[] meleeAnims = { 1658,2400 };
	public List<Position> usedTiles = new ArrayList<>();

	private int getRandomAnimationMelee() {
		return meleeAnims[RandomUtility.exclusiveRandom(0, meleeAnims.length)];
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
		Player player = (Player) victim;
		NPC supreme = (NPC) entity;

		if (player.getConstitution() <= 0 || entity.getConstitution() <= 0) {
			return true;
		}

		if (supreme.isChargingAttack() || player.getConstitution() <= 0) {
			return true;
		}

		Animation anim = new Animation(getRandomAnimationMelee());
		Graphic graphics = new Graphic(194);

		if (Misc.getRandom(100) <= 15 && !supreme.isChargingAttack()) {
			supreme.performAnimation(anim);
			supreme.forceChat("Suffer from mah lazers!");
			supreme.setChargingAttack(true);
			TaskManager.submit(new Task(1, supreme, false) {
				int ticks = 0;

				@Override
				public void execute() {
					PrayerHandler.deactivateAll(player);
					CurseHandler.deactivateAll(player);
					if (ticks == 6) {
						player.sendMessage("You feel like you can use prayers again!");
						supreme.setChargingAttack(false);
						this.stop();
					}
					ticks++;
				}
			});
		} else if (Misc.getRandom(100) <= 25 && !supreme.isChargingAttack()) {
			supreme.performAnimation(anim);
			supreme.forceChat("Dodge this!!");
			supreme.setChargingAttack(true);
			for (int i = 0; i < 50; i++) {// TODO edit with new coords
				int randX = Misc.random(2708, 2740), randY = Misc.random(4424, 4457);
				Position pos = new Position(randX, randY, 0);
				if (!usedTiles.contains(pos) && Misc.random(2) != 2) {
					usedTiles.add(pos);
				}
			}
			for (Position p : usedTiles) {
				TaskManager.submit(new Task(Misc.random(5, 15), supreme, false) {

					@Override
					public void execute() {
						TaskManager.submit(new Task(2, supreme, false) {

							@Override
							public void execute() {
								this.stop();
								player.getPacketSender().sendGraphic(new Graphic(281), new Position(p.getX(), p.getY(), p.getZ()));
								if (player.getPosition().getX() == p.getX() &&
										player.getPosition().getY() == p.getY() &&
										player.getPosition().getZ() == supreme.getPosition().getZ()) {
									int damage = Misc.exclusiveRandom(300, 800);
									player.dealDamage(new Hit(damage, Hitmask.RED, CombatIcon.NONE));
									player.sendMessage("@blu@You were damaged by rocks!");
									player.performGraphic(new Graphic(2149));
								}

								supreme.setChargingAttack(false);
								this.stop();
							}
						});
						this.stop();
					}
				});
			}
		} else {
			int random = Misc.getRandom(100);

			if (random <= 33) {
				supreme.performAnimation(new Animation(2240));
				new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
				supreme.getCombatBuilder().setContainer(new CombatContainer(supreme, victim, 1, 1, CombatType.MAGIC, true));
			} else if (random <= 66) {
				supreme.performAnimation(new Animation(811));
				new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
				supreme.getCombatBuilder().setContainer(new CombatContainer(supreme, victim, 1, 1, CombatType.RANGED, true));
			} else {
				supreme.performAnimation(new Animation(724));
				new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
				supreme.getCombatBuilder().setContainer(new CombatContainer(supreme, victim, 1, 1, CombatType.MELEE, true));
			}
		}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 8;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}

	@Override
	public boolean ignoreAttackDistance() {
		return true;
	}

}
