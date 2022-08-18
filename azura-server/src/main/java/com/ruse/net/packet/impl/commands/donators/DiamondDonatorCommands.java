package com.ruse.net.packet.impl.commands.donators;

import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.util.RandomUtility;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.player.Player;

public class DiamondDonatorCommands {

    public static void diamondCommands(Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("dzone")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position[] locations = new Position[]{new Position(2590, 2718, 0)};
            Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
            TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you to the Diamond Donator Zone!");
        }
    }

}
