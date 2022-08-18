package com.ruse.engine.task.impl.globalevents;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.world.World;

public class GlobalDoubleKoLTask extends Task {

    public GlobalDoubleKoLTask() {
        super(1, 0, true);
    }

    int timer = 0;

    @Override
    protected void execute() {
        if (timer < 1) {
            timer = 6000;
            World.sendMessage("Keepers of the Light 2x tickets event has started for 60 minutes!");
            GameSettings.DOUBLE_KOL = true;
        } else if(timer == 1) {
            World.sendMessage("Keepers of the Light 2x tickets event has ended!");
            GameSettings.DOUBLE_KOL = false;
            stop();
        } else {
            timer--;
        }
    }
}
