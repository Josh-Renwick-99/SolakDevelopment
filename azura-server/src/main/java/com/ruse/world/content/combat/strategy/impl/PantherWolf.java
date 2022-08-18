package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class PantherWolf implements CombatStrategy {

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
        NPC wolf = (NPC)entity;
        if(wolf.isChargingAttack() || victim.getConstitution() <= 0) {
            return true;
        }
        wolf.performAnimation(new Animation(wolf.getDefinition().getAttackAnimation()));
        wolf.getCombatBuilder().setContainer(new CombatContainer(wolf, victim, 1, 1, CombatType.MELEE, false));
        return false;
    }

    @Override
    public int attackDelay(Character entity) {
        return entity.getAttackSpeed();
    }

    @Override
    public int attackDistance(Character entity) {
        return 1;
    }

    @Override
    public CombatType getCombatType() {
        return null;
    }
}