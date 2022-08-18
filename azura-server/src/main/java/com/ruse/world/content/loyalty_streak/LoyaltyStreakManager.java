package com.ruse.world.content.loyalty_streak;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.entity.impl.player.Player;

/**
 * Created by Grant_ | Rune-server.ee | on 6/17/2018.
 */
public class LoyaltyStreakManager {

	public static final int INTERFACE_ID = 22406;
	public static final int DAY_CHILD = 22435;
	public static final int REWARD_CHILD = 22436;
	public static final int ITEM_CHILD = 22437;
	public static final int SIDEBAR_CHILD = 22413;
	public static final long MILLIS_TO_DAY = 86400000;
	public static boolean BYPASS_VOTE = false;

	public static void open(Player c) {
		handleReset(c);
		loadRewards(c);
		updateSideBar(c);
		updateStrings(c);
		c.getPA().sendInterface(INTERFACE_ID);
	}

	public static void updateStrings(Player player) {
		if (player.currentVotingStreak == 1 || player.daysVoted == 1) {
			player.getPA().sendFrame126("@red@ Already Claimed", 22435);
		} else if (player.currentVotingStreak == 2 || player.daysVoted == 2) {
			player.getPA().sendFrame126("@red@ Already Claimed", 22436);
		} else if (player.currentVotingStreak == 3 || player.daysVoted == 3) {
			player.getPA().sendFrame126("@red@ Already Claimed", 22437);
		} else if (player.currentVotingStreak == 4 || player.daysVoted == 4) {
			player.getPA().sendFrame126("@red@ Already Claimed", 22438);
		} else if (player.currentVotingStreak == 5 || player.daysVoted == 5) {
			player.getPA().sendFrame126("@red@ Already Claimed", 22439);
		} else if (player.currentVotingStreak == 6 || player.daysVoted == 6) {
			player.getPA().sendFrame126("@red@ Already Claimed", 22440);
		} else if (player.currentVotingStreak == 7 || player.daysVoted == 7) {
			player.getPA().sendFrame126("@red@ Already Claimed", 22441);
		} else {
			return;
		}
	}

	public static void loadRewards(Player c) {
		LoyaltyStreakRewards[] rewards = LoyaltyStreakRewards.values();
		for (int i = 0; i < rewards.length; i++) {
			Item[] items = { new Item(rewards[i].getItemID(), rewards[i].getAmount()) };
			// c.getPA().sendItemOnInterface2(ITEM_CHILD + (i*6), items);
			c.getPA().sendItemsOnInterface(ITEM_CHILD + (i * 6), items);
			handleDayText(c, i);
			c.getPA().sendFrame126(((c.daysVoted % 7) >= (i + 1) ? "" : "") + rewards[i].getAmount() + " x "
					+ ItemDefinition.forId(rewards[i].getItemID()).getName(), REWARD_CHILD + (i * 6));
		}
	}

	public static void updateSideBar(Player c) {
		c.getPA().sendFrame126("@whi@" + c.daysVoted, SIDEBAR_CHILD + 1);
		c.getPA().sendFrame126("@whi@" + c.longestDaysVoted, SIDEBAR_CHILD + 3);
		c.getPA().sendFrame126("@whi@" + c.totalTimesClaimed, SIDEBAR_CHILD + 6);
		c.getPA().sendFrame126("@whi@" + c.getTimeString() + " (D:H:M)", SIDEBAR_CHILD + 8);
		c.getPA().sendFrame126(
				(getTimeLeft(c) > 0 ? "@red@" : "@gre@") + formatRewardTime(getTimeLeft(c)) + " @whi@(H:M:S)",
				SIDEBAR_CHILD + 11);
		c.getPA().sendFrame126((hasVotedToday(c) ? "@gre@Yes" : "@red@No"), SIDEBAR_CHILD + 14);
	}

	public static boolean handleButtonClick(Player c, int actionButtonId) {
		switch (actionButtonId) {
		case 22432:
			handleDay(c, 1);
			return true;
		case 22438:
			handleDay(c, 2);
			return true;
		case 22444:
			handleDay(c, 3);
			return true;
		case 22450:
			handleDay(c, 4);
			return true;
		case 22456:
			handleDay(c, 5);
			return true;
		case 22462:
			handleDay(c, 6);
			return true;
		case 22468:
			handleDay(c, 7);
			return true;
		case 87137:
			handleClose(c);
			return true;
		case 22428:// Refresh
			updateSideBar(c);
			return true;
		}
		return false;
	}

	private static void handleClose(Player c) {
		c.getPA().sendInterfaceRemoval();
	}

	private static void handleReset(Player c) {
		// if(System.currentTimeMillis() - c.timeUntilNextReward >= (MILLIS_TO_DAY * 2))
		// {
		if (c.currentVotingStreak >= 7 || c.daysVoted >= 7) {
			c.daysVoted = 0;
			c.currentVotingStreak = 0;
			c.sendMessage("You have lost your loyalty streak!");
		}
	}

