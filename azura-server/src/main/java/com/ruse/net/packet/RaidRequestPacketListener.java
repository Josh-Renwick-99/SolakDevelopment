package com.ruse.net.packet;

import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.ruse.model.Locations;
import com.ruse.model.Locations.Location;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

public class RaidRequestPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {

		if (player.getConstitution() <= 0)
			return;
		if (player.isTeleporting())
			return;
		player.getSkillManager().stopSkilling();
		if (player.getLocation() == Location.FIGHT_PITS) {
			player.getPacketSender().sendMessage("You cannot trade other players here.");
			return;
		}
		int index = packet.getOpcode() == RAID_OPCODE ? (packet.readShort() & 0xFF) : packet.readLEShort();
		// System.out.println("Index from client: " + index);
		if (index < 0 || index > World.getPlayers().capacity())
			return;
		Player target = World.getPlayers().get(index);

		// // System.out.println("Index: " + index);
		// // System.out.println("Name: " + target.getUsername());
		if (target == null || !Locations.goodDistance(player.getPosition(), target.getPosition(), 13))
			return;
		player.setWalkToTask(
				new WalkToTask(player, target.getPosition(), target.getSize(), new FinalizedMovementTask() {
					@Override
					public void execute() { // TODO: inviting from player menu
						if (target.getIndex() != player.getIndex()) {
							if (packet.getOpcode() == 220) {
								// System.out.println(
								//	player.getMinigameAttributes().getDungeoneeringAttributes().getPartyInvitation()
								//			+ " | " + target.getMinigameAttributes().getDungeoneeringAttributes()
								//					.getPartyInvitation());
								if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() == null) {
									// System.out.println("YE1");
									if (player.getMinigameAttributes().getDungeoneeringAttributes()
											.getPartyInvitation() != null) {
										// System.out.println("YE2");
										player.getMinigameAttributes().getDungeoneeringAttributes().getPartyInvitation()
												.add(player);

									}
								}
								player.getMinigameAttributes().getDungeoneeringAttributes().setPartyInvitation(null);
							} else {
								if (player.getMinigameAttributes().getDungeoneeringAttributes().getParty() != null) {
									player.getMinigameAttributes().getDungeoneeringAttributes().getParty()
											.invite(target);
								} else {
									player.sendMessage(
											"You need to create a raid group before you can start inviting anyone");
								}
							}
						}
					}
				}));
	}

	public static final int RAID_OPCODE = 216;
	public static final int CHATBOX_RAID_OPCODE = 220;

}
