package com.ruse.world.content.skill.impl.slayer;

import com.ruse.model.Skill;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.world.World;
import com.ruse.world.content.KillsTracker;
import com.ruse.world.content.NpcRequirements;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;

public class NightmareSlayer {

    Player player;
    private NightmareTasks slayerTask = NightmareTasks.NO_TASK;
    private NightmareTasks lastTask = NightmareTasks.NO_TASK;
    private NightmareSlayerMaster slayerMaster = NightmareSlayerMaster.NIGHTMARE_SLAYER;
    private int amountToKill;
    private int taskStreak;


    public NightmareSlayer(Player player){
        this.player = player;
    }

    public void assignTask() {
        boolean hasTask = getNightmareTask() != NightmareTasks.NO_TASK && player.getNightmareSlayer().getLastTask() != getNightmareTask();
        if (hasTask) {
            player.getPacketSender().sendInterfaceRemoval();
            return;
        }
        NightmareTaskData taskData = NightmareTasks.getNewTaskData(slayerMaster);
        int slayerTaskAmount = taskData.getSlayerTaskAmount();
        NightmareTasks taskToSet = taskData.getTask();
        if (taskToSet == player.getNightmareSlayer().getLastTask() ||
                NpcDefinition.forId(taskToSet.getNpcId()).getSlayerLevel() > player.getSkillManager().getMaxLevel(Skill.SLAYER)) {
            assignTask();
            return;
        }
        for (NpcRequirements req : NpcRequirements.values()) {
            if (taskToSet.getNpcId() == req.getNpcId()) {
                if (req.getKillCount() > 0) {
                    if (player.getPointsHandler().getNPCKILLCount() < req.getKillCount()) {
                        assignTask();
                        return;
                    }
                } else {
                    int total = KillsTracker.getTotalKillsForNpc(req.getRequireNpcId(), player);
                    if (total < req.getAmountRequired()) {
                        assignTask();
                        return;
                    }
                }
                break;

            }
        }

        player.getPacketSender().sendInterfaceRemoval();
        this.amountToKill = slayerTaskAmount;
        this.slayerTask = taskToSet;
        DialogueManager.start(player, SlayerDialogues.receivedNightmareTask(player, getNightmareMaster(), getNightmareTask()));
        PlayerPanel.refreshPanel(player);
    }

    public NightmareTasks getNightmareTask() {
        return slayerTask;
    }

    public NightmareSlayer setNightmareTask(NightmareTasks slayerTask) {
        this.slayerTask = slayerTask;
        return this;
    }

    public int getTaskStreak() {
        return this.taskStreak;
    }

    public NightmareSlayer setTaskStreak(int taskStreak) {
        this.taskStreak = taskStreak;
        return this;
    }

    public NightmareTasks getLastTask() {
        return this.lastTask;
    }

    public void setLastTask(NightmareTasks lastTask) {
        this.lastTask = lastTask;
    }

    public NightmareSlayerMaster getNightmareMaster() {
        return slayerMaster;
    }


    public void setNightmareMaster(NightmareSlayerMaster master) {
        this.slayerMaster = master;
    }
}
