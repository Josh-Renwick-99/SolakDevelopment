package com.ruse.world.content.voteclaiming;

import com.ruse.world.entity.impl.player.Player;

public class HandleVoteClaiming {

	private Player player;

	public HandleVoteClaiming(Player player) {
		this.player = player;
	}

	public void onLogin() {
		player.getPacketSender().sendItemOnInterface(39672, 15501, 0, 1);
		player.getPacketSender().sendItemOnInterface(39673, 19114, 0, 1);
		player.getPacketSender().sendItemOnInterface(39674, 19116, 0, 1);
		player.getPA().sendInterface(39670);
		player.getPA().sendMessage(":helloworld");
	}

	public void handleClick1() {
		if (player.getCurrentVotes() >= 5) {
			player.currentVotes = -5;
			player.getInventory().add(6199, 1);
		}
	}

	public void handleClick2() {
		if (player.getCurrentVotes() >= 10) {
			player.currentVotes = -10;
			player.getInventory().add(6199, 1);
		}
	}

	public void handleClick3() {
		if (player.getCurrentVotes() >= 15) {
			player.currentVotes = -15;
			player.getInventory().add(6199, 1);
		}
	}
}
