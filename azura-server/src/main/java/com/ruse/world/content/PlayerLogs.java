package com.ruse.world.content;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.engine.GameEngine;
import com.ruse.model.Position;
import com.ruse.util.Misc;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class PlayerLogs {

	private static final String NPC_COORDS = "./data/saves/";
	private static final String FILE_PATH = "./data/saves/logs/";
	private static final String FILE_PATH_LOGS = "./data/saves/logs/other/";
	private static final String WORLD_FILE_PATH = "./data/saves/worldlogs/";

	public static String getTime() {
		Date getDate = new Date();
		String timeFormat = "M/d/yy hh:mma";
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		return "[" + sdf.format(getDate) + "] ";
	}

	public static void logPlayerDonations(String file, String writable) {
		if (!GameSettings.LOG_DONATIONS)
			return;

		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "donations/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "donations/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "donations.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "donations.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logPlayerGambles(String file, String writable) {
		if (!GameSettings.LOG_SPINSWIP)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "gambles/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "gambles/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "gambles.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "gambles.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logPlayerVotes(String file, String writable) {
		if (!GameSettings.LOG_SPINSWIP)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "votes/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "votes/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "votes.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "votes.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logStores(String file, String writable) {
		if (!GameSettings.LOG_BONDS)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "stores/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "stores/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "stores.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "stores.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logPlayerLoginWithIP(String file, String writable) {
		if (!GameSettings.LOG_LOGINSWIP)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "loginsWIP/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "loginsWIP/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "loginsWIP.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "loginsWIP.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logPlayerLogin(String file, String writable) {
		if (!GameSettings.LOG_LOGINS)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "logins/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "logins/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "logins.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "logins.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logNpcDrops(String file, String writable) {
		if (!GameSettings.LOG_NPCDROPS)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "npcdrops/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "npcdrops/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "npcdrops.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "npcdrops.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logUpgrades(String file, String writable) {
		if (!GameSettings.LOG_NPCDROPS)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "upgrades/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "upgrades/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "upgrades.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "upgrades.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logPickupItems(String file, String writable) {
		if (!GameSettings.LOG_PICKUPS)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "pickups/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "pickups/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "pickups.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "pickups.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logCommands(String file, String writable) {
		if (!GameSettings.LOG_COMMANDS)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "commands/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "commands/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "commands.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "commands.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logDroppedItem(String file, String writable) {
		if (!GameSettings.LOG_DROPPED)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "droppeditems/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "droppeditems/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "droppeditems.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "droppeditems.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logTrades(String file, String writable) {
		if (!GameSettings.LOG_TRADE)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "trades/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "trades/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "trades.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "trades.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logPOS(String file, String writable) {
		if (!GameSettings.LOG_POS)
			return;
		GameEngine.submit(() -> {
			try {
				FileWriter fw = new FileWriter(FILE_PATH + "pos/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "pos.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logKills(String file, String writable) {
		if (!GameSettings.LOG_KILLS)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "kills/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "kills/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "kills.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "kills.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logChat(String file, String writable) {
		if (!GameSettings.LOG_CHAT)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "chat/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "chat/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "chat.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "chat.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}


	public static void logPMS(String file, String writable) {
		if (!GameSettings.LOG_CHAT)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH + "pms/" + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH + "pms/" + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "pms.txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "pms.txt", true);
				world.write(getTime() + " [ " + file + " ] " + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void logClanMessages(String file, String writable) {
		if (!GameSettings.LOG_CLAN)
			return;
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(WORLD_FILE_PATH + "clans/" + file + ".txt", false);
				FileWriter world = new FileWriter(WORLD_FILE_PATH + "clans/" + file + ".txt", true);
				world.write(getTime() + writable + "\t");
				world.write(System.lineSeparator());
				world.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void npccoords(String file, int npcId, Position pos) {
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(NPC_COORDS + file + ".txt", false);
				FileWriter fw = new FileWriter(NPC_COORDS + file + ".txt", true);

				fw.write("    {    ");
				fw.write(System.lineSeparator());
				fw.write("    \"npc-id\": " + npcId + ",");
				fw.write(System.lineSeparator());
				fw.write("		\"face\": \"SOUTH\",");
				fw.write(System.lineSeparator());
				fw.write("        \"position\": { \"x\": " + pos.getX() + ", \"y\": " + pos.getY() + ", \"z\": " + pos.getZ() + " },");
				fw.write(System.lineSeparator());
				fw.write("		\"walking-policy\": {\"coordinate\": false,\"radius\": 1");
				fw.write(System.lineSeparator());
				fw.write("        }");
				fw.write(System.lineSeparator());
				fw.write("    },");
				fw.write(System.lineSeparator());

				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	public static void log(String file, String writable) {
		GameEngine.submit(() -> {
			try {
				Misc.createFilesIfNotExist(FILE_PATH_LOGS, true);
				Misc.createFilesIfNotExist(FILE_PATH_LOGS + file + ".txt", false);
				FileWriter fw = new FileWriter(FILE_PATH_LOGS + file + ".txt", true);
				fw.write(getTime() + writable + "\t");
				fw.write(System.lineSeparator());
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
