package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.polling.PollCreation;
import com.ruse.world.entity.impl.player.Player;

public class PollCreationTimeForPoll extends EnterAmount {

	@Override
	public void handleSyntax(Player player, String amount) {
		try {
			player.getPacketSender().sendClientRightClickRemoval();
			String input = (String) amount;
			int newInput;
			newInput = Integer.parseInt(input);
			PollCreation.updateExpireTime(player, newInput);
		} catch (NumberFormatException e) {
			player.sendMessage("enter a number brainlet");
		}
	}
}
