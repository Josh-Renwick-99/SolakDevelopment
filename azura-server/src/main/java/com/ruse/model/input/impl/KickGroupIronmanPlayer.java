package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;

public class KickGroupIronmanPlayer extends Input {

    @Override
    public void handleSyntax(Player player, String name) {
        if (player.getIronmanGroup() != null)
            player.getIronmanGroup().kickPlayer(player, name);
    }
}
