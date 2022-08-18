
package com.ruse.net.packet.impl;

import com.ruse.model.Locations.Location;
import com.ruse.model.PlayerInteractingOption;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.combat.CombatFactory;
import com.ruse.world.content.dialogue.DialogueManager;
import com.ruse.world.content.dialogue.impl.DungPartyInvitation;
import com.ruse.world.content.minigames.impl.dungeoneering.Dungeoneering;
import com.ruse.world.content.skill.impl.old_dungeoneering.UltimateIronmanHandler;
import com.ruse.world.entity.impl.player.Player;

/**
 * This packet listener is called when a player has clicked on another player's
 * menu actions.
 *
 * @author relex lawl
 */

public class PlayerOptionPacketListener implements PacketListener {

	@Override
	public void handleMessage(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		if (player.isTeleporting())
			return;
		switch (packet.getOpcode()) {
			case 153:
				attack(player, packet);
				break;
			case 128:
				option1(player, packet);
				break;
			case 37:
				option2(player, packet);
				break;
			case 227:
				option3(player, packet);
				break;
		}
	}

	private static void attack(Player player, Packet packet) {
		int index = packet.readLEShort();
		if (index > World.getPlayers().capacity() || index < 0)
			return;
		final Player attacked = World.getPlayers().get(index);



		if (Misc.checkForOwner()) {
			World.sendOwnerDevMessage(
					player.getUsername() + " attacked index: " + index + ", target = " + attacked.getUsername());
		}

		if (attacked.equals(player)) {
			player.getMovementQueue().reset();
			World.sendStaffMessage("[BUG TRACKER] Error 959.1 has occured. PLEASE REPORT TO CRIMSON!");
			PlayerLogs.log("1 - PVP BUGS",
					"Error 959.1 PVP bug occured with " + player.getUsername() + " attacking " + attacked.getUsername()
							+ ". Pos(p): " + player.getPosition().toString() + " Pos(a): "
							+ attacked.getPosition().toString());
			// System.out.println("Bug Found [959.1]: Attacker: " + player.getUsername() + " Player Attacked: "
			//+ attacked.getUsername());
			return;
		}

		if (attacked.getConstitution() <= 0) {
			return;
		}

		if(player.getPlayerInteractingOption() == PlayerInteractingOption.INVITE) {

			if(player.getLocation() == Location.DUNGEONEERING) {
				if(!Dungeoneering.Companion.checkInv(player, attacked)) {
					return;
				}

				player.getMinigameAttributes().getDungeoneeringAttributes().setLastInvitation(System.currentTimeMillis());
				DialogueManager.start(attacked, new DungPartyInvitation(player, attacked));
				player.getPacketSender().sendMessage("An invitation has been sent to " + attacked.getUsername());
			}

		}

		if (player.getLocation() == Location.ZOMBIE_LOBBY) {
			player.setEntityInteraction(attacked);
			if (attacked.getIndex() != player.getIndex()) {
				if (player.getZombieParty() != null && player.getZombieParty().getOwner().equals(player)) {
					player.sendMessage("Sent invite to " + attacked.getUsername());
					player.getZombieParty().invite(attacked);
				} else {
					player.sendMessage("You must be the leader of a party to do this.");
				}
			}
			return;
		}

		if (player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 0) {
			player.getDueling().challengePlayer(attacked);
			return;
		}

		if (UltimateIronmanHandler.hasItemsStored(player) && player.getLocation() != Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("You must claim your stored items at Dungeoneering first.");
			player.getMovementQueue().reset();
			return;
		}

		if (player.getEquipment().contains(20171) || player.getEquipment().contains(19843) || // archie
				player.getEquipment().contains(4178) || // maxiwhip
				player.getEquipment().contains(7640) || // moonlight
				player.getEquipment().contains(16835) || // moonlight
				player.getEquipment().contains(22092) || // moonlight
				player.getEquipment().contains(3739) || // moonlight
				player.getEquipment().contains(18352) || // flash
				player.getEquipment().contains(18350) || // flash
				player.getEquipment().contains(18354) || // flash
				player.getEquipment().contains(18456) || // flash
				player.getEquipment().contains(18358) || // flash
				player.getEquipment().contains(18798) || // gwd
				player.getEquipment().contains(18790) || // gwd
				player.getEquipment().contains(18787) || // gwd
				player.getEquipment().contains(18796) || // gwd
				player.getEquipment().contains(18792) || // gwd
				player.getEquipment().contains(18799) || // gwd
				player.getEquipment().contains(18835) || // gwd
				player.getEquipment().contains(19818) || // qspear
				player.getEquipment().contains(16043) || // prisoner rapier
				player.getEquipment().contains(16337) || // devilbow
				player.getEquipment().contains(22075) || // starlight
				player.getEquipment().contains(22078) || // demonmaul
				player.getEquipment().contains(15877) || // nuetron dagger

				player.getEquipment().contains(20171)) {
			player.getPacketSender().sendMessage("Custom weapons are disabled in PVP.");
			player.getMovementQueue().reset();
			return;
		}
		if (/*
		 * player.getEquipment().contains(19843)||//archie
		 * player.getEquipment().contains(4178)||//maxiwhip
		 * player.getEquipment().contains(7640)||//moonlight
		 * player.getEquipment().contains(22092)||//moonlight
		 * player.getEquipment().contains(18352)||//flash
		 * player.getEquipment().contains(18350)||//flash
		 * player.getEquipment().contains(18354)||//flash
		 * player.getEquipment().contains(18456)||//flash
		 * player.getEquipment().contains(18358)||//flash
		 * player.getEquipment().contains(18798)||//gwd
		 * player.getEquipment().contains(18790)||//gwd
		 * player.getEquipment().contains(18787)||//gwd
		 * player.getEquipment().contains(18796)||//gwd
		 * player.getEquipment().contains(18792)||//gwd
		 * player.getEquipment().contains(18799)||//gwd
		 * player.getEquipment().contains(18835)||//gwd
		 * player.getEquipment().contains(19818)||//qspear
		 * player.getEquipment().contains(16043)||//prisoner rapier
		 * player.getEquipment().contains(16337)||//devilbow
		 * player.getEquipment().contains(22075)||//starlight
		 * player.getEquipment().contains(22078)||//demonmaul
		 * player.getEquipment().contains(15877)||//nuetron dagger
		 * player.getEquipment().contains(22075)||//
		 * player.getEquipment().contains(22075)||//
		 * player.getEquipment().contains(22075)||//
		 * player.getEquipment().contains(22075)||//
		 * player.getEquipment().contains(22075)||//
		 * player.getEquipment().contains(22075)||//
		 * player.getEquipment().contains(22075)||//
		 * player.getEquipment().contains(20171) &&
		 */player.getLocation() == Location.FREE_FOR_ALL_ARENA) {
			player.getPacketSender().sendMessage("No safe pvp area :) Teleport to ::PVP");
			player.getMovementQueue().reset();
			return;
		}

		if (player.getCombatBuilder().getStrategy() == null) {
			player.getCombatBuilder().determineStrategy();
		}
		if (CombatFactory.checkAttackDistance(player, attacked)) {
			// confirmed this is called all the time, but shouldn't fuck with people
			// fighting. http://i.imgur.com/qUFhl5L.png
			player.getMovementQueue().reset();
		}

		if (player.getCombatBuilder().getAttackTimer() <= 0)
			player.getCombatBuilder().attack(attacked);
	}

