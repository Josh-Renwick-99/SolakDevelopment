package com.ruse.engine.task.impl.globalevents;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.world.World;

public class GlobalDoubleSlayerTask extends Task {

    public GlobalDoubleSlayerTask() {
        super(1, 0, true);
    }

    int timer = 0;

    @Override
    protected void execute() {
        if (timer < 1) {
            timer = 6000;
            World.sendMessage("2X Slayer Tickets on Task Completion Event has Begun for 60 minutes!");
            GameSettings.DOUBLE_SLAYER = true;
        } else if(timer == 1) {
            World.sendMessage("2X Slayer Tickets on Task Completion Event has Ended!");
            GameSettings.DOUBLE_SLAYER = false;
            stop();
        } else {
            timer--;
        }
    }
}
