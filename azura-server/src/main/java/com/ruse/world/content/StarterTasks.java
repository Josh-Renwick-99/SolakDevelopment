package com.ruse.world.content;

import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

/**
 * @author Suic
 * @since June
 */

public class StarterTasks {

	public enum StarterTaskData {

		KILL_100_STARTER("Kill 100 warriors npc", 53207, "", new int[] { 0, 100 }, "Teleport using the",
				"spellbook or do ::melee", "", ""),
		KILL_20_GWD_BOSSES("Kill 20 GWD bosses", 53208, "", new int[] { 0, 20 }, "bosses @ ::GWD", "just enter a door",
				"To fight", "Kill 20 any of the selective "),
		CUT_1000_LOGS("Cut 1000 logs", 53209, "Any axe", new int[] { 2, 1000 }, "Click the woodcutting skill",
				"In your skill tab", "", ""),
		REDEEM_A_VOTE_SCROLL("Redeem a Vote Scroll", 53210, "", null, "::vote to vote", "After you vote",
				"Redeem the scroll", "And claim it"),
		REACH_1000_TOTAL("Reach 1000 Killcount", 53211, "", new int[] { 0, 1000 },
				"Reach a total of 1000 npc kill count", "you can check your killcount by", "typing ::kills", ""),
		KILL_ETERNAL("Defeat Eternal", 53212, "", null, "Defeat Eternal dragon @ ::eternal", "", "", "");


		StarterTaskData(String taskName, int interFaceId, String requirements, int[] progressData,
				String taskDescription, String taskDescription1, String taskDescription2, String taskDescription3) {
			this.taskName = taskName;
			this.interFaceId = interFaceId;
			this.requirements = requirements;
			this.progressData = progressData;
			this.taskDescription = taskDescription;
			this.taskDescription1 = taskDescription1;
			this.taskDescription2 = taskDescription2;
			this.taskDescription3 = taskDescription3;
		}

		private String taskName;
		private int interFaceId;
		private String requirements;
		private int[] progressData;
		private String taskDescription;
		private String taskDescription1;
		private String taskDescription2;
		private String taskDescription3;
	}

	static int[] rewards = { 22074, 6570, 7462, 17273, 19153, 19142, 19141, 19115, 11137, 20000 };

	public static boolean claimReward(Player player) {
		for (StarterTaskData tasks : StarterTaskData.values()) {
			if (!player.getStarterTaskAttributes().completed[tasks.ordinal()]) {
				player.sendMessage("Complete all the tasks first before claiming the reward.");
				return false;
			}
		}
		if (player.starterClaimed) {
			player.sendMessage("@red@You have already claimed the reward. @blu@ nice try though :)");
			return false;
		}
		if (!player.starterClaimed) {
			for (int i = 0; i < rewards.length; i++) {
				player.getInventory().add(rewards[i], 1);
			}
		}
		player.sendMessage("Enjoy your reward");
		player.starterClaimed = true;
		return true;
	}

	public static boolean handleButton(Player player, int buttonID) {
		if (!(buttonID >= -12329 && buttonID <= -12324)) {
			return false;
		}
		int index = -1;

		if (buttonID >= -12329) {
			index = 12329 + buttonID;
		}

		if (index >= 0 && index < StarterTaskData.values().length) {
			StarterTaskData tasks = StarterTaskData.values()[index];
			openInterface(player, tasks);
		}
		return true;
	}

	public static void updateInterface(Player player) {

		for (StarterTaskData tasks : StarterTaskData.values()) {
			boolean completed = player.getStarterTaskAttributes().getCompletion()[tasks.ordinal()];
			boolean progress = tasks.progressData != null
					&& player.getStarterTaskAttributes().getProgress()[tasks.progressData[0]] > 0;
			player.getPacketSender().sendString(tasks.interFaceId,
					(completed ? "@gre@" : progress ? "@yel@" : "@red@") + tasks.taskName);
		}
	}

