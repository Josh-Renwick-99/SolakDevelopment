package com.ruse.world.content.grandLottery;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerHandler;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Tyrant
 */
public class GrandLottery {

	/**
	 * Is the lottery enabled? This determines if player can enter the lottery and
	 * if it should sequences
	 */
	public static boolean enabled = false;

	/**
	 * The maximum of entries that each player can enter with every round
	 */
//    private static final int MAXIMUM_ENTRIES = 10;

	/**
	 * The interface id of this lottery
	 */
	private static final int INTERFACE_ID = 30900;

	/**
	 * The map that holds entries: String represents the name of the entry owner,
	 * the List contains the price that user paid for each entry which also
	 * represents the amount of entries he has. this is because incase we need to
	 * refund and the price of the entry that the user originally paid for has
	 * changed, we dont want to give them the current amount but the one they paid
	 */
	private static final Map<String, ArrayList<Double>> ENTRIES = new LinkedHashMap<>();

	/**
	 * The default price for an entry. It is also the minimum entry cost (entry cost
	 * that is settled to below this wont pass)
	 */
	private static final double DEFAULT_ENTRY_COST = 5.00;

	/**
	 * The default 1st prize that is initialized with every new lottery round
	 */
	public static int random_1 = Misc.exclusiveRandom(2, 4);
	public static int random_2 = Misc.exclusiveRandom(100, 200);
	public static int random_3 = Misc.exclusiveRandom(10, 20);

	private static final Item DEFAULT_1ST_PRIZE = new Item(ItemDefinition.COIN_ID, random_1);

	/**
	 * The default 2nd prize that is initialized with every new lottery round
	 */
	private static final Item DEFAULT_2ND_PRIZE = new Item(ItemDefinition.COIN_ID, random_2);

	/**
	 * The default 3rd prize that is initialized with every new lottery round
	 */
	private static final Item DEFAULT_3RD_PRIZE = new Item(ItemDefinition.COIN_ID, random_3);

	/**
	 * The default static prize that is initialized with every new lottery round
	 */
	private static final Item DEFAULT_STATIC_PRIZE = new Item(ItemDefinition.COIN_ID, random_2);

	/**
	 * The default minutes to count the lottery drawing (3 hours -> 180 minutes)
	 */
	private static final int DEFAULT_SECONDS_COUNT = (int) TimeUnit.HOURS.toSeconds(1);

	/**
	 * The amount of minutes that needs to be passed before being able to join the
	 * lottery when a new round begins
	 */
	private static final int NEW_ROUND_LOCK_TIMER = (int) TimeUnit.MINUTES.toSeconds(1);

	/**
	 * The default starting syntax of the lottery message
	 */
	private static final String LOTTERY_MESSAGE_SYNTAX = "[Lottery]";

	/**
	 * The minimum amount of the {@link GrandLottery#getUniqueEntries} to draw the
	 * lottery.
	 */
	private static final int MINIMUM_ENTRIES_TO_DRAW = 10;

	// /**
//     * Checks if the occurrence of the given <code>username</code> does not occur more than the {@link #MAXIMUM_ENTRIES} allowed.
//     *
//     * @param username the given username to check
//     * @return <code>true</code> if username is still eligible to enter the lottery
//     */
//    private static boolean canEnter(String username) {
//        return getEntriesForUsername(username) < MAXIMUM_ENTRIES;
//    }
	private static boolean canEnter(String username) {
		return true;
	}

	private static int getEntriesForUsername(String username) {
		if (ENTRIES.get(username.toLowerCase()) == null) {
			return 0;
		}
		return ENTRIES.get(username.toLowerCase()).size();
//        Optional<Map.Entry<String, ArrayList<Double>>> entry = ENTRIES.entrySet().stream().filter($it -> $it.getKey().equalsIgnoreCase(username)).findAny();
//        return entry.map(stringArrayListEntry -> stringArrayListEntry.getValue().size()).orElse(0);
	}

	private static boolean isParticipating(String username) {
		return ENTRIES.containsKey(username.toLowerCase());
	}

