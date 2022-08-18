package com.ruse.world.instance.impl;

import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.Character;
import com.ruse.world.instance.DestroyMode;
import com.ruse.world.instance.MinigameInstance;

public final class DungeoneeringInstance extends MinigameInstance {

    /**
     * Constructs a new {@link com.ruse.world.instance.TestInstance} type object instance.
     */
    public DungeoneeringInstance() {
        super(DestroyMode.AUTO_ON_EMPTY, 280, 624, 8, 8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createNpcs() {
        for(int i = 0; i < Dungeoneering.Constants.KILLS_REQUIRED; i++) {
            createNpc(Dungeoneering.Constants.MINION_MOB, 32+ Misc.random(20), 12+ Misc.random(12), 0, 3);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Position getTeleportPosition(Player player) {
        return toPosition(45, 21);
    }

    @Override
    public Position getLobbyPosition(Player player) {
        return Dungeoneering.Constants.INSTANCE.getLOBBY();
    }

    @Override
    public boolean isMultiArea(Character p) { return true; }
}
