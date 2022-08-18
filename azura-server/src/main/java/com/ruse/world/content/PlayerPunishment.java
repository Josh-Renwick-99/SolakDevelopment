package com.ruse.world.content;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruse.engine.GameEngine;
import com.ruse.util.Misc;
import lombok.Getter;

public class PlayerPunishment {

	private static final String BAN_DIRECTORY = "./data/saves/";
	private static final String JAIL_DIRECTORY = "./data/saves/";
	private static final String MUTE_DIRECTORY = "./data/saves/";

	public static final int MAX_HOURS = 999_999_999;

	public static ArrayList<String> IPSBanned = new ArrayList<String>();
	public static ArrayList<String> IPSMuted = new ArrayList<String>();
	public static Map<String, LocalDateTime> AccountsBanned = new HashMap<>();
	public static Map<String, LocalDateTime> AccountsMuted = new HashMap<>();
	public static ArrayList<String> AccountsTempMuted = new ArrayList<String>();

	public static void init() {
		initializeList(BAN_DIRECTORY, "IPBans", IPSBanned);
		initializeList(BAN_DIRECTORY, "Bans", AccountsBanned);
		initializeList(MUTE_DIRECTORY, "IPMutes", IPSMuted);
		initializeList(MUTE_DIRECTORY, "Mutes", AccountsMuted);
		initializeList(MUTE_DIRECTORY, "TempMutes", AccountsTempMuted);
		initializeList(JAIL_DIRECTORY, "Jails", Jail.jailedPlayers);
	}

