package com.ruse.net.packet.impl.commands.staff;

import com.ruse.GameSettings;
import com.ruse.model.Animation;
import com.ruse.model.Graphic;
import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.util.Misc;
import com.ruse.world.World;
import com.ruse.world.content.PlayerLogs;
import com.ruse.world.content.PlayerPunishment;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.content.transportation.TeleportType;
import com.ruse.world.entity.impl.player.Player;

import java.io.File;
import java.time.LocalDateTime;

public class HelperCommands {

    public static void handleHelperCommands(final Player player, String[] command, String wholeCommand) {

        if (command[0].equalsIgnoreCase("checkalt")) {
            Player target = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (target != null) {
                player.sendMessage("Searching...");

                for (Player plr : World.getPlayers()) {
                    if (plr != null) {
                        if (plr.getHostAddress().equals(target.getHostAddress()) && !plr.equals(target)
                                && !plr.getUsername().equalsIgnoreCase("nucky")
                                && !target.getUsername().equalsIgnoreCase("nucky")
                                && !plr.getUsername().equalsIgnoreCase("test")
                                && !target.getUsername().equalsIgnoreCase("test")
                                && !plr.getUsername().equalsIgnoreCase("james")
                                && !target.getUsername().equalsIgnoreCase("james")) {
                            player.sendMessage(
                                    plr.getUsername() + " has the same Ip address as " + target.getUsername());
                        }
                    }
                }

                player.sendMessage("Done search");
            } else {
                player.sendMessage(wholeCommand.substring(command[0].length() + 1) + " is not valid");
            }
        }

        if (command[0].equalsIgnoreCase("teleto")) {
            String playerToTele = wholeCommand.substring(command[0].length() + 1);
            Player player2 = World.getPlayerByName(playerToTele);
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            } else {
                boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy());
                if (canTele) {
                    player.performGraphic(new Graphic(342));

                    TeleportHandler.teleportPlayer(player, player2.getPosition().copy(), TeleportType.ZOOM);
                    player.getPacketSender().sendMessage("Teleporting to player: " + player2.getUsername() + "");
                    player2.performGraphic(new Graphic(730));
                    player2.getPacketSender().sendMessage("<img=5> ATTENTION: <col=6600FF>" + player.getRights() + " " + player.getUsername() + " is teleporting to you.");
                } else
                    player.getPacketSender()
                            .sendMessage("You can not teleport to this player at the moment. Minigame maybe?");
            }
        }
        if (command[0].equalsIgnoreCase("teletome")) {
            String playerToTele = wholeCommand.substring(command[0].length() + 1);
            Player player2 = World.getPlayerByName(playerToTele);
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player..");
                return;
            } else {
                boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy());
                if (canTele && player2.getDueling().duelingStatus < 5) {
                    player.getPacketSender().sendMessage("Moving player: " + player2.getUsername() + "");
                    player2.getPacketSender().sendMessage("You've been moved to " + player.getUsername());
                    World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                            + " just moved " + player2.getUsername() + " to them.");
                    player2.moveTo(player.getPosition().copy());
                    player2.performGraphic(new Graphic(342));
                } else
                    player.getPacketSender()
                            .sendMessage("Failed to move player to your coords. Are you or them in a minigame?")
                            .sendMessage("Also will fail if they're in duel/wild.");
            }
        }
        if (command[0].equalsIgnoreCase("staffzone")) {
            if (command.length > 1 && command[1].equalsIgnoreCase("all") && player.getRights().OwnerDeveloperOnly()) {
                player.getPacketSender().sendMessage("Teleporting all staff to staffzone.");
                for (Player players : World.getPlayers()) {
                    if (players != null) {
                        if (players.getRights().isStaff()) {
                            TeleportHandler.teleportPlayer(players, new Position(2007, 4439), TeleportType.NORMAL);
                        }
                    }
                }
            } else {
                TeleportHandler.teleportPlayer(player, new Position(2007, 4439), TeleportType.NORMAL);
            }
        }
        if (command[0].equalsIgnoreCase("movehome")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToMove = World.getPlayerByName(player2);
            if (playerToMove != null && playerToMove.getDueling().duelingStatus < 5) {
                if (playerToMove.getLocation() == Locations.Location.WILDERNESS) {
                    PlayerLogs.log(player.getUsername(), "Just moved " + playerToMove.getUsername()
                            + " to home from level " + playerToMove.getWildernessLevel() + " wild.");
                } else {
                    PlayerLogs.log(player.getUsername(), "Just moved " + playerToMove.getUsername() + " to home.");
                }
                playerToMove.getControllerManager().forceStop();
                playerToMove.moveTo(GameSettings.DEFAULT_POSITION.copy());
                playerToMove.getPacketSender()
                        .sendMessage("You've been teleported home by " + player.getUsername() + ".");
                player.getPacketSender().sendMessage("Successfully moved " + playerToMove.getUsername() + " to home.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just moved " + playerToMove.getUsername() + " to home.");
                player.performGraphic(new Graphic(730));
                playerToMove.performGraphic(new Graphic(730));
            } else {
                player.getPacketSender().sendMessage("Could not send \"" + player2 + "\" home. Try kicking first?")
                        .sendMessage("Will also fail if they're in duel/wild.");
            }
        }
        if (command[0].equalsIgnoreCase("unmute")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            if (player2 == null) {
                player.getPacketSender().sendMessage("Player " + player2 + " does not exist.");
                return;
            } else {
                if (!PlayerPunishment.muted(player2)) {
                    player.getPacketSender().sendMessage("Player " + player2 + " is not muted!");
                    return;
                }
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just unmuted " + player2 + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just unmuted " + player2);
                PlayerPunishment.unmute(player2);
                player.getPacketSender()
                        .sendMessage("Player " + player2 + " was successfully unmuted. Command logs written.");
                Player plr = World.getPlayerByName(player2);
                if (plr != null) {
                    plr.getPacketSender().sendMessage("You have been unmuted by " + player.getUsername() + ".");
                }
            }
        }
        if (command[0].equalsIgnoreCase("kick")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick == null) {
                player.getPacketSender()
                        .sendMessage("Player " + player2 + " couldn't be found on " + GameSettings.RSPS_NAME + ".");
                return;
            } else if (playerToKick.getDueling().duelingStatus < 5) {
                //PlayerHandler.handleLogout(playerToKick, false);
                playerToKick.save();
                World.getPlayers().remove(playerToKick);
                player.getPacketSender().sendMessage("Kicked " + playerToKick.getUsername() + ".");
                PlayerLogs.log(player.getUsername(),
                        player.getUsername() + " just kicked " + playerToKick.getUsername() + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just kicked " + playerToKick.getUsername() + ".");
                player.getPacketSender().sendMessage("You can try ::kick2 if this command didn't work.");
            } else {
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just tried to kick "
                        + playerToKick.getUsername() + " in an active duel.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just tried to kick " + playerToKick.getUsername() + " in an active duel.");
                player.getPacketSender().sendMessage("You've tried to kick someone in duel arena/wild. Logs written.");
            }
        }
        if (command[0].equalsIgnoreCase("kill")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick == null) {
                player.getPacketSender()
                        .sendMessage("Player " + player2 + " couldn't be found on " + GameSettings.RSPS_NAME + ".");
                return;
            } else if (playerToKick.getDueling().duelingStatus < 5) {
                playerToKick.setConstitution(0);
                //PlayerHandler.handleLogout(playerToKick, false);
            } else {
                playerToKick.setConstitution(0);
            }
        }
        if (command[0].equalsIgnoreCase("kick2")) {
            String player2 = wholeCommand.substring(command[0].length() + 1);
            Player playerToKick = World.getPlayerByName(player2);
            if (playerToKick == null) {
                player.getPacketSender()
                        .sendMessage("Player " + player2 + " couldn't be found on " + GameSettings.RSPS_NAME + ".");
                return;
            } else if (playerToKick.getDueling().duelingStatus < 5) {
                World.getPlayers().remove(playerToKick);
                player.getPacketSender().sendMessage("Kicked " + playerToKick.getUsername() + ".");
                PlayerLogs.log(player.getUsername(),
                        player.getUsername() + " just kicked " + playerToKick.getUsername() + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just kicked " + playerToKick.getUsername() + ".");
            } else {
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just tried to kick "
                        + playerToKick.getUsername() + " in an active duel.");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just tried to kick " + playerToKick.getUsername() + " in an active duel.");
                player.getPacketSender().sendMessage("You've tried to kick someone in duel arena/wild. Logs written.");
            }
        }

        if (command[0].equalsIgnoreCase("mute")) {
            try {
                String[] time = command[1].split("h");
                Long timer = Long.parseLong(time[0]);
                String target = wholeCommand.substring(wholeCommand.lastIndexOf("h") + 1).trim();

                String fileName = Misc.formatText(target.toLowerCase());
                File file = new File("./data/saves/characters/" + fileName + ".json");
                if (!file.exists()) {
                    Player targetPlayer = World.getPlayerByName(target);
                    if (targetPlayer != null) {
                        targetPlayer.save();
                    } else {
                        player.getPacketSender().sendMessage(fileName + " does not exist in my files, " +
                                "maybe you typed it wrong!");
                        return;
                    }
                }
                if (PlayerPunishment.muted(target)) {
                    player.getPacketSender().sendMessage("Player: " + target + " already has an active mute.");
                    return;
                }
                PlayerLogs.log(player.getUsername(), player.getUsername() + " just muted " + target + " for " + command[2] + "!");
                World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                        + " just muted " + target + " for " + command[2] + ".");
                PlayerPunishment.mute(target, LocalDateTime.now().plusHours(timer));//* , GameSettings.Temp_Mute_Time); *//*
                player.getPacketSender().sendMessage("Player " + target + " was successfully muted for " + command[2] + ".");// for
                Player plr = World.getPlayerByName(target);
                if (plr != null) {
                    plr.getPacketSender().sendMessage("You have been muted by " + player.getUsername() + " for " + command[2] + "."); // for
                }
                /*if (World.getPlayerByName(target) == null) {
                    String fileName = Misc.formatText(target.toLowerCase());
                    File file = new File("./data/saves/characters/" + fileName + ".json");
                    if (file.exists()) {
                        if (PlayerPunishment.muted(target)) {
                            player.getPacketSender().sendMessage("Player: " + target + " already has an active mute.");
                            return;
                        }
                        PlayerLogs.log(player.getUsername(), player.getUsername() + " just muted " + target + "!");
                        World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                                + " just muted " + target);
                        PlayerPunishment.mute(target, LocalDateTime.now().plusHours(timer));
                        player.getPacketSender().sendMessage("Player " + target + " was successfully muted");// for
                        Player plr = World.getPlayerByName(target);
                        if (plr != null) {
                            plr.getPacketSender().sendMessage("You have been muted by " + player.getUsername() + "."); // for
                        }
                    } else {
                        player.getPacketSender().sendMessage(fileName + " does not exist in my files, " +
                                "maybe you typed it wrong!");
                    }
                    return;
                } else {
                    if (PlayerPunishment.muted(target)) {
                        player.getPacketSender().sendMessage("Player: " + target + " already has an active mute.");
                        return;
                    }
                    PlayerLogs.log(player.getUsername(), player.getUsername() + " just muted " + target + "!");
                    World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                            + " just muted " + target);
                    PlayerPunishment.mute(target, LocalDateTime.now().plusHours(timer));*//* , GameSettings.Temp_Mute_Time); *//*
                    player.getPacketSender().sendMessage("Player " + target + " was successfully muted");// for
                    Player plr = World.getPlayerByName(target);
                    if (plr != null) {
                        plr.getPacketSender().sendMessage("You have been muted by " + player.getUsername() + "."); // for
                    }
                }*/
            } catch (Exception e) {
                player.getPacketSender().sendMessage("Error! Usage ::mute 6h username");
            }
        }


        if (command[0].equalsIgnoreCase("jail")) {
            try {
                String[] time = command[1].split("h");
                Long timer = Long.parseLong(time[0]);
                String target = wholeCommand.substring(wholeCommand.lastIndexOf("h") + 1).trim();

                if (PlayerPunishment.Jail.isJailed(target)) {
                    player.getPacketSender().sendMessage("Player: " + target + " is already jailed.");
                    return;
                }
                Player player2 = World.getPlayerByName(target);
                if (player2 != null) {
                    if (player2.getDueling().duelingStatus == 0) {
                        PlayerPunishment.Jail.jailPlayer(target, LocalDateTime.now().plusHours(timer));
                        player2.getSkillManager().stopSkilling();
                        PlayerLogs.log(player.getUsername(),
                                player.getUsername() + " just jailed " + player2.getUsername() + "!");
                        player.getPacketSender().sendMessage("Jailed player: " + player2.getUsername());
                        player2.getPacketSender().sendMessage("You have been jailed by " + player.getUsername() + ".");
                        World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                                + " just jailed " + player2.getUsername());
                        player2.performAnimation(new Animation(1994));
                        player.performGraphic(new Graphic(730));
                        player2.moveTo(new Position(2510, 9326));
                        player2.setLocation(Locations.Location.JAIL);
                    } else {
                        if (player2.getDueling().duelingStatus > 0) {
                            player.getPacketSender().sendMessage(
                                    "That player is currently in a stake. Please wait before jailing them, or kick then jail.");
                            return;
                        } else {
                            player.getPacketSender().sendMessage("This shouldn't happen, message Crimson. Error: JAIL45");
                        }
                    }
                }
            } catch (Exception e) {
                player.getPacketSender().sendMessage("Error! Usage ::jail 6h username");
            }
        }
        if (command[0].equalsIgnoreCase("unjail")) {
            Player player2 = World
                    .getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 != null) {
                if (true) {
                    PlayerPunishment.Jail.unJail(player2.getUsername());
                    PlayerLogs.log(player.getUsername(),
                            "" + player.getUsername() + " just unjailed " + player2.getUsername() + "!");
                    player.getPacketSender().sendMessage("Unjailed player: " + player2.getUsername() + "");
                    player2.getPacketSender().sendMessage("You have been unjailed by " + player.getUsername() + ".");
                    World.sendStaffMessage("<col=FF0066><img=2> [PUNISHMENTS]<col=6600FF> " + player.getUsername()
                            + " just unjailed " + player2.getUsername());
                    player2.performAnimation(new Animation(1993));
                    player.performGraphic(new Graphic(730));
                }
            } else {
                player.getPacketSender().sendMessage("Could not find \""
                        + wholeCommand.substring(command[0].length() + 1) + "\" online.");
            }
        }


        if (command[0].equalsIgnoreCase("remindvote")) {
            World.sendMessage(
                    "<img=5> <col=008FB2><shad=0>Remember to collect rewards by using the ::vote command every 12 hours!");
        }
        if (command[0].equalsIgnoreCase("remindafk")) {
            World.sendMessage(
                    "<img=5> <col=008FB2><shad=0>Remember! You can put up to an account in the ::Afk zone! Get yourself some free items!");
        }
        if (command[0].equalsIgnoreCase("reminddonate")) {
            World.sendMessage("<img=5> <col=008FB2><shad=0>Remember to check out the donation deals in our ::discord!");
        }
        if (command[0].equalsIgnoreCase("remindhelp")) {
            World.sendMessage(
                    "<img=5> <col=008FB2><shad=0>Staff members are always available, pm them if you need help!");
        }

    }



}
