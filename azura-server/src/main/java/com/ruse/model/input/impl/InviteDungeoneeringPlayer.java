package com.ruse.model.input.impl;

import com.ruse.model.Locations;
import com.ruse.model.input.Input;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

public class InviteDungeoneeringPlayer extends Input {

	@Override
	public void handleSyntax(Player player, String plrToInvite) {
		if (player.getLocation() == Locations.Location.DUNGEONEERING) {

			player.getPacketSender().sendInterfaceRemoval();
			Player invite = World.getPlayerByName(plrToInvite);
			if (invite == null) {
				player.getPacketSender().sendMessage("That player is currently not online.");
				return;
			}

			player.sendMessage("Sent invite to " + plrToInvite);

			player.getMinigameAttributes().getDungeoneeringAttributes().getParty().invite(invite);

		}
	}
}