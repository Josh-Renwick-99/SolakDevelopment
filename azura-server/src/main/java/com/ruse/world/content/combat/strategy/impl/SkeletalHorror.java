package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.GraphicHeight;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.model.Skill;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class SkeletalHorror implements CombatStrategy {

	private static final Animation anim = new Animation(12063);
	private static final Animation anim2 = new Animation(12062);
	private static final Animation anim3 = new Animation(12061);

	private static final Graphic gfx1 = new Graphic(1901, 3, GraphicHeight.MIDDLE);
	private static final Graphic gfx2 = new Graphic(1901);

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
		NPC skele = (NPC) entity;
		if (victim.getConstitution() <= 0) {
			return true;
		}
		if (skele.isChargingAttack()) {
			return true;
		}
		if (Locations.goodDistance(skele.getPosition().copy(), victim.getPosition().copy(), 1)
				&& Misc.getRandom(6) <= 4) {
			skele.performAnimation(anim);
			skele.performGraphic(gfx1);
			skele.getCombatBuilder().setContainer(new CombatContainer(skele, victim, 1, 2, CombatType.MELEE, true));
		} else if (Misc.getRandom(10) <= 7) {
			skele.performAnimation(anim2);
			skele.setChargingAttack(true);
			skele.getCombatBuilder().setContainer(new CombatContainer(skele, victim, 1, 2, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, skele, false) {
				@Override
				protected void execute() {
					new Projectile(skele, victim, 1901, 44, 3, 43, 31, 0).sendProjectile();
					skele.setChargingAttack(false).getCombatBuilder()
							.setAttackTimer(skele.getDefinition().getAttackSpeed() - 1);
					stop();
				}
			});
		} else {
			skele.performAnimation(anim3);
			victim.performGraphic(gfx2);
			skele.getCombatBuilder().setContainer(new CombatContainer(skele, victim, 1, 2, CombatType.MAGIC, true));
		}
		return true;
	}

	@Override
	public int attackDelay(Character entity) {
		return entity.getAttackSpeed();
	}

	@Override
	public int attackDistance(Character entity) {
		return 5;
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MIXED;
	}

	/// private static final Animation anim2 = new Animation(12696);
	// private static final Animation anim3 = new Animation(12698);
	// private static final Graphic gfx2 = new Graphic(2281, GraphicHeight.LOW);
	// private static final Graphic gfx3 = new Graphic(369, GraphicHeight.LOW);
}
