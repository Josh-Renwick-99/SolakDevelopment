package com.ruse.world.entity.impl.npc.impl.HoVMobs;

import com.ruse.model.Position;
import com.ruse.world.content.combat.strategy.CombatStrategy;
import com.ruse.world.content.combat.strategy.impl.SimpleScript;
import com.ruse.world.entity.impl.npc.NPC;

public class CrystalOrc extends NPC {

    public CrystalOrc(int id, Position position) {
        super(id, position);
        //Activate Mage Prayers for Red Fenrir
        this.setPrayerActive(16, true);
    }
    @Override
    public CombatStrategy determineStrategy() {
        return new SimpleScript();
    }
}
