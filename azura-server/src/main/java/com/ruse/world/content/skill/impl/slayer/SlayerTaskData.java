package com.ruse.world.content.skill.impl.slayer;

public class SlayerTaskData {
    private SlayerTasks task;
    private int slayerTaskAmount = 0;
    public SlayerTaskData(SlayerTasks task, int slayerTaskAmount) {
        this.task = task;
        this.slayerTaskAmount = slayerTaskAmount;
    }

    public SlayerTasks getTask() {
        return task;
    }

    public void setTask(SlayerTasks task) {
        this.task = task;
    }

    public int getSlayerTaskAmount() {
        return slayerTaskAmount;
    }

    public void setSlayerTaskAmount(int slayerTaskAmount) {
        this.slayerTaskAmount = slayerTaskAmount;
    }
}
