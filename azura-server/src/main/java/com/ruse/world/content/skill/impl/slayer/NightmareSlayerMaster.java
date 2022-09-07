package com.ruse.world.content.skill.impl.slayer;

import com.ruse.model.Position;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.world.entity.impl.player.Player;

public enum NightmareSlayerMaster {

    NIGHTMARE_SLAYER(80, 9085, new Position(2072, 4455));

    private NightmareSlayerMaster(int slayerReq, int npcId, Position telePosition) {
        this.slayerReq = slayerReq;
        this.npcId = npcId;
        this.position = telePosition;
    }

    private int slayerReq;
    private int npcId;
    private Position position;

    public int getSlayerReq() {
        return this.slayerReq;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public Position getPosition() {
        return this.position;
    }

    public String getSlayerMasterName() {
        String name = "";
        NpcDefinition def = NpcDefinition.forId(getNpcId());
        if(def != null && def.getName() != null) {
            name = def.getName();
        }
        return name;
    }

}
