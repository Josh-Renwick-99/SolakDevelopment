package com.ruse.world.entity.impl.npc.impl.HoVMobs;

import com.ruse.model.Position;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.content.combat.strategy.impl.SimpleScript;
import com.ruse.world.entity.impl.npc.NPC;

public class Skreeg extends NPC {

    public Skreeg(int id, Position position) {
        super(id, position);
        // Activate Melee Prayer for Fenrir
        this.setPrayerActive(18, true);
    }

    @Override
    public CombatStrategy determineStrategy() {
        return new SimpleScript();
    }
}
