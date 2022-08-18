package com.ruse.world.content;

import com.ruse.model.Item;
import com.ruse.world.entity.impl.player.Player;

public class AchievementsOLD {

	public enum AchievementDataOLD {
;

		AchievementDataOLD(Difficulty difficulty, String interfaceLine, int interfaceFrame, int[] progressData,
						   Item reward) {
			this.difficulty = difficulty;
			this.interfaceLine = interfaceLine;
			this.interfaceFrame = interfaceFrame;
			this.progressData = progressData;
			this.reward = reward;
		}

		private Difficulty difficulty;
		private String interfaceLine;
		private int interfaceFrame;
		private int[] progressData;
		private Item reward;

		public Item getReward() {
			return reward;
		}

		public Difficulty getDifficulty() {
			return difficulty;
		}
	}

	public enum Difficulty {
		BEGINNER, EASY, MEDIUM, HARD, ELITE;
	}

	public static boolean handleButton(Player player, int button) {
		return false;
	}

	public static void updateInterface(Player player) {
	}

	public static void setPoints(Player player) {
	}

	public static void doProgress(Player player, AchievementDataOLD achievement) {
	}

	public static void doProgress(Player player, AchievementDataOLD achievement, int amt) {
	}

	public static void bankItemReward(Player player, Item item) {
	}

	public static void invItemReward(Player player, Item item) {
	}

	public static void giveReward(Player player, AchievementDataOLD achievement) {
	}

	public static void finishAchievement(Player player, AchievementDataOLD achievement) {
		if (player.getAchievementAttributes().getCompletion()[achievement.ordinal()])
			return;
	}

	public static class AchievementAttributes {

		public AchievementAttributes() {
		}

		/** ACHIEVEMENTS **/
		private boolean[] completed = new boolean[AchievementDataOLD.values().length];
		private int[] progress = new int[53];

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

		/** MISC **/
		private int coinsGambled;
		private double totalLoyaltyPointsEarned;
		private boolean[] godsKilled = new boolean[5];

		public int getCoinsGambled() {
			return coinsGambled;
		}

		public void setCoinsGambled(int coinsGambled) {
			this.coinsGambled = coinsGambled;
		}

		public double getTotalLoyaltyPointsEarned() {
			return totalLoyaltyPointsEarned;
		}

		public void incrementTotalLoyaltyPointsEarned(double totalLoyaltyPointsEarned) {
			this.totalLoyaltyPointsEarned += totalLoyaltyPointsEarned;
		}

		public boolean[] getGodsKilled() {
			return godsKilled;
		}

		public void setGodKilled(int index, boolean godKilled) {
			this.godsKilled[index] = godKilled;
		}

		public void setGodsKilled(boolean[] b) {
			this.godsKilled = b;
		}
	}
}
