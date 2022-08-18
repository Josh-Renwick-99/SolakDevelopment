package com.ruse.model.input.impl;

import com.ruse.model.GroundItem;
import com.ruse.model.Item;
import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.GroundItemManager;
import com.ruse.world.entity.impl.player.Player;

public class MiniPlayerDropWarning extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		player.getPacketSender().sendInterfaceRemoval();
		if(syntax.equalsIgnoreCase("confirm")) {
			if(!player.getInventory().contains(10944) || player.busy())
				return;
			player.getInventory().delete(10944, 1);
			player.getInventory().refreshItems();
			player.getMiniPManager().disposeMiniPlayer();
			GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(10944), player.getPosition().copy(), player.getUsername(), player.getHostAddress(), false, 80, player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4 ? true : false, 80));
		}
	}
}
