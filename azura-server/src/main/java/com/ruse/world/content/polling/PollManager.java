package com.ruse.world.content.polling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.world.entity.impl.player.Player;

/**
 * Created by Grant_ | Rune-server.ee | on 2/15/2018.
 */
public class PollManager {

	public static ArrayList<Poll> polls = new ArrayList<>();

	public static final Path POLL_DIR = Paths.get("./data/poll_data.json");

	public static final int MAX_CAPACITY = 7;

	private static JsonSaver js = new JsonSaver();

	/**
	 * Single instanced; {@link Gson#BUILDER} for the poll data
	 */
	private static final Gson BUILDER = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * The Type<T> reference of the serializer
	 */
	private static final Type TYPE = new TypeToken<ArrayList<Poll>>() {
	}.getType();

	/**
	 * Deserialize our json data
	 */
	public static void deserialize() {
		if (!polls.isEmpty())
			throw new IllegalStateException("Polls cant be loaded twice.");

		File file = POLL_DIR.toFile();
		if (file.exists()) {
			try {
				polls = BUILDER.fromJson(new FileReader(file.getAbsolutePath()), TYPE);
			}

			catch (JsonIOException | JsonSyntaxException | IllegalStateException | FileNotFoundException e) {
				// System.out.println("Exception occured while deserializing polls");
				e.printStackTrace();
			}
		}

		if (polls == null)
			polls = new ArrayList<>(MAX_CAPACITY);
	}

	/**
	 * The method referenced to save the polls to its path
	 */
	public static void serialize() {
		if (polls.isEmpty()) {
			// System.out.println("Polls were not saved because there were no polls to save!");
			return;
		}

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(POLL_DIR.toFile()));
			bw.write(js.serializer().toJson(polls));
			bw.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	static int tick = 0;

	/**
	 * Task runs on every 1 second, every 30 seconds we reserealize the data
	 * 
	 * @return
	 */
	public static Task countdownTask() {
		return new Task(2) {

			@Override
			protected void execute() {
				if (tick == 30) {
					serialize();
					tick = 0;
				}

				polls.forEach((poll) -> {
					if (poll.getSecondsLeft() > 0) {
						poll.decrementSecondsLeft();
					}
				});
				tick++;

			}
		};
	}

	/**
	 * Intial deserialization and starting our task
	 */
	public static void initialize() {
		// deserialize();
//		Server.getTaskScheduler().addEvent(countdownTask());
		TaskManager.submit(new Task(false) {
			@Override
			protected void execute() {

				stop();
			}
		});
	}

	/**
	 * Open the interface
	 * 
	 * @param c
	 */
	public static void openInterface(Player c) {

		updateDisplay(c);

		c.getPA().sendInterface(22239);
	}

	/**
	 * Update the polls
	 * 
	 * @param c
	 */
	public static void updateDisplay(Player c) {
		for (int i = 0; i < polls.size(); i++) {
			if (!polls.get(i).getTitle().equals("")) {
				updatePoll(c, i);
			} else {
				updateClosedPoll(c, i);
			}
		}
	}

	/**
	 * Update a single poll
	 * 
	 * @param c
	 * @param i
	 */
	public static void updatePoll(Player c, int i) {
		int offset = i * 20;
		c.getPA().sendFrame126("Question", 22247 + offset);
		c.getPA().sendFrame126(polls.get(i).getTimeRemaining(), 22248 + offset);
		c.getPA().sendFrame126(polls.get(i).getTitle(), 22249 + offset);
		c.getPA().sendFrame126(polls.get(i).getTitleLineTwo(), 22250 + offset);
		c.getPA().sendProgressBar(22251 + offset, 0, polls.get(i).getUpvotePercentage(), 0);
		c.getPA().sendFrame126("@whi@Total (" + polls.get(i).getTotalVotes() + ")", 22252 + offset);
		c.getPA().sendFrame126("Option 1: @gre@" + polls.get(i).getOptionOne(), 22253 + offset);
		c.getPA().sendFrame126("Option 2: @red@" + polls.get(i).getOptionTwo(), 22254 + offset);
		c.getPA().sendFrame126("@gre@" + polls.get(i).getUpvotePercentage() + "% (" + polls.get(i).getUpvotes() + ")",
				22264 + offset);
		c.getPA().sendFrame126(
				"@red@" + polls.get(i).getDownvotePercentage() + "% (" + polls.get(i).getDownvotes() + ")",
				22265 + offset);
	}

	/**
	 * Update a closed poll
	 * 
	 * @param c
	 * @param i
	 */
	public static void updateClosedPoll(Player c, int i) {
		int offset = i * 20;
		c.getPA().sendFrame126("", 22247 + offset);
		c.getPA().sendFrame126("", 22248 + offset);
		c.getPA().sendFrame126("This poll has been closed! A new poll", 22249 + offset);
		c.getPA().sendFrame126("will be coming shortly...", 22250 + offset);
		c.getPA().sendProgressBar(22251 + offset, 0, 0, 0);
		c.getPA().sendFrame126("", 22252 + offset);
		c.getPA().sendFrame126("", 22253 + offset);
		c.getPA().sendFrame126("", 22254 + offset);
		c.getPA().sendFrame126("", 22264 + offset);
		c.getPA().sendFrame126("", 22265 + offset);
	}

