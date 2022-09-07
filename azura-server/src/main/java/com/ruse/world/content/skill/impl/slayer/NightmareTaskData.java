package com.ruse.world.content.skill.impl.slayer;

public class NightmareTaskData {

    private NightmareTasks task;
    private int slayerTaskAmount = 0;

    public NightmareTaskData(NightmareTasks task, int slayerTaskAmount) {
        this.task = task;
        this.slayerTaskAmount = slayerTaskAmount;
    }

    public NightmareTasks getTask() {
        return task;
    }

    public void setTask(NightmareTasks task) {
        this.task = task;
    }

    public int getSlayerTaskAmount() {
        return slayerTaskAmount;
    }

    public void setSlayerTaskAmount(int slayerTaskAmount) {
        this.slayerTaskAmount = slayerTaskAmount;
    }
}