	/**
	 * Manages the first option click on a player option menu.
	 *
	 * @param player The player clicking the other entity.
	 * @param packet The packet to read values from.
	 */
	private static void option1(Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if (id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*
		 * GameServer.getTaskScheduler().schedule(new WalkToTask(player,
		 * victim.getPosition(), new FinalizedMovementTask() {
		 *
		 * @Override public void execute() { //do first option here } }));
		 */
	}

	/**
	 * Manages the second option click on a player option menu.
	 *
	 * @param player The player clicking the other entity.
	 * @param packet The packet to read values from.
	 */
	private static void option2(Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if (id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*
		 * GameServer.getTaskScheduler().schedule(new WalkToTask(player,
		 * victim.getPosition(), new FinalizedMovementTask() {
		 *
		 * @Override public void execute() { //do second option here } }));
		 */
	}

	/**
	 * Manages the third option click on a player option menu.
	 *
	 * @param player The player clicking the other entity.
	 * @param packet The packet to read values from.
	 */
	private static void option3(Player player, Packet packet) {
		int id = packet.readLEShortA() & 0xFFFF;
		if (id < 0 || id > World.getPlayers().capacity())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*
		 * GameServer.getTaskScheduler().schedule(new WalkToTask(player,
		 * victim.getPosition(), new FinalizedMovementTask() {
		 *
		 * @Override public void execute() { //do third option here } }));
		 */
	}
}
