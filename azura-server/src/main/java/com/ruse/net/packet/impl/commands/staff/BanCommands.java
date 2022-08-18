package com.ruse.net.packet.impl.commands.staff;

import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.StringUtils;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment;
import com.ruse.world.entity.impl.player.Player;
import com.ruse.world.entity.impl.player.PlayerSaving;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDateTime;

public class BanCommands {

    public static void handleBanCommands(Player player, String[] command, String wholeCommand) {

        if (command[0].equalsIgnoreCase("unban")) {
            String playerToBan = wholeCommand.substring(6);
            if (PlayerSaving.playerExists(playerToBan)) {
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
            } else {
                player.getPacketSender().sendMessage("Player " + playerToBan + " does not exist.");
                return;
            }
        }

        if (command[0].toLowerCase().startsWith("forcenamechange")) {
            String[] usernames = wholeCommand.substring(14).split(",");
            String username = StringUtils.capitalizeEachFirst(usernames[0].trim());
            String newUsername = StringUtils.capitalizeEachFirst(usernames[1].trim());

            // Check if the player file exists that you're trying to change the user to
            File newUserFile = new File("./data/saves/characters/" + newUsername + ".json");
            if(newUserFile.exists()) {
                player.sendMessage("The username you selected is already in use.");
                return;
            }

            // Ban the players name to boot them offline and prevent them
            // from using this name again
            Player toBan = World.getPlayerByName(username);

            // Kick the player if they're online
            if (toBan != null) {
                World.deregister(toBan);
            }

            // Ban them and log it
            PlayerLogs.log(player.getUsername(), player.getUsername() + " just banned " + username + " and transferred their account to the new username " + newUsername + "!");
            PlayerPunishment.ban(username, LocalDateTime.now().plusYears(100));

            try (Reader reader = new FileReader("./data/saves/characters/" + username + ".json")) {
                // Read JSON file
                JSONParser parser = new JSONParser();
                JSONObject data = (JSONObject) parser.parse(reader);
                data.put("username", newUsername);
                @SuppressWarnings("resource")
                FileWriter file = new FileWriter(newUserFile);
                file.write(data.toJSONString());
                file.flush();
                player.sendMessage("Switched the user " + username + " to " + newUsername + ".");
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage("Error! Usage ::forceswapuser name,new name");
            } catch(ParseException e2) {
                e2.printStackTrace();
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

        if (command[0].equalsIgnoreCase("permban") || command[0].equalsIgnoreCase("permaban")) {
            try {
                Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
                if (player2 == null) {
                    player.getPacketSender().sendMessage("Target does not exist. Unable to permban.");
                    return;
                }

                String uuid = player2.getSerialNumber();
                String mac = player2.getMac();
                String name = player2.getUsername();
                String bannedIP = player2.getHostAddress();

                World.sendStaffMessage("Perm banned " + name + " (" + bannedIP + "/" + mac + "/" + uuid + ")");
                PlayerLogs.log(player.getUsername(), "Has perm banned: " + name + "!");
                PlayerLogs.log(name, player + " perm banned: " + name + ".");

                if("0.0.0.0".equals(bannedIP) || "127.0.0.1".equals(bannedIP)) {
                    player.sendMessage("Please dont ip ban localhost!");
                } else {
                    PlayerPunishment.addBannedIP(bannedIP);
                }
                if(uuid == null) {
                    player.sendMessage("This player could not be UUID banned because they are not on windows.");
                } else {
                    ConnectionHandler.banUUID(name, uuid);
                }
                if(mac != null) {
                    ConnectionHandler.banMac(name, mac);
                }
                PlayerPunishment.ban(name, LocalDateTime.now().plusHours(PlayerPunishment.MAX_HOURS));

                if (player2 != null) {
                    World.deregister(player2);
                }

                for (Player playersToBan : World.getPlayers()) {
                    if (playersToBan == null)
                        continue;
                    if (playersToBan.getHostAddress() == bannedIP || playersToBan.getSerialNumber() == uuid
                            || playersToBan.getMac() == mac) {
                        PlayerLogs.log(player.getUsername(),
                                player.getUsername() + " just caught " + playersToBan.getUsername() + " with permban!");
                        PlayerLogs.log(name, player + " perm banned: " + name + ", we were crossfire.");
                        World.sendStaffMessage(playersToBan.getUsername() + " was banned as well.");
                        PlayerPunishment.ban(playersToBan.getUsername(), LocalDateTime.now().plusHours(PlayerPunishment.MAX_HOURS));
                        World.deregister(playersToBan);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (command[0].equalsIgnoreCase("ipban")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Could not find that player online.");
                return;
            } else {
                if (PlayerPunishment.IPBanned(player2.getHostAddress())) {
                    player.getPacketSender()
                            .sendMessage("Player " + player2.getUsername() + "'s IP is already banned.");
                    return;
                }
                final String bannedIP = player2.getHostAddress();
                PlayerPunishment.ban(player2.getUsername(), LocalDateTime.now().plusHours(PlayerPunishment.MAX_HOURS));
                if("0.0.0.0".equals(bannedIP) || "127.0.0.1".equals(bannedIP)) {
                    player.sendMessage("Please dont ip ban localhost!");
                } else {
                    PlayerPunishment.addBannedIP(bannedIP);
                }
                player.getPacketSender().sendMessage(
                        "Player " + player2.getUsername() + "'s IP was successfully banned. Command logs written.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just IP Banned " + player2.getUsername());
                for (Player playersToBan : World.getPlayers()) {
                    if (playersToBan == null)
                        continue;
                    if (playersToBan.getHostAddress() == bannedIP) {
                        PlayerLogs.log(player.getUsername(),
                                "" + player.getUsername() + " just IPBanned " + playersToBan.getUsername() + "!");
                        PlayerPunishment.ban(playersToBan.getUsername(), LocalDateTime.now().plusHours(PlayerPunishment.MAX_HOURS));
                        World.deregister(playersToBan);
                        if (player2.getUsername() != playersToBan.getUsername())
                            player.getPacketSender().sendMessage("Player " + playersToBan.getUsername()
                                    + " was successfully IPBanned. Command logs written.");
                    }
                }
            }
        }

    }
}
