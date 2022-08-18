package com.ruse.world.content;

import com.ruse.engine.task.Task;
import com.ruse.engine.task.TaskManager;
import com.ruse.world.entity.impl.player.Player;

public class DonatorDiscount {

	// ok look @ how i code this so u can just do next one ur self, ok ?ok
	
	private Player player; // player instance of this class.
	
	public DonatorDiscount(Player player) {
		this.player = player; // constructor of this class
	}
	
	public void init() {
		player.getPacketSender().sendWalkableInterface(48500, true); // send the interface
		
		//run(); // start the task where it sends update of the timer every minute
	}
	public void end() {
		player.getPacketSender().sendWalkableInterface(48500, false); // send the interface
		
	//	run(); // start the task where it sends update of the timer every minute
	}
	
	
}
