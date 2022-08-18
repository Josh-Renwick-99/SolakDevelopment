package com.ruse.world.content.combat.strategy.impl;

import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;

public class EmptyCombatStrategy implements CombatStrategy {
    @Override
    public boolean canAttack(Character entity, Character victim) {
        return false;
    }

    @Override
    public CombatContainer attack(Character entity, Character victim) {
        return null;
    }

    @Override
    public boolean customContainerAttack(Character entity, Character victim) {
        return false;
    }

    @Override
    public int attackDelay(Character entity) {
        return 0;
    }

    @Override
    public int attackDistance(Character entity) {
        return 0;
    }

    @Override
    public CombatType getCombatType() {
        return null;
    }
}
