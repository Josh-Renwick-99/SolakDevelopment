package com.ruse.world.entity.impl.npc.impl.HoVMobs.HoVStrategies;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatContainer;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.prayer.PrayerHandler;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class HeimdallCombatStrategy implements CombatStrategy {
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
        NPC heimdall = (NPC) entity;
        int chanceOnSpec = Misc.getRandom(10);

        if(heimdall.isChargingAttack()) {
            return false;
        }

        if(chanceOnSpec == 1) {
            attack(heimdall, (Player) victim, true);
            return true;
        }

        attack(heimdall, (Player) victim, false);
        return true;
    }

    private void attack(NPC heimdall, Player player, Boolean special) {
        if(heimdall.isChargingAttack()) {
            return;
        }

        //Activate the special that fucks people's prayers.
        if(special) {
            heimdall.setChargingAttack(true);
            TaskManager.submit(new Task(1, heimdall, false) {
                int tick = 0;

                @Override
                public void execute() {
                    if (tick == 1) {
                        heimdall.forceChat("Perish, fool!");
                    } else if (tick == 2) {
                        heimdall.performAnimation(heimdall.getAnimation());
                        heimdall.getCombatBuilder().setContainer(new CombatContainer(heimdall, player, 1, 0, CombatType.MELEE, false));
                        PrayerHandler.deactivateAll(player);
                    } else if(tick == 4) {
                        heimdall.setChargingAttack(false);
                        stop();
                    }
                    tick++;
                }
            });
        } else {
            heimdall.performAnimation(heimdall.getAnimation());
            heimdall.getCombatBuilder().setContainer(new CombatContainer(heimdall, player, 1, 0, CombatType.MELEE, false));
        }
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
        return CombatType.MELEE;
    }
}
