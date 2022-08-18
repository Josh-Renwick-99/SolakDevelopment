package com.ruse.world.allornothing;

import com.ruse.model.Item;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Grant_ | Rune-server.ee | on 2/22/2018.
 */
public class DoubleOrNothing {

	public static final int INTERFACE = 22087;

	public static final int COMMON = 1;
	public static final int UNCOMMON = 5;
	public static final int ULTIMATE = 10;
	public static final int LEGENDARY = 250;

	public static final int BOX_ITEM = 19118;

	public static ArrayList<AONItem> gameItems = new ArrayList<>();

	/**
	 * Opening the interface
	 * 
	 * @param c
	 */
	public static void open(Player c) {
		if (c.getInventory().getFreeSlots() == 0) {
			c.sendMessage("Please have atleast 1 free inventory slot before doing this!");
			return;
		}

		// Remove box item here!
		c.getInventory().delete(BOX_ITEM, 1);

		AONType itemTypeToGrab = findType();
		AONType itemTypeToGamble = findForGamble(itemTypeToGrab);
		Item[] itemsToSend = new Item[30];

		int itemsAdded = 0;
		for (int i = 0; i < gameItems.size(); i++) {
			if (gameItems.get(i).getType() == itemTypeToGamble) {
				itemsToSend[itemsAdded] = gameItems.get(i).getItem();
				itemsAdded++;
			}
		}

		if (itemsAdded < 30) {
			for (int i = itemsAdded; i < 30; i++) {
				itemsToSend[i] = new Item(-1, 0);
			}
		}

		int itemToReward = findItem(itemTypeToGrab);

		c.getPA().sendItemsOnInterface(22238, itemsToSend);
		c.getPA().sendItemOnInterface(22105, itemToReward, 1);
		c.getPA().sendFrame126("You've won a " + ItemDefinition.forId(itemToReward).getName() + "!", 22094);

		c.aonBoxItem = itemToReward;
		c.aonBoxType = itemTypeToGrab;
		c.aonBoxTypeGamble = itemTypeToGamble;
		writeFile(c);

		int offset = 125 - gameItems.size();
		for (int i = 0; i < gameItems.size(); i++) {
			c.getPA().sendFrame126(
					gameItems.get(i).getType().getColor() + gameItems.get(i).getItem().getDefinition().getName(),
					22113 + i);
		}

		for (int i = 0; i < offset; i++) {
			c.getPA().sendFrame126("", 22113 + gameItems.size() + i);
		}
		c.getPA().sendInterface(INTERFACE);
	}

	/**
	 * Finds the initial type, always common, but chance of uncommon, ultimate etc.
	 * 
	 * @return
	 */
	private static AONType findType() {
		AONType typeToReturn = AONType.COMMON;
		if (Misc.random(UNCOMMON) == 1) {
			typeToReturn = AONType.UNCOMMON;
		}

		if (Misc.random(ULTIMATE) == 1) {
			typeToReturn = AONType.ULTIMATE;
		}

		if (Misc.random(LEGENDARY) == 1) {
			typeToReturn = AONType.LEGENDARY;
		}

		return typeToReturn;
	}

	/**
	 * Finds the type if the player gambles
	 * 
	 * @param foundType
	 * @return
	 */
	private static AONType findForGamble(AONType foundType) {
		switch (foundType) {
		case COMMON:
			return AONType.UNCOMMON;
		case UNCOMMON:
			return AONType.ULTIMATE;
		case ULTIMATE:
			return AONType.LEGENDARY;
		case LEGENDARY:
			return AONType.DREAM;
		default:
			return AONType.UNCOMMON;
		}
	}

	/**
	 * Finds the first item based on the type supplied
	 * 
	 * @param type
	 * @return
	 */
	private static int findItem(AONType type) {
		ArrayList<AONItem> toRandomize = new ArrayList<>();
		for (AONItem aon : gameItems) {
			if (aon.getType() == type) {
				toRandomize.add(aon);
			}
		}

		return toRandomize.get(Misc.random(toRandomize.size() - 1)).getItem().getId();
	}

	/**
	 * Find an item if the player decides to gamble, returns -1 if they get nothing
	 * 
	 * @param type
	 * @return
	 */
	private static int findGambleItem(AONType type) {
		int random = Misc.random(100);
		if (random >= 0 && random <= type.getGamblePercentage() - 1) {
			return findItem(type);
		}
		return -1;
	}

