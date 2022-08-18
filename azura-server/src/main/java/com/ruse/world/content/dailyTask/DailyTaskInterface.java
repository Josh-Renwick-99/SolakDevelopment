package com.ruse.world.content.dailyTask;

import com.ruse.model.Position;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.player.Player;

/**
 * 
 * @author Adam_#6723 Date: 18/03/2019
 */

public class DailyTaskInterface {

	private final static DailyTaskData taskData[] = DailyTaskData.values();

	protected Player player;

	public DailyTaskInterface(Player player) {
		this.player = player;
	}

	public static int currentClick;

	public void display() {
		player.setDailyTasksTemporary(null);
		tabClicking(-26198);
		player.getPA().sendInterface(39330);
		if (player.getCurrentDailyTask() != null) {
			player.getPA().sendMessage("Assigned Task " + " NPC: " + player.getCurrentDailyTask() + " Amount: "
					+ player.getCurrentDailyTaskAmount() + "!");
		} else {
			player.getPA().sendMessage("Select a task you'd like to do!");
		}
		player.getPA().sendMessage("If you'd like to reset your task, do ::resetdaily");
	}

	public void textSwitchUp() {
		int count = 39371;
		for (int i = 0; i < 20; i++) {
			player.getPA().sendFrame126("", 39371 + i);
		}
		for (DailyTaskData data : taskData) {
			if (player.getDailyTasksDifficulty() == data.getDifficulty()) {
				player.getPA().sendFrame126(data.getTaskName(), count++);
			}
		}
	}

	public void resetFrame34() {
		int interfaceId = 39361;
		for (int index = 0; index < 10; index++) {
			player.getPA().sendItemOnInterface(interfaceId, -1, -1);
		}
	}

	public void itemGroup(int buttonId) {
		for (DailyTaskData data : taskData) {
			if (data.getDifficulty() == player.getDailyTasksDifficulty()) {
				if (buttonId == data.getClickingId()) {
					resetFrame34();
					player.getPA().sendItemOnInterface(39361, data.getRewards(), 0, 1);
				}
			}
		}
	}

	public void button(int buttonId) {
		for (DailyTaskData data : taskData) {
			if (data.getDifficulty() == player.getDailyTasksDifficulty()) {
				if (buttonId == data.getClickingId()) {
					player.setDailyTasksTemporary(data);
					player.getPA().sendFrame126("" + data.getTaskInformation(), 39431);
					player.getPA().sendFrame126("" + data.getTaskName(), 39335);
					player.getPA().sendFrame126("" + data.getTaskTimer(), 39341);
					player.getPA().sendNpcIdToDisplayPacket(data.getNpcId(), 39351);
					if (player.getCurrentBossTaskAmount() != -1) {
						int amount = data.getKillingRequirement() - player.currentDailyTaskAmount;
						player.getPA().sendFrame126("Progress: " + amount + " Kills.", 39352);
					} else {
					}
					itemGroup(buttonId);
				}
			}
		}
	}

	public void tabClicking(int buttonid) {
		switch (buttonid) {
		case -26198:
			player.setDailyTasksDifficulty(DailyTaskDifficulty.EASY);
			textSwitchUp();
			break;
		case -26197:
			player.setDailyTasksDifficulty(DailyTaskDifficulty.MEDIUM);
			textSwitchUp();
			break;
		case -26196:
			player.setDailyTasksDifficulty(DailyTaskDifficulty.HARD);
			textSwitchUp();
			break;
		case -26190:
			teleport();
			break;
		case -26189:
			if (player.getDailyTasksTemporary() == null) {
				player.getPA().sendMessage("Please select a task first before proceeding to confirmation!");
				return;
			}
			if (player.getDailyTasks() != null) {
				player.getPA().sendMessage("You already have to kill: " + player.getCurrentDailyTask() + " Amount: "
						+ player.currentDailyTaskAmount + "!");
				player.getPA().sendInterfaceRemoval();
			} else {
				new DailyTaskHandler(player).assignTasks(player.getDailyTasksTemporary().getTaskNumber());
			}
			break;
		default:
			break;
		}
	}

	public void teleport() {
		if (player.getCurrentDailyTask() != null) {
			TeleportHandler.teleportPlayer(player,
					new Position(player.getxPosDailyTask(), player.getyPostDailyTask(), player.getzPosDailyTask()),
					player.getSpellbook().getTeleportType());
		} else {
			player.getPA().sendMessage("You have not clicked on a daily task yet.");
		}
	}

	public void assignTaskButton() {
		player.setDialogueActionId(215);
	}
}
