package com.ruse.world.content.dailytasks_new;

import com.google.gson.annotations.Expose;
import com.ruse.util.Misc;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.entity.impl.player.Player;
import lombok.Getter;
import lombok.val;
import lombok.var;

import java.util.concurrent.TimeUnit;

public class TaskChallenge {

    /** The index of this challenge. **/
    @Expose
    @Getter
    public int index;

    /**
     * The timestamp indicating when this task will expire. This variable is calculated by the following during assignment:
     * <p>
     *     System.currentTimeMillis() + {@link TaskChallengeTimespan#getMillis()}
     * </p>
     */
    @Expose
    public long deadlineTimestamp;

    /** The amount of tasks done. **/
    @Expose
    public int completedAmount;

    /** The total amount assigned. **/
    @Expose
    public int assignedAmount;

    @Expose
    public TaskTimeType type;

    /** The title for this challenge. **/
    @Expose
    public String title;

    /** The item id for this challenge. **/
    @Expose
    public int itemId;

    /** The description for this task. **/
    @Expose
    public String description;

    /** The daily task key. **/
    @Expose
    public DailyTask key;

    /** The timespan type this task is to be completed in. **/
    @Expose
    public TaskChallengeTimespan timespan;

    /** A flag indicating if this reward has been claimed already. **/
    @Expose
    public boolean claimedReward;

    public void send(Player player) {
        player.getPacketSender().sendInterfaceRemoval();
        player.sendMessage("You have received a daily task: @red@"+description+"");
    }

    public void sendInfo(Player player) {
        player.taskInfo = "Your daily task is: @red@"+description+" @bla@Progress: @red@"+completedAmount+"/"+assignedAmount+"@bla@.";
    }

    /**
     * Increments the task progression by 1.
     */
    public int increment() {
        return increment(1);
    }

    /**
     * Increments the task progression by the specified amount.
     * @param amount
     */
    public int increment(int amount) {
        if (completedAmount >= assignedAmount)
            return assignedAmount;

        return completedAmount += amount;
    }

    /**
     * A flag determining if this task has expired.
     * @return
     */
    public boolean expired() {
        return deadlineTimestamp - System.currentTimeMillis() <= 0;
    }

    /**
     * A flag determining if this task has been completed.
     * @return
     */
    public boolean completed() {
        return completedAmount >= assignedAmount;
    }

    /**
     * A flag indicating if the task reward has been claimed.
     * @return
     */
    public boolean claimed() {
        return claimedReward;
    }

}
