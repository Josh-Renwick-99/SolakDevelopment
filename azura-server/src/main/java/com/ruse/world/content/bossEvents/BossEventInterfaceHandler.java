package com.ruse.world.content.bossEvents;

import com.ruse.world.entity.impl.player.Player;

/**
 * 
 * @author Adam_#6723
 *
 */

public class BossEventInterfaceHandler {

	protected Player player;

	public BossEventInterfaceHandler(Player player) {
		this.player = player;
	}

	private final static BossEventData information[] = BossEventData.values();

	private final int ITEM_GROUP_ID = 37418;

	private final int INTERFACE_ID = 37400;

	public void open() {
		if (BossEventHandler.isEventRunning) {
			player.sendMessage("@red@There is currently an active events!");
		} else {
			player.sendMessage("There is no event running at this time");
		}
		sendStrings();
		player.getPacketSender().sendInterface(INTERFACE_ID);
	}

	public void sendStrings() {
		if (player.getBossevent() != null) {
			player.getPA().sendFrame126("Task Name: " + player.getBossevent().getName(), 37409);
			player.getPA().sendFrame126("Task Combat level: " + player.getBossevent().getCombatLevel(), 37410);
			player.getPA().sendFrame126("Task Health: " + player.getBossevent().getHealth(), 37411);
			player.getPA().sendFrame126("Task Maxhit: " + player.getBossevent().getMaxhit(), 37412);
			player.getPA().sendFrame126("Task Weakness: " + player.getBossevent().getWeakness(), 37413);
			player.getPA().sendFrame126("Task Amount: " + player.getBossevent().getEndamount(), 37415);
			player.getPA().sendNpcIdToDisplayPacket(player.getBossevent().getNpcid(), 37416);
			player.getPacketSender().sendItemOnInterface(ITEM_GROUP_ID, player.getBossevent().getRewards(), 0, 1);
		}
	}

	public boolean button(int buttonid) {
		if (buttonid == -28115) {
			new BossEventHandler().teleport(player);
			return true;
		}
		return false;
	}

}
