package com.ruse.world.content.polling;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by Grant_ | Rune-server.ee | on 2/15/2018.
 */
public class Poll {

	private String title;
	private String titleLineTwo;
	private String creator;
	private int secondsLeft;
	private int upvotes;
	private int downvotes;
	private String optionOne;
	private String optionTwo;
	ArrayDeque<String> voters = new ArrayDeque<String>();

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleLineTwo(String titleLineTwo) {
		this.titleLineTwo = titleLineTwo;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setSecondsLeft(int secondsLeft) {
		this.secondsLeft = secondsLeft;
	}

	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}

	public void setDownvotes(int downvotes) {
		this.downvotes = downvotes;
	}

	public void setOptionOne(String optionOne) {
		this.optionOne = optionOne;
	}

	public void setOptionTwo(String optionTwo) {
		this.optionTwo = optionTwo;
	}

	public int getSecondsLeft() {
		return secondsLeft;
	}

	public String getTitle() {
		return title;
	}

	public String getTitleLineTwo() {
		return titleLineTwo;
	}

	public String getCreator() {
		return creator;
	}

	public int getUpvotes() {
		return upvotes;
	}

	public int getDownvotes() {
		return downvotes;
	}

	public String getOptionOne() {
		return optionOne;
	}

	public String getOptionTwo() {
		return optionTwo;
	}

	public void decrementSecondsLeft() {
		secondsLeft -= 1;

		if (secondsLeft <= 0) {
			secondsLeft = 0;
		}
	}

	public int getTotalVotes() {
		return downvotes + upvotes;
	}

	public int getUpvotePercentage() {
		return (int) (((double) upvotes / (double) getTotalVotes()) * 100);
	}

	public int getDownvotePercentage() {
		return (int) (((double) downvotes / (double) getTotalVotes()) * 100);
	}

	public String getTimeRemaining() {
		int day = (int) TimeUnit.SECONDS.toDays(secondsLeft);
		long hours = TimeUnit.SECONDS.toHours(secondsLeft) - (day * 24);

		if (secondsLeft == 0) {
			return "Time Remaining: @red@Expired";
		}

		return "Time Remaining: @whi@" + (day > 0 ? day + (day == 1 ? " day " : " days ") : "")
				+ (hours > 0 ? hours + (hours == 1 ? " hour" : " hours") : (secondsLeft > 0 ? " < 1 hour" : ""));
	}
}
