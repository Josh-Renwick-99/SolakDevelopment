package com.ruse.world.content.combat.strategy.impl;


import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Animation;
import com.ruse.model.Locations;
import com.ruse.model.Projectile;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;


public class SimpleScript implements CombatStrategy {

    @Override
    public boolean canAttack(Character entity, Character victim) {
        return victim.isPlayer();
    }

    @Override
    public CombatContainer attack(Character entity, Character victim) {
        return null;
    }

    @Override
    public boolean customContainerAttack(Character entity, Character victim) {
        NPC npc = (NPC) entity;
        if (victim.getConstitution() <= 0) {
            return true;
        }
        if (npc.isChargingAttack()) {
            return true;
        }
        if (!victim.isPlayer()) {
            return true;
        }
        Player player = (Player) victim;

        if (Locations.goodDistance(npc.copy(), victim.copy(), 1)) {
            int x = Misc.random(9);
            if (x <= 2) {
                meleeAttack(npc, player);
            } else if (x <= 6) {
                magicAttack(npc, player);
            } else {
                rangedAttack(npc, player);
            }
        } else {
            if (Misc.getRandom(1) == 0)
                rangedAttack(npc, player);
            else
                magicAttack(npc, player);
        }

        return true;
    }


    public void magicAttack(NPC attacker, Character victim) {
        attacker.setChargingAttack(true);
        attacker.performAnimation(new Animation(attacker.getDefinition().getAttackAnimation()));
        attacker.getCombatBuilder().setContainer(new CombatContainer(attacker, victim, 1, 3, CombatType.MAGIC, true));
        TaskManager.submit(new Task(1, attacker, false) {
            int tick = 0;

            @Override
            protected void execute() {
                if (tick == 0) {
                    new Projectile(attacker, victim, 1067, 44, 1, 43, 43, 0).sendProjectile();
                }
                if (tick == 2) {
                    attacker.setChargingAttack(false);
                    stop();
                }
                tick++;

            }

        });
    }

    private void rangedAttack(NPC npc, Player player) {
        npc.setChargingAttack(true);
        npc.performAnimation(new Animation(npc.getDefinition().getAttackAnimation()));
        npc.getCombatBuilder().setContainer(new CombatContainer(npc, player, 1, 3, CombatType.RANGED, true));
        TaskManager.submit(new Task(1, npc, false) {
            int tick = 0;

            @Override
            public void execute() {
                if (tick == 0) {
                    for (final Player near : Misc.getCombinedPlayerList(player)) {
                        if (near == null || near.isTeleporting())
                            continue;
                        if (near.getPosition().distanceToPoint(npc.getPosition().getX(), npc.getPosition().getY()) > 20)
                            continue;
                        new Projectile(npc, near, 570, 44, 3, 41, 43, 0).sendProjectile();
                    }

                } else if (tick == 2) {
                    npc.setChargingAttack(false);
                    stop();
                }
                tick++;
            }
        });
    }

    private void meleeAttack(NPC npc, Player player) {
        npc.performAnimation(new Animation(npc.getDefinition().getAttackAnimation()));
        npc.setChargingAttack(true);
		npc.getCombatBuilder().setContainer(new CombatContainer(npc, player, 1, 1, CombatType.MELEE, true));

        TaskManager.submit(new Task(1, npc, false) {
            int tick = 0;

            @Override
            public void execute() {
                if (tick == 2) {
                    npc.setChargingAttack(false);
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
        return 15;
    }

    @Override
    public CombatType getCombatType() {
        return CombatType.MIXED;
    }

}