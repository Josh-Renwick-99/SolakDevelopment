package com.ruse.net.packet.impl.commands.staff;

import com.ruse.GameSettings;
import com.ruse.model.definitions.ItemDefinition;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerSaving;

import java.time.LocalDateTime;

public class ModeratorCommands {

    public static void handleModeratorCommands(final Player player, String[] command, String wholeCommand) {

        if (command[0].equalsIgnoreCase("kickgi")) {
            Player target = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (target == null) {
                player.getPacketSender().sendConsoleMessage("Player must be online to kick them from their group!");
            } else {
                player.getPacketSender().sendConsoleMessage("Kicked " + target.getUsername() + " from their ironman group.");
                if (target.getIronmanGroup() != null){
                    target.getIronmanGroup().kickPlayer(player, target.getUsername());
                }else{
                    player.getPacketSender().sendConsoleMessage("Player is not in a ironman group!");
                }
            }
        }

        if (command[0].equalsIgnoreCase("invis")) {
            player.setVisible(!player.isVisible());
            player.sendMessage("You are now " + (player.isVisible() ? "visible" : "invisible"));
        }
        if (command[0].equalsIgnoreCase("broadcast")) {
            int time = Integer.parseInt(command[1]);
            String message = wholeCommand.substring(command[0].length() + command[1].length() + 2);
            for (Player players : World.getPlayers()) {
                if (players == null) {
                    continue;
                }
                players.getPacketSender().sendBroadCastMessage(message, time);
            }
            World.sendBroadcastMessage(message);
            GameSettings.broadcastMessage = message;
            GameSettings.broadcastTime = time;
        }
        if (command[0].equalsIgnoreCase("id")) {
            String name = wholeCommand.substring(3).toLowerCase().replaceAll("_", " ");
            player.getPacketSender().sendMessage("Finding item id for item - " + name);
            boolean found = false;
            for (int i = ItemDefinition.getMaxAmountOfItems() - 1; i > 0; i--) {
                if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
                    player.getPacketSender().sendMessage("Found item with name ["
                            + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
                    found = true;
                }
            }
            if (!found) {
                player.getPacketSender().sendMessage("No item with name [" + name + "] has been found!");
            }
        }
        if (command[0].equalsIgnoreCase("checkbank")) {
            Player plr = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (plr != null) {
                player.getPacketSender().sendMessage("Loading bank..");
                plr.getBank(0).openOther(player, true, false);
            } else {
                player.getPacketSender().sendMessage("Player is offline!");
            }
        }
        if (command[0].equalsIgnoreCase("check")) {
            Player plr = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (plr != null) {
                player.getPacketSender().sendMessage("Showing bank and inventory of " + plr.getUsername() + "...");
                plr.getBank(0).openOther(player, true, false);
                player.getPacketSender().sendInterfaceSet(5292, 3321);
                player.getPacketSender().sendItemContainer(plr.getInventory(), 3322);
            } else {
                player.getPacketSender().sendMessage("Player is offline!");
            }
        }
        if (command[0].equalsIgnoreCase("checkinv")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player.getPacketSender().sendItemContainer(player2.getInventory(), 3214);
        }
        if (command[0].equalsIgnoreCase("endcheck")) {
            player.getInventory().refreshItems();
        }
        if (command[0].equalsIgnoreCase("unban")) {
            String playerToBan = wholeCommand.substring(6);
            if (!PlayerSaving.playerExists(playerToBan)) {
                player.getPacketSender().sendMessage("Player " + playerToBan + " does not exist.");
                return;
            } else {
                if (!PlayerPunishment.banned(playerToBan)) {
                    player.getPacketSender().sendMessage("Player " + playerToBan + " is not banned!");
                    return;
                }
                PlayerLogs.log(player.getUsername(), "" + player.getUsername() + " just unbanned " + playerToBan + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just unbanned " + playerToBan + ".");
                PlayerPunishment.unban(playerToBan);
                player.getPacketSender()
                        .sendMessage("Player " + playerToBan + " was successfully unbanned. Command logs written.");
            }
        }

        if (command[0].equalsIgnoreCase("ban")) {
            try {
                String[] time = command[1].split("h");
                Long timer = Long.parseLong(time[0]);
                String playerToBan = wholeCommand.substring(wholeCommand.lastIndexOf("h") + 1).trim();
                if (PlayerPunishment.banned(playerToBan)) {
                    player.getPacketSender().sendMessage("Player " + playerToBan + " already has an active ban.");
                    return;
                }
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just banned " + playerToBan + " for " + command[2] + "!");
                PlayerPunishment.ban(playerToBan, LocalDateTime.now().plusHours(timer));
                player.getPacketSender()
                        .sendMessage("Player " + playerToBan + " was successfully banned for " + command[2] + ". Command logs written.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just banned " + playerToBan + " for " + command[2] + ".");
                Player toBan = World.getPlayerByName(playerToBan);
                if (toBan != null) {
                    World.deregister(toBan);
                }
            } catch (Exception e) {
                player.getPacketSender().sendMessage("Error! Usage ::ban 6h username");
            }
        }

    }


}
