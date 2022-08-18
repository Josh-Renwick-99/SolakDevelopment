package com.ruse.world.content.rewardsList;

import com.ruse.world.entity.impl.player.Player;

/**
 * 
 * @author Adam_#6723
 *
 */

public class RewardsHandler {

	protected Player player;

	private static final RewardsData information[] = RewardsData.values();

	public RewardsHandler(Player player) {
		this.player = player;
	}

	public void open() {
		SendRewardStrings();
		player.getPacketSender().sendInterface(17550);

	}

	public void open(int id) {
		RewardsData data = RewardsData.byId.get(id);
		id = data.getIndex();
		SendRewardStrings();
		ItemGroup(id);
		player.getPacketSender().sendInterface(17550);
	}

	public void ItemGroup(int id) {
		RewardsData data = RewardsData.byId.get(id);
		if (data != null) {
			for (int index = 0; index < data.getItemID().length; index++) {
				player.getPacketSender().sendItemOnInterface(17752, data.getItemID()[index], index, 1);
			}
		}
		player.getPacketSender().sendString(17556, "Rewards List From: " + data.getText());
	}

	public void SendRewardStrings() {
		int startID = 17651;
		for (RewardsData data : information) {
			player.getPacketSender().sendString(startID++, data.getText());
		}
	}

	public void ResetFrame34() {
		int interfaceId = 17752;
		for (int index = 0; index < 100; index++) {
			player.getPacketSender().sendItemOnInterface(interfaceId, -1, index, -1);
		}
	}

	public boolean button(int buttonId) {
		switch (buttonId) {
		case 17651:
			ResetFrame34();
			ItemGroup(0);
			return true;
		case 17652:
			ResetFrame34();
			ItemGroup(1);
			return true;
		case 17653:
			ResetFrame34();
			ItemGroup(2);
			break;
		case 17654:
			ResetFrame34();
			ItemGroup(3);
			break;
		case 17655:
			ResetFrame34();
			ItemGroup(4);
			break;
		case 17656:
			ResetFrame34();
			ItemGroup(5);
			break;
		case 17657:
			ResetFrame34();
			ItemGroup(6);
			break;
		case 17658:
			ResetFrame34();
			ItemGroup(7);
			break;
		case 17659:
			ResetFrame34();
			ItemGroup(8);
			break;
		case 17660:
			ResetFrame34();
			ItemGroup(9);
			break;
		case 17661:
			ResetFrame34();
			ItemGroup(10);
			break;
		}
		return false;
	}
}