	public static void doProgress(Player player, StarterTaskData tasks) {
		doProgress(player, tasks, 1);
	}

	public static void doProgress(Player player, StarterTaskData taskData, int amount) {
		if (player.getStarterTaskAttributes().getCompletion()[taskData.ordinal()])
			return;
		if (taskData.progressData != null) {
			int progressIndex = taskData.progressData[0];
			int amountNeeded = taskData.progressData[1];
			int previousDone = player.getStarterTaskAttributes().getProgress()[progressIndex];
			if ((previousDone + amount) < amountNeeded) {
				player.getStarterTaskAttributes().getProgress()[progressIndex] = previousDone + amount;
				if (previousDone == 0)
					player.getPacketSender().sendString(taskData.interFaceId, "@yel@" + taskData.taskName);
			} else {
				finishTask(player, taskData);
			}
		}
	}

	public static void finishTask(Player player, StarterTaskData tasks) {
		if (player.getStarterTaskAttributes().getCompletion()[tasks.ordinal()])
			return;
		player.getStarterTaskAttributes().getCompletion()[tasks.ordinal()] = true;
	}

	public static void openInterface(Player player, StarterTaskData task) {
		player.getPacketSender().sendString(53220, "" + task.taskDescription);
		player.getPacketSender().sendString(53221, "" + task.taskDescription1);
		player.getPacketSender().sendString(53222, "" + task.taskDescription2);
		player.getPacketSender().sendString(53223, "" + task.taskDescription3);

		if (!task.requirements.equalsIgnoreCase("")) {
			player.getPacketSender().sendString(53226, "Task Requirements:@yel@ " + task.requirements);
		} else {
			player.getPacketSender().sendString(53226, "Task Requirements: @yel@None");
		}

		if (player.getStarterTaskAttributes().getCompletion()[task.ordinal()]) {
			player.getPacketSender().sendString(53225, "Progress: @gre@100% (1/1)");
		} else if (task.progressData == null) {
			player.getPacketSender().sendString(53225, "Progress: @gre@0% (0/0)");

		} else {
			int currentProgress = player.getStarterTaskAttributes().getProgress()[task.progressData[0]];
			int totalProgress = task.progressData[1];
			boolean completed = player.getStarterTaskAttributes().getCompletion()[task.ordinal()];
			double percent = ((double) currentProgress * 100) / (double) totalProgress;
			if (currentProgress == 0) {
				player.getPacketSender().sendString(53225,
						"Progress: @gre@0 (" + Misc.insertCommasToNumber("" + currentProgress) + "/"
								+ Misc.insertCommasToNumber("" + totalProgress) + ")");
			} else if (currentProgress != totalProgress) {
				player.getPacketSender().sendString(53225,
						"Progress: @yel@" + percent + "% (" + Misc.insertCommasToNumber("" + currentProgress) + "/"
								+ Misc.insertCommasToNumber("" + totalProgress) + ")");
			} else if (completed) {
				player.getPacketSender().sendString(53225,
						"Progress: @gre@" + percent + " (" + Misc.insertCommasToNumber("" + currentProgress) + "/"
								+ Misc.insertCommasToNumber("" + totalProgress) + ")");
			}
		}

		player.getPacketSender().sendInterface(53200);
	}

	public static class StarterTaskAttributes {

		public StarterTaskAttributes() {

		}

		/** Tasks **/
		private boolean[] completed = new boolean[StarterTaskData.values().length];
		private int[] progress = new int[55];

		public boolean[] getCompletion() {
			return completed;
		}

		public void setCompletion(int index, boolean value) {
			this.completed[index] = value;
		}

		public void setCompletion(boolean[] completed) {
			this.completed = completed;
		}

		public int[] getProgress() {
			return progress;
		}

		public void setProgress(int index, int value) {
			this.progress[index] = value;
		}

		public void setProgress(int[] progress) {
			this.progress = progress;
		}

	}
}
