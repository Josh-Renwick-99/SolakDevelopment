package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.MessageType;
import com.ruse.model.Position;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class Vasa implements CombatStrategy {

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
		NPC Vasa = (NPC) entity;
		if (Vasa.isChargingAttack() || victim.getConstitution() <= 0) {
			return true;
		}
		if (Misc.getRandom(15) <= 2) {
			int hitAmount = 2;
			Vasa.performGraphic(new Graphic(2549));
			Vasa.setConstitution(Vasa.getConstitution() + hitAmount);
			Vasa.getCombatBuilder().setContainer(new CombatContainer(Vasa, victim, 1, 3, CombatType.MAGIC, true));
			Player Player = (com.ruse.world.entity.impl.player.Player) victim;
			CurseHandler.deactivateAll(Player);
			((Player) victim).getPacketSender().sendMessage(
					"<img=18><shad=1>@red@Vasa Nistirio absorbs his next attack, healing himself a bit.");
			((Player) victim).getPacketSender().sendMessage("<img=18><shad=1>@cya@Vasa Nistirio deactivated your prayer");

		}
			Vasa.setChargingAttack(true);
			Vasa.performAnimation(new Animation(345));
			Vasa.getCombatBuilder().setContainer(new CombatContainer(Vasa, victim, 1, 3, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, Vasa, false) {
				int tick = 0;

				@Override
				public void execute() {
					if (tick == 0) {
						new Projectile(Vasa, victim, 1194, 44, 3, 41, 31, 0).sendProjectile();
					} else if (tick == 1) {
						Vasa.setChargingAttack(false);
						stop();
					}
					tick++;
				}
			});
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 20;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}
}