	/**
	 * Handle the clicking of voting and closing a poll
	 * 
	 * @param c
	 * @param button
	 * @return
	 */
	public static boolean handlePollClick(Player c, int button) {
		if (button < 86239 || button > 87109) {
			return false;
		}

		for (int i = 0; i < MAX_CAPACITY; i++) {
			int offset = i * 20;

			if (button >= 86239 && button <= 86245) {
				if (86245 + offset == button) {
					closePoll(c, i);
					break;
				} else if (86239 + offset == button) {
					upVotePoll(c, i);
					break;
				} else if (86242 + offset == button) {
					downVotePoll(c, i);
					break;
				}
			} else {
				if (87009 + offset == button) {
					closePoll(c, i + 1);
					break;
				} else if (87003 + offset == button) {
					upVotePoll(c, i + 1);
					break;
				} else if (87006 + offset == button) {
					downVotePoll(c, i + 1);
					break;
				}
			}
		}
		return true;
	}

	/**
	 * Close a poll
	 * 
	 * @param c
	 * @param i
	 */
	public static void closePoll(Player c, int i) {
		if (!hasEditorPrivileges(c)) {
			return;
		}

		if (!pollOpen(i)) {
			c.sendMessage("This poll has already been closed!");
			return;
		}

		// write "Poll Title"+

		pollLog(c, "Poll Title: " + polls.get(i).getTitle() + ", " + polls.get(i).getTitleLineTwo());
		pollLog(c, "Option 1: (" + polls.get(i).getUpvotes() + ") " + polls.get(i).getOptionOne());
		pollLog(c, "Option 1: (" + polls.get(i).getDownvotes() + ") " + polls.get(i).getOptionTwo());
		polls.get(i).setOptionTwo("");
		polls.get(i).setOptionOne("");
		polls.get(i).setSecondsLeft(0);
		polls.get(i).setTitle("");
		polls.get(i).setTitleLineTwo("");
		polls.get(i).setCreator("");
		polls.get(i).setUpvotes(0);
		polls.get(i).setDownvotes(0);
		polls.get(i).voters.clear();
		updateDisplay(c);
	}

	public static void pollLog(Player player, String data) {
		// i dont want writting to disk to slow server
		TaskManager.submit(new Task(false) {
			@Override
			public void execute() {
				try {
					player.getPA().checkDateAndTime(); // convert this later to better system lol
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./data/poll.txt"), true));
					writer.write("[" + player.date + "]" + "-" + "[" + player.currentTime + " "
							+ player.getPA().checkTimeOfDay() + "]: " + "[" + player.connectedFrom + "]: " + "" + data
							+ " ");
					writer.newLine();
					writer.flush();
					writer.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		});
	}

	/**
	 * Upvote a poll
	 * 
	 * @param c
	 * @param i
	 */
	public static void upVotePoll(Player c, int i) {
		if (pollExpired(i)) {
			c.sendMessage("This poll has expired!");
			return;
		}

		if (!pollOpen(i)) {
			c.sendMessage("This poll is not currently active!");
			return;
		}

		if (polls.get(i).voters.contains(c.getUsername())) {
			c.sendMessage("You have already voted on this poll!");
			return;
		}

		polls.get(i).voters.add(c.getUsername());
		polls.get(i).setUpvotes(polls.get(i).getUpvotes() + 1);
		reward(c);
		updateDisplay(c);
	}

	private static void reward(Player player) {
		player.getInventory().add(22572, 1);
		player.sendMessage("You received a reward due to your feedback!");
	}

	/**
	 * Downvote a poll
	 * 
	 * @param c
	 * @param i
	 */
	public static void downVotePoll(Player c, int i) {
		if (pollExpired(i)) {
			c.sendMessage("This poll has expired!");
			return;
		}

		if (!pollOpen(i)) {
			c.sendMessage("This poll is not currently active!");
			return;
		}

		if (polls.get(i).voters.contains(c.getUsername())) {
			c.sendMessage("You have already voted on this poll!");
			return;
		}

		polls.get(i).voters.add(c.getUsername());
		polls.get(i).setDownvotes(polls.get(i).getDownvotes() + 1);
		reward(c);
		updateDisplay(c);
	}

	/**
	 * Checks if a poll is expired
	 * 
	 * @param poll
	 * @return
	 */
	public static boolean pollExpired(int poll) {
		return polls.get(poll).getSecondsLeft() == 0;
	}

	/**
	 * Checks if a poll is open
	 * 
	 * @param poll
	 * @return
	 */
	public static boolean pollOpen(int poll) {
		return !polls.get(poll).getTitle().equals("");
	}

	/**
	 * Checks priviledges to see if that player can close a poll
	 * 
	 * @param player
	 * @return
	 */
	public static boolean hasEditorPrivileges(Player player) {
		if (player.getRights().isStaff()) {
			return true;
		} else {
			player.sendMessage("You don't have the privileges to do this.");
			return false;
		}

	}

	/**
	 * returns how many open poll slots are left (7 max)
	 * 
	 * @return
	 */
	public static int getOpenPolls() {
		int openPolls = 0;
		for (Poll poll : polls) {
			if (poll.getTitle().equals("")) {
				openPolls++;
			}
		}
		return openPolls;
	}
}
