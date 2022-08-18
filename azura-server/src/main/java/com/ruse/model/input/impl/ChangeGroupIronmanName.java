package com.ruse.model.input.impl;

import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;

public class ChangeGroupIronmanName extends Input {

    @Override
    public void handleSyntax(Player player, String input) {
            player.getIronmanGroup().changeName(player,input);
    }
}
