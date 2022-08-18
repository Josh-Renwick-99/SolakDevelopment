package com.ruse.world.content;

import com.ruse.model.PlayerRights;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

import java.util.ArrayList;
import java.util.List;

public class StaffList {

	public static List<String> staff = new ArrayList<>();

	public static void clearList(Player player) {
		for (int i = 0; i < 40; i++) {
			player.getPacketSender().sendString(32410 + i, "");
		}
	}

	public static String getName(String name) {
		return name.substring(name.lastIndexOf('>') + 1);
	}

	public static String getPrefix(Player player) {
		if (player.getRights() == PlayerRights.DEVELOPER) { // 679 main img 0
			return "<img=4>";
		}
		if (player.getRights() == PlayerRights.MODERATOR) {
			return "<img=1>";
		}
		if (player.getRights() == PlayerRights.ADMINISTRATOR) {
			return "<img=2>";
		}
		if (player.getRights() == PlayerRights.HELPER) {
			return "<img=5>";
		}
		return "";
	}

	public static void delete(Player player) {

		if (staff.contains("<img=4>" + "@gre@" + player.getUsername()))
			staff.remove("<img=4>" + "@gre@" + player.getUsername());

		if (staff.contains("<img=1>" + "@gre@" + player.getUsername()))
			staff.remove("<img=1>" + "@gre@" + player.getUsername());

		if (staff.contains("<img=2>" + "@gre@" + player.getUsername()))
			staff.remove("<img=2>" + "@gre@" + player.getUsername());

		if (staff.contains("<img=5>" + "@gre@" + player.getUsername()))
			staff.remove("<img=5>" + "@gre@" + player.getUsername());

	}

	public static void handleButton(Player player, int id) {
		if (id == 110) {
			if (System.currentTimeMillis() - player.lastHelpRequest < 3000) {
				player.getPacketSender().sendMessage("You have to wait a few seconds before doing this again.");
				return;
			}
			sendGlobalHelpRequest(player);
			player.lastHelpRequest = System.currentTimeMillis();
			return;
		}

		int index = (id - 32410);
		if (index > staff.size() - 1) {
			return;
		}
		String name = staff.get(index);

		if (name == null)
			return;

		// System.out.println("name " + name);

		name = getName(name);
		Player p = World.getPlayerByName(name);
		if (p == null) {
			player.getPacketSender().sendMessage(name + " is currently offline.");
			return;
		}
		sendHelpRequest(player, p);
	}

	public static void login(Player player) {
		delete(player);
		if (!staff.contains(getPrefix(player) + "@gre@" + player.getUsername()))
			staff.add(getPrefix(player) + "@gre@" + player.getUsername());
	}

	public static void logout(Player player) {
		staff.remove(getPrefix(player) + "@gre@" + player.getUsername());
	}

	public static void sendGlobalHelpRequest(Player player) {
		for (String name : staff) {
			Player p = World.getPlayerByName(getName(name));
			if (p == null) {
				continue;
			}
			if (p == player) {
				p.getPacketSender().sendMessage("You have sent a help request to the staff.");
				continue;
			}
			p.getPacketSender().sendMessage("@red@" + player.getUsername() + " is asking for staff attention!");
		}
	}

	public static void sendHelpRequest(Player player, Player staff) {
		if (System.currentTimeMillis() - player.lastHelpRequest < 30000) {
			player.getPacketSender().sendMessage("You have to wait 30 seconds before doing this again.");
			return;
		}
		player.getPacketSender().sendMessage("You have asked " + staff.getUsername() + " for help.");
		staff.getPacketSender().sendMessage("@red@" + player.getUsername() + " asks for your help!");
		player.lastHelpRequest = System.currentTimeMillis();
	}

	public static void updateInterface(Player player) {
		clearList(player);
		player.getPacketSender().sendString(32396, "Online staff: " + staff.size());
		if (staff.size() >= 50) {
			player.getPacketSender().setScrollBar(32385, 4 * staff.size());
		}
		for (int i = 0; i < staff.size(); i++) {
			String name = staff.get(i);
			player.getPacketSender().sendString(32410 + i, " " + name);
		}
	}

	public static void updateGlobalInterface() {
		for (Player player : World.getPlayers()) {
			if (player != null) {
				updateInterface(player);
			}
		}
	}

}
