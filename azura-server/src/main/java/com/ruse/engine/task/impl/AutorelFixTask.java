package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.world.entity.impl.player.Player;

public class AutorelFixTask extends Task {

	
	int tick = 14;
	
	final Player player;
	public AutorelFixTask(Player player) {
		super(1, player, true);
		this.player = player;
	}
	
	
	@Override
	protected void execute() {
		tick -=1;
		
		if(tick == 13 && !player.isAutoRetaliate()) {
			stop();
		}
		
		if(tick == 13 && player.isAutoRetaliate()) {
			player.setAutoRetaliate(false);
			player.sendMessage("@red@Your autoretaliate has been turned off.");
			player.sendMessage("@red@It will automatically will be turned back on in 5 secounds.");
		}
		if(tick == 5) {
			player.setAutoRetaliate(true);
			player.sendMessage("Your autoretaliate is now turned back on");
			stop();
		}
		
		player.getPacketSender().sendConfig(172, player.isAutoRetaliate() ? 1 : 0);
	}

}
