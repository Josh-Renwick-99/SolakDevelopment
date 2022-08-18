package com.ruse.world.content.combat.strategy.impl;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.*;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;


public class CrazyWitch implements CombatStrategy {

    private static final Animation MAGIC = new Animation(5498);
    private static final Animation MELEE = new Animation(9449);
    private static final Animation SCREAM = new Animation(9382);
    private static final Graphic DRAIN = new Graphic(524);


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
        NPC witch = (NPC) entity;

        if (witch.isChargingAttack() || witch.getConstitution() <= 0 || victim.getConstitution() <= 0) {
            return true;
        }
        int rng = Misc.getRandom(100);

        if (Locations.goodDistance(witch.copy(), victim.copy(), 1)) {
            if (rng >= 90) {
                drainAttack(witch, victim);
            } else if (rng >= 83) {
                meleeAttack(witch, victim);
            } else {
                magicAttack(witch, victim);
            }
        } else {
            if (rng >= 85) {
                drainAttack(witch, victim);
            } else {
                magicAttack(witch, victim);
            }
        }
        return true;
    }


    private void magicAttack(NPC npc, Character victim) {
        npc.getCombatBuilder().setContainer(new CombatContainer(npc, victim, 1, 2, CombatType.MAGIC, true));
        new Projectile(npc, victim, 866, 50, 35, 20, 28, 0).sendProjectile();
        npc.performAnimation(MAGIC);
        npc.setChargingAttack(true);
        TaskManager.submit(new Task(2, npc, false) {
            @Override
            public void execute() {
                victim.performGraphic(new Graphic(865));
                npc.setChargingAttack(false);
                stop();
            }
        });
    }
    private void drainAttack(NPC npc, Character victim) {
        npc.setChargingAttack(true);
        npc.performAnimation(SCREAM);
        TaskManager.submit(new Task(1, npc, false) {
            Player target = (Player) victim;
            int tick = 0;
            int selectedDrain = 0;
            final String[] names = {"melee", "magic", "range"};

            @Override
            public void execute() {
                if (tick == 0) {
                    selectedDrain = Misc.random(0, 2);
                    npc.forceChat("Your " + names[selectedDrain] + " is your weakness!");
                    target.sendMessage("If you hurry you can protect yourself from the witches spell!");
                } else if(tick == 6) {
                    int drain = Misc.getRandom(15);
                    if(selectedDrain == 0 && !PrayerHandler.isActivated(target, PrayerHandler.PROTECT_FROM_MELEE)) {
                        target.getSkillManager().setCurrentLevel(Skill.ATTACK, (target.getSkillManager().getCurrentLevel(Skill.ATTACK) - drain));
                        target.getSkillManager().setCurrentLevel(Skill.STRENGTH, (target.getSkillManager().getCurrentLevel(Skill.STRENGTH) - drain));
                        target.getSkillManager().setCurrentLevel(Skill.DEFENCE, (target.getSkillManager().getCurrentLevel(Skill.DEFENCE) - drain));
                        target.performGraphic(DRAIN);
                        target.sendMessage("Your melee stats have been weakened by the crazy witch!");
                    } else if(selectedDrain == 1 && !PrayerHandler.isActivated(target, PrayerHandler.PROTECT_FROM_MAGIC)) {
                        target.getSkillManager().setCurrentLevel(Skill.MAGIC, (target.getSkillManager().getCurrentLevel(Skill.MAGIC) - drain));
                        target.performGraphic(DRAIN);
                        target.sendMessage("Your magic has been weakened by the crazy witch!");
                    } else if(selectedDrain == 2 && !PrayerHandler.isActivated(target, PrayerHandler.PROTECT_FROM_MISSILES)) {
                        target.getSkillManager().setCurrentLevel(Skill.RANGED, (target.getSkillManager().getCurrentLevel(Skill.RANGED) - drain));
                        target.performGraphic(DRAIN);
                        target.sendMessage("Your range has been weakened by the crazy witch!");
                    }
                } else if (tick == 7) {
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




