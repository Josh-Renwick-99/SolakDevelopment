package com.ruse.world.content.seasonpass;


import com.ruse.world.entity.impl.player.Player;

public class SeasonPass {

	private Player player;
	private int tier;
	private int xp;
	private boolean member;

	public SeasonPass(Player player) {
		this.player = player;
	}

	public void openInterface() {

		player.getPacketSender().sendString(105007, "XP: " + getXp() + "/10");
		player.getPacketSender().sendString(105008, "" + getTier());
		player.getPacketSender().sendString(105009, "SEASON " + getTier());
		player.getPacketSender().sendString(105010, "Season Ends: " + getTier() + " days");

		System.out.println("l: " + PassRewards.memberRewards[1].getId());
		int index = 0;
		int interfaceId = 105105;
		for (int i = 0; i < PassRewards.defaultRewards.length; i++) {
			player.getPacketSender().sendItemOnInterface(interfaceId++, PassRewards.defaultRewards[index].getId(),
					PassRewards.defaultRewards[index].getAmount());
			player.getPacketSender().sendItemOnInterface(interfaceId++, PassRewards.memberRewards[index].getId(),
					PassRewards.memberRewards[index].getAmount());

			player.getPacketSender().sendConfig(interfaceId++, 1714 + index);
			player.getPacketSender().sendConfig(interfaceId++, 1814 + index);

			interfaceId += 4;
			index++;
		}

		player.getPacketSender().sendInterface(105000);

	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

}
