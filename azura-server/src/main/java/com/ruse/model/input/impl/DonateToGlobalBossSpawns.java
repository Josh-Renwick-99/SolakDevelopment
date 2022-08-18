package com.ruse.model.input.impl;

import com.ruse.model.input.EnterAmount;
import com.ruse.world.content.wellForGlobalBosses.WellForGlobalBosses;
import com.ruse.world.entity.impl.player.Player;

public class DonateToGlobalBossSpawns extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		new WellForGlobalBosses().donate(player, amount);
	}
}
