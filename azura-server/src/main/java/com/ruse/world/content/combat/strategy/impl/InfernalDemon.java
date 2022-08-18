package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.prayer.CurseHandler;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;




public class InfernalDemon implements CombatStrategy {

    private static final Animation MAGIC = new Animation(14971);
    private static final Animation MELEE = new Animation(14963);
    private static final Animation SCREAM = new Animation(14961);
    private static final Animation SUMMON = new Animation(14962);


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
        NPC demon = (NPC) entity;

        if (demon.isChargingAttack() || demon.getConstitution() <= 0 || victim.getConstitution() <= 0) {
            return true;
        }
        int rng = Misc.getRandom(100);

        if (Locations.goodDistance(demon.copy(), victim.copy(), 1)) {
            if (rng >= 90) {
                screamAttack(demon, victim);
            } else if (rng >= 87) {
                summonAttack(demon, victim);
            } else if (rng >= 83) {
                meleeAttack(demon, victim);
            } else {
                magicAttack(demon, victim);
            }
        } else {
            if (rng >= 85) {
                screamAttack(demon, victim);
            } else if (rng >= 81) {
                summonAttack(demon, victim);
            } else {
                magicAttack(demon, victim);
            }
        }
        return true;
    }


    private void magicAttack(NPC npc, Character victim) {
        npc.setChargingAttack(true);
        npc.performAnimation(MAGIC);
        npc.getCombatBuilder().setContainer(new CombatContainer(npc, victim, 1, 3, CombatType.MAGIC, true));
        TaskManager.submit(new Task(1, npc, false) {
            int tick = 0;

            @Override
            public void execute() {
                if (tick == 0) {
                    new Projectile(npc, victim, 393, 50, 35, 20, 28, 0).sendProjectile();
                } else if (tick == 1) {
                    npc.setChargingAttack(false);
                    stop();
                }
                tick++;
            }
        });
    }

    private void summonAttack(NPC npc, Character victim) {
        if (!npc.isAttackable()) {
            magicAttack(npc, victim);
            return;
        }
        magicAttack(npc, victim);
        /*npc.setChargingAttack(true);
        npc.setAttackable(false);
        npc.performAnimation(SUMMON);
        NPC demon1 = new NPC(83, npc.getPosition().transform(-1, 0, 0));
        NPC demon2 = new NPC(83, npc.getPosition().transform(1, 0, 0));
        NPC demon3 = new NPC(83, npc.getPosition().transform(0, -1, 0));

        TaskManager.submit(new Task(3, npc, false) {
            int tick = 0;

            @Override
            public void execute() {
                if (demon1.getConstitution() <= 0 && demon2.getConstitution() <= 0 &&
                        demon3.getConstitution() <= 0) {
                    npc.setAttackable(true);
                    stop();
                }
                if (tick == 0) {
                    npc.forceChat("Rise my children!");
                    World.register(demon1);
                    World.register(demon2);
                    World.register(demon3);
                    demon1.getCombatBuilder().attack(victim);
                    demon2.getCombatBuilder().attack(victim);
                    demon3.getCombatBuilder().attack(victim);
                } else if (tick == 1) {
                    npc.setChargingAttack(false);
                }
                tick++;
            }
        });*/
    }


    private void screamAttack(NPC npc, Character victim) {
        npc.setChargingAttack(true);
        npc.performAnimation(SCREAM);
        //npc.getCombatBuilder().setContainer(new CombatContainer(npc, victim, 1, 3, CombatType.MAGIC, true));
        TaskManager.submit(new Task(1, npc, false) {
            Player target = (Player) victim;
            int tick = 0;

            @Override
            public void execute() {
                if (tick == 0) {

                    if(Locations.goodDistance(target, npc, 10)) {
                        npc.forceChat("Your gods wont help you here!!");
                        target.sendMessage("Infernal demon has knocked your prayers down!");
                        CurseHandler.deactivateAll(target);
                        PrayerHandler.deactivateAll(target);
                        target.performGraphic(new Graphic(481));
                        target.dealDamage(new Hit(target.getConstitution() / 3, Hitmask.RED, CombatIcon.MAGIC));
                        //new Projectile(npc, victim, 393, 50, 35, 20, 28, 0).sendProjectile();
                    }
                } else if (tick == 1) {
                    npc.setChargingAttack(false);
                    stop();
                }
                tick++;
            }
        });
    }


    private void meleeAttack(NPC npc, Character victim) {
        npc.setChargingAttack(true);
        npc.performAnimation(MELEE);
        npc.getCombatBuilder().setContainer(new CombatContainer(npc, victim, 1, 1, CombatType.MELEE, true));
        npc.setChargingAttack(false);
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




