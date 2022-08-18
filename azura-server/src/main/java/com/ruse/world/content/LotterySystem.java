package com.ruse.world.content;

import com.ruse.util.Misc;
import com.ruse.util.RandomUtility;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class LotterySystem {

	/**
	 * Author @Suic
	 */

	public static final Path LOTTERY_DATA = Paths.get("data", "lottery_data.txt");
	public static final Path LOTTERY_WINNERS = Paths.get("data", "lottery_winners.txt");

	private static Map<String, Integer> entries = new HashMap<>();

	public static void addUser(Player player, int amount) {
		entries.merge(player.getUsername(), amount, Integer::sum);
		player.sendMessage("You have bought: @blu@" + amount + "@bla@ tickets");
	}

	public static void loadTickets() {
		try {
			Misc.createFilesIfNotExist("./data/lottery_data.txt", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try(Stream<String> lines = Files.lines(LOTTERY_DATA)) {
			lines.forEach(line -> {
				String[] data = line.split(",");
				entries.put(data[0], Integer.parseInt(data[1]));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveTickets() { // ok i brb  but test it, it should work now.
		entries.entrySet().forEach(entry -> {
			try {
				Files.write(LOTTERY_DATA, (entry.getKey() + "," + entry.getValue() + "\n").getBytes(),
						StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static void pickWinner() {
//i just finished the lottery also added the trivia one to quest tab  sec lemme add something to lottery first
		try {
			int totalTickets = entries.values().stream().mapToInt(Integer::intValue).sum();
			int winnerTicket = RandomUtility.exclusiveRandom(0, totalTickets);
			int currentTicket = 0;

			for (Entry<String, Integer> userEntry : entries.entrySet()) {
				int tickets = userEntry.getValue();
				currentTicket += tickets;
				if (currentTicket >= winnerTicket) {
					String winner = userEntry.getKey();//msg me on discord when ur back
					Files.write(LOTTERY_WINNERS, (winner + "," + getTotalPrizepool() + "\n").getBytes(),
							StandardOpenOption.APPEND);
					World.sendMessage(
							"<img=5>@blu@[LOTTERY SYSTEM]<img=5> @red@" + winner + " @blu@Has won the lottery! Pot: " + Misc.formatNumber(getTotalPrizepool()));
					
					entries.clear();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void claimReward(Player player) {
		String name = player.getUsername();
		try {
			List<String> winners = Files.readAllLines(LOTTERY_WINNERS).stream().map(x -> x.substring(0, x.indexOf(",")))
					.collect(Collectors.toList());
			// System.out.println("Winners: " + Arrays.asList(winners.toArray()));
			if (winners.contains(name)) {
				player.sendMessage("Winner!");
				//player.getInventory().add(ItemDefinition.MILL_ID, getWinnerPrizePool());
				winners.remove(name);
			} else {
				player.sendMessage("You don't have any pending winnings.");
			}
			Files.write(LOTTERY_WINNERS, winners);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void claimReward(String username) {
		List<String> winnerList = new ArrayList<>();
		try (Stream<String> lines = Files.lines(LOTTERY_WINNERS)) {

			lines.forEach(line -> {
				String name = line.substring(0, line.indexOf(","));
				// System.out.println("Found name: " + name);
				if (!name.equalsIgnoreCase(username)) {
					winnerList.add(line);
				} else {
					// do logic
				}
			});
			Files.write(LOTTERY_WINNERS, winnerList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int prizePool = 0;

	private static int getWinnerPrizePool() {

		try (Stream<String> lines = Files.lines(LOTTERY_WINNERS)) {
			lines.forEach(x -> {
				String[] split = x.split(","); // split by ','
				prizePool = Integer.parseInt(split[1]);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prizePool;
	}

	public static int getCurrentTicketAmount() {
		return entries.values().stream().mapToInt(Integer::intValue).sum();
	}

	public static int getTotalPrizepool() {
		return (int) ((getCurrentTicketAmount()) * (0.9)); // 10% tax by server
	}

}