	/**
	 * Gets the total amount of entries. Do not use {@link #ENTRIES#size} because
	 * the value of each key represents the amount of entries
	 *
	 * @return
	 */
	private static int getTotalEntries() {
		AtomicInteger count = new AtomicInteger();
		ENTRIES.forEach((key, value) -> count.addAndGet(value.size()));
		return count.get();
	}

	/**
	 * returns the unique amount of entries, it is counted per user that entered and
	 * not by the amount of entries he has
	 *
	 * @return
	 */
	private static int getUniqueEntries() {
		return ENTRIES.size();
	}

	// those are the constants that changes on every new round
	private static int timeAdded = 0; // the amount of time added throughout the new round
	private static int secondsLeft = DEFAULT_SECONDS_COUNT;
	private static double entryCost = DEFAULT_ENTRY_COST;
	private static boolean restarting = false;
	private static int entriesToUnlockSpecial = 0;

	private static Item prize1St = DEFAULT_1ST_PRIZE;
	private static Item prize2Nd = DEFAULT_2ND_PRIZE;
	private static Item prize3Rd = DEFAULT_3RD_PRIZE;
	private static Item staticPrize = DEFAULT_STATIC_PRIZE;
	private static Item specialPrize = null;
	private static Item backupPrize1st = prize1St; // incase resets

	private static String winner1St = null;
	private static String winner2Nd = null;
	private static String winner3Rd = null;

	// open the lottery for player
	public static void open(Player c) { // extra checks
		c.getPA().sendInterface(INTERFACE_ID);
		draw(c);
		c.sendMessage("Welcome to Lotties Lottery, each Lottery ticket is worth 50m!.");
	}

	// buy entry
	public static void buy(Player c, int amount) {
		if (Misc.currentTimeMillis() - c.lastLotteryEnter < 3000) {
			c.sendMessage("Please wait another " + (3 - (int) ((Misc.currentTimeMillis() - c.lastLotteryEnter) / 1000))
					+ " second(s) before clicking this again.");
			return;
		}
		c.lastLotteryEnter = Misc.currentTimeMillis();

		if (amount > 100) {
			c.sendMessage("You can only enter at a maximum of a 100 times at a time.");
			return;
		} else if (amount <= 0) {
			c.sendMessage("You must insert amount of atleast 1.");
			return;
		}
		if (((DEFAULT_SECONDS_COUNT + timeAdded) - secondsLeft) < NEW_ROUND_LOCK_TIMER) {
			c.sendMessage("The Grand lottery will be opened in <col=255>"
					+ (NEW_ROUND_LOCK_TIMER - ((DEFAULT_SECONDS_COUNT + timeAdded) - secondsLeft)) + "</col> seconds.");
			return;
		}
		if (c.entriesCurrency < (entryCost * amount)) {
			c.sendMessage("Your entry balance is insufficient to enter the lottery.");
			c.sendMessage("Please buy a ticket first.");
			return;
		}
		/*
		 * if (!canEnter(c.getUsername())) {
		 * c.sendMessage("You have already purchased the maximum allowed entries.");
		 * return; }
		 */
		if (secondsLeft <= 15) {
			c.sendMessage("You can't enter the lottery in the last 15 seconds of it.");
			c.sendMessage("You may however join in the next round.");
			return;
		}
		if (entryCost < DEFAULT_ENTRY_COST) {
			c.sendMessage("Price is invalid. Please report to staff.");
			return;
		}
		c.entriesCurrency -= (entryCost * amount);
		if (ENTRIES.get(c.getUsername().toLowerCase()) == null) {
			ArrayList<Double> newArray = new ArrayList<>();
			for (int i = 0; i < amount; i++)
				newArray.add(entryCost);
			ENTRIES.put(c.getUsername().toLowerCase(), newArray);
		} else
			for (int i = 0; i < amount; i++)
				ENTRIES.get(c.getUsername().toLowerCase()).add(entryCost);
		draw(c);
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "<col=800000000>" + Misc.ucFirst(c.getUsername())
				+ " </col>just entered for <col=800000000>" + amount + "</col> time(s)! Total entries: <col=800000000>"
				+ getTotalEntries());

