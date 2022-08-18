package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class CrystalQueen implements CombatStrategy {

	private void magicAttack(NPC npc, Character victim) {
		npc.setChargingAttack(true);
		npc.performAnimation(new Animation(403));
		npc.getCombatBuilder().setContainer(new CombatContainer(npc, victim, 1, 3, CombatType.MAGIC, true));
		TaskManager.submit(new Task(1, npc, false) {
			int tick = 0;

			@Override
			public void execute() {
				if (tick == 0) {
					new Projectile(npc, victim, 359, 50, 35, 20, 28, 0).sendProjectile();
				} else if (tick == 1) {
					npc.setChargingAttack(false);
					stop();
				}
				tick++;
			}
		});
	}
	private void meleeAttack(NPC npc, Character victim) {
		npc.setChargingAttack(true);
		npc.performAnimation(new Animation(npc.getDefinition().getAttackAnimation()));
		npc.getCombatBuilder().setContainer(new CombatContainer(npc, victim, 1, 1, CombatType.MELEE, true));
		npc.setChargingAttack(false);
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
		NPC demon = (NPC) entity;

		if (demon.isChargingAttack() || demon.getConstitution() <= 0 || victim.getConstitution() <= 0) {
			return true;
		}
		int rng = Misc.getRandom(100);

		if (Locations.goodDistance(demon.copy(), victim.copy(), 1)) {
			if (rng >= 70) {
				meleeAttack(demon, victim);
			} else {
				magicAttack(demon, victim);
			}
		} else {
			if (rng >= 85) {
				specialAttack(demon, victim);
			} else {
				magicAttack(demon, victim);
			}
		}
		return true;
	}
		private void specialAttack(NPC npc, Character victim) {
			npc.setChargingAttack(true);
			npc.performAnimation(new Animation(2890));
			//npc.getCombatBuilder().setContainer(new CombatContainer(npc, victim, 1, 3, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, npc, false) {
				Player target = (Player) victim;
				int tick = 0;

				@Override
				public void execute() {
					if (tick == 0) {
						CurseHandler.deactivateAll(target);
						PrayerHandler.deactivateAll(target);
						npc.forceChat("SKREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
					} else if (tick == 1) {
						npc.setChargingAttack(false);
						stop();
					}
					tick++;
				}
			});
		}
	@Override
	public int attackDelay(Character entity) {
		return 7;
	}

	@Override
	public int attackDistance(Character entity) {
		return 2;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}

}
