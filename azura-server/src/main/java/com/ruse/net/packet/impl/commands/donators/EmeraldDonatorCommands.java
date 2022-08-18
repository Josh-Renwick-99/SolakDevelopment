package com.ruse.net.packet.impl.commands.donators;

import com.ruse.model.Locations;
import com.ruse.model.Position;
import com.ruse.util.RandomUtility;
import com.ruse.world.content.transportation.TeleportHandler;
import com.ruse.world.entity.impl.player.Player;

public class EmeraldDonatorCommands {

    public static void emeraldCommands(Player player, String[] command, String wholeCommand) {
        if (command[0].equalsIgnoreCase("ezone")) {
            if (player.getLocation() != null && player.getLocation() == Locations.Location.WILDERNESS
                    || player.getLocation() != null && player.getLocation() == Locations.Location.CUSTOM_RAIDS) {
                player.getPacketSender().sendMessage("You cannot do this at the moment.");
                return;
            }
            Position[] locations = new Position[]{new Position(2602, 2774, 0)};
            Position teleportLocation = locations[RandomUtility.exclusiveRandom(0, locations.length)];
            TeleportHandler.teleportPlayer(player, teleportLocation, player.getSpellbook().getTeleportType());
            player.getPacketSender().sendMessage("Teleporting you to the Emerald Donator Zone!");
        }
        if (command[0].equalsIgnoreCase("getyellhex")) {
            player.getPacketSender().sendMessage(
                    "Your current yell hex is: <shad=0><col=" + player.getYellHex() + ">#" + player.getYellHex());
            return;
        }
        if (command[0].equalsIgnoreCase("setyellhex")) {
            String hex = command[1].replaceAll("#", "");
            player.setYellHex(hex);
            player.getPacketSender().sendMessage("You have set your hex color to: <shad=0><col=" + hex + ">#" + hex);
            if (player.getYellHex() == null)
                player.getPacketSender().sendMessage("There was an error setting your yell hex. You entered: " + hex);
            return;
        }
    }

}