	/**
	 * Initializes our data reading
	 */
	public static void initialize() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("./data/aon_box_data.txt")));
			String len = "";
			AONType currentType = null;
			while ((len = br.readLine()) != null) {
				try {
					int itemID = Integer.parseInt(len);
					gameItems.add(new AONItem(new Item(itemID), currentType));
				} catch (NumberFormatException e) {
					currentType = matchesType(len);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Matches the type to a supplied string (when reading in data file)
	 * 
	 * @param currentLine
	 * @return
	 */
	public static AONType matchesType(String currentLine) {
		for (AONType type : AONType.values()) {
			if (type.name().equals(currentLine)) {
				return type;
			}
		}

		return null;
	}

	/**
	 * Handles the gamble option
	 * 
	 * @param c
	 */
	public static void handleGamble(Player c) {
		c.getPA().sendInterfaceRemoval();
		removeFromFile(c);
		c.aonBoxItem = 0;
		int newRandomItem = findGambleItem(c.aonBoxTypeGamble);
		c.aonBoxType = null;
		if (newRandomItem == -1) {
			c.sendMessage("You were unlucky and have won nothing!");
			c.aonBoxTypeGamble = null;
			return;
		} else {
			c.getInventory().add(newRandomItem, 1);
			c.sendMessage("You have gotten lucky and won a " + ItemDefinition.forId(newRandomItem).getName() + "!");
			if (c.aonBoxTypeGamble == AONType.ULTIMATE || c.aonBoxTypeGamble == AONType.LEGENDARY
					|| c.aonBoxTypeGamble == AONType.DREAM) {
				announce("[<col=16711680>DON BOX</col>] <col=16711680>" + Misc.optimizeText(c.getUsername())
						+ " has gambled and won a [" + c.aonBoxTypeGamble + "] <col=75695415>"
						+ ItemDefinition.forId(newRandomItem).getName() + "!");
			}
			c.aonBoxTypeGamble = null;
			return;
		}
	}

	/**
	 * Handles the keep option
	 * 
	 * @param c
	 */
	public static void handleKeep(Player c) {
		c.getPA().sendInterfaceRemoval();
		c.getInventory().add(c.aonBoxItem, 1);
		c.sendMessage("You keep the " + ItemDefinition.forId(c.aonBoxItem).getName() + "!");
		if (c.aonBoxType == AONType.LEGENDARY || c.aonBoxType == AONType.ULTIMATE) {
			announce(
					"[<col=16711680>DON BOX</col>] <col=16711680>" + Misc.optimizeText(c.getUsername()) + " has won a ["
							+ c.aonBoxType + "] <col=938999>" + ItemDefinition.forId(c.aonBoxItem).getName() + "!");
		}
		removeFromFile(c);
		c.aonBoxItem = 0;
		c.aonBoxType = null;
		c.aonBoxTypeGamble = null;
	}

	/**
	 * Announcer
	 */
	public static void announce(String text) {
		World.sendMessage(text);
	}

	/**
	 * Used for testing cases
	 * 
	 * @param c
	 * @param trials
	 */
	public static void runTest(Player c, int trials) {
		if (trials > 1000) {
			c.sendMessage("Limit of 1000 openings");
			return;
		}

		HashMap<String, Integer> entries = new HashMap<String, Integer>();
		entries.put("COMMON", 0);
		entries.put("UNCOMMON", 0);
		entries.put("ULTIMATE", 0);
		entries.put("LEGENDARY", 0);

		for (int i = 0; i < trials; i++) {
			AONType itemTypeToGrab = findType();

			if (entries.get(itemTypeToGrab.name()) != null) {
				entries.put(itemTypeToGrab.name(), entries.get(itemTypeToGrab.name()) + 1);
			} else {
				entries.put(itemTypeToGrab.name(), 1);
			}
		}

		Iterator it = entries.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String key = (String) pair.getKey();
			int value = (Integer) pair.getValue();
			double percentage = (double) value / (double) trials;
			percentage = percentage * 100;
			c.sendMessage("[" + pair.getValue() + "] " + percentage + "% [" + key + "]");
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	/**
	 * Writes to the file when a box is opened
	 * 
	 * @param c
	 */
	public static void writeFile(Player c) {
		File file = new File("./data/aon_box_backup.txt");

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(c.getUsername() + "\t" + c.aonBoxItem + "\n");

			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes a player from the file if they get there reward successfully
	 * 
	 * @param c
	 */
	public static void removeFromFile(Player c) {
		File inputFile = new File("./data/aon_box_backup.txt");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));

			String lineToRemove = c.getUsername() + "\t" + c.aonBoxItem;
			String currentLine;

			String newFileContent = "";

			while ((currentLine = reader.readLine()) != null) {
				// trim newline when comparing with lineToRemove
				String trimmedLine = currentLine.trim();
				if (trimmedLine.equals(lineToRemove))
					continue;
				newFileContent = currentLine + System.getProperty("line.separator");
			}

			reader.close();

			BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile));
			writer.write(newFileContent);

			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks the file that stores the rolls incase of DC
	 */
	/*
	 * public static void checkFile() { try { BufferedReader br = new
	 * BufferedReader(new FileReader(new File("./data/aon_box_backup.txt"))); String
	 * line; while ((line = br.readLine()) != null) { String components[] =
	 * line.split("\t"); ItemSet s = new ItemSet(); s.add(new
	 * CollectedGameItem(Integer.parseInt(components[1]), 1, Types.AON_BOX));
	 * CollectionBox.save(components[0], s); } br.close();
	 * 
	 * BufferedWriter bw = new BufferedWriter(new FileWriter(new
	 * File("./data/aon_box_backup.txt"))); bw.write(""); bw.flush(); bw.close(); }
	 * catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException
	 * e) { e.printStackTrace(); } }
	 */

	public static ArrayList<String> getAllItemNames() {
		ArrayList<String> items = new ArrayList<>();
		for (AONItem x : gameItems) {
			items.add(x.getType().getColor() + x.getItem().getDefinition().getName());
		}
		return items;
	}
}
