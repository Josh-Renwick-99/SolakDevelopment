package com.ruse.world.content.bossEvents;

import java.util.Timer;
import java.util.TimerTask;

import com.ruse.util.Misc;
import com.ruse.world.World;

/**
 * 
 * @author Adam_#6723
 *
 */

public class BossEventThread {

	public int tick = 0;
	public int Reminder = 0;

	public void ExecuteEvent() {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (tick == 35) {
					stop();
					cancel();
					new BossEventHandler().AssignTasks(Misc.exclusiveRandom(0, 1));
					return;
				}
				tick++;
				Reminder++;
				if (Reminder > 600) {
					Remind();
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1000, 1000);
	}

	public void stop() {
		// System.out.println("stop executing event. tick now equals to 0");
		tick = 0;
	}

	public void Remind() {
		World.sendMessage("<shad=916323>[BossEvent] " + tick / 60 + " Minutes passed.");
		// System.out.println("[Boss Event] 10 Minutes passed. ");
		Reminder = 0;
	}
}
