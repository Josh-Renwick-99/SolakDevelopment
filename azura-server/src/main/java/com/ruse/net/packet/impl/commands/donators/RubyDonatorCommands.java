package com.ruse.net.packet.impl.commands.donators;

import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.util.RandomUtility;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.player.Player;

public class RubyDonatorCommands {

    public static void rubyCommands(Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("rzone")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }

            Position[] locations = new Position[]{new Position(2530, 2716, 0),new Position(2534, 2716, 0), new Position(2535, 2711, 0), new Position(2530, 2711, 0)};
            Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
            TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you to the Ruby Donator Zone!");
        }

        if (wholeCommand.equalsIgnoreCase("bank")) {
            if (player.getInterfaceId() > 0) {
                player.getPacketSender()
                        .sendMessage("Please close the interface you have open before opening another one.");
                return;
            }
            if (player.getLocation() == Locations.Location.WILDERNESS || player.getLocation() == Locations.Location.DUNGEONEERING
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS
                    || player.getLocation() == Locations.Location.DUEL_ARENA) {
                player.getPacketSender().sendMessage("You cannot open your bank here.");
                return;
            }
            player.getBank(player.getCurrentBankTab()).open();
        }
    }

}
