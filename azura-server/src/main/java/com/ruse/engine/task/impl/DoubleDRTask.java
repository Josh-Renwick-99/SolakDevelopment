package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.model.*;
import com.ruse.model.Locations.Location;
import com.ruse.world.content.Consumables;
import com.ruse.world.content.EffectTimer;
import com.ruse.world.entity.impl.player.Player;

public class DoubleDRTask extends Task {

	public DoubleDRTask(Player player) {
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
		int timer = player.getDoubleDRTimer();
		if (timer == 6000) {
			player.getPacketSender().sendEffectTimerSeconds(3600, EffectTimer.X2_DR_1HR);
		}
		if (timer == 3000) {
			player.getPacketSender().sendEffectTimerSeconds(1800, EffectTimer.X2_DR_30MIN);
		}
		player.setDoubleDRTimer(timer - 1);

		if (player.getDoubleDRTimer() <= 0) {
			player.getPacketSender().sendMessage("@red@Your x2 drop rate boost has ended.");
			player.setDoubleDRTimer(0);
			stop();
		}
	}
}
