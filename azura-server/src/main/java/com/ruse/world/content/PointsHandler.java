package com.ruse.world.content;

import com.ruse.world.entity.impl.player.Player;

public class PointsHandler {

	private Player p;

	public PointsHandler(Player p) {
		this.p = p;
	}

	public void reset() {
		dungTokens = commendations = (int) (loyaltyPoints = votingPoints = slayerPoints = pkPoints = 0);
		p.getPlayerKillingAttributes().setPlayerKillStreak(0);
		p.getPlayerKillingAttributes().setPlayerKills(0);
		p.getPlayerKillingAttributes().setPlayerDeaths(0);
		p.getDueling().arenaStats[0] = p.getDueling().arenaStats[1] = 0;
	}

	/*
	 * public PointsHandler refreshPanel() { p.getPacketSender().sendString(39170,
	 * "@or2@Prestige Points: @yel@"+prestigePoints);
	 * p.getPacketSender().sendString(39171,
	 * "@or2@Commendations: @yel@ "+commendations);
	 * p.getPacketSender().sendString(39172,
	 * "@or2@Loyalty Points: @yel@"+(int)loyaltyPoints);
	 * p.getPacketSender().sendString(39173,
	 * "@or2@Dung. Tokens: @yel@ "+dungTokens);
	 * p.getPacketSender().sendString(39174,
	 * "@or2@Voting Points: @yel@ "+votingPoints);
	 * p.getPacketSender().sendString(39175,
	 * "@or2@Slayer Points: @yel@"+slayerPoints);
	 * p.getPacketSender().sendString(39176,
	 * "@or2@Barrows Points: @yel@"+barrowsPoints);
	 * p.getPacketSender().sendString(39177,
	 * "@or2@Member Points: @yel@"+memberPoints);
	 * p.getPacketSender().sendString(39178, "@or2@Pk Points: @yel@"+pkPoints);
	 * p.getPacketSender().sendString(39179,
	 * "@or2@Wilderness Killstreak: @yel@"+p.getPlayerKillingAttributes().
	 * getPlayerKillStreak()); p.getPacketSender().sendString(39180,
	 * "@or2@Wilderness Kills: @yel@"+p.getPlayerKillingAttributes().getPlayerKills(
	 * )); p.getPacketSender().sendString(39181,
	 * "@or2@Wilderness Deaths: @yel@"+p.getPlayerKillingAttributes().
	 * getPlayerDeaths()); p.getPacketSender().sendString(39182,
	 * "@or2@Arena Victories: @yel@"+p.getDueling().arenaStats[0]);
	 * p.getPacketSender().sendString(39183,
	 * "@or2@Arena Points: @yel@"+p.getDueling().arenaStats[1]);
	 * //p.getPacketSender().sendString(30184,
	 * "@or2@Skilling points: @yel@"+skillingPoints);
	 * //p.getPacketSender().sendString(30185,
	 * "@or2@Barrows chests opened: @yel@"+barrowsChests); return this; }
	 */

	private int barrowsPoints;
	private int prestigePoints;
	private int slayerPoints;
	private int commendations;
	private int dungTokens;
	private int pkPoints;
	private double loyaltyPoints;
	private int votingPoints;
	private int eventpoints;
	private int bosspoints;
	private int spawnkillcount;
	private int lordkillcount;
	private int demonkillcount;
	private int dragonkillcount;
	private int beastkillcount;
	private int kingkillcount;
	private int avatarkillcount;
	private int angelkillcount;
	private int lucienkillcount;
	private int herculeskillcount;
	private int satankillcount;
	private int zeuskillcount;
	private int miniLuciferkillcount;
	private int luciferkillcount;
	private int shillingrate;
	private int npckillcount;
	private int totalprestiges;
	private int totalsprees;
	private int achievementPoints;
	private int donatorPoints;
	private int skillingPoints;
	private int barrowsChests;
	private int clueSteps;

	public int getClueSteps() {
		return clueSteps;
	}

	public void setClueSteps(int points, boolean add) {
		if (add) {
			this.clueSteps += points;
		} else {
			this.clueSteps = points;
		}
	}

	public int getBarrowsChests() {
		return barrowsChests;
	}

	public void setBarrowsChests(int points, boolean add) {
		if (add)
			this.barrowsChests += points;
		else
			this.barrowsChests = points;
	}

	public int getDonatorPoints() {
		return donatorPoints;
	}

	public void setDonatorPoints(int points, boolean add) {
		if (add)
			this.donatorPoints += points;
		else
			this.donatorPoints = points;
	}

	public int getBarrowsPoints() {
		return this.barrowsPoints;
	}

