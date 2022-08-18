package com.ruse.engine.task.impl.globalevents;

import com.ruse.engine.task.Task;
import com.ruse.model.Position;
import com.ruse.world.World;
import com.ruse.world.entity.impl.npc.NPC;

public class GlobalEventBossTask extends Task {

    /**
     * Variables
     */
    public static final int eventBossID = 5;
    public static final Position eventBossPosition = new Position(0, 0, 0);
    private NPC eventBoss;

    public GlobalEventBossTask() {
        super(1,0, true);
    }

    @Override
    protected void execute() {
        if(!World.getNpcs().contains(eventBoss)) {
            eventBoss = new NPC(eventBossID, eventBossPosition);
            World.sendMessage("Event Boss has Spawned!");
        } else {
            System.out.println("Already spawned.");
            stop();
        }
    }
}
