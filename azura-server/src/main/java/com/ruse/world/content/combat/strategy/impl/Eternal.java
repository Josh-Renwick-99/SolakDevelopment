package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Eternal implements CombatStrategy {

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
		NPC eternal = (NPC) entity;
		if (eternal.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		if (Misc.getRandom(15) <= 2) {
			int hitAmount = 2;
			eternal.performGraphic(new Graphic(2549));
			eternal.setConstitution(eternal.getConstitution() + hitAmount);
			eternal.getCombatBuilder().setContainer(new CombatContainer(eternal, victim, 1, 3, CombatType.MAGIC, true));
			((Player) victim).getPacketSender().sendMessage(MessageType.NPC_ALERT,
					"Eternal absorbs his next attack, healing himself a bit.");
		}
		if (Locations.goodDistance(eternal.getPosition().copy(), victim.getPosition().copy(), 3)
				&& Misc.getRandom(5) <= 3) {
			eternal.performAnimation(new Animation(eternal.getDefinition().getAttackAnimation()));
			// Eternal.performAnimation(new Animation(5026));
			eternal.getCombatBuilder().setContainer(new CombatContainer(eternal, victim, 1, 1, CombatType.MELEE, true));
			if (Misc.getRandom(10) <= 2) {
				if (victim.isPlayer()) {
					int moveX = victim.getPosition().getX() - eternal.getPosition().getX();
					int moveY = victim.getPosition().getY() - eternal.getPosition().getY();

					if (moveX > 0)
						moveX = 3;
					else if (moveX < 0)
						moveX = -3;
					if (moveY > 0)
						moveY = 3;
					else if (moveY < 0)
						moveY = -3;
					if (victim.getMovementQueue().canWalk(moveX, moveY)) {
						victim.setEntityInteraction(eternal);
						victim.getMovementQueue().reset();
						victim.moveTo(new Position(victim.getPosition().getX() + moveX, victim.getPosition().getY() + moveY));
						victim.performGraphic(new Graphic(254, GraphicHeight.HIGH));
						TaskManager.submit(new Task(1, false) {
							@Override
							public void execute() {
								victim.getMovementQueue().freeze(6);
								stop();
							}
						});
						eternal.getCombatBuilder()
								.setContainer(new CombatContainer(eternal, victim, 1, 3, CombatType.MAGIC, true));
						((Player) victim).getPacketSender().sendMessage(MessageType.NPC_ALERT, "You have been knocked back!");
					}
				}

			}
		} else {
			eternal.setChargingAttack(true);
			eternal.performAnimation(new Animation(5026));
			eternal.getCombatBuilder().setContainer(new CombatContainer(eternal, victim, 1, 3, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, eternal, false) {
				int tick = 0;

				@Override
				public void execute() {
					if (tick == 0) {
						new Projectile(eternal, victim, 1194, 44, 3, 41, 31, 0).sendProjectile();
					} else if (tick == 1) {
						eternal.setChargingAttack(false);
						stop();
					}
					tick++;
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
		return 3;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