	public void setBarrowsPoints(int points, boolean add) {
		if (add)
			this.barrowsPoints += points;
		else
			this.barrowsPoints = points;
	}

	public int getPrestigePoints() {
		return prestigePoints;
	}

	public void setPrestigePoints(int points, boolean add) {
		if (add)
			this.prestigePoints += points;
		else
			this.prestigePoints = points;
	}

	public int getSlayerPoints() {
		return slayerPoints;
	}

	public void setSlayerPoints(int slayerPoints, boolean add) {
		if (add)
			this.slayerPoints += slayerPoints;
		else
			this.slayerPoints = slayerPoints;
	}

	public int getCommendations() {
		return this.commendations;
	}

	public void setCommendations(int commendations, boolean add) {
		if (add)
			this.commendations += commendations;
		else
			this.commendations = commendations;
	}

	public int getLoyaltyPoints() {
		return (int) this.loyaltyPoints;
	}

	public void setLoyaltyPoints(int points, boolean add) {
		if (add)
			this.loyaltyPoints += points;
		else
			this.loyaltyPoints = points;
	}

	public void incrementLoyaltyPoints(double amount) {
		this.loyaltyPoints += amount;
	}

	public int getPkPoints() {
		return this.pkPoints;
	}

	public void setPkPoints(int points, boolean add) {
		if (add)
			this.pkPoints += points;
		else
			this.pkPoints = points;
	}

	public int getDungeoneeringTokens() {
		return dungTokens;
	}

	public void setDungeoneeringTokens(int dungTokens, boolean add) {
		if (add)
			this.dungTokens += dungTokens;
		else
			this.dungTokens = dungTokens;
	}

	public int getEventPoints() {
		return eventpoints;
	}

	public void setEventPoints(int eventpoints) {
		this.eventpoints = eventpoints;
	}

	public void incrementEventPoints() {
		this.eventpoints++;
	}

	public void incrementEventPoints(int amt) {
		this.eventpoints += amt;
	}

	public void setEventPoints(int points, boolean add) {
		if (add)
			this.eventpoints += points;
		else
			this.eventpoints = points;
	}

	public int getVotingPoints() {
		return votingPoints;
	}

	public void setVotingPoints(int votingPoints) {
		this.votingPoints = votingPoints;
	}

	public void incrementVotingPoints() {
		this.votingPoints++;
	}

	public void incrementVotingPoints(int amt) {
		this.votingPoints += amt;
	}

	public void setVotingPoints(int points, boolean add) {
		if (add)
			this.votingPoints += points;
		else
			this.votingPoints = points;
	}

	public int getBossPoints() {
		return bosspoints;
	}

	public void setBossPoints(int bosspoints) {
		this.bosspoints = bosspoints;
	}

	public void incrementBossPoints() {
		this.bosspoints++;
	}

	public void incrementBossPoints(int amt) {
		this.bosspoints += amt;
	}

	public void setBossPoints(int points, boolean add) {
		if (add)
			this.bosspoints += points;
		else
			this.bosspoints = points;
	}

	public int getSPAWNKILLCount() {
		return spawnkillcount;
	}

	public void setSPAWNKILLCount(int spawnkillcount) {
		this.spawnkillcount = spawnkillcount;
	}

	public void incrementSPAWNKILLCount() {
		this.spawnkillcount++;
	}

	public void incrementSPAWNKILLCount(int amt) {
		this.spawnkillcount += amt;
	}

	public void setSPAWNKILLCount(int points, boolean add) {
		if (add)
			this.spawnkillcount += points;
		else
			this.spawnkillcount = points;
	}

	public int getLORDKILLCount() {
		return lordkillcount;
	}

	public void setLORDKILLCount(int lordkillcount) {
		this.lordkillcount = lordkillcount;
	}

	public void incrementLORDKILLCount() {
		this.lordkillcount++;
	}

	public void incrementLORDKILLCount(int amt) {
		this.lordkillcount += amt;
	}

	public void setLORDKILLCount(int points, boolean add) {
		if (add)
			this.lordkillcount += points;
		else
			this.lordkillcount = points;
	}

	public int getDEMONKILLCount() {
		return demonkillcount;
	}

	public void setDEMONKILLCount(int demonkillcount) {
		this.demonkillcount = demonkillcount;
	}

	public void incrementDEMONKILLCount() {
		this.demonkillcount++;
	}

	public void incrementDEMONKILLCount(int amt) {
		this.demonkillcount += amt;
	}

	public void setDEMONKILLCount(int points, boolean add) {
		if (add)
			this.demonkillcount += points;
		else
			this.demonkillcount = points;
	}

