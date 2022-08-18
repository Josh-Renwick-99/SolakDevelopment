package com.ruse.engine.task.impl;

import com.ruse.engine.task.Task;
import com.ruse.world.World;
import com.ruse.world.content.LotterySystem;

public class LotteryTask extends Task {

	public LotteryTask() {//WHAT IS NUMBER FOR 5 HOURS?
		super(2000); // 20mins testing enjoy i gtg.
	}

	int tick = 0;

	protected void execute() {
		if(LotterySystem.getCurrentTicketAmount() < 1) {
			return;// am falling a sleep lol, slept 2h in the last 48h 
		}
		if (tick % 16 == 0) { // 5 hours passed
			LotterySystem.pickWinner(); // pick winner
			tick = 0;
		} else {
			//otherwise send this msg
			World.sendMessage("<img=5>@blu@[LOTTERY SYSTEM]<img=5> @red@ " + (16 - tick) * 20 + " minutes left until a winner is selected!");
		}
		
		tick++;

	}
	
	private static float tickToMin(int tick) {
		int ticksInMillis = tick * 600;
		float tickToMin = ticksInMillis / 60000f;
		return tickToMin;
	}
	

}
