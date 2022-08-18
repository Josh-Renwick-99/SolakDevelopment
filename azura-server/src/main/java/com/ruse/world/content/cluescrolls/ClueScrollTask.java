package com.ruse.world.content.cluescrolls;

import com.ruse.model.Skill;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.KillsTracker;
import com.ruse.world.content.NpcRequirements;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.skill.impl.slayer.*;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;

public class ClueScrollTask {

    public ClueScrollTask(Player p) {
        this.player = p;
    }

    public SlayerTasks getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(SlayerTasks currentTask) {
        this.currentTask = currentTask;
    }

    public int getAmountToSlay() {
        return amountToSlay;
    }

    public void setAmountToSlay(int amountToSlay) {
        this.amountToSlay = amountToSlay;
    }

    SlayerTasks currentTask = SlayerTasks.NO_TASK;

    public SlayerTasks getLastTask() {
        return lastTask;
    }

    public void setLastTask(SlayerTasks lastTask) {
        this.lastTask = lastTask;
    }

    SlayerTasks lastTask = SlayerTasks.NO_TASK;
    Player player;
    int amountToSlay = 0;

    public void assignTask(ClueScroll difficulty) {
        boolean hasTask = currentTask != SlayerTasks.NO_TASK && lastTask != currentTask;
        if (hasTask) {
            player.sendMessage("You already have a clue task!");
            return;
        }

        SlayerMaster master = SlayerMaster.EASY_SLAYER;

        if(difficulty == ClueScroll.MEDIUM) {
            master = SlayerMaster.MEDIUM_SLAYER;
        } else if(difficulty == ClueScroll.HARD) {
            master = SlayerMaster.HARD_SLAYER;
        }
        SlayerTaskData taskData = getNewTaskData(master);
        int slayerTaskAmount = taskData.getSlayerTaskAmount();
        SlayerTasks taskToSet = taskData.getTask();

        if (taskToSet == lastTask || NpcDefinition.forId(taskToSet.getNpcId())
                .getSlayerLevel() > player.getSkillManager().getMaxLevel(Skill.SLAYER)) {
            assignTask(difficulty);
            return;
        }
        for (NpcRequirements req : NpcRequirements.values()) {
            if (taskToSet.getNpcId() == req.getNpcId()) {
                if (req.getKillCount() > 0) {
                    if (player.getPointsHandler().getNPCKILLCount() < req.getKillCount()) {
                        assignTask(difficulty);
                        return;
                    }
                } else {
                    int total = KillsTracker.getTotalKillsForNpc(req.getRequireNpcId(), player);
                    if (total < req.getAmountRequired()) {
                        assignTask(difficulty);
                        return;
                    }
                }
                break;

            }
        }

        player.getPacketSender().sendInterfaceRemoval();
        this.amountToSlay = slayerTaskAmount;
        this.currentTask = taskToSet;
    }

    public static SlayerTaskData getNewTaskData(SlayerMaster master) {
        int slayerTaskAmount = 20;
        ArrayList<SlayerTasks> possibleTasks = SlayerTasks.tasks.get(master);
        SlayerTasks task = possibleTasks.get(Misc.getRandom(possibleTasks.size() - 1));


        /*
         * Getting a task
         */
        if (master == SlayerMaster.EASY_SLAYER) {
            slayerTaskAmount = 10 + Misc.random(15);
        } else if (master == SlayerMaster.MEDIUM_SLAYER) {
            slayerTaskAmount = 25 + Misc.random(10);
        } else if (master == SlayerMaster.HARD_SLAYER) {
            slayerTaskAmount = 50 + Misc.random(25);
        }
        return new SlayerTaskData(task, slayerTaskAmount);
    }
}
