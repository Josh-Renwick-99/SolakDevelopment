package com.ruse.world.content.combat.strategy;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.model.projectile.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

/**
 * Handles NAZASTAROOL Combat strategy
 * 
 * @author 2012 <https://www.rune-server.ee/members/dexter+morgan/>
 *
 */
public class NazastaroolCombatStrategy implements CombatStrategy {

	/**
	 * Attacking melee
	 */
	private static final Animation MELEE = new Animation(4959);
	
		/**
	 * Attacking mage
	 */
	private static final Animation MAGIC = new Animation(4967);

	/**
	 * The NAZASTAROOL npc id
	 */
	public static final int NAZASTAROOL = 20355;

	/**
	 * The attack number
	 */
	private int attack;

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
		NPC nazastarool = (NPC) entity;
		if (nazastarool.isChargingAttack() || nazastarool.getConstitution() <= 0) {
			nazastarool.getCombatBuilder().setAttackTimer(4);
			return true;
		}
		attack = 0;
		if (attack == 7) {
			Position pos = victim.getPosition().copy();
			nazastarool.setChargingAttack(true).getCombatBuilder().setAttackTimer(6);
			new Projectile(nazastarool, victim, 1741, 44, 3, 43, 43, 0).sendProjectile();
			TaskManager.submit(new Task(4, nazastarool, false) {

				@Override
				public void execute() {
					if (victim.getPosition().sameAs(pos)) {
						nazastarool.getCombatBuilder()
						.setContainer(new CombatContainer(nazastarool, victim, 1, 1, CombatType.MAGIC, true));
					}
					nazastarool.setChargingAttack(false);
					stop();
				}
			});
			attack = 0;
		} else {
			if (Locations.goodDistance(nazastarool.getPosition().copy(), victim.getPosition().copy(), 1)
					&& Misc.getRandom(5) <= 3) {
				//new Projectile(nazastarool, victim, 333 , 44, 3, 43, 43, 0).sendProjectile();

				nazastarool.getCombatBuilder().setContainer(new CombatContainer(nazastarool, victim, 1, 1, CombatType.MELEE, true));
				nazastarool.getCombatBuilder().setContainer(new CombatContainer(nazastarool, victim, 1, 1, CombatType.MELEE, true));

				nazastarool.getCombatBuilder().setContainer(new CombatContainer(nazastarool, victim, 1, 1, CombatType.MELEE, true));

				nazastarool.performAnimation(MELEE);
			} 
			else {
				new Projectile(nazastarool, victim, 1830 , 44, 3, 43, 43, 0).sendProjectile();
				nazastarool.getCombatBuilder()
						.setContainer(new CombatContainer(nazastarool, victim, 1, 1, CombatType.MAGIC, true));
				nazastarool.performAnimation(MAGIC);
			}
			
		}
		attack++;
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
}
