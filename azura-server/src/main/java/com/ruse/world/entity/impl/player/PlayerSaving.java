package com.ruse.world.entity.impl.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.util.Misc;
import com.ruse.world.content.dailyTask.DailyTaskData;
import org.apache.commons.lang3.text.WordUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class PlayerSaving {

	private static final DailyTaskData[] daily = DailyTaskData.values();

	public static void save(Player player) {
		if (player.newPlayer())
			return;
		// Create the path and file objects.
		Path path = Paths.get("./data/saves/characters/", player.getUsername() + ".json");
		File file = path.toFile();
		file.getParentFile().setWritable(true);

		// Attempt to make the player save directory if it doesn't
		// exist.
		if (!file.getParentFile().exists()) {
			try {
				file.getParentFile().mkdirs();
			} catch (SecurityException e) {
				// System.out.println("Unable to create directory for player data!");
			}
		}
		try (FileWriter writer = new FileWriter(file)) {

			Gson builder = new GsonBuilder().setPrettyPrinting().create();
			JsonObject object = new JsonObject();
			object.addProperty("total-play-time-ms", player.getTotalPlayTime());
			object.addProperty("username", player.getUsername().trim());

			if (GameSettings.BCRYPT_HASH_PASSWORDS) {
				object.addProperty("hash", BCrypt.hashpw(player.getPassword(), player.getSalt()));
			} else {
				object.addProperty("password", player.getPassword().trim());
			}

			object.addProperty("email", player.getEmailAddress() == null ? "null" : player.getEmailAddress().trim());
			object.addProperty("staff-rights", player.getRights().name());			
			object.addProperty("game-mode", player.getGameMode().name());
			object.addProperty("loyalty-title", player.getLoyaltyTitle().name());
			
			/** HEX YELL COLORS **/
			object.addProperty("yellhexcolor", player.getYellHex() == null ? "ffffff" : player.getYellHex());
			object.add("position", builder.toJsonTree(player.getPosition()));
			object.addProperty("online-status", player.getRelations().getStatus().name());
			object.addProperty("given-starter", new Boolean(player.didReceiveStarter()));
			object.addProperty("has-pin2", player.getHasPin());
			object.addProperty("saved-pin2", player.getSavedPin());
			object.addProperty("saved-ip", player.getSavedIp());
			object.addProperty("donated", new Long(player.getAmountDonated()));
			object.addProperty("current-boss-task", player.getCurrentBossTask());
			object.addProperty("current-boss-amount", player.getCurrentBossTaskAmount());
			object.addProperty("global-rate", player.getPointsHandler().getGlobalRate());
			object.addProperty("peng-rate", player.getPointsHandler().getPengRate());
			object.addProperty("slayer-rate", player.getPointsHandler().getSlayerRate());
			object.addProperty("has-completed-boss-task", player.isHasPlayerCompletedBossTask());
			object.addProperty("current-daily-task-id", player.getCurrentDailyTask());
			object.addProperty("current-daily-task-amount", player.getCurrentDailyTaskAmount());
			if (player.getDailyTasks() != null) {
				object.addProperty("current-daily-task-data", player.getDailyTasks().getNpcId());
			}
			object.addProperty("current-daily-task-x-pos", player.getxPosDailyTask());
			object.addProperty("current-daily-task-y-pos", player.getyPostDailyTask());
			object.addProperty("current-daily-task-z-pos", player.getzPosDailyTask());
			object.addProperty("current-daily-task-reward", player.getRewardDailyTask());
			
			object.add("uim-bank", builder.toJsonTree(player.getUimBankItems()));
			
			object.addProperty("dg-floor", player.getDungeoneeringFloor());
			object.addProperty("dg-prestige", player.getDungeoneeringPrestige());
			object.addProperty("clue-task", player.getCurrentClue().getCurrentTask().name());
			object.addProperty("clue-kc", player.getCurrentClue().getAmountToSlay());
			object.addProperty("daysVoted", player.getDaysVoted());
			object.addProperty("totalTimesClaimed", player.getTotalTimesClaimed());
			object.addProperty("longestDaysVoted", player.getLongestDaysVoted());
			object.addProperty("timeUntilNextReward", player.getTimeUntilNextReward());
			object.addProperty("lastVoted", player.getLastVoted());
			object.addProperty("current-voting-streak", player.getCurrentVotingStreak());
			object.addProperty("entriesCurrency", player.getEntriesCurrency());
			object.addProperty("amount-donated-today", player.getAmountDonatedToday());
			object.addProperty("claimed-first", player.claimedFirst);
			object.addProperty("claimed-second", player.claimedSecond);
			object.addProperty("claimed-third", player.claimedThird);
			object.addProperty("last-donation", player.lastDonation);
			object.addProperty("last-time-reset", player.lastTimeReset);
			object.addProperty("lastlogin", new Long(player.lastLogin));
			object.addProperty("lastdailyclaim", new Long(player.lastDailyClaim));
			object.addProperty("lastvotetime", new Long(player.lastVoteTime));
			object.addProperty("hasvotedtoday", new Boolean(player.hasVotedToday));
			object.addProperty("day1claimed", new Boolean(player.day1Claimed));
			object.addProperty("day2claimed", new Boolean(player.day2Claimed));
			object.addProperty("day3claimed", new Boolean(player.day3Claimed));
			object.addProperty("day4claimed", new Boolean(player.day4Claimed));
			object.addProperty("day5claimed", new Boolean(player.day5Claimed));
			object.addProperty("day6claimed", new Boolean(player.day6Claimed));
			object.addProperty("day7claimed", new Boolean(player.day7Claimed));
			object.addProperty("minutes-bonus-exp", new Integer(player.getMinutesBonusExp()));
			object.addProperty("minutes-voting-dr", new Integer(player.getMinutesVotingDR()));
			object.addProperty("minutes-voting-dmg", new Integer(player.getMinutesVotingDMG()));
			object.addProperty("total-gained-exp", new Long(player.getSkillManager().getTotalGainedExp()));
			object.addProperty("barrows-points", new Integer(player.getPointsHandler().getBarrowsPoints()));
			object.addProperty("donator-points", new Integer(player.getPointsHandler().getDonatorPoints()));
			object.addProperty("Skilling-points", new Integer(player.getPointsHandler().getSkillingPoints()));
			object.addProperty("prestige-points", new Integer(player.getPointsHandler().getPrestigePoints()));
			object.addProperty("achievement-points", new Integer(player.getPointsHandler().getAchievementPoints()));
			object.addProperty("dung-tokens", new Integer(player.getPointsHandler().getDungeoneeringTokens()));
			object.addProperty("commendations", new Integer(player.getPointsHandler().getCommendations()));
			object.addProperty("loyalty-points", new Integer(player.getPointsHandler().getLoyaltyPoints()));
			object.addProperty("total-loyalty-points",
					new Double(player.getAchievementAttributes().getTotalLoyaltyPointsEarned()));
			object.addProperty("godmodetime", player.getGodModeTimer());
			object.addProperty("voting-points", new Integer(player.getPointsHandler().getVotingPoints()));
			object.add("tasks-completion", builder.toJsonTree(player.getStarterTaskAttributes().getCompletion()));
			object.add("tasks-progress", builder.toJsonTree(player.getStarterTaskAttributes().getProgress()));
			object.addProperty("total-prestiges", new Integer(player.getPointsHandler().getTotalPrestiges()));
			object.addProperty("slayer-spree", new Integer(player.getPointsHandler().getSlayerSpree()));

			object.addProperty("npc-killcount", new Integer(player.getPointsHandler().getNPCKILLCount()));
			object.addProperty("spawn-killcount", new Integer(player.getPointsHandler().getSPAWNKILLCount()));
			object.addProperty("lord-killcount", new Integer(player.getPointsHandler().getLORDKILLCount()));
			object.addProperty("demon-killcount", new Integer(player.getPointsHandler().getDEMONKILLCount()));
			object.addProperty("dragon-killcount", new Integer(player.getPointsHandler().getDRAGONKILLCount()));
			object.addProperty("beast-killcount", new Integer(player.getPointsHandler().getBEASTKILLCount()));
			object.addProperty("king-killcount", new Integer(player.getPointsHandler().getKINGKILLCount()));
			object.addProperty("avatar-killcount", new Integer(player.getPointsHandler().getAVATARKILLCount()));
			object.addProperty("angel-killcount", new Integer(player.getPointsHandler().getANGELKILLCount()));
			object.addProperty("lucien-killcount", new Integer(player.getPointsHandler().getLUCIENKILLCount()));
			object.addProperty("hercules-killcount", new Integer(player.getPointsHandler().getHERCULESKILLCount()));
			object.addProperty("satan-killcount", new Integer(player.getPointsHandler().getSATANKILLCount()));
			object.addProperty("zeus-killcount", new Integer(player.getPointsHandler().getZEUSKILLCount()));
			object.addProperty("mini-lucifer-killcount", player.getPointsHandler().getMiniLuciferkillcount());
			object.addProperty("lucifer-killcount", player.getPointsHandler().getLuciferkillcount());

			object.addProperty("event-points", new Integer(player.getPointsHandler().getEventPoints()));
			object.addProperty("boss-points", new Integer(player.getPointsHandler().getBossPoints()));
			object.addProperty("shilling-rate", new Integer(player.getPointsHandler().getSHILLINGRate()));

			object.addProperty("slayer-points", new Integer(player.getPointsHandler().getSlayerPoints()));
			object.addProperty("pk-points", new Integer(player.getPointsHandler().getPkPoints()));
			object.addProperty("player-kills", new Integer(player.getPlayerKillingAttributes().getPlayerKills()));
			object.addProperty("player-killstreak",
					new Integer(player.getPlayerKillingAttributes().getPlayerKillStreak()));
			object.addProperty("player-deaths", new Integer(player.getPlayerKillingAttributes().getPlayerDeaths()));
			object.addProperty("target-percentage",
					new Integer(player.getPlayerKillingAttributes().getTargetPercentage()));
			object.addProperty("bh-rank", new Integer(player.getAppearance().getBountyHunterSkull()));
			object.addProperty("gender", player.getAppearance().getGender().name());
			object.addProperty("spell-book", player.getSpellbook().name());
			object.addProperty("prayer-book", player.getPrayerbook().name());
			object.addProperty("running", new Boolean(player.isRunning()));
			object.addProperty("run-energy", new Integer(player.getRunEnergy()));
			object.addProperty("music", new Boolean(player.musicActive()));
			object.addProperty("sounds", new Boolean(player.soundsActive()));
			object.addProperty("auto-retaliate", new Boolean(player.isAutoRetaliate()));
			object.addProperty("xp-locked", new Boolean(player.experienceLocked()));
			object.addProperty("veng-cast", new Boolean(player.hasVengeance()));
			object.addProperty("last-veng", new Long(player.getLastVengeance().elapsed()));
			object.addProperty("fight-type", player.getFightType().name());
			object.addProperty("sol-effect", new Integer(player.getStaffOfLightEffect()));
			object.addProperty("skull-timer", new Integer(player.getSkullTimer()));
			object.addProperty("accept-aid", new Boolean(player.isAcceptAid()));
			object.addProperty("poison-damage", new Integer(player.getPoisonDamage()));
			object.addProperty("poison-immunity", new Integer(player.getPoisonImmunity()));
			object.addProperty("overload-timer", new Integer(player.getOverloadPotionTimer()));

			object.addProperty("double-dr-timer", new Integer(player.getDoubleDRTimer()));
			object.addProperty("double-ddr-timer", new Integer(player.getDoubleDDRTimer()));
			object.addProperty("double-dmg-timer", new Integer(player.getDoubleDMGTimer()));

			object.addProperty("fire-immunity", new Integer(player.getFireImmunity()));
			object.addProperty("fire-damage-mod", new Integer(player.getFireDamageModifier()));
			object.addProperty("prayer-renewal-timer", new Integer(player.getPrayerRenewalPotionTimer()));
			object.addProperty("teleblock-timer", new Integer(player.getTeleblockTimer()));
			object.addProperty("special-amount", new Integer(player.getSpecialPercentage()));
			object.addProperty("entered-gwd-room",
					new Boolean(player.getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()));
			object.addProperty("gwd-altar-delay",
					new Long(player.getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay()));
			object.add("gwd-killcount",
					builder.toJsonTree(player.getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()));
			object.addProperty("effigy", new Integer(player.getEffigy()));
			object.addProperty("summon-npc",
					new Integer(player.getSummoning().getFamiliar() != null
							? player.getSummoning().getFamiliar().getSummonNpc().getId()
							: -1));
			object.addProperty("summon-death",
					new Integer(player.getSummoning().getFamiliar() != null
							? player.getSummoning().getFamiliar().getDeathTimer()
							: -1));
			object.addProperty("process-farming", new Boolean(player.shouldProcessFarming()));
			object.addProperty("clanchat", player.getClanChatName() == null ? "null" : player.getClanChatName().trim());
			object.addProperty("autocast", new Boolean(player.isAutocast()));
			object.addProperty("autocast-spell",
					player.getAutocastSpell() != null ? player.getAutocastSpell().spellId() : -1);
			object.addProperty("dfs-charges", player.getDfsCharges());
			object.addProperty("coins-gambled", new Integer(player.getAchievementAttributes().getCoinsGambled()));
			object.addProperty("slayer-master", player.getSlayer().getSlayerMaster().name());
			object.addProperty("slayer-task", player.getSlayer().getSlayerTask().name());
			object.addProperty("prev-slayer-task", player.getSlayer().getLastTask().name());
			object.addProperty("task-amount", player.getSlayer().getAmountToSlay());
			object.addProperty("task-streak", player.getSlayer().getTaskStreak());
			object.addProperty("duo-partner",
					player.getSlayer().getDuoPartner() == null ? "null" : player.getSlayer().getDuoPartner());
			object.addProperty("double-slay-xp", player.getSlayer().doubleSlayerXP);
			object.addProperty("recoil-deg", new Integer(player.getRecoilCharges()));
			object.addProperty("blowpipe-deg", new Integer(player.getBlowpipeCharges()));
			object.add("brawlers-deg", builder.toJsonTree(player.getBrawlerChargers()));
			object.add("ancient-deg", builder.toJsonTree(player.getAncientArmourCharges()));
			object.add("killed-players", builder.toJsonTree(player.getPlayerKillingAttributes().getKilledPlayers()));
			object.add("killed-gods", builder.toJsonTree(player.getAchievementAttributes().getGodsKilled()));
			object.add("vod-brother",
					builder.toJsonTree(player.getMinigameAttributes().getVoidOfDarknessAttributes().getBarrowsData()));
			object.addProperty("vod-killcount",
					new Integer(player.getMinigameAttributes().getVoidOfDarknessAttributes().getKillcount()));
			object.addProperty("hov-killcount",
					new Integer(player.getMinigameAttributes().getHallsOfValorAttributes().getKillcount()));
			object.add("barrows-brother",
					builder.toJsonTree(player.getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()));
			object.addProperty("random-coffin",
					new Integer(player.getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()));
			object.addProperty("barrows-killcount",
					new Integer(player.getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount()));
			object.add("nomad",
					builder.toJsonTree(player.getMinigameAttributes().getNomadAttributes().getQuestParts()));
			object.add("recipe-for-disaster", builder
					.toJsonTree(player.getMinigameAttributes().getRecipeForDisasterAttributes().getQuestParts()));
			object.addProperty("recipe-for-disaster-wave",
					new Integer(player.getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted()));
			object.addProperty("clue-progress", new Integer(player.getClueProgress()));
			object.add("dung-items-bound",
					builder.toJsonTree(player.getMinigameAttributes().getDungeoneeringAttributes().getBoundItems()));

			object.add("collection-data", builder.toJsonTree(player.getCollectionLogData()));
			object.add("holy-prayers-unlocked", builder.toJsonTree(player.getUnlockedHolyPrayers()));

			object.addProperty("rune-ess", new Integer(player.getStoredRuneEssence()));
			object.addProperty("pure-ess", new Integer(player.getStoredPureEssence()));
			object.addProperty("has-bank-pin", new Boolean(player.getBankPinAttributes().hasBankPin()));
			object.addProperty("last-pin-attempt", new Long(player.getBankPinAttributes().getLastAttempt()));
			object.addProperty("invalid-pin-attempts", new Integer(player.getBankPinAttributes().getInvalidAttempts()));
			object.add("bank-pin", builder.toJsonTree(player.getBankPinAttributes().getBankPin()));
			object.add("appearance", builder.toJsonTree(player.getAppearance().getLook()));
			object.add("agility-obj", builder.toJsonTree(player.getCrossedObstacles()));
			object.add("skills", builder.toJsonTree(player.getSkillManager().getSkills()));
			object.add("inventory", builder.toJsonTree(player.getInventory().getItems()));
			object.add("equipment", builder.toJsonTree(player.getEquipment().getItems()));
			object.add("preset-equipment", builder.toJsonTree(player.getPreSetEquipment().getItems()));
			object.add("offences", builder.toJsonTree(player.getOffences()));
			object.add("bank-0", builder.toJsonTree(player.getBank(0).getValidItems()));
			object.add("bank-1", builder.toJsonTree(player.getBank(1).getValidItems()));
			object.add("bank-2", builder.toJsonTree(player.getBank(2).getValidItems()));
			object.add("bank-3", builder.toJsonTree(player.getBank(3).getValidItems()));
			object.add("bank-4", builder.toJsonTree(player.getBank(4).getValidItems()));
			object.add("bank-5", builder.toJsonTree(player.getBank(5).getValidItems()));
			object.add("bank-6", builder.toJsonTree(player.getBank(6).getValidItems()));
			object.add("bank-7", builder.toJsonTree(player.getBank(7).getValidItems()));
			object.add("bank-8", builder.toJsonTree(player.getBank(8).getValidItems()));

			object.add("ge-slots", builder.toJsonTree(player.getGrandExchangeSlots()));

			/** STORE SUMMON **/
			if (player.getSummoning().getBeastOfBurden() != null) {
				object.add("store", builder.toJsonTree(player.getSummoning().getBeastOfBurden().getValidItems()));
			}
			object.add("charm-imp", builder.toJsonTree(player.getSummoning().getCharmImpConfigs()));

			object.add("friends", builder.toJsonTree(player.getRelations().getFriendList().toArray()));
			object.add("ignores", builder.toJsonTree(player.getRelations().getIgnoreList().toArray()));
			object.add("loyalty-titles", builder.toJsonTree(player.getUnlockedLoyaltyTitles()));
			object.add("kills", builder.toJsonTree(player.getKillsTracker().toArray()));
			object.add("drops", builder.toJsonTree(player.getDropLog().toArray()));
			object.add("achievements-completion",
					builder.toJsonTree(player.getAchievementAttributes().getCompletion()));
			object.add("achievements-progress", builder.toJsonTree(player.getAchievementAttributes().getProgress()));
			object.addProperty("fri13may16", new Boolean(player.didFriday13May2016())); // player.didfri13may16
			object.addProperty("spiritdebug", new Boolean(player.isSpiritDebug()));
			object.addProperty("reffered", new Boolean(player.gotReffered()));
			object.addProperty("indung", new Boolean(player.isInDung()));
			object.addProperty("toggledglobalmessages", new Boolean(player.toggledGlobalMessages()));
			object.addProperty("flying", new Boolean(player.isFlying()));
			object.addProperty("canfly", new Boolean(player.canFly()));
			object.addProperty("ghostwalking", new Boolean(player.isGhostWalking()));
			object.addProperty("canghostwalk", new Boolean(player.canGhostWalk()));
			object.addProperty("barrowschests", new Integer(player.getPointsHandler().getBarrowsChests()));
			object.addProperty("cluesteps", new Integer(player.getPointsHandler().getClueSteps()));
			object.addProperty("difficulty", player.getDifficulty().name());
			object.add("hween2016", builder.toJsonTree(player.getHween2016All()));
			object.addProperty("donehween2016", new Boolean(player.doneHween2016()));
			object.addProperty("christmas2016", new Integer(player.getChristmas2016()));
			object.addProperty("newYear2017", new Integer(player.getNewYear2017()));
			object.addProperty("easter2017", new Integer(player.getEaster2017()));
			object.add("hcimdunginventory", builder.toJsonTree(player.getDungeoneeringIronInventory().getItems()));
			object.add("hcimdungequipment", builder.toJsonTree(player.getDungeoneeringIronEquipment().getItems()));
			object.addProperty("bonecrusheffect", new Boolean(player.getBonecrushEffect()));
			object.add("p-tps", builder.toJsonTree(player.getPreviousTeleports()));
			object.addProperty("yell-tit", player.getYellTitle() == null ? "null" : player.getYellTitle());
			// doneHween2016
			// object.add("uimDungItems",
			// builder.toJsonTre1e(player.getBank(0).getValidItems()));


			object.addProperty("afkstall1", player.getAfkStallCount1());
			object.addProperty("afkstall2", player.getAfkStallCount2());
			object.addProperty("afkstall3", player.getAfkStallCount3());


			object.add("achievements-points", builder.toJsonTree(player.getAchievements().getPoints()));
			object.add("achievements-amount", builder.toJsonTree(player.getAchievements().getAmountRemaining()));
			object.add("achievements-completed", builder.toJsonTree(player.getAchievements().getCompleted()));
			object.addProperty("achievements-daily", player.getAchievements().getDailyAchievementsDate());

			object.add("progression-zones", builder.toJsonTree(player.getProgressionZones()));
			object.add("zones-complete", builder.toJsonTree(player.getZonesComplete()));

			object.addProperty("gamble-banned", player.isGambleBanned());

			object.addProperty("lucifers-unlocked", player.isUnlockedLucifers());
			object.addProperty("dark-supremes-unlocked", player.isUnlockedDarkSupreme());
			object.add("currency-pouch", builder.toJsonTree(player.getCurrencyPouch()));
			object.addProperty("lastTGloveIndex", player.lastTGloveIndex);
			if (player.getIronmanGroup() != null)
				object.addProperty("group-ironman-id", player.getIronmanGroup().getUniqueId());
			object.addProperty("group-ironman-locked", player.isGroupIronmanLocked());
			object.addProperty("lastInstanceNpc", player.lastInstanceNpc);
			object.add("daily-task", builder.toJsonTree(player.dailies));
			object.addProperty("daily-skips", player.dailySkips);
			object.addProperty("dailies-done", player.dailiesCompleted);
			object.addProperty("daily-task-info", player.taskInfo);
			object.addProperty("level-notifications", player.levelNotifications);
			object.add("dailies-received-times", builder.toJsonTree(player.getTaskReceivedTimes()));


			object.add("favorite-teleports", builder.toJsonTree(player.getFavoriteTeleports()));

			object.add("obtained-pets", builder.toJsonTree(player.getObtainedPets()));

			writer.write(builder.toJson(object));
			writer.close();

			/*
			 * Housing
			 */
			/*
			 * FileOutputStream fileOut = new FileOutputStream("./data/saves/housing/rooms/"
			 * + player.getUsername() + ".ser"); ObjectOutputStream out = new
			 * ObjectOutputStream(fileOut); out.writeObject(player.getHouseRooms());
			 * out.close(); fileOut.close();
			 * 
			 * fileOut = new FileOutputStream("./data/saves/housing/furniture/" +
			 * player.getUsername() + ".ser"); out = new ObjectOutputStream(fileOut);
			 * out.writeObject(player.getHouseFurniture()); out.close(); fileOut.close();
			 * 
			 * fileOut = new FileOutputStream("./data/saves/housing/portals/" +
			 * player.getUsername() + ".ser"); out = new ObjectOutputStream(fileOut);
			 * out.writeObject(player.getHousePortals()); out.close(); fileOut.close();
			 */
		} catch (Exception e) {
			// An error happened while saving.
			GameServer.getLogger().log(Level.WARNING, "An error has occured while saving a character file!", e);
		}
	}

	public static boolean playerExists(String p) {
		p = Misc.formatPlayerName(p.toLowerCase());
		p = WordUtils.capitalizeFully(p);
		p.replaceAll(" ", "\\ ");
		// // System.out.println("./data/saves/characters/"+p+".json ....... "+ new
		// File("./data/saves/characters/"+p+".json").exists());
		return new File("./data/saves/characters/" + p + ".json").exists();
	}
}