	public static void initializeList(String directory, String file, Map<String, LocalDateTime> list) {
		try {
			Misc.createFilesIfNotExist("" + directory + "" + file + ".txt", false);
			BufferedReader in = new BufferedReader(new FileReader("" + directory + "" + file + ".txt"));
			String data = null;
			while ((data = in.readLine()) != null) {
				String[] array = data.split(";");
				LocalDateTime date = LocalDateTime.parse(array[1]);
				list.put(array[0], date);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initializeList(String directory, String file, ArrayList<String> list) {
		try {
			Misc.createFilesIfNotExist("" + directory + "" + file + ".txt", false);
			BufferedReader in = new BufferedReader(new FileReader("" + directory + "" + file + ".txt"));
			String data = null;
			while ((data = in.readLine()) != null) {
				list.add(data);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addBannedIP(String IP) {
		if (!IPSBanned.contains(IP)) {
			addToFile("" + BAN_DIRECTORY + "IPBans.txt", new String[] {IP, String.valueOf(MAX_HOURS)});
			IPSBanned.add(IP);
		}
	}

	public static void addMutedIP(String IP) {
		if (!IPSMuted.contains(IP)) {
			addToFile("" + MUTE_DIRECTORY + "IPMutes.txt", new String[] {IP, String.valueOf(MAX_HOURS)});
			IPSMuted.add(IP);
		}
	}

	public static void ban(String p, LocalDateTime hours) {
		p = Misc.formatPlayerName(p.toLowerCase());
		if (AccountsBanned.get(p) == null) {
			addToFile("" + BAN_DIRECTORY + "Bans.txt", new String[] {p, hours.toString()});
			AccountsBanned.put(p, hours);
		}
	}

	public static void mute(String p, LocalDateTime hours) {
		p = Misc.formatPlayerName(p.toLowerCase());
		if (AccountsMuted.get(p) == null) {
			addToFile("" + MUTE_DIRECTORY + "Mutes.txt", new String[] {p, hours.toString()});
			AccountsMuted.put(p, hours);
		}
	}

	public static void tempMute(String p) {
		p = Misc.formatPlayerName(p.toLowerCase());
		if (!AccountsTempMuted.contains(p)) {
			addToFile("" + MUTE_DIRECTORY + "TempMutes.txt", new String[] {p, String.valueOf(1)});
			AccountsTempMuted.add(p);
		}
		long start = System.currentTimeMillis();
	}

	public static boolean banned(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsBanned.get(player) != null && !LocalDateTime.now().isAfter(AccountsBanned.get(player));
	}
	public static boolean isReadyForUnban(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsBanned.get(player) != null && LocalDateTime.now().isAfter(AccountsBanned.get(player));
	}
	public static boolean isReadyForUnmute(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsMuted.get(player) != null && LocalDateTime.now().isAfter(AccountsMuted.get(player));
	}
	public static boolean tempMuted(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsTempMuted.contains(player);
	}

	public static boolean muted(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		return AccountsMuted.get(player) != null && !LocalDateTime.now().isAfter(AccountsMuted.get(player));
	}

	public static boolean IPBanned(String IP) {
		return IPSBanned.contains(IP);
	}

	public static boolean IPMuted(String IP) {
		return IPSMuted.contains(IP);
	}

	public static void unban(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		deleteFromFile("" + BAN_DIRECTORY + "Bans.txt", player);
		AccountsBanned.remove(player);
	}

	public static void unmute(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		deleteFromFile("" + MUTE_DIRECTORY + "Mutes.txt", player);
		AccountsMuted.remove(player);
	}

	public static void unTempMute(String player) {
		player = Misc.formatPlayerName(player.toLowerCase());
		deleteFromFile(MUTE_DIRECTORY + "TempMutes.txt", player);
		AccountsTempMuted.remove(player);
	}

	public static void reloadIPBans() {
		AccountsBanned.clear();
		initializeList(BAN_DIRECTORY, "Bans", AccountsBanned);
		IPSBanned.clear();
		initializeList(BAN_DIRECTORY, "IPBans", IPSBanned);
	}

	public static void reloadIPMutes() {
		IPSMuted.clear();
		initializeList(MUTE_DIRECTORY, "IPMutes", IPSMuted);
	}

	public static void deleteFromFile(String file, String name) {
		GameEngine.submit(() -> {
			try {
				BufferedReader r = new BufferedReader(new FileReader(file));
				ArrayList<String> contents = new ArrayList<String>();
				while (true) {
					String line = r.readLine();
					String lineUser = line;
					if (line == null) {
						break;
					} else {
						line = line.trim();
						lineUser = line.substring(0, line.indexOf(";"));
					}
					if (!lineUser.equalsIgnoreCase(name)) {
						contents.add(line);
					}
				}
				r.close();
				BufferedWriter w = new BufferedWriter(new FileWriter(file));
				for (String line : contents) {
					w.write(line, 0, line.length());
					w.write(System.lineSeparator());
				}
				w.flush();
				w.close();
			} catch (Exception e) {
			}
		});
	}

	public static void addToFile(String file, String[] data) {
		GameEngine.submit(() -> {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				try {
					out.write(data[0]+";"+data[1]);
					out.write(System.lineSeparator());
				} finally {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public static class Jail {
		private static final Map<String, LocalDateTime> jailedPlayers = new HashMap<>();

		public static void unJail(String player) {
			player = Misc.formatPlayerName(player.toLowerCase());
			deleteFromFile("" + JAIL_DIRECTORY + "Jails.txt", player);
			jailedPlayers.remove(player);
		}

		public static boolean isJailed(String player) {
			player = Misc.formatPlayerName(player.toLowerCase());
			return jailedPlayers.get(player) != null && !LocalDateTime.now().isAfter(jailedPlayers.get(player));
		}
		public static boolean isReadyForParole(String player) {
			player = Misc.formatPlayerName(player.toLowerCase());
			return jailedPlayers.get(player) != null && LocalDateTime.now().isAfter(jailedPlayers.get(player));
		}
		public static void jailPlayer(String p, LocalDateTime hours) {
			p = Misc.formatPlayerName(p.toLowerCase());
			if (jailedPlayers.get(p) == null) {
				addToFile("" + JAIL_DIRECTORY + "Jails.txt", new String[] {p, hours.toString()});
				jailedPlayers.put(p, hours);
			}
		}

		public static void reloadJails() {
			jailedPlayers.clear();
			initializeList(JAIL_DIRECTORY, "Jails", jailedPlayers);
		}
	}

}
