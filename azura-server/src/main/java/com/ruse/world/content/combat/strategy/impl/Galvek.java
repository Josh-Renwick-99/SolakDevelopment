package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

public class Galvek implements CombatStrategy {

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
		NPC galvek = (NPC) entity;
		if (victim.getConstitution() <= 0 || victim.getConstitution() <= 0) {
			return true;
		}
		if (galvek.getConstitution() <= 1200 && !galvek.hasHealed()) {
			galvek.performAnimation(anim1);
			galvek.performGraphic(gfx1);
			galvek.setConstitution(galvek.getConstitution() + Misc.getRandom(1600));
			galvek.setHealed(true);
		}
		if (galvek.isChargingAttack()) {
			return true;
		}
		int random = Misc.getRandom(10);
		if (random <= 8 && Locations.goodDistance(galvek.getPosition().getX(), galvek.getPosition().getY(),
				victim.getPosition().getX(), victim.getPosition().getY(), 3)) {
			galvek.performAnimation(anim2);
			galvek.performGraphic(gfx5);
			victim.performGraphic(gfx7);

			galvek.getCombatBuilder().setContainer(new CombatContainer(galvek, victim, 1, 2, CombatType.MELEE, true));
		} else if (random <= 4 || !Locations.goodDistance(galvek.getPosition().getX(), galvek.getPosition().getY(),
				victim.getPosition().getX(), victim.getPosition().getY(), 14)) {
			galvek.getCombatBuilder().setContainer(new CombatContainer(galvek, victim, 1, 2, CombatType.MAGIC, true));
			galvek.performAnimation(anim3);
			galvek.performGraphic(gfx3);

			galvek.setChargingAttack(false);
		} else if (random <= 6 || !Locations.goodDistance(galvek.getPosition().getX(), galvek.getPosition().getY(),
				victim.getPosition().getX(), victim.getPosition().getY(), 14)) {
			galvek.getCombatBuilder().setContainer(new CombatContainer(galvek, victim, 1, 2, CombatType.MAGIC, true));
			galvek.performAnimation(anim1);
			galvek.performGraphic(gfx3);
			victim.performAnimation(anim5);
			victim.performGraphic(gfx8);
			galvek.setChargingAttack(false);

		} else if (random >= 2 || !Locations.goodDistance(galvek.getPosition().getX(), galvek.getPosition().getY(),
				victim.getPosition().getX(), victim.getPosition().getY(), 14)) {
			galvek.getCombatBuilder().setContainer(new CombatContainer(galvek, victim, 1, 2, CombatType.MAGIC, true));
			galvek.performAnimation(anim4);
			galvek.performGraphic(gfx4);

			galvek.setChargingAttack(false);
			TaskManager.submit(new Task(2, galvek, true) {
				int tick = 0;

				@Override
				public void execute() {
					switch (tick) {
					case 1:
						new Projectile(galvek, victim, gfx5.getId(), 44, 3, 43, 31, 0).sendProjectile();
						galvek.setChargingAttack(false);
						stop();
						break;
					}
					tick++;
				}
			});
		} else {
			galvek.getCombatBuilder().setContainer(new CombatContainer(galvek, victim, 1, 3, CombatType.RANGED, true));
			galvek.performAnimation(anim4);
			galvek.performGraphic(gfx2);
			victim.performGraphic(gfx6);
			// galvek.setChargingAttack(true);
			TaskManager.submit(new Task(2, galvek, false) {
				@Override
				public void execute() {
					galvek.performAnimation(anim1);
					victim.performGraphic(gfx4);
					galvek.setChargingAttack(false);
					stop();
				}
			});
		}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 10;
	}

	private static final Animation anim1 = new Animation(3108);
	private static final Animation anim2 = new Animation(11991);
	private static final Animation anim3 = new Animation(10961);
	private static final Animation anim4 = new Animation(11989);
	private static final Animation anim5 = new Animation(3108);
	private static final Graphic gfx1 = new Graphic(1640);
	private static final Graphic gfx2 = new Graphic(1639);
	private static final Graphic gfx3 = new Graphic(1995);
	private static final Graphic gfx4 = new Graphic(1549);
	private static final Graphic gfx5 = new Graphic(1223);
	private static final Graphic gfx6 = new Graphic(1662);
	private static final Graphic gfx7 = new Graphic(1455);
	private static final Graphic gfx8 = new Graphic(639);

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
