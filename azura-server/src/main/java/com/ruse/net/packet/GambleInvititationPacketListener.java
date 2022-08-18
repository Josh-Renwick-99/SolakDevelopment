package com.ruse.net.packet;

import com.ruse.engine.task.impl.WalkToTask;
import com.ruse.engine.task.impl.WalkToTask.FinalizedMovementTask;
import com.ruse.model.Locations;
import com.ruse.model.Locations.Location;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

public class GambleInvititationPacketListener implements PacketListener {

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
		int index = packet.getOpcode() == GAMBLE_OPCODE ? (packet.readShort() & 0xFF) : packet.readLEShort();
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
					public void execute() {
						if (target.getIndex() != player.getIndex()) {
							if ((player.getTotalPlayTime() >= 1)) {

								//player.sendMessage("@red@Gambling has been disabled ");
								return;
							} else {							

							}
						}
					}
				}));
	}

	public static final int GAMBLE_OPCODE = 191;
	public static final int CHATBOX_GAMBLE_OPCODE = 193;
}
