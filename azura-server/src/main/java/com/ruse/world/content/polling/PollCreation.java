package com.ruse.world.content.polling;

import com.ruse.world.entity.impl.player.Player;

/**
 * Created by Grant_ | Rune-server.ee | on 2/15/2018.
 */
public class PollCreation {

	/**
	 * Opens interface
	 * 
	 * @param c
	 */
	public static void openInterface(Player c) {
		if (!PollManager.hasEditorPrivileges(c)) {
			c.sendMessage("You do not have access to this!");
			return;
		}

		clearInterface(c);

		c.getPA().sendInterface(19627);
	}

	/**
	 * Clears interface
	 * 
	 * @param c
	 */
	public static void clearInterface(Player c) {
		c.getPA().sendFrame126("Current question: ", 19659);
		c.getPA().sendFrame126("", 19660);
		c.getPA().sendFrame126("Option 1: ", 19661);
		c.getPA().sendFrame126("Option 2: ", 19662);
		c.getPA().sendFrame126("Time for poll: ", 19663);
	}

	/**
	 * Updates the current new question
	 * 
	 * @param c
	 * @param input
	 */
	public static void updateCurrentQuestion(Player c, String input) {
		if (input.length() > 98) {
			c.sendMessage("Please limit your title to 98 characters!");
			return;
		}

		c.pollTitle = input;

		if (c.pollTitle.length() > 39) {
			c.getPA().sendFrame126("Current question: @whi@" + input.substring(0, 39), 19659);
			c.getPA().sendFrame126("@whi@" + input.substring(39), 19660);
		} else {
			c.getPA().sendFrame126("Current question: @whi@" + input, 19659);
			c.getPA().sendFrame126("", 19660);
		}
	}

	/**
	 * Updates option one
	 * 
	 * @param c
	 * @param input
	 */
	public static void updateOptionOne(Player c, String input) {
		c.pollOptionOne = input;
		c.getPA().sendFrame126("Option 1: @whi@" + input, 19661);
	}

	/**
	 * Updates option two
	 * 
	 * @param c
	 * @param input
	 */
	public static void updateOptionTwo(Player c, String input) {
		c.pollOptionTwo = input;
		c.getPA().sendFrame126("Option 2: @whi@" + input, 19662);
	}

	/**
	 * Updates the expire time
	 * 
	 * @param c
	 * @param input
	 */
	public static void updateExpireTime(Player c, int input) {
		c.pollHours = input;
		c.getPA().sendFrame126("Time for poll: @whi@" + input + " hours", 19663);
	}

	/**
	 * Attempts to launch a new poll
	 * 
	 * @param c
	 */
	public static void launchPoll(Player c) {
		if (PollManager.getOpenPolls() == 0) {
			c.sendMessage("All polls are currently in-use! Please end one to create a new one!");
			return;
		}

		if (c.pollTitle == null) {
			c.sendMessage("Please set a poll title before doing this!");
			return;
		}

		if (c.pollTitle.equals("")) {
			c.sendMessage("Please set a poll title before doing this!");
			return;
		}

		if (c.pollOptionOne.equals("")) {
			c.sendMessage("Please set a poll option 1 before doing this!");
			return;
		}

		if (c.pollOptionTwo.equals("")) {
			c.sendMessage("Please set a poll option 2 before doing this!");
			return;
		}

		if (c.pollHours == 0) {
			c.sendMessage("Please set the poll's expire time before doing this!");
			return;
		}

		for (Poll poll : PollManager.polls) {
			if (poll.getTitle().equals("")) {
				if (c.pollTitle.length() > 39) {
					poll.setTitle(c.pollTitle.substring(0, 39));
					poll.setTitleLineTwo(c.pollTitle.substring(39));
				} else {
					poll.setTitle(c.pollTitle);
					poll.setTitleLineTwo("");
				}
				poll.setCreator(c.getUsername());
				poll.setOptionOne(c.pollOptionOne);
				poll.setOptionTwo(c.pollOptionTwo);
				poll.setSecondsLeft(c.pollHours * 3600);
				c.sendMessage("You have created a new poll!");
				clearInterface(c);
				c.pollTitle = "";
				c.pollOptionOne = "";
				c.pollOptionTwo = "";
				c.pollHours = 0;
				break;
			}
		}
	}

	/**
	 * Reset new poll form data
	 * 
	 * @param c
	 */
	public static void resetPoll(Player c) {
		clearInterface(c);
		c.pollTitle = "";
		c.pollOptionOne = "";
		c.pollOptionTwo = "";
		c.pollHours = 0;
		c.sendMessage("Your current inputs have been cleared!");
	}
}
