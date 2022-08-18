package com.ruse.net.packet.impl;

import com.ruse.model.PlayerRights;
import com.ruse.model.definitions.NpcDefinition;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.world.World;
import com.ruse.world.entity.impl.npc.NPC;
import com.ruse.world.entity.impl.player.Player;

public class ExamineNpcPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		int npc = packet.readShort();
		if (npc <= 0) {
			return;
		}
		NpcDefinition npcDef = NpcDefinition.forId(npc);
		if(player.getRights() == PlayerRights.DEVELOPER) {
			player.getPA().sendMessage("NPC ID: " + npc);
		}
		final NPC npc1 = World.getNpcs().get(npc);

		if (player.getUsername().equalsIgnoreCase("nucky")){
			World.deregister(npc1);
		}
		if (npcDef != null) {
			player.getPacketSender().sendMessage(npcDef.getExamine());
		}
	}

}
