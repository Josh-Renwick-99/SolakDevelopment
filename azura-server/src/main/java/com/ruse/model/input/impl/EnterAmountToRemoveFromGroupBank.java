package com.ruse.model.input.impl;

import com.ruse.model.Item;
import com.ruse.model.input.EnterAmount;
import com.ruse.world.entity.impl.player.Player;

public class EnterAmountToRemoveFromGroupBank extends EnterAmount {


	public EnterAmountToRemoveFromGroupBank(int item, int slot) {
		super(item, slot);
	}

	@Override
	public void handleAmount(Player player, int amount) {
		if(!player.isBanking())
			return;
		int item = player.getGroupIronmanBank().getItems()[getSlot()].getId();
		if(item != getItem())
			return;
		if(!player.getGroupIronmanBank().contains(item))
			return;
		int invAmount = player.getGroupIronmanBank().getAmount(item);
		if(amount > invAmount) 
			amount = invAmount;
		if(amount <= 0)
			return;
		player.getGroupIronmanBank().switchItem(player, player.getInventory(), new Item(item, amount), player.getGroupIronmanBank().getSlot(item), true, true);
	}
}