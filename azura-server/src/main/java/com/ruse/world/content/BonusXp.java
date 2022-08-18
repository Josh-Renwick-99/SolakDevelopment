package com.ruse.world.content;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.world.entity.impl.player.Player;

public class BonusXp {

	// ok look @ how i code this so u can just do next one ur self, ok ?ok
	
	private Player player; // player instance of this class.
	
	public BonusXp(Player player) {
		this.player = player; // constructor of this class
	}
	
	public void init() {
		player.getPacketSender().sendWalkableInterface(48400, true); // send the interface
		
		run(); // start the task where it sends update of the timer every minute
	}
	
	private void run() { // all code in here runs every minute
		
		TaskManager.submit(new Task(100, true) { // 100 = 100ticks = 60 secs, true = start instantly.
			@Override
			protected void execute() {
				player.getPacketSender().sendString(48402, "" + player.getMinutesBonusExp() + " minutes");
				
				if(player.getMinutesBonusExp() < 1) { // check if there is still any bonus xp for the player
					player.getPacketSender().sendWalkableInterface(48400, false); // if not, then no longer display the interface
					stop(); // stop the task from running every 60secs.
				}
			}
		});
	}
	
}
