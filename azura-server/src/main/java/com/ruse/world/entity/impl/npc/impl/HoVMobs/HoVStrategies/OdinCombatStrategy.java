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

public class OdinCombatStrategy implements CombatStrategy {
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
        NPC odin = (NPC) entity;
        int chanceOnSpec = Misc.getRandom(10);

        if(odin.isChargingAttack()) {
            return false;
        }

        if(chanceOnSpec == 1) {
            attack(odin, (Player) victim, true);
            return true;
        }

        attack(odin, (Player) victim, false);
        return true;
    }

    private void attack(NPC odin, Player player, Boolean special) {
        if(odin.isChargingAttack()) {
            return;
        }

        //Activate the special that fucks people's prayers.
        if(special) {
            odin.setChargingAttack(true);
            TaskManager.submit(new Task(1, odin, false) {
                int tick = 0;

                @Override
                public void execute() {
                    if (tick == 1) {
                        odin.forceChat("Perish, fool!");
                    } else if (tick == 2) {
                        odin.performAnimation(odin.getAnimation());
                        odin.getCombatBuilder().setContainer(new CombatContainer(odin, player, 1, 0, CombatType.MELEE, false));
                        PrayerHandler.deactivateAll(player);
                    } else if(tick == 4) {
                        odin.setChargingAttack(false);
                        stop();
                    }
                    tick++;
                }
            });
        } else {
            odin.performAnimation(odin.getAnimation());
            odin.getCombatBuilder().setContainer(new CombatContainer(odin, player, 1, 0, CombatType.MELEE, false));
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