		c.lastLotteryEnter = Misc.currentTimeMillis();
	}

	public static void addTime(Player c, int seconds) {
		timeAdded += seconds;
		secondsLeft += seconds;
		c.sendMessage("New time: " + getTimeLeft());
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "<col=800000000>" + TimeUnit.SECONDS.toMinutes(seconds)
				+ "</col> minutes has been <col=800000000>added</col> to the timer. (New time: <col=800000000>"
				+ getTimeLeft() + "</col>)");

	}

	// the system currently reads the paramater as seconds,
	// to make it minutes, simply multiple the secnods by 60
	// i.e 1 minute will be 60 seconds, 50 minutes will be 3000 seconds
	public static void removeTime(Player c, int seconds) {
		if (secondsLeft - seconds < 0) {
			c.sendMessage("You can't make the timer past 0. (Maximum to remove: <col=255>"
					+ (((seconds - (seconds - secondsLeft)) / 60)) + "</col> minutes.)");
			return;
		}
		timeAdded -= seconds;
		secondsLeft -= seconds;
		c.sendMessage("New time: " + getTimeLeft());
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "(New time: <col=800000000>" + getTimeLeft() + "</col>)");
		draw(c);

	}

	public static void setEntryCost(Player c, double cost) {
		if (cost < DEFAULT_ENTRY_COST) {
			c.sendMessage("Minimum entry cost must be " + DEFAULT_ENTRY_COST);
			return;
		}
		entryCost = cost;
		c.sendMessage("New entry cost: " + entryCost);
		World.sendMessage(
				LOTTERY_MESSAGE_SYNTAX + "The entry cost has been updated to <col=800000000>" + entryCost + "</col>.");
		draw(c);
	}

	public static void set1stWinner(Player c, String username) {
		if (username == null || username.isEmpty()) {
			c.sendMessage("Please insert a proper username.");
			return;
		}
		if (winner2Nd.equalsIgnoreCase(username) || winner3Rd.equalsIgnoreCase(username)) {
			c.sendMessage("You can not have the same winner for multiple prizes.");
			c.sendMessage("Username is already being used as " + (winner2Nd.equalsIgnoreCase(username) ? "winner 2nd"
					: winner3Rd.equalsIgnoreCase(username) ? "winner 3rd" : "winner ?"));
			return;
		}
		if (username.equalsIgnoreCase("null"))
			winner1St = null;
		else
			winner1St = username.toLowerCase();
		if (winner1St == null)
			c.sendMessage("Resettled 1st winner.");
		else
			c.sendMessage("Set 1st winner to: " + winner1St);
	}

	public static void set2ndWinner(Player c, String username) {
		if (username == null || username.isEmpty()) {
			c.sendMessage("Please insert a proper username.");
			return;
		}
		if (winner1St.equalsIgnoreCase(username) || winner3Rd.equalsIgnoreCase(username)) {
			c.sendMessage("You can not have the same winner for multiple prizes.");
			c.sendMessage("Username is already being used as " + (winner1St.equalsIgnoreCase(username) ? "winner 1st"
					: winner3Rd.equalsIgnoreCase(username) ? "winner 3rd" : "winner ?"));
			return;
		}
		if (username.equalsIgnoreCase("null"))
			winner2Nd = null;
		else
			winner2Nd = username.toLowerCase();
		if (winner2Nd == null)
			c.sendMessage("Resettled 2nd winner.");
		else
			c.sendMessage("Set 2nd winner to: " + winner2Nd);
	}

	public static void set3rdWinner(Player c, String username) {
		if (username == null || username.isEmpty()) {
			c.sendMessage("Please insert a proper username.");
			return;
		}
		if (winner2Nd.equalsIgnoreCase(username) || winner1St.equalsIgnoreCase(username)) {
			c.sendMessage("You can not have the same winner for multiple prizes.");
			c.sendMessage("Username is already being used as " + (winner2Nd.equalsIgnoreCase(username) ? "winner 2nd"
					: winner1St.equalsIgnoreCase(username) ? "winner 1st" : "winner ?"));
			return;
		}
		if (username.equalsIgnoreCase("null"))
			winner3Rd = null;
		else
			winner3Rd = username.toLowerCase();
		if (winner3Rd == null)
			c.sendMessage("Resettled 3rd winner.");
		else
			c.sendMessage("Set 3rd winner to: " + winner3Rd);
	}

	public static void set1stPrize(Player c, int itemId, int amount) {
		if (itemId <= 0) {
			c.sendMessage("ItemId must be above 0");
			return;
		}
		if (amount <= 0)
			amount = 1;
		prize1St = new Item(itemId, amount);
		backupPrize1st = prize1St;
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "1st Prize has been updated to <col=800000000>"
				+ ItemDefinition.forId(itemId).getName() + "</col> x <col=800000000>" + amount + "</col>!");
	}

	public static void set2ndPrize(Player c, int itemId, int amount) {
		if (itemId <= 0) {
			c.sendMessage("ItemId must be above 0");
			return;
		}
		if (amount <= 0)
			amount = 1;
		prize2Nd = new Item(itemId, amount);
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "2nd Prize has been updated to <col=800000000>"
				+ ItemDefinition.forId(itemId).getName() + "</col> x <col=800000000>" + amount + "</col>!");

	}

	public static void set3rdPrize(Player c, int itemId, int amount) {
		if (itemId <= 0) {
			c.sendMessage("ItemId must be above 0");
			return;
		}
		if (amount <= 0)
			amount = 1;
		prize3Rd = new Item(itemId, amount);
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "3rd Prize has been updated to <col=800000000>"
				+ ItemDefinition.forId(itemId).getName() + "</col> x <col=800000000>" + amount + "</col>!");
	}

	public static void setStaticPrize(Player c, int itemId, int amount) {
		if (itemId <= 0) {
			c.sendMessage("ItemId must be above 0");
			return;
		}
		if (amount <= 0)
			amount = 1;
		staticPrize = new Item(itemId, amount);
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "Static Prize has been updated to <col=800000000>"
				+ ItemDefinition.forId(itemId).getName() + "</col> x <col=800000000>" + amount + "</col>!");
	}

	public static void setSpecialPrize(Player c, int itemId, int amount, int entriesToUnlock) {
		if (entriesToUnlock < 1) {
			c.sendMessage("Entries to unlock must be atleast 1.");
			return;
		}
		if (itemId <= 0) {
			c.sendMessage("ItemId must be above 0");
			return;
		}
		if (amount <= 0)
			amount = 1;
		specialPrize = new Item(itemId, amount);
		entriesToUnlockSpecial = entriesToUnlock;
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "Special Prize has been updated to <col=800000000>"
				+ ItemDefinition.forId(itemId).getName() + "</col> x <col=800000000>" + amount + "</col>!");

	}

	public static void removeSpecialPrize(Player c) {
		if (specialPrize == null) {
			c.sendMessage("There is no special prize settled.");
			return;
		}
		if (prize1St.equals(specialPrize)) {
			prize1St = backupPrize1st;
		}
		specialPrize = null;
		entriesToUnlockSpecial = 0;
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "Special Prize has been removed.");
		c.sendMessage("Special prize removed.");
	}

	// sequence the lottery :: should run through a task every 1 second
	private static void sequence() {
		// just to make sure we're not calling this when unnecessary
		if (restarting)
			return;
		secondsLeft--;

		// time left handling (either add time or end round)
		if (secondsLeft <= 0) {
			random();
		}

		// unlocks & applies special prize
		if (entriesToUnlockSpecial != 0 && specialPrize != null && !prize1St.equals(specialPrize)
				&& getTotalEntries() >= entriesToUnlockSpecial) {
			backupPrize1st = prize1St;
			prize1St = specialPrize;
			World.sendMessage(LOTTERY_MESSAGE_SYNTAX
					+ "The <col=800000000>1st Prize</col> has been replaced with the <col=800000000>Special Prize</col>!");
			// might no need != null cus equals has it
			if (entriesToUnlockSpecial == 0 && specialPrize != null && prize1St.equals(specialPrize)) {
				// reset prize if no entries to unlock (means we reset but somethin fucked up)
				prize1St = backupPrize1st;

			}
		}
	}

	public static void random() {
		// time left handling (either add time or end round)
		endRound();
	}

	// called once to create the task
	public static void init() {
		TaskManager.submit(new Task(false) {

			@Override
			protected void execute() {
				if (!enabled || entryCost < DEFAULT_ENTRY_COST) {
					stop();
					if (enabled) // only if enabled
						World.sendMessage("Grand Lottery has just ended.");
					// announce?
					return;
				}
				sequence();
			}
		});
		//Logger.getGlobal().info("Grand lottery has been initialized.");
	}

	// draws the current round and initialize a new one
	public static void endRound() {
		// no enough entries to end round
		if (getUniqueEntries() <= MINIMUM_ENTRIES_TO_DRAW) {
			secondsLeft += TimeUnit.MINUTES.toSeconds(30);
			World.sendMessage(LOTTERY_MESSAGE_SYNTAX
					+ "Another <col=800000000>30</col> minutes has been added, talk to Lottie to enter.");
			return;
		} else {
			restarting = true;
			handleRewards();
			newRound();
		}
	}

	// could rewrite some parts of this
	private static void handleRewards() {
		// the list that contains the usernames of the entries, they will occur as many
		// times as entries they've bought.
		// this list allows duplicates.
		List<String> occurrenceList = new ArrayList<>();

		// put the entries into the list for the amount of entries they bought
		for (Map.Entry<String, ArrayList<Double>> entry : ENTRIES.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++)
				occurrenceList.add(entry.getKey());
		}

		// no entries ?
		if (occurrenceList.isEmpty()) {
			return;
		}
		// we shuffle duffle the list
		Collections.shuffle(occurrenceList);

		String winner1st;
		String winner2nd;
		String winner3rd;

		if (winner1St != null && isParticipating(winner1St)) {
			winner1st = winner1St;
		} else {
			winner1st = occurrenceList.stream().findAny().orElse(null);
		}

		String finalWinner1st = winner1st;

		if (winner2Nd != null && isParticipating(winner2Nd)) {
			winner2nd = winner2Nd;
		} else {
			winner2nd = occurrenceList.stream().filter($it -> !$it.equalsIgnoreCase(finalWinner1st)).findAny()
					.orElse(null);
		}

		String finalWinner2nd = winner2nd;

		if (winner3Rd != null && isParticipating(winner3Rd)) {
			winner3rd = winner3Rd;
		} else {
			winner3rd = occurrenceList.stream()
					.filter($it -> !$it.equalsIgnoreCase(finalWinner1st) && !$it.equalsIgnoreCase(finalWinner2nd))
					.findAny().orElse(null);
		}

		// could probably remove this
		{// idk how this would happen.. so bunch of "impossible" occurrences
			if (winner1st == null) {
				winner1st = winner2nd;
				winner2nd = null;
			}
			if (winner1st == null) {
				winner1st = winner3rd;
				winner3rd = null;
			}
			if (winner2nd == null) {
				winner2nd = winner3rd;
				winner3rd = null;
			}

			if (winner1st == null) {
				winner1st = occurrenceList.stream().findAny().orElse(null);
			}
			String finalWinner1st1 = winner1st;
			if (winner2nd == null) {
				winner2nd = occurrenceList.stream().filter($it -> !$it.equalsIgnoreCase(finalWinner1st1)).findAny()
						.orElse(null);
			}
			String finalWinner2nd1 = winner2nd;
			if (winner3rd == null) {
				winner3rd = occurrenceList.stream()
						.filter($it -> !$it.equalsIgnoreCase(finalWinner1st1) && !$it.equalsIgnoreCase(finalWinner2nd1))
						.findAny().orElse(null);
			}
		}

		{// handles for 1st
			Player w1nd = PlayerHandler.getPlayer(winner1st);
			if (w1nd != null) {
				((Player) w1nd).sendMessage("Congratulations, you have won the grand lottery at position 1st");
				World.sendMessage("Congratulations, you have won the "
						+ (prize1St.equals(specialPrize) ? "grand lottery with a Special Prize" : "grand lottery")
						+ " at position 1st");
				w1nd.getInventory().add(prize1St.getId(), (prize1St.getAmount() == 0 ? 1 : prize1St.getAmount()));
			} else {
			}
		}

		{// handles for 2nd
			Player w2nd = PlayerHandler.getPlayer(winner2nd);
			if (w2nd != null) {
				((Player) w2nd).sendMessage("Congratulations, you have won the grand lottery at position 2nd");
				w2nd.getInventory().add(prize2Nd.getId(), (prize2Nd.getAmount() == 0 ? 1 : prize2Nd.getAmount()));
			} else {
			}
		}

		{// handles for 3rd
			Player w3rd = PlayerHandler.getPlayer(winner3rd);
			if (w3rd != null) {
				((Player) w3rd).sendMessage("Congratulations, you have won the grand lottery at position 3rd");
				w3rd.getInventory().add(prize3Rd.getId(), (prize3Rd.getAmount() == 0 ? 1 : prize3Rd.getAmount()));
			} else {
			}
		}
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "The <col=800000000>Grand Lottery</col> has drawn "
				+ (specialPrize != null ? "with a Special Prize" : "") + ", the results are:");
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "1st - <col=800000000>" + winner1st + "</col>, 2nd - <col=800000000>"
				+ winner2nd + "</col>, 3rd - <col=800000000>" + winner3rd + "</col>. Congrats!");

	}

	// reset everything to default

	private static void newRound() {
		timeAdded = 0;
		secondsLeft = DEFAULT_SECONDS_COUNT;
		entryCost = DEFAULT_ENTRY_COST;
		entriesToUnlockSpecial = 0;

		prize1St = DEFAULT_1ST_PRIZE;
		prize2Nd = DEFAULT_2ND_PRIZE;
		prize3Rd = DEFAULT_3RD_PRIZE;
		staticPrize = DEFAULT_STATIC_PRIZE;
		specialPrize = null;
		World.sendMessage("LOTTERY HAS BEEN DRAWED! CONGRALATION TO THE WINNERS");

		winner1St = winner2Nd = winner3Rd = null;

		ENTRIES.clear();

		// cant see how this would happen but i mean w.e
		if (!ENTRIES.isEmpty()) {
			enabled = false;
			World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "Lottery has ended please alert staff");
		}
		World.sendMessage(
				LOTTERY_MESSAGE_SYNTAX + "A new round of the <col=800000000>Grand Lottery</col> has started.");
		restarting = false;
	}

	// force reset the lottery whether its update or by command
	public static void forceReset(boolean disable) {
		if (disable)
			enabled = false;

		restarting = true;
		// REFUND!
		ENTRIES.forEach((key, value) -> {
			Player c = PlayerHandler.getPlayer(key);
			double toRefund = 0;
			// online
			if (c != null) {
				for (double d : value)
					toRefund += d;
				c.sendMessage("You have been refunded with " + toRefund + " entries currency because it has stopped.");
				c.entriesCurrency += toRefund;
			} else {
				// offline
				for (double d : value)
					toRefund += d;
				c.getInventory().add(ItemDefinition.COIN_ID, (int) toRefund);
			}
		});
		World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "The <col=800000000>Grand Lottery</col> has been "
				+ (enabled ? "disabled" : "force reset") + ". Entries are refunded.");
		secondsLeft = DEFAULT_SECONDS_COUNT;
		entryCost = DEFAULT_ENTRY_COST;
		entriesToUnlockSpecial = 0;

		prize1St = DEFAULT_1ST_PRIZE;
		prize2Nd = DEFAULT_2ND_PRIZE;
		prize3Rd = DEFAULT_3RD_PRIZE;
		staticPrize = DEFAULT_STATIC_PRIZE;
		specialPrize = null;

		ENTRIES.clear();
		if (enabled)
			newRound();

	}

	// draw the lottery (update the actual interface)
	private static void draw(Player c) {
		c.getPA().sendFrame126("Available entry balance: @yel@" + c.entriesCurrency, 30918);
		c.getPA().sendString(30919, "Single entry cost: @yel@" + entryCost);
		c.getPA().sendString(30920, "Total entries: @yel@" + getTotalEntries());
		c.getPA().sendString(30921, "Time left to draw: @yel@" + getTimeLeft());

//        c.getPA().sendString(30922, "You have " + (MAXIMUM_ENTRIES - getEntriesForUsername(c.getUsername())) + " entry(s) left");
		c.getPA().sendString(30922, "");
		c.getPA().sendString(30915, "Your entries: @yel@" + getEntriesForUsername(c.getUsername()));
		if (specialPrize != null) {
			c.getPA().sendString(30917, "Unlocks at @yel@" + entriesToUnlockSpecial + "@nor@ entries");
		} else {
			c.getPA().sendString(30917, "No special prize.");
		}

		c.getPacketSender().sendItemOnInterface(30924, prize1St.getId(), random_1);
		c.getPacketSender().sendItemOnInterface(30925, prize2Nd.getId(), random_2);
		c.getPacketSender().sendItemOnInterface(30926, prize3Rd.getId(), random_3);
		c.getPacketSender().sendItemOnInterface(30927, staticPrize.getId(), random_2);

		if (specialPrize != null)
			c.getPacketSender().sendItemOnInterface(30928, staticPrize.getId(), random_2);
		else
			c.getPacketSender().sendItemsOnInterface(30928, new Item[] {});

		drawTopThree(c);

	}

	// draws the top 3
	private static void drawTopThree(Player c) {
		Map<String, Integer> newTempList = new LinkedHashMap<>();
		ENTRIES.forEach((key, entry) -> newTempList.put(key, entry.size()));
		Map<String, Integer> sortedMap = sortByValue(newTempList);
		int start = 30912;
		int pos = 1;// starting position
		int total = 3; // max to show
		boolean[] isTaken = new boolean[total]; // if spot isn't free
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			c.getPA().sendString(start++, pos + ") " + getColorForPos(pos) + Misc.ucFirst(entry.getKey())
					+ " @nor@- @gre@" + entry.getValue());
			isTaken[pos - 1] = true;
			pos++;
			if (pos > total)
				break;
		}
		start = 30912;
		for (int i = 0; i < isTaken.length; i++) {
			if (isTaken[i]) {
				start++;
				continue;
			}
			c.getPA().sendString(start++, i + 1 + ") None");
		}
	}

	// cheapy creepy!
	private static String getTimeLeft() {
		int hours = secondsLeft / 3600;
		int minutes = (secondsLeft % 3600) / 60;
		int seconds = secondsLeft % 60;

		return format(hours) + ":" + format(minutes) + ":" + format(seconds);
	}

	private static String format(int number) {
		if (number == 0) {
			return "00";
		}
		if (number / 10 == 0) {
			return "0" + number;
		}
		return String.valueOf(number);
	}

	private static void announceTimeLeft(int secondsLeft) {
		if ((TimeUnit.SECONDS.toMinutes(secondsLeft) % 10 == 0) && (secondsLeft % 60 == 0)) {// every 10 minutes roughly
			World.sendMessage(LOTTERY_MESSAGE_SYNTAX + "<col=800000000>" + TimeUnit.SECONDS.toMinutes(secondsLeft)
					+ "</col> " + "minutes are left till the <col=800000000>Grand2");
			World.sendMessage(
					LOTTERY_MESSAGE_SYNTAX + "There are <col=800000000>" + getTotalEntries() + "</col> entries!");
		}

	}

	private static String getColorForPos(int pos) {
		return (pos == 1 ? "@gre@" : pos == 2 ? "@yel@" : "@red@");
	}

	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
}
