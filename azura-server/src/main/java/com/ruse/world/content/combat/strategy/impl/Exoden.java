package com.ruse.world.content.combat.strategy.impl;

import java.util.List;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Hit;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.HitQueue.CombatHit;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Exoden implements CombatStrategy {

	public static boolean minions_spawned = false;
	public static boolean minions_dead = false;
	public static final int MINION_NPCID = 3732;

	@Override
	public boolean canAttack(Character entity, Character victim) {
		return true;
	}

	@Override
	public CombatContainer attack(Character entity, Character victim) {
		return null;
	}

	@Override
	public boolean customContainerAttack(Character attacker, Character target) {
		int randomNomad = Misc.getRandom(30);
		NPC nomad = (NPC) attacker;
		if (target.getConstitution() <= 0) {
			nomad.forceChat("Muhahaha, easy!");
			return true;
		}
		if (nomad.isChargingAttack())
			return true;

		if (randomNomad >= 0 && randomNomad <= 15) {
			boolean meleeDistance = Locations.goodDistance(nomad.getPosition(), target.getPosition(), 2);
			boolean heal = !nomad.hasHealed() && nomad.getConstitution() < 35000;
			if (meleeDistance) {
				nomad.setHealed(true);
				nomad.performGraphic(gfx2);
				nomad.performAnimation(anim3);
				nomad.getMovementQueue().setLockMovement(true);
				nomad.forceChat("Zamorak.. Aid me..");
				nomad.setChargingAttack(true);
				TaskManager.submit(new Task(1, nomad, false) {
					int ticks = 0;

					@Override
					public void execute() {
						nomad.setConstitution(nomad.getConstitution() + 600);
						ticks++;
						if (ticks >= 5) {
							nomad.forceChat("Zamorak, I am in your favor.");
							nomad.getMovementQueue().setLockMovement(false);
							nomad.setChargingAttack(false);
							stop();
						}
					}
				});

			}
			if (randomNomad >= 23 && randomNomad <= 29) {
				nomad.setChargingAttack(true);
				nomad.getMovementQueue().setLockMovement(true);
				TaskManager.submit(new Task(1, nomad, false) {
					int ticks = 0;

					@Override
					public void execute() {
						if (ticks == 0 || ticks == 4) {
							nomad.performGraphic(gfx2);
							nomad.performAnimation(anim3);
						}
						if (ticks == 7)
							nomad.forceChat("Almost.. Almost there..");
						if (ticks == 9 || ticks == 11 || ticks == 13) {
							nomad.forceChat("Die!");
							nomad.performAnimation(new Animation(6501));
							nomad.performGraphic(new Graphic(65565));
						}
						if (ticks == 10 || ticks == 12 || ticks == 14) {
							nomad.performAnimation(new Animation(6501));
							new Projectile(nomad, target, 2283, 44, 3, 43, 31, 0).sendProjectile();
							nomad.getCombatBuilder()
									.setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MAGIC, true));
							int damage = target.getConstitution() / 4;
							Hit h = new Hit(damage);
							target.dealDamage(h);
							target.setConstitution(target.getConstitution() / 4);
						} else if (ticks == 16) {
							nomad.getMovementQueue().setLockMovement(false);
							nomad.setChargingAttack(false);
							this.stop();
						}
						ticks++;
					}
				});

			} else if (randomNomad >= 16 && randomNomad <= 19) {
				nomad.setChargingAttack(true);
				nomad.getMovementQueue().reset().setLockMovement(true);
				TaskManager.submit(new Task(1, nomad, false) {
					int ticks = 0;

					@Override
					public void execute() {
						if (ticks == 0) {
							target.getMovementQueue().freeze(15);
							target.performGraphic(gfx3);
							nomad.forceChat("Freeze!");
							nomad.performAnimation(new Animation(6501));
							nomad.getCombatBuilder()
									.setContainer(new CombatContainer(nomad, target, 1, CombatType.MAGIC, true));
							int damage = target.getConstitution() / 4;
							Hit h = new Hit(damage);
							target.dealDamage(h);
							target.setConstitution(target.getConstitution() / 4);
						} else if (ticks == 1 || ticks == 4 || ticks == 5) {
							nomad.performGraphic(gfx2);
							nomad.performAnimation(anim3);
						}
						if (ticks == 5)
							nomad.forceChat("Zamorak, please! Allow me to me channel your power!");
						if (ticks == 10)
							nomad.forceChat("Adventurer, prepare to be blown away!");
						if (ticks == 18)
							nomad.forceChat("I call upon you, Zamorak!");
						if (ticks == 20)
							nomad.performAnimation(new Animation(6501));
						if (ticks == 23)
							new Projectile(nomad, target, 2001, 44, 3, 43, 31, 0).sendProjectile();
						if (ticks == 24)
							target.performGraphic(new Graphic(2004));
						int damage = target.getConstitution() / 4;
						Hit h = new Hit(damage);
						target.dealDamage(h);
						target.setConstitution(target.getConstitution() / 4);
						if (ticks == 25) {
							nomad.getCombatBuilder()
									.setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MAGIC, false));
							target.getMovementQueue().freeze(0);
							nomad.getMovementQueue().setLockMovement(false);
							nomad.setChargingAttack(false);
							stop();
						}
						ticks++;
					}
				});
			} else {
				if (meleeDistance) {
					nomad.performAnimation(anim2);
					nomad.forceChat("You shall fall!");
					nomad.getCombatBuilder()
							.setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MELEE, false));
				} else {
					target.getMovementQueue().freeze(15);
					target.performGraphic(gfx3);
					nomad.forceChat("Freeze!");
					nomad.performAnimation(new Animation(6501));
					nomad.getCombatBuilder()
							.setContainer(new CombatContainer(nomad, target, 1, 1, CombatType.MAGIC, true));
					int damage = target.getConstitution() / 4;
					Hit h = new Hit(damage);
					target.dealDamage(h);
					target.setConstitution(target.getConstitution() / 4);
				}
			}
		}
		if (nomad.getConstitution() <= 65000 && minions_dead == false) {
			nomad.forceChat("Time to spawn my army!");
			NPC[] babies = new NPC[] { new NPC(MINION_NPCID, target.getPosition()) };
			minions_spawned = true;
			for (NPC n : babies) {
				World.register(n);
				n.getCombatBuilder().attack(target);
				if (n.getConstitution() <= 0) {
					World.deregister(n);
				}
			}
		}
		if (nomad.getConstitution() <= 50000) {
			final Position start = target.getPosition().copy();
			final Position second = new Position(start.getX() + 2, start.getY() + Misc.getRandom(2));
			final Position last = new Position(start.getX() - 2, start.getY() - Misc.getRandom(2));

			final Player p = (Player) target;
			final List<Player> list = Misc.getCombinedPlayerList(p);
			// if(nomad.getConstitution() <= 35000 && randomNomad == 30) {
			for (Player t : list) {
				if (t == null)
					continue;
				if (t.getPosition().equals(start) || t.getPosition().equals(second) || t.getPosition().equals(last)) {
					new CombatHit(nomad.getCombatBuilder(), new CombatContainer(nomad, t, 3, CombatType.MAGIC, true))
							.handleAttack();
					p.getPacketSender().sendGlobalGraphic(new Graphic(281), start);
					p.getPacketSender().sendGlobalGraphic(new Graphic(281), second);
					p.getPacketSender().sendGlobalGraphic(new Graphic(281), last);
					int damage = target.getConstitution() / 2;
					Hit h = new Hit(damage);
					p.dealDamage(h);
					p.setConstitution(p.getConstitution() / 2);
				}
			}
			// }
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
		return CombatType.MIXED;
	}

	private static final Animation anim2 = new Animation(6501);
	private static final Animation anim3 = new Animation(6501);
	private static final Graphic gfx2 = new Graphic(2281, GraphicHeight.LOW);
	private static final Graphic gfx3 = new Graphic(369, GraphicHeight.LOW);
}
