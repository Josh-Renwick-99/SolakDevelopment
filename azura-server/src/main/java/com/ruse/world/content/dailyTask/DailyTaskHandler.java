package com.ruse.world.content.dailyTask;

import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

/**
 * 
 * @author Adam_#6723 Date: 18/03/2019
 */

public class DailyTaskHandler {

	private Player player;

	public DailyTaskHandler(Player player) {
		this.player = player;
	}

	public void assignTasks(int taskNumber) {
		DailyTaskData data = DailyTaskData.byId.get(taskNumber);
		resetTasks();
		if (player.currentDailyTask != "" || player.currentDailyTaskAmount != -1 || player.getDailyTasks() == null) {
			if (taskNumber == data.getTaskNumber()) {
				player.setDailyTasks(data);
				player.setCurrentDailyTask(player.getDailyTasks().getNpcName());
				player.setCurrentDailyTaskAmount(player.getDailyTasks().getKillingRequirement());
				player.setxPosDailyTask(player.getDailyTasks().getPosition().getX());
				player.setyPostDailyTask(player.getDailyTasks().getPosition().getY());
				player.setzPosDailyTask(player.getDailyTasks().getPosition().getZ());
				player.setRewardDailyTask(player.getDailyTasks().getRewards());
				player.getPA().sendMessage("Assigned Task: " + player.getDailyTasks().getTaskName() + " NPC: "
						+ player.getCurrentDailyTask() + " Amount: " + player.getCurrentDailyTaskAmount());
			} else {
				player.getPA().sendMessage("You already have a daily task assigned fella.");
			}
		}
	}

	public void death(String NpcName) {
		if (!NpcName.equalsIgnoreCase(player.getCurrentDailyTask())) {
			return;
		}
		if (player.currentDailyTaskAmount == -1) {
			return;
		}
		if (player.currentDailyTask == "") {
			return;
		}
		player.setCurrentDailyTaskAmount(player.getCurrentDailyTaskAmount() - 1);
		player.getPA().sendMessage("You currently need to kill " + player.getCurrentDailyTaskAmount() + " "
				+ player.getCurrentDailyTask());

		if (player.getCurrentDailyTaskAmount() <= 0) {
			player.setCurrentDailyTask("");
			player.setCurrentDailyTaskAmount(-1);
			player.getPA().sendMessage("You have completed daily tasks! check your bank for your reward!");
			finish();
			return;
		}
	}

	public void resetTasks() {
		player.setDailyTasks(null);
		player.setCurrentDailyTask("");
		player.setCurrentDailyTaskAmount(-1);
		player.setxPosDailyTask(-1);
		player.setyPostDailyTask(-1);
		player.setzPosDailyTask(-1);
		player.setRewardDailyTask(-1);
	}

	int amount = Misc.exclusiveRandom(1000000, 10000000);

	public void finish() {
		World.sendMessage("<img=13><col=0000ff> " + player.getUsername()
				+ " Has just been rewarded for completing the Daily Tasks");
		player.getInventory().add(player.getRewardDailyTask(), 1);
		// player.getInventory().add(player.getDailyTasksTemporary().getRewards(), 1);
		player.getPA().sendMessage("You have Recieved a reward for completing the daily tasks!");
		if (player != null) {
			resetTasks();
		}
	}
}
