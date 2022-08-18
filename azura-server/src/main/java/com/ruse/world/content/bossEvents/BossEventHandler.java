package com.ruse.world.content.bossEvents;

import java.util.ArrayList;

import com.ruse.model.Item;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

/**
 * 
 * @author Adam_#6723
 *
 */

public class BossEventHandler {

	public static boolean isEventRunning = false;

	public static ArrayList<String> array = new ArrayList<String>();

	public void AssignTasks(int taskNumber) {
		if (isEventRunning == true) {
			return;
		}
		array.clear();
		BossEventData data = BossEventData.byId.get(taskNumber);
		for (Player players : World.getPlayers()) {
			if (players != null) {
				if (taskNumber == data.getTaskNumber()) {
					players.setBossevent(data);
					players.setCurrentBossTask(data.getNpcid());
					players.setCurrentBossTaskAmount(data.getEndamount());
					players.sendMessage("You have been Assigned to kill: " + players.getBossevent().getName()
							+ " Amount: " + players.getBossevent().getEndamount());
					players.sendMessage("First one to complete this task will be rewarded handsomely!");
					players.setHasPlayerCompletedBossTask(false);
					isEventRunning = true;
				} else {
				}
			}
		}
		World.sendMessage("@red@ Do ::bossevents to teleport to the task!");
	}

	public void Execute() {
		AssignTasks(Misc.exclusiveRandom(0, 9));
	}

	public void forceReset(Player player) {
		for (Player players : World.getPlayers()) {
			if (players != null) {
				resetTasks(players);
			}
		}
		World.sendMessage("<img=13><col=0000ff> " + " The Boss Event has been force resetted.");
		array.clear();
		isEventRunning = false;
	}

	public void teleport(Player player) {
		if (player.getBossevent() != null) {
			TeleportHandler.teleportPlayer(
					player, new Position(player.getBossevent().getPosition().getX(),
							player.getBossevent().getPosition().getY(), player.getBossevent().getPosition().getZ()),
					player.getSpellbook().getTeleportType());
		} else {
			player.getPA().sendMessage("You do not have a task currently!");
		}
	}

	public void death(Player player, NPC npc, String NpcName) {
		if (isEventRunning == false) {
			//System.err.println("Event isn't running");
			return;
		}
		if (player.bossevent == null || player.currentBossTask == -1 || player.currentBossTaskAmount == -1) {
			//System.err.println("No Task Applied");
			return;
		}
		if (player.isHasPlayerCompletedBossTask() == true) {
			//System.err.println("already completed boss task");
			return;
		}
		if (npc.getId() != player.getCurrentBossTask()) {
		//	System.err.println("Not killing correct npc");
			return;
		}
		player.setCurrentBossTaskAmount(player.getCurrentBossTaskAmount() - 1);
		player.getPA().sendMessage("You currently need to kill " + player.getCurrentBossTaskAmount() + " " + NpcName);

		if (player.getCurrentBossTaskAmount() <= 0 && array.size() <= 3
				&& player.isHasPlayerCompletedBossTask() == false) {
			player.setHasPlayerCompletedBossTask(true);
			player.getPA().sendMessage(
					"You have completed the boss task, check your inventory or bank or the floor for your reward.");
			array.add(player.getUsername());
			finish(player);
			return;
		}
	}

	public void resetTasks(Player player) {
		if (player.bossevent != null || player.currentBossTask != -1 || player.currentBossTaskAmount != -1) {
			player.setCurrentBossTask(-1);
			player.setCurrentBossTaskAmount(-1);
			player.setBossevent(null);
			player.setHasPlayerCompletedBossTask(false);
			isEventRunning = false;

		} else {
		}
	}

	public void finish(Player player) {
		if (array.size() == 1) {
			World.sendMessage("<img=13><col=0000ff> " + player.getUsername()
					+ " Has just been awared 1st place for completing the Daily Boss Event Task");
		}
		if (array.size() == 2) {
			World.sendMessage("<img=13><col=0000ff> " + player.getUsername()
					+ " Has just been awared 2nd place for completing the Daily Boss Event Task");
		}
		if (array.size() == 3) {
			World.sendMessage("<img=13><col=0000ff> " + player.getUsername()
					+ " Has just been awared 3rd place for completing the Daily Boss Event Task");
		}
		player.getInventory().add(new Item(player.getBossevent().getRewards(), 1));
		player.getPA().sendMessage("You have Recieved a reward for completing the daily tasks!");
		if (array.size() == 3) {
			for (Player players : World.getPlayers()) {
				if (players != null) {
					resetTasks(players);
				}
			}
			World.sendMessage(
					"<img=13><col=0000ff> " + " The Boss Event has just ended, congrats to the first three winners!!");
			array.clear();
		}
	}
}
