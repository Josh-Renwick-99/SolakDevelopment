package com.ruse.world.content.instanceMananger;

import com.ruse.world.entity.impl.player.Player;

public class InstanceInterfaceHandler {

	private Player player;

	private static final InstanceData data[] = InstanceData.values();

	public InstanceInterfaceHandler(Player player) {
		this.player = player;
	}

	public void open() {
		sendBossNames();
		player.getPA().sendInterface(35000);
	}

	public void sendBossNames() {
		int startID = 35071;
		for (InstanceData data : data) {
			player.getPA().sendString(startID++, data.getName());
		}
	}

	public void sendItems() {
		resetItems();
		for (InstanceData data : data) {
			for (int i = 0; i < data.getRewards().length; i++) {
			}
		}
	}

	public void handleButtons(int id) {
		for (InstanceData data : data) {
			
			if ( id == data.getButtonid()) {
				player.setData(data);
				sendItems();
				player.getPA().sendNpcIdToDisplayPacket(data.getNpcid(), 35004);
			}
		}
	}

	public void resetItems() {
		int interfaceId = 35007;
		for (int index = 0; index < 10; index++) {
			player.getPA().sendItemOnInterface(interfaceId, -1, -1);
		}
	}
}
