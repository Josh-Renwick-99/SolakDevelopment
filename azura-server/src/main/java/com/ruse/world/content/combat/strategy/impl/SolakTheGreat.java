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

public class SolakTheGreat implements CombatStrategy {

    private boolean[] specialAttacks = new boolean[3];
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
        NPC solak = (NPC)entity;
        if(solak.isChargingAttack() || victim.getConstitution() <= 0) {
            return true;
        }
        double total = (double) solak.getConstitution() / (double) solak.getDefaultConstitution() * 100;
        if(total <= 75 && !specialAttacks[0]) {
            specialAttack(victim, solak);
            specialAttacks[0] = true;
        } else if(total <= 50 && !specialAttacks[1]) {
            specialAttack(victim, solak);
            specialAttacks[1] = true;
        } else if(total <= 25 && !specialAttacks[2]) {
            specialAttack(victim, solak);
            specialAttacks[2] = true;
        }
        regularAttack(victim, solak);
        return true;
    }

    public void regularAttack(Character victim, NPC solak) {
        solak.performAnimation(new Animation(solak.getDefinition().getAttackAnimation()));
        //solak.setChargingAttack(false).getCombatBuilder().setAttackTimer(attackDelay(solak) - 2);
        for (Player p : Misc.getCombinedPlayerList((Player) victim)) {
            if (p != null) {
                boolean mage = Misc.getRandom(10) <= 7;
                new Projectile(solak, p, mage ? 1658 : 1017, 44, 3, 43, 43, 0).sendProjectile();
                solak.getCombatBuilder().setContainer(new CombatContainer(solak, p, 1, mage ? 3 : 2,
                        mage ? CombatType.MAGIC : CombatType.RANGED, true));
            }
        }
    }

    public void specialAttack(Character victim, NPC boss) {
        //boss.setChargingAttack(true);
        boss.performAnimation(new Animation(boss.getDefinition().getAttackAnimation()));
        boss.forceChat("I am the conqerour of this world.");
        TaskManager.submit(new Task(1, boss, false) {
            Player target = (Player) victim;
            int tick = 0;

            @Override
            public void execute() {// every 2 ticks for 5 seconds
                if (tick < 10 && tick % 2 != 0) {
                    // try using boss.getPossibleTargets()
                    for (Player t : Misc.getCombinedPlayerList(target)) {
                        if (t == null || !Locations.goodDistance(t, boss, 50))
                            continue;

                        t.performGraphic(new Graphic(481));
                        t.dealDamage(new Hit(500, Hitmask.RED, CombatIcon.MAGIC));

                    }

                } else if (tick == 11) {
                    //boss.setChargingAttack(false);
                    stop();
                }
                tick++;
            }
        });
    }

    @Override
    public int attackDelay(Character entity) {
        return entity.getAttackSpeed();
    }

    @Override
    public int attackDistance(Character entity) {
        return 50;
    }

    @Override
    public CombatType getCombatType() {
        return CombatType.MIXED;
    }
}
