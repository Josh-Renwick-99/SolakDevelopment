package com.ruse.world.content.serverperks;

import com.ruse.model.input.Input;
import com.ruse.world.entity.impl.player.Player;

public class ServerPerkContributionInput extends Input {

    @Override
    public void handleAmount(Player player, int amount) {
        ServerPerks.getInstance().contribute(player, amount);
    }
}
