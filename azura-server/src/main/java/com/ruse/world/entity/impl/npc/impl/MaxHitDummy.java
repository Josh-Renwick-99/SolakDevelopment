package com.ruse.world.entity.impl.npc.impl;

import com.ruse.model.Hit;
import com.ruse.model.Position;
import com.ruse.world.content.combat.strategy.CombatStrategies;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.Entity;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;

public class MaxHitDummy extends NPC {

	public MaxHitDummy(int id, Position position) {
		super(id, position);
		this.getMovementQueue().setLockMovement(true);
		maxHitDummy = true;
	}

	@Override
	public Character setPositionToFace(Position positionToFace) {
		return this;
	}

	@Override
	public Character setEntityInteraction(Entity entity) {
		return this;
	}

	@Override
	public Hit decrementHealth(Hit hit) {
		return hit;
	}

	@Override
	public CombatStrategy determineStrategy() {
		return CombatStrategies.getEmptyCombatStrategy();
	}

	@Override
	public void setPoisonDamage(int poisonDamage) {
	}
}
