package com.ruse.world.content.loyalty_streak;

import com.ruse.model.definitions.ItemDefinition;

/**
 * Created by Grant_ | Rune-server.ee | on 6/17/2018.
 */
public enum LoyaltyStreakRewards {
	DAY_ONE(6199, 1), DAY_TWO(20078, 1), DAY_THREE(989, 250), DAY_FOUR(ItemDefinition.COIN_ID, 1), DAY_FIVE(6199, 25), DAY_SIX(ItemDefinition.COIN_ID, 500),
	DAY_SEVEN(6200, 1);

	private int itemID;

	private int amount;

	LoyaltyStreakRewards(int itemID, int amount) {
		this.itemID = itemID;
		this.amount = amount;
	}

	public int getItemID() {
		return itemID;
	}

	public int getAmount() {
		return amount;
	}

}
