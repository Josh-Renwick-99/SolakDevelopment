package com.ruse.model;

import com.ruse.engine.task.impl.BonusExperienceTask;
import com.ruse.util.Misc;
import com.ruse.world.entity.impl.player.Player;

/**
 * Created by brandon on 4/8/2017. Audited by the NSA
 */
public class VoteRewardHandler {

	public static final int minutes = 5;

	public static void voteRewards(Player player, boolean claimAll) {

		if (!player.getInventory().contains(23020) || player.getInventory().getAmount(23020) < 1) {
			return;
		}

		int amt = player.getInventory().getAmount(23020);

		player.getInventory().delete(23020, (claimAll ? amt : 1));
		player.getPacketSender()
				.sendMessage("You are rewarded " + (claimAll ? Misc.format(amt) : 1) + " vote "
						+ (claimAll && amt > 1 ? "points" : "point") + " " + "and "
						+ (claimAll ? Misc.format(amt * minutes) : minutes) + " minutes of 30% bonus XP!");
		BonusExperienceTask.addBonusXp(player, (claimAll ? amt * minutes : minutes)); // minutes);
		player.getPointsHandler().incrementVotingPoints(claimAll ? amt : 1);
		player.getClickDelay().reset();
	}

	public static void AFKFISH(Player player, boolean claimAll) {

		if (!player.getInventory().contains(10138) || player.getInventory().getAmount(10138) < 1) {
			return;
		}

		int amt = player.getInventory().getAmount(10138);

		player.getInventory().delete(10138, (claimAll ? amt : 1));
		player.getPacketSender().sendMessage("You are rewarded " + (claimAll ? Misc.format(amt) : 1) + " AFK "
				+ (claimAll && amt > 1 ? "tickets" : "ticket") + ".");
		player.getInventory().add(5020, (claimAll ? amt : 1));
		// BonusExperienceTask.addBonusXp(player, (claimAll ? amt*minutes : minutes));
		// //minutes);
		// player.getPointsHandler().incrementVotingPoints(claimAll ? amt : 1);

		player.getClickDelay().reset();
	}

	public static void AFKMINE(Player player, boolean claimAll) {

		if (!player.getInventory().contains(17634) || player.getInventory().getAmount(17634) < 1) {
			return;
		}

		int amt = player.getInventory().getAmount(17634);

		player.getInventory().delete(17634, (claimAll ? amt : 1));
		player.getPacketSender().sendMessage("You are rewarded " + (claimAll ? Misc.format(amt) : 1) + " AFK "
				+ (claimAll && amt > 1 ? "tickets" : "ticket") + ".");
		player.getInventory().add(5020, (claimAll ? amt : 1));
		// BonusExperienceTask.addBonusXp(player, (claimAll ? amt*minutes : minutes));
		// //minutes);
		// player.getPointsHandler().incrementVotingPoints(claimAll ? amt : 1);

		player.getClickDelay().reset();
	}

}