package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;

public class SetPinPacketListener extends Input {
	@Override
	public void handleSyntax(Player player, String pin) {

		if (pin.length() < 6 && pin.length() > 0) {
			player.setSavedPin(pin);
			player.setSavedIp(player.getHostAddress());
			player.setHasPin(true);
			if (player.isPlayerLocked() && !player.newPlayer()){
				player.setPlayerLocked(false);
			}
			player.sendMessage("<shad=1>@blu@Your pin is: @red@" + pin);

		}
	}
}
