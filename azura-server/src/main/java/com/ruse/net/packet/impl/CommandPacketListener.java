package com.ruse.net.packet.impl;

import com.ruse.model.*;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketListener;
import com.ruse.net.packet.impl.commands.PlayerCommands;
import com.ruse.net.packet.impl.commands.donators.*;
import com.ruse.net.packet.impl.commands.staff.*;
import com.ruse.util.Misc;
import com.ruse.world.content.*;
import com.ruse.world.content.boxes.Diamond;
import com.ruse.world.content.boxes.Ruby;
import com.ruse.world.entity.impl.player.Player;
import mysql.impl.Donation;

import java.util.*;

/**
 * This packet listener manages commands a player uses by using the command
 * console prompted by using the "`" char.
 *
 * @author Gabriel Hannason
 */

public class CommandPacketListener implements PacketListener {

    public static int voteCount = 8;
    static HashMap<String, Integer> dissolvables = new HashMap<>();

    public static void resetInterface(Player player) {
        for (int i = 8145; i < 8196; i++)
            player.getPacketSender().sendString(i, "");
        for (int i = 12174; i < 12224; i++)
            player.getPacketSender().sendString(i, "");
        player.getPacketSender().sendString(8136, "Close window");
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(hm.entrySet());
        // Sort the list
        list.sort((o1, o2) -> (o1.getValue()).compareTo(o2.getValue()));
        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    @Override
    public void handleMessage(Player player, Packet packet) {
        String command = Misc.readString(packet.getBuffer());
        String[] parts = command.toLowerCase().split(" ");
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        PlayerLogs.logCommands(player.getUsername(), "" + player.getUsername() + " used command ::" + command
                + " | Player rights = " + player.getRights() + "");
        if (player.aonBoxItem > 0) {
            player.sendMessage("Please keep or gamble your reward before doing this!");
            return;
        }
        if (player.getAmountDonated() >= Donation.SAPPHIRE_DONATION_AMOUNT) {
            SapphireDonatorCommands.sapphireCommands(player, parts, command);
        }
        if (player.getAmountDonated() >= Donation.EMERALD_DONATION_AMOUNT) {
            EmeraldDonatorCommands.emeraldCommands(player, parts, command);
        }
        if (player.getAmountDonated() >= Donation.RUBY_DONATION_AMOUNT) {
            RubyDonatorCommands.rubyCommands(player, parts, command);
        }
        if (player.getAmountDonated() >= Donation.DIAMOND_DONATION_AMOUNT) {
            DiamondDonatorCommands.diamondCommands(player, parts, command);
        }
        if (player.getAmountDonated() >= Donation.ONYX_DONATION_AMOUNT) {
            OnyxDonatorCommands.onyxCommands(player, parts, command);
        }
        if (player.getAmountDonated() >= Donation.ZENYTE_DONATION_AMOUNT) {
            ZenyteDonatorCommands.zenyteCommands(player, parts, command);
        }
        try {
            switch (player.getRights()) {
                case PLAYER:
                    OwnerCommands.handleOwnerCommands(player, parts, command);
                case YOUTUBER:
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                case MODERATOR:
                    BanCommands.handleBanCommands(player, parts, command);
                    ModeratorCommands.handleModeratorCommands(player, parts, command);
                    HelperCommands.handleHelperCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
               case ADMINISTRATOR:
                    //TODO: add admin specific commands
                    SetRankCommands.handleSetRankCommands(player, parts, command);
                    BanCommands.handleBanCommands(player, parts, command);
                    ModeratorCommands.handleModeratorCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                   HelperCommands.handleHelperCommands(player, parts, command);
                   break;
                case DEVELOPER:
                    SetRankCommands.handleSetRankCommands(player, parts, command);
                    BanCommands.handleBanCommands(player, parts, command);
                    DeveloperCommands.handleDeveloperCommands(player, parts, command);
                    OwnerCommands.handleOwnerCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    HelperCommands.handleHelperCommands(player, parts, command);
                    ModeratorCommands.handleModeratorCommands(player, parts, command);
                    break;
                case SUPPORT:
                case HELPER:
                    HelperCommands.handleHelperCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                case SAPPHIRE_DONATOR:
                    SapphireDonatorCommands.sapphireCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                case EMERALD_DONATOR:
                    EmeraldDonatorCommands.emeraldCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                case ZENYTE_DONATOR:
                    ZenyteDonatorCommands.zenyteCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                case ONYX_DONATOR:
                    OnyxDonatorCommands.onyxCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                case DIAMOND_DONATOR:
                    DiamondDonatorCommands.diamondCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                case RUBY_DONATOR:
                    RubyDonatorCommands.rubyCommands(player, parts, command);
                    PlayerCommands.handlePlayerCommands(player, parts, command);
                    break;
                default:
                    break;
            }
        } catch (Exception exception) {
            exception.printStackTrace();

            if (player.getRights() == PlayerRights.DEVELOPER) {
                player.getPacketSender().sendMessage("Error executing that command.");

            } else {
                player.getPacketSender().sendMessage("Error executing that command.");
            }

        }
    }
}
