package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.world.content.EffectTimer;
import com.ruse.world.entity.impl.player.Player;

public class DoubleDDRTask extends Task {

	public DoubleDDRTask(Player player) {
		super(1, player, true);
		this.player = player;
	}

	final Player player;

	@Override
	public void execute() {
		if (player == null || !player.isRegistered()) {
			stop();
			return;
		}
		int timer = player.getDoubleDDRTimer();
		if (timer == 6000) {
			player.getPacketSender().sendEffectTimerSeconds(3600, EffectTimer.X2_DDR_1HR);
		}
		player.setDoubleDDRTimer(timer - 1);

		if (player.getDoubleDDRTimer() <= 0) {
			player.getPacketSender().sendMessage("@red@Your x2 DDR boost has ended.");
			player.setDoubleDDRTimer(0);
			stop();
		}
	}
}
