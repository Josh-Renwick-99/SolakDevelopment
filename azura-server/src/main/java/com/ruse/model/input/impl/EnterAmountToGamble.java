package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.entity.impl.player.Player;

public class EnterAmountToGamble extends EnterAmount {

	public EnterAmountToGamble(int item, int slot) {
		super(item, slot);
	}

	@Override
	public void handleAmount(Player player, int amount) {
		if (player.getGambling().inGamble()&&getItem() > 0 && getSlot() >= 0 && getSlot() < 28)
			player.getGambling().gambleItem(getItem(), amount, getSlot());
		else
			player.getPacketSender().sendInterfaceRemoval();
	}

}
