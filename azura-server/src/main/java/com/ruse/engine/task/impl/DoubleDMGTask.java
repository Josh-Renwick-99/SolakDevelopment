package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.world.content.EffectTimer;
import com.ruse.world.entity.impl.player.Player;

public class DoubleDMGTask extends Task {

	public DoubleDMGTask(Player player) {
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
		int timer = player.getDoubleDMGTimer();
		if (timer == 6000) {
			player.getPacketSender().sendEffectTimerSeconds(3600, EffectTimer.X2_DMG_1HR);
		}
		if (timer == 3000) {
			player.getPacketSender().sendEffectTimerSeconds(1800, EffectTimer.X2_DMG_30MIN);
		}
		player.setDoubleDMGTimer(timer - 1);

		if (player.getDoubleDMGTimer() <= 0) {
			player.getPacketSender().sendMessage("@red@Your x2 damage boost has ended.");
			player.setDoubleDMGTimer(0);
			stop();
		}
	}
}
