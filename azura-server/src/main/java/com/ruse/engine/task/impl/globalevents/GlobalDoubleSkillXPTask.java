package com.ruse.engine.task.impl.globalevents;

import com.ruse.GameSettings;
import com.ruse.engine.task.Task;
import com.ruse.world.World;

public class GlobalDoubleSkillXPTask extends Task {


    public GlobalDoubleSkillXPTask() {
        super(1, 0, true);
    }

    int timer = 0;

    @Override
    protected void execute() {
        if (timer < 1) {
            timer = 6000;
            World.sendMessage("2X Skilling EXP has begun for 60 minutes!");
            GameSettings.DOUBLE_SKILL_EXP = true;
        } else if (timer == 1) {
            World.sendMessage("2X Skilling EXP has Ended!");
            GameSettings.DOUBLE_SKILL_EXP = false;
            stop();
        } else {
            timer--;
        }
    }
}