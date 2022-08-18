package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;

public class InviteGroupIronmanPlayer extends Input {

    @Override
    public void handleSyntax(Player player, String plrToInvite) {
            Player invite = World.getPlayerByName(plrToInvite);
            if (invite == null) {
                player.getPacketSender().sendMessage("That player is currently not online.");
                return;
            }
            player.getIronmanGroup().invite(invite);
    }
}
