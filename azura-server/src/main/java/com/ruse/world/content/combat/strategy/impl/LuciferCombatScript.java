package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class LuciferCombatScript implements CombatStrategy {
	static List<Position> tiles = new ArrayList<>();
	private final int[] meleeAnims = { 1658,2400 };

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
		NPC lucifer = (NPC) entity;

		if (player.getConstitution() <= 0 || entity.getConstitution() <= 0) {
			return true;
		}

		if (lucifer.isChargingAttack() || player.getConstitution() <= 0) {
			return true;
		}

		Animation anim = new Animation(getRandomAnimationMelee());
		Graphic graphics = new Graphic(194);

		if (Misc.getRandom(100) <= 25 && !lucifer.isChargingAttack()) {
			lucifer.performAnimation(anim);

			lucifer.forceChat("Burn In Hell!");
			lucifer.setChargingAttack(true);
			final int origX = lucifer.getPosition().getX(), origY = lucifer.getPosition().getY();
			TaskManager.submit(new Task(1, lucifer, false) {
				int ticks = 0;

				@Override
				public void execute() {
					for (int x = origX - ticks; x < origX + ticks; x++) {
						for (int y = origY - ticks; y < origY + ticks; y++) {
							if (x == origX - ticks || y == origY - ticks || x == origX + ticks
									|| y == origY + ticks) {
								player.getPacketSender().sendGraphic(new Graphic(78), new Position(x, y));
								for (Player p_ : Misc.getCombinedPlayerList(player)) {
									if (p_ == null || p_.getPosition().getZ() != lucifer.getPosition().getZ() &&
									p_.getPosition().getRegionId() != lucifer.getPosition().getRegionId())
										continue;
									if (p_.getPosition().getX() == x &&
											p_.getPosition().getY() == y &&
											p_.getPosition().getZ() == lucifer.getPosition().getZ())
										p_.dealDamage(new Hit(150 + Misc.getRandom(110), Hitmask.RED, CombatIcon.NONE));
								}
							}
						}
					}
					if (ticks == 6) {
						lucifer.setChargingAttack(false);
						this.stop();
					}
					ticks++;
				}
			});
		} else if (Misc.getRandom(100) <= 25 && !lucifer.isChargingAttack()) {
			lucifer.performAnimation(anim);

			lucifer.forceChat("Burn To Ashes!!");
			lucifer.setChargingAttack(true);
			TaskManager.submit(new Task(2, lucifer, false) {

				@Override
				public void execute() {
					getWavesNorthSouth(lucifer, player);
				}

				public void getWavesNorthSouth(NPC npc, Player player) {
					if (tiles.size() <= 0) {
						lucifer.setChargingAttack(false);
						this.stop();
						return;
					}

					TaskManager.submit(new Task(1, npc, false) {
						final int[] y = {5410};
						final int hole1 = Misc.exclusiveRandom(1, 20);
						final int hole2 = Misc.exclusiveRandom(1, 20);
						final int hole3 = Misc.exclusiveRandom(1, 20);
						final int hole4 = Misc.exclusiveRandom(1, 20);
						public void execute() {
							final List<Position> currentYTiles = tiles.stream()
									.filter(position -> position.getY() == y[0])
									.collect(Collectors.toList());
							removeExitPoints(currentYTiles, hole1, hole2, hole3, hole4);
							for (Position p : currentYTiles) {
								player.getPacketSender().sendGraphic(new Graphic(78), new Position(p.getX(), p.getY(), p.getZ()));
								for (Player players : npc.getClosePlayers(32)) {
									if (players.getPosition().getX() == p.getX() &&
											players.getPosition().getY() == p.getY() &&
											players.getPosition().getZ() == npc.getPosition().getZ()) {
										int damage = Misc.exclusiveRandom(300, 800);
										player.dealDamage(new Hit(damage, Hitmask.RED, CombatIcon.NONE));
										players.sendMessage("@blu@You were hit by the flames! move faster!");
										players.performGraphic(new Graphic(1640));
									}
								}
								if (p.getX() == 1897) {
									y[0]--;
								}
								if (p.getX() == 1897 && y[0] == 5398) {
									lucifer.setChargingAttack(false);
									this.stop();
								}
							}
						}
					});
					this.stop();
				}
			});
		} else {
			lucifer.performAnimation(anim);
			new Projectile(entity, victim, graphics.getId(), 44, 3, 43, 31, 0).sendProjectile();
			lucifer.getCombatBuilder().setContainer(new CombatContainer(lucifer, victim, 1, 1, CombatType.MAGIC, true));
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

	public void removeExitPoints(List<Position> tiles, int hole1, int hole2, int hole3, int hole4) {
		if (tiles.isEmpty()) {
			return;
		}
		if (hole1 < tiles.size()) {
			tiles.remove(hole1);
		}
		if (hole2 < tiles.size()) {
			tiles.remove(hole2);
		}
		if (hole3 < tiles.size()) {
			tiles.remove(hole3);
		}
		if (hole4 < tiles.size()) {
			tiles.remove(hole4);
		}
	}

	static {
	//	for (int y = 5154; y >= 5139; y--) {
	//		for (int x = 1945; x <= 1958; x++) {
		for (int y = 5410; y >= 5398; y--) {
			for (int x = 1883; x <= 1897; x++) {
				tiles.add(new Position(x, y));
			}
		}
	}

}
