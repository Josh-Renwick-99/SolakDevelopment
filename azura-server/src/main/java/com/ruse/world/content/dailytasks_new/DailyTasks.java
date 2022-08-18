package com.ruse.world.content.dailytasks_new;

import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DailyTasks {

    private static final int MAX_DAILIES = 3;

    public static void checkTask(Player player) {
        val tasks = player.dailies;
        if (player.dailiesCompleted >= 3) {
            if (System.currentTimeMillis() - player.taskReceivedTime[0] >= 86_400_000) {
                player.dailiesCompleted--;
                player.taskReceivedTime[0] = -1;
                checkTask(player);
                return;
            }
            if (System.currentTimeMillis() - player.taskReceivedTime[1] >= 86_400_000) {
                player.dailiesCompleted--;
                player.taskReceivedTime[1] = -1;
                checkTask(player);
                return;
            }
            if (System.currentTimeMillis() - player.taskReceivedTime[2] >= 86_400_000) {
                player.dailiesCompleted--;
                player.taskReceivedTime[2] = -1;
                checkTask(player);
                return;
            }
            DialogueManager.start(player, 9903);
        } else if (!tasks.isEmpty()) {
            scanForExpired(player);
        } else {
            assign(player, TaskChallengeTimespan.DAILY);
        }
    }

    public static void cancelTask(Player player) {
        if (player.dailies.isEmpty()) {
            player.sendMessage("You do not currently have a daily task to cancel.");
            return;
        }
        if (player.dailySkips > 0) {
            player.dailySkips--;
            player.dailies.clear();
            player.dailiesCompleted++;
            player.sendMessage("You've cancelled your task. You now have "+player.dailySkips+" skips remaining.");
        }
    }

    public static void assign(Player player, TaskChallengeTimespan timespan) {
        val dailies = player.dailies;
        val freeIndex = findFreeIndexDaily(dailies);
        if (freeIndex == -1) {
            System.err.println("Error assigning new achievement index for Player "+ player.getUsername());
            return;
        }
        if (!dailies.isEmpty()) {
            return;
        }
        val tasks = player.skillingTask ? DailyTask.SKILLING_TASKS : DailyTask.PVM_TASKS;
        var selected = Misc.random(tasks);

        while(dailies.containsKey(selected)) {
            selected = Misc.random(tasks);
        }

        val task = createTask(freeIndex, selected, timespan);
        dailies.put(selected, task);
        task.send(player);
        player.taskInfo = "Your daily task is: @red@"+task.description+" @bla@Progress: @red@"+task.completedAmount+"/"+task.assignedAmount+"@bla@.";
        for (int i = 0; i < 3; i++) {
            if (player.taskReceivedTime[i] == -1) {
                player.taskReceivedTime[i] = System.currentTimeMillis();
                return;
            }
        }
    }

    public static void sendProgress(Player player) {
        val dailies = player.dailies;
        ArrayList<TaskChallenge> send = new ArrayList<>();
        dailies.forEach((task, challenge) -> {
            challenge.sendInfo(player);
        });
    }

    private static TaskChallenge createTask(int index, DailyTask task, TaskChallengeTimespan time) {
        val assignedAmount = Misc.random(task.assignmentMin, task.assignmentMax);
        val session = new TaskChallenge();
        session.key = task;
        session.deadlineTimestamp = time.getMillis() + System.currentTimeMillis();
        session.assignedAmount = assignedAmount;
        session.index = index;
        session.title = task.title;
        session.description = task.longDescr.replace("%d", Integer.toString(assignedAmount));
        session.timespan = time;
        return session;
    }

    public static void progress(Player player, DailyTask task) {
        val dts = player.dailies.get(task);
        if (dts == null || dts.completed())
            return;

        val oldVal = dts.completedAmount;
        dts.increment();
        if (dts.completedAmount == dts.assignedAmount) {
            player.sendMessage("You have completed your "+(dts.type == TaskTimeType.WEEKLY ? "weekly" : "daily")+" task: @red@"+dts.title+"@bla@!");
        }
        if (oldVal == 0) {
            player.sendMessage("@red@You have made progress towards your daily tasks!");
        }
    }

    public static void scanForExpired(Player player) {
        val dailies = player.dailies;
        ArrayList<TaskChallenge> removed = new ArrayList<>();
        dailies.forEach((task, challenge) -> {
        if (challenge.expired()) {
                removed.add(challenge);
                player.dailiesCompleted = 0;
            }
        });
        if (!removed.isEmpty()) {
            removed.forEach(index -> dailies.remove(index.key));
        } else {
            DialogueManager.start(player, 9904);
        }

        removed.clear();
    }

    public static void claimReward(Player player) {
        val task = player.dailies.values().stream()
                .filter(t -> t.index == 0)
                .findAny().orElse(null);

        if (task == null) {
            player.sendMessage("You do not currently have a daily task.");
            return;
        }

        if (!task.completed()) {
            player.sendMessage("You have not yet completed this task.");
            return;
        }

        player.dailies.clear();
        player.dailiesCompleted++;
        int coins = Misc.random(1_000_000, 10_000_000);
        player.getInventory().add(995, coins);
        boolean skipAdd = player.dailySkips < 3;
        if (skipAdd)
            player.dailySkips++;
        player.sendMessage("@red@You have claimed your reward & receive "+Misc.format(coins)+" GP"+(skipAdd ? " as well as one skip." : "."));
    }

    private static int findFreeIndexDaily(HashMap<DailyTask, TaskChallenge> map) {
        for (int i = 0; i < MAX_DAILIES; i++) {
            int finalI = i;
            if (map.values().stream().noneMatch(t -> t.index == finalI)) {
                return i;
            }
        }
        return -1;
    }

    private static long getTaskCountOf(Player player, TaskChallengeTimespan timespan) {
        return player.dailies.values().stream().filter(t -> t.timespan == timespan).count();
    }
}
