package com.ruse.world.entity.impl.npc.impl.HoVMobs;

import com.ruse.model.Position;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.content.combat.strategy.impl.SimpleScript;
import com.ruse.world.entity.impl.npc.NPC;

public class Orix extends NPC {

    public Orix(int id, Position position) {
        super(id, position);
        //Activate Ranged Prayers for Green Fenrir
        this.setPrayerActive(17, true);
    }

    @Override
    public CombatStrategy determineStrategy() {
        return new SimpleScript();
    }
}
