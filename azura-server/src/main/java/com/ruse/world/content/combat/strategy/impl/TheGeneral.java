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

public class TheGeneral implements CombatStrategy {

	private static final Animation anim = new Animation(11665);
	private static final Animation anim2 = new Animation(11665);
	private static final Animation anim3 = new Animation(11665);

	private static final Graphic gfx1 = new Graphic(1935, 3, GraphicHeight.MIDDLE);
	private static final Graphic gfx2 = new Graphic(1834);

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
		NPC cB = (NPC) entity;
		if (victim.getConstitution() <= 0) {
			return true;
		}
		if (cB.isChargingAttack()) {
			return true;
		}
		Player target = (Player) victim;
		boolean stomp = false;
		for (Player t : Misc.getCombinedPlayerList(target)) {
			if (Locations.goodDistance(t.getPosition(), cB.getPosition(), 1)) {
				stomp = true;
				cB.getCombatBuilder().setVictim(t);
				cB.getCombatBuilder().setContainer(new CombatContainer(cB, target, 1, CombatType.MAGIC, true));
			}
		}
		if (stomp) {
			cB.performAnimation(anim);
			victim.performGraphic(gfx1);
			cB.performGraphic(gfx2);

		}

		if (Locations.goodDistance(cB.getPosition().copy(), victim.getPosition().copy(), 1) && Misc.getRandom(6) <= 4) {
			cB.performAnimation(anim);
			victim.performGraphic(gfx1);
			cB.getCombatBuilder().setContainer(new CombatContainer(cB, victim, 1, 2, CombatType.MELEE, true));
		} else if (Misc.getRandom(10) <= 7) {
			cB.performAnimation(anim2);
			cB.setChargingAttack(true);
			cB.getCombatBuilder().setContainer(new CombatContainer(cB, victim, 1, 2, CombatType.MAGIC, true));
			TaskManager.submit(new Task(1, cB, false) {
				@Override
				protected void execute() {
					if (cB.getConstitution() <= 0) {
						target.getPacketSender().sendMessage("The General was unsuccessful today.");

					}
					int skill = Misc.getRandom(6);
					Skill skillT = Skill.forId(skill);
					Player player = (Player) target;
					int lvl = player.getSkillManager().getCurrentLevel(skillT);
					lvl -= 5 + Misc.getRandom(6);
					player.getSkillManager().setCurrentLevel(skillT,
							player.getSkillManager().getCurrentLevel(skillT) - lvl <= 0 ? 1 : lvl);
					target.getPacketSender()
							.sendMessage("Your " + skillT.getFormatName() + " has been slighly drained!");
					stop();
					new Projectile(cB, victim, 1901, 44, 3, 43, 31, 0).sendProjectile();
					cB.setChargingAttack(false).getCombatBuilder()
							.setAttackTimer(cB.getDefinition().getAttackSpeed() - 1);
					stop();
				}
			});
		} else {
			cB.performAnimation(anim3);
			victim.performGraphic(gfx2);
			cB.getCombatBuilder().setContainer(new CombatContainer(cB, victim, 1, 2, CombatType.MAGIC, true));
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

	/// private static final Animation anim2 = new Animation(12696);
	// private static final Animation anim3 = new Animation(12698);
	// private static final Graphic gfx2 = new Graphic(2281, GraphicHeight.LOW);
	// private static final Graphic gfx3 = new Graphic(369, GraphicHeight.LOW);
}
