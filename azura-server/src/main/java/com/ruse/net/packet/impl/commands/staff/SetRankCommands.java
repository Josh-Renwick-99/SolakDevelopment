package com.ruse.net.packet.impl.commands.staff;

import com.ruse.model.PlayerRights;
import com.ruse.world.World;
import com.ruse.world.content.PlayerPanel;
import com.ruse.world.entity.impl.player.Player;

public class SetRankCommands {

    public static void handleSetRankCommands(final Player player, String[] command, String wholeCommand) {

        if (command[0].equalsIgnoreCase("givemod")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("Promoted to moderator.");
            player.getPacketSender().sendMessage("Promoted to moderator.");
            player2.setRights(PlayerRights.MODERATOR);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }
        if (command[0].equalsIgnoreCase("givehelper") || command[0].equalsIgnoreCase("givess")) {
            Player player2 = World.getPlayerByName(wholeCommand.substring(command[0].length() + 1));
            if (player2 == null) {
                player.getPacketSender().sendMessage("Cannot find that player online..");
                return;
            }
            player2.getPacketSender().sendMessage("Promoted to helper.");
            player.getPacketSender().sendMessage("Promoted to helper.");
            player2.setRights(PlayerRights.HELPER);
            player2.getPacketSender().sendRights();
            PlayerPanel.refreshPanel(player2);
        }



    }
}
