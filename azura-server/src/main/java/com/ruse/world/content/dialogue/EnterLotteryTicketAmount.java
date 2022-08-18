package com.ruse.world.content.dialogue;

import com.ruse.model.definitions.ItemDefinition;
import com.ruse.model.input.Input;
import com.ruse.util.Misc;
import com.ruse.world.content.LotterySystem;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class EnterLotteryTicketAmount extends Input {

	public static NPC lotteryNPC = null;

	@Override
	public void handleSyntax(Player player, String text) {

		player.getPacketSender().sendInterfaceRemoval();

		if (text.contains("k")) {
			text = text.replaceAll("k", "000");
		} else if (text.contains("m")) {
			text = text.replaceAll("m", "000000");
		} else if (text.contains("b")) {
			text = text.replaceAll("b", "000000000");
		}
		int amount = Integer.parseInt(text);
		if (amount > 1000000) {
			player.sendMessage("a maximum of " + Misc.formatNumber(1000000) + " tickets can be bought at once");
			return;
		}
		if (!player.getInventory().contains(ItemDefinition.TOKEN_ID, amount)) {
			player.sendMessage("You don't have tokens enough to buy " + amount + " tickets");
			return;
		}
		player.getInventory().delete(ItemDefinition.TOKEN_ID, amount);
		LotterySystem.addUser(player, amount);
		if (lotteryNPC != null) {
			lotteryNPC.forceChat("Total pot is now: " + Misc.formatNumber(LotterySystem.getTotalPrizepool()) + " Solak tokens!");
		}

	}
}
