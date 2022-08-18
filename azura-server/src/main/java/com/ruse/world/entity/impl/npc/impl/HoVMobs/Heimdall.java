package com.ruse.world.entity.impl.npc.impl.HoVMobs;

import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.content.combat.CombatType;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.content.combat.strategy.impl.SimpleScript;
import com.ruse.world.entity.impl.npc.NPC;

public class Heimdall extends NPC {

    public CombatType avoiding;

    public Heimdall(int id, Position position) {
        super(id, position);
        this.avoiding = toAvoid();
    }

    @Override
    public CombatStrategy determineStrategy() {
        return new SimpleScript();
    }

    /**
     * Determines which Combat Type to neglect on spawn.
     * @return
     */
    private CombatType toAvoid() {
        int avoiding = Misc.getRandom(0, 2);
        if(avoiding == 0) {
            return CombatType.MELEE;
        } else if (avoiding == 1) {
            return CombatType.RANGED;
        } else {
            return CombatType.MAGIC;
        }
    }
}
