package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.content.groupironman.GroupManager;
import com.ruse.world.entity.impl.player.Player;

public class CreateIronmanGroup extends Input {

	@Override
	public void handleSyntax(Player player, final String syntax) {
		GroupManager.createGroup(player, syntax);
	}

}