	public int getDRAGONKILLCount() {
		return dragonkillcount;
	}

	public void setDRAGONKILLCount(int dragonkillcount) {
		this.dragonkillcount = dragonkillcount;
	}

	public void incrementDRAGONKILLCount() {
		this.dragonkillcount++;
	}

	public void incrementDRAGONKILLCount(int amt) {
		this.dragonkillcount += amt;
	}

	public void setDRAGONKILLCount(int points, boolean add) {
		if (add)
			this.dragonkillcount += points;
		else
			this.dragonkillcount = points;
	}

	public int getSHILLINGRate() {
		return shillingrate;
	}
	
	private int globalRate;
	
	public int getGlobalRate() {
		return globalRate;
	}
	
	public void setGlobalRate(int rate) {
		this.globalRate = rate > 100 ? 100 : rate;
	}
	
	public void incrementGlobalRate(int rate) {
		this.globalRate += (this.globalRate + rate) > 100 ? 0 : rate;
	}
	
	public void decrementGlobalRate(int rate) {
		this.globalRate -= rate;
	}
private int slayerRate;
	
	public int getSlayerRate() {
		return slayerRate;
	}
	
	public void setSlayerRate(int rate) {
		this.slayerRate = rate > 100 ? 100 : rate;
	}
	
	public void incrementSlayerRate(int rate) {
		this.slayerRate += (this.slayerRate + rate) > 100 ? 0 : rate;
	}
	
	public void decrementSlayerRate(int rate) {
		this.slayerRate -= rate;
	}
private int pengRate;
	
	public int getPengRate() {
		return pengRate;
	}
	
	public void setPengRate(int rate) {
		this.pengRate = rate > 500000 ? 500000 : rate;
	}
	
	public void incrementPengRate(int rate) {
		this.pengRate += (this.pengRate + rate) > 500000 ? 0 : rate;
	}
	
	public void decrementPengRate(int rate) {
		this.pengRate -= rate;
	}
	public void setSHILLINGRate(int shillingrate) {
		this.shillingrate = shillingrate;
	}

	public void incrementSHILLINGRate() {
		this.shillingrate++;
	}

	public void incrementSHILLINGRate(int amt) {
		this.shillingrate += amt;
	}

	public void setSHILLINGRate(int points, boolean add) {
		if (add)
			this.shillingrate += points;
		else
			this.shillingrate = points;
	}

	public int getBEASTKILLCount() {
		return beastkillcount;
	}

	public void setBEASTKILLCount(int beastkillcount) {
		this.beastkillcount = beastkillcount;
	}

	public void incrementBEASTKILLCount() {
		this.beastkillcount++;
	}

	public void incrementBEASTKILLCount(int amt) {
		this.beastkillcount += amt;
	}

	public void setBEASTKILLCount(int points, boolean add) {
		if (add)
			this.beastkillcount += points;
		else
			this.beastkillcount = points;
	}

//
	public int getKINGKILLCount() {
		return kingkillcount;
	}

	public void setKINGKILLCount(int kingkillcount) {
		this.kingkillcount = kingkillcount;
	}

	public void incrementKINGKILLCount() {
		this.kingkillcount++;
	}

	public void incrementKINGKILLCount(int amt) {
		this.kingkillcount += amt;
	}

	public void setKINGKILLCount(int points, boolean add) {
		if (add)
			this.kingkillcount += points;
		else
			this.kingkillcount = points;
	}

	//
	public int getAVATARKILLCount() {
		return avatarkillcount;
	}

	public void setAVATARKILLCount(int avatarkillcount) {
		this.avatarkillcount = avatarkillcount;
	}

	public void incrementAVATARKILLCount() {
		this.avatarkillcount++;
	}

	public void incrementAVATARKILLCount(int amt) {
		this.avatarkillcount += amt;
	}

	public void setAVATARKILLCount(int points, boolean add) {
		if (add)
			this.avatarkillcount += points;
		else
			this.avatarkillcount = points;
	}

	//
	public int getANGELKILLCount() {
		return angelkillcount;
	}

	public void setANGELKILLCount(int angelkillcount) {
		this.angelkillcount = angelkillcount;
	}

	public void incrementANGELKILLCount() {
		this.angelkillcount++;
	}

	public void incrementANGELKILLCount(int amt) {
		this.angelkillcount += amt;
	}

	public void setANGELKILLCount(int points, boolean add) {
		if (add)
			this.angelkillcount += points;
		else
			this.angelkillcount = points;
	}

	//
	public int getLUCIENKILLCount() {
		return lucienkillcount;
	}