	private static void handleDay(Player c, int i) {
		if (!hasVotedToday(c)) {
			c.sendMessage("You have not voted today! Please vote before claiming...");
			return;
		}

		if (!canGetReward(c)) {
			c.sendMessage("You must wait " + formatRewardTime(getTimeLeft(c))
					+ " (H:M:S) until you can claim your next reward!");
			return;
		}

		if (i <= (c.daysVoted % 7)) {
			c.sendMessage("You have already claimed this days reward!");
			return;
		}

		if (c.currentVotingStreak != i) {
			return;
		}

		if (hasVotedToday(c) && canGetReward(c) && (c.daysVoted % 7) == (i - 1) || c.currentVotingStreak == i) {
			c.daysVoted++;
			c.totalTimesClaimed++;
			c.timeUntilNextReward = System.currentTimeMillis();
			if (c.daysVoted > c.longestDaysVoted) {
				c.longestDaysVoted = c.daysVoted;
			}
			c.getPA().sendFrame126("@gre@Claimed: @or4@Day " + i, DAY_CHILD + ((i - 1) * 6));
			c.getPA().sendFrame126("@whi@" + c.daysVoted, SIDEBAR_CHILD + 1);
			c.getPA().sendFrame126("@whi@" + c.longestDaysVoted, SIDEBAR_CHILD + 3);
			c.getPA().sendFrame126("@whi@" + c.totalTimesClaimed, SIDEBAR_CHILD + 6);
			c.getPA().sendFrame126(
					(getTimeLeft(c) > 0 ? "@red@" : "@gre@") + formatRewardTime(getTimeLeft(c)) + " @whi@(H:M:S)",
					SIDEBAR_CHILD + 11);

			LoyaltyStreakRewards[] rewards = LoyaltyStreakRewards.values();
			c.getInventory().add(rewards[i - 1].getItemID(), rewards[i - 1].getAmount());
			c.sendMessage("You have been rewarded with " + rewards[i - 1].getAmount() + " x "
					+ ItemDefinition.forId(rewards[i - 1].getItemID()).getName() + " for your loyalty!");

			if ((c.daysVoted % 7) == 0) {
				loadRewards(c);
				c.sendMessage("You have completed a set of 7 days and have been set back to day 1!");
				c.sendMessage("Don't worry your streak will continue!");
			}
		} else {
			c.sendMessage("You can't claim that now!");
		}
	}

	private static boolean hasVotedToday(Player c) {
		return BYPASS_VOTE
				|| new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()).equals(c.lastVoted);
	}

	private static long getTimeUntilNextReward(Player c) {
		if (c.timeUntilNextReward == 0) {// If its their first time having this data loaded
			c.timeUntilNextReward = System.currentTimeMillis() - MILLIS_TO_DAY;
		}
		return System.currentTimeMillis() - c.timeUntilNextReward;
	}

	private static boolean canGetReward(Player c) {
		if (getTimeUntilNextReward(c) >= MILLIS_TO_DAY) {
			return true;
		}
		return false;
	}

	private static int getTimeLeft(Player c) {
		return ((int) (MILLIS_TO_DAY - getTimeUntilNextReward(c))) > 0
				? (int) (MILLIS_TO_DAY - getTimeUntilNextReward(c))
				: 0;
	}

	private static void handleDayText(Player c, int i) {
		if ((c.daysVoted % 7) > i) {
			c.getPA().sendFrame126("@gre@Claimed: @or4@Day " + (i + 1), DAY_CHILD + (i * 6));
		} else if (hasVotedToday(c) && canGetReward(c) && (c.daysVoted % 7) == i) {
			c.getPA().sendFrame126("@whi@Can claim: @or4@Day " + (i + 1), DAY_CHILD + (i * 6));
		} else {
			c.getPA().sendFrame126("@str@Day " + (i + 1), DAY_CHILD + (i * 6));
		}
	}

	/**
	 * Formats the time until next reward
	 * 
	 * @param totalMilliseconds
	 * @return
	 */
	public static String formatRewardTime(int totalMilliseconds) {
		int totalSecs = totalMilliseconds / 1000;
		long hours = (totalSecs / 3600);
		long mins = (totalSecs / 60) % 60;
		long secs = totalSecs % 60;
		String minsString = (mins == 0) ? "00" : ((mins < 10) ? "0" + mins : "" + mins);
		String secsString = (secs == 0) ? "00" : ((secs < 10) ? "0" + secs : "" + secs);
		if (hours > 0)
			return hours + ":" + minsString + ":" + secsString;
		else if (mins > 0)
			return "00:" + mins + ":" + secsString;
		else
			return "00:00:" + secsString;
	}

}