	public void setLUCIENKILLCount(int lucienkillcount) {
		this.lucienkillcount = lucienkillcount;
	}

	public void incrementLUCIENKILLCount() {
		this.lucienkillcount++;
	}

	public void incrementLUCIENKILLCount(int amt) {
		this.lucienkillcount += amt;
	}

	public void setLUCIENKILLCount(int points, boolean add) {
		if (add)
			this.lucienkillcount += points;
		else
			this.lucienkillcount = points;
	}

	//
	public int getHERCULESKILLCount() {
		return herculeskillcount;
	}

	public void setHERCULESKILLCount(int herculeskillcount) {
		this.herculeskillcount = herculeskillcount;
	}

	public void incrementHERCULESKILLCount() {
		this.herculeskillcount++;
	}

	public void incrementHERCULESKILLCount(int amt) {
		this.herculeskillcount += amt;
	}

	public void setHERCULESKILLCount(int points, boolean add) {
		if (add)
			this.herculeskillcount += points;
		else
			this.herculeskillcount = points;
	}

	//
	public int getSATANKILLCount() {
		return satankillcount;
	}

	public void setSATANKILLCount(int satankillcount) {
		this.satankillcount = satankillcount;
	}

	public void incrementSATANKILLCount() {
		this.satankillcount++;
	}

	public void incrementSATANKILLCount(int amt) {
		this.satankillcount += amt;
	}

	public void setSATANKILLCount(int points, boolean add) {
		if (add)
			this.satankillcount += points;
		else
			this.satankillcount = points;
	}

	//
	public int getZEUSKILLCount() {
		return zeuskillcount;
	}

	public void setZEUSKILLCount(int zeuskillcount) {
		this.zeuskillcount = zeuskillcount;
	}

	public void incrementZEUSKILLCount() {
		this.zeuskillcount++;
	}

	public void incrementZEUSKILLCount(int amt) {
		this.zeuskillcount += amt;
	}

	public void incrementMiniLuciferKillCount(int amt) {
		this.miniLuciferkillcount += amt;
	}

	public void incrementLuciferKillCount(int amt) {
		this.luciferkillcount += amt;
	}

	public void setZEUSKILLCount(int points, boolean add) {
		if (add)
			this.zeuskillcount += points;
		else
			this.zeuskillcount = points;
	}
	public int getSlayerSpree() {
		return totalsprees;
	}

	public void setSlayerSpree(int totalsprees) {
		this.totalsprees = totalsprees;
	}

	public void incrementSlayerspree() {
		this.totalsprees++;
	}

	public void incrementSlayerSpree(int amt) {
		this.totalsprees += amt;
	}

	public void setSlayerSpree(int points, boolean add) {
		if (add)
			this.totalsprees += points;
		else
			this.totalsprees = points;
	}
	//
	public int getNPCKILLCount() {
		return npckillcount;
	}

	public void setNPCKILLCount(int npckillcount) {
		this.npckillcount = npckillcount;
	}

	public void incrementNPCKILLCount() {
		this.npckillcount++;
	}

	public void incrementNPCKILLCount(int amt) {
		this.npckillcount += amt;
	}

	public void setNPCKILLCount(int points, boolean add) {
		if (add)
			this.npckillcount += points;
		else
			this.npckillcount = points;
	}

	public int getTotalPrestiges() {
		return totalprestiges;
	}

	public void setTotalPrestiges(int totalprestiges) {
		this.totalprestiges = totalprestiges;
	}

	public void incrementTotalprestiges() {
		this.totalprestiges++;
	}

	public void incrementTotalPrestiges(int amt) {
		this.totalprestiges += amt;
	}

	public void setTotalPrestiges(int points, boolean add) {
		if (add)
			this.totalprestiges += points;
		else
			this.totalprestiges = points;
	}
	
	

	public int getAchievementPoints() {
		return achievementPoints;
	}

	public void setAchievementPoints(int points, boolean add) {
		if (add)
			this.achievementPoints += points;
		else
			this.achievementPoints = points;
	}

	public int getSkillingPoints() {
		return skillingPoints;
	}

	public void setSkillingPoints(int points, boolean add) {
		if (add)
			this.skillingPoints += points;
		else
			this.skillingPoints = points;
	}

	public int getMiniLuciferkillcount() {
		return miniLuciferkillcount;
	}

	public void setMiniLuciferkillcount(int miniLuciferkillcount) {
		this.miniLuciferkillcount = miniLuciferkillcount;
	}

	public int getLuciferkillcount() {
		return luciferkillcount;
	}

	public void setLuciferkillcount(int luciferkillcount) {
		this.luciferkillcount = luciferkillcount;
	}
}